/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IFmeCacheService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuPlc;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcCluster;

import java.util.List;

/**  
 * <pre>缓存业务处理</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0  
 */
public interface IMcuPlcCacheService
{

    /**
     * <pre>删除一个会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    void deleteMcuPlc(long bridgeHostId);

    /**
     * <pre>根据ID查询会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-31 16:00
     * @param cid
     * @return BridgeHostGroup
     */
    BusiMcuPlcCluster getMcuPlcClusterById(long cid);

    /**
     * <pre>新增会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void addMcuPlc(BusiMcuPlc busiMcuPlc);

    /**
     * <pre>修改会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void updateMcuPlc(BusiMcuPlc busiMcuPlc);

    /**
     * <pre>新增会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void addBusiMcuPlcCluster(BusiMcuPlcCluster busiMcuPlcCluster);

    /**
     * <pre>删除会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void deleteBusiMcuPlcCluster(long id);

    /**
     * <pre>获取所有会议桥组</pre>
     * @author lilinhai
     * @since 2021-01-04 17:57
     * @return List<BridgeHostGroup>
     */
    List<ModelBean> getAllBusiMcuPlcCluster();

    /**
     * <pre>修改会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void updateBusiMcuPlcCluster(BusiMcuPlcCluster busiMcuPlcCluster);
}
