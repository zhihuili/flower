package com.ly.train.flower.ddd;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ly.train.flower.core.akka.ServiceFacade;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceFactory;
import com.ly.train.flower.core.service.container.ServiceLoader;
import com.ly.train.flower.core.service.container.simple.SimpleFlowerFactory;
import com.ly.train.flower.ddd.api.command.CreateOrderCommand;
import com.ly.train.flower.ddd.api.command.SelectOrderCommand;
import com.ly.train.flower.ddd.config.DDDConfig;
import com.ly.train.flower.ddd.factory.DDDFactory;

/**
 * @author leeyazhou
 */
public class DDDTest {

  protected static FlowerFactory flowerFactory;
  protected static ServiceFactory serviceFactory;
  protected static ServiceLoader serviceLoader;
  protected static ServiceFacade serviceFacade;


  @BeforeClass
  public static void beforeClass() {
    flowerFactory = new SimpleFlowerFactory();
    serviceFactory = flowerFactory.getServiceFactory();
    serviceLoader = serviceFactory.getServiceLoader();
    serviceFacade = flowerFactory.getServiceFacade();
  }

  public static int sleep = 2000;

  @AfterClass
  public static void afterClass() throws InterruptedException {
    System.out.println("休眠" + sleep + "ms后stopFlower。");
    Thread.sleep(sleep);
    flowerFactory.stop();
  }

  @Test
  public void testDDD() {
    DDDFactory dddFactory = new DDDFactory(flowerFactory, null);
    int i = 0;
    while (i++ < 100000) {
      System.out.println("创建订单" + i);
      dddFactory.getCommandGateway().send(new CreateOrderCommand(Long.parseLong(i + ""), "test ddd flow"));
      System.out.println("选择订单" + i);
      dddFactory.getCommandGateway().send(new SelectOrderCommand(Long.parseLong(i + "")));
    }

  }

  @Test
  public void testConfig() {
    DDDConfig config = new DDDConfig();
    config.scan(null);
  }
}
