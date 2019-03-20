package com.seiyaya.nettydetail.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 反应器模式，用于解决多用户并发访问问题
 * @author 王佳
 * @created 2018年3月14日 上午10:03:41
 */
public class Reactor implements Runnable
{
    
    public Selector            selector;
    
    public ServerSocketChannel serverSocketChannel;
    
    public Reactor(int port) throws IOException
    {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", port);
        serverSocketChannel.socket().bind(inetSocketAddress);
        serverSocketChannel.configureBlocking(false);
        
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        //如果响应accept时间，则出发acceptor
        selectionKey.attach(new Acceptor(this));
    }
    
    @Override
    public void run()
    {
        try
        {
            while (Thread.interrupted())
            {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext())
                {
                    SelectionKey selectionKey = iterator.next();
                    dispatch(selectionKey);
                    selectedKeys.clear();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 运行绑定在accept或SocketReadhandler
     * @author 王佳
     * @created 2018年3月19日 下午2:03:22
     * @param selectionKey
     */
    private void dispatch(SelectionKey selectionKey)
    {
        Runnable r = (Runnable) selectionKey.attachment();
        if ( r != null )
        {
            r.run();
        }
    }
    
}
