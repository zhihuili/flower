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
package com.ly.train.flower.common.bytecode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ly.train.flower.common.util.ArrayUtil;
import com.ly.train.flower.common.util.ReflectUtil;
import com.ly.train.flower.common.util.StringUtil;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * 
 * @author leeyazhou
 * 
 */
public final class ClassGenerator {
  private static final Logger logger = LoggerFactory.getLogger(ClassGenerator.class);
  private static final AtomicLong CLASS_NAME_COUNTER = new AtomicLong(0);
  private static final String SIMPLE_NAME_TAG = "<init>";
  private static final Map<ClassLoader, ClassPool> POOL_MAP = new ConcurrentHashMap<>(); // ClassLoader
                                                                                         // -
                                                                                         // ClassPool
  private ClassPool classPool;
  private CtClass ctClass;
  private String className;
  private String superClass;
  private Set<String> interfaces;
  private List<String> fields;
  private List<String> constructors;
  private List<String> methods;
  private Map<String, Method> copyMethods; // <method Desk,method instance>
  private Map<String, Constructor<?>> copyConstructors; // <constructor
                                                        // desc,constructor
                                                        // instance>
  private boolean defaultConstructor = false;

  // <method desc, <annotation,annotationProps>>
  private Map<String, Map<String, Map<String, Object>>> methodAnnotations;

  private Map<String, Map<String, Object>> annotations;

  private ClassGenerator() {}

  private ClassGenerator(ClassPool pool) {
    this.classPool = pool;
  }

  public static ClassGenerator newInstance() {
    return new ClassGenerator(getClassPool(Thread.currentThread().getContextClassLoader()));
  }

  public static ClassGenerator newInstance(ClassLoader loader) {
    return new ClassGenerator(getClassPool(loader));
  }

  public static boolean isDynamicClass(Class<?> cl) {
    return ClassGenerator.DC.class.isAssignableFrom(cl);
  }

  public static ClassPool getClassPool(ClassLoader loader) {
    if (loader == null) {
      return ClassPool.getDefault();
    }

    ClassPool pool = POOL_MAP.get(loader);
    if (pool == null) {
      pool = new ClassPool(true);
      pool.appendClassPath(new LoaderClassPath(loader));
      POOL_MAP.put(loader, pool);
    }
    return pool;
  }

  public ClassPool getClassPool() {
    return classPool;
  }

  private static String modifier(int mod) {
    StringBuilder modifier = new StringBuilder();
    if (Modifier.isPublic(mod)) {
      modifier.append("public");
    }
    if (Modifier.isProtected(mod)) {
      modifier.append("protected");
    }
    if (Modifier.isPrivate(mod)) {
      modifier.append("private");
    }

    if (Modifier.isStatic(mod)) {
      modifier.append(" static");
    }
    if (Modifier.isVolatile(mod)) {
      modifier.append(" volatile");
    }

    return modifier.toString();
  }

  public String getClassName() {
    return className;
  }

  public ClassGenerator setClassName(String name) {
    className = name;
    return this;
  }

  public ClassGenerator addInterface(String cn) {
    if (interfaces == null) {
      interfaces = new HashSet<String>();
    }
    interfaces.add(cn);
    return this;
  }

  public ClassGenerator addInterface(Class<?> cl) {
    return addInterface(cl.getName());
  }

  public ClassGenerator setSuperClass(String cn) {
    superClass = cn;
    return this;
  }

  public ClassGenerator setSuperClass(Class<?> cl) {
    superClass = cl.getName();
    return this;
  }

  public ClassGenerator addField(String code) {
    if (fields == null) {
      fields = new ArrayList<String>();
    }
    fields.add(code);
    return this;
  }

  public ClassGenerator addField(String name, int mod, Class<?> type) {
    return addField(name, mod, type, null);
  }

  public ClassGenerator addField(String name, int mod, Class<?> type, String def) {
    StringBuilder sb = new StringBuilder();
    sb.append(modifier(mod)).append(' ').append(ReflectUtil.getName(type)).append(' ');
    sb.append(name);
    if (StringUtil.isNotEmpty(def)) {
      sb.append('=');
      sb.append(def);
    }
    sb.append(';');
    return addField(sb.toString());
  }

  public ClassGenerator addMethod(String code) {
    if (methods == null) {
      methods = new ArrayList<String>();
    }
    methods.add(code);
    return this;
  }

  public ClassGenerator addMethod(String name, int mod, Class<?> rt, Class<?>[] pts, String body) {
    return addMethod(name, mod, rt, pts, null, body);
  }

  public ClassGenerator addMethod(String name, int mod, Class<?> rt, Class<?>[] pts, Class<?>[] ets, String body) {
    StringBuilder sb = new StringBuilder();
    sb.append(modifier(mod)).append(' ').append(ReflectUtil.getName(rt)).append(' ').append(name);
    sb.append('(');
    for (int i = 0; i < pts.length; i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(ReflectUtil.getName(pts[i]));
      sb.append(" arg").append(i);
    }
    sb.append(')');
    if (ArrayUtil.isNotEmpty(ets)) {
      sb.append(" throws ");
      for (int i = 0; i < ets.length; i++) {
        if (i > 0) {
          sb.append(',');
        }
        sb.append(ReflectUtil.getName(ets[i]));
      }
    }
    sb.append('{').append(body).append('}');
    return addMethod(sb.toString());
  }

