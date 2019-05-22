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
/**
 * 
 */
package com.ly.train.flower.registry.simple;

import java.util.Date;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import com.ly.train.flower.base.TestBase;
import com.ly.train.flower.base.service.user.UserServiceA;
import com.ly.train.flower.base.service.user.UserServiceB;
import com.ly.train.flower.base.service.user.UserServiceC1;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.service.container.ServiceFlow;
import com.ly.train.flower.common.util.ExtensionLoader;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.RegistryFactory;
import com.ly.train.flower.registry.config.ServiceInfo;

/**
 * @author leeyazhou
 * 
 */
public class SimpleRegistryTest extends TestBase {

  @Test
  public void testRegister() throws Exception {

    URL url = new URL("flower", "127.0.0.1", 8096);
    url.addParam("application", "FlowerCenter");
    RegistryFactory factory = new SimpleRegistryFactory();
    Registry registry = factory.createRegistry(url);
    ((SimpleRegistry) registry).setFlowerFactory(flowerFactory);

    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setApplication(flowerFactory.getFlowerConfig().getName());
    serviceInfo.setClassName(UserServiceA.class.getName());
    serviceInfo.setServiceName(UserServiceA.class.getName());
    serviceInfo.setAddress(flowerFactory.getFlowerConfig().toURL());
    serviceInfo.setCreateTime(new Date());
    registry.register(serviceInfo);
    Thread.sleep(2000);
  }

  @Test
  public void testGetProviders() throws Exception {
    URL url = new URL("http", "127.0.0.1", 8096);
    url.addParam("application", "FlowerCenter");
    RegistryFactory factory = new SimpleRegistryFactory();
    Registry registry = factory.createRegistry(url);
    ((SimpleRegistry) registry).setFlowerFactory(flowerFactory);


    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setApplication(flowerFactory.getFlowerConfig().getName());
    serviceInfo.setClassName(UserServiceA.class.getName());
    serviceInfo.setCreateTime(new Date());
    List<ServiceInfo> serviceInfos = registry.getProvider(serviceInfo);
    System.out.println("请求结果:" + serviceInfos);
  }

  @Test
  @Ignore
  public void testRegisterServiceConfig() {
    URL url = new URL("http", "127.0.0.1", 8080);
    RegistryFactory factory = ExtensionLoader.load(RegistryFactory.class).load();
    Registry registry = factory.createRegistry(url);
    ((SimpleRegistry) registry).setFlowerFactory(flowerFactory);

    ServiceConfig serviceConfig =
        ServiceFlow.getOrCreate("registerFlow", serviceFactory).buildFlow(UserServiceA.class, UserServiceB.class)
            .buildFlow(UserServiceB.class, UserServiceC1.class).getServiceConfig("ServiceA");
    serviceConfig.addAddress(new URL("flower", "127.0.0.1", 12001));
    serviceConfig.addAddress(new URL("flower", "127.0.0.1", 12002));
    registry.registerServiceConfig(serviceConfig);
  }

  @Test
  @Ignore
  public void testGetServiceConfig() throws Exception {
    URL url = new URL("http", "127.0.0.1", 8080);
    RegistryFactory factory = ExtensionLoader.load(RegistryFactory.class).load();
    Registry registry = factory.createRegistry(url);
    ((SimpleRegistry) registry).setFlowerFactory(flowerFactory);
    List<ServiceConfig> serviceInfos = registry.getServiceConfig(null);
    System.out.println("请求结果:" + serviceInfos);
  }
}
