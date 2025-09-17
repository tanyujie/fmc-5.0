/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.cache;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class TencentBridgeCollection
{
    private volatile List<TencentBridge> TencentBridges = new ArrayList<>();
    
    private TencentBridge masterTencentBridge;
    
    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public TencentBridge getMasterTencentBridge()
    {
        return masterTencentBridge;
    }

    /**
     * <p>Set Method   :   masterFmeBridge FmeBridge</p>
     * @param masterTencentBridge
     */
    public void setMasterTencentBridge(TencentBridge masterTencentBridge)
    {
        this.masterTencentBridge = masterTencentBridge;
    }

    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<TencentBridge> getTencentBridges()
    {
        return TencentBridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param fmeBridges
     */
    public void addTencentBridge(TencentBridge TencentBridge)
    {
        this.TencentBridges.add(TencentBridge);
    }
}
