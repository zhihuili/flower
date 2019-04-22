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
