/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.ding.cache;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class DingBridgeCollection
{
    private volatile List<DingBridge> dingBridges = new ArrayList<>();
    
    private DingBridge masterDingBridge;
    
    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public DingBridge getMasterDingBridge()
    {
        return masterDingBridge;
    }

    /**
     * <p>Set Method   :   masterFmeBridge FmeBridge</p>
     * @param masterDingBridge
     */
    public void setMasterDingBridge(DingBridge masterDingBridge)
    {
        this.masterDingBridge = masterDingBridge;
    }

    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<DingBridge> getDingBridges()
    {
        return dingBridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param fmeBridges
     */
    public void addDingBridge(DingBridge DingBridge)
    {
        this.dingBridges.add(DingBridge);
    }
}
