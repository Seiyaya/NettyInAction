package com.seiyaya.thread.chat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Producter implements Runnable {

	private Product product;

	public Producter(Product product) {
		this.product = product;
	}

	@Override
	public void run() {
		if (ConsumerProducterTest.CHOOSE_METHOD.equals(ConsumerProducterTest.SYNC_METHOD)) {
			syncMethod();
		} else {
			lockMethod();
		}
	}

	private void lockMethod() {
		int count = 0;
		while (true) {
			try {
				product.getLock().lock();
				if (!product.isConsume()) {
					try {
						product.getCondition().await();
					} catch (InterruptedException e) {
						log.error("", e);
					}
				}
				if (count == 0) {
					product.setName("小明");
					product.setSex("男");
				} else {
					product.setName("小红");
					product.setSex("女");
				}
				count = (count + 1) % 2;
				product.setConsume(false);
				product.getCondition().signal();
			} finally {
				if (product.getLock() != null) {
					product.getLock().unlock();
				}
			}
		}
	}

	private void syncMethod() {
		int count = 0;
		synchronized (product) {
			while (true) {
				if (!product.isConsume()) {
					try {
						product.wait();
					} catch (InterruptedException e) {
						log.error("", e);
					}
				}
				if (count == 0) {
					product.setName("小明");
					product.setSex("男");
				} else {
					product.setName("小红");
					product.setSex("女");
				}
				count = (count + 1) % 2;
				product.setConsume(false);
				product.notify();
			}
		}
	}

}
