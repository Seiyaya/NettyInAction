package com.seiyaya.nettydetail.websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object>
{

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if( msg instanceof FullHttpRequest)
        {
            //
            handleHttpRequest(ctx ,(FullHttpRequest) msg);
        }
        else if(msg instanceof WebSocketFrame)
        {
            handleWebsocketFrame(ctx , (WebSocketFrame)msg);
        }
    }

    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame msg)
    {
        
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg)
    {
        
    }
    
    
}
