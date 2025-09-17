/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.cache.model;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class McuPlcBridgeCollection
{
    private volatile List<McuPlcBridge> mcuXdBridges = new ArrayList<>();
    
    private McuPlcBridge masterMcuPlcBridge;
    
    /**
     * <p>Get Method   :   masterMcuPlcBridge McuPlcBridge</p>
     * @return masterMcuPlcBridge
     */
    public McuPlcBridge getMasterMcuPlcBridge()
    {
        return masterMcuPlcBridge;
    }

    /**
     * <p>Set Method   :   masterMcuPlcBridge McuPlcBridge</p>
     * @param masterMcuPlcBridge
     */
    public void setMasterMcuPlcBridge(McuPlcBridge masterMcuPlcBridge)
    {
        this.masterMcuPlcBridge = masterMcuPlcBridge;
    }

    /**
     * <p>Get Method   :   mcuXdBridges List<McuPlcBridge></p>
     * @return fmeBridges
     */
    public List<McuPlcBridge> getMcuPlcBridges()
    {
        return mcuXdBridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param mcuXdBridges
     */
    public void addMcuPlcBridge(McuPlcBridge mcuXdBridges)
    {
        this.mcuXdBridges.add(mcuXdBridges);
    }
}
