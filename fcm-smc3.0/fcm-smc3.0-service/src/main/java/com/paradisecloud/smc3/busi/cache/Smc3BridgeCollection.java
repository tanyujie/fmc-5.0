/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.cache;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class Smc3BridgeCollection
{
    private volatile List<Smc3Bridge> smc3Bridges = new ArrayList<>();
    
    private Smc3Bridge masterSmc3Bridge;
    
    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public Smc3Bridge getMasterSmc3Bridge()
    {
        return masterSmc3Bridge;
    }

    /**
     * <p>Set Method   :   masterFmeBridge FmeBridge</p>
     * @param masterFmeBridge
     */
    public void setMasterSmc3Bridge(Smc3Bridge masterSmc3Bridge)
    {
        this.masterSmc3Bridge = masterSmc3Bridge;
    }

    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<Smc3Bridge> getSmc3Bridges()
    {
        return smc3Bridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param fmeBridges
     */
    public void addSmc3Bridge(Smc3Bridge smc3Bridge)
    {
        this.smc3Bridges.add(smc3Bridge);
    }
}
