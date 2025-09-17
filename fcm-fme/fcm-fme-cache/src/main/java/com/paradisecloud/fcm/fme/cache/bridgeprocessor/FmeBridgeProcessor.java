/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeBridgeProcessor.java
 * Package     : com.paradisecloud.fcm.fme.cache.bridgeprocessor
 * @author lilinhai 
 * @since 2021-03-22 11:48
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.bridgeprocessor;

import java.util.Set;

import com.paradisecloud.fcm.fme.cache.model.FmeBridge;

/**  
 * <pre>FME桥处理器</pre>
 * @author lilinhai
 * @since 2021-03-22 11:48
 * @version V1.0  
 */
public interface FmeBridgeProcessor
{
    
    /**
     * 处理fme桥业务
     * @author lilinhai
     * @since 2021-03-22 11:51 
     * @param fmeBridge void
     */
    void process(FmeBridge fmeBridge);
    
    /**
     * 命中后是否需要跳出循环
     * @author lilinhai
     * @since 2021-03-22 11:51 
     * @return boolean
     */
    boolean isBreak();
    
    /**
     * 排除的fmeip
     * @author sinhy
     * @since 2021-09-13 08:50 
     * @return Set<String>
     */
    Set<String> excludeFmeIps();
}
