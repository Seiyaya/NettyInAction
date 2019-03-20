package com.seiyaya.nettydetail.reactor;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable
{
    
    private Reactor reactor;
    
    public Acceptor(Reactor reactor)
    {
        this.reactor = reactor;
    }

    @Override
    public void run()
    {
        try
        {
            SocketChannel clientChannel = reactor.serverSocketChannel.accept();
            if(clientChannel!=null) {
                new SocketReadHandler(reactor.selector,clientChannel);
            }
            
            Buffer buffer = ByteBuffer.allocate(1024);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
