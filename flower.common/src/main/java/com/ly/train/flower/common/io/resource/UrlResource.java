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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;

/**
 * @author leeyazhou
 */
public class UrlResource extends AbstractResource {
  private static final Logger logger = LoggerFactory.getLogger(UrlResource.class);

  public UrlResource(URL url) {
    super(url);
  }

  @Override
  public InputStream getInputStream() {
    try {
      return new FileInputStream(getPath());
    } catch (FileNotFoundException e) {
      logger.error("path : " + getPath() + ", name : " + getName(), e);
    }
    return null;
  }
}
