package com.seiyaya.nettydetail.decoder.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * delimiter���õ����������Դ������
 * @author ����
 * @created 2018��2��26�� ����2:01:01
 */
public class EchoServerHandler extends ChannelHandlerAdapter
{
    
    int counter = 0;
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        String body = (String) msg;
        System.out.println("server " + ++counter + " accpet client connect ,msg " + body);
        body += "$_";
        
        //���ͻ��˷��͵����ݷ���
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(echo);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }
}
