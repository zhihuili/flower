package com.ly.train.flower.filter;

import com.ly.train.flower.common.core.service.ServiceContext;

/**
 * @author leeyazhou
 */
public interface FilterChain {

  Object doFilter(Object message, ServiceContext context);
}
