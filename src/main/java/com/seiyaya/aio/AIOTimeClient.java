package com.seiyaya.aio;

import com.seiyaya.aio.client.AsyncTimeClientHandler;

public class AIOTimeClient {
	public static void main(String[] args) {
		int port = 8080;
		new Thread(new AsyncTimeClientHandler("localhost",port),"client thread -01").start();;
	}
}
