package com.ly.train.flower.common.actor;

import com.ly.train.flower.common.service.message.FlowMessage;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;

public class ServiceRouter {

  int number;
  ActorRef[] ar;

  public ServiceRouter(String flowName, String serviceName, int number) {
    this.number = number;
    ar = new ActorRef[number];
    for (int i = 0; i < number; i++) {
      ar[i] = ServiceActorFactory.buildServiceActor(flowName, serviceName, i);
    }
  }

  public void asyncCallService(Object message) {
    FlowMessage flowMessage = new FlowMessage();
    flowMessage.setMessage(message);
    ar[index()].tell(flowMessage, null);
  }

  public Object syncCallService(Object o) throws Exception {
    FlowMessage flowMessage = new FlowMessage();
    flowMessage.setMessage(o);
    return Await.result(Patterns.ask(ar[index()], flowMessage, new Timeout(ServiceFacade.duration)),
        ServiceFacade.duration);
  }

  private int index() {
    return (int) (Math.random() * number);
  }
}
