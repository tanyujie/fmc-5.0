/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FreeSwitchCacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fs.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.dao.mapper.BusiFreeSwitchClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiFreeSwitchDeptMapper;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchCluster;
import com.paradisecloud.fcm.dao.model.BusiFreeSwitchDept;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.FreeSwitchClusterCache;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiFreeSwitchClusterService;
import com.sinhy.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**  
 * <pre>FreeSwitch缓存业务处理实现类</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0  
 */
@Service
public class BusiFreeSwitchClusterServiceImpl implements IBusiFreeSwitchClusterService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Resource
    private BusiFreeSwitchClusterMapper busiFreeSwitchClusterMapper;
    
    @Resource
    private BusiFreeSwitchDeptMapper busiFreeSwitchDeptMapper;

    /**
     * 
     * @param cid
     * @return
     */
    public BusiFreeSwitchCluster getFreeSwitchClusterById(long cid)
    {
        return null;
    }
    
    /**
     * <pre>新增FreeSwitch组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void addBusiFreeSwitchCluster(BusiFreeSwitchCluster busiFreeSwitchCluster)
    {
        busiFreeSwitchCluster.setCreateTime(new Date());
        busiFreeSwitchClusterMapper.insertBusiFreeSwitchCluster(busiFreeSwitchCluster);
        
        // 添加缓存
        FreeSwitchClusterCache.getInstance().put(busiFreeSwitchCluster.getId(), busiFreeSwitchCluster);
        logger.info("添加FCM组成功: {}", busiFreeSwitchCluster);
    }
    
    /**
     * <pre>删除FreeSwitch集群</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void deleteBusiFreeSwitchCluster(long id)
    {
        if (FcmBridgeCache.getInstance().isInUse(id))
        {
            throw new SystemException(1000013, "FCM集群的删除，请先删除该集群下所有FCM，再删除该FCM集群，最后再重新创建！");
        }
        
        BusiFreeSwitchDept con1 = new BusiFreeSwitchDept();
        con1.setServerId(id);
        con1.setFcmType(FcmType.CLUSTER.getValue());
        List<BusiFreeSwitchDept> fds = busiFreeSwitchDeptMapper.selectBusiFreeSwitchDeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }
        
        int c = busiFreeSwitchClusterMapper.deleteBusiFreeSwitchClusterById(id);
        if (c > 0)
        {
            FreeSwitchClusterCache.getInstance().remove(id);
            logger.info("删除FCM集群成功，id: {}", id);
        }
    }
    
    /**
     * <pre>获取所有FreeSwitch组</pre>
     * @author lilinhai
     * @since 2021-01-21 15:54 
     * @return
     */
    public List<ModelBean> getAllBusiFreeSwitchCluster()
    {
        List<BusiFreeSwitchCluster> gs = new ArrayList<>(FreeSwitchClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiFreeSwitchCluster busiFreeSwitchCluster : gs)
        {
            ModelBean m = new ModelBean(busiFreeSwitchCluster);
            m.put("bindDeptCount", DeptFcmMappingCache.getInstance().getBindDeptCount(FcmType.CLUSTER, busiFreeSwitchCluster.getId()));
            ms.add(m);
        }
        return ms;
    }
    
    /**
     * <pre>修改FreeSwitch组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void updateBusiFreeSwitchCluster(BusiFreeSwitchCluster busiFreeSwitchCluster)
    {
        busiFreeSwitchCluster.setUpdateTime(new Date());
        busiFreeSwitchClusterMapper.updateBusiFreeSwitchCluster(busiFreeSwitchCluster);
        FreeSwitchClusterCache.getInstance().put(busiFreeSwitchCluster.getId(), busiFreeSwitchClusterMapper.selectBusiFreeSwitchClusterById(busiFreeSwitchCluster.getId()));
        logger.info("修改FCM集群信息成功: {}", busiFreeSwitchCluster);
    }
    
}
