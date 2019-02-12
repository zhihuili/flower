package com.ly.train.flower.common.service.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;

public class Web {

  AsyncContext context;
  PrintWriter writer;
  ServletRequest sr;

  public Web(AsyncContext context) throws IOException {
    this.context = context;
    this.sr = context.getRequest();
    this.writer = context.getResponse().getWriter();

  }

  public void print(String s) throws IOException {
    writer.print(s);
  }

  public void println(String s) throws IOException {
    writer.println(s);
  }

  public void flush() {
    writer.flush();
  }

  public void complete() {
    context.complete();
  }

  public String getParameter(String para) {
    return sr.getParameter(para);
  }
}
