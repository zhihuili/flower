/**
 * 
 */
package com.ly.train.flower.config.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import org.yaml.snakeyaml.Yaml;
import com.ly.train.flower.config.FlowerConfig;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class FlowerConfigParser implements ConfigParser<FlowerConfig> {
  private static final Logger logger = LoggerFactory.getLogger(FlowerConfigParser.class);
  private static final String defaultConfigName = "flower.yml";

  @Override
  public FlowerConfig parse() {
    try {
      URL url = getClass().getClassLoader().getResource(defaultConfigName);
      if (url == null) {
        logger.warn("flower config can't be found.");
      } else {
        return new Yaml().loadAs(new FileInputStream(url.getPath()), FlowerConfig.class);
      }
    } catch (FileNotFoundException e) {
      logger.error("fail to parse flower.yml", e);
    }
    return new FlowerConfig();
  }


}
