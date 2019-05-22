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
package com.ly.train.flower.common.io.resource;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.ly.train.flower.common.io.util.ResourceUtil;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 */
public class ResourceLoader {
  private static final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);
  private String rootPath;
  private final String subfix;
  private ClassLoader classLoader;
  private Set<String> cache = new TreeSet<String>();

  public ResourceLoader(String rootPath) {
    this(rootPath, null);
  }

  public ResourceLoader(String rootPath, String subfix) {
    this(rootPath, subfix, Thread.currentThread().getContextClassLoader());
  }



  public ResourceLoader(String rootPath, String subfix, ClassLoader classLoader) {
    this.rootPath = rootPath;
    this.subfix = subfix;
    this.classLoader = classLoader;
  }

  public Resource[] getResources() {
    Set<Resource> resources = new HashSet<Resource>();
    try {
      Iterator<Resource> it = findAllClassPathResources().iterator();
      while (it.hasNext()) {
        URL url = it.next().getURL();
        String protocol = url.getProtocol();
        if (ResourceUtil.URL_PROTOCOL_FILE.equals(protocol)) {
          String file = URLDecoder.decode(url.getFile(), "UTF-8");
          File dir = new File(file);
          if (dir.isDirectory()) {
            parseFile(dir, rootPath, resources);
          } else {
            throw new IllegalArgumentException("file must be directory, url : " + url);
          }
        } else if (ResourceUtil.URL_PROTOCOL_JAR.equals(protocol)) {
          parseJarFile(url, resources);
        }
      }
    } catch (IOException e) {
      logger.error("", e);
    }
    return resources.toArray(new Resource[resources.size()]);
  }

  protected void parseFile(File dir, String packageName, Set<Resource> resources) throws MalformedURLException {
    if (dir.isDirectory()) {
      File[] files = dir.listFiles();
      for (File file : files) {
        parseFile(file, packageName, resources);
      }
    } else if (dir.getName().endsWith(subfix)) {
      UrlResource resource = new UrlResource(dir.toURI().toURL());
      resource.setPath(dir.getPath());
      resource.setName(dir.getName());
      resources.add(resource);
    }
  }

  protected void parseJarFile(URL url, Set<Resource> resources) throws IOException {
    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
    Enumeration<JarEntry> entries = jar.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      if (entry.isDirectory()) {
        continue;
      }
      String name = entry.getName();

      boolean flag = true;
      if (StringUtil.isNotBlank(rootPath) && !name.startsWith(rootPath)) {
        flag = false;
      }
      if (!flag) {
        continue;
      }
      if (StringUtil.isNotBlank(subfix) && !name.endsWith(subfix)) {
        flag = false;
      }

      if (!flag) {
        continue;
      }

      JarResource resource = new JarResource(url);
      String path = url.getPath();
      if (path.startsWith(ResourceUtil.FILE_URL_PREFIX)) {
        path = path.substring(ResourceUtil.FILE_URL_PREFIX.length());
      }
      if (path.endsWith(ResourceUtil.JAR_URL_SEPARATOR)) {
        path = path.substring(0, path.indexOf(ResourceUtil.JAR_URL_SEPARATOR));
      }
      resource.setName(name);
      resource.setJarResource(true);
      resource.setPath(path);
      resources.add(resource);
    }
  }


  protected Set<Resource> findAllClassPathResources() throws IOException {
    Set<Resource> resources = new HashSet<Resource>();
    if (rootPath.startsWith("/")) {
      rootPath = rootPath.substring(1);
    }
    if (rootPath.endsWith(".")) {
      rootPath = rootPath.substring(0, rootPath.length() - 1);
    }
    String path = rootPath.replace(".", "/");
    Enumeration<URL> urls = classLoader.getResources(path);
    while (urls.hasMoreElements()) {
      URL url = urls.nextElement();
      if (cache.add(url.getPath())) {
        resources.add(new UrlResource(url));
      }
    }

    if (StringUtil.isBlank(rootPath)) {
      addAllClassLoaderJarRoots(classLoader, resources);
    }
    return resources;
  }

  protected void addAllClassLoaderJarRoots(ClassLoader classLoader, Set<Resource> resources) {
    if (classLoader instanceof URLClassLoader) {
      try {
        for (URL url : ((URLClassLoader) classLoader).getURLs()) {
          try {
            AbstractResource resource = null;
            if (ResourceUtil.URL_PROTOCOL_JAR.equals(url.getProtocol())) {
              resource = new JarResource(url);
            } else if (url.getPath().endsWith(ResourceUtil.JAR_FILE_EXTENSION)) {
              URL temp = new URL(ResourceUtil.JAR_URL_PREFIX + url + ResourceUtil.JAR_URL_SEPARATOR);
              resource = new UrlResource(temp);
            }
            if (resource != null && cache.add(resource.getURL().getPath())) {
              resources.add(resource);
            }
          } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
              logger.debug("Cannot search for matching files underneath [" + url
                  + "] because it cannot be converted to a valid 'jar:' URL: " + ex.getMessage());
            }
          }
        }
      } catch (Exception ex) {
        if (logger.isDebugEnabled()) {
          logger.debug("Cannot introspect jar files since ClassLoader [" + classLoader
              + "] does not support 'getURLs()': " + ex);
        }
      }
    }

    if (classLoader == ClassLoader.getSystemClassLoader()) {
      // "java.class.path" manifest evaluation...
      addClassPathManifestEntries(resources);
    }

    if (classLoader != null) {
      try {
        // Hierarchy traversal...
        addAllClassLoaderJarRoots(classLoader.getParent(), resources);
      } catch (Exception ex) {
        if (logger.isDebugEnabled()) {
          logger.debug("Cannot introspect jar files in parent ClassLoader since [" + classLoader
              + "] does not support 'getParent()': " + ex);
        }
      }
    }
  }

  protected void addClassPathManifestEntries(Set<Resource> result) {
    try {
      String javaClassPathProperty = System.getProperty("java.class.path");
      for (String path : StringUtil.delimitedListToStringArray(javaClassPathProperty,
          System.getProperty("path.separator"))) {
        try {
          String filePath = new File(path).getAbsolutePath();
          int prefixIndex = filePath.indexOf(':');
          if (prefixIndex == 1) {
            // Possibly "c:" drive prefix on Windows, to be upper-cased for
            // proper duplicate
            // detection
            filePath = StringUtil.capitalize(filePath);
          }
          if (path.endsWith(ResourceUtil.JAR_FILE_EXTENSION)) {
            URL url =
                new URL(ResourceUtil.JAR_URL_PREFIX + ResourceUtil.FILE_URL_PREFIX + filePath
                    + ResourceUtil.JAR_URL_SEPARATOR);
            Resource resource = new JarResource(url);
            if (cache.add(resource.getURL().getPath())) {
              result.add(resource);
            }
          }
        } catch (MalformedURLException ex) {
          if (logger.isDebugEnabled()) {
            logger.debug("Cannot search for matching files underneath [" + path
                + "] because it cannot be converted to a valid 'jar:' URL: " + ex.getMessage());
          }
        }
      }
    } catch (Exception ex) {
      if (logger.isDebugEnabled()) {
        logger.debug("Failed to evaluate 'java.class.path' manifest entries: " + ex);
      }
    }
  }

}
