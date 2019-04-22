package com.ly.train.flower.common.serializer.hessian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CollectionModel implements Serializable {
  private String name;
  private List<String> data = new ArrayList<String>();
  private Set<String> data2 = new HashSet<String>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getData() {
    return data;
  }

  public void setData(List<String> data) {
    this.data = data;
  }

  public Set<String> getData2() {
    return data2;
  }

  public void setData2(Set<String> data2) {
    this.data2 = data2;
  }
  
  
}
