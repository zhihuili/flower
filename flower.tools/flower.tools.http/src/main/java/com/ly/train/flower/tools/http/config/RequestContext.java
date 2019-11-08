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
package com.ly.train.flower.tools.http.config;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.protocol.HTTP;
import com.ly.train.flower.tools.http.enums.RequestMethod;

/**
 * @author leeyazhou
 */
public class RequestContext implements Serializable {

  private static final long serialVersionUID = 1L;

  private int connectTimeout = 3000;

  private int readTimeout = 6000;

  private int writeTimeout = 3000;

  private RequestMethod method = RequestMethod.GET;
  private Charset charset = Charset.forName("UTF-8");

  private String url;

  private Map<String, String> parameters = new HashMap<String, String>();
  private Map<String, String> headers = new HashMap<>();
  private String requestBody;


  public RequestContext(String url) {
    this.url = url;
  }


  public int getConnectTimeout() {
    return connectTimeout;
  }

  public RequestContext setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
    return this;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public RequestContext setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
    return this;
  }

  public int getWriteTimeout() {
    return writeTimeout;
  }

  public RequestContext setWriteTimeout(int writeTimeout) {
    this.writeTimeout = writeTimeout;
    return this;
  }


  public RequestMethod getMethod() {
    return method;
  }

  public RequestContext setMethod(RequestMethod method) {
    this.method = method;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public RequestContext setUrl(String url) {
    this.url = url;
    return this;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public RequestContext setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
    return this;
  }

  public String getRequestBody() {
    return requestBody;
  }

  public RequestContext setRequestBody(String requestBody) {
    this.requestBody = requestBody;
    return this;
  }


  public Map<String, String> getHeaders() {
    return headers;
  }


  public RequestContext setHeaders(Map<String, String> headers) {
    this.headers = headers;
    return this;
  }

  public RequestContext addHeader(String headerName, String headerValue) {
    this.headers.put(headerName, headerValue);
    return this;
  }

  public RequestContext addParameter(String name, String value) {
    this.headers.put(name, value);
    return this;
  }

  public RequestContext setContentType(String contentValue) {
    this.headers.put(HTTP.CONTENT_TYPE, contentValue);
    return this;
  }
  
  public RequestContext setCharset(Charset charset) {
    this.charset = charset;
    return this;
  }
  
  public Charset getCharset() {
    return charset;
  }
  
}
