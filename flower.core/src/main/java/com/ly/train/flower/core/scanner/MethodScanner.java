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
package com.ly.train.flower.core.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 类扫描接口
 * 
 * @author leeyazhou
 */
public interface MethodScanner {

  /**
   * 获取一个类上方法名符合正则的所有的方法
   * 
   * @param clazz clazz
   * @param methodPattern methodPattern
   * @return List
   */
  List<Method> getMethodList(Class<?> clazz, String methodPattern);

  /**
   * 获取一个类上有期望Annotation的所有方法
   * 
   * @param clazz clazz
   * @param annotationClass annotationClass
   * @return List
   */
  List<Method> getMethodListByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass);

  /**
   * 获取一个类及接口类上有期望Annotation的所有方法
   * 
   * @param clazz clazz
   * @param annotationClass annotationClass
   * @return {@link Method }
   */
  List<Method> getMethodListByAnnotationInterface(Class<?> clazz, Class<? extends Annotation> annotationClass);
}
