/**
 * 
 */
package com.ly.train.flower.registry.simple;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import com.ly.train.flower.base.service.ServiceA;
import com.ly.train.flower.registry.Registry;
import com.ly.train.flower.registry.RegistryFactory;
import com.ly.train.flower.registry.ServiceInfo;

/**
 * @author leeyazhou
 *
 */
public class SimpleRegistryTest {

  @Test
  public void testRegister() throws Exception {

    URL url = new URL("http://127.0.0.1:8080");
    RegistryFactory factory = new SimpleRegistryFactory();
    Registry registry = factory.createRegistry(url);


    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setClassName(ServiceA.class.getName());
    Set<String> host = new HashSet<>();
    host.add("127.0.0.1:12001");
    host.add("127.0.0.1:12002");
    serviceInfo.setHost(host);
    serviceInfo.setCreateTime(new Date());
    registry.register(serviceInfo);
  }

  @Test
  public void testGetProviders() throws MalformedURLException {
    URL url = new URL("http://127.0.0.1:8080");
    RegistryFactory factory = new SimpleRegistryFactory();
    Registry registry = factory.createRegistry(url);


    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setClassName(ServiceA.class.getName());
    Set<String> host = new HashSet<>();
    host.add("127.0.0.1:12001");
    host.add("127.0.0.1:12002");
    serviceInfo.setHost(host);
    serviceInfo.setCreateTime(new Date());
    List<ServiceInfo> serviceInfos = registry.getProvider(serviceInfo);
    System.out.println("请求结果:" + serviceInfos);
  }
}
