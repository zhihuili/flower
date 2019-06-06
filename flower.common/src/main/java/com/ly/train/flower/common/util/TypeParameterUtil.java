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
package com.ly.train.flower.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import com.ly.train.flower.common.service.Service;

/**
 * @author leeyazhou
 */
public class TypeParameterUtil {

  public static Pair<Class<?>, Class<?>> getServiceClassParam(Class<?> serviceClass) {
    Type[] paramTypes = null;
    Type[] types = serviceClass.getGenericInterfaces();
    if (types != null) {
      for (Type type : types) {
        if (type instanceof ParameterizedType) {
          Class<?> a = (Class<?>) ((ParameterizedType) type).getRawType();
          if (Service.class.isAssignableFrom(a)) {
            paramTypes = ((ParameterizedType) type).getActualTypeArguments();
            break;
          }
        }
      }
    }
    if (paramTypes == null) {
      Type type = serviceClass.getGenericSuperclass();
      if (type instanceof ParameterizedType) {
        paramTypes = ((ParameterizedType) type).getActualTypeArguments();
      }
    }

    Class<?> paramType = Object.class;
    Class<?> returnType = Object.class;
    if (paramTypes != null && paramTypes.length == 2) {
      if (paramTypes[0] instanceof ParameterizedType) {
        paramType = (Class<?>) ((ParameterizedType) paramTypes[0]).getRawType();
      } else {
        paramType = (Class<?>) paramTypes[0];
      }
      paramType = getClassByType(paramTypes[0]);
      returnType = getClassByType(paramTypes[1]);
      if (!Object.class.equals(returnType) && returnType.isAssignableFrom(CompletableFuture.class)) {
        returnType = getClassByType(((ParameterizedType) paramTypes[1]).getActualTypeArguments()[0]);
      }

    }
    return new Pair<Class<?>, Class<?>>(paramType, returnType);
  }

  public static Class<?> getClassByType(Type type) {
    Class<?> ret = null;
    if (type instanceof ParameterizedType) {
      ret = (Class<?>) ((ParameterizedType) type).getRawType();
    } else {
      ret = (Class<?>) type;
    }
    return ret;
  }
}
