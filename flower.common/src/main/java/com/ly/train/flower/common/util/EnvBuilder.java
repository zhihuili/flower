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
package com.ly.train.flower.common.util;

import java.util.Set;
import java.util.regex.Pattern;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Predicate;
import com.ly.train.flower.common.service.container.ServiceFactory;
import com.ly.train.flower.common.service.container.ServiceFlow;

public class EnvBuilder {
  private static final Logger logger = LoggerFactory.getLogger(EnvBuilder.class);

  public static void buildEnv() throws Exception {
    buildEnv(null);
  }

  /**
   *
   * @param clz Reflections需要从clz类中获取ClassLoader的路径，再遍历Resources目录 clz填写Resources对应的包下面的Class
   *        当clz为null的时候，从所有的jar中读取services和flow文件
   * @throws Exception
   */
  public static void buildEnv(Class<?> clz) throws Exception {
    Predicate<String> filter = new FilterBuilder().include(".*\\.services").include(".*\\.flow");


    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.filterInputsBy(filter).setScanners(new ResourcesScanner());
    if (clz != null) {
      configurationBuilder.setUrls(ClasspathHelper.forClass(clz));
    } else {
      configurationBuilder.setUrls(ClasspathHelper.forClassLoader());
    }

    Reflections reflections = new Reflections(configurationBuilder);

    Set<String> servicesFiles = reflections.getResources(Pattern.compile(".*\\.services"));
    for (String path : servicesFiles) {
      logger.info("find service, path : {}", path);
      ServiceFactory.registerService(FileUtil.readService("/" + path));
    }
    Set<String> flowFiles = reflections.getResources(Pattern.compile(".*\\.flow"));
    for (String path : flowFiles) {
      logger.info("find flow file, path : {}", path);
      String flowName = path.substring(0, path.lastIndexOf("."));
      ServiceFlow.getOrCreate(flowName).buildFlow(FileUtil.readFlow("/" + path));
    }
  }
}
