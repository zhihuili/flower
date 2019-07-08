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

import java.io.PrintWriter;
import java.io.StringWriter;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;

/**
 * @author lee
 */
public class ExceptionUtil {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionUtil.class);

  /**
   * 获取异常信息的string
   * 
   * @param err err
   * @return str
   */
  public static String getErrorMessage(Exception err) {
    try {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      // pw.append("\r\n");
      err.printStackTrace(pw);
      pw.close();
      return sw.toString();
    } catch (Exception e2) {
      logger.error("", e2);
    }
    return null;
  }

  public static String getErrorMessage(Throwable err) {
    try {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      // pw.append("\r\n");
      err.printStackTrace(pw);
      pw.close();
      return sw.toString();
    } catch (Exception e2) {
      logger.error("", e2);
    }
    return null;
  }
}
