package com.ly.train.flower.common.service;

import java.util.HashSet;
import java.util.Set;

public class JointService implements Service, Joint {

  int sourceNumber = 0;
  Set<Object> result = new HashSet<Object>();

  @Override
  public Object process(Object message) {
    result.add(message);
    if (--sourceNumber <= 0)
      return result;
    return null;
  }

  @Override
  // sourceNumber++ when initialize
  public void sourceNumberPlus() {
    sourceNumber++;
  }

}
