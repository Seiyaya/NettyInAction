package com.seiyaya.nettydetail.http;

import com.seiyaya.nettydetail.http.handler.HttpFileServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 
 * @author 王佳
 * @created 2018年2月27日 下午5:07:27
 */
public class HttpFileServer
{
    public static final String DEFAULT_PATH = "/src/main/java/com/seiyaya/nettydetail/";
    
    public void run(final int port, final String url) throws Exception
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(bossGroup,workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>()
            {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception
                {
                    ch.pipeline().addLast("http-decoder" , new HttpRequestDecoder());
                    //将多个请求转换为单一的HttpFullRequest和HttpFullResponse
                    ch.pipeline().addLast("http-aggregator" , new HttpObjectAggregator(65536));
                    ch.pipeline().addLast("http-encoder" , new HttpResponseEncoder());
                    
                    //支持异步发送最大码流
                    ch.pipeline().addLast("http-chunked" , new ChunkedWriteHandler());
                    ch.pipeline().addLast("fileServerHandler" , new HttpFileServerHandler(url));
                }
            });
        
        ChannelFuture future = boot.bind("localhost", port).sync();
        System.out.println("http 文件目录服务器启动");
        future.channel().closeFuture().sync();
        
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
    
    public static void main(String[] args) throws Exception
    {
        new HttpFileServer().run(8080, DEFAULT_PATH);
    }
}
