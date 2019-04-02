/**
 * 
 */
package com.ly.flower.center.service;

import com.ly.flower.center.model.ServiceInfo;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;

/**
 * @author leeyazhou
 *
 */
@FlowerService
public class ServiceListService implements Service<ServiceInfo, Boolean> {

  @Override
  public Boolean process(ServiceInfo message, ServiceContext context) throws Throwable {
    
    
    return Boolean.TRUE;
  }

}
