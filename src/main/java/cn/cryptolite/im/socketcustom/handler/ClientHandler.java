package cn.cryptolite.im.socketcustom.handler;

import cn.cryptolite.im.proto.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends ChannelInboundHandlerAdapter {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  // 连接成功后，向server发送消息
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    /*
    Message.Test.Builder authMsg = Message.Test.newBuilder();
    authMsg.setCmd(Message.CommandType.AUTH);
    authMsg.setData("This is auth data, " + ctx.name());

    ctx.writeAndFlush(authMsg.build());
    */
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.debug("连接断开 ");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Message.Test test = (Message.Test) msg;
    System.out.println("服务器说：" + test.getData());
  /*
    ctx.writeAndFlush(
        Message.Test.newBuilder()
            .setCmd(Message.CommandType.CHAT)
            .setData("This is upload data")
            .build()
    );
*/
  }
}
