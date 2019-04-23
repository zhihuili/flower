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
package com.ly.train.flower.common.serializer.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.ly.train.flower.common.serializer.Serializer;
import com.ly.train.flower.common.util.IOUtil;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

public class JdkSerializer implements Serializer {
  private static final Logger logger = LoggerFactory.getLogger(JdkSerializer.class);

  @Override
  public Object decode(byte[] data, String className) {
    ObjectInputStream objectIn = null;
    Object resultObject = null;
    try {
      objectIn = new ObjectInputStream(new ByteArrayInputStream(data));
      resultObject = objectIn.readObject();
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      if (null != objectIn) {
        IOUtil.close(objectIn);
      }
    }

    return resultObject;
  }

  @Override
  public byte[] encode(Object data) {
    ByteArrayOutputStream byteArray = null;
    ObjectOutputStream output = null;
    try {
      byteArray = new ByteArrayOutputStream();
      output = new ObjectOutputStream(byteArray);
      output.writeObject(data);
      output.flush();
    } catch (Exception e) {
      logger.error("", e);
      return null;
    } finally {
      if (null != output) {
        IOUtil.close(output);
      }
    }
    return byteArray.toByteArray();
  }

}
