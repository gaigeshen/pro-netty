package me.gaigeshen.pro.netty.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author gaigeshen
 */
public class EchoClient {
  private final String host;
  private final int port;

  private EchoClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public static void main(String[] args) throws Exception {
    String host;
    int port;
    if (args.length != 2) {
      host = "127.0.0.1";
      port = 8080;
    } else {
      host = args[0];
      port = Integer.parseInt(args[1]);
    }
    new EchoClient(host, port).start();
  }

  private void start() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(host, port).handler(new ChannelInitializer<SocketChannel>() {
        protected void initChannel(SocketChannel ch) {
          ch.pipeline().addLast(new EchoClientHandler()); // 这里每次都是新的实例
        }
      });
      ChannelFuture future = bootstrap.connect().sync();
      future.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }
}
