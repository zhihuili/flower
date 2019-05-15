package com.ly.train.flower.common.io.resource;

import java.net.URL;

/**
 * @author leeyazhou
 */
public interface Resource {


  URL getURL();
  
  boolean isJarResource();
  
  String getPath();
  
  String getName();
}
