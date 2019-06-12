package cn.cryptolite.im.socketcustom.handler;

import cn.cryptolite.im.proto.Message;
import cn.cryptolite.im.proto.Message.Test;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * see: https://www.cnblogs.com/lemon-flm/p/7813914.html
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//    System.out.println("channelRead");

    Test test = (Test) msg;
    String value = test.getData();
    String reverseValue = new StringBuilder(value).reverse().toString();
    System.out.println("客户端说：" + value);
    System.out.println("服务器说：" + reverseValue);
    ctx.writeAndFlush(
        Test.newBuilder()
            .setCmd(Message.CommandType.CHAT)
            .setData(reverseValue)
            .build()
    );
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channelActive    ");
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    System.out.println("userEventTriggered," + evt.getClass().getSimpleName());
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channelReadComplete");
    super.channelReadComplete(ctx);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channelRegistered");
    super.channelRegistered(ctx);
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channelUnregistered");
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channelInactive");
  }
}
