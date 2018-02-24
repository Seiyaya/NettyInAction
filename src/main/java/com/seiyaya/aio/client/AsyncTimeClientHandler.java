package com.seiyaya.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>,Runnable{

	private AsynchronousSocketChannel clientChannel;
	private String host;
	private int port;
	/**
	 * ��ֹ����˻�û����Ӧ���ͻ��˾��Ѿ��˳�
	 */
	private CountDownLatch latch;
	
	public AsyncTimeClientHandler(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			clientChannel = AsynchronousSocketChannel.open();
		} catch (Exception e) {
		}
	}

	@Override
	public void completed(Void result, AsyncTimeClientHandler attachment) {
		byte[] req = "query time order".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		clientChannel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				System.out.println("cleint send " + new String(attachment.array()));
				if(attachment.hasRemaining()) {
					clientChannel.write(attachment,attachment,this);
				}else {
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					//�ͻ��˷��ͳɹ��󣬶�ȡ����˷��ص���Ϣ
					clientChannel.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {

						@Override
						public void completed(Integer result, ByteBuffer attachment) {
							attachment.flip();
							byte[] bytes = new byte[readBuffer.remaining()];
							readBuffer.get(bytes);
							System.out.println("client get response "+ new String(bytes));
							latch.countDown();
						}

						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							try {
								clientChannel.close();
								latch.countDown();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				try {
					clientChannel.close();
					latch.countDown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
		try {
			clientChannel.close();
			latch.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		latch = new CountDownLatch(1);
		clientChannel.connect(new InetSocketAddress(host, port),this,this);
		try {
			latch.await();
		} catch (Exception e) {
		}
		
		try {
			clientChannel.close();
		} catch (Exception e) {
		}
	}

}
