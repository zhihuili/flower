/**
 * 
 */
package com.ly.train.flower.springboot.flower;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ly.flower.web.spring.FlowerController;
import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.annotation.Flower;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.springboot.model.User;
import com.ly.train.flower.springboot.service.UserService;
import com.ly.train.flower.springboot.service.UserService2;

/**
 * @author leeyazhou
 *
 */
@Flower("flower")
@RequestMapping("/flower/")
@RestController
public class DemoFlower extends FlowerController {


  @RequestMapping("test")
  public void test(User user, HttpServletRequest req) throws IOException {
    AsyncContext context = req.startAsync();
    serviceRouter.asyncCallService(user, context);
  }

  @Override
  public ServiceRouter initServiceRouter() {
    return ServiceFacade.buildServiceRouter(getFlowName(), "UserService", 400);
  }

  @Override
  public void buildFlower() {
    ServiceFlow.buildFlow(getFlowName(), UserService.class, UserService2.class);
  }

}
