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
public class ActorContextCommand implements Command {

  private static final long serialVersionUID = 1L;
  private CommandType commandType = CommandType.GET_CONTEXT;
  private MessageType messagetype = MessageType.REQUEST;

  @Override
  public CommandType getCommandType() {
    return commandType;
  }

  @Override
  public MessageType getMessageType() {
    return messagetype;
  }
}
