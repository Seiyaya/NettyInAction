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
		//���ܿͻ��˵�����  ��ʼ��channel����������·״̬֪ͨ��ChannelPipeline
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		/** �������紫��(SocketChannel)�Ķ�д
		 *  �첽��ȡͨ�ŶԶ˵����ݣ����Ͷ��¼���ChannelPipeline
		 *  �첽������Ϣ��ͨ�ŶԶˣ�����pipeline����Ϣ���ͽӿ�
		 *  ִ��ϵͳ����task
		 *  ִ�ж�ʱ���񣬱�����·����״̬��ⶨʱ����
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
	 * io�¼�������
	 * @author ����
	 * @created 2018��2��24�� ����2:48:24
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
