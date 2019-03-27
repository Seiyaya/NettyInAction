package com.seiyaya.thread.chat;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;

@Data
public class Product {
	
	private String name;
	private String sex;
	/**
	  * 表示是否已经消费
	 */
	private boolean isConsume = false;
	
	/**
	  * 用lock实现线程间通信
	 */
	private Lock lock = new ReentrantLock();
	
	private Condition condition = lock.newCondition();

	public Product(String name, String sex) {
		super();
		this.name = name;
		this.sex = sex;
	}
	
	
}
