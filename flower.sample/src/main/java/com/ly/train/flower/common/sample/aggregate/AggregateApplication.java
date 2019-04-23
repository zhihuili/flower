/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.common.sample.aggregate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import com.ly.train.flower.web.spring.context.FlowerComponentScan;

import java.util.Map;

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
     * 关于端口配置：
     * application.yml 是spring-boot启动的配置，其优先级高于以下端口配置；
     * 如果需要在代码中自定义端口，请勿在application.xml文件中配置端口；
     */
    @Bean
    public JettyServletWebServerFactory servletContainer(){
        int port = 8080;
        Map<String, String> map = System.getenv();
        String envPort = map.get("PORT0");
        if(envPort != null && !envPort.isEmpty()){
            port = Integer.valueOf(envPort);
        }
        return new JettyServletWebServerFactory(port) ;
    }
}
