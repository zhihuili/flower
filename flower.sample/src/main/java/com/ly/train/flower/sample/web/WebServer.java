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


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.sample.web.async.AsyncServlet;
import com.ly.train.flower.sample.web.forktest.BlockServlet;
import com.ly.train.flower.sample.web.forktest.ForkServlet;
import com.ly.train.flower.sample.web.sync.SyncServlet;
import ch.qos.logback.classic.Level;


public class WebServer {

  public static class NoLogging implements Logger {
    @Override
    public String getName() {
      return "no";
    }

    @Override
    public void warn(String msg, Object... args) {}

    @Override
    public void warn(Throwable thrown) {}

    @Override
    public void warn(String msg, Throwable thrown) {}

    @Override
    public void info(String msg, Object... args) {}

    @Override
    public void info(Throwable thrown) {}

    @Override
    public void info(String msg, Throwable thrown) {}

    @Override
    public boolean isDebugEnabled() {
      return false;
    }

    @Override
    public void setDebugEnabled(boolean enabled) {}

    @Override
    public void debug(String msg, Object... args) {}

    @Override
    public void debug(String s, long l) {}

    @Override
    public void debug(Throwable thrown) {}

    @Override
    public void debug(String msg, Throwable thrown) {}

    @Override
    public Logger getLogger(String name) {
      return this;
    }

    @Override
    public void ignore(Throwable ignored) {}
  }

  public static void main(String[] args) throws Exception {
    org.apache.ibatis.logging.LogFactory.useSlf4jLogging();
    ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.ERROR);
    org.eclipse.jetty.util.log.Log.setLog(new NoLogging());

    Server server = new Server(8080);
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);
    context.addServlet(new ServletHolder(new FlowServlet()), "/flow");
    context.addServlet(new ServletHolder(new SyncServlet()), "/sync");
    context.addServlet(new ServletHolder(new AsyncServlet()), "/async");
    context.addServlet(new ServletHolder(new ForkServlet()), "/test/fork");
    context.addServlet(new ServletHolder(new BlockServlet()), "/test/block");

    server.start();
    server.join();
  }
}
