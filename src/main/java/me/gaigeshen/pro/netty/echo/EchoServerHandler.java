package me.gaigeshen.pro.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author gaigeshen
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf in = (ByteBuf) msg;
    System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));

    // 为什么不使用简单通道入站处理器（SimpleChannelInboundHandler）
    // 因为此处需要将接收到的消息写出到客户端，而写出操作是异步的，当此方法（channelRead）返回时，有可能还未写完，
    // 该消息缓冲有可能就会被释放，所以推迟到读取完毕方法被调用时，使用写出并冲刷方法自动释放消息缓冲。
    ctx.write(in);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
            .addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
