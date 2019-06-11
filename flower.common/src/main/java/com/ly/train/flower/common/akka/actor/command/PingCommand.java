package com.ly.train.flower.common.akka.actor.command;

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
