package com.ly.train.flower.common.sample.web;

public class WasteTime {

  public synchronized void waste100() {
    try {
      wait(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
