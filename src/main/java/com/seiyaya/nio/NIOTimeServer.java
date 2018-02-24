package com.seiyaya.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import com.seiyaya.nio.server.MultiplexerTimeServer;
import com.seiyaya.nio.task.ReactorTask;

public class NIOTimeServer {
	public static void main(String[] args) throws Exception {
		/**
		 * 1.��ServerSocketChannel���������ͻ��˵�����
		 * 2.�󶨶˿�,���óɷ�����ģʽ
		 * 3.����Reactor�̺߳Ͷ�·������selector
		 * 4.��serverSocketChannelע�ᵽselector��
		 * 5.�ڶ�·��������ѯ׼��������key
		 * 6.��·�����������ͻ��˵Ľ���
		 * 7.���ÿͻ�������Ϊ������ģʽ
		 * 8.���ͻ�������ע�ᵽreactor�߳��ϵĶ�·������
		 * 9.�첽��ȡ������Ϣ��������
		 * 10.�Ի��������ݱ���룬����Ϣ��װΪtask���̳߳ض�task����
		 * 11.����˻�����Ϣ
		 */
		new Thread(new MultiplexerTimeServer()).start();;
	}
}
