package com.seiyaya.aio.server.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.seiyaya.aio.server.AsyncTimeServerHandler;

/**
 * ������ɺ����ʧ�ܵĻص�����
 * @author ����
 * @created 2018��2��24�� ����11:12:47
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncTimeServerHandler> {

	@Override
	public void completed(AsynchronousSocketChannel result,AsyncTimeServerHandler attachment) {
		//��ǰ�Ŀͻ����Ѿ����ϣ����ǻ�Ҫ�ȴ���Ŀͻ�������
		attachment.serverChannel.accept(attachment,this);
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		result.read(byteBuffer,byteBuffer,new ReadCompletionHandler(result));
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
		attachment.latch.countDown();
	}

}
