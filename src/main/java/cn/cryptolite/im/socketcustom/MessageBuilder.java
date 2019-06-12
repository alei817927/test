package cn.cryptolite.im.socketcustom;

import cn.cryptolite.im.proto.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import java.util.HashMap;
import java.util.Map;

public class MessageBuilder {
  private static Map<Byte, Parser<? extends MessageLite>> messages;
  private static Map<Class, Byte> messageTypes;

  static {
    messages = new HashMap<>();
    messages.put(MessageConstant.TEST, Message.Test.getDefaultInstance().getParserForType());

    messageTypes = new HashMap<>();
    messageTypes.put(Message.Test.class, MessageConstant.TEST);
  }

  public static Byte getMessageType(Class clazz) {
    return messageTypes.get(clazz);
  }

  public static MessageLite buildMessage(byte dataType, byte[] array, int offset, int length) throws InvalidProtocolBufferException {
    return messages.get(dataType).parseFrom(array, offset, length);
  }
}
