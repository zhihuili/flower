/**
 * 
 */
package com.ly.train.flower.base;

import org.junit.Before;
import com.ly.train.flower.base.service.ServiceA;
import com.ly.train.flower.base.service.ServiceB;
import com.ly.train.flower.base.service.ServiceC1;
import com.ly.train.flower.base.service.ServiceC2;
import com.ly.train.flower.base.service.ServiceD;
import com.ly.train.flower.common.service.container.ServiceFactory;

/**
 * @author leeyazhou
 *
 */
public class TestBase {
  protected final String flowName = "sample";

  @Before
  public void before() {
    ServiceFactory.registerService(ServiceA.class.getSimpleName(), ServiceA.class);
    ServiceFactory.registerService(ServiceB.class.getSimpleName(), ServiceB.class);
    ServiceFactory.registerService(ServiceC1.class.getSimpleName(), ServiceC1.class);
    ServiceFactory.registerService(ServiceC2.class.getSimpleName(), ServiceC2.class);
    ServiceFactory.registerService(ServiceD.class.getSimpleName(), ServiceD.class);
  }
}
