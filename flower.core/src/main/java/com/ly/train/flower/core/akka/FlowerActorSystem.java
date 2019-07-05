package com.ly.train.flower.core.akka;

import java.util.concurrent.TimeUnit;
import com.ly.train.flower.common.exception.FlowException;
import com.ly.train.flower.common.lifecyle.AbstractLifecycle;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.core.akka.actor.SupervisorActor;
import com.ly.train.flower.core.akka.actor.command.ActorContextCommand;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import akka.actor.AbstractActor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * @author leeyazhou
 */
public class FlowerActorSystem extends AbstractLifecycle {
  private static final Logger logger = LoggerFactory.getLogger(FlowerActorSystem.class);

  public static final Long DEFAULT_TIMEOUT = 5000L;
  public static final Duration timeout = Duration.create(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

  private final FlowerConfig flowerConfig;
  private final ActorFactory actorFactory;
  private volatile ActorSystem actorSystem;
  private volatile ActorRef supervierActor;
  private volatile ActorContext actorContext;

  public FlowerActorSystem(FlowerConfig flowerConfig, ActorFactory actorFactory) {
    this.flowerConfig = flowerConfig;
    this.actorFactory = actorFactory;
    this.actorSystem = initActorSystem();
    initActorContext();
  }

  private ActorSystem initActorSystem() {
    StringBuffer configBuilder = new StringBuffer();

    final String sepator = "\r\n";
    // @formatter:off
    if (StringUtil.isNotBlank(flowerConfig.getHost())) {
      configBuilder.append(getFormatString("akka.actor.provider = %s", "remote")).append(sepator);
      configBuilder.append(getFormatString("akka.remote.enabled-transports = [%s]", "akka.remote.netty.tcp")).append(sepator);
      configBuilder.append(getFormatString("akka.remote.netty.tcp.hostname = %s", flowerConfig.getHost())).append(sepator);
      configBuilder.append(getFormatString("akka.remote.netty.tcp.port = %s", flowerConfig.getPort())).append(sepator);
    }
    configBuilder.append(getFormatString("dispatcher.fork-join-executor.parallelism-min = %s", flowerConfig.getParallelismMin())).append(sepator);
    configBuilder.append(getFormatString("dispatcher.fork-join-executor.parallelism-max = %s", flowerConfig.getParallelismMax())).append(sepator);
    configBuilder.append(getFormatString("dispatcher.fork-join-executor.parallelism-factor = %s", flowerConfig.getParallelismFactor())).append(sepator);
    // @formatter:on
    logger.info("akka config ：{}", configBuilder.toString());
    Config config = ConfigFactory.parseString(configBuilder.toString()).withFallback(ConfigFactory.load());
    ActorSystem actorSystem = ActorSystem.create("flower", config);
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
    return actorSystem;
  }

  private String getFormatString(String format, Object data) {
    return String.format(format, "\"" + data + "\"");
  }

  private void initActorContext() {
    try {
      this.supervierActor = actorSystem.actorOf(SupervisorActor.props(actorFactory), "flower");
      Future<Object> future = Patterns.ask(this.supervierActor, new ActorContextCommand(), DEFAULT_TIMEOUT - 1);
      this.actorContext = (ActorContext) Await.result(future, timeout);
    } catch (Exception e) {
      throw new FlowException("fail to start flower", e);
    }
  }

  /**
   * @return the actorContext
   */
  public ActorContext getActorContext() {
    return actorContext;
  }

  @Override
  protected void doStart() {}

  @Override
  protected void doStop() {
    logger.info("stop flower, config : {}", flowerConfig);
    this.actorSystem.terminate();
  }
}
