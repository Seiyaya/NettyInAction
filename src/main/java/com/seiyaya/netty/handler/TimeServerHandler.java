package com.seiyaya.netty.handler;



import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 对网络事件进行读写操作
 * @author 王佳
 * @created 2018年2月24日 下午2:54:48
 */
public class TimeServerHandler extends ChannelHandlerAdapter{
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buffer = (ByteBuf) msg;
		byte[] req = new byte[buffer.readableBytes()];
		buffer.readBytes(req);
		String body = new String(req,"utf-8");
		System.out.println("server accpet message "+ body);
		String currentTime = "query time order".equals(body)?new Date().toString():"bad order";
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.write(resp);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//将消息发送队列中的消息写入到SocketChannel发送给客户端
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
