/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IFmeCacheService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.service.interfaces;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMcuZte;
import com.paradisecloud.fcm.dao.model.BusiMcuZteCluster;

import java.util.List;

/**  
 * <pre>缓存业务处理</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0  
 */
public interface IMcuZteCacheService
{

    /**
     * <pre>删除一个会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    void deleteMcuZte(long bridgeHostId);

    /**
     * <pre>根据ID查询会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-31 16:00
     * @param cid
     * @return BridgeHostGroup
     */
    BusiMcuZteCluster getMcuZteClusterById(long cid);

    /**
     * <pre>新增会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void addMcuZte(BusiMcuZte busiMcuZte);

    /**
     * <pre>修改会议桥，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    void updateMcuZte(BusiMcuZte busiMcuZte);

    /**
     * <pre>新增会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void addBusiMcuZteCluster(BusiMcuZteCluster busiMcuZteCluster);

    /**
     * <pre>删除会议桥组</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void deleteBusiMcuZteCluster(long id);

    /**
     * <pre>获取所有会议桥组</pre>
     * @author lilinhai
     * @since 2021-01-04 17:57
     * @return List<BridgeHostGroup>
     */
    List<ModelBean> getAllBusiMcuZteCluster();

    /**
     * <pre>修改会议桥组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    void updateBusiMcuZteCluster(BusiMcuZteCluster busiMcuZteCluster);
}
