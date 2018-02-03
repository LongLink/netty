package com.nan.netty_demo.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	/**
	 *这个方法在任何时候都会被调用来接收数据
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("Server received: " + msg);
		ctx.write(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(
				ChannelFutureListener.CLOSE);
	}

	/**
	 * exceptionCaught 方法可以捕获服务器的异常，比如客户端连接服务器后强制关闭，服务器会抛出"客户端主机强制关闭错误"，通过重写exceptionCaught 方法就可以处理异常
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}