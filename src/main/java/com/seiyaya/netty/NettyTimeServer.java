package com.seiyaya.netty;


import org.junit.Test;

import com.seiyaya.netty.handler.NotStickyTimeServerHandler;

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
import io.netty.util.internal.SystemPropertyUtil;

public class NettyTimeServer {
	
	public static void main(String[] args) {
		int port = 8080;
		new NettyTimeServer().bind(port);
	}

	private void bind(int port) {
		//接受客户端的连接  初始化channel参数，将链路状态通知给ChannelPipeline
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		/** 进行网络传输(SocketChannel)的读写
		 *  异步读取通信对端的数据，发送读事件到ChannelPipeline
		 *  异步发送消息到通信对端，调用pipeline的消息发送接口
		 *  执行系统调用task
		 *  执行定时任务，比如链路空闲状态检测定时任务
		 */
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
			System.out.println(Thread.currentThread().getName()+" initalizer");
			
			//ch.pipeline().addLast(new StickyTimeServerHandler());
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new NotStickyTimeServerHandler());
		}
		
	}
	
	@Test
	public void test() {
	    boolean isOptimization = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
	    System.out.println(isOptimization);
	}
}
