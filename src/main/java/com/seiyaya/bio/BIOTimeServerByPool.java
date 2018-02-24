package com.seiyaya.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.seiyaya.bio.handler.BIOTimeServerHandler;
import com.seiyaya.bio.pool.TimeServerHandlerExecutePool;

public class BIOTimeServerByPool {
	public static void main(String[] args) {
		int port = 8080;
		ServerSocket server = null;
		try {
			server = new ServerSocket(8080);
			System.out.println("---初始化time server完成,端口为:" + port);

			Socket socket;
			TimeServerHandlerExecutePool pool = new TimeServerHandlerExecutePool(50,1000);
			while (true) {
				socket = server.accept();
				pool.execute(new BIOTimeServerHandler(socket));
			}
		} catch (Exception e) {
		} finally {
			if (server != null) {
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