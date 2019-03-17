package com.ly.train.flower.common.util;

import akka.actor.ActorRef;
import com.ly.train.flower.common.service.containe.FlowContext;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.message.TimerMessage;

import java.util.*;

public class ServiceTimer {
    private static final long TimePeriod = 5000;
    private static final long DelayTime = 10000;

    private Set<ActorRef> aggregateServiceActors;
    private Timer timer;

    private static ServiceTimer instance = new ServiceTimer();

    public static ServiceTimer getInstance(){
        return instance;
    }

    private ServiceTimer(){
        aggregateServiceActors = new HashSet<>();
        timer = new Timer();
        schedule();
    }

    public void add(ActorRef actor){
        synchronized (aggregateServiceActors){
            aggregateServiceActors.add(actor);
        }
    }

    private void schedule(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 清理超时context
                    FlowContext.clearTimeoutServiceContext();

                    // 清理聚合服务内的超时对象
                    for (ActorRef actor : aggregateServiceActors) {
                        TimerMessage timerMessage = new TimerMessage();
                        timerMessage.setTransactionId(UUID.randomUUID().toString());
                        timerMessage.setMessage(new Object());
                        FlowMessage flowMessage = new FlowMessage();
                        flowMessage.setTransactionId(timerMessage.getTransactionId());
                        flowMessage.setMessage(timerMessage);
                        actor.tell(flowMessage, null);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, DelayTime, TimePeriod);
    }
}
