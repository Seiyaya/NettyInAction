package com.seiyaya.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;



public class MultiplexerTimeServer implements Runnable {

	private Selector selector;

	private ServerSocketChannel serverChannel;

	private volatile boolean stop;

	public MultiplexerTimeServer() {
		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress("localhost", 8080));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("time server init complete");
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {
		
		while(!stop) {
			try {
				selector.select(1000);
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> keysIt = keys.iterator();
				System.out.println(keys.size()+"  是否有返回 "+System.currentTimeMillis());
				while(keysIt.hasNext()) {
					SelectionKey key = keysIt.next();
					System.out.println("accept:"+key.isAcceptable()+"\t read "+key.isReadable()+"\twrite"+key.isWritable());
					keysIt.remove();
					try {
						handlerInput(key);
					} catch (Exception e) {
						if(key!=null) {
							key.cancel();
						}
						
						if(key.channel()!=null) {
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

	private void handlerInput(SelectionKey key) throws IOException {
		if(key.isValid()) {
			if(key.isAcceptable()) {
				//接受就绪，接受来自客户端的连接
				ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
				SocketChannel clientChannel = serverChannel.accept();
				clientChannel.configureBlocking(false);
				clientChannel.register(selector, SelectionKey.OP_READ);
			}
			
			if(key.isReadable()) {
				//读就绪，从客户端的管道中读取数据
				SocketChannel clientChnnel = (SocketChannel) key.channel();
				ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
				int readBytes = clientChnnel.read(byteBuffer);
				if(readBytes>0) {
					byteBuffer.flip();
					byte[] bytes = new byte[byteBuffer.remaining()];
					byteBuffer.get(bytes);
					String body = new String(bytes,"utf-8");
					System.out.println("accept client content "+ body);
					
					String currentTime = "query time order".equals(body)?new Date().toString():"bad order";
					doWrite(clientChnnel,currentTime);
				}else if(readBytes<0) {
					key.cancel();
					key.channel().close();
				}
			}
		}
	}

	/**
	 * 回写给客户端
	 * @author 王佳
	 * @created 2018年2月23日 下午6:51:58tag
	 * @param clientChnnel
	 * @param result
	 * @throws IOException 
	 */
	private void doWrite(SocketChannel clientChnnel, String result) throws IOException {
		if(result!=null && result.trim().length()>0) {
			byte[] bytes = result.getBytes();
			ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
			byteBuffer.put(bytes);
			byteBuffer.flip();
			clientChnnel.write(byteBuffer);
		}
	}

	public void stop() {
		stop = true;
	}

}
