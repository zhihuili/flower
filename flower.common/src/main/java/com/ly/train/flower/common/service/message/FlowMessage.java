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
package com.ly.train.flower.common.service.message;

import java.io.Serializable;
import java.util.UUID;

public class FlowMessage implements Serializable {
  private static final long serialVersionUID = 1L;
  private String transactionId;
  private Object message;

  public FlowMessage() {
    this.transactionId = UUID.randomUUID().toString().replaceAll("-", "");
  }

  public FlowMessage(Object message) {
    this();
    this.message = message;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public Object getMessage() {
    return message;
  }

  public void setMessage(Object message) {
    this.message = message;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FlowMessage [transactionId=");
    builder.append(transactionId);
    builder.append(", message=");
    builder.append(message);
    builder.append("]");
    return builder.toString();
  }

}
