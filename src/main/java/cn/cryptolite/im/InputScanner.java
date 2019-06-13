package cn.cryptolite.im;

import cn.cryptolite.im.proto.Message;
import io.netty.channel.Channel;

import java.util.Scanner;

public class InputScanner {
  private Channel channel;

  public InputScanner(Channel channel) {
    this.channel = channel;
  }

  public void start() {
    new Thread() {
      @Override
      public void run() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
          //利用nextXXX()方法输出内容
          String str = sc.next();
          if (channel == null || !channel.isWritable()) {
            System.out.println("不能发送消息");
            continue;
          }
          System.out.println("客户端说：" + str);
//          channel.writeAndFlush(str);
          Message.Test.Builder authMsg = Message.Test.newBuilder();
          authMsg.setCmd(Message.CommandType.CHAT);
          authMsg.setData(str);
          channel.writeAndFlush(authMsg.build());
        }
      }
    }.start();
  }
}
