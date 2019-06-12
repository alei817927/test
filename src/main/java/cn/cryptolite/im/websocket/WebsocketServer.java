package cn.cryptolite.im.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class WebsocketServer {
  @Value("${websocket.port}")
  private int port;
  @Value("${idle.writerIdleTime}")
  private int writerIdleTime;
  @Value("${idle.allIdleTime}")
  private int readerIdleTime;
  @Value("${idle.readerIdleTime}")
  private int allIdleTime;

  private NioEventLoopGroup work = new NioEventLoopGroup();
  private NioEventLoopGroup boss = new NioEventLoopGroup();


  @PostConstruct
  public void startup() throws InterruptedException {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(boss, work)
        .channel(NioServerSocketChannel.class)
//        .handler(new LoggingHandler(LogLevel.DEBUG))
        .childHandler(new WebSocketChannelInitializer());
    bootstrap.bind(port).sync().channel().closeFuture().sync().channel();
  }

  @PreDestroy
  public void shutdown() {
    work.shutdownGracefully();
    boss.shutdownGracefully();
  }
}
