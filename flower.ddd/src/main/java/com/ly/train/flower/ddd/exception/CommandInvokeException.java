package com.ly.train.flower.ddd.exception;

import com.ly.train.flower.common.exception.FlowerException;

/**
 * @author leeyazhou
 */
public class CommandInvokeException extends FlowerException{


  private static final long serialVersionUID = 1L;

  public CommandInvokeException() {
    super();
  }

  public CommandInvokeException(String message) {
    super(message);
  }

  public CommandInvokeException(String message, Throwable cause) {
    super(message, cause);
  }

  public CommandInvokeException(Throwable cause) {
    super(cause);
  }

  @Override
  public void printStackTrace() {
    super.printStackTrace();
  }

}
