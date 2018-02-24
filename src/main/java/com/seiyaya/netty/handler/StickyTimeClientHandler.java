package com.seiyaya.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class StickyTimeClientHandler extends ChannelHandlerAdapter{
	
	private int counter;
	
	private byte[] req;
	
	
	public StickyTimeClientHandler() {
		req = ("query time order"+System.getProperty("line.separator")).getBytes();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf msg = null;
		for(int i=0;i<100;i++) {
			msg = Unpooled.buffer(req.length);
			msg.writeBytes(req);
			ctx.writeAndFlush(msg);
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] resp = new byte[buf.readableBytes()];
		buf.readBytes(resp);
		System.out.println("client get response " + new String(resp,"utf-8")+" counter:"+(++counter));
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
