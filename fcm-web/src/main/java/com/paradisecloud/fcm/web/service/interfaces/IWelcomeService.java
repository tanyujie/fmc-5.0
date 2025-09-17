/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IWelcomeService.java
 * Package     : com.paradisecloud.fcm.service.interfaces
 * @author lilinhai 
 * @since 2021-06-02 11:33
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.web.service.interfaces;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**  
 * <pre>首页服务</pre>
 * @author lilinhai
 * @since 2021-06-02 11:33
 * @version V1.0  
 */
public interface IWelcomeService
{

    /**
     * <pre>获取会议统计信息</pre>
     * @author lilinhai
     * @since 2021-06-02 11:35 
     * @return Object
     */
    JSONObject conferenceStat();
    
    /**
     * 终端统计
     * @author lilinhai
     * @since 2021-06-02 14:53 
     * @return JSONObject
     */
    JSONObject terminalStat();
    
    /**
     * 活跃会议室列表
     * @author lilinhai
     * @since 2021-06-02 14:10 
     * @return List<JSONObject>
     */
    List<JSONObject> activeConferences();

    /**
     * 活跃会议列表
     * @param searchKey
     * @param pageIndex
     * @param pagSize
     * @return
     */
    List<JSONObject> activeConferencesPages(String searchKey,int pageIndex,int pagSize);
    /**
     * <pre>租户资源信息</pre>
     * @author lilinhai
     * @since 2021-06-02 14:56 
     * @return Object
     */
    JSONObject tenantResource();
    
}
