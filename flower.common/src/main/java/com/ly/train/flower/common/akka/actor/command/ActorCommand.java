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
package com.ly.train.flower.common.akka.actor.command;

import java.io.Serializable;

/**
 * @author leeyazhou
 */
public class ActorCommand implements Command, Serializable {

  private static final long serialVersionUID = 1L;
  private String serviceName;
  private int index;

  private CommandType commandType = CommandType.CREATE_ACTOR;
  private MessageType messageType = MessageType.REQUEST;
  private String data = "PING";

  public ActorCommand(String serviceName, int index) {
    this.serviceName = serviceName;
    this.index = index;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Override
  public CommandType getCommandType() {
    return commandType;
  }

  @Override
  public MessageType getMessageType() {
    return messageType;
  }

  /**
   * @param messageType the messageType to set
   */
  public void setMessageType(MessageType messageType) {
    this.messageType = messageType;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public void setCommandType(CommandType commandType) {
    this.commandType = commandType;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CreateCommand [serviceName=");
    builder.append(serviceName);
    builder.append(", index=");
    builder.append(index);
    builder.append("]");
    return builder.toString();
  }


}
