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
package com.ly.train.flower.common.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.message.TimerMessage;

public class AggregateService implements Service<Object, Object>, Aggregate {

  private static final long DefaultTimeOutMilliseconds = 60000;

  private int sourceNumber = 0;
  private long timeoutMillis = DefaultTimeOutMilliseconds;

  // <messageId,Set<message>>
  private Map<String, Set<Object>> resultMap = new ConcurrentHashMap<String, Set<Object>>();
  // <messageId,sourceNumber>
  private Map<String, Integer> resultNumberMap = new ConcurrentHashMap<String, Integer>();
  // <messageId,addedTime>
  private Map<String, Long> resultDateMap = new ConcurrentHashMap<String, Long>();

  public AggregateService() {}

  public AggregateService(String config) {
    this.timeoutMillis = Integer.valueOf(config);
  }

  @Override
  public Object process(Object message, ServiceContext context) {
    FlowMessage flowMessage = (FlowMessage) message;
    if (flowMessage instanceof TimerMessage) {
      doClean();
      return null;
    }

    // first joint message
    if (!resultMap.containsKey(flowMessage.getTransactionId())) {
      Set<Object> objectSet = new HashSet<Object>();
      resultMap.put(flowMessage.getTransactionId(), objectSet);
      resultNumberMap.put(flowMessage.getTransactionId(), sourceNumber);
      resultDateMap.put(flowMessage.getTransactionId(), System.currentTimeMillis());
    }
    resultMap.get(flowMessage.getTransactionId()).add(flowMessage.getMessage());

    Integer integer = resultNumberMap.get(flowMessage.getTransactionId()) - 1;
    resultNumberMap.put(flowMessage.getTransactionId(), integer);
    if (integer <= 0) {
      Set<Object> returnObject = resultMap.get(flowMessage.getTransactionId());
      resultMap.remove(flowMessage.getTransactionId());
      resultNumberMap.remove(flowMessage.getTransactionId());
      resultDateMap.remove(flowMessage.getTransactionId());

      return buildMessage(returnObject);
    }
    return null;
  }

  /**
   * subclass should override the method.
   * 
   * @param messages: Set<Message>
   * @return
   */
  public Object buildMessage(Set<Object> messages) {
    return messages;
  }

  // sourceNumber++ when initialize
  @Override
  public void setSourceNumber(int number) {
    sourceNumber = number;
  }

  private void doClean() {
    Set<String> transactionIds = resultDateMap.keySet();
    long currentTimeMillis = System.currentTimeMillis();
    for (String transactionId : transactionIds) {
      if (currentTimeMillis - resultDateMap.get(transactionId) > this.timeoutMillis) {
        resultDateMap.remove(transactionId);
        resultMap.remove(transactionId);
        resultNumberMap.remove(transactionId);
      }
    }
  }
}
