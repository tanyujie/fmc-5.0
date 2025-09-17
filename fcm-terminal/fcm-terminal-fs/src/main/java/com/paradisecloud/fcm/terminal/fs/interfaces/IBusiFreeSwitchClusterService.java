/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IFreeSwitchCacheService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fs.interfaces;

import java.util.List;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchCluster;

/**  
 * <pre>缓存业务处理</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0  
 */
public interface IBusiFreeSwitchClusterService
{
    
    /**
     * <pre>根据ID查询会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-31 16:00 
     * @param cid
     * @return BridgeHostGroup
     */
    BusiFreeSwitchCluster getFreeSwitchClusterById(long cid);
    
    /**
     * <pre>新增会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void addBusiFreeSwitchCluster(BusiFreeSwitchCluster busiFreeSwitchCluster);
    
    /**
     * <pre>删除会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void deleteBusiFreeSwitchCluster(long id);
    
    /**
     * <pre>获取所有会议桥组</pre>
     * @author lilinhai
     * @since 2021-01-04 17:57 
     * @return List<BridgeHostGroup>
     */
    List<ModelBean> getAllBusiFreeSwitchCluster();
    
    /**
     * <pre>修改会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void updateBusiFreeSwitchCluster(BusiFreeSwitchCluster busiFreeSwitchCluster);
}
