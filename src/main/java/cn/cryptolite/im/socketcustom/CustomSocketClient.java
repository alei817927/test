package cn.cryptolite.im.socketcustom;

import cn.cryptolite.im.InputScanner;
import cn.cryptolite.im.codec.CIMProtobufDecoder;
import cn.cryptolite.im.codec.CIMProtobufEncoder;
import cn.cryptolite.im.socket.handler.IdleHandler;
import cn.cryptolite.im.socketcustom.handler.CustomClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CustomSocketClient {
  private static int PORT = 8089;
  private static String HOST = "localhost";

  private static int READER_IDLE_TIME_SECONDS = 20;
  private static int WRITER_IDLE_TIME_SECONDS = 20;
  private static int ALL_IDLE_TIME_SECONDS = 40;

  private static Logger LOGGER = LoggerFactory.getLogger(CustomSocketClient.class);

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

        p.addLast(new CIMProtobufDecoder());
        p.addLast(new CIMProtobufEncoder());

        p.addLast(new CustomClientHandler());
      }
    });
    bootstrap.remoteAddress(HOST, PORT);
    ChannelFuture f = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
      if (!futureListener.isSuccess()) {
        LOGGER.error("Failed to connect to server, try connect after 10s");
        return;
      }
      new InputScanner(futureListener.channel()).start();
    });
    f.channel().closeFuture().sync();
    group.shutdownGracefully();
  }
}
