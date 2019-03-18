/**
 * 
 */
package com.ly.train.flower.common.exception;

/**
 * @author leeyazhou
 *
 */
public class FlowerException extends RuntimeException {

  private static final long serialVersionUID = 6532232062131720108L;

  public FlowerException() {
    super();
  }

  public FlowerException(String message) {
    super(message);
  }

  public FlowerException(String message, Throwable cause) {
    super(message, cause);
  }

  public FlowerException(Throwable cause) {
    super(cause);
  }
}
