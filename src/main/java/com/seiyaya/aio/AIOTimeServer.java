package com.seiyaya.aio;

import com.seiyaya.aio.server.AsyncTimeServerHandler;

public class AIOTimeServer {
	
	public static void main(String[] args) {
		int port = 8080;
		new Thread(new AsyncTimeServerHandler(port),"server thread -01").start();
	}
}
