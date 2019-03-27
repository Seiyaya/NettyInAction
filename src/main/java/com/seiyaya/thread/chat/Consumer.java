package com.seiyaya.thread.chat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Consumer implements Runnable {

	private Product product;

	public Consumer(Product product) {
		this.product = product;
	}

	@Override
	public void run() {
		while (true) {
			if (ConsumerProducterTest.CHOOSE_METHOD.equals(ConsumerProducterTest.SYNC_METHOD)) {
				syncMethod();
			} else {
				lockMethod();
			}

		}
	}

	/**
	 * lock实现线程间通信
	 * 
	 * @author Seiyaya
	 * @date 2019年3月27日 下午12:47:34
	 */
	private void lockMethod() {
		while (true) {
			try {
				product.getLock().lock();
				if (product.isConsume()) {
					// 已消费则等待
					try {
						product.getCondition().await();
					} catch (InterruptedException e) {
						log.error("", e);
					}
				}
				log.info("{}-->{}", product.getName(), product.getSex());
				// 已消费 等待生产者再次生产
				product.setConsume(true);
				product.getCondition().signal();
			} finally {
				if (product.getLock() != null) {
					product.getLock().unlock();
				}
			}
		}
	}

	/**
	 * 同步关键字实现线程间通信
	 * 
	 * @author Seiyaya
	 * @date 2019年3月27日 下午12:32:48
	 */
	private void syncMethod() {
		synchronized (product) {
			if (product.isConsume()) {
				// 已消费则等待
				try {
					product.wait();
				} catch (InterruptedException e) {
					log.error("", e);
				}
			}
			log.info("{}-->{}", product.getName(), product.getSex());
			// 已消费 等待生产者再次生产
			product.setConsume(true);
			product.notify();
		}
	}

}
