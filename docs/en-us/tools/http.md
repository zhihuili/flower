# http
flower.tools.http provides asynchronous https operations, including GET, POST, PUT and DELETE. It's based on httpasyncclient. 

## Installation 

```xml
<dependency>
    <groupId>com.ly.train</groupId>
    <artifactId>flower.tools.http</artifactId>
    <version>A.B.C</version>
</dependency>
```

## Examples 

Examples for GET, POST, PUT, PATCH and DELTE.

Two ways to create httpFactory:
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
    System.out.println("Result：" + r);
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
    System.out.println("Result：" + r);
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
    System.out.println("Result：" + r);
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
    System.out.println("Result：" + r);
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
    System.out.println("Result：" + r);
});
```

### HttpFactory

Provides 5 types of asynchronous operations: GET, POST, PUT, PATCH and DELETE


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

RequestContext contains all the information about the request

RequestContext是请求上下文对象，包含了一次请求需要的信息：

- connectionTime: timeout time for connecting to remote
- readTimeout: timeout time for read operation  
- writeTimeout: timeout time for write operation
- method: request method 
- url: request url 
- charset: encoding method of the request
- parameters: parameters of the request
- headers: headers of the request
- requestBody: body of the request in json format 

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