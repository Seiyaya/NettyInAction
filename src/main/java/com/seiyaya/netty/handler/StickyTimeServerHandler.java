package com.seiyaya.netty.handler;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 没有考虑粘包的处理器
 * @author 王佳
 * @created 2018年2月24日 下午3:27:17
 */
public class StickyTimeServerHandler extends ChannelHandlerAdapter{
	
	private int counter;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"utf-8").substring(0, req.length - System.getProperty("line.separator").length());
		System.out.println("sever get req " + body + " counter:"+(++counter));
		
		String currentTime = "query time order".equals(body)?new Date().toString():"bad order";
		currentTime += System.getProperty("line.separator");
		ByteBuf writerBuf = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.writeAndFlush(writerBuf);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
