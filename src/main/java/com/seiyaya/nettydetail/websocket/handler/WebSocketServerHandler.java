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
        //http����,��һ��������ͨ��httpЭ�飬����Upgrade����ͷ
        if( msg instanceof FullHttpRequest)
        {
            handleHttpRequest(ctx ,(FullHttpRequest) msg);
        }
        else if(msg instanceof WebSocketFrame)
        {
            //websocket����
            handleWebsocketFrame(ctx , (WebSocketFrame)msg);
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    /**
     * ����websocket����
     * @author ����
     * @created 2018��3��2�� ����6:02:22
     * @param ctx
     * @param frame
     */
    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame)
    {
        //�ر���·����
        if( frame instanceof CloseWebSocketFrame)
        {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
        }
        
        //ping����
        if( frame instanceof PingWebSocketFrame)
        {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }
        
        //������ı���Ϣ
        if(! (frame instanceof TextWebSocketFrame))
        {
            throw new RuntimeException("��֧���ı�����");
        }
        
        String request = ((TextWebSocketFrame)frame).text();
        ctx.channel().write(request + "��ʹ��websocket����");
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req)
    {
        //http����ʧ�ܣ�����http�쳣
        if(!req.getDecoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade"))))
        {
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1 , HttpResponseStatus.BAD_REQUEST));
            return ;
        }
        
        //������Ӧ����
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
     * ����http��Ӧ
     * @author ����
     * @created 2018��3��1�� ����5:55:30
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
