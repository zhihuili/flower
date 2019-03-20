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
package com.ly.train.flower.common.bytecode;

import java.lang.reflect.Field;
import org.junit.Test;
import com.ly.train.flower.common.annotation.FlowerService;
import javassist.bytecode.annotation.Annotation;

/**
 * @author leeyazhou
 *
 */
public class ClassGeneratorTest {

  @Test
  public void testCreateClass() throws Exception {
    Bean b = new Bean();
    Field fname = null, fs[] = Bean.class.getDeclaredFields();
    for (Field f : fs) {
      f.setAccessible(true);
      if (f.getName().equals("name"))
        fname = f;
    }

    ClassGenerator cg = ClassGenerator.newInstance();
    cg.setClassName(Bean.class.getName() + "$Builder");
    cg.addInterface(Builder.class);
    cg.addField("public static java.lang.reflect.Field FNAME;");

    cg.addMethod("public String getName(" + Bean.class.getName() + " o){  return (String)FNAME.get($1); }");
    cg.addMethod("public void setName(" + Bean.class.getName() + " o, Object name){ FNAME.set($1, $2); }");

    cg.addDefaultConstructor();
    cg.addAnnotation(FlowerService.class.getName(), null);
    Class<?> cl = cg.toClass();
    cl.getField("FNAME").set(null, fname);
    
    
    for(java.lang.annotation.Annotation an : cl.getAnnotations()) {
      System.out.println(an);
    }
    System.out.println(cl.getName());
    Builder<String> builder = (Builder<String>) cl.newInstance();
    System.out.println(b.getName());
    builder.setName(b, "ok");
    System.out.println(b.getName());
  }

}


interface Builder<T> {
  T getName(Bean bean);

  void setName(Bean bean, T name);
}


class Bean {
  int age = 30;

  private String name = "flower";

  public int getAge() {
    return age;
  }

  public String getName() {
    return name;
  }
}
