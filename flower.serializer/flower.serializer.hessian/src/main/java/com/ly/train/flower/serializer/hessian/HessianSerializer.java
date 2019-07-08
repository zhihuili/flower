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
package com.ly.train.flower.serializer.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.alibaba.com.caucho.hessian.io.HessianInput;
import com.alibaba.com.caucho.hessian.io.HessianOutput;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;
import com.ly.train.flower.common.logging.Logger;
import com.ly.train.flower.common.logging.LoggerFactory;
import com.ly.train.flower.common.util.ClassUtil;
import com.ly.train.flower.common.util.IOUtil;
import com.ly.train.flower.serializer.Serializer;

/**
 * @author lee
 */
public class HessianSerializer implements Serializer {
  static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);
  private final SerializerFactory factory = new SerializerFactory();

  @Override
  public byte[] encode(Object data) {
    if (data == null) {
      return null;
    }
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    HessianOutput out = new HessianOutput(bos);
    out.setSerializerFactory(factory);
    try {
      out.writeObject(data);
      out.flush();
    } catch (IOException e) {
      logger.error("", e);
    } finally {
      IOUtil.close(bos);
    }
    return bos.toByteArray();
  }

  @Override
  public Object decode(byte[] data, String className) {
    if (data == null) {
      return null;
    }
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    HessianInput in = new HessianInput(bin);
    in.setSerializerFactory(factory);
    Object ret = null;
    try {
      Class<?> expectedClass = ClassUtil.forName(className);
      ret = in.readObject(expectedClass);
    } catch (Throwable e) {
      logger.error("className : " + className, e);
    } finally {
      IOUtil.close(bin);
    }
    return ret;
  }


}
