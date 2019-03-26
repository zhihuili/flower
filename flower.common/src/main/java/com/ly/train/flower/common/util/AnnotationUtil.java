/**
 * 
 */
package com.ly.train.flower.common.util;

import com.ly.train.flower.common.annotation.FlowerService;

/**
 * @author leeyazhou
 *
 */
public class AnnotationUtil {

  public static  String getFlowerServiceValue(Class<?> flowServiceClass) {
    String ret = flowServiceClass.getSimpleName();
    FlowerService flowerService = flowServiceClass.getAnnotation(FlowerService.class);
    if (flowerService != null && StringUtil.isNotBlank(flowerService.value())) {
      ret = flowerService.value();
    }
    return ret;
  }

}
