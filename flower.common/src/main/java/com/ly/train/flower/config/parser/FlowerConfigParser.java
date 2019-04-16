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
/** */
package com.ly.train.flower.config.parser;

import java.io.FileInputStream;
import java.io.InputStream;
import org.yaml.snakeyaml.Yaml;
import com.ly.train.flower.common.util.IOUtil;
import com.ly.train.flower.common.util.ResourceUtil;
import com.ly.train.flower.common.util.StringUtil;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/** @author leeyazhou */
public class FlowerConfigParser implements ConfigParser<FlowerConfig> {
  private static final Logger logger = LoggerFactory.getLogger(FlowerConfigParser.class);
  private static final String defaultConfigLocation = "flower.yml";
  private String configLocation = defaultConfigLocation;

  public FlowerConfigParser(String configLocation) {
    if (StringUtil.isNotBlank(configLocation)) {
      this.configLocation = configLocation;
    }
  }

  @Override
  public FlowerConfig parse() {
    FlowerConfig config = null;
    InputStream is = null;
    try {
      logger.info("parse FlowerConfig, configLocation : {}", configLocation);
      is = getClass().getResourceAsStream(configLocation);
      if (is == null) {
        is = getClass().getResourceAsStream("/" + configLocation);
      }
      if (is == null) {
        is = new FileInputStream(ResourceUtil.getFile(configLocation));
      }
      if (is != null) {
        config = new Yaml().loadAs(is, FlowerConfig.class);
        logger.info("flowerConfig : {}", config);
      }
      return config;
    } catch (Exception e) {
      logger.error("fail to parse : " + configLocation, e);
    } finally {
      IOUtil.close(is);
    }
    config = new FlowerConfig();
    return config;
  }
}
