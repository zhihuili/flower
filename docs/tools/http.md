# http

flower.tools.http项目主要提供了异步操作，支持GET、POST、PUT、DELETE等请求方式，基础能力依赖库是httpasyncclient
和okhttp。

## 安装

```xml
<dependency>
    <groupId>com.ly.train</groupId>
    <artifactId>flower.tools.http</artifactId>
    <version>A.B.C</version>
</dependency>
```

## 示例

展示GET、POST、PUT、PATCH和DELETE的示例方式。

获取两种HttpFactory实现的方式：
```java
HttpFactory httpFactory = HttpFactory.httpAsyncClientFactory;
```

```java
HttpFactory httpFactory = HttpFactory.okHttpFactory;
```

### GET

```java
RequestContext requestContext = new RequestContext(baseUrl + "demo/get");
CompletableFuture<String> result = httpFactory.get(requestContext);
requestContext.addHeader("Connect", "keep-alive");
result.exceptionally(ex -> {
    ex.printStackTrace();
    return null;
}).thenAccept(r -> {
    System.out.println("结果：" + r);
});
```

### POST 

```java
RequestContext requestContext = new RequestContext(baseUrl + "demo/post");
JSONObject body = new JSONObject();
body.put("name", "liyazhou");
body.put("age", 201);
requestContext.setRequestBody(body.toJSONString());
CompletableFuture<String> result = httpFactory.post(requestContext);
result.exceptionally(ex -> {
    ex.printStackTrace();
    return null;
}).thenAccept(r -> {
    System.out.println("结果：" + r);
});
```

### PUT

```java
RequestContext requestContext = new RequestContext(baseUrl + "demo/put");
JSONObject body = new JSONObject();
body.put("name", "liyazhou");
body.put("age", 201);
requestContext.setRequestBody(body.toJSONString());
CompletableFuture<String> result = httpFactory.put(requestContext);
result.exceptionally(ex -> {
    ex.printStackTrace();
    return null;
}).thenAccept(r -> {
    System.out.println("结果：" + r);
});
```

### PATCH

```java
RequestContext requestContext = new RequestContext(baseUrl + "demo/patch");
JSONObject body = new JSONObject();
body.put("name", "liyazhou");
body.put("age", 201);
requestContext.setRequestBody(body.toJSONString());
CompletableFuture<String> result = httpFactory.patch(requestContext);
result.exceptionally(ex -> {
    ex.printStackTrace();
    return null;
}).thenAccept(r -> {
    System.out.println("结果：" + r);
});
```

### DELETE

```java
RequestContext requestContext = new RequestContext(baseUrl + "demo/delete");
JSONObject body = new JSONObject();
body.put("name", "liyazhou");
body.put("age", 201);
requestContext.setRequestBody(body.toJSONString());
CompletableFuture<String> result = httpFactory.delete(requestContext);
result.exceptionally(ex -> {
    ex.printStackTrace();
    return null;
}).thenAccept(r -> {
    System.out.println("结果：" + r);
});
```

## 源码解读

解读flower.tools.http核心接口代码，其他核心数据交换功能有依赖库提供，对应的源码解读暂不做解析。

### HttpFactory

主要提供五种异步请求方式：GET、POST、PUT、PATCH和DELETE：

```java
public interface HttpFactory {

  CompletableFuture<String> get(RequestContext requestContext);

  CompletableFuture<String> post(RequestContext requestContext);

  CompletableFuture<String> put(RequestContext requestContext);

  CompletableFuture<String> patch(RequestContext requestContext);

  CompletableFuture<String> delete(RequestContext requestContext);
}
```

### RequestContext

RequestContext是请求上下文对象，包含了一次请求需要的信息：

- connectionTime: 远程连接超时时间
- readTimeout: 读取数据超时时间
- writeTimeout: 写入超时时间
- method: 请求方法
- url: 请求地址
- charset: 数据字符编码
- parameters: 请求参数数据
- headers: 请求头信息
- requestBody: 请求体数据，例如请求体数据是json格式数据

```java
package com.ly.train.flower.tools.http.config;
import org.apache.http.protocol.HTTP;
import com.ly.train.flower.tools.http.enums.RequestMethod;

/**
 * @author leeyazhou
 */
public class RequestContext implements Serializable {
  private int connectTimeout = 3000;
  private int readTimeout = 6000;
  private int writeTimeout = 3000;
  private RequestMethod method = RequestMethod.GET;
  private Charset charset = Charset.forName("UTF-8");
  private String url;
  private Map<String, String> parameters = new HashMap<String, String>();
  private Map<String, String> headers = new HashMap<>();
  private String requestBody;
  // setter、getter
}
```