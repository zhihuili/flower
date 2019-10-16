/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.ddd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.util.concurrent.NamedThreadFactory;
import com.ly.train.flower.ddd.config.DDDConfig;
import com.ly.train.flower.ddd.config.EventMessage;
import com.ly.train.flower.ddd.exception.EventHandlerNotFoundException;
import com.ly.train.flower.ddd.proxy.MethodProxy;

/**
 * @author leeyazhou
 */
public class EventHandlerService extends BaseService {
  private static final Logger logger = LoggerFactory.getLogger(EventHandlerService.class);
  private static final ExecutorService executorService = new ThreadPoolExecutor(32, 64, 60, TimeUnit.SECONDS,
      new LinkedBlockingQueue<>(), new NamedThreadFactory("flower-ddd-"));

  private DDDConfig dddConfig;
  private TransactionTemplate transactionTemplate;

  public EventHandlerService(DDDConfig dddConfig) {
    this.dddConfig = dddConfig;
  }

  @Override
  public Object doProcess(Object message, ServiceContext context) throws Throwable {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return handleEvent(message, context);
      } catch (Exception e) {
        logger.error("", e);
      }
      return null;
    }, executorService);
  }

  public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }



  @SuppressWarnings("unchecked")
  private List<Object> handleEvent(Object message, ServiceContext context) {
    final List<EventMessage> messages = new ArrayList<>();
    if (message instanceof List) {
      messages.addAll((List<EventMessage>) message);
    } else {
      messages.add(EventMessage.asEventMessage(messages));
    }
    Supplier<List<Object>> callback = () -> {
      List<Object> ret = new ArrayList<>();
      for (EventMessage item : messages) {
        Set<MethodProxy> methods = dddConfig.getEventHandler(item.getMessage().getClass());
        if (methods == null || methods.isEmpty()) {
          throw new EventHandlerNotFoundException("for " + message.getClass());
        }
        for (MethodProxy method : methods) {
          ret.add(method.invoke(item.getMessage(), context));
        }
      }
      return ret;
    };

    boolean isInTransaction = messages.stream().anyMatch(item -> {
      Set<MethodProxy> methodProxies = dddConfig.getEventHandler(item.getMessage().getClass());
      if (methodProxies == null || methodProxies.isEmpty()) {
        return false;
      }
      return methodProxies.stream().anyMatch(m -> {
        return m != null && m.isTransactional();
      });
    });
    if (isInTransaction && transactionTemplate != null) {
      return transactionTemplate.execute(status -> callback.get());
    }
    return callback.get();
  }


}
