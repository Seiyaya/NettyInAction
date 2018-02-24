package com.seiyaya.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * ���ճ������Ŀͻ��˴�����
 * @author ����
 * @created 2018��2��24�� ����4:07:23
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
		//ʹ����StringDecoder������ֱ��ת����string
		String body = (String) msg;
		System.out.println("client get response " + body + " counter:" + (++counter));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
