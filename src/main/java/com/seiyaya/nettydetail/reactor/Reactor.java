package com.seiyaya.nettydetail.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * ��Ӧ��ģʽ�����ڽ�����û�������������
 * @author ����
 * @created 2018��3��14�� ����10:03:41
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
        
        //�����Ӧacceptʱ�䣬�����acceptor
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
     * ���а���accept��SocketReadhandler
     * @author ����
     * @created 2018��3��19�� ����2:03:22
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
