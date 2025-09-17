/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.huaweicloud.huaweicloud.cache;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class HwcloudBridgeCollection
{
    private volatile List<HwcloudBridge> hwcloudBridges = new ArrayList<>();
    
    private HwcloudBridge masterHwcloudBridge;
    
    /**
     * <p>Get Method   :   masterFmeBridge FmeBridge</p>
     * @return masterFmeBridge
     */
    public HwcloudBridge getMasterHwcloudBridge()
    {
        return masterHwcloudBridge;
    }

    /**
     * <p>Set Method   :   masterFmeBridge FmeBridge</p>
     * @param masterHwcloudBridge
     */
    public void setMasterHwcloudBridge(HwcloudBridge masterHwcloudBridge)
    {
        this.masterHwcloudBridge = masterHwcloudBridge;
    }

    /**
     * <p>Get Method   :   fmeBridges List<FmeBridge></p>
     * @return fmeBridges
     */
    public List<HwcloudBridge> getHwcloudBridges()
    {
        return hwcloudBridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param fmeBridges
     */
    public void addHwcloudBridge(HwcloudBridge HwcloudBridge)
    {
        this.hwcloudBridges.add(HwcloudBridge);
    }
}
