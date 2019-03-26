package com.seiyaya.thread;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class NewThread
{
    public static void main(String[] args)
    {
        List<Thread> list = new ArrayList<>(100);
        
        while(true) {
            list.add(new Thread(()->{
                
            }));
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
