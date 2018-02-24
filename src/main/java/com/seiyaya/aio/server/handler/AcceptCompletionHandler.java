package com.seiyaya.aio.server.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.seiyaya.aio.server.AsyncTimeServerHandler;

/**
 * 接受完成后或者失败的回调方法
 * @author 王佳
 * @created 2018年2月24日 上午11:12:47
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncTimeServerHandler> {

	@Override
	public void completed(AsynchronousSocketChannel result,AsyncTimeServerHandler attachment) {
		//当前的客户端已经脸上，但是还要等待别的客户端连接
		attachment.serverChannel.accept(attachment,this);
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		result.read(byteBuffer,byteBuffer,new ReadCompletionHandler(result));
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
		attachment.latch.countDown();
	}

}