  public ClassGenerator addMethod(Method method) {
    addMethod(method.getName(), method, null);
    return this;
  }

  public ClassGenerator addMethod(Method method, Map<String, Map<String, Object>> annotations) {
    addMethod(method.getName(), method, annotations);
    return this;
  }

  public ClassGenerator addMethod(Method method, java.lang.annotation.Annotation[] annotations) {
    Map<String, Map<String, Object>> anMap = new HashMap<>();
    for (java.lang.annotation.Annotation an : annotations) {
      Map<String, Object> value = new HashMap<>();
      try {
        for (Method mm : an.getClass().getDeclaredMethods()) {

          if ("equals".equals(mm.getName()) || "toString".equals(mm.getName()) || "hashCode".equals(mm.getName())) {
            continue;
          }
          Object ret = mm.invoke(an);
          value.put(mm.getName(), ret);
        }
      } catch (Exception e) {
        logger.error("", e);
      }
      anMap.put(an.annotationType().getName(), value);
    }

    addMethod(method.getName(), method, anMap);
    return this;
  }

  public ClassGenerator addMethod(String name, Method method, Map<String, Map<String, Object>> annotations) {
    String desc = name + ReflectUtil.getDescWithoutMethodName(method);
    addMethod(':' + desc);
    if (copyMethods == null) {
      copyMethods = new ConcurrentHashMap<String, Method>(8);
    }
    copyMethods.put(desc, method);
    if (annotations != null) {
      if (methodAnnotations == null) {
        this.methodAnnotations = new HashMap<>();
      }
      methodAnnotations.put(':' + desc, annotations);
    }

    return this;
  }

  public ClassGenerator addConstructor(String code) {
    if (constructors == null) {
      constructors = new LinkedList<String>();
    }
    constructors.add(code);
    return this;
  }

  public ClassGenerator addConstructor(int mod, Class<?>[] pts, String body) {
    return addConstructor(mod, pts, null, body);
  }

  public ClassGenerator addConstructor(int mod, Class<?>[] pts, Class<?>[] ets, String body) {
    StringBuilder sb = new StringBuilder();
    sb.append(modifier(mod)).append(' ').append(SIMPLE_NAME_TAG);
    sb.append('(');
    for (int i = 0; i < pts.length; i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(ReflectUtil.getName(pts[i]));
      sb.append(" arg").append(i);
    }
    sb.append(')');
    if (ArrayUtil.isNotEmpty(ets)) {
      sb.append(" throws ");
      for (int i = 0; i < ets.length; i++) {
        if (i > 0) {
          sb.append(',');
        }
        sb.append(ReflectUtil.getName(ets[i]));
      }
    }
    sb.append('{').append(body).append('}');
    return addConstructor(sb.toString());
  }

  public ClassGenerator addConstructor(Constructor<?> clazz) {
    String desc = ReflectUtil.getDesc(clazz);
    addConstructor(":" + desc);
    if (copyConstructors == null) {
      copyConstructors = new ConcurrentHashMap<String, Constructor<?>>(4);
    }
    copyConstructors.put(desc, clazz);
    return this;
  }

  public ClassGenerator addDefaultConstructor() {
    defaultConstructor = true;
    return this;
  }



  public Class<?> toClass() {
    return toClass(ReflectUtil.getClassLoader(ClassGenerator.class), getClass().getProtectionDomain());
  }

