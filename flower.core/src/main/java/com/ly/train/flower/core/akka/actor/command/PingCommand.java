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
package com.ly.train.flower.core.akka.actor.command;

/**
 * @author leeyazhou
 */
public class PingCommand implements Command {
  private static final long serialVersionUID = 1L;
  private CommandType commandType = CommandType.HEART_BEAT;
  private MessageType messageType = MessageType.REQUEST;
  private String text = "PING";

  @Override
  public CommandType getCommandType() {
    return commandType;
  }

  @Override
  public MessageType getMessageType() {
    return messageType;
  }


  /**
   * @param commandType the commandType to set
   */
  public void setCommandType(CommandType commandType) {
    this.commandType = commandType;
  }

  /**
   * @param messageType the messageType to set
   */
  public void setMessageType(MessageType messageType) {
    this.messageType = messageType;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PingCommand [commandType=");
    builder.append(commandType);
    builder.append(", messageType=");
    builder.append(messageType);
    builder.append(", text=");
    builder.append(text);
    builder.append("]");
    return builder.toString();
  }
}
