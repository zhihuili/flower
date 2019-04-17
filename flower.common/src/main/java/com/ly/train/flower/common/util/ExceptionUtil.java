
package com.ly.train.flower.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

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
