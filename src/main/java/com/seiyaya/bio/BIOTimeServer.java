package com.seiyaya.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.seiyaya.bio.handler.BIOTimeServerHandler;

/**
 * 一个客户端接入对应一个线程
 * 可以改为线程池+阻塞队列的实现，不会因为接入太多导致崩溃
 * @author 王佳
 * @created 2018年2月23日 下午6:01:29
 */
public class BIOTimeServer {
	
	public static void main(String[] args) {
		int port = 8080;
		ServerSocket server = null;
		try {
			server = new ServerSocket(8080);
			System.out.println("---初始化time server完成,端口为:"+port);
			
			Socket socket;
			while(true) {
				socket = server.accept();
				new Thread(new BIOTimeServerHandler(socket)).start();
			}
		} catch (Exception e) {
		}finally {
			if(server!=null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				server = null;
			}
		}
	}
}
