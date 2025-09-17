/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class FmeBridgeCollection
{
    private volatile List<FmeBridge> fmeBridges = new ArrayList<>();
    
    private FmeBridge masterFmeBridge;
    
    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public FmeBridge getMasterFmeBridge()
    {
        return masterFmeBridge;
    }

    /**
     * <p>Set Method   :   masterFmeBridge FmeBridge</p>
     * @param masterFmeBridge
     */
    public void setMasterFmeBridge(FmeBridge masterFmeBridge)
    {
        this.masterFmeBridge = masterFmeBridge;
    }

    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<FmeBridge> getFmeBridges()
    {
        return fmeBridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param fmeBridges
     */
    public void addFmeBridge(FmeBridge fmeBridge)
    {
        this.fmeBridges.add(fmeBridge);
    }
}
