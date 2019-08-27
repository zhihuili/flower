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
package com.ly.train.flower.core.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.ly.train.flower.common.core.message.FlowMessage;
import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.Assert;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.common.util.cache.Cache;
import com.ly.train.flower.common.util.cache.CacheManager;
import com.ly.train.flower.core.service.Aggregate;
import com.ly.train.flower.serializer.Serializer;

public class AggregateService implements Service<Object, List<Object>>, Aggregate {
  static final Logger logger = LoggerFactory.getLogger(AggregateService.class);
  private static final Long DefaultTimeOutMilliseconds = 60000L;

  private int sourceNumber = 0;
  private Long timeoutMillis = DefaultTimeOutMilliseconds;

  private static final String cacheKeyPrefix = "FLOWER_AGGREGATE_SERVICE_";

  // private ReentrantLock lock = new ReentrantLock();

  public AggregateService() {}

  public AggregateService(String config) {
    this.timeoutMillis = Long.valueOf(config);
  }

  @Override
  public List<Object> process(Object message, ServiceContext context) {
    FlowMessage flowMessage = context.getFlowMessage();

    final String transactionId = flowMessage.getTransactionId();
    AggregateInfo aggregateInfo = getAndCacheResult(context.getFlowName(), transactionId, flowMessage);
    if (aggregateInfo.getResultNum().get() <= 0) {
      clear(context.getFlowName(), transactionId);
      return buildMessage(aggregateInfo.getResults(), context);
    }
    return null;
  }

  private void clear(final String flowName, final String transactionId) {
    Assert.notNull(flowName, "flowName can't be null .");
    CacheManager.get(cacheKeyPrefix + flowName).invalidate(transactionId);
  }

  private AggregateInfo getAndCacheResult(final String flowName, final String transactionId, FlowMessage flowMessage) {
    Assert.notNull(flowName, "flowName can't be null .");
    CacheManager cacheManager = CacheManager.get(cacheKeyPrefix + flowName);
    AggregateInfo aggregateInfo = null;
    // lock.lock();
    try {
      Cache<AggregateInfo> cache = cacheManager.getCache(transactionId);
      if (cache == null) {
        cache = cacheManager.getCache(transactionId);
        if (cache == null) {
          aggregateInfo = new AggregateInfo(transactionId, sourceNumber);
          Cache<AggregateInfo> temp = cacheManager.add(transactionId, aggregateInfo, timeoutMillis);
          if (temp != null) {
            cache = temp;
          } else {
            cache = cacheManager.getCache(transactionId);
          }
        }
      }
      aggregateInfo = cache.getValue();
      aggregateInfo.addResult(flowMessage);
      aggregateInfo.getResultNum().decrementAndGet();// 计数-1
    } finally {
      // lock.unlock();
    }
    return aggregateInfo;
  }

  /**
   * subclass should override the method.
   * 
   * @param messages Set&lt;Message&gt;
   * @param context {@link ServiceContext}
   * @return Object
   */
  public List<Object> buildMessage(List<FlowMessage> messages, ServiceContext context) {
    List<Object> ret = new ArrayList<Object>();
    for (FlowMessage message : messages) {
      try {
        Serializer codec = ExtensionLoader.load(Serializer.class).load(context.getCodec());
        ret.add(codec.decode(message.getMessage(), message.getMessageType()));
      } catch (Exception e) {
        logger.error("序列化异常 : ", e);
        ret.add(1);
      }
    }
    return ret;
  }

  // sourceNumber++ when initialize
  public void setSourceNumber(int sourceNumber) {
    this.sourceNumber = sourceNumber;
  }


  // cahce object
  class AggregateInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long createTime = System.currentTimeMillis();
    private final String id;
    private List<FlowMessage> results;
    private AtomicInteger resultNum;

    public AggregateInfo(String id, int resultNum) {
      Assert.notNull(id, "cacheKey can't be null.");
      this.id = id;
      this.resultNum = new AtomicInteger(resultNum);
    }

    public AggregateInfo addResult(FlowMessage result) {
      if (results == null) {
        results = new ArrayList<>();
      }
      results.add(result);
      return this;
    }

    public List<FlowMessage> getResults() {
      return results;
    }

    public AtomicInteger getResultNum() {
      return resultNum;
    }

    public String getId() {
      return id;
    }

    public boolean isTimeout() {
      return System.currentTimeMillis() - createTime > AggregateService.this.timeoutMillis;
    }

  }
}
