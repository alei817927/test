package cn.cryptolite.im.socket;

import cn.cryptolite.im.codec.CIMTextDecoder;
import cn.cryptolite.im.codec.CIMTextEncoder;
import cn.cryptolite.im.proto.Message;
import cn.cryptolite.im.socket.handler.IdleHandler;
import cn.cryptolite.im.socket.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

//@Component
public class SocketServer {
  @Value("${socket.port}")
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
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {

            ChannelPipeline p = ch.pipeline();

            p.addLast(new IdleStateHandler(readerIdleTime
                , writerIdleTime, allIdleTime, TimeUnit.SECONDS));
            p.addLast(new LoggingHandler(LogLevel.INFO));
            p.addLast(new IdleHandler());

//            p.addLast(new ProtobufDecoder(Message.Test.getDefaultInstance()));
//            p.addLast(new ProtobufEncoder());
            p.addLast(new CIMTextDecoder());
            p.addLast(new CIMTextEncoder());

/*
            p.addLast(new ProtobufVarint32FrameDecoder());
            p.addLast(new ProtobufDecoder(Message.Test.getDefaultInstance()));

            p.addLast(new ProtobufVarint32LengthFieldPrepender());
            p.addLast(new ProtobufEncoder());
*/
            p.addLast(new LoggingHandler(LogLevel.INFO));
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
