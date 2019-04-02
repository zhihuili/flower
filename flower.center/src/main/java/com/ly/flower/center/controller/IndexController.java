/**
 * 
 */
package com.ly.flower.center.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ly.flower.center.model.Response;

/**
 * @author leeyazhou
 *
 */
@RestController
public class IndexController extends BaseController {



  @GetMapping(value = {"/", "index.htm", "index.html"})
  public Response<String> index() {
    return ok("请求成功");
  }
}
