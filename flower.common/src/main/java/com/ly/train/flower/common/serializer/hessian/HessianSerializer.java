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
package com.ly.train.flower.common.serializer.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.ly.train.flower.common.serializer.Serializer;
import com.ly.train.flower.common.util.ClassUtil;
import com.ly.train.flower.common.util.IOUtil;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

/**
 * @author lee
 */
public class HessianSerializer implements Serializer {
  static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

  @Override
  public byte[] encode(Object data) {
    if (data == null) {
      return null;
    }
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Hessian2Output out = new Hessian2Output(bos);
    try {
      out.startMessage();
      out.writeObject(data);
      out.completeMessage();
    } catch (IOException e) {
      logger.error("", e);
    } finally {
      IOUtil.close(bos);
      close(out);
    }
    return bos.toByteArray();
  }

  @Override
  public Object decode(byte[] data, String className) {
    if (data == null) {
      return null;
    }
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    Hessian2Input in = new Hessian2Input(bin);
    Object ret = null;
    try {
      in.startMessage();
      Class<?> expectedClass = ClassUtil.forName(className);
      ret = in.readObject(expectedClass);
      in.completeMessage();
    } catch (Throwable e) {
      logger.error("className : " + className, e);
    } finally {
      IOUtil.close(bin);
      close(in);
    }
    return ret;
  }

  private void close(Hessian2Input in) {
    try {
      in.close();
    } catch (IOException e) {
      logger.error("", e);
    }
  }

  private void close(Hessian2Output in) {
    try {
      in.close();
    } catch (IOException e) {
      logger.error("", e);
    }
  }
}
