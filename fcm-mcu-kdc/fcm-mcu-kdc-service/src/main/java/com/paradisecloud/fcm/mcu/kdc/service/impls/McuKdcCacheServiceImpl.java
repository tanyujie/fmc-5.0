/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuKdcCacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.common.enumer.McuKdcType;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.DeptMcuKdcMappingCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcClusterCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.model.core.McuIDBuilder;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IMcuKdcCacheService;
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
public class McuKdcCacheServiceImpl implements IMcuKdcCacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiMcuKdcMapper busiMcuKdcMapper;
    
    @Resource
    private BusiMcuKdcClusterMapper busiMcuKdcClusterMapper;
    
    @Resource
    private BusiMcuKdcDeptMapper busiMcuKdcDeptMapper;
    
    @Resource
    private BusiMcuKdcClusterMapMapper busiMcuKdcClusterMapMapper;

    /**
     * <pre>删除一个MCU</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    public void deleteMcuKdc(long bridgeHostId)
    {
        McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().get(bridgeHostId);
        if (mcuKdcBridge != null)
        {
            boolean hasMeeting = false;
            for (McuKdcConferenceContext conferenceContext : McuKdcConferenceContextCache.getInstance().values()) {
                if (conferenceContext.getBusiMcuKdc().getId() == mcuKdcBridge.getBusiMcuKdc().getId()) {
                    hasMeeting = true;
                }
            }

            if (hasMeeting)
            {
                throw new SystemException(1002435, "当前MCU有会议正在进行中，无法删除！");
            }

            BusiMcuKdc busiMcuKdcCon = new BusiMcuKdc();
            busiMcuKdcCon.setSpareMcuId(bridgeHostId);
            List<BusiMcuKdc> gs = busiMcuKdcMapper.selectBusiMcuKdcList(busiMcuKdcCon);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该MCU作为备用MCU已被其它MCU关联，不能删除！");
            }

            BusiMcuKdcDept busiMcuKdcDeptCon = new BusiMcuKdcDept();
            busiMcuKdcDeptCon.setMcuId(bridgeHostId);
            busiMcuKdcDeptCon.setMcuType(FmeType.SINGLE_NODE.getValue());
            List<BusiMcuKdcDept> busiMcuKdcDeptList = busiMcuKdcDeptMapper.selectBusiMcuKdcDeptList(busiMcuKdcDeptCon);
            if (!ObjectUtils.isEmpty(busiMcuKdcDeptList))
            {
                throw new SystemException(1000016, "该MCU正在被租户使用，不能删除！");
            }

            BusiMcuKdcClusterMap busiMcuKdcClusterMapCon = new BusiMcuKdcClusterMap();
            busiMcuKdcClusterMapCon.setMcuId(bridgeHostId);
            List<BusiMcuKdcClusterMap> busiMcuKdcClusterMapList = busiMcuKdcClusterMapMapper.selectBusiMcuKdcClusterMapList(busiMcuKdcClusterMapCon);
            if (!ObjectUtils.isEmpty(busiMcuKdcClusterMapList))
            {
                throw new SystemException(1000016, "该MCU正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            McuKdcBridgeCache.getInstance().delete(mcuKdcBridge);
            busiMcuKdcMapper.deleteBusiMcuKdcById(bridgeHostId);
        }

        logger.info("删除MCU命令下发成功，bridgeHostId: {}", bridgeHostId);
    }

    public BusiMcuKdcCluster getMcuKdcClusterById(long cid)
    {
        return McuKdcClusterCache.getInstance().get(cid);
    }

    /**
     * <pre>新增MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    public synchronized void addMcuKdc(BusiMcuKdc busiMcuKdc)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(busiMcuKdc.getIp())
                || ObjectUtils.isEmpty(busiMcuKdc.getName()) || ObjectUtils.isEmpty(busiMcuKdc.getDevKey()) || ObjectUtils.isEmpty(busiMcuKdc.getDevValue()))
        {
            throw new SystemException(1000000, "新增的MCU“名称”, “ip”, “开发者KEY”和“开发者VALUE”不能为空！");
        }

        if (ObjectUtils.isEmpty(busiMcuKdc.getUsername()))
        {
            busiMcuKdc.setUsername("ttadmin");
        }

        if (ObjectUtils.isEmpty(busiMcuKdc.getPassword()))
        {
            busiMcuKdc.setPassword("P@rad1se");
        }

        if (ObjectUtils.isEmpty(busiMcuKdc.getAdminUsername()))
        {
            busiMcuKdc.setAdminUsername("admin");
        }

        if (ObjectUtils.isEmpty(busiMcuKdc.getAdminPassword()))
        {
            busiMcuKdc.setAdminPassword("P@rad1se");
        }

        if (ObjectUtils.isEmpty(busiMcuKdc.getPort()))
        {
            busiMcuKdc.setPort(80);
        }

        if (ObjectUtils.isEmpty(busiMcuKdc.getCallPort())) {
            busiMcuKdc.setCallPort(5060);
        }

        if (busiMcuKdc.getPort() <= 0 || busiMcuKdc.getPort() > 65535)
        {
            throw new SystemException(1000103, "端口号不正确");
        }

        if (busiMcuKdc.getCallPort() != null) {
            if (busiMcuKdc.getCallPort() <= 0 || busiMcuKdc.getCallPort() > 65535) {
                throw new SystemException(1000103, "呼叫端口号不正确");
            }
        }

        // 获取MCU组对象
        if (McuKdcBridgeCache.getInstance().getMcuKdcBridgeByBridgeAddress(McuIDBuilder.build(busiMcuKdc)) != null)
        {
            throw new SystemException(1000000, "新增的MCU“" + McuIDBuilder.build(busiMcuKdc) + "”已存在，请勿重复添加！");
        }

        BusiMcuKdc query = new BusiMcuKdc();
        query.setIp(busiMcuKdc.getIp());
        query.setPort(busiMcuKdc.getPort());
        List<BusiMcuKdc> bs = busiMcuKdcMapper.selectBusiMcuKdcList(query);
        BusiMcuKdc old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            busiMcuKdc.setId(old.getId());
            busiMcuKdc.setUpdateTime(new Date());
        }
        else
        {
            busiMcuKdc.setCreateTime(new Date());
        }

        busiMcuKdc.setCapacity(busiMcuKdc.getCapacity() == null ? 80 : busiMcuKdc.getCapacity());
        busiMcuKdcMapper.insertBusiMcuKdc(busiMcuKdc);
        McuKdcBridge mcuKdcBridge = new McuKdcBridge(busiMcuKdc);
        McuKdcBridgeCache.getInstance().update(mcuKdcBridge);
        logger.info("添加MCU并初始化成功：" + busiMcuKdc);
    }

    /**
     * <pre>修改MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    public void updateMcuKdc(BusiMcuKdc busiMcuKdc)
    {
        McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().get(busiMcuKdc.getId());
        if (mcuKdcBridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiMcuKdc busiMcuKdcExist  = mcuKdcBridge.getBusiMcuKdc();
            if ((busiMcuKdc.getIp() != null && !busiMcuKdcExist.getIp().equals(busiMcuKdc.getIp()))
                    || (busiMcuKdc.getPort() != null && !busiMcuKdcExist.getPort().equals(busiMcuKdc.getPort()))
                    || (busiMcuKdc.getStatus() != null && !busiMcuKdcExist.getStatus().equals(busiMcuKdc.getStatus())))
            {
                throw new SystemException(1000000, "MCU的“ip”, “端口不支持修改，若是要更改，请删除该MCU，重新创建！");
            }

            if (busiMcuKdc.getCallPort() != null) {
                if (busiMcuKdc.getCallPort() <= 0 || busiMcuKdc.getCallPort() > 65535) {
                    throw new SystemException(1000103, "呼叫端口号不正确");
                }
            }

            // 设置基本属性
            busiMcuKdcExist.setName(busiMcuKdc.getName() == null ? busiMcuKdcExist.getName() : busiMcuKdc.getName());
            busiMcuKdcExist.setSpareMcuId(busiMcuKdc.getSpareMcuId());
            busiMcuKdcExist.setUpdateTime(new Date());
            busiMcuKdcExist.setCucmIp(busiMcuKdc.getCucmIp());
            busiMcuKdcExist.setCapacity(busiMcuKdc.getCapacity() == null ? 80 : busiMcuKdc.getCapacity());
            if (ObjectUtils.isEmpty(busiMcuKdcExist.getMcuDomain())) {
                busiMcuKdcExist.setMcuDomain("51vmr.com");
            }

            busiMcuKdcMapper.updateBusiMcuKdc(busiMcuKdcExist);
        }
    }

    /**
     * <pre>新增MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void addBusiMcuKdcCluster(BusiMcuKdcCluster busiMcuKdcCluster)
    {
        busiMcuKdcCluster.setCreateTime(new Date());
        busiMcuKdcClusterMapper.insertBusiMcuKdcCluster(busiMcuKdcCluster);

        // 添加缓存
        McuKdcClusterCache.getInstance().put(busiMcuKdcCluster.getId(), busiMcuKdcCluster);
        logger.info("添加MCU组成功: {}", busiMcuKdcCluster);
    }

    /**
     * <pre>删除MCU集群</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void deleteBusiMcuKdcCluster(long id)
    {
        BusiMcuKdcClusterMap busiMcuKdcClusterMapCon = new BusiMcuKdcClusterMap();
        busiMcuKdcClusterMapCon.setClusterId(id);
        List<BusiMcuKdcClusterMap> busiMcuKdcClusterMapList = busiMcuKdcClusterMapMapper.selectBusiMcuKdcClusterMapList(busiMcuKdcClusterMapCon);
        if (!ObjectUtils.isEmpty(busiMcuKdcClusterMapList))
        {
            throw new SystemException(1000013, "MCU集群的删除，请先删除该集群下所有MCU，再删除该MCU集群，最后再重新创建！");
        }

        BusiMcuKdcCluster con = new BusiMcuKdcCluster();
        con.setSpareMcuType(McuKdcType.CLUSTER.getValue());
        con.setSpareMcuId(id);
        List<BusiMcuKdcCluster> gs = busiMcuKdcClusterMapper.selectBusiMcuKdcClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }

        BusiMcuKdcDept con1 = new BusiMcuKdcDept();
        con1.setMcuId(id);
        con1.setMcuType(McuKdcType.CLUSTER.getValue());
        List<BusiMcuKdcDept> fds = busiMcuKdcDeptMapper.selectBusiMcuKdcDeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }

        int c = busiMcuKdcClusterMapper.deleteBusiMcuKdcClusterById(id);
        if (c > 0)
        {
            McuKdcClusterCache.getInstance().remove(id);
            logger.info("删除MCU集群成功，id: {}", id);
        }
    }

    /**
     * <pre>获取所有MCU组</pre>
     * @author lilinhai
     * @since 2021-01-21 15:54
     * @return
     */
    public List<ModelBean> getAllBusiMcuKdcCluster()
    {
        List<BusiMcuKdcCluster> gs = new ArrayList<>(McuKdcClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMcuKdcCluster busiMcuKdcCluster : gs)
        {
            ModelBean m = new ModelBean(busiMcuKdcCluster);
            m.put("bindDeptCount", DeptMcuKdcMappingCache.getInstance().getBindDeptCount(McuKdcType.CLUSTER, busiMcuKdcCluster.getId()));
            ms.add(m);
        }
        return ms;
    }

    /**
     * <pre>修改MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void updateBusiMcuKdcCluster(BusiMcuKdcCluster busiMcuKdcCluster)
    {
        busiMcuKdcCluster.setUpdateTime(new Date());
        busiMcuKdcClusterMapper.updateBusiMcuKdcCluster(busiMcuKdcCluster);
        McuKdcClusterCache.getInstance().put(busiMcuKdcCluster.getId(), busiMcuKdcClusterMapper.selectBusiMcuKdcClusterById(busiMcuKdcCluster.getId()));
        logger.info("修改MCU集群信息成功: {}", busiMcuKdcCluster);
    }
    
}
