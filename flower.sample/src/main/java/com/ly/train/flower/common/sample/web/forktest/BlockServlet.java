package com.ly.train.flower.common.sample.web.forktest;

import com.ly.train.flower.common.actor.ServiceFacade;
import com.ly.train.flower.common.actor.ServiceRouter;
import com.ly.train.flower.common.service.FlowerService;
import com.ly.train.flower.common.service.ServiceFlow;
import com.ly.train.flower.common.service.containe.ServiceFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 13:13
 */
public class BlockServlet extends HttpServlet {
    static final long serialVersionUID = 1L;
    ServiceRouter sr;
    ApplicationContext context;

    @Override
    public void init() {
        context = new ClassPathXmlApplicationContext("spring-mybatis.xml");
        buildServiceEnv();
        sr = ServiceFacade.buildServiceRouter("block", "serviceBegin", 400);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        AsyncContext ctx = req.startAsync();

        asyncExe(ctx);
    }

    private void asyncExe(AsyncContext ctx) {
        try {
            sr.asyncCallService(null, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildServiceEnv() {
        ServiceFactory.registerService("serviceBegin",
                "com.ly.train.flower.common.sample.web.forktest.service.BeginService");
        ServiceFactory.registerService("serviceBLock",
                "com.ly.train.flower.common.sample.web.forktest.service.BlockService");
        ServiceFactory.registerService("serviceReturn",
                "com.ly.train.flower.common.sample.web.forktest.service.ReturnService");

        // 缺少该语句，会导致spring注入失败
        ServiceFactory.registerFlowerService("serviceBLock", (FlowerService) context.getBean("BlockService"));

        ServiceFlow.buildFlow("block", "serviceBegin", "serviceBLock");
        ServiceFlow.buildFlow("block", "serviceBLock", "serviceReturn");

    }
}
