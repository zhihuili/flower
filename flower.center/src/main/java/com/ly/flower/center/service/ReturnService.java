/**
 * 
 */
package com.ly.flower.center.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ly.flower.center.util.R;
import com.ly.train.flower.common.annotation.FlowerService;
import com.ly.train.flower.common.service.Complete;
import com.ly.train.flower.common.service.Service;
import com.ly.train.flower.common.service.container.ServiceContext;
import com.ly.train.flower.common.service.web.Flush;

/**
 * @author leeyazhou
 *
 */
@FlowerService
public class ReturnService implements Service<Object, Object>, Flush, Complete {

  @Override
  public Object process(Object message, ServiceContext context) throws Throwable {
    context.getWeb().println(JSONObject.toJSONString(R.ok(message), SerializerFeature.PrettyFormat));
    return message;
  }

}
