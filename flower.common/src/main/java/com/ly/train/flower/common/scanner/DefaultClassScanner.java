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
package com.ly.train.flower.common.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import com.ly.train.flower.common.scanner.filter.AbstractAnnotationClassFilter;
import com.ly.train.flower.common.scanner.filter.AbstractClassFilter;
import com.ly.train.flower.common.scanner.filter.AbstractSupperClassFilter;

public class DefaultClassScanner implements ClassScanner {
  private static final DefaultClassScanner Instance = new DefaultClassScanner();

  private DefaultClassScanner() {}

  public static DefaultClassScanner getInstance() {
    return Instance;
  }

  @Override
  public Set<Class<?>> getClassList(final String packageName, final String pattern) {
    return new AbstractClassFilter(packageName) {
      @Override
      public boolean filterCondition(Class<?> cls) {
        String className = cls.getName();
        String patternStr = (null == pattern || pattern.isEmpty()) ? ".*" : pattern;
        return className.startsWith(packageName) && className.matches(patternStr);

      }
    }.getClassList();
  }

  @Override
  public Set<Class<?>> getClassList(final String packageName, final String pattern, ClassLoader classLoader) {
    return new AbstractClassFilter(packageName, classLoader) {
      @Override
      public boolean filterCondition(Class<?> cls) {
        String className = cls.getName();
        String pkgName = className.substring(0, className.lastIndexOf("."));
        String patternStr = (null == pattern || pattern.isEmpty()) ? ".*" : pattern;
        return pkgName.startsWith(packageName) && pkgName.matches(patternStr);
      }
    }.getClassList();
  }

  @Override
  public Set<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
    return new AbstractAnnotationClassFilter(packageName, annotationClass) {
      @Override
      public boolean filterCondition(Class<?> cls) {
        return cls.isAnnotationPresent(annotationClass);
      }
    }.getClassList();
  }

  @Override
  public Set<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass,
      ClassLoader classLoader) {
    return new AbstractAnnotationClassFilter(packageName, annotationClass, classLoader) {
      @Override
      public boolean filterCondition(Class<?> cls) { // 这里去掉了内部类
        return cls.isAnnotationPresent(annotationClass);
      }
    }.getClassList();
  }

  @Override
  public Set<Class<?>> getClassListBySuper(String packageName, Class<?> superClass) {
    return new AbstractSupperClassFilter(packageName, superClass) {
      @Override
      public boolean filterCondition(Class<?> clazz) { // 这里去掉了内部类
        return superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)
            && !Modifier.isInterface(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers())
            && Modifier.isPublic(clazz.getModifiers());
        // !cls.getName().contains("$");
      }

    }.getClassList();
  }

  @Override
  public Set<Class<?>> getClassListBySuper(String packageName, Class<?> superClass, ClassLoader classLoader) {
    return new AbstractSupperClassFilter(packageName, superClass, classLoader) {
      @Override
      public boolean filterCondition(Class<?> cls) { // 这里去掉了内部类
        return superClass.isAssignableFrom(cls) && !superClass.equals(cls);// &&
        // !cls.getName().contains("$");
      }

    }.getClassList();
  }

  // test
  public static void main(String[] args) throws Exception {
    ClassScanner cs = DefaultClassScanner.getInstance();
    Set<Class<?>> set = cs.getClassList("com.github", ".*\\.scanner\\..*Filter");
    for (Class<?> c : set) {
      System.out.println(c.getName());
    }
  }
}
