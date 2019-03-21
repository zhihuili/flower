/**
 * 
 */
package com.ly.train.flower.common.filter;

import com.ly.train.flower.common.service.container.ServiceContext;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractFilter implements Filter {

  private AbstractFilter nextFilter;

  @Override
  public void filter(ServiceContext serviceContext) {
    doFilter(serviceContext);
    if (nextFilter != null) {
      nextFilter.filter(serviceContext);
    }
  }

 public abstract void doFilter(ServiceContext serviceContext);

  public void addFilter(Filter filter) {
    if (this.nextFilter == null) {
      this.nextFilter = (AbstractFilter) filter;
    } else {
      this.nextFilter.addFilter(filter);
    }
  }

}
