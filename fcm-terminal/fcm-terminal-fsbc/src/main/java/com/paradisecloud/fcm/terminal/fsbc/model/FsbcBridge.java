/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FSBCBridge.java
 * Package     : com.paradisecloud.fcm.terminal.fsbc.model
 * @author lilinhai 
 * @since 2021-04-21 14:30
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fsbc.model;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.dao.model.BusiFsbcRegistrationServer;
import com.paradisecloud.fcm.terminal.fsbc.invoker.CredentialInvoker;
import com.paradisecloud.fcm.terminal.fsbc.invoker.RegistrationInvoker;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;

/**  
 * <pre>FSBC桥</pre>
 * @author lilinhai
 * @since 2021-04-21 14:30
 * @version V1.0  
 */
public class FsbcBridge
{
    
    private BusiFsbcRegistrationServer busiFsbcRegistrationServer;
    
    private HttpRequester httpRequester;
    
    private FsbcLogger fsbcLogger;
    
    private CredentialInvoker credentialInvoker;
    
    private RegistrationInvoker registrationInvoker;
    
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-21 14:39 
     * @param busiFsbcRegistrationServer 
     */
    public FsbcBridge(BusiFsbcRegistrationServer busiFsbcRegistrationServer)
    {
        this.busiFsbcRegistrationServer = busiFsbcRegistrationServer;
        if (!ObjectUtils.isEmpty(busiFsbcRegistrationServer.getUsername()) && !ObjectUtils.isEmpty(busiFsbcRegistrationServer.getPassword()))
        {
            this.httpRequester = HttpObjectCreator.getInstance().createHttpRequester(busiFsbcRegistrationServer.getUsername(), busiFsbcRegistrationServer.getPassword(), false);
        }
        else
        {
            this.httpRequester = HttpObjectCreator.getInstance().createHttpRequester(false);
        }
        this.fsbcLogger = new FsbcLogger(this);
        
        String rootUrl = "https://" + busiFsbcRegistrationServer.getDataSyncIp() + ":" + busiFsbcRegistrationServer.getPort();
        this.credentialInvoker = new CredentialInvoker(httpRequester, rootUrl + "/api/provisioning", fsbcLogger);
        this.registrationInvoker = new RegistrationInvoker(httpRequester, rootUrl + "/getxml?location=/Status/Registrations", fsbcLogger);
    }
    
    /**
     * <p>Get Method   :   credentialInvoker CredentialInvoker</p>
     * @return credentialInvoker
     */
    public CredentialInvoker getCredentialInvoker()
    {
        return credentialInvoker;
    }
    
    /**
     * <p>Get Method   :   registrationInvoker RegistrationInvoker</p>
     * @return registrationInvoker
     */
    public RegistrationInvoker getRegistrationInvoker()
    {
        return registrationInvoker;
    }

    /**
     * <p>Get Method   :   busiFsbcRegistrationServer BusiFsbcRegistrationServer</p>
     * @return busiFsbcRegistrationServer
     */
    public BusiFsbcRegistrationServer getBusiFsbcRegistrationServer()
    {
        return busiFsbcRegistrationServer;
    }
    
    /**
     * <p>Get Method   :   fsbcLogger FsbcLogger</p>
     * @return fsbcLogger
     */
    public FsbcLogger getFsbcLogger()
    {
        return fsbcLogger;
    }

    public void destroy()
    {
        if (httpRequester != null)
        {
            httpRequester.destroy();
        }
    }
}
