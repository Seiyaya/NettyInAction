package com.seiyaya.bio.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class BIOTimeServerHandler implements Runnable{

	
	private Socket socket;
	
	
	public BIOTimeServerHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			
			String currentTime = null;
			String body = null;
			while(true) {
				body = in.readLine();
				if(body == null) {
					break;
				}
				System.out.println("接受到的数据:"+body);
				currentTime = "query time order".equals(body)?new Date().toString():"bad order";
				out.println(currentTime);
			}
		} catch (Exception e) {
		}finally {
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(out!=null) {
				out.close();
			}
			
			if(socket!=null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
