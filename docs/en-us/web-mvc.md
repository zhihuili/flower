# web-mvc
Flower support web development based on servlet 3.1+ asynchronous pattern. It coordinate asynchronous requests using AsyncContext object. 

## spring-mvc

### Installation 

```xml
 <dependency>
    <groupId>com.ly.train</groupId>
    <artifactId>flower.web</artifactId>
    <version>A.B.C</version>
</dependency>
```

### API Gateway
You can define an API gateway using IndexController. Just extend FlowerController and implement buildFlower() method, which will build service flow. 

Use @Flower annotation to define the flow name and flowNumber.

The entry point for gateway is index (configurable), which takes HttpServletRequest as input and invoke doProcess() method from parent class.

IndexController.java
```java
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.sample.springboot.service.ServiceA;
import com.ly.train.flower.sample.springboot.service.ServiceB;
import com.ly.train.flower.web.spring.FlowerController;

@RestController
@Flower(value = "async-index", flowNumber = 128)
public class IndexController extends FlowerController {

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public void index(Object param, HttpServletRequest req) throws Throwable {
		doProcess(param, req);
	}

	@Override
	public void buildFlower() {
		getServiceFlow().buildFlow(ServiceA.class, ServiceB.class);
	}
}
```

## Servlet 

The following is an example for using Flower with servlet. Servlet will create a service flow and process Get request asynchronous.

### Installation 

```xml
 <dependency>
    <groupId>com.ly.train</groupId>
    <artifactId>flower.web</artifactId>
    <version>A.B.C</version>
</dependency>
```

### Create a servlet

FlowService.java
```java
package com.ly.train.flower.sample.web;

import com.ly.train.flower.common.core.service.Service;
import com.ly.train.flower.common.core.service.ServiceContext;
import com.ly.train.flower.core.service.Complete;
import com.ly.train.flower.core.service.web.Flush;
import com.ly.train.flower.core.service.web.HttpComplete;

public class FlowService implements Service<Object, Object>, HttpComplete, Flush, Complete {

	@Override
	public Object process(Object message, ServiceContext context) throws Exception {
		context.getWeb().println(" - end:" + System.currentTimeMillis());
		return message;
	}

}

```

FlowerHttpServlet.java
```java
import javax.servlet.http.HttpServlet;
import com.ly.train.flower.core.akka.ServiceFacade;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.core.service.container.ServiceFactory;
import com.ly.train.flower.core.service.container.ServiceFlow;
import com.ly.train.flower.core.service.container.simple.SimpleFlowerFactory;

/**
 * @author leeyazhou
 */
public class FlowerHttpServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  protected static FlowerFactory flowerFactory = null;
  protected static ServiceFactory serviceFactory;
  protected static ServiceFacade serviceFacade;

  public FlowerHttpServlet() {
    if (flowerFactory == null) {
      synchronized (FlowerHttpServlet.class) {
        if (flowerFactory == null) {
          flowerFactory = new SimpleFlowerFactory();
          serviceFactory = flowerFactory.getServiceFactory();
          serviceFacade = flowerFactory.getServiceFacade();
        }
      }
    }
  }

  public ServiceFacade getServiceFacade() {
    return flowerFactory.getServiceFacade();
  }

  public ServiceFlow getServiceFlow(String flowName) {
    return serviceFactory.getOrCreateServiceFlow(flowName);
  }
}
```

FlowServlet.java
```java
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ly.train.flower.core.akka.router.FlowRouter;
import com.ly.train.flower.core.service.impl.NothingService;

public class FlowServlet extends FlowerHttpServlet {
	private static final long serialVersionUID = 1L;
	private FlowRouter sr;
	private final String flowName = "flower-service-flow";

	@Override
	public void init() {
		flowerFactory.getServiceFactory().registerService("flowService", FlowService.class);
		flowerFactory.getServiceFactory().registerService("endService", NothingService.class);
		getServiceFlow(flowName).buildFlow("flowService", "endService").build();
		this.sr = flowerFactory.getServiceFacade().buildFlowRouter(flowName, 400);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.println("begin：" + System.currentTimeMillis());
		out.flush();

		AsyncContext ctx = req.startAsync();
		sr.asyncCallService(" Hello, Flow World! ", ctx);
	}

}
```
