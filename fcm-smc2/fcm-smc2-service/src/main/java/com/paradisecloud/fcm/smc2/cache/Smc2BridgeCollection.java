/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.cache;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class Smc2BridgeCollection
{
    private volatile List<Smc2Bridge> smc2Bridges = new ArrayList<>();
    
    private Smc2Bridge masterSmc2Bridge;
    
    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public Smc2Bridge getMasterSmc2Bridge()
    {
        return masterSmc2Bridge;
    }

    /**
     * <p>Set Method   :   masterFmeBridge FmeBridge</p>
     * @param masterSmc2Bridge
     */
    public void setMasterSmc2Bridge(Smc2Bridge masterSmc2Bridge)
    {
        this.masterSmc2Bridge = masterSmc2Bridge;
    }

    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<Smc2Bridge> getSmc2Bridges()
    {
        return smc2Bridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param fmeBridges
     */
    public void addSmc2Bridge(Smc2Bridge smc2Bridge)
    {
        this.smc2Bridges.add(smc2Bridge);
    }
}
