/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IMcuNodeDataSyncService.java
 * Package     : com.paradisecloud.service.interfaces
 * @author lilinhai 
 * @since 2020-12-07 13:37
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.apiservice.interfaces;

import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;

/**  
 * <pre>MCU节点数据同步服务</pre>
 * @author lilinhai
 * @since 2020-12-07 13:37
 * @version V1.0  
 */
public interface ICoSpaceService
{
    
    /**
     * <pre>同步指定节点全量会议室</pre>
     * @author lilinhai
     * @since 2020-12-07 16:05 
     * @param fmeBridge void
     */
    void syncCoSpaces(FmeBridge fmeBridge);
    
    /**
     * 根据会议号获取coSpace对象
     * @author lilinhai
     * @since 2021-03-15 16:19 
     * @param deptId 
     * @param conferenceNumber
     * @return CoSpace
     */
    CoSpace getCoSpaceByConferenceNumber(Long deptId, String conferenceNumber);
    
    /**
     * 更新coSpace缓存
     * @author lilinhai
     * @since 2021-04-15 11:11 
     * @param fmeBridge
     * @param coSpaceId void
     */
    void updateCoSpaceCache(FmeBridge fmeBridge, String coSpaceId);
    
    /**
     * 根据会议号获取coSpace对象
     * @author lilinhai
     * @since 2021-03-22 15:36 
     * @param fmeBridge
     * @param deptId
     * @param conferenceNumber
     * @return CoSpace
     */
    CoSpace getCoSpaceByConferenceNumber(FmeBridge fmeBridge, String conferenceNumber);
    
    
    /**
     * 修改入会方案为默认的
     * @author lilinhai
     * @since 2021-03-15 17:34 
     * @param fmeBridge
     * @param coSpace
     * @param callLegProfileId void
     */
    void updateCoSpaceCallLegProfile(FmeBridge fmeBridge, Long deptId, CoSpace coSpace);
    
    /**
     * 修改入会方案
     * @author lilinhai
     * @since 2021-03-15 17:34 
     * @param fmeBridge
     * @param coSpace
     * @param callLegProfileId void
     */
    void updateCoSpaceCallLegProfile(FmeBridge fmeBridge, CoSpace coSpace, String callLegProfileId);
    
    /**
     * 修改入会方案
     * @author lilinhai
     * @since 2021-03-15 17:34 
     * @param fmeBridge
     * @param coSpace
     * @param callLegProfileId void
     */
    void updateCoSpace(FmeBridge fmeBridge, CoSpace coSpace, CoSpaceParamBuilder coSpaceParamBuilder);
    
    /**
     * 回收coSpace
     * @author lilinhai
     * @since 2021-06-01 16:47 
     * @param deptId
     * @param conferenceNumber void
     */
    void recoveryCospace(Long deptId, String conferenceNumber);
    
    void recoveryCospace(FmeBridge fmeBridge, String conferenceNumber);
}
