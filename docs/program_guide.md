# Flower应用指南

**在Flower里面消息是一等公民，基于Flower开发的应用系统是面向消息的应用系统。**
消息由Service产生，是Service的返回值；同时消息也是Service的输入。前一个Service的返回消息是下一个Service的输入消息，没有耦合的Service正是通过消息关联起来，组成一个Service流程，并最终构建出一个拥有完整处理能力的应用系统。流程举例：
```
// -> service1 -> service2 -> service5 -> service4
//      ^      |             ^              |
//      |       -> service3 -|              |
//      |___________________________________|
```
## 术语
* 服务：即Service，Flower里完成业务逻辑的最小单位，服务的粒度可以很大，比如完成整个http请求处理计算可以封装在一个服务里；服务的粒度也可以很小，比如仅仅完成一个邮件格式验证。服务处于流程之中，因此有前后顺序。
   * 前序服务，在流程中处于当前讨论的服务的前一个服务，上面例子中，service2是service5的前序服务。
   * 后继服务，在流程中处于当前讨论的服务的后一个服务，上面例子中，service5是service2的后继服务。
   * 框架内置服务，由Flower框架内置提供的服务，通常用来实现某种消息模式。
* 消息：消息是服务的输入和输出，消息经过流程中的一个又一个服务的处理，完成系统的功能。
* 流程：通过流程定义，将多个服务联系起来，构成这个系统的业务处理流程，Flower应用中，流程及服务边界的设计是重要的设计目标。
   * 流程通道：定义好的流程，会被Flower框架加载，构成流程实例通道，简称流程通道，一个流程通道，就是一个消息的处理流通道。一个流程定义可以被加载创建多个流程通道。如果流程入口消息是并发发送过来的，比如典型的web应用入口，那么就应该创建多个流程通道。
   * 流程通道路由器：如果有多个流程通道，在流程入口处，使用流程通道路由器进行消息选择，由路由器将消息发送给不同的流程通道去处理。
   

## Flower消息处理模式
消息除了将服务串联起来，构成一个简单的串行流程，还可以组合应用，产生更强大的功能。

### 消息分叉
消息分叉是指，一个服务输出的消息，可能产生分叉，分发给1个或者多个其他服务。消息分叉后有两种处理方式，全部分发和条件分发。

#### 全部分发
将输出消息分发给全部流程后续服务。后续多个服务接受到消息后，并行执行。这种模式多用于可并行执行的多个子任务，比如用户注册成功后，需要1、将用户数据写入数据库，2、给用户发送激活邮件，3、给用户发送通知短信，4、将新用户注册信息发送给关联产品，实现账户打通。上述4个服务就可以采用消息全部分发模式，接受用户注册消息，并发完成上述4个任务。

要实现消息全部分发，需要在流程中进行配置，所有需要接受前序服务的输出消息的服务都要配置在流程中，如
```
service1 -> service2
service1 -> service3
```
service1是前序服务，service2和service3是后继服务。
如果service2和service3的class定义中，实现Service<T>接口的声明中指定了泛型<T>，则泛型类型必须是service1的输出类型或者其父类。

```
public class Service1 implements Service {

  @Override
  public Object process(Object message) {
    return new Message2();
  }
}

// Service2声明类型为Service1的输出类型
public class Service2 implements Service<Message2> {

  @Override
  public Object process(Message2 message) {
    return message.getAge() + 1;
  }
}

// Service3声明类型为Service1的输出类型
public class Service3 implements Service<Message2> {

  @Override
  public Object process(Message2 message) {
    return message.getName().toUpperCase();
  }
}
```

#### 条件分发
有时候，前一个服务产生的消息，根据消息内容和业务逻辑可能会交给后续的某一个服务处理，而不是全部服务处理。比如用户贷款申请，当前服务计算出用户信用等级后，需要根据信用等级判断采用何种贷款方式，或者是拒绝贷款，不同贷款方式和拒绝贷款是不同的服务，这些服务在流程配置的时候，都需要配置为前序服务的后继服务，但是在运行期根据条件决定将消息分发给具体哪个后继服务。

实现条件分发在流程配置上和全部分发一样，所有可能的后继服务都要配置在流程中。具体实现条件分发有如下三种方式。

