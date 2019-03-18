/**
 * 
 */
package com.ly.train.flower.common.bytecode;

import java.lang.reflect.Field;
import org.junit.Test;

/**
 * @author leeyazhou
 *
 */
public class ClassGeneratorTest {

  @Test
  public void testMain() throws Exception {
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

    cg.addMethod(
        "public String getName(" + Bean.class.getName() + " o){  return (String)FNAME.get($1); }");
    cg.addMethod(
        "public void setName(" + Bean.class.getName() + " o, Object name){ FNAME.set($1, $2); }");

    cg.addDefaultConstructor();
    Class<?> cl = cg.toClass();
    cl.getField("FNAME").set(null, fname);

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
