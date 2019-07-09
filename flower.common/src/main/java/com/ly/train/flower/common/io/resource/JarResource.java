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
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.IOUtil;

/**
 * @author leeyazhou
 */
public class JarResource extends AbstractResource {
  private static final Logger logger = LoggerFactory.getLogger(JarResource.class);

  public JarResource(URL url) {
    super(url);
  }

  @Override
  public InputStream getInputStream() {
    JarFile jarFile = null;
    try {
      jarFile = new JarFile(getPath());
      ZipEntry zipEntry = jarFile.getEntry(getName());
      return jarFile.getInputStream(zipEntry);
    } catch (IOException e) {
      logger.error("path : " + getPath() + ", name : " + getName(), e);
    } finally {
      IOUtil.close(jarFile);
    }

    return null;

  }

}
