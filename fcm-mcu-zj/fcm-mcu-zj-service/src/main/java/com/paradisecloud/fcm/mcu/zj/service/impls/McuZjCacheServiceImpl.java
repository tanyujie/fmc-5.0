/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuZjCacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.common.enumer.McuZjType;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.DeptMcuZjMappingCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjClusterCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.core.McuIDBuilder;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IMcuZjCacheService;
import com.sinhy.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**  
 * <pre>MCU缓存业务处理实现类</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0  
 */
@Service
public class McuZjCacheServiceImpl implements IMcuZjCacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiMcuZjMapper busiMcuZjMapper;
    
    @Resource
    private BusiMcuZjClusterMapper busiMcuZjClusterMapper;
    
    @Resource
    private BusiMcuZjDeptMapper busiMcuZjDeptMapper;
    
    @Resource
    private BusiMcuZjClusterMapMapper busiMcuZjClusterMapMapper;

    /**
     * <pre>删除一个MCU</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    public void deleteMcuZj(long bridgeHostId)
    {
        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(bridgeHostId);
        if (mcuZjBridge != null)
        {
            boolean hasMeeting = false;
            for (McuZjConferenceContext conferenceContext : McuZjConferenceContextCache.getInstance().values()) {
                if (conferenceContext.getBusiMcuZj().getId() == mcuZjBridge.getBusiMcuZj().getId()) {
                    hasMeeting = true;
                }
            }

            if (hasMeeting)
            {
                throw new SystemException(1002435, "当前MCU有会议正在进行中，无法删除！");
            }

            BusiMcuZj busiMcuZjCon = new BusiMcuZj();
            busiMcuZjCon.setSpareMcuId(bridgeHostId);
            List<BusiMcuZj> gs = busiMcuZjMapper.selectBusiMcuZjList(busiMcuZjCon);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该MCU作为备用MCU已被其它MCU关联，不能删除！");
            }

            BusiMcuZjDept busiMcuZjDeptCon = new BusiMcuZjDept();
            busiMcuZjDeptCon.setMcuId(bridgeHostId);
            busiMcuZjDeptCon.setMcuType(FmeType.SINGLE_NODE.getValue());
            List<BusiMcuZjDept> busiMcuZjDeptList = busiMcuZjDeptMapper.selectBusiMcuZjDeptList(busiMcuZjDeptCon);
            if (!ObjectUtils.isEmpty(busiMcuZjDeptList))
            {
                throw new SystemException(1000016, "该MCU正在被租户使用，不能删除！");
            }

            BusiMcuZjClusterMap busiMcuZjClusterMapCon = new BusiMcuZjClusterMap();
            busiMcuZjClusterMapCon.setMcuId(bridgeHostId);
            List<BusiMcuZjClusterMap> busiMcuZjClusterMapList = busiMcuZjClusterMapMapper.selectBusiMcuZjClusterMapList(busiMcuZjClusterMapCon);
            if (!ObjectUtils.isEmpty(busiMcuZjClusterMapList))
            {
                throw new SystemException(1000016, "该MCU正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            McuZjBridgeCache.getInstance().delete(mcuZjBridge);
            busiMcuZjMapper.deleteBusiMcuZjById(bridgeHostId);
        }

        logger.info("删除MCU命令下发成功，bridgeHostId: {}", bridgeHostId);
    }

    public BusiMcuZjCluster getMcuZjClusterById(long cid)
    {
        return McuZjClusterCache.getInstance().get(cid);
    }

    /**
     * <pre>新增MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    public synchronized void addMcuZj(BusiMcuZj busiMcuZj)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(busiMcuZj.getIp())
                || ObjectUtils.isEmpty(busiMcuZj.getName()) || ObjectUtils.isEmpty(busiMcuZj.getDevId()) || ObjectUtils.isEmpty(busiMcuZj.getDevToken()))
        {
            throw new SystemException(1000000, "新增的MCU“名称”, “ip”, “端口”, “开发者ID”和“开发者Token”不能为空！");
        }

        if (ObjectUtils.isEmpty(busiMcuZj.getUsername()))
        {
            busiMcuZj.setUsername("ttadmin");
        }

        if (ObjectUtils.isEmpty(busiMcuZj.getPassword()))
        {
            busiMcuZj.setPassword("P@rad1");
        }

        if (ObjectUtils.isEmpty(busiMcuZj.getAdminUsername()))
        {
            busiMcuZj.setAdminUsername("admin");
        }

        if (ObjectUtils.isEmpty(busiMcuZj.getAdminPassword()))
        {
            busiMcuZj.setAdminPassword("P@rad1se");
        }

        if (ObjectUtils.isEmpty(busiMcuZj.getPort()))
        {
            busiMcuZj.setPort(443);
        }

        if (ObjectUtils.isEmpty(busiMcuZj.getMcuDomain())) {
            busiMcuZj.setMcuDomain("51vmr.com");
        }

        if (ObjectUtils.isEmpty(busiMcuZj.getCallPort())) {
            busiMcuZj.setCallPort(5060);
        }

        if (busiMcuZj.getPort() <= 0 || busiMcuZj.getPort() > 65535)
        {
            throw new SystemException(1000103, "端口号不正确");
        }

        if (busiMcuZj.getCallPort() != null) {
            if (busiMcuZj.getCallPort() <= 0 || busiMcuZj.getCallPort() > 65535) {
                throw new SystemException(1000103, "呼叫端口号不正确");
            }
        }

        if (busiMcuZj.getProxyPort() != null) {
            if (busiMcuZj.getProxyPort() <= 0 || busiMcuZj.getProxyPort() > 65535) {
                throw new SystemException(1000103, "代理端口号不正确");
            }
        }

        // 获取MCU组对象
        if (McuZjBridgeCache.getInstance().getMcuZjBridgeByBridgeAddress(McuIDBuilder.build(busiMcuZj)) != null)
        {
            throw new SystemException(1000000, "新增的MCU“" + McuIDBuilder.build(busiMcuZj) + "”已存在，请勿重复添加！");
        }

        BusiMcuZj query = new BusiMcuZj();
        query.setIp(busiMcuZj.getIp());
        query.setPort(busiMcuZj.getPort());
        List<BusiMcuZj> bs = busiMcuZjMapper.selectBusiMcuZjList(query);
        BusiMcuZj old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            busiMcuZj.setId(old.getId());
            busiMcuZj.setUpdateTime(new Date());
        }
        else
        {
            busiMcuZj.setCreateTime(new Date());
        }

        busiMcuZj.setCapacity(busiMcuZj.getCapacity() == null ? 80 : busiMcuZj.getCapacity());
        busiMcuZjMapper.insertBusiMcuZj(busiMcuZj);
        McuZjBridge mcuZjBridge = new McuZjBridge(busiMcuZj);
        McuZjBridgeCache.getInstance().update(mcuZjBridge);
        logger.info("添加MCU并初始化成功：" + busiMcuZj);
    }

    /**
     * <pre>修改MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    public void updateMcuZj(BusiMcuZj busiMcuZj)
    {
        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(busiMcuZj.getId());
        if (mcuZjBridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiMcuZj busiMcuZjExist  = mcuZjBridge.getBusiMcuZj();
            if ((busiMcuZj.getIp() != null && !busiMcuZjExist.getIp().equals(busiMcuZj.getIp()))
                    || (busiMcuZj.getPort() != null && !busiMcuZjExist.getPort().equals(busiMcuZj.getPort()))
                    || (busiMcuZj.getStatus() != null && !busiMcuZjExist.getStatus().equals(busiMcuZj.getStatus())))
            {
                throw new SystemException(1000000, "MCU的“ip”, “端口不支持修改，若是要更改，请删除该MCU，重新创建！");
            }

            if (busiMcuZj.getCallPort() != null) {
                if (busiMcuZj.getCallPort() <= 0 || busiMcuZj.getCallPort() > 65535) {
                    throw new SystemException(1000103, "呼叫端口号不正确");
                }
            }

            if (busiMcuZj.getProxyPort() != null) {
                if (busiMcuZj.getProxyPort() <= 0 || busiMcuZj.getProxyPort() > 65535) {
                    throw new SystemException(1000103, "代理端口号不正确");
                }
            }

            // 设置基本属性
            busiMcuZjExist.setName(busiMcuZj.getName() == null ? busiMcuZjExist.getName() : busiMcuZj.getName());
            busiMcuZjExist.setSpareMcuId(busiMcuZj.getSpareMcuId());
            busiMcuZjExist.setUpdateTime(new Date());
            busiMcuZjExist.setCucmIp(busiMcuZj.getCucmIp());
            busiMcuZjExist.setCallPort(busiMcuZj.getCallPort());
            busiMcuZjExist.setCapacity(busiMcuZj.getCapacity() == null ? 80 : busiMcuZj.getCapacity());
            busiMcuZjExist.setProxyHost(busiMcuZj.getProxyHost() == null ? busiMcuZjExist.getProxyHost() : busiMcuZj.getProxyHost());
            busiMcuZjExist.setProxyPort(busiMcuZj.getProxyPort() == null ? busiMcuZjExist.getProxyPort() : busiMcuZj.getProxyPort());
            busiMcuZjExist.setMcuDomain(busiMcuZj.getMcuDomain());
            if (ObjectUtils.isEmpty(busiMcuZjExist.getMcuDomain())) {
                busiMcuZjExist.setMcuDomain("51vmr.com");
            }

            busiMcuZjMapper.updateBusiMcuZj(busiMcuZjExist);
        }
    }

    /**
     * <pre>新增MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void addBusiMcuZjCluster(BusiMcuZjCluster busiMcuZjCluster)
    {
        busiMcuZjCluster.setCreateTime(new Date());
        busiMcuZjClusterMapper.insertBusiMcuZjCluster(busiMcuZjCluster);

        // 添加缓存
        McuZjClusterCache.getInstance().put(busiMcuZjCluster.getId(), busiMcuZjCluster);
        logger.info("添加MCU组成功: {}", busiMcuZjCluster);
    }

    /**
     * <pre>删除MCU集群</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void deleteBusiMcuZjCluster(long id)
    {
        BusiMcuZjClusterMap busiMcuZjClusterMapCon = new BusiMcuZjClusterMap();
        busiMcuZjClusterMapCon.setClusterId(id);
        List<BusiMcuZjClusterMap> busiMcuZjClusterMapList = busiMcuZjClusterMapMapper.selectBusiMcuZjClusterMapList(busiMcuZjClusterMapCon);
        if (!ObjectUtils.isEmpty(busiMcuZjClusterMapList))
        {
            throw new SystemException(1000013, "MCU集群的删除，请先删除该集群下所有MCU，再删除该MCU集群，最后再重新创建！");
        }

        BusiMcuZjCluster con = new BusiMcuZjCluster();
        con.setSpareMcuType(McuZjType.CLUSTER.getValue());
        con.setSpareMcuId(id);
        List<BusiMcuZjCluster> gs = busiMcuZjClusterMapper.selectBusiMcuZjClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }

        BusiMcuZjDept con1 = new BusiMcuZjDept();
        con1.setMcuId(id);
        con1.setMcuType(McuZjType.CLUSTER.getValue());
        List<BusiMcuZjDept> fds = busiMcuZjDeptMapper.selectBusiMcuZjDeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }

        int c = busiMcuZjClusterMapper.deleteBusiMcuZjClusterById(id);
        if (c > 0)
        {
            McuZjClusterCache.getInstance().remove(id);
            logger.info("删除MCU集群成功，id: {}", id);
        }
    }

    /**
     * <pre>获取所有MCU组</pre>
     * @author lilinhai
     * @since 2021-01-21 15:54
     * @return
     */
    public List<ModelBean> getAllBusiMcuZjCluster()
    {
        List<BusiMcuZjCluster> gs = new ArrayList<>(McuZjClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMcuZjCluster busiMcuZjCluster : gs)
        {
            ModelBean m = new ModelBean(busiMcuZjCluster);
            m.put("bindDeptCount", DeptMcuZjMappingCache.getInstance().getBindDeptCount(McuZjType.CLUSTER, busiMcuZjCluster.getId()));
            ms.add(m);
        }
        return ms;
    }

    /**
     * <pre>修改MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void updateBusiMcuZjCluster(BusiMcuZjCluster busiMcuZjCluster)
    {
        busiMcuZjCluster.setUpdateTime(new Date());
        busiMcuZjClusterMapper.updateBusiMcuZjCluster(busiMcuZjCluster);
        McuZjClusterCache.getInstance().put(busiMcuZjCluster.getId(), busiMcuZjClusterMapper.selectBusiMcuZjClusterById(busiMcuZjCluster.getId()));
        logger.info("修改MCU集群信息成功: {}", busiMcuZjCluster);
    }
    
}
