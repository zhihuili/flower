/**
 * 
 */
package com.ly.flower.center.model;

/**
 * @author leeyazhou
 *
 */
public class Response<T> {

  private int code = 0;

  private String msg = "success";

  private T data;

  public Response() {
  }

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
