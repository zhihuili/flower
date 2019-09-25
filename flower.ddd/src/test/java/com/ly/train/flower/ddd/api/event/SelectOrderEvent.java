package com.ly.train.flower.ddd.api.event;

import java.io.Serializable;

/**
 * @author leeyazhou
 */
public class SelectOrderEvent implements Serializable{

  private static final long serialVersionUID = 1L;
  private Long id;
  public SelectOrderEvent(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


}
