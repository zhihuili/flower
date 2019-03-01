package com.ly.train.flower.common.service.containe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ly.train.flower.common.service.web.Web;

public class ServiceContext {
  private Map<Object, Object> map = new ConcurrentHashMap<Object, Object>();
  private Web web;

  public Web getWeb() {
    return web;
  }

  public void setWeb(Web web) {
    this.web = web;
  }

  public void put(Object key, Object value) {
    map.put(key, value);
  }

  public Object get(Object key) {
    return map.get(key);
  }
  
  public void remove(Object key) {
    map.remove(key);
  }
}
