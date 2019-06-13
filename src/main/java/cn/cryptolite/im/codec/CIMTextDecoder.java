package cn.cryptolite.im.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class CIMTextDecoder extends ByteToMessageDecoder {
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    byte[] b = new byte[in.readableBytes()];
    in.readBytes(b);
    out.add(new String(b, Charset.forName("UTF-8")));
  }
}
