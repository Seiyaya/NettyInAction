package com.seiyaya.nettydetail.http.handler;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{
    
    private String               url;
    
    /**
     * 不安全的uri
     */
    private static final Pattern INSECURE_URI      = Pattern.compile(".*[<>&\"].*");
    
    /**
     * 允许访问的文件名称
     */
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    
    public HttpFileServerHandler(String url)
    {
        this.url = url;
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception
    {
        //请求是否成功
        if ( !request.getDecoderResult().isSuccess() )
        {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        
        //是否是get请求
        if ( request.getMethod() != HttpMethod.GET )
        {
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        
        final String uri = request.getUri();
        
        final String path = sanitizeUri(uri);
        //路径是否有权限
        if ( path == null )
        {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        
        File file = new File(path);
        //文件是否存在
        if ( file.isHidden() || !file.exists() )
        {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        
        /**
         * 如果请求的是一个目录且想要访问目录下的内容(包含/)返回目录下的文件列表
         * 否则响应为重定向到该目录
         */
        if ( file.isDirectory() )
        {
            if ( uri.endsWith("/") )
            {
                sendListing(ctx, file);
            }
            else
            {
                sendRedirect(ctx, uri + "/");
            }
        }
        
        //文件类型是否是文件夹
        if ( !file.isFile() )
        {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        
        RandomAccessFile randomAccessFile = null;
        try
        {
            randomAccessFile = new RandomAccessFile(file, "r");
        }
        catch (Exception e)
        {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        
        long fileLen = randomAccessFile.length();
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        
        //设置相应内容的长度
        HttpHeaders.setContentLength(response, fileLen);
        
        //设置响应内容的类型
        setContentTypeHeader(response, file);
        if ( HttpHeaders.isKeepAlive(request) )
        {
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        
        ctx.write(response);
        ChannelFuture sendFileFuture;
        sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLen, 8192), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener()
        {
            
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception
            {
                if ( total < 0 )
                {
                    System.out.println("trans progress " + progress);
                }
                else
                {
                    System.out.println("trans progress " + progress + "/" + total);
                }
            }
            
            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception
            {
                System.out.println("transform complete");
            }
            
        });
        
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        
        if ( !HttpHeaders.isKeepAlive(request) )
        {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    /**
     * 发送重定向到下一级目录
     * @author 王佳
     * @created 2018年2月28日 下午5:11:05
     * @param ctx
     * @param path  目录的地址
     */
    private void sendRedirect(ChannelHandlerContext ctx, String path)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, path);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     * 发送文件列表
     * @author 王佳
     * @created 2018年2月28日 下午5:10:58
     * @param ctx
     * @param file
     */
    private void sendListing(ChannelHandlerContext ctx, File file)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");
        StringBuilder buf = new StringBuilder();
        String dirPath = file.getPath();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append(" 目录：");
        buf.append("</title></head><body>\r\n");
        buf.append("<h3>");
        buf.append(dirPath).append(" 目录：");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>链接：<a href=\"../\">..</a></li>\r\n");
        
        //显示文件夹下的所有文件
        for (File f : file.listFiles())
        {
            if ( f.isHidden() || !f.canRead() )
            {
                continue;
            }
            String name = f.getName();
            if ( !ALLOWED_FILE_NAME.matcher(name).matches() )
            {
                continue;
            }
            buf.append("<li>链接：<a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     * 设置内容相应头(Content-Type)
     * @author 王佳
     * @created 2018年2月28日 上午11:26:40
     * @param response
     * @param file
     */
    private void setContentTypeHeader(HttpResponse response, File file)
    {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, mimeTypesMap.getContentType(file));
        System.out.println("文件的类型是:" + mimeTypesMap.getContentType(file));
    }
    
    /**
     * 对uri进行编码
     * @author 王佳
     * @created 2018年2月28日 下午5:21:49
     * @param uri
     * @return
     */
    private String sanitizeUri(String uri)
    {
        try
        {
            uri = URLDecoder.decode(uri, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            try
            {
                uri = URLDecoder.decode(uri, "ios-8859-1");
            }
            catch (Exception e2)
            {
                throw new Error();
            }
            e.printStackTrace();
        }
        if ( !uri.startsWith(url) )
        {
            return null;
        }
        
        if ( !uri.startsWith("/") )
        {
            return null;
        }
        
        uri = uri.replace('/', File.separatorChar);
        if ( uri.contains('.' + File.separator) || uri.startsWith(".") || uri.endsWith(".")
                || INSECURE_URI.matcher(uri).matches() )
        {
            return null;
        }
        return System.getProperty("user.dir") + File.separator + uri;
    }
    
    /**
     * 响应异常的状态码
     * @author 王佳
     * @created 2018年2月28日 下午5:19:15
     * @param ctx
     * @param status
     */
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
}
