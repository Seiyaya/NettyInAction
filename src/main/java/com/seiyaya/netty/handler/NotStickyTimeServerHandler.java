package com.seiyaya.netty.handler;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 解决了粘包问题的服务端处理器
 * @author 王佳
 * @created 2018年2月24日 下午4:10:19
 */
public class NotStickyTimeServerHandler extends ChannelHandlerAdapter {
	private int counter;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body =  (String) msg;
		System.out.println("sever get req " + body + " counter:" + (++counter));

		String currentTime = "query time order".equals(body) ? new Date().toString() : "bad order";
		currentTime += System.getProperty("line.separator");
		ByteBuf writerBuf = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.writeAndFlush(writerBuf);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
