package com.nan.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TimeServer {
	public void bind(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();//服务端接受客户端连接
		EventLoopGroup workerGroup = new NioEventLoopGroup();//进行SocketChannel网络读写
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)//设置Channel
					//.option(ChannelOption.SO_BACKLOG, 1024)// LineBasedFrameDecoder解析器
					.option(ChannelOption.SO_BACKLOG, 100)
					.handler(new LoggingHandler(LogLevel.INFO))
					//.childHandler(new ChildChannelHandler());
					//.childHandler(new DelimiterBasedFrameDecoderHandler());//绑定I/O事件处理类
					.childHandler(new FixedLengthFrameDecoderHandler());

			ChannelFuture f = b.bind(port).sync();//绑定监听端口,等待绑定完成

			f.channel().closeFuture().sync();//进行阻塞，等待服务端链路关闭
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	private class FixedLengthFrameDecoderHandler extends ChannelInitializer<SocketChannel>{
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new EchoServerHandler());
		}
		
	}
	
	
	//
	private class DelimiterBasedFrameDecoderHandler extends ChannelInitializer<SocketChannel>{
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//			ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new EchoServerHandler());
		}
	}

	//
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));//处理粘包 遍历ByteBuf中的可读字节，判断是否有“\n”或“\r\n”,如果有，就以此为结束位置，从可读索引到结束位置区间字节组成一行。
			ch.pipeline().addLast(new StringDecoder());//处理粘包 将收到的对象转成字符串，然后继续调用后面的Handler
			ch.pipeline().addLast(new TimeServerHandler());
		}
	}

	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {

			}
		}
		new TimeServer().bind(port);
	}
}
