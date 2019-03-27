package com.seiyaya.thread.chat;

public class ConsumerProducterTest {
	
	public static final String SYNC_METHOD = "0";
	
	public static final String LOCK_METHOD = "1";
	
	public static final String CHOOSE_METHOD = "1";
	
	public static void main(String[] args) {
		Product product = new Product("Ð¡Ã÷", "ÄÐ");
		new Thread(new Consumer(product)).start();
		new Thread(new Producter(product)).start();
	}
}
