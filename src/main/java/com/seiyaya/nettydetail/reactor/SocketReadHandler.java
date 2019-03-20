package com.seiyaya.nettydetail.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SocketReadHandler implements Runnable
{
    private SocketChannel clientChannel;

    public SocketReadHandler(Selector selector, SocketChannel clientChannel)
    {
        this.clientChannel = clientChannel;
        try
        {
            this.clientChannel.configureBlocking(false);
            SelectionKey selectionKey = clientChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            
            selectionKey.attach(this);
            
            selectionKey.interestOps(SelectionKey.OP_READ);
            selector.wakeup();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }

    @Override
    public void run()
    {
        ByteBuffer inputBuffers =  ByteBuffer.allocate(1024);
        inputBuffers.clear();
        try
        {
            clientChannel.read(inputBuffers);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
