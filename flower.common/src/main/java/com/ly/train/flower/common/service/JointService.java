package com.ly.train.flower.common.service;

import com.ly.train.flower.common.service.message.FlowMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JointService implements Service, Joint {

  int sourceNumber = 0;
  Map<String, Set<Object>> resultMap = new HashMap <String, Set<Object>>();
  Map<String, Integer> resultNumberMap = new HashMap <String, Integer>();

  @Override
  public Object process(Object message) {

    FlowMessage flowMessage = (FlowMessage)message;
    if (!resultMap.containsKey(flowMessage.getTransactionId())) {
      Set<Object> objectSet = new HashSet<Object>();
      resultMap.put(flowMessage.getTransactionId(), objectSet);
      resultNumberMap.put(flowMessage.getTransactionId(), sourceNumber);
    }
    resultMap.get(flowMessage.getTransactionId()).add(((FlowMessage) message).getMessage());

    Integer integer = resultNumberMap.get(flowMessage.getTransactionId()) - 1;
    resultNumberMap.put(flowMessage.getTransactionId(), integer);
    if (integer <= 0) {
      Set<Object> returnObject = resultMap.get(flowMessage.getTransactionId());
      resultMap.remove(flowMessage.getTransactionId());
      resultNumberMap.remove(flowMessage.getTransactionId());

      return returnObject;
    }
    //TODO resultNumberMap memory leak
    return null;
  }

  @Override
  // sourceNumber++ when initialize
  public void sourceNumberPlus() {
    sourceNumber++;
  }

}
