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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.ly.train.flower.common.util.IOUtil;
import org.yaml.snakeyaml.Yaml;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/** @author leeyazhou */
public class FlowerConfigParser implements ConfigParser<FlowerConfig> {
  private static final Logger logger = LoggerFactory.getLogger(FlowerConfigParser.class);
  private static final String defaultConfigName = "flower.yml";

  @Override
  public FlowerConfig parse() {
    InputStream is = null;
    try {
      URL url = getClass().getClassLoader().getResource(defaultConfigName);
      if (url == null) {
        logger.warn("flower config can't be found.");
      } else {
        is = new FileInputStream(url.getPath());
        return new Yaml().loadAs(is, FlowerConfig.class);
      }
    } catch (FileNotFoundException e) {
      logger.error("fail to parse flower.yml", e);
    } finally {
      IOUtil.close(is);
    }
    return new FlowerConfig();
  }
}
