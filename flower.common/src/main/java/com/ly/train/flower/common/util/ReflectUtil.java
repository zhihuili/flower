/**
 * 
 */
package com.ly.train.flower.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javassist.NotFoundException;

/**
 * @author leeyazhou
 *
 */
public class ReflectUtil {
  /**
   * void(V).
   */
  public static final char JVM_VOID = 'V';

  /**
   * boolean(Z).
   */
  public static final char JVM_BOOLEAN = 'Z';

  /**
   * byte(B).
   */
  public static final char JVM_BYTE = 'B';

  /**
   * char(C).
   */
  public static final char JVM_CHAR = 'C';

  /**
   * double(D).
   */
  public static final char JVM_DOUBLE = 'D';

  /**
   * float(F).
   */
  public static final char JVM_FLOAT = 'F';

  /**
   * int(I).
   */
  public static final char JVM_INT = 'I';

  /**
   * long(J).
   */
  public static final char JVM_LONG = 'J';

  /**
   * short(S).
   */
  public static final char JVM_SHORT = 'S';

  public static String getName(Class<?> c) {
    if (c.isArray()) {
      StringBuilder sb = new StringBuilder();
      do {
        sb.append("[]");
        c = c.getComponentType();
      } while (c.isArray());

      return c.getName() + sb.toString();
    }
    return c.getName();
  }

  /**
   * get class desc. boolean[].class => "[Z" Object.class => "Ljava/lang/Object;"
   *
   * @param c class.
   * @return desc.
   * @throws NotFoundException
   */
  public static String getDesc(Class<?> c) {
    StringBuilder ret = new StringBuilder();

    while (c.isArray()) {
      ret.append('[');
      c = c.getComponentType();
    }

    if (c.isPrimitive()) {
      String t = c.getName();
      if ("void".equals(t))
        ret.append(JVM_VOID);
      else if ("boolean".equals(t))
        ret.append(JVM_BOOLEAN);
      else if ("byte".equals(t))
        ret.append(JVM_BYTE);
      else if ("char".equals(t))
        ret.append(JVM_CHAR);
      else if ("double".equals(t))
        ret.append(JVM_DOUBLE);
      else if ("float".equals(t))
        ret.append(JVM_FLOAT);
      else if ("int".equals(t))
        ret.append(JVM_INT);
      else if ("long".equals(t))
        ret.append(JVM_LONG);
      else if ("short".equals(t))
        ret.append(JVM_SHORT);
    } else {
      ret.append('L');
      ret.append(c.getName().replace('.', '/'));
      ret.append(';');
    }
    return ret.toString();
  }

  /**
   * get constructor desc. "()V", "(Ljava/lang/String;I)V"
   *
   * @param c constructor.
   * @return desc
   */
  public static String getDesc(final Constructor<?> c) {
    StringBuilder ret = new StringBuilder("(");
    Class<?>[] parameterTypes = c.getParameterTypes();
    for (int i = 0; i < parameterTypes.length; i++)
      ret.append(getDesc(parameterTypes[i]));
    ret.append(')').append('V');
    return ret.toString();
  }

  public static ClassLoader getClassLoader(Class<?> cls) {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Throwable ex) {
      // Cannot access thread context ClassLoader - falling back to system class loader...
    }
    if (cl == null) {
      // No thread context class loader -> use class loader of this class.
      cl = cls.getClassLoader();
    }
    return cl;
  }

  /**
   * get method desc. "(I)I", "()V", "(Ljava/lang/String;Z)V"
   *
   * @param m method.
   * @return desc.
   */
  public static String getDescWithoutMethodName(Method m) {
    StringBuilder ret = new StringBuilder();
    ret.append('(');
    Class<?>[] parameterTypes = m.getParameterTypes();
    for (int i = 0; i < parameterTypes.length; i++)
      ret.append(getDesc(parameterTypes[i]));
    ret.append(')').append(getDesc(m.getReturnType()));
    return ret.toString();
  }
}
