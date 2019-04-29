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
  private static final Map<ClassLoader, ClassPool> POOL_MAP = new ConcurrentHashMap<ClassLoader, ClassPool>(); // ClassLoader
                                                                                                               // -
                                                                                                               // ClassPool
  private ClassPool classPool;
  private CtClass ctClass;
  private String mClassName;
  private String mSuperClass;
  private Set<String> mInterfaces;
  private List<String> mFields;
  private List<String> mConstructors;
  private List<String> mMethods;
  private Map<String, Method> mCopyMethods; // <method desc,method instance>
  private Map<String, Constructor<?>> mCopyConstructors; // <constructor
                                                         // desc,constructor
                                                         // instance>
  private boolean mDefaultConstructor = false;

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
    return mClassName;
  }

  public ClassGenerator setClassName(String name) {
    mClassName = name;
    return this;
  }

  public ClassGenerator addInterface(String cn) {
    if (mInterfaces == null) {
      mInterfaces = new HashSet<String>();
    }
    mInterfaces.add(cn);
    return this;
  }

  public ClassGenerator addInterface(Class<?> cl) {
    return addInterface(cl.getName());
  }

  public ClassGenerator setSuperClass(String cn) {
    mSuperClass = cn;
    return this;
  }

  public ClassGenerator setSuperClass(Class<?> cl) {
    mSuperClass = cl.getName();
    return this;
  }

  public ClassGenerator addField(String code) {
    if (mFields == null) {
      mFields = new ArrayList<String>();
    }
    mFields.add(code);
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
    if (mMethods == null) {
      mMethods = new ArrayList<String>();
    }
    mMethods.add(code);
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

  public ClassGenerator addMethod(Method m) {
    addMethod(m.getName(), m, null);
    return this;
  }

  public ClassGenerator addMethod(Method m, Map<String, Map<String, Object>> annotations) {
    addMethod(m.getName(), m, annotations);
    return this;
  }

  public ClassGenerator addMethod(Method m, java.lang.annotation.Annotation[] annotations) {
    Map<String, Map<String, Object>> anMap = new HashMap<>();
    for (java.lang.annotation.Annotation an : annotations) {
      Map<String, Object> value = new HashMap<>();
      try {
        for (Method mm : an.getClass().getDeclaredMethods()) {

          if ("equals".equals(mm.getName()) || "toString".equals(mm.getName()) || "hashCode".equals(mm.getName())) {
            continue;
          }
          Object v = mm.invoke(an);
          value.put(mm.getName(), v);
        }
      } catch (Exception e) {
        logger.error("", e);
      }
      anMap.put(an.annotationType().getName(), value);
    }

    addMethod(m.getName(), m, anMap);
    return this;
  }

  public ClassGenerator addMethod(String name, Method m, Map<String, Map<String, Object>> annotations) {
    String desc = name + ReflectUtil.getDescWithoutMethodName(m);
    addMethod(':' + desc);
    if (mCopyMethods == null) {
      mCopyMethods = new ConcurrentHashMap<String, Method>(8);
    }
    mCopyMethods.put(desc, m);
    if (annotations != null) {
      if (methodAnnotations == null) {
        this.methodAnnotations = new HashMap<>();
      }
      methodAnnotations.put(':' + desc, annotations);
    }

    return this;
  }

  public ClassGenerator addConstructor(String code) {
    if (mConstructors == null) {
      mConstructors = new LinkedList<String>();
    }
    mConstructors.add(code);
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

  public ClassGenerator addConstructor(Constructor<?> c) {
    String desc = ReflectUtil.getDesc(c);
    addConstructor(":" + desc);
    if (mCopyConstructors == null) {
      mCopyConstructors = new ConcurrentHashMap<String, Constructor<?>>(4);
    }
    mCopyConstructors.put(desc, c);
    return this;
  }

  public ClassGenerator addDefaultConstructor() {
    mDefaultConstructor = true;
    return this;
  }

  public ClassPool getClassPool() {
    return classPool;
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
      CtClass ctcs = mSuperClass == null ? null : classPool.get(mSuperClass);
      if (mClassName == null) {
        mClassName =
            (mSuperClass == null || javassist.Modifier.isPublic(ctcs.getModifiers()) ? ClassGenerator.class.getName()
                : mSuperClass + "$sc") + id;
      }
      ctClass = classPool.makeClass(mClassName);
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

      if (mSuperClass != null) {
        ctClass.setSuperclass(ctcs);
      }
      ctClass.addInterface(classPool.get(DC.class.getName())); // add dynamic
                                                               // class tag.
      if (mInterfaces != null) {
        for (String cl : mInterfaces) {
          ctClass.addInterface(classPool.get(cl));
        }
      }
      if (mFields != null) {
        for (String code : mFields) {
          ctClass.addField(CtField.make(code, ctClass));
        }
      }
      if (mMethods != null) {
        for (String code : mMethods) {
          CtMethod method = null;
          if (code.charAt(0) == ':') {
            method =
                CtNewMethod.copy(getCtMethod(mCopyMethods.get(code.substring(1))),
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
      if (mDefaultConstructor) {
        ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));
      }
      if (mConstructors != null) {
        for (String code : mConstructors) {
          if (code.charAt(0) == ':') {
            ctClass.addConstructor(CtNewConstructor.copy(getCtConstructor(mCopyConstructors.get(code.substring(1))),
                ctClass, null));
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


  private MemberValue getMemberValue(Object o, ConstPool cp) {
    if (o == null) {
      return null;
    }
    if (o instanceof Integer) {
      return new IntegerMemberValue(cp, (Integer) o);
    } else if (o instanceof Boolean) {
      return new BooleanMemberValue((Boolean) o, cp);
    } else if (o instanceof Double) {
      return new DoubleMemberValue((Double) o, cp);
    } else if (o instanceof Float) {
      return new FloatMemberValue((Float) o, cp);
    } else if (o instanceof Short) {
      return new ShortMemberValue((Short) o, cp);
    } else if (o instanceof String) {
      return new StringMemberValue((String) o, cp);
    } else if (o instanceof String[]) {
      String[] oo = (String[]) o;
      MemberValue[] memberValues = new MemberValue[oo.length];
      ArrayMemberValue v = new ArrayMemberValue(cp);
      for (int i = 0; i < oo.length; i++) {
        memberValues[i] = getMemberValue(oo[i], cp);
      }
      v.setValue(memberValues);
      return v;
    } else if (o instanceof Byte) {
      return new ByteMemberValue((Byte) o, cp);
    } else if (o instanceof Annotation) {
      return new AnnotationMemberValue((Annotation) o, cp);
    } else if (o instanceof ArrayMemberValue) {
      return new ArrayMemberValue((MemberValue) o, cp);
    } else if (o instanceof Character) {
      return new CharMemberValue((char) o, cp);
    } else if (o instanceof Long) {
      return new LongMemberValue((Long) o, cp);
    }
    return null;
  }

  public void release() {
    if (ctClass != null) {
      ctClass.detach();
    }
    if (mInterfaces != null) {
      mInterfaces.clear();
    }
    if (mFields != null) {
      mFields.clear();
    }
    if (mMethods != null) {
      mMethods.clear();
    }
    if (mConstructors != null) {
      mConstructors.clear();
    }
    if (mCopyMethods != null) {
      mCopyMethods.clear();
    }
    if (mCopyConstructors != null) {
      mCopyConstructors.clear();
    }
  }

  private CtClass getCtClass(Class<?> c) throws NotFoundException {
    return classPool.get(c.getName());
  }

  private CtMethod getCtMethod(Method m) throws NotFoundException {
    return getCtClass(m.getDeclaringClass()).getMethod(m.getName(), ReflectUtil.getDescWithoutMethodName(m));
  }

  private CtConstructor getCtConstructor(Constructor<?> c) throws NotFoundException {
    return getCtClass(c.getDeclaringClass()).getConstructor(ReflectUtil.getDesc(c));
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
