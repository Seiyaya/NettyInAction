package com.seiyaya.nettydetail.reactor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * buffer����ز���
 * @author ����
 * @created 2018��3��19�� ����3:05:33
 */
public class BufferTest
{
    public static final int BUFFER_SIZE = 1024;
    
    public static void main(String[] args) throws Exception
    {
        FileChannel fc = new FileOutputStream("E:/data.txt").getChannel();
        fc.write(ByteBuffer.wrap("����".getBytes()));
        
        
        fc = new FileInputStream("E:/data.txt").getChannel();
        ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE);
        fc.read(buff);
        buff.flip();
        
        System.out.println(new String(buff.array(),"gbk"));
        
        buff.rewind();
        String encoding = System.getProperty("file.encoding");
        System.out.println("�ַ����룺"+encoding+"--" + Charset.forName(encoding).decode(buff));
        
        
        fc.close();
        System.out.println("ok");
    }
}
