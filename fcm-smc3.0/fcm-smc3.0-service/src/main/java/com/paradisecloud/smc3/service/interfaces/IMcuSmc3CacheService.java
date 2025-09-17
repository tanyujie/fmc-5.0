/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IFmeCacheService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Cluster;

import java.util.List;

/**  
 * <pre>缓存业务处理</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0  
 */
public interface IMcuSmc3CacheService
{

    /**
     * <pre>删除一个会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    void deleteMcuSmc3(Long bridgeHostId);

    /**
     * <pre>根据ID查询会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-31 16:00
     * @param cid
     * @return BridgeHostGroup
     */
    BusiMcuSmc3Cluster getMcuSmc3ClusterById(Long cid);

    /**
     * <pre>新增会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void addMcuSmc3(BusiMcuSmc3 busiMcuSmc3);

    /**
     * <pre>修改会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void updateMcuSmc3(BusiMcuSmc3 busiMcuSmc3);

    /**
     * <pre>新增会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void addBusiMcuSmc3Cluster(BusiMcuSmc3Cluster busiMcuSmc3Cluster);

    /**
     * <pre>删除会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void deleteBusiMcuSmc3Cluster(Long id);

    /**
     * <pre>获取所有会议桥组</pre>
     * @author lilinhai
     * @since 2021-01-04 17:57
     * @return List<BridgeHostGroup>
     */
    List<ModelBean> getAllBusiMcuSmc3Cluster();

    /**
     * <pre>修改会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void updateBusiMcuSmc3Cluster(BusiMcuSmc3Cluster busiMcuSmc3Cluster);
}
