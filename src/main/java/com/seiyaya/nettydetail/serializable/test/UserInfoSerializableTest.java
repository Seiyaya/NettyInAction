package com.seiyaya.nettydetail.serializable.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.sound.midi.MidiDevice.Info;

import com.seiyaya.nettydetail.serializable.bean.UserInfo;

/**
 * ����userInfo�����л�
 * jdk�Դ����л�
 * �Զ������л� len+byte
 * @author ����
 * @created 2018��2��27�� ����4:35:26
 */
public class UserInfoSerializableTest
{
    public static void main(String[] args) throws IOException
    {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(100).setUserName("zhangsan");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(userInfo);
        oos.flush();
        oos.close();
        
        byte[] jdkResult = bos.toByteArray();
        
        //127
        System.out.println("jdk result length "+ jdkResult.length);
        
        
        System.out.println("-----------------------------------");
        
        //16
        System.out.println("my result length "+ userInfo.codeC().length);
    }
}
