/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.ddd.config;

import java.io.Serializable;

/**
 * @author leeyazhou
 */
public class EventMessage implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Object message;

  public EventMessage() {}

  public EventMessage(Object message) {
    this.message = message;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(Object message) {
    this.message = message;
  }

  /**
   * @return the message
   */
  public Object getMessage() {
    return message;
  }

  public static EventMessage asEventMessage(Object message) {
    return new EventMessage(message);
  }

}
