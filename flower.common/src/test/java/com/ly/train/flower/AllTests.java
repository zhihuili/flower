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
package com.ly.train.flower;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import com.ly.train.flower.common.akka.actor.ActorSelectionTest;
import com.ly.train.flower.common.akka.actor.ServiceFacadeTest;
import com.ly.train.flower.common.akka.actor.ServiceRouterTest;
import com.ly.train.flower.common.bytecode.ClassGeneratorTest;
import com.ly.train.flower.common.service.ServiceFlowTest;
import com.ly.train.flower.common.util.HttpClientTest;
import com.ly.train.flower.registry.simple.SimpleRegistryTest;
import com.ly.train.flower.registry.zookeeper.ZookeeperRegistryTest;

/**
 * @author leeyazhou
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ActorSelectionTest.class, ServiceFacadeTest.class, ServiceRouterTest.class, ClassGeneratorTest.class,
    ServiceFlowTest.class, HttpClientTest.class, SimpleRegistryTest.class, ZookeeperRegistryTest.class})
public class AllTests {

}
