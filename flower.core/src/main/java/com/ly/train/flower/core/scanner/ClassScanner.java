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
import java.util.Set;

/**
 * 类扫描接口
 * 
 * @author leeyazhou
 */
public interface ClassScanner {

  /**
   * 获取指定包名中的所有类
   * 
   * @param packageName packageName
   * @param packagePattern packagePattern
   * @return Set
   */
  Set<Class<?>> getClassList(String packageName, String packagePattern);

  /**
   * 自定义ClassLoader中获取指定包名中的所有类
   * 
   * @param packageName packageName
   * @param packagePattern packagePattern
   * @param classLoader classLoader
   * @return Set
   */
  Set<Class<?>> getClassList(String packageName, String packagePattern, ClassLoader classLoader);

  /**
   * 获取指定包名中指定注解的相关类
   * 
   * @param packageName packageName
   * @param annotationClass annotationClass
   * @return Set
   */
  Set<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass);

  /**
   * 自定义ClassLoader中获取指定包名中指定注解的相关类
   * 
   * @param packageName packageName
   * @param annotationClass annotationClass
   * @param classLoader classLoader
   * @return Set
   */
  Set<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass,
      ClassLoader classLoader);

  /**
   * 获取指定包名中指定父类或接口的相关类
   * 
   * @param packageName packageName
   * @param superClass superClass
   * @return Set
   */
  Set<Class<?>> getClassListBySuper(String packageName, Class<?> superClass);

  /**
   * 自定义ClassLoader中获取指定包名中指定父类或接口的相关类
   * 
   * @param packageName packageName
   * @param superClass superClass
   * @param classLoader classLoader
   * @return Set
   */
  Set<Class<?>> getClassListBySuper(String packageName, Class<?> superClass, ClassLoader classLoader);
}
