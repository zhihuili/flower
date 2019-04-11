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
package com.ly.train.flower.common.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.ly.train.flower.common.annotation.Scope;
import com.ly.train.flower.common.service.Aggregate;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.message.TimerMessage;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

@Scope(scopeName = Constant.SCOPE_REQUEST)
public class AggregateService implements Service<Object, List<Object>>, Aggregate {
  static final Logger logger = LoggerFactory.getLogger(AggregateService.class);
  private static final Long DefaultTimeOutMilliseconds = 60000L;

  private int sourceNumber = 0;
  private Long timeoutMillis = DefaultTimeOutMilliseconds;

  // <messageId,Set<message>>
  private static final ConcurrentMap<String, List<Object>> resultMap = new ConcurrentHashMap<String, List<Object>>();
  // <messageId,sourceNumber>
  private static final  ConcurrentMap<String, AtomicInteger> resultNumberMap = new ConcurrentHashMap<>();
  // <messageId,addedTime>
  private static final  ConcurrentMap<String, Long> resultDateMap = new ConcurrentHashMap<String, Long>();

  public AggregateService() {}

  public AggregateService(String config) {
    this.timeoutMillis = Long.valueOf(config);
  }

  @Override
  public List<Object> process(Object message, ServiceContext context) {
    FlowMessage flowMessage = context.getFlowMessage();
    if (flowMessage instanceof TimerMessage) {
      doClean();
      return null;
    }

    final String transactionId = flowMessage.getTransactionId();
    // first joint message
    if (!resultMap.containsKey(transactionId)) {
      List<Object> objectSet = new ArrayList<Object>();
      resultMap.put(transactionId, objectSet);
      resultNumberMap.put(transactionId, new AtomicInteger(sourceNumber));
      resultDateMap.put(transactionId, System.currentTimeMillis());
    }
    resultMap.get(transactionId).add(flowMessage.getMessage());

    AtomicInteger number = resultNumberMap.get(transactionId);
    if (number != null && number.decrementAndGet() <= 0) {
      List<Object> returnObject = resultMap.get(transactionId);
      resultMap.remove(transactionId);
      resultNumberMap.remove(transactionId);
      resultDateMap.remove(transactionId);

      return buildMessage(returnObject);
    }
    return null;
  }

  /**
   * subclass should override the method.
   * 
   * @param messages Set<Message>
   * @return Object
   */
  public List<Object> buildMessage(List<Object> messages) {
    return messages;
  }

  // sourceNumber++ when initialize
  public void setSourceNumber(int sourceNumber) {
    this.sourceNumber = sourceNumber;
  }

  private void doClean() {
    long currentTimeMillis = System.currentTimeMillis();
    for (Map.Entry<String, Long> entry : resultDateMap.entrySet())
      if (currentTimeMillis - entry.getValue() > this.timeoutMillis) {
        resultDateMap.remove(entry.getKey());
        resultMap.remove(entry.getKey());
        resultNumberMap.remove(entry.getKey());
      }
  }
}
