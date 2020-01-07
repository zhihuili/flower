# 服务异常处理

Flower作为一个应用框架，需要有一个全局的异常管理机制，当服务流程调用过程中出现异常的时候，应用需要能够捕捉到异常信息，并根据应用自身的情况进行酌情处理。
那么

## ExceptionHandlerManager

Flower框架使用ExceptionHandlerManager管理ExceptionHandler，通过registerHandler为指定异常类型设置异常处理器，
通过setDefaultExceptionHandler设置默认异常处理器。

```java
    void registerHandler(Class<?> exceptionClass, ExceptionHandler exceptionHandler);
    
    void setDefaultExceptionHandler(ExceptionHandler defaultExceptionHandler);
```

## ExceptionHandler

Flower的异常处理器接口是*com.ly.train.flower.common.exception.handler.ExceptionHandler*，默认使用DefaultExceptionHandler进行异常处理，
默认情况下使用日志框架记录异常信息。

```java
public interface ExceptionHandler {

  /**
   * 处理异常
   * 
   * @param context 上下文
   * @param throwable 异常信息
   */
  void handle(ServiceContext context, Throwable throwable);

}
```

参数：
* **ServiceContext**    服务流程上下文信息
* **Throwable** 异常堆栈信息

## 示例

通过FlowerFactory可以设置指定异常的异常处理器，也可以设置默认异常处理器，默认异常处理器是指发生的异常，没有匹配的异常处理器时，使用默认的异常处理器进行处理。

```java
    FlowerFactory flowerFactory = new SimpleFlowerFactory();
    ExceptionHandler exceptionHandler = new DefaultExceptionHandler();
    flowerFactory.registerExceptionHandler(ServiceNotFoundException.class, exceptionHandler);
    flowerFactory.setDefaultExceptionHandler(exceptionHandler);
```