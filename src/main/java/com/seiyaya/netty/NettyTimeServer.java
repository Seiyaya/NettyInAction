package com.seiyaya.netty;


import com.seiyaya.netty.handler.NotStickyTimeServerHandler;
import com.seiyaya.netty.handler.StickyTimeServerHandler;
import com.seiyaya.netty.handler.TimeServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class NettyTimeServer {
	
	public static void main(String[] args) {
		int port = 8080;
		new NettyTimeServer().bind(port);
	}

	private void bind(int port) {
		//接受客户端的连接
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		//进行网络传输(SocketChannel)的读写
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap boot = new ServerBootstrap();
			boot.group(bossGroup,workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childHandler(new ChildChannelHandler());
			ChannelFuture future = boot.bind(port).sync();
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	/**
	 * io事件处理类
	 * @author 王佳
	 * @created 2018年2月24日 下午2:48:24
	 */
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			//ch.pipeline().addLast(new TimeServerHandler());
			
			
			//ch.pipeline().addLast(new StickyTimeServerHandler());
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new NotStickyTimeServerHandler());
		}
		
	}
}
