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
package com.ly.train.flower.common.io.resource;

import java.io.IOException;
import java.net.URL;

/**
 * @author leeyazhou
 */
public abstract class AbstractResource implements Resource {
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

  @Override
  public void close() throws IOException {

  }

}
