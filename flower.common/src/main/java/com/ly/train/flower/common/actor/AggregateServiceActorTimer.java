package com.ly.train.flower.common.actor;

import akka.actor.ActorRef;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.message.TimerMessage;

import java.util.*;

public class AggregateServiceActorTimer {
    private static final long TimePeriod = 5000;
    private static final long DelayTime = 10000;

    private Set<ActorRef> _aggregateServiceActors;
    private Timer _timer;
    private boolean _isScheduled;

    private static AggregateServiceActorTimer _instance = new AggregateServiceActorTimer();

    public static AggregateServiceActorTimer getInstance(){
        return _instance;
    }

    protected AggregateServiceActorTimer(){
        _aggregateServiceActors = new HashSet<>();
        _timer = new Timer();
        _isScheduled = false;
    }

    public void add(ActorRef actor){
        synchronized (_aggregateServiceActors){
            _aggregateServiceActors.add(actor);
            schedule();
        }
    }

    private void schedule(){
        if(_isScheduled){ return; }

        _isScheduled = true;
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    for (ActorRef actor : _aggregateServiceActors) {
                        TimerMessage timerMessage = new TimerMessage();
                        timerMessage.setTransactionId(UUID.randomUUID().toString());
                        timerMessage.setMessage(new Object());
                        FlowMessage flowMessage = new FlowMessage();
                        flowMessage.setTransactionId(timerMessage.getTransactionId());
                        flowMessage.setMessage(timerMessage);
                        actor.tell(flowMessage, null);
                    }
                }catch(Exception e){

                }
            }
        }, DelayTime, TimePeriod);
    }
}
