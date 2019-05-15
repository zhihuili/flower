package com.ly.train.flower.common.io;

import java.net.URL;

/**
 * @author leeyazhou
 */
public class AbstractResource implements Resource {

  private URL url;

  /**
   * 文件路径
   */
  private String path;

  /**
   * 文件名
   */
  private String name;

  private boolean jarResource;

  /**
   * 
   */
  public AbstractResource(URL url) {
    this.url = url;
  }

  @Override
  public URL getURL() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public void setUrl(URL url) {
    this.url = url;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isJarResource() {
    return jarResource;
  }

  public void setJarResource(boolean jarResource) {
    this.jarResource = jarResource;
  }

  public URL getUrl() {
    return url;
  }



}
