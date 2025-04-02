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
package com.ly.train.flower.core.akka;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.pekko.actor.AbstractActor.ActorContext;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSelection;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.pattern.Patterns;

import com.ly.train.flower.common.exception.FlowerException;
import com.ly.train.flower.common.lifecyle.AbstractLifecycle;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.core.akka.actor.ServiceActor;
import com.ly.train.flower.core.akka.actor.SupervisorActor;
import com.ly.train.flower.core.akka.actor.command.ActorContextCommand;
import com.ly.train.flower.core.akka.extension.FlowerExtension;
import com.ly.train.flower.core.akka.extension.FlowerExtensionConfiguration;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author leeyazhou
 */
public class FlowerActorSystem extends AbstractLifecycle {
	private static final Logger logger = LoggerFactory.getLogger(FlowerActorSystem.class);
	private static final String actorSystemName = "flower";
	private static final String supervisorPathName = "flower";
	private static final String dispatcherName = "dispatcher";
	/**
	 * for example : akka://flower@127.0.0.1:2551/user/flower
	 */
	public static final String actorPathFormat = "akka://%s@%s:%s/user/flower/%s_%s";
	/**
	 * for example : akka://flower@127.0.0.1:2551/user/flower
	 */
	public static final String supervisorActorPathFormat = "akka://%s@%s:%s/user/flower";

	public static final Long DEFAULT_TIMEOUT = 3000L;
	public static final Duration timeout = Duration.create(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

	private FlowerConfig flowerConfig;
	private final ActorFactory actorFactory;
	private volatile ActorSystem actorSystem;
	private volatile ActorContext actorContext;
	private volatile FlowerFactory flowerFactory;
	private Config actorSystemConfig;

	public FlowerActorSystem(ActorFactory actorFactory, FlowerFactory flowerFactory) {
		this.actorFactory = actorFactory;
		this.flowerFactory = flowerFactory;
	}

	@Override
	protected void doInit() {
		this.flowerConfig = flowerFactory.getFlowerConfig();
		Properties properties = new Properties();
		if (StringUtil.isNotBlank(flowerConfig.getHost()) && flowerConfig.getPort() > 0) {
			properties.put("akka.actor.provider", "remote");
			properties.put("akka.remote.artery.transport", "tcp");
			properties.put("akka.remote.artery.canonical.hostname", flowerConfig.getHost());
			properties.put("akka.remote.artery.canonical.port", String.valueOf(flowerConfig.getPort()));
		}

		properties.put("dispatcher.fork-join-executor.parallelism-min", flowerConfig.getParallelismMin());
		properties.put("dispatcher.fork-join-executor.parallelism-max", flowerConfig.getParallelismMax());
		properties.put("dispatcher.fork-join-executor.parallelism-factor", flowerConfig.getParallelismFactor());
		logger.info("akka config ：{}", properties);
		this.actorSystemConfig = ConfigFactory.parseProperties(properties).withFallback(ConfigFactory.load());
	}

	@Override
	protected void doStart() {
		this.actorSystem = ActorSystem.create(actorSystemName, actorSystemConfig);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					actorSystem.terminate();
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		});
		initActorContext();
	}

	private void initActorContext() {
		try {
			Props props = SupervisorActor.props(actorFactory).withDispatcher(dispatcherName);
			ActorRef supervierActor = actorSystem.actorOf(props, supervisorPathName);
			Future<Object> future = Patterns.ask(supervierActor, new ActorContextCommand(), DEFAULT_TIMEOUT - 1);
			this.actorContext = (ActorContext) Await.result(future, timeout);
		} catch (Exception e) {
			throw new FlowerException("fail to start flower", e);
		}
	}

	protected ActorRef createLocalActor(String serviceName, int index) {
		final String cacheKey = serviceName + "_" + index;
		Props props = ServiceActor.props(serviceName, flowerFactory, index).withDispatcher(dispatcherName);
		ActorRef actorRef = actorContext.actorOf(props, cacheKey);
		actorContext.watch(actorRef);
		return actorRef;
	}

	protected ActorRef createRemoteActor(String host, int port, String serviceName, int index) {
		final String actorPath = String.format(actorPathFormat, actorSystemName, host, port, serviceName, index);
		return createRemoteActor(actorPath);
	}

	protected ActorRef createRemoteActor(String actorPath) {
		try {
			ActorSelection actorSelection = actorContext.actorSelection(actorPath);
			ActorRef actorRef = Await.result(actorSelection.resolveOne(new FiniteDuration(3, TimeUnit.SECONDS)),
					Duration.create(3, TimeUnit.SECONDS));
			actorContext.watch(actorRef);
			return actorRef;
		} catch (Exception e) {
			throw new FlowerException("fail to create remote actor, actor path : " + actorPath, e);
		}
	}

	public String getActorPath(String host, int port, String serviceName, int index) {
		return String.format(actorPathFormat, actorSystemName, host, port, serviceName, index);
	}

	public String getSupervisorActorPath(String host, int port) {
		return String.format(supervisorActorPathFormat, actorSystemName, host, port);
	}

	public FlowerExtension getConfigurationExtension() {
		return FlowerExtensionConfiguration.getFlowerExtension(actorSystem);
	}

	@Override
	protected void doStop() {
		logger.info("Stop flower, config : {}", flowerConfig);
		this.actorSystem.terminate();
	}
}
