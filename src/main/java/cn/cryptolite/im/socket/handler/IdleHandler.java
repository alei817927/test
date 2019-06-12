package cn.cryptolite.im.socket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 连接空闲Handler
 */
@Component
public class IdleHandler extends ChannelInboundHandlerAdapter {
  public Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent event = (IdleStateEvent) evt;
      String type = "";
      if (event.state() == IdleState.READER_IDLE) {
        type = "read idle";
      } else if (event.state() == IdleState.WRITER_IDLE) {
        type = "write idle";
      } else if (event.state() == IdleState.ALL_IDLE) {
        type = "all idle";
      }
      log.debug(ctx.channel().remoteAddress() + "超时类型：" + type);
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}
