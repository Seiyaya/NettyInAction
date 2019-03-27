package com.seiyaya.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewThread
{
    public static void main(String[] args)
    {
        List<Thread> list = new ArrayList<>(100);
        try {
        	while(true) {
                new Thread(()->{
                    try {
    					TimeUnit.SECONDS.sleep(3);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
                }).start();
            }
		} catch (Exception e2) {
			log.error("",e2);
		}
        
        //Java heap space
        //GC²ÎÊý  -XX:+PrintGCDetails -Xms2m -Xmx2m
    }
    
    @Test
    public void testStackOOM() {
        fun();//stack over flow error
    }
    
    public void fun() {
        fun();
    }
}
