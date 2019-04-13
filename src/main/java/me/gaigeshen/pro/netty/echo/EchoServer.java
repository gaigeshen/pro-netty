package me.gaigeshen.pro.netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author gaigeshen
 */
public class EchoServer {
  private final int port;

  private EchoServer(int port) {
    this.port = port;
  }

  public static void main(String[] args) throws Exception {
    int port;
    if (args.length != 1) {
      port = 8080;
    } else {
      port = Integer.parseInt(args[0]);
    }

    new EchoServer(port).start();
  }

  private void start() throws Exception {
    final EchoServerHandler serverHandler = new EchoServerHandler();
    EventLoopGroup group = new NioEventLoopGroup(); // 接受和处理新的连接
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(group).channel(NioServerSocketChannel.class).localAddress(port).childHandler(new ChannelInitializer<SocketChannel>() {
        protected void initChannel(SocketChannel ch) {
          ch.pipeline().addLast(serverHandler); // 我们可以总是使用同样的实例，因为该类被标注为可共享的
        }
      });
      ChannelFuture future = bootstrap.bind().sync();
      future.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }
}
