package com.ly.train.flower.common.sample.aggregate;

import com.ly.flower.web.spring.context.FlowerComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;

/**
 * 本示例兼容2019-4-10日master提交
 * @author: fengyu.zhang
 */
@SpringBootApplication
@FlowerComponentScan("com.ly.train.flower.common.sample.aggregate")
public class AggregateApplication {
    public static void main(String[] args){
        SpringApplication.run(AggregateApplication.class,args);
    }

    /**
     * 使用jetty运行，不写该方法则默认使用tomcat运行
     */
    @Bean
    public JettyServletWebServerFactory servletContainer(){
        int port = 8081;
        return new JettyServletWebServerFactory(port) ;
    }
}
