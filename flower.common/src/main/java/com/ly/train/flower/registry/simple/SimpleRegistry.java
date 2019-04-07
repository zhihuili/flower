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

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.service.config.ServiceConfig;
import com.ly.train.flower.common.util.HttpClient;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.common.util.URL;
import com.ly.train.flower.registry.AbstractRegistry;
import com.ly.train.flower.registry.config.ServiceInfo;

/**
 * @author leeyazhou
 *
 */
public class SimpleRegistry extends AbstractRegistry {

  public SimpleRegistry(URL url) {
    super(url);
  }

  @Override
  public boolean doRegister(ServiceInfo serviceInfo) {
    // logger.info("register serviceInfo : {}", serviceInfo);
    String u = String.format("http://%s:%s/service/register", url.getHost(), url.getPort());

    String ret = HttpClient.builder().setUrl(u).setParam("data=" + JSONObject.toJSONString(serviceInfo)).build().post();
    // logger.info("register service result : {}, serviceInfo : {}", ret,
    // serviceInfo);
    return Boolean.TRUE;
  }

  @Override
  public boolean doRegisterServiceConfig(ServiceConfig serviceConfig) {
    String u = String.format("http://%s:%s/serviceconfig/register", url.getHost(), url.getPort());

    String ret =
        HttpClient.builder().setUrl(u).setParam("data=" + JSONObject.toJSONString(serviceConfig)).build().post();

    return false;
  }

  @Override
  public List<ServiceInfo> doGetProvider(ServiceInfo serviceInfo) {
    String param = "";
    if (serviceInfo != null) {
      param = JSONObject.toJSONString(serviceInfo);
    }
    // logger.info("register serviceInfo : {}", serviceInfo);
    String u = String.format("http://%s:%s/service/list", url.getHost(), url.getPort());
    String ret = HttpClient.builder().setUrl(u).setParam("data=" + param).build().post();
    // logger.info("register service result : {}, serviceInfo : {}", ret,
    // serviceInfo);
    if (StringUtil.isNotBlank(ret)) {
      ret = JSONObject.parseObject(ret).getString("data");
      return JSONObject.parseArray(ret, ServiceInfo.class);
    }
    return null;
  }

  @Override
  public List<ServiceConfig> doGetServiceConfig(ServiceConfig serviceConfig) {
    String param = "";
    if (serviceConfig != null) {
      param = JSONObject.toJSONString(serviceConfig);
    }
    // logger.info("register serviceInfo : {}", serviceInfo);
    String u = String.format("http://%s:%s/serviceconfig/list", url.getHost(), url.getPort());
    String ret = HttpClient.builder().setUrl(u).setParam("data=" + param).build().post();
    // logger.info("register service result : {}, serviceInfo : {}", ret,
    // serviceInfo);
    if (StringUtil.isNotBlank(ret)) {
      ret = JSONObject.parseObject(ret).getString("data");
      return JSONObject.parseArray(ret, ServiceConfig.class);
    }
    return null;
  }
}
