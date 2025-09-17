/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeIDBuilder.java
 * Package     : com.paradisecloud.fcm.fme.model.core
 * @author lilinhai 
 * @since 2021-01-21 11:13
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.model.core;

import com.paradisecloud.fcm.dao.model.BusiMcuPlc;

/**
 * <pre>获取MCU ID</pre>
 * @author lilinhai
 * @since 2021-01-21 11:13
 * @version V1.0  
 */
public class McuIDBuilder
{
    
    public static String build(BusiMcuPlc busiMcuPlc)
    {
        return busiMcuPlc.getIp() + ":" + busiMcuPlc.getPort();
    }
}
