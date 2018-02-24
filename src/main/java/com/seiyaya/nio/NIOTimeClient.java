package com.seiyaya.nio;

import com.seiyaya.nio.client.MultiplexerTimeClient;

public class NIOTimeClient {
	
	public static void main(String[] args) {
		
		new Thread(new MultiplexerTimeClient("localhost",8080)).start();;
	}
}
