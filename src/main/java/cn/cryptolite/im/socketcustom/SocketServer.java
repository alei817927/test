package cn.cryptolite.im.socketcustom;

import cn.cryptolite.im.codec.CIMProtobufDecoder;
import cn.cryptolite.im.codec.CIMProtobufEncoder;
import cn.cryptolite.im.socket.handler.IdleHandler;
import cn.cryptolite.im.socket.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
public class SocketServer {
  @Value("${socketcustom.port}")
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
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {

            ChannelPipeline p = ch.pipeline();

            p.addLast(new IdleStateHandler(readerIdleTime
                , writerIdleTime, allIdleTime, TimeUnit.SECONDS));
            p.addLast(new IdleHandler());

            p.addLast(new CIMProtobufDecoder());
            p.addLast(new CIMProtobufEncoder());

            p.addLast(new ServerHandler());
          }
        });
    bootstrap.bind(port).sync().channel().closeFuture().sync().channel();
  }

  @PreDestroy
  public void shutdown() {
    work.shutdownGracefully();
    boss.shutdownGracefully();
  }
}
