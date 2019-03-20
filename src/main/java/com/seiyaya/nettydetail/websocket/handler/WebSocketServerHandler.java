package com.seiyaya.nettydetail.websocket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object>
{

    private WebSocketServerHandshaker handshaker = null;
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        //http接入,第一次握手是通过http协议，会有Upgrade请求头
        if( msg instanceof FullHttpRequest)
        {
            handleHttpRequest(ctx ,(FullHttpRequest) msg);
        }
        else if(msg instanceof WebSocketFrame)
        {
            //websocket接入
            handleWebsocketFrame(ctx , (WebSocketFrame)msg);
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    /**
     * 处理websocket请求
     * @author 王佳
     * @created 2018年3月2日 下午6:02:22
     * @param ctx
     * @param frame
     */
    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame)
    {
        //关闭链路命令
        if( frame instanceof CloseWebSocketFrame)
        {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
        }
        
        //ping命令
        if( frame instanceof PingWebSocketFrame)
        {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }
        
        //对与非文本消息
        if(! (frame instanceof TextWebSocketFrame))
        {
            throw new RuntimeException("不支持文本类型");
        }
        
        String request = ((TextWebSocketFrame)frame).text();
        ctx.channel().write(request + "，使用websocket服务");
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req)
    {
        //http解码失败，返回http异常
        if(!req.getDecoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade"))))
        {
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1 , HttpResponseStatus.BAD_REQUEST));
            return ;
        }
        
        //握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("http://localhost:8080/websocket", null , false);
        handshaker = wsFactory.newHandshaker(req);
        if(handshaker == null)
        {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        }
        else
        {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 发送http响应
     * @author 王佳
     * @created 2018年3月1日 下午5:55:30
     * @param ctx
     * @param req
     * @param response
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req,FullHttpResponse resp)
    {
        if(resp.getStatus().code() != 200)
        {
            ByteBuf buf = Unpooled.copiedBuffer(resp.getStatus().toString() , CharsetUtil.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(resp, resp.content().readableBytes());
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }
    
}
