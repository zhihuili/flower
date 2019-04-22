package com.ly.train.flower.common.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ly.train.flower.common.serializer.Serializer;

public class KryoSerializer implements Serializer {

  @Override
  public Object decode(byte[] data, String className) {
    Input input = new Input(data);
    try {
      return getKryo().readClassAndObject(input);
    } finally {
      input.close();
    }
  }

  @Override
  public byte[] encode(Object data) {
    Output output = new Output(256, -1);
    try {
      getKryo().writeClassAndObject(output, data);
      return output.toBytes();
    } finally {
      output.flush();
      output.close();
    }
  }

  private static final ThreadLocal<Kryo> kryosLocal = new ThreadLocal<Kryo>() {
    protected Kryo initialValue() {
      Kryo kryo = new Kryo();
      kryo.setRegistrationRequired(false);
      kryo.setReferences(true);
      return kryo;
    }
  };

  public Kryo getKryo() {
    return kryosLocal.get();
  }
}
