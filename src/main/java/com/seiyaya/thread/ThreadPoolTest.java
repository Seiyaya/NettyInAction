package com.seiyaya.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadPoolTest {
	
	public static void main(String[] args) {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.SECONDS,new LinkedBlockingQueue<>());
		
		for(int i=0;i<30;i++) {
			executor.execute(()->{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error("",e);
				}
				log.info("name-->{}",Thread.currentThread().getName());
			});
		}
	}
}
