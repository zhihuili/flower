package com.ly.train.flower.common.sample.web;

import java.util.Date;

import com.ly.train.flower.common.service.containe.FlowContext;
import com.ly.train.flower.common.service.web.Web;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyActor extends AbstractActor {
  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(
            String.class,
            s -> {
              log.info("Received String message: {}", s);
              Thread.sleep(100);
              Web web = FlowContext.getServiceContext(s).getWeb();
              web.println("Hello，MyActor");
              web.flush();
              web.complete();
              
              // #my-actor
              // #reply
//              getSender().tell(s, getSelf());
              // #reply
              // #my-actor
            })
        .matchAny(o -> log.info("received unknown message"))
        .build();
  }
}