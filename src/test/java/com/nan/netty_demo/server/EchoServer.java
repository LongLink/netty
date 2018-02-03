package com.nan.netty_demo.server;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/*
 * ServerBootstrap引导绑定和启动服务器
 * EventLoopGroup来处理事件，如接受新连，接收数据，写数据等
 * InetSocketAddress，服务器监听此端口
 * childHandler 执行所有的连接请求
 * 都设置完毕了，最后调用ServerBootstrap.bind() 方法来绑定服务器
 */
public class EchoServer {

	private final int port;

	public EchoServer(int port) {
		this.port = port;
	}

	public void start() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();// 接收处理新连接请求
		try {
			// create ServerBootstrap instance
			ServerBootstrap b = new ServerBootstrap();
			// Specifies NIO transport, local socket address
			// Adds handler to channel pipeline
			b.group(group).channel(NioServerSocketChannel.class)
					// 通道类型
					.localAddress(new InetSocketAddress("127.0.0.1",port))
					.childHandler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast(new EchoServerHandler());
						}
					});
			// Binds server, waits for server to close, and releases resources
			ChannelFuture f = b.bind().sync();// sync()方法会阻塞直到服务器完成绑定
			System.out.println(EchoServer.class.getName()
					+ "started and listen on “" + f.channel().localAddress());
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully().sync();
		}
	}

	public static void main(String[] args) throws Exception {
		new EchoServer(65535).start();
	}

}
