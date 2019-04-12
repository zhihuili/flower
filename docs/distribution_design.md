# Flower分布式开发

## 核心概念

- FlowerFactory Flower框架的入口程序，同一个JVM进程中可以创建多个FlowerFactory，互相不影响，实现应用隔离。
- ServiceFactory 管理Flower框架中的服务，包括流程管理和服务管理
- FlowRouter 流程的载体，一个FlowerRouter包含一个流程相关信息
- ServiceRouter 服务的载体，一个ServiceRouter包含一个服务的相关信息

### FlwerFactory

- 方法一

使用默认的FlowerFactory
```
FlowerFactory flowerFactory = SimpleFlowerFactory.get();
flowerFactory.start();
flowerFactory.stop();
```

- 方法二

按需创建自己的FlowerFactory，配置文件路径默认读取classpath:flower.yml,配置文件内容格式为yaml风格，详情查看配置信息。

```
FlowerFactory factory = new SimpleFlowerFactory("conf/flower_25003.yml");
factory.start();
factory.stop();
```

获取FlowerFactory之后，就可以使用它提供的接口：
```
  /**
   * 获取Flower容器配置信息
   * 
   * @return {@link FlowerConfig}
   */
  FlowerConfig getFlowerConfig();

  /**
   * 获取注册中心
   * 
   * @return {@link Registry}
   */
  Set<Registry> getRegistry();

  /**
   * 异常处理器
   * 
   * @return {@link ExceptionHandler}
   */
  ExceptionHandler getExceptionHandler();

  /**
   * akka Actor 工厂
   * 
   * @return {@link ServiceActorFactory}
   */
  ServiceActorFactory getServiceActorFactory();

  /**
   * {@link Service}工厂
   * 
   * @return {@link ServiceFactory}
   */
  ServiceFactory getServiceFactory();

  ServiceFacade getServiceFacade(); 
```

### FlowRouter流程路由器，创建流程之后，通过FlowerFactory可以创建出对应的路由器，之后便可以进行服务的调用了。
``` 
FlowRouter flowRouter = factory.getServiceFacade().buildFlowRouter("flowerSample", 2 << 6);
flowRouter.syncCallService(message);
flowRouter.asyncCallService(message, ctx);
```

## 分布式

### Flower.yml配置信息
```
name: "LocalFlower"
host: "127.0.0.1"
port: 25003
# 注册中心地址
registry:
  - url: 
      - "flower://127.0.0.1:8096"
  - url:
     - "flower://127.0.0.1:8096"
basePackage: com.ly.train.flower
```

  - name 服务名称
  - host 服务的地址
  - port 服务对外暴露的端口，也是当前Flower监听的段端口
  - registry 注册中心，对于有多个注册中心的服务，需要配置多个地址
  - basePackage 服务扫描的路径，扫描到对应的FlowerService之后会自动注册

1. Flower容器启动后，会把本地的服务元数据注册到注册中心，便可以为其他应用提供服务；流程编排的过程中，会优先使用本地容器中包含的容器，如果在本地找不到对应的服务信息，会从注册中心拉取服务信息，并创建RemoteActor备用。

2. 流程创建完毕，会把流程配置信息上传到注册中心。如果流程中涉及到本地Service和远程Service进行混排时，那么远程Service执行完毕后，可能需要把消息回传到本地Service中，这时需要从注册中心拉取Flow的配置信息，然后才能获取到当前服务的下一个Service的地址。

## 流程图
<img src="img/flower-distribute.png" height="600"/>