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
package com.ly.train.flower.common.service.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
   * 
   * @return String / null
   * @throws IOException When an error occurs while reading the InputStream, IOException is thrown
   * @throws UnsupportedEncodingException Thrown when encountering an unresolved character encoding
   * @since JDK 1.7+
   */
  public String getPostJson() throws IOException {
    HttpServletRequest httpSr = (HttpServletRequest) sr;
    if (!httpSr.getMethod().equalsIgnoreCase("POST") || null == httpSr.getContentType()) {
      return null;
    }
    if (!httpSr.getContentType().toLowerCase().contains("application/json")) {
      return null;
    }
    if (httpSr.getContentLength() <= 0) {
      return null;
    }
    String charsetName = httpSr.getCharacterEncoding();
    if (null == charsetName) {
      charsetName = "UTF-8";
    }
    byte[] b = new byte[httpSr.getContentLength()];
    try (InputStream is = httpSr.getInputStream()) {
      int len = is.read(b, 0, httpSr.getContentLength());
      return len <= 0 ? null : new String(b, charsetName);
    } catch (UnsupportedEncodingException uee) {
      throw new UnsupportedEncodingException("UnsupportedEncoding");
    }
  }
}
