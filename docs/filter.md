# 过滤器

Filter是flower扩展支持的拦截器，拦截器开发需要定义一个实现Filter接口的类，并在classpath的目录META-INF/services/flower下的文件com.ly.train.flower.filter.Filter中进行声明。

## 过滤器定义

```java
package com.ly.train.flower.filter.impl;

import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.filter.AbstractFilter;
import com.ly.train.flower.filter.FilterChain;

/**
 * @author leeyazhou
 * 
 */
public class AccessLogFilter extends AbstractFilter {
  private static final Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);

  @Override
  public Object doFilter(Object message, ServiceContext context, FilterChain chain) {
    long start = System.currentTimeMillis();
    Object result = null;
    try {
      result = chain.doFilter(message, context);
      return result;
    } finally {
      logger.info("flowName : {}, serviceName : {}, invoke time(ms):{}, message : {}, result : {}",
          context.getFlowName(), (System.currentTimeMillis() - start), context.getCurrentServiceName(), message,
          result);
    }
  }

}
```

## 过滤器配置

1. 格式
```text
filterName=filterClass
```

2. 配置文件META-INF/services/flower/com.ly.train.flower.filter.Filter

```text
accessLogFilter=com.ly.train.flower.filter.impl.AccessLogFilter
```

## 过滤器应用

通过以下方式组建一个服务流程，并使用setFilters设置要应用的过滤器列表，在调用这个服务流程的时候，过滤器就会生效。

```java
    // 组建服务流程
    ServiceFlow serviceFlow = serviceFactory.getOrCreateServiceFlow(flowName);
    serviceFlow.buildFlow(UserServiceA.class, UserServiceB.class);
    serviceFlow.buildFlow(UserServiceB.class, UserServiceC1.class);
    serviceFlow.buildFlow(UserServiceC1.class, UserServiceD.class);
    // 应用过滤器
    serviceFlow.setFilters(Sets.newSet("accessLogFilter"));
    serviceFlow.build();
```
