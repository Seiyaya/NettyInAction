package com.seiyaya.netty;

import com.seiyaya.netty.handler.NotStickyTimeClientHandler;
import com.seiyaya.netty.handler.StickyTimeClientHandler;
import com.seiyaya.netty.handler.StickyTimeServerHandler;
import com.seiyaya.netty.handler.TimeClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class NettyTimeClient {
	public static void main(String[] args) {
		int port = 8080;
		new NettyTimeClient().connect(port, "localhost");
	}

	private void connect(int port, String host) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap boot = new Bootstrap();
			boot.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							//ch.pipeline().addLast(new TimeClientHandler());
							
							//ch.pipeline().addLast(new StickyTimeClientHandler());
							ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
							ch.pipeline().addLast(new StringDecoder());
							ch.pipeline().addLast(new NotStickyTimeClientHandler());
						}
					});

			ChannelFuture future = boot.connect(host, port).sync();
			future.channel().closeFuture().sync();
		} catch (Exception e) {
		} finally {
			group.shutdownGracefully();
		}
	}
}
