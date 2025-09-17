/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2022, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : JschInvoker.java
 * Package : com.paradisecloud.fcm.fme.cache.model.invoker
 * 
 * @author sinhy
 * 
 * @since 2022-01-07 17:27
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.cache.model.invoker;

import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.sinhy.utils.IOUtils;

/**
 * JSCH操作
 * 
 * @author sinhy
 * @since 2022-01-07 17:27
 * @version V1.0
 */
public class JschInvoker
{
    
    private BusiFme busiFme;
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author sinhy
     * @since 2022-01-07 17:30
     * @param busiFme
     */
    public JschInvoker(BusiFme busiFme)
    {
        super();
        this.busiFme = busiFme;
    }
    
    private Session getSession() throws Exception
    {
        JSch jsch = new JSch();
        Session session = jsch.getSession(busiFme.getAdminUsername(), busiFme.getIp(), 22);
        return session;
    }
    
    public Session connect() throws Exception
    {
        Session session = getSession();
        session.setPassword(busiFme.getAdminPassword());
        Properties config = new Properties();
        config.setProperty("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        return session;
    }
    
    public String execCmd(String command) throws RuntimeException
    {
        Session session = null;
        ChannelExec exec = null;
        try
        {
            session = connect();
            exec = (ChannelExec) session.openChannel("exec");
            InputStream in = exec.getInputStream();
            exec.setCommand(command);
            exec.connect();
            return IOUtils.copyToString(in);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                if (exec != null && exec.isConnected())
                {
                    exec.disconnect();
                }
            }
            catch (Exception e2)
            {
            }
            try
            {
                if (session != null && session.isConnected())
                {
                    session.disconnect();
                }
            }
            catch (Exception e2)
            {
            }
        }
    }
}
