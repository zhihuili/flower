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
package com.ly.train.flower.common.scanner.filter;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.StringUtil;

/**
 * 
 * @author leeyazhou
 */
public abstract class AbstractClassFilter {
  private static final Logger logger = LoggerFactory.getLogger(AbstractClassFilter.class);
  private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  private final String packageName;
  private boolean isDebugEnabled = logger.isDebugEnabled();

  protected AbstractClassFilter(final String packageName) {
    this.packageName = packageName;
  }

  protected AbstractClassFilter(final String packageName, ClassLoader classLoader) {
    this(packageName);
    this.classLoader = classLoader;
  }

  public final Set<Class<?>> getClassList() {
    // 收集符合条件的Class类容器
    Set<Class<?>> clazzes = new HashSet<Class<?>>();
    try {
      // 从包名获取 URL 类型的资源
      Enumeration<URL> urls = classLoader.getResources(packageName.replace(".", "/"));
      // 遍历 URL 资源
      URL url;
      while (urls.hasMoreElements()) {
        url = urls.nextElement();
        if (url != null) {
          if (isDebugEnabled) {
            logger.debug("scan url : " + url.toString());
          }
          // 获取协议名（分为 file 与 jar）
          String protocol = url.getProtocol();
          if (protocol.equals("file")) { // classPath下的.class文件
            String packagePath = url.getPath();
            addClass(clazzes, packagePath, packageName);
          } else if (protocol.equals("jar")) {
            // classPath下的.jar文件
            JarURLConnection jarUrlConnection = (JarURLConnection) url.openConnection();
            JarFile jarFile = jarUrlConnection.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
              JarEntry jarEntry = jarEntries.nextElement();
              String jarEntryName = jarEntry.getName();
              // 判断该 entry 是否为 class
              if (jarEntryName.endsWith(".class")) {
                // 获取类名
                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                // 执行添加类操作
                doAddClass(clazzes, className);
              }
            }
          }
        }
      }
    } catch (Exception err) {
      logger.error("find class error！", err);
    }
    return clazzes;
  }

  private void addClass(Set<Class<?>> clazzes, String packagePath, String packageName) {
    try {
      // 获取包名路径下的 class 文件或目录
      File[] files = new File(packagePath).listFiles(new FileFilter() {
        @Override
        public boolean accept(File file) {
          return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
        }
      });
      // 遍历文件或目录
      for (File file : files) {
        String fileName = file.getName();
        // 判断是否为文件或目录
        if (file.isFile()) {
          // 获取类名
          String className = fileName.substring(0, fileName.lastIndexOf("."));
          if (StringUtil.isNotEmpty(packageName)) {
            className = packageName + "." + className;
          }
          // 执行添加类操作
          doAddClass(clazzes, className);
        } else {
          // 获取子包
          String subPackagePath = fileName;
          if (StringUtil.isNotEmpty(packagePath)) {
            subPackagePath = packagePath + "/" + subPackagePath;
          }
          // 子包名
          String subPackageName = fileName;
          if (StringUtil.isNotEmpty(packageName)) {
            subPackageName = packageName + "." + subPackageName;
          }
          // 递归调用
          addClass(clazzes, subPackagePath, subPackageName);
        }
      }
    } catch (Exception err) {
      logger.error("find class error！", err);
    }
  }

  private void doAddClass(Set<Class<?>> clazzes, String className) throws ClassNotFoundException {
    // 加载类
    Class<?> cls = classLoader.loadClass(className);
    // 判断是否可以添加类
    if (filterCondition(cls)) {
      // 添加类
      clazzes.add(cls);
      if (isDebugEnabled) {
        logger.debug("add class:" + cls.getName());
      }
    }
  }

  /**
   * 验证是否允许添加类
   * 
   * @param clazz clazz
   * @return Boolean Boolean
   */
  public abstract boolean filterCondition(Class<?> clazz);
}
