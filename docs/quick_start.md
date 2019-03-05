# Flower响应式编程Quick Start

### 依赖
```
git clone https://github.com/zhihuili/flower.git
mvn clean install

<dependency>
	<groupId>com.ly.train</groupId>
	<artifactId>flower.common</artifactId>
	<version>0.1.1</version>
</dependency>
```
### Flower框架主要元素
Flower框架的主要元素包括：Service（服务）、Message（消息）和Flow（流程）。Service实现一个细粒度的服务功能，Service之间通过Message关联，前一个Service的返回值（Message），必须是后一个Service的输入参数（Message），Service按照业务逻辑编辑成一个Flow（流程），Flower框架负责将前一个Service的返回消息，发送给后一个Service。
```
public class Service2 implements Service<Message2> {

  @Override
  public Object process(Message2 message) {
    return message.getAge() + 1;
  }

}
```
开发Service类必须实现Flower框架的Service接口，在process方法内完成服务业务逻辑。

### 服务编排构建服务流程
多个服务通过服务编排构成一个服务流程。
服务编排可以通过编程方式
```
    // serviceA -> serviceB -> serviceC
    ServiceFlow.buildFlow("sample", "serviceA", "serviceB");
    ServiceFlow.buildFlow("sample", "serviceB", "serviceC");
```

也可以直接编辑、生成流程文件，流程文件命名为*.flow，放在src/main/resources目录下，Flower框架会自动加载
```
// -> service1 -> service2 -> service5 -> service4
//      ^      |             ^              |
//      |       -> service3 -|              |
//      |___________________________________|

service1 -> service2
service1 -> service3
service2 -> service5
service3 -> service5
service5 -> service4
service4 -> service1
```


流程文件*.flow中根据service别名进行流程编排，service别名定义在src/main/resources下的*.services文件
```
service1 = com.ly.train.flower.common.sample.one.Service1
service2 = com.ly.train.flower.common.sample.one.Service2
service3 = com.ly.train.flower.common.sample.one.Service3
service4 = com.ly.train.flower.common.sample.one.Service4
service5 = com.ly.train.flower.common.service.JointService
```
### 调用流程

Flower提供了两种调用方式。

一种简单调用方式，将流程名，流程第一个服务名，消息直接通过ServiceFacade.asyncCallService()方法调用，这种情况下，Flower框架只创建一个消息处理流程通道，如果前面有消息在某个服务阻塞，将会导致后面的消息都排队等待。
```
ServiceFacade.asyncCallService("sample", "serviceA", " Hello World! ");
```
一种是带路由功能的调用方式，调用者创建服务路由器ServiceRouter并指定消息处理通道个数（服务流程实例数），然后通过ServiceRouter提交高并发的消息给流程实例处理。
```
ServiceRouter sr = ServiceFacade.buildServiceRouter("async", "serviceA", 400);//400个通道
sr.asyncCallService(message);
```

### Sample代码
调用者异步调用，编程式流程编排sample
```/flower.sample/src/main/java/com/ly/train/flower/common/sample/programflow/Sample.java```

调用者同步调用，可视化流程编排sample(sample.flow, sample.service在src/main/resources目录下)
```/flower.sample/src/main/java/com/ly/train/flower/common/sample/textflow/Sample.java```

集成Servlet3异步特性、Spring、Mybatis的Web应用sample
```
main启动入口：/flower.sample/src/main/java/com/ly/train/flower/common/sample/web/WebServer.java
request处理入口：/flower.sample/src/main/java/com/ly/train/flower/common/sample/web/async/AsyncServlet.java
mysql脚本：/flower.sample/src/main/resources/my.sql
请求URL：http://localhost:8080/async?id=1
```
