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
package com.ly.train.flower.sample.web;

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
