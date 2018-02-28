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
 * nettyʵ��websocket����
 * @author ����
 * @created 2018��2��28�� ����6:18:57
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
                        //��Ӵ�����
                        
                        //��������Ϣ���б����
                        ch.pipeline().addLast("http-codec" , new HttpServerCodec());
                        
                        //��http�����ϳ�һ����Ϣ
                        ch.pipeline().addLast("aggregator" , new HttpObjectAggregator(65536));
                        
                        //��ͻ��˷���h5�ļ�
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
