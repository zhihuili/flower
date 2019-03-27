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
package com.ly.train.flower.common.sample.web.forktest;

import com.alibaba.fastjson.JSONObject;
import com.ly.train.flower.common.sample.web.forktest.service.BlockService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author fengyu.zhang
 * @date 2019/2/24 13:13
 */
public class BlockServlet extends HttpServlet {
    ApplicationContext context;

    @Override
    public void init() {
        context = new ClassPathXmlApplicationContext("spring-mybatis.xml");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        int id = Integer.valueOf(req.getParameter("id"));
        BlockService blockService = (BlockService) context.getBean("BlockService");
        String result = JSONObject.toJSONString(blockService.getInfo(id));
        PrintWriter out = resp.getWriter();
        out.println(result);
        out.flush();
    }
}