##### 根据泛型进行分发
后续服务实现接口的时候声明不同的泛型类型，前序服务根据业务逻辑构建不同的消息类型，Flower会根据消息类型匹配对应的服务，只有成功匹配，消息才发送给过去。比如：
```
//构建流程，ServiceB和ServiceC为ServiceA的后续流程
    ServiceFlow.buildFlow("sample", "serviceA", "serviceB");
    ServiceFlow.buildFlow("sample", "serviceA", "serviceC");

//声明ServiceB接受的消息类型为MessageB
public class ServiceB implements Service<MessageB> {

  @Override
  public Object process(MessageB message) {
    System.out.println("I am Service B.");
    return null;
  }
}

//声明ServiceC接受的消息类型为MessageC
public class ServiceC implements Service<MessageC> {

  @Override
  public Object process(MessageC message) {
    System.out.println("I am Service C.");
    MessageX mx = new MessageX();
    mx.setCondition("serviceE");
    return mx;
  }
}

public class ServiceA implements Service<String> {

  @Override
  public Object process(String message) {
    if ("b".equals(message)) {
      return new MessageB();
    }
    if ("c".equals(message)) {
      return new MessageC();
    }
    return null;
  }
}
```
ServiceB是ServiceA的后续服务，ServiceA收到的消息如果是字符串“b”，就会返回消息类型B，这时候框架就会将消息发送给ServiceB，而不会发送给ServiceC。

##### 在消息中指定后继服务的id进行分发
前序消息实现Condition接口，并指定后继服务的id，如：
```
//serviceE和serviceD是serviceC的后继服务
ServiceFlow.buildFlow("sample", "serviceC", "serviceD");
ServiceFlow.buildFlow("sample", "serviceC", "serviceE");

//消息实现Condition接口
public class MessageX implements Condition {

  private Object condition;

  public void setCondition(Object src) {
    this.condition = src;
  }

  @Override
  public Object getCondition() {
    return condition;
  }

}

//在serviceC的返回消息中设定要分发的后续服务id
public class ServiceC implements Service<MessageC> {

  @Override
  public Object process(MessageC message) {
    System.out.println("I am Service C.");
    MessageX mx = new MessageX();
    //设定将消息分发给后续服务serviceE
    mx.setCondition("serviceE");
    return mx;
  }
}
```
一般说来，服务是可复用的，可复用于不同的流程中，但是在不同的流程中后继服务可能是不同的，后继服务的id也是不同的，在服务中写死后续服务id，显然不利于服务的复用。解决方案有两种，一种是在不同的流程中，写一个专门用于分发的服务，也就是处理业务逻辑的服务并不关心消息的分发，只管返回消息内容，但是其后继服务是一个专门用来做消息分发的服务，这个服务没有业务逻辑，仅仅实现Condition接口根据消息内容指定后继服务。

另一种是使用框架内置服务ConditionService进行消息分发

##### 使用框架内置服务ConditionService进行消息分发
ConditionService是一个通用的消息分发服务，
```
    ServiceFlow.buildFlow("sample", "serviceE", "serviceCondition");
    ServiceFlow.buildFlow("sample", "serviceCondition", "serviceF");
    ServiceFlow.buildFlow("sample", "serviceCondition", "serviceG");
```
服务serviceE要将消息根据条件分发给serviceF或者serviceG，流程配置如上，中间加入serviceCondition进行适配。
serviceCondition的服务注册方法为
```
    ServiceFactory.registerService("serviceCondition",
        "com.ly.train.flower.common.service.ConditionService;serviceF,serviceG");
```
com.ly.train.flower.common.service.ConditionService为框架内置服务

```
public class ServiceE implements Service {

  @Override
  public Object process(Object message) {
    System.out.println("I am Service E.");
    MessageX x = new MessageX();
    x.setCondition(1);
    return x;
  }

}
```
这种方式中，依然需要在serviceCondition的前驱服务serviceE中设置返回消息的condition，但是不必设置后续服务的id，只需要设置后续服务的顺序号即可。

几种条件分发的代码示例参考/flower.sample/src/main/java/com/ly/train/flower/common/sample/condition/Sample.java

### 消息聚合
对于全部分发的消息分叉而言，通常目的在于使多个服务能够并行执行，加快处理速度。通常还需要得到这些并行处理的服务的全部结果，进行后续处理。
在Flower中，得到多个并行处理服务的结果消息，称为消息聚合。实现方式为，在流程中，配置需要聚合的多个消息的后续服务为`com.ly.train.flower.common.service.AggregateService`，这是一个框架内置服务，负责聚合多个并行服务产生的消息，将其封装到一个Set对象中返回。
如流程
```
service2 -> service5
service3 -> service5
service5 -> service4
```
这里的service5就是一个消息聚合服务，负责聚合并行的service2和service3产生的消息，并把聚合后的Set消息发送给service4.
服务配置如下，service5配置为框架内置服务AggregateService。
```
service2 = com.ly.train.flower.common.sample.textflow.Service2
service3 = com.ly.train.flower.common.sample.textflow.Service3
service4 = com.ly.train.flower.common.sample.textflow.Service4
service5 = com.ly.train.flower.common.service.AggregateService
```
service4负责接收处理聚合后的消息，从Set中取出各个消息，分别处理。
```
public class Service4 implements Service<Set> {

  @Override
  public Object process(Set message) {
    Message2 m = new Message2();
    for (Object o : message) {
      if (o instanceof Integer) {
        m.setAge((Integer) o);
      }
      if (o instanceof String) {
        m.setName(String.valueOf(o));
      }
    }
    Message3 m3 = new Message3();
    m3.setM2(m);
    return m3;
  }
```

