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
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.ly.train.flower.common.util.Constant;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

public class Web {
  private static final Logger logger = LoggerFactory.getLogger(Web.class);
  private AsyncContext asyncContext;
  private PrintWriter writer;
  private ServletRequest servletRequest;
  private ServletResponse servletResponse;

  public Web(AsyncContext context) {
    this.asyncContext = context;
    this.servletRequest = context.getRequest();
    this.servletResponse = context.getResponse();
    servletResponse.setCharacterEncoding(Constant.ENCODING_UTF_8);
    try {
      this.writer = servletResponse.getWriter();
    } catch (IOException e) {
      logger.error("", e);
    }
  }

  public void print(String message) throws IOException {
    writer.print(message);
  }

  public void printJSON(String json) throws IOException {
    servletResponse.setContentType(Constant.DEFAULT_CONTENT_JSON);
    print(json);
  }

  public void println(String message) throws IOException {
    writer.println(message);
  }

  public void flush() {
    writer.flush();
  }

  public void complete() {
    asyncContext.complete();
  }

  public String getParameter(String para) {
    return servletRequest.getParameter(para);
  }

  /**
   * Get the JSON data submitted by the post method
   * 
   * @return String / null
   * @throws IOException When an error occurs while reading the InputStream,
   *         IOException is thrown
   * @throws UnsupportedEncodingException Thrown when encountering an unresolved
   *         character encoding
   * @since JDK 1.7+
   */
  public String getPostJson() throws IOException {
    HttpServletRequest httpSr = (HttpServletRequest) servletRequest;
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
      charsetName = Constant.ENCODING_UTF_8;
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
