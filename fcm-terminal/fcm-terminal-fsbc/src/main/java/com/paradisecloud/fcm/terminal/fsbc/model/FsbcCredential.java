/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FsbcCredential.java
 * Package     : com.paradisecloud.fcm.terminal.fsbc.model
 * @author lilinhai 
 * @since 2021-04-21 17:23
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fsbc.model;

import com.alibaba.fastjson.JSONObject;

/**  
 * <pre>FSBC账号</pre>
 * @author lilinhai
 * @since 2021-04-21 17:23
 * @version V1.0  
 */
public class FsbcCredential extends JSONObject
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-04-21 17:27 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 创建一个新的账号
     * @author lilinhai
     * @since 2021-04-21 17:28 
     * @return FsbcCredential
     */
    public static FsbcCredential newCredential()
    {
        return new FsbcCredential();
    }
    
    public FsbcCredential name(String name)
    {
        put("Name", name);
        return this;
    }
    
    public FsbcCredential newName(String newName)
    {
        if (!getString("Name").equals(newName))
        {
            put("NewName", newName);
        }
        return this;
    }
    
    public FsbcCredential password(String password)
    {
        put("Password", password);
        return this;
    }
    
    public String name()
    {
        return getString("Name");
    }
    
    public String newName()
    {
        return getString("NewName");
    }
    
    public String password()
    {
        return getString("Password");
    }
}
