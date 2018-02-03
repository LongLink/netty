package com.nan.http;

import com.nan.protobuf.SubReqServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {
	private static final String DEFAULT_URL = "d:/";

	public void run(final int port, final String url) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
					ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));//将多个消息转换为单一的FullHTTPRequest或者FullHTTPResponse
					ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
					ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());//支持异步发送大的码流，但不占用过多内存
					ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(DEFAULT_URL));
				}
			});
			ChannelFuture future = b.bind("127.0.0.1", port).sync();
			System.out.println();
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
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
		String url = DEFAULT_URL;
		if (args.length > 1)
			url = args[1];
		new HttpFileServer().run(port, url);
	}
}
