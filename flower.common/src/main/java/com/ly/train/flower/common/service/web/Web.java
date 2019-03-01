package com.ly.train.flower.common.service.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

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

  /**
   * Get the JSON data submitted by the post method
   * @return String / null
   */
  public String getJsonString() throws IOException{
    HttpServletRequest httpSr = (HttpServletRequest)sr;
    if(!httpSr.getMethod().equalsIgnoreCase("POST") || null == httpSr.getContentType()){
      return null;
    }
    if(!httpSr.getContentType().toLowerCase().contains("application/json")){
      return null;
    }
    InputStream is = httpSr.getInputStream();
    byte[] b = new byte[httpSr.getContentLength()];
    int len = is.read(b,0,httpSr.getContentLength());
    is.close();
    return len <= 0 ? null:new String(b,httpSr.getCharacterEncoding());
  }
}
