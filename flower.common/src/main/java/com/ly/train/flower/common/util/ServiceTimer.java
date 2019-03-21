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
package com.ly.train.flower.common.util;

import akka.actor.ActorRef;
import com.ly.train.flower.common.service.message.FlowMessage;
import com.ly.train.flower.common.service.message.TimerMessage;

import java.util.*;

public class ServiceTimer {
    private static final long TimePeriod = 5000;
    private static final long DelayTime = 10000;

    private Set<ActorRef> aggregateServiceActors;
    private Timer timer;
    private boolean isScheduled;

    private static ServiceTimer instance = new ServiceTimer();

    public static ServiceTimer getInstance(){
        return instance;
    }

    protected ServiceTimer(){
        aggregateServiceActors = new HashSet<>();
        timer = new Timer();
        isScheduled = false;
    }

    public void add(ActorRef actor){
        synchronized (aggregateServiceActors){
            aggregateServiceActors.add(actor);
            schedule();
        }
    }

    /**
     * public void add(ActorRef actor) 之外调用会引起并发问题。
     */
    private void schedule(){
        if(isScheduled){ return; }

        isScheduled = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
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

                }
            }
        }, DelayTime, TimePeriod);
    }
}
