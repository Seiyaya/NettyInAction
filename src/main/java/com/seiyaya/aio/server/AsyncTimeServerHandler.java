package com.seiyaya.aio.server;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

import com.seiyaya.aio.server.handler.AcceptCompletionHandler;

public class AsyncTimeServerHandler implements Runnable{

	private int port;
	/**
	 * 完成其它操作之前当前线程都会阻塞
	 */
	public CountDownLatch latch;
	public AsynchronousServerSocketChannel serverChannel;
	
	public AsyncTimeServerHandler(int port) {
		this.port = port;
		try {
			serverChannel = AsynchronousServerSocketChannel.open();
			serverChannel.bind(new InetSocketAddress("localhost", port));
			System.out.println("the server complete init");
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {
		latch = new CountDownLatch(1);
		doAccept();
		try {
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doAccept() {
		serverChannel.accept(this,new AcceptCompletionHandler());
	}

}
