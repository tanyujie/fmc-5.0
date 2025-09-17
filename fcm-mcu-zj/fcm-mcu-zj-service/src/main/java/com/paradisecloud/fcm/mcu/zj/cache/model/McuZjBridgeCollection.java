/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.cache.model;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class McuZjBridgeCollection
{
    private volatile List<McuZjBridge> mcuXdBridges = new ArrayList<>();
    
    private McuZjBridge masterMcuZjBridge;
    
    /**
     * <p>Get Method   :   masterMcuZjBridge McuZjBridge</p>
     * @return masterMcuZjBridge
     */
    public McuZjBridge getMasterMcuZjBridge()
    {
        return masterMcuZjBridge;
    }

    /**
     * <p>Set Method   :   masterMcuZjBridge McuZjBridge</p>
     * @param masterMcuZjBridge
     */
    public void setMasterMcuZjBridge(McuZjBridge masterMcuZjBridge)
    {
        this.masterMcuZjBridge = masterMcuZjBridge;
    }

    /**
     * <p>Get Method   :   mcuXdBridges List<McuZjBridge></p>
     * @return fmeBridges
     */
    public List<McuZjBridge> getMcuZjBridges()
    {
        return mcuXdBridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param mcuXdBridges
     */
    public void addMcuZjBridge(McuZjBridge mcuXdBridges)
    {
        this.mcuXdBridges.add(mcuXdBridges);
    }
}
