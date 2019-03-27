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
	 * lockʵ���̼߳�ͨ��
	 * 
	 * @author Seiyaya
	 * @date 2019��3��27�� ����12:47:34
	 */
	private void lockMethod() {
		while (true) {
			try {
				product.getLock().lock();
				if (product.isConsume()) {
					// ��������ȴ�
					try {
						product.getCondition().await();
					} catch (InterruptedException e) {
						log.error("", e);
					}
				}
				log.info("{}-->{}", product.getName(), product.getSex());
				// ������ �ȴ��������ٴ�����
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
	 * ͬ���ؼ���ʵ���̼߳�ͨ��
	 * 
	 * @author Seiyaya
	 * @date 2019��3��27�� ����12:32:48
	 */
	private void syncMethod() {
		synchronized (product) {
			if (product.isConsume()) {
				// ��������ȴ�
				try {
					product.wait();
				} catch (InterruptedException e) {
					log.error("", e);
				}
			}
			log.info("{}-->{}", product.getName(), product.getSex());
			// ������ �ȴ��������ٴ�����
			product.setConsume(true);
			product.notify();
		}
	}

}
