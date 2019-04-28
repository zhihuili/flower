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
package com.ly.train.flower.common.service.container;

import org.junit.Assert;
import org.junit.Test;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.impl.AbstractService;
import com.ly.train.flower.common.service.web.HttpComplete;

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
  public void testLoadServiceMeta2() {
    serviceLoader.registerServiceType(SuperService.class.getName(), SuperService.class);
    ServiceMeta serviceMeta = serviceLoader.loadServiceMeta(SuperService.class.getName());
    Assert.assertEquals(String.class.getName(), serviceMeta.getParamType());
  }


  public class ObjectService implements Service<Object, Object> {
    @Override
    public Object process(Object message, ServiceContext context) throws Throwable {
      return null;
    }
  }

  public class SuperService extends AbstractService<String, String> implements HttpComplete {
    @Override
    public String doProcess(String message, ServiceContext context) throws Throwable {
      return null;
    }

    @Override
    public void onError(Throwable throwable, String param) {}
  }

}
