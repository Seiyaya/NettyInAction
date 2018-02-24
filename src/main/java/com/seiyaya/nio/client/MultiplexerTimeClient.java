package com.seiyaya.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeClient implements Runnable{

	private String host;
	private int port;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean stop;
	
	public MultiplexerTimeClient(String host , int port) {
		this.host = host;
		this.port = port;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			
			System.out.println("the client is completed,connect server "+host+":"+port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			doConnect();
		} catch (Exception e) {
			System.out.println("---connect fail");
		}
		
		while(!stop) {
			try {
				selector.select(1000);
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				SelectionKey key;
				while(iterator.hasNext()) {
					key = iterator.next();
					iterator.remove();
					try {
						handleInput(key);
					} catch (Exception e) {
						if(key!=null && key.channel()!=null) {
							key.cancel();
							key.channel().close();
						}
					}
				}
			} catch (Exception e) {
			}
		}
		
		if(selector!=null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if(key.isValid()) {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			if(key.isConnectable()) {
				//已经连接上了服务器，服务端返回了ack信息 
				if(socketChannel.finishConnect()) {
					socketChannel.register(selector, SelectionKey.OP_READ);
					doWrite(socketChannel);
				}
			}
			
			if(key.isReadable()) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
				int readBytes = socketChannel.read(byteBuffer);
				if(readBytes>0) {
					byteBuffer.flip();
					byte[] bytes = new byte[byteBuffer.remaining()];
					byteBuffer.get(bytes);
					String body = new String(bytes,"utf-8");
					System.out.println("now is :" + body);
					stop=true;
				}else if(readBytes<0) {
					key.cancel();
					key.channel().close();
				}
			}
		}
	}

	private void doWrite(SocketChannel key) throws IOException {
		byte[] req = "query time order".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		socketChannel.write(writeBuffer);
		if(!writeBuffer.hasRemaining()) {
			System.out.println("send order 2 server success");
		}
	}

	private void doConnect() throws ClosedChannelException, IOException {
		if(socketChannel.connect(new InetSocketAddress(host, port))) {
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		}else {
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

}
