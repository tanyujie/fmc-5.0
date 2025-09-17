/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.cache.model;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class McuKdcBridgeCollection
{
    private volatile List<McuKdcBridge> mcuXdBridges = new ArrayList<>();
    
    private McuKdcBridge masterMcuKdcBridge;
    
    /**
     * <p>Get Method   :   masterMcuKdcBridge McuKdcBridge</p>
     * @return masterMcuKdcBridge
     */
    public McuKdcBridge getMasterMcuKdcBridge()
    {
        return masterMcuKdcBridge;
    }

    /**
     * <p>Set Method   :   masterMcuKdcBridge McuKdcBridge</p>
     * @param masterMcuKdcBridge
     */
    public void setMasterMcuKdcBridge(McuKdcBridge masterMcuKdcBridge)
    {
        this.masterMcuKdcBridge = masterMcuKdcBridge;
    }

    /**
     * <p>Get Method   :   mcuXdBridges List<McuKdcBridge></p>
     * @return fmeBridges
     */
    public List<McuKdcBridge> getMcuKdcBridges()
    {
        return mcuXdBridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param mcuXdBridges
     */
    public void addMcuKdcBridge(McuKdcBridge mcuXdBridges)
    {
        this.mcuXdBridges.add(mcuXdBridges);
    }
}
