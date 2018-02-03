package com.nan.netty_demo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	/*
	 * 客户端连接服务器后被调用
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.write(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
	}

	/*
	 * 从服务器接收到数据后调用
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		System.out.println("Client received: "
				+ ByteBufUtil.hexDump(msg.readBytes(msg.readableBytes())));
	}

	/*
	 * 发生异常时被调用
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
