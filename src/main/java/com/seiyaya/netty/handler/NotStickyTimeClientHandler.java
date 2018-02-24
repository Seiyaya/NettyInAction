package com.seiyaya.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 解决粘包问题的客户端处理器
 * @author 王佳
 * @created 2018年2月24日 下午4:07:23
 */
public class NotStickyTimeClientHandler extends ChannelHandlerAdapter {
	private int counter;

	private byte[] req;

	public NotStickyTimeClientHandler() {
		req = ("query time order"+System.getProperty("line.separator")).getBytes();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf msg = null;
		for (int i = 0; i < 100; i++) {
			msg = Unpooled.buffer(req.length);
			msg.writeBytes(req);
			ctx.writeAndFlush(msg);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//使用了StringDecoder，可以直接转换成string
		String body = (String) msg;
		System.out.println("client get response " + body + " counter:" + (++counter));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
