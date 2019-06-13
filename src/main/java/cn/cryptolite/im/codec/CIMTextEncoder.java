package cn.cryptolite.im.codec;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class CIMTextEncoder  extends MessageToByteEncoder<MessageLite> {

  @Override
  protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {
    System.out.println(msg);
    out.writeBytes(msg.toByteArray());
  }
}
