package com.seiyaya.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import com.seiyaya.nio.server.MultiplexerTimeServer;
import com.seiyaya.nio.task.ReactorTask;

public class NIOTimeServer {
	public static void main(String[] args) throws Exception {
		/**
		 * 1.打开ServerSocketChannel用来监听客户端的连接
		 * 2.绑定端口,设置成非阻塞模式
		 * 3.创建Reactor线程和多路复用器selector
		 * 4.将serverSocketChannel注册到selector上
		 * 5.在多路复用器轮询准备就绪的key
		 * 6.多路复用器监听客户端的接入
		 * 7.设置客户端连接为非阻塞模式
		 * 8.将客户端连接注册到reactor线程上的多路复用器
		 * 9.异步读取请求消息到缓冲区
		 * 10.对缓冲区内容编解码，将消息封装为task，线程池对task处理
		 * 11.服务端回送消息
		 */
		new Thread(new MultiplexerTimeServer()).start();;
	}
}
