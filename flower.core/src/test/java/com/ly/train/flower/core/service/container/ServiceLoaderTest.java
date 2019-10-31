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
package com.ly.train.flower.core.service.container;

import org.junit.Assert;
import org.junit.Test;
import com.ly.train.flower.common.core.config.ServiceMeta;
import com.ly.train.flower.common.core.proxy.MethodProxy;
import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.core.service.impl.AbstractService;
import com.ly.train.flower.core.service.web.HttpComplete;

/**
 * @author leeyazhou
 * 
 */
public class ServiceLoaderTest {

  private ServiceLoader serviceLoader = new ServiceLoader(null);

  @Test
  public void testLoadServiceMeta() {
    serviceLoader.registerServiceType(ObjectService.class.getName(), ObjectService.class);
    ServiceMeta serviceMeta = serviceLoader.loadServiceMeta(ObjectService.class.getName());
    Assert.assertEquals(Object.class.getName(), serviceMeta.getParamType());
  }

  @Test
  public void testLoadServiceMetaAndParamTypeFromSuperclass() {
    serviceLoader.registerServiceType(SuperService.class.getName(), SuperService.class);
    MethodProxy methodProxy = serviceLoader.loadService(SuperService.class.getName());
    Assert.assertEquals(true, methodProxy.isCompleted());
    ServiceMeta serviceMeta = serviceLoader.loadServiceMeta(SuperService.class.getName());
    Assert.assertEquals(String.class.getName(), serviceMeta.getParamType());
  }

  @Test
  public void testLoadServiceMetaAndParamTypeFromSuperclass2() {
    serviceLoader.registerServiceType(SuperService2.class.getName(), SuperService2.class);
    MethodProxy methodProxy = serviceLoader.loadService(SuperService2.class.getName());
    Assert.assertEquals(false, methodProxy.isCompleted());
    ServiceMeta serviceMeta = serviceLoader.loadServiceMeta(SuperService2.class.getName());
    Assert.assertEquals(Object.class.getName(), serviceMeta.getParamType());
  }



}


class ObjectService implements Service<Object, Object> {
  @Override
  public Object process(Object message, ServiceContext context) throws Throwable {
    return message;
  }
}


class SuperService extends AbstractService<String, String> implements HttpComplete {
  @Override
  public String doProcess(String message, ServiceContext context) throws Throwable {
    return null;
  }
}


class BaseService implements Service<String, Object> {

  @Override
  public Object process(String message, ServiceContext context) throws Throwable {
    return null;
  }

}


class SuperService2 extends BaseService {

}
