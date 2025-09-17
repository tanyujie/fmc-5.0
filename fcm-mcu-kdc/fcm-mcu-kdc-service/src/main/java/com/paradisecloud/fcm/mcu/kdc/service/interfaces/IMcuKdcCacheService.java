/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IFmeCacheService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuKdc;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcCluster;

import java.util.List;

/**  
 * <pre>缓存业务处理</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0  
 */
public interface IMcuKdcCacheService
{

    /**
     * <pre>删除一个会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    void deleteMcuKdc(long bridgeHostId);

    /**
     * <pre>根据ID查询会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-31 16:00
     * @param cid
     * @return BridgeHostGroup
     */
    BusiMcuKdcCluster getMcuKdcClusterById(long cid);

    /**
     * <pre>新增会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void addMcuKdc(BusiMcuKdc busiMcuKdc);

    /**
     * <pre>修改会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void updateMcuKdc(BusiMcuKdc busiMcuKdc);

    /**
     * <pre>新增会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void addBusiMcuKdcCluster(BusiMcuKdcCluster busiMcuKdcCluster);

    /**
     * <pre>删除会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void deleteBusiMcuKdcCluster(long id);

    /**
     * <pre>获取所有会议桥组</pre>
     * @author lilinhai
     * @since 2021-01-04 17:57
     * @return List<BridgeHostGroup>
     */
    List<ModelBean> getAllBusiMcuKdcCluster();

    /**
     * <pre>修改会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void updateBusiMcuKdcCluster(BusiMcuKdcCluster busiMcuKdcCluster);
}
