package com.ly.train.flower.common.actor;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.AsyncContext;

import com.ly.train.flower.common.service.message.FlowMessage;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;

public class ServiceRouter {

  int number;
  int currentIndex = 0;
  ActorRef[] ar;

  public ServiceRouter(String flowName, String serviceName, int number) {
    this.number = number;
    ar = new ActorRef[number];
    for (int i = 0; i < number; i++) {
      ar[i] = ServiceActorFactory.buildServiceActor(flowName, serviceName, i);
    }
  }

  public void asyncCallService(Object message) throws IOException {
    asyncCallService(message, null);
  }

  public void asyncCallService(Object message, AsyncContext ctx) throws IOException {
    FlowMessage flowMessage = ServiceUtil.buildFlowMessage(message);
    ServiceUtil.makeWebContext(flowMessage, ctx);
    ar[randomIndex()].tell(flowMessage, null);
  }

  public Object syncCallService(Object o) throws Exception {
    FlowMessage flowMessage = ServiceUtil.buildFlowMessage(o);
    ServiceUtil.makeWebContext(flowMessage, null);
    return Await.result(
        Patterns.ask(ar[randomIndex()], flowMessage, new Timeout(ServiceFacade.duration)),
        ServiceFacade.duration);
  }

  private int randomIndex() {
    int index = ThreadLocalRandom.current().nextInt(number);
    return index;
  }

  private synchronized int roundIndex() {
    if(currentIndex < (number-1)) {
      return ++currentIndex;
    }
    currentIndex = 0;
    return currentIndex;
  }


  /**
   * 当actor个数为2^n个数时才可以使用
   * @return
   */
  private int bitRandomIndex() {
    if (currentIndex > 1024) {
      currentIndex = 0;
    }
    return (currentIndex++) & (number-1);
  }

  /**
   * @return
   */
  private int moduleRandomIndex() {
    if (currentIndex > 1024) {
      currentIndex = 0;
    }
    return (currentIndex++)%(number);
  }
}
