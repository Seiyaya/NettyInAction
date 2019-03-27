package com.seiyaya.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

/**
 *  volatile���α�֤��condition�����Ŀɼ���
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
	 *  ������֤��count���ڲ�ͬ�̵߳Ĳ��ɼ���
	 * @author Seiyaya
	 * @date 2019��3��27�� ����5:55:47
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
	 * ͨ��״̬��֤���̵߳Ĳ��ɼ���
	 * @author Seiyaya
	 * @date 2019��3��27�� ����5:56:23
	 * @throws InterruptedException
	 */
	private static void changeStatus() throws InterruptedException {
		RunThread thread = new RunThread(true);
		thread.start();
		log.info("�ı����߳�����");
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
			log.info("���߳̿�ʼ");
			while(condition) {
				
			}
			log.info("���߳̽���");
		}
		
		
		public void setRun(boolean condition) {
			this.condition = condition;
		}
	}
}
