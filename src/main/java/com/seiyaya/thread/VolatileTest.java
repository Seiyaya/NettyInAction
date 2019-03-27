package com.seiyaya.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

/**
 *  volatile修饰保证了condition变量的可见性
 * @author Seiyaya
 *
 */
@Slf4j
public class VolatileTest {
	
	private static int count = 0;
	private static AtomicInteger atomicInteger = new AtomicInteger(0);
	
	
	public static void main(String[] args) throws InterruptedException {
		String key = "1";
		switch (key) {
		case "1":
			changeStatus();
			break;
		case "2":
			addCountMethod();
			break;
		default:
			break;
		}
	}

	/**
	 *  自增来证明count对于不同线程的不可见性
	 * @author Seiyaya
	 * @date 2019年3月27日 下午5:55:47
	 */
	private static void addCountMethod() {
		Runnable run = ()->{
			addCount();
		};
		
		for(int i=0;i<10;i++) {
			new Thread(run).start();
		}
	}

	/**
	 * 通过状态来证明线程的不可见性
	 * @author Seiyaya
	 * @date 2019年3月27日 下午5:56:23
	 * @throws InterruptedException
	 */
	private static void changeStatus() throws InterruptedException {
		RunThread thread = new RunThread(true);
		thread.start();
		log.info("改变子线程条件");
		TimeUnit.SECONDS.sleep(3);
		thread.setRun(false);
	}
	
	private static void addCount() {
		for(int i=0;i<1000;i++) {
			count++;
			atomicInteger.incrementAndGet();
		}
		log.info("Thread_Name{}-->count-{}-->atomic{}",Thread.currentThread(),count,atomicInteger.get());
	}

	static class RunThread extends Thread{
		
		private volatile boolean condition;
		
		public RunThread(boolean condition) {
			this.condition = condition;
		}
		
		@Override
		public void run() {
			log.info("子线程开始");
			while(condition) {
				
			}
			log.info("子线程结束");
		}
		
		
		public void setRun(boolean condition) {
			this.condition = condition;
		}
	}
}
