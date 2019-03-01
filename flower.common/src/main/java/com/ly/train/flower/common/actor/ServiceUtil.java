package com.ly.train.flower.common.actor;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.AsyncContext;

import com.ly.train.flower.common.service.containe.FlowContext;
import com.ly.train.flower.common.service.containe.ServiceContext;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.web.Web;

public class ServiceUtil {

  public static FlowMessage buildFlowMessage(Object o) {
    FlowMessage flowMessage = new FlowMessage();
    flowMessage.setTransactionId(UUID.randomUUID().toString());
    flowMessage.setMessage(o);
    return flowMessage;
  }

  public static void makeWebContext(FlowMessage flowMessage, AsyncContext ctx) throws IOException {
    ServiceContext serviceContext = new ServiceContext();
    if (ctx != null) {
      Web web = new Web(ctx);
      serviceContext.setWeb(web);
    }
    FlowContext.putServiceContext(flowMessage.getTransactionId(), serviceContext);
  }

}
