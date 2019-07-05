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
import com.ly.train.flower.common.scanner.MethodScanner;
import com.ly.train.flower.core.scanner.filter.AbstractAnnotationMethodFilter;
import com.ly.train.flower.core.scanner.filter.AbstractPatternNameMethodFilter;

/**
 * 
 * @author leeyazhou
 */
public class DefaultMethodScanner implements MethodScanner {
  private static final DefaultMethodScanner instance = new DefaultMethodScanner();

  private DefaultMethodScanner() {

  }

  public static DefaultMethodScanner getInstance() {
    return instance;
  }

  @Override
  public List<Method> getMethodList(Class<?> clazz, final String methodPattern) {
    return new AbstractPatternNameMethodFilter(clazz, methodPattern) {

      @Override
      public boolean filterCondition(Method method) {
        return method.getName().matches(methodPattern);
      }
    }.getMethodList();
  }

  @Override
  public List<Method> getMethodListByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationType) {
    return new AbstractAnnotationMethodFilter(clazz, annotationType) {

      @Override
      public boolean filterCondition(Method method) {
        return method.isAnnotationPresent(annotationType);
      }
    }.getMethodList();
  }

  @Override
  public List<Method> getMethodListByAnnotationInterface(Class<?> clazz, Class<? extends Annotation> annotationType) {
    return new AbstractAnnotationMethodFilter(clazz, annotationType) {

      @Override
      public boolean filterCondition(Method method) {
        if (method.isAnnotationPresent(annotationType)) {
          return true;
        }
        Class<?>[] cls = clazz.getInterfaces();
        for (Class<?> c : cls) {
          try {
            Method md = c.getDeclaredMethod(method.getName(), method.getParameterTypes());
            if (md.isAnnotationPresent(annotationType)) {
              return true;
            }
          } catch (NoSuchMethodException err) {
            err.printStackTrace();
          } catch (Exception err) {
            err.printStackTrace();
          }
        }
        return false;
      }
    }.getMethodList();
  }
}
