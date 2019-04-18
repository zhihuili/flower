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
package com.ly.train.flower.common.akka.serializer.protostuff;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author lee
 */
public final class ProtobufUtils {

  private static ConcurrentMap<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

  private ProtobufUtils() {}

  @SuppressWarnings("unchecked")
  private static <T> Schema<T> getSchema(Class<T> cls) {
    Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
    if (schema == null) {
      schema = RuntimeSchema.getSchema(cls);
      if (schema != null) {
        Schema<T> temp = (Schema<T>) cachedSchema.putIfAbsent(cls, schema);
        if (temp != null) {
          schema = temp;
        }
      }
    }
    return schema;
  }

  public static <T> byte[] encode(T obj) {
    if (obj == null) {
      return null;
    }
    LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    try {
      @SuppressWarnings("unchecked")
      Schema<T> schema = (Schema<T>) getSchema(obj.getClass());
      return ProtobufIOUtil.toByteArray(obj, schema, buffer);
    } catch (Exception err) {
      throw new IllegalStateException(err.getMessage(), err);
    } finally {
      buffer.clear();
    }
  }

  public static <T> T decode(byte[] data, Class<T> cls) {
    if (data == null) {
      return null;
    }
    if (cls == null) {
      throw new IllegalStateException("class Name can't be null");
    }
    try {
      Schema<T> schema = getSchema(cls);
      T message = schema.newMessage();
      ProtobufIOUtil.mergeFrom(data, message, schema);
      return message;
    } catch (Exception err) {
      throw new IllegalStateException(err.getMessage(), err);
    }
  }

}
