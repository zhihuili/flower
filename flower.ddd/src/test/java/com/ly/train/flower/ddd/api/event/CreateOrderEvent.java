package com.ly.train.flower.ddd.api.event;

import java.io.Serializable;

/**
 * @author leeyazhou
 */
public class CreateOrderEvent implements Serializable{

  private static final long serialVersionUID = 1L;
  private Long id;
  private String name;

  public CreateOrderEvent(Long id, String name) {
    this.id = id;
    this.name =name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


}