### 消息回复
Flower中的消息全部都是异步处理，也就是服务之间不会互相阻塞等待，以实现**低耦合、无阻塞、高并发的响应式系统**。Flower流程调用者发送出请求消息以后，消息在流程中处理，调用者无需阻塞等待处理结果，可以继续去执行其他的计算任务。

和传统的命令式编程不同，通常流程的发起调用者并不是流程处理结果的最终接受者，比如对于web开发，流程的发起者通常是一个servlet，但是真正接受处理结果的是用户端浏览器或者App，流程中的服务可以直接发送处理结果给用户端，而不必通过servlet。也就是调用发起者servlet无需等待流程服务的最终处理结果，将用户请求发送到流程中后，不必阻塞等待处理，可以立即获取另一个用户的请求继续进行处理。

但是Flower也支持调用者阻塞等待消息处理结果，消息回复模式可以使流程调用者得到流程处理的最终结果消息。可参考代码示例
/flower.sample/src/main/java/com/ly/train/flower/common/sample/textflow/Sample.java

## Flower web开发模式

### Flower集成Servlet3的web开发模式
Flower支持Servlet3的异步模式，请求处理线程在调用Flower流程，并传入AsyncContext对象后立即释放。
代码示例参考/flower.sample/src/main/java/com/ly/train/flower/common/sample/web/async/AsyncServlet.java

开发支持Servlet3的Flower服务，需要实现框架的Service接口，在方法 `Object process(T message, ServiceContext context) throws Exception;`中，Flower框架会传入一个Web对象，通过`context.getWeb()`得到Web对象，用以获得请求参数和输出处理响应结果。

### Flower集成Spring boot的web开发模式
Flower支持Spring boot开发，在Spring boot项目依赖flower.web，实现框架中的Service接口和InitController接口。
初始化@BindController注解需要的参数，在编译过程中自动由flower.web枚举@BindController注解, 生成Spring boot需要的Controller。

代码示例参考/flower.sample/src/main/java/com/ly/train/flower/common/sample/springboot

@BindController path参数: http请求的url路径
@BindController method参数: http发起的请求方式（GET， POST）
@BindController paramClass参数: http发送的参数反序列化的对象类型

@BindController 不指定paramClass，GET和POST方式都需要在process中使用context.getWeb()获取AsyncContext, 自行获取参数

@BindController method=GET paramClass 指定了类，需要实现Service<paramClass>接口，process的第一个参数返回paramClass对象

@BindController method=POST paramClass 指定了类, 没有继承PostJson接口，需要实现Service<paramClass>接口，process的第一个参数返回paramClass对象
http请求的参数:name=aaa&id=2222, header需要Content-Type: application/x-www-form-urlencoded

@BindController method=POST paramClass 指定了类, 继承PostJson接口，需要实现Service<paramClass>接口，process的第一个参数返回paramClass对象
http请求的参数:{"name":"aaa","id":111}, header需要Content-Type: Content-Type: application/json;charset=UTF-8

## 使用Flower框架的开发建议
* 进行流程设计。服务边界，服务流程，消息类型和数据，在系统设计阶段充分考虑，流程设计好了，系统架构也就设计好了。
* 进行消息设计。在Flower里，消息也是服务之间的的接口，设计好消息，对服务的输入和输出约束就有了，团队开发时，就可以基于消息接口，各自开发自己的Service，只要严格遵循消息规范，不同开发者在开发的时候不需要彼此依赖，可以提高并行开发速度。而且只要各自做好自己的单元测试，集成测试的时候问题和工作量会少很多。
* 尽量提高可并行的服务数量，特别是不同资源消耗性的服务的并行；比如CPU密集型的、内存消耗型的、IO密集型的任务并行执行，可以极大提供系统的资源利用率。
* 如无非常必要，请勿阻塞等待，阻塞等待将会挂起Flower底层的akka线程，也会占用Flower的流程通道。
