package com.nan.netty_demo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
/**
 * 
 * 创建Bootstrap 对象用来引导启动客户端
 * 创建EventLoopGroup 对象并设置到Bootstrap 中，EventLoopGroup 可以理解为是一个线程池，这个线程池用来处理连接、接受数据、发送数据
 * 创建InetSocketAddress 并设置到Bootstrap 中，InetSocketAddress 是指定连接的服务器地址
 * 添加一个ChannelHandler，客户端成功连接服务器后就会被执行
 * 调用Bootstrap.connect()来连接服务器
 * 最后关闭EventLoopGroup 来释放资源
 *
 */
public class EchoClient {

	private final String host;
	private final int port;

	public EchoClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void start() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.remoteAddress(new InetSocketAddress(host, port))
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast(new EchoClientHandler());
						}
					});
			ChannelFuture f = b.connect().sync();
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully().sync();
		}
	}

	public static void main(String[] args) throws Exception {
		new EchoClient("127.0.0.1", 65535).start();
	}
}
