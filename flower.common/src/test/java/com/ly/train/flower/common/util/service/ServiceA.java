/**
 * 
 */
package com.ly.train.flower.common.util.service;

import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.util.model.UserA;

/**
 * @author leeyazhou
 *
 */
@FlowerService
public class ServiceA implements Service<UserA, UserA> {

  @Override
  public UserA process(UserA message, ServiceContext context) throws Throwable {
    return null;
  }

}
