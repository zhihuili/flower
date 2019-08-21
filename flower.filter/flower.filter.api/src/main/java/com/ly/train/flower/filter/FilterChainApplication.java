package com.ly.train.flower.filter;

import java.util.Arrays;
import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.common.exception.ServiceException;
import com.ly.train.flower.common.util.Assert;

/**
 * @author leeyazhou
 */
public class FilterChainApplication implements FilterChain {

  private Service<Object, Object> service;
  private Filter[] filters;
  private int pos;

  public FilterChainApplication(Filter[] filters, Service<Object, Object> service) {
    Assert.notNull(service, "service");
    this.service = service;
    this.filters = filters;
    if (filters == null) {
      this.filters = new Filter[0];
    } else {
      Arrays.sort(filters, (o1, o2) -> {
        if (o1.getOrder() > o2.getOrder()) {
          return 1;
        }
        return -1;
      });
    }
  }


  @Override
  public Object doFilter(Object message, ServiceContext context) {
    if (pos < filters.length) {
      return filters[pos++].doFilter(message, context, this);
    }
    try {
      return service.process(message, context);
    } catch (Throwable e) {
      throw new ServiceException("fail to invoke service : " + service + ", message : " + message, e);
    }

  }
}
