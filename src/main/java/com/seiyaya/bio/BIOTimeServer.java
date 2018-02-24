package com.seiyaya.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.seiyaya.bio.handler.BIOTimeServerHandler;

/**
 * һ���ͻ��˽����Ӧһ���߳�
 * ���Ը�Ϊ�̳߳�+�������е�ʵ�֣�������Ϊ����̫�ർ�±���
 * @author ����
 * @created 2018��2��23�� ����6:01:29
 */
public class BIOTimeServer {
	
	public static void main(String[] args) {
		int port = 8080;
		ServerSocket server = null;
		try {
			server = new ServerSocket(8080);
			System.out.println("---��ʼ��time server���,�˿�Ϊ:"+port);
			
			Socket socket;
			while(true) {
				socket = server.accept();
				new Thread(new BIOTimeServerHandler(socket)).start();
			}
		} catch (Exception e) {
		}finally {
			if(server!=null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				server = null;
			}
		}
	}
}
