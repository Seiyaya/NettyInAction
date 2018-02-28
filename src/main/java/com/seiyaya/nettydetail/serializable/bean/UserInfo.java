package com.seiyaya.nettydetail.serializable.bean;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class UserInfo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String userName;
    
    private int    userId;
    
    public UserInfo setUserName(String userName)
    {
        this.userName = userName;
        return this;
    }
    
    public UserInfo setUserId(int userId)
    {
        this.userId = userId;
        return this;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public int getUserId()
    {
        return userId;
    }
    
    public byte[] codeC()
    {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putInt(this.getUserName().getBytes().length);
        buffer.put(this.getUserName().getBytes());
        buffer.putInt(userId);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        return result;
    }
}
