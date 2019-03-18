/**
 * 
 */
package com.ly.train.flower.common.service.container;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 * @author leeyazhou
 *
 */
public class FlowerMeta implements Serializable {

  private static final long serialVersionUID = 7142991778425415404L;

  public FlowerMeta() {}


  private Method method;
  private Class<?> resultType;
  private List<Class<?>> paramType;

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public Class<?> getResultType() {
    return resultType;
  }

  public void setResultType(Class<?> resultType) {
    this.resultType = resultType;
  }

  public List<Class<?>> getParamType() {
    return paramType;
  }

  public void setParamType(List<Class<?>> paramType) {
    this.paramType = paramType;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FlowerMeta [method=");
    builder.append(method);
    builder.append(", resultType=");
    builder.append(resultType);
    builder.append(", paramType=");
    builder.append(paramType);
    builder.append("]");
    return builder.toString();
  }



}
