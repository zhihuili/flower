/**
 * 
 */
package com.ly.train.flower.registry.simple;

import java.net.URL;
import java.util.List;
import com.ly.train.flower.common.util.HttpClient;
import com.ly.train.flower.registry.AbstractRegistry;
import com.ly.train.flower.registry.ServiceInfo;

/**
 * @author leeyazhou
 *
 */
public class SimpleRegistry extends AbstractRegistry {

  private final URL url;

  public SimpleRegistry(URL url) {
    this.url = url;
  }

  @Override
  public boolean doRegister(ServiceInfo serviceInfo) {
    logger.info("register serviceInfo : {}", serviceInfo);
    String u = String.format("http://%s:%s/service/register", url.getHost(), url.getPort());

    String ret = HttpClient.builder().setUrl(u).setParam(serviceInfo.toParam()).build().post();
    logger.info("register service result : {}, serviceInfo : {}", ret, serviceInfo);
    return Boolean.TRUE;
  }

  @Override
  public List<ServiceInfo> doGetProvider(ServiceInfo serviceInfo) {
    logger.info("register serviceInfo : {}", serviceInfo);
    String u = String.format("http://%s:%s/service/list", url.getHost(), url.getPort());
    String ret = HttpClient.builder().setUrl(u).setParam(serviceInfo.toParam()).build().post();
    logger.info("register service result : {}, serviceInfo : {}", ret, serviceInfo);
    return null;
  }


}
