package com.ly.train.flower.ddd.api.command;

import java.io.Serializable;

/**
 * @author leeyazhou
 */
public class SelectOrderCommand implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long id;

  /**
   * 
   */
  public SelectOrderCommand(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }



}
