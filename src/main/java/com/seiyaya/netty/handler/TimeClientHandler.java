package com.seiyaya.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter {

	private ByteBuf firstMessage;

	public TimeClientHandler() {
		byte[] req = "query time order".getBytes();
		firstMessage = Unpooled.buffer(req.length);
		firstMessage.writeBytes(req);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf respBuffer = (ByteBuf) msg;
		byte[] bytes = new byte[respBuffer.readableBytes()];
		respBuffer.readBytes(bytes);
		String body = new String(bytes, "utf-8");
		System.out.println("client get response " + body);
	}

	/**
	 * 建立连接成功后调用
	 * 将请求发送给服务端
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(firstMessage);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
