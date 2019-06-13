package cn.cryptolite.im.codec;

import cn.cryptolite.im.socketcustom.MessageBuilder;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Arrays;

/**
 * body长度（low）	body长度（high）	保留字节	类型
 * 参考：https://www.cnblogs.com/Binhua-Liu/p/5577622.html
 */
@ChannelHandler.Sharable
public class CIMProtobufEncoder extends MessageToByteEncoder<MessageLite> {
  @Override
  protected void encode(
      ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {
    byte[] body = msg.toByteArray();
    System.out.println(Arrays.toString(body));
    byte[] header = encodeHeader(msg, (short) body.length);
    out.writeBytes(header);
    out.writeBytes(body);
  }

  private byte[] encodeHeader(MessageLite msg, short bodyLength) {
    byte messageType = MessageBuilder.getMessageType(msg.getClass());
    /*
    byte messageType = 0x0f;
    if (msg.getClass().getSimpleName() instanceof StockTickOuterClass.StockTick) {
      messageType = 0x00;
    } else if (msg instanceof OptionTickOuterClass.OptionTick) {
      messageType = 0x01;
    }
    */
    byte[] header = new byte[4];
    header[0] = (byte) (bodyLength & 0xff);
    header[1] = (byte) ((bodyLength >> 8) & 0xff);
    header[2] = 0; // 保留字段
    header[3] = messageType;
    return header;
  }
}
