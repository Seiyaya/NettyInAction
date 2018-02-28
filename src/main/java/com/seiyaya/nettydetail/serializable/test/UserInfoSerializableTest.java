package com.seiyaya.nettydetail.serializable.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.sound.midi.MidiDevice.Info;

import com.seiyaya.nettydetail.serializable.bean.UserInfo;

/**
 * 测试userInfo的序列化
 * jdk自带序列化
 * 自定义序列化 len+byte
 * @author 王佳
 * @created 2018年2月27日 下午4:35:26
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
