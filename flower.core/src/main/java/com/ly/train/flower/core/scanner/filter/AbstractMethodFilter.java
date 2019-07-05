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
package com.ly.train.flower.core.scanner.filter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author leeyazhou
 */
public abstract class AbstractMethodFilter {
  protected final Class<?> clazz;

  protected AbstractMethodFilter(Class<?> clazz) {
    this.clazz = clazz;
  }

  public abstract boolean filterCondition(Method method);

  public List<Method> getMethodList() {
    List<Method> mlist = new ArrayList<Method>();
    for (Method m : clazz.getMethods()) {
      if (filterCondition(m)) {
        mlist.add(m);
      }
    }
    return mlist;
  }

}
