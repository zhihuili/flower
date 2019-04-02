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
