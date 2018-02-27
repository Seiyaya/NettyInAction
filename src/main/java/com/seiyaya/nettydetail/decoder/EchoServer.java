package com.seiyaya.nettydetail.decoder;

import com.seiyaya.nettydetail.decoder.handler.EchoServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * ʹ��delimiter�����������ݽ��б���
 * ��$_��Ϊ�ָ���
 * @author ����
 * @created 2018��2��26�� ����1:59:40
 */
public class EchoServer
{
    
    public static void main(String[] args)
    {
        int port = 8080;
        new EchoServer().bind(port);
    }
    
    private void bind(int port)
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>()
                    {
                        
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception
                        {
//                            ʹ�÷ָ�������ճ������
//                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            
                            //�ñ������ᰴ��ָ���ĳ��Ƚ��н��룬����ǰ����Ϣ�Ỻ��֪���¸������Ϣ�ִ�
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            ChannelFuture future = boot.bind(port).sync();
            future.channel().closeFuture().sync();
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
