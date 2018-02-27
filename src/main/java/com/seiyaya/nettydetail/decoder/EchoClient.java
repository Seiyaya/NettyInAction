package com.seiyaya.nettydetail.decoder;

import com.seiyaya.nettydetail.decoder.handler.EchoClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient
{
    public static void main(String[] args)
    {
        int port = 8080;
        EventLoopGroup group = new NioEventLoopGroup();
        try
        {
            Bootstrap boot = new Bootstrap();
            boot.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>()
                    {
                        
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception
                        {
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            
            ChannelFuture future = boot.connect("localhost", port).sync();
            future.channel().closeFuture().sync();
        }
        catch (Exception e)
        {
        }
        finally
        {
            group.shutdownGracefully();
        }
    }
}