  public Class<?> toClass(ClassLoader loader, ProtectionDomain pd) {
    if (ctClass != null) {
      ctClass.detach();
    }
    long id = CLASS_NAME_COUNTER.getAndIncrement();
    try {
      CtClass ctcs = superClass == null ? null : classPool.get(superClass);
      if (className == null) {
        className =
            (superClass == null || javassist.Modifier.isPublic(ctcs.getModifiers()) ? ClassGenerator.class.getName()
                : superClass + "$sc") + id;
      }
      ctClass = classPool.makeClass(className);
      ClassFile classFile = ctClass.getClassFile();
      ConstPool constpool = classFile.getConstPool();
      if (annotations != null) {

        AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);

        for (Entry<String, Map<String, Object>> entry : annotations.entrySet()) {
          final String annotation = entry.getKey();
          final Map<String, Object> pros = entry.getValue();
          Annotation annot = new Annotation(annotation, constpool);
          if (pros != null) {
            for (Map.Entry<String, Object> v : pros.entrySet()) {
              MemberValue memberValue = getMemberValue(v.getValue(), constpool);
              if (memberValue != null) {
                annot.addMemberValue(v.getKey(), memberValue);
              }
            }
          }
          attr.addAnnotation(annot);
        }
        classFile.addAttribute(attr);
      }

      if (superClass != null) {
        ctClass.setSuperclass(ctcs);
      }
      ctClass.addInterface(classPool.get(DC.class.getName())); // add dynamic
                                                               // class tag.
      if (interfaces != null) {
        for (String cl : interfaces) {
          ctClass.addInterface(classPool.get(cl));
        }
      }
      if (fields != null) {
        for (String code : fields) {
          ctClass.addField(CtField.make(code, ctClass));
        }
      }
      if (methods != null) {
        for (String code : methods) {
          CtMethod method = null;
          if (code.charAt(0) == ':') {
            method = CtNewMethod.copy(getCtMethod(copyMethods.get(code.substring(1))),
                code.substring(1, code.indexOf('(')), ctClass, null);
          } else {
            method = CtNewMethod.make(code, ctClass);
          }

          if (methodAnnotations != null) {
            Map<String, Map<String, Object>> annotations = methodAnnotations.get(code);
            if (annotations != null) {
              AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
              for (Map.Entry<String, Map<String, Object>> entry : annotations.entrySet()) {
                Annotation annot = new Annotation(entry.getKey(), constpool);
                for (Map.Entry<String, Object> p : entry.getValue().entrySet()) {
                  MemberValue memberValue = getMemberValue(p.getValue(), constpool);
                  if (memberValue != null) {
                    annot.addMemberValue(p.getKey(), memberValue);
                  }
                }
                attr.addAnnotation(annot);
              }
              method.getMethodInfo().addAttribute(attr);
            }
          }


          ctClass.addMethod(method);
        }
      }
      if (defaultConstructor) {
        ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));
      }
      if (constructors != null) {
        for (String code : constructors) {
          if (code.charAt(0) == ':') {
            ctClass.addConstructor(
                CtNewConstructor.copy(getCtConstructor(copyConstructors.get(code.substring(1))), ctClass, null));
          } else {
            String[] sn = ctClass.getSimpleName().split("\\$+"); // inner class
                                                                 // name include
                                                                 // $.
            ctClass
                .addConstructor(CtNewConstructor.make(code.replaceFirst(SIMPLE_NAME_TAG, sn[sn.length - 1]), ctClass));
          }
        }
      }
      return ctClass.toClass(loader, pd);
    } catch (RuntimeException e) {
      throw e;
    } catch (NotFoundException e) {
      throw new RuntimeException(e.getMessage(), e);
    } catch (CannotCompileException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }


  private MemberValue getMemberValue(Object obj, ConstPool cp) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof Integer) {
      return new IntegerMemberValue(cp, (Integer) obj);
    } else if (obj instanceof Boolean) {
      return new BooleanMemberValue((Boolean) obj, cp);
    } else if (obj instanceof Double) {
      return new DoubleMemberValue((Double) obj, cp);
    } else if (obj instanceof Float) {
      return new FloatMemberValue((Float) obj, cp);
    } else if (obj instanceof Short) {
      return new ShortMemberValue((Short) obj, cp);
    } else if (obj instanceof String) {
      return new StringMemberValue((String) obj, cp);
    } else if (obj instanceof String[]) {
      String[] oo = (String[]) obj;
      MemberValue[] memberValues = new MemberValue[oo.length];
      ArrayMemberValue value = new ArrayMemberValue(cp);
      for (int i = 0; i < oo.length; i++) {
        memberValues[i] = getMemberValue(oo[i], cp);
      }
      value.setValue(memberValues);
      return value;
    } else if (obj instanceof Byte) {
      return new ByteMemberValue((Byte) obj, cp);
    } else if (obj instanceof Annotation) {
      return new AnnotationMemberValue((Annotation) obj, cp);
    } else if (obj instanceof ArrayMemberValue) {
      return new ArrayMemberValue((MemberValue) obj, cp);
    } else if (obj instanceof Character) {
      return new CharMemberValue((char) obj, cp);
    } else if (obj instanceof Long) {
      return new LongMemberValue((Long) obj, cp);
    }
    return null;
  }

  public void release() {
    if (ctClass != null) {
      ctClass.detach();
    }
    if (interfaces != null) {
      interfaces.clear();
    }
    if (fields != null) {
      fields.clear();
    }
    if (methods != null) {
      methods.clear();
    }
    if (constructors != null) {
      constructors.clear();
    }
    if (copyMethods != null) {
      copyMethods.clear();
    }
    if (copyConstructors != null) {
      copyConstructors.clear();
    }
  }

  private CtClass getCtClass(Class<?> clazz) throws NotFoundException {
    return classPool.get(clazz.getName());
  }

  private CtMethod getCtMethod(Method method) throws NotFoundException {
    return getCtClass(method.getDeclaringClass()).getMethod(method.getName(),
        ReflectUtil.getDescWithoutMethodName(method));
  }

  private CtConstructor getCtConstructor(Constructor<?> construct) throws NotFoundException {
    return getCtClass(construct.getDeclaringClass()).getConstructor(ReflectUtil.getDesc(construct));
  }

  public void addAnnotation(String annotation, Map<String, Object> properties) {
    if (annotations == null) {
      annotations = new HashMap<>();
    }
    annotations.put(annotation, properties);
  }

  public static interface DC {

  } // dynamic class tag interface.
}
