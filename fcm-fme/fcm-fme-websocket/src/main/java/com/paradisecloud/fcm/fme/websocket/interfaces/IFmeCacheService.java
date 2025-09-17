/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IFmeCacheService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.interfaces;

import java.util.List;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.dao.model.BusiFmeCluster;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;

/**  
 * <pre>缓存业务处理</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0  
 */
public interface IFmeCacheService
{
    
    /**
     * <pre>初始化FME调用器，仅仅启动时调用</pre>
     * @author lilinhai
     * @since 2020-12-29 17:37 
     * @param fmeHttpInvoker void
     */
    void initFmeBridge(FmeBridge fmeHttpInvoker);
    
    /**
     * <pre>删除一个会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    void deleteFme(long bridgeHostId);
    
    /**
     * <pre>清除fme数据缓存</pre>
     * @author lilinhai
     * @since 2020-12-31 13:40 
     * @param fmeHttpInvoker void
     */
    void clearFmeDataCache(FmeBridge fmeHttpInvoker);
    
    /**
     * <pre>根据ID查询会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-31 16:00 
     * @param cid
     * @return BridgeHostGroup
     */
    BusiFmeCluster getFmeClusterById(long cid);
    
    /**
     * <pre>新增会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void addFme(BusiFme busiFme);
    
    /**
     * <pre>修改会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void updateFme(BusiFme bh);
    
    /**
     * <pre>新增会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void addBusiFmeCluster(BusiFmeCluster busiFmeCluster);
    
    /**
     * <pre>删除会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void deleteBusiFmeCluster(long id);
    
    /**
     * <pre>获取所有会议桥组</pre>
     * @author lilinhai
     * @since 2021-01-04 17:57 
     * @return List<BridgeHostGroup>
     */
    List<ModelBean> getAllBusiFmeCluster();
    
    /**
     * <pre>修改会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void updateBusiFmeCluster(BusiFmeCluster busiFmeCluster);

    /**
     * MTU设置
     * @author sinhy
     * @since 2022-05-13 10:58 
     * @param intValue void
     */
    void mtuSettings(Long fmeId, int mtuValue);

    /**
     * 根据IP ping 终端
     */
    String pingIp(String ip,long id);
}
