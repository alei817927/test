package cn.cryptolite.im.codec;

import cn.cryptolite.im.socketcustom.MessageBuilder;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class CIMProtobufDecoder extends ByteToMessageDecoder {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    /*
    // 防止socket字节流攻击, 防止客户端传来的数据过大, 因为太大的数据，是不合理的
    if (in.readableBytes() > 2048) {
      in.skipBytes(in.readableBytes());
    }
    */
    while (in.readableBytes() > 4) { // 如果可读长度小于包头长度，退出。
      in.markReaderIndex();
      // 获取包头中的body长度
      byte low = in.readByte();
      byte high = in.readByte();
      short s0 = (short) (low & 0xff);
      short s1 = (short) (high & 0xff);
      s1 <<= 8;
      short length = (short) (s0 | s1);

      // 获取包头中的protobuf类型
      in.readByte();
      byte dataType = in.readByte();

      // 如果可读长度小于body长度，恢复读指针，退出。
      if (in.readableBytes() < length) {
        in.resetReaderIndex();
        return;
      }

      // 读取body
      ByteBuf bodyByteBuf = in.readBytes(length);

      byte[] array;
      int offset;

      int readableLen = bodyByteBuf.readableBytes();
      if (bodyByteBuf.hasArray()) {
        array = bodyByteBuf.array();
        offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
      } else {
        array = new byte[readableLen];
        bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
        offset = 0;
      }

      //反序列化
      MessageLite result = MessageBuilder.buildMessage(dataType, array, offset, readableLen);
      System.out.println(Arrays.toString(result.toByteArray()));
      out.add(result);
    }
  }

  public MessageLite decodeBody(byte dataType, byte[] array, int offset, int length) throws Exception {
    /*
    if (dataType == 0x00) {
      return StockTickOuterClass.StockTick.getDefaultInstance().
          getParserForType().parseFrom(array, offset, length);

    } else if (dataType == 0x01) {
      return OptionTickOuterClass.OptionTick.getDefaultInstance().
          getParserForType().parseFrom(array, offset, length);
    }
*/
    return null; // or throw exception
  }
}
