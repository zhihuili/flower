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

import com.ly.train.flower.common.akka.actor.message.Message;
import com.ly.train.flower.common.serializer.Codec;
import com.ly.train.flower.common.util.StringUtil;

public class FlowMessage implements Message {
  private static final long serialVersionUID = 1L;
  private String transactionId = StringUtil.uuid();
  private byte[] message;
  private String messageType;
  private int codec = Codec.Hessian.getCode();
  private boolean error;

  private String exception;

  public FlowMessage() {
    this(null);
  }


  public FlowMessage(byte[] message) {
    this.message = message;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public byte[] getMessage() {
    return message;
  }

  public void setMessage(byte[] result) {
    this.message = result;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public boolean isError() {
    return error;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.error = Boolean.TRUE;
    this.exception = exception;
  }

  public String getMessageType() {
    return messageType;
  }


  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public void setCodec(int codec) {
    this.codec = codec;
  }

  public int getCodec() {
    return codec;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
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
