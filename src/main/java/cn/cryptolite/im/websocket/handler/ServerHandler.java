package cn.cryptolite.im.websocket.handler;

import cn.cryptolite.im.proto.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    System.out.println("userEventTriggered, " + evt.getClass().getName());
//    super.userEventTriggered(ctx, evt);
    if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
      WebSocketServerProtocolHandler.HandshakeComplete handshake = (WebSocketServerProtocolHandler.HandshakeComplete) evt;

      //http request header
      HttpHeaders headers = handshake.requestHeaders();

      //http request uri: /chat?accesskey=hello
      String uri = handshake.requestUri();
      String token = headers.get("token");
      System.out.println("==============token=" + token + ",uri=" + uri);
    }
    ctx.fireUserEventTriggered(evt);
  }

  /**
   * 客户端与服务端会话连接成功
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    System.out.println("客户端与服务端会话连接成功");
  }

  /**
   * 服务端接收到客户端消息
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof BinaryWebSocketFrame) {
      Message.Test test = (Message.Test) msg;
      System.out.println("客户端：" + test.getData());
      return;
    }

    System.out.println("==================" + msg.getClass().getName() + " start==============");
    System.out.println(msg);
    System.out.println("==================" + msg.getClass().getSimpleName() + " finish==============");

    if (msg instanceof TextWebSocketFrame) {
      String _msg = ((TextWebSocketFrame) msg).text();
      String _reverseMsg = new StringBuilder(_msg).reverse().toString();
      System.out.println("客户端：" + _msg);
      System.out.println("服务器：" + _reverseMsg);
      ctx.channel().writeAndFlush(new TextWebSocketFrame(_reverseMsg));
    } else if (msg instanceof FullHttpRequest) {
      FullHttpRequest request = (FullHttpRequest) msg;
      WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://" + request.headers().get(HttpHeaders.Names.HOST), null, false);
      WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
      handshaker.handshake(ctx.channel(), request);
    }

  }

  /**
   * 服务端监听到客户端异常
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    System.out.println("服务端监听到客户端异常");
    System.out.println(cause);
  }

  /**
   * 客户端与服务端会话连接断开
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    System.out.println("客户端与服务端会话连接断开");
  }

}
