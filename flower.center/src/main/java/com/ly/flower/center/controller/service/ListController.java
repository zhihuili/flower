/**
 * 
 */
package com.ly.flower.center.controller.service;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ly.flower.web.spring.FlowerController;
import com.ly.train.flower.common.annotation.Flower;

/**
 * @author leeyazhou
 *
 */
@RestController
@RequestMapping("/service/")
@Flower(serviceName = "", value = "listservice")
public class ListController extends FlowerController {

  @Override
  @GetMapping("list")
  protected void doProcess(Object param, HttpServletRequest req) throws IOException {
  }



  @Override
  public void buildFlower() {
  }
}
