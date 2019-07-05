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
package com.ly.train.flower.common.core.service;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.ly.train.flower.common.core.message.FlowMessage;
import com.ly.train.flower.common.core.message.Message;
import com.ly.train.flower.common.core.web.Web;
import com.ly.train.flower.common.util.CloneUtil;
import com.ly.train.flower.common.util.StringUtil;

public class ServiceContext implements Message, Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * 附属参数
   */
  private Map<String, Object> attachments;
  private String id = StringUtil.uuid();
  private Web web;
  private boolean sync;
  private String flowName;
  private String codec = "hessian";

  private FlowMessage flowMessage;

  private String currentServiceName;

  public ServiceContext() {}

  /**
   * 从当前对象创建一个副本
   * 
   * @return {@link ServiceContext}
   */
  public ServiceContext newInstance() {
    ServiceContext serviceContext = new ServiceContext();
    serviceContext.setWeb(this.getWeb());
    serviceContext.attachments = attachments;
    serviceContext.id = this.id;
    serviceContext.setCodec(this.codec);
    serviceContext.setFlowMessage(CloneUtil.clone(flowMessage));
    serviceContext.setSync(this.sync);
    serviceContext.setFlowName(this.flowName);
    return serviceContext;
  }

  public Web getWeb() {
    return web;
  }

  public ServiceContext setWeb(Web web) {
    this.web = web;
    return this;
  }

  public ServiceContext addAttachment(String key, Object value) {
    if (attachments == null) {
      attachments = new ConcurrentHashMap<String, Object>();
    }
    attachments.put(key, value);
    return this;
  }

  public Object getAttachment(String key) {
    if (attachments == null) {
      return null;
    }
    return attachments.get(key);
  }

  public ServiceContext removeAttachment(String key) {
    attachments.remove(key);
    return this;
  }



  public FlowMessage getFlowMessage() {
    return flowMessage;
  }

  public void setFlowMessage(FlowMessage flowMessage) {
    this.flowMessage = flowMessage;
  }


  /**
   * 服务ID
   * 
   * @return string
   */
  public String getId() {
    return id;
  }

  public boolean isSync() {
    return sync;
  }

  /**
   * 同步调用
   * 
   * @param sync
   */
  public ServiceContext setSync(boolean sync) {
    this.sync = sync;
    return this;
  }

  public String getFlowName() {
    return flowName;
  }

  public ServiceContext setFlowName(String flowName) {
    this.flowName = flowName;
    return this;
  }

  public ServiceContext setCurrentServiceName(String currentServiceName) {
    this.currentServiceName = currentServiceName;
    return this;
  }

  public String getCurrentServiceName() {
    return currentServiceName;
  }

  /**
   * @param codec the codec to set
   */
  public void setCodec(String codec) {
    this.codec = codec;
  }

  /**
   * @return the codec
   */
  public String getCodec() {
    return codec;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ServiceContext [id=");
    builder.append(id);
    builder.append(", flowName=");
    builder.append(flowName);
    builder.append(", currentServiceName=");
    builder.append(currentServiceName);
    builder.append(", sync=");
    builder.append(sync);
    builder.append(", attachments=");
    builder.append(attachments);
    builder.append(", flowMessage=");
    builder.append(flowMessage);
    builder.append(", web=");
    builder.append(web);
    builder.append("]");
    return builder.toString();
  }


}
