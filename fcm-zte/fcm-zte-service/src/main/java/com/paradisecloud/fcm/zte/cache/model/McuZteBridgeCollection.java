/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeCollection.java
 * Package     : com.paradisecloud.fcm.fme.service.model
 * @author lilinhai 
 * @since 2021-02-05 14:42
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.cache.model;

import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>FME桥集合</pre>
 * @author lilinhai
 * @since 2021-02-05 14:42
 * @version V1.0  
 */
public class McuZteBridgeCollection
{
    private volatile List<McuZteBridge> mcuXdBridges = new ArrayList<>();
    
    private McuZteBridge masterMcuZteBridge;
    
    /**
     * <p>Get Method   :   masterMcuZteBridge McuZteBridge</p>
     * @return masterMcuZteBridge
     */
    public McuZteBridge getMasterMcuZteBridge()
    {
        return masterMcuZteBridge;
    }

    /**
     * <p>Set Method   :   masterMcuZteBridge McuZteBridge</p>
     * @param masterMcuZteBridge
     */
    public void setMasterMcuZteBridge(McuZteBridge masterMcuZteBridge)
    {
        this.masterMcuZteBridge = masterMcuZteBridge;
    }

    /**
     * <p>Get Method   :   mcuXdBridges List<McuZteBridge></p>
     * @return fmeBridges
     */
    public List<McuZteBridge> getMcuZteBridges()
    {
        return mcuXdBridges;
    }

    /**
     * <p>Set Method   :   fmeBridges List<FmeBridge></p>
     * @param mcuXdBridges
     */
    public void addMcuZteBridge(McuZteBridge mcuXdBridges)
    {
        this.mcuXdBridges.add(mcuXdBridges);
    }
}
