/**
 * 
 */
package com.ly.train.flower.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author leeyazhou
 *
 */
public class IOUtil {
  private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

  public static void close(InputStream is) {
    if (is != null) {
      try {
        is.close();
      } catch (IOException e) {
        logger.error("", e);
      }
    }
  }

  public static void close(Closeable x) {
    if (x != null) {
      try {
        x.close();
      } catch (Exception e) {
        logger.error("", e);
      }
    }
  }
}
