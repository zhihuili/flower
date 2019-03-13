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
package com.ly.train.flower.common.sample.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import static java.util.concurrent.TimeUnit.SECONDS;

public class FaultHandlingTest  {
  static ActorSystem system = ActorSystem.create("FaultHandlingTest");
  static scala.concurrent.duration.Duration timeout =
      scala.concurrent.duration.Duration.create(5, SECONDS);
  /*
   * 使用akka编写的Supervisor处理异常的例子
   */
  public static void main(String[] args) throws Exception {
    Props superprops = Props.create(Supervisor.class);
    ActorRef supervisor = system.actorOf(superprops, "supervisor");
    ActorRef child =
        (ActorRef) Await.result(Patterns.ask(supervisor, Props.create(Child.class), 5000), timeout);
    child.tell(42, ActorRef.noSender());
    assert Await.result(Patterns.ask(child, "get", 5000), timeout).equals(42);

    child.tell(new ArithmeticException(), ActorRef.noSender());
    assert Await.result(Patterns.ask(child, "get", 5000), timeout).equals(42);

    child.tell(new NullPointerException(), ActorRef.noSender());
    assert Await.result(Patterns.ask(child, "get", 5000), timeout).equals(0);

    Thread.sleep(1000);
    child.tell(111, ActorRef.noSender());
    assert Await.result(Patterns.ask(child, "get", 5000), timeout).equals(111);
    child.tell(123, ActorRef.noSender());
    assert Await.result(Patterns.ask(child, "get", 5000), timeout).equals(123);

    system.terminate();
  }
}
