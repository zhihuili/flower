package com.ly.train.flower.ddd.api.command;

import java.io.Serializable;
import com.ly.train.flower.ddd.annotation.TargetAggregateIdentifier;

/**
 * @author leeyazhou
 */
public class CreateOrderCommand implements Serializable {

  private static final long serialVersionUID = 1L;
  @TargetAggregateIdentifier
  private Long id;
  private String name;

  /**
   * 
   */
  public CreateOrderCommand(Long id, String name) {
    this.id = id;
    this.name = name;
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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CreateOrderCommand [id=");
    builder.append(id);
    builder.append(", name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }


}
