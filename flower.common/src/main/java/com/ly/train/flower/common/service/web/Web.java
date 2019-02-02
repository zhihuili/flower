package com.ly.train.flower.common.service.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.AsyncContext;

public class Web {

  AsyncContext context;
  PrintWriter writer;

  public Web(AsyncContext context) throws IOException {
    this.context = context;
    this.writer = context.getResponse().getWriter();
    
  }

  public void print(String s) throws IOException {
    writer.print(s);
  }

  public void println(String s) throws IOException {
    PrintWriter out = context.getResponse().getWriter();
    out.println("业务处理完毕的时间：" + new Date() + ".");
    out.flush();
    writer.println(s);
  }

  public void flush() {
    writer.flush();
  }

  public void complete() {
    context.complete();
  }
}
