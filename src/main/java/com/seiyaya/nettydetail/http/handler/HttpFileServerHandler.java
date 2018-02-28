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
     * ����ȫ��uri
     */
    private static final Pattern INSECURE_URI      = Pattern.compile(".*[<>&\"].*");
    
    /**
     * ������ʵ��ļ�����
     */
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    
    public HttpFileServerHandler(String url)
    {
        this.url = url;
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception
    {
        //�����Ƿ�ɹ�
        if ( !request.getDecoderResult().isSuccess() )
        {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        
        //�Ƿ���get����
        if ( request.getMethod() != HttpMethod.GET )
        {
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        
        final String uri = request.getUri();
        
        final String path = sanitizeUri(uri);
        //·���Ƿ���Ȩ��
        if ( path == null )
        {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        
        File file = new File(path);
        //�ļ��Ƿ����
        if ( file.isHidden() || !file.exists() )
        {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        
        /**
         * ����������һ��Ŀ¼����Ҫ����Ŀ¼�µ�����(����/)����Ŀ¼�µ��ļ��б�
         * ������ӦΪ�ض��򵽸�Ŀ¼
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
        
        //�ļ������Ƿ����ļ���
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
        
        //������Ӧ���ݵĳ���
        HttpHeaders.setContentLength(response, fileLen);
        
        //������Ӧ���ݵ�����
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
     * �����ض�����һ��Ŀ¼
     * @author ����
     * @created 2018��2��28�� ����5:11:05
     * @param ctx
     * @param path  Ŀ¼�ĵ�ַ
     */
    private void sendRedirect(ChannelHandlerContext ctx, String path)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, path);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     * �����ļ��б�
     * @author ����
     * @created 2018��2��28�� ����5:10:58
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
        buf.append(" Ŀ¼��");
        buf.append("</title></head><body>\r\n");
        buf.append("<h3>");
        buf.append(dirPath).append(" Ŀ¼��");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>���ӣ�<a href=\"../\">..</a></li>\r\n");
        
        //��ʾ�ļ����µ������ļ�
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
            buf.append("<li>���ӣ�<a href=\"");
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
     * ����������Ӧͷ(Content-Type)
     * @author ����
     * @created 2018��2��28�� ����11:26:40
     * @param response
     * @param file
     */
    private void setContentTypeHeader(HttpResponse response, File file)
    {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, mimeTypesMap.getContentType(file));
        System.out.println("�ļ���������:" + mimeTypesMap.getContentType(file));
    }
    
    /**
     * ��uri���б���
     * @author ����
     * @created 2018��2��28�� ����5:21:49
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
     * ��Ӧ�쳣��״̬��
     * @author ����
     * @created 2018��2��28�� ����5:19:15
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
