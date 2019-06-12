package cn.cryptolite.im.socket;

import cn.cryptolite.im.InputScanner;
import cn.cryptolite.im.proto.Message;
import cn.cryptolite.im.socket.handler.ClientHandler;
import cn.cryptolite.im.socket.handler.IdleHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SocketClient {
  private static int PORT = 8090;
  private static String HOST = "localhost";

  private static int READER_IDLE_TIME_SECONDS = 20;
  private static int WRITER_IDLE_TIME_SECONDS = 20;
  private static int ALL_IDLE_TIME_SECONDS = 40;

  private static Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);

  public static void main(String[] args) throws InterruptedException {
    NioEventLoopGroup group = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(group);
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast(new IdleStateHandler(READER_IDLE_TIME_SECONDS
            , WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS, TimeUnit.SECONDS));
        p.addLast(new IdleHandler());

        p.addLast(new ProtobufVarint32FrameDecoder());
        p.addLast(new ProtobufDecoder(Message.Test.getDefaultInstance()));

        p.addLast(new ProtobufVarint32LengthFieldPrepender());
        p.addLast(new ProtobufEncoder());

        p.addLast(new ClientHandler());
      }
    });
    bootstrap.remoteAddress(HOST, PORT);
    ChannelFuture f = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
//      final EventLoop eventLoop = futureListener.channel().eventLoop();
      if (!futureListener.isSuccess()) {
        LOGGER.warn("Failed to connect to server, try connect after 10s");
//        futureListener.channel().eventLoop().schedule();
        return;
      }
      new InputScanner(futureListener.channel()).start();
    });
    f.channel().closeFuture().sync();
    group.shutdownGracefully();
  }
}
