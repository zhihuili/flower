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
/**
 * 
 */
package com.ly.train.flower.sample.multi.springboot.util;

/**
 * @author leeyazhou
 * 
 */
public class Response<T> {

  private int code = 0;

  private String msg = "success";

  private T data;

  public Response() {}

  public Response(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public Response(T data) {
    this.data = data;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Response [code=");
    builder.append(code);
    builder.append(", msg=");
    builder.append(msg);
    builder.append(", data=");
    builder.append(data);
    builder.append("]");
    return builder.toString();
  }



}
