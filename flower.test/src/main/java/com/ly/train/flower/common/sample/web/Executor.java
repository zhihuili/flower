package com.ly.train.flower.common.sample.web;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.AsyncContext;

public class Executor implements Runnable {
  private AsyncContext ctx = null;

  public Executor(AsyncContext ctx) {
    this.ctx = ctx;
  }

  public void run() {
    try {
//      Thread.sleep(100);
      PrintWriter out = ctx.getResponse().getWriter();
      out.println("- end：" + System.currentTimeMillis());
      out.flush();
      ctx.complete();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
