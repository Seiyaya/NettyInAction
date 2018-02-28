package com.seiyaya.nettydetail.websocket;

import com.seiyaya.nettydetail.websocket.handler.WebSocketServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * netty实现websocket服务
 * @author 王佳
 * @created 2018年2月28日 下午6:18:57
 */
public class WebSocketServer
{
    public static void main(String[] args)
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try
        {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>()
                {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception
                    {
                        //添加处理器
                        
                        //将请求消息进行编解码
                        ch.pipeline().addLast("http-codec" , new HttpServerCodec());
                        
                        //将http多个组合成一个消息
                        ch.pipeline().addLast("aggregator" , new HttpObjectAggregator(65536));
                        
                        //向客户端发送h5文件
                        ch.pipeline().addLast("http-chunked" , new ChunkedWriteHandler());
                        ch.pipeline().addLast("handler"  , new WebSocketServerHandler());
                    }
                });
            
            Channel channel = boot.bind("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        }
        catch (Exception e)
        {
        }
        finally 
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
