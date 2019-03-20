package com.ly.train.flower.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.ly.train.flower.logging.Logger;
import com.ly.train.flower.logging.LoggerFactory;

public class CloneUtil {
  private static final Logger logger = LoggerFactory.getLogger(CloneUtil.class);

  public static Object clone(Serializable obj) {
    Object clone = cloneObject(obj);
    if (clone == null) {
      clone = cloneObject(obj);
    }
    return clone;
  }

  public static Object cloneObject(Serializable obj) {
    Object anotherObj = null;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;

    ObjectInputStream ois = null;
    try {
      oos = new ObjectOutputStream(baos);
      oos.writeObject(obj);
      byte[] bytes = baos.toByteArray();

      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

      ois = new ObjectInputStream(bais);
      anotherObj = ois.readObject();
    } catch (IOException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    } catch (StackOverflowError error) {
      logger.error("", error);
      return null;
    } finally {
      if (oos != null)
        try {
          oos.close();
        } catch (IOException localIOException3) {
        }
      if (ois != null)
        try {
          ois.close();
        } catch (IOException localIOException4) {
        }
    }
    return anotherObj;
  }

  public static int getObjectSize(Serializable obj) {
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    ObjectOutputStream os = null;
    try {
      os = new ObjectOutputStream(bs);
      os.writeObject(obj);
      os.flush();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      try {
        bs.close();
        if (os != null)
          os.close();
      } catch (IOException e) {
      }
    }
    return bs.size();
  }
}
