package com.seiyaya.aio.server.handler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

/**
 * 读取完成或者失败的回调方法
 * @author 王佳
 * @created 2018年2月24日 上午11:13:32
 */
public class ReadCompletionHandler implements CompletionHandler<Integer,ByteBuffer> {

	private AsynchronousSocketChannel channel;
	
	public ReadCompletionHandler(AsynchronousSocketChannel result) {
		if(result!=null) {
			this.channel = result;
		}
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		String req;
		try {
			req = new String(body,"utf-8");
			System.out.println("server accept message "+new String(req));
			String currentTime = "query time order".equals(req)?new Date().toString():"bad order";
			doWrite(currentTime);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void doWrite(String currentTime) {
		if(currentTime!=null && !"".equals(currentTime.trim())) {
			byte[] bytes = currentTime.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer,ByteBuffer>() {

				@Override
				public void completed(Integer result, ByteBuffer attachment) {
					if(attachment.hasRemaining()) {
						channel.write(attachment,attachment,this);
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try {
						channel.close();
					} catch (Exception e) {
					}
				}
			});
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			channel.close();
		} catch (Exception e) {
		}
	}


}
