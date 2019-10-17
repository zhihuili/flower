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
package com.ly.train.flower.ddd.config;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.scanner.DefaultMethodScanner;
import com.ly.train.flower.ddd.annotation.CommandHandler;
import com.ly.train.flower.ddd.annotation.EventHandler;
import com.ly.train.flower.ddd.annotation.QueryHandler;
import com.ly.train.flower.ddd.proxy.MethodProxy;

/**
 * @author leeyazhou
 */
public class DDDConfig implements ApplicationContextAware {
  private static final Logger logger = LoggerFactory.getLogger(DDDConfig.class);
  private final ConcurrentMap<Class<?>, Set<MethodProxy>> commandHandlerCache = new ConcurrentHashMap<>();
  private final ConcurrentMap<Class<?>, Set<MethodProxy>> queryHandlerCache = new ConcurrentHashMap<>();
  private final ConcurrentMap<Class<?>, Set<MethodProxy>> eventHandlerCache = new ConcurrentHashMap<>();
  private ApplicationContext applicationContext;

  public Set<MethodProxy> getCommandHandler(Class<?> clazz) {
    return commandHandlerCache.get(clazz);
  }

  public Set<MethodProxy> getEventHandler(Class<?> clazz) {
    return eventHandlerCache.get(clazz);
  }

  public Set<MethodProxy> getQueryHandler(Class<?> clazz) {
    return this.queryHandlerCache.get(clazz);
  }

  public void dealHandlers(Class<?> clazz) {
    List<Method> commandHandlers =
        DefaultMethodScanner.getInstance().getMethodListByAnnotation(clazz, CommandHandler.class);
    List<Method> eventHandlers =
        DefaultMethodScanner.getInstance().getMethodListByAnnotation(clazz, EventHandler.class);
    List<Method> queryHandlers =
        DefaultMethodScanner.getInstance().getMethodListByAnnotation(clazz, QueryHandler.class);
    dealCommandHandlers(commandHandlers, clazz);
    dealQueryHandlers(queryHandlers, clazz);
    dealEventHandlers(eventHandlers, clazz);
  }

  private void dealCommandHandlers(List<Method> commandHandlers, Class<?> target) {
    for (Method method : commandHandlers) {
      doDealCommandHandler(method, target);
    }
  }

  private void doDealCommandHandler(Method method, Class<?> target) {
    Class<?>[] types = method.getParameterTypes();
    if (types == null || types.length == 0) {
      return;
    }
    Class<?> eventType = types[0];
    logger.info("Command Handler ：{}", eventType);
    Set<MethodProxy> methodProxies = commandHandlerCache.computeIfAbsent(eventType, i -> new HashSet<>());
    methodProxies.add(new MethodProxy(applicationContext.getBean(target), method));
  }

  private void dealQueryHandlers(List<Method> commandHandlers, Class<?> target) {
    for (Method method : commandHandlers) {
      doDealQueryHandler(method, target);
    }
  }

  private void doDealQueryHandler(Method method, Class<?> target) {
    Class<?>[] types = method.getParameterTypes();
    if (types == null || types.length == 0) {
      return;
    }
    Class<?> eventType = types[0];
    logger.info("Query Handler ：{}", eventType);
    Set<MethodProxy> methodProxies = queryHandlerCache.computeIfAbsent(eventType, i -> new HashSet<>());
    methodProxies.add(new MethodProxy(applicationContext.getBean(target), method));
  }

  private void dealEventHandlers(List<Method> eventHandlers, Class<?> target) {
    for (Method method : eventHandlers) {
      doDealEventHandler(method, target);
    }
  }

  private void doDealEventHandler(Method method, Class<?> target) {

    Class<?>[] types = method.getParameterTypes();
    if (types == null || types.length == 0) {
      return;
    }

    Class<?> eventType = types[0];
    logger.info("Event handler ：{}", eventType);

    Set<MethodProxy> methodProxies = eventHandlerCache.computeIfAbsent(eventType, i -> new HashSet<>());
    methodProxies.add(new MethodProxy(applicationContext.getBean(target), method));
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
