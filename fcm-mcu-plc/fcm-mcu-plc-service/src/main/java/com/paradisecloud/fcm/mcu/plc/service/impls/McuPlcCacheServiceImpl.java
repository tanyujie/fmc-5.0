/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuPlcCacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.common.enumer.McuPlcType;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.DeptMcuPlcMappingCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcClusterCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.model.core.McuIDBuilder;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IMcuPlcCacheService;
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
public class McuPlcCacheServiceImpl implements IMcuPlcCacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiMcuPlcMapper busiMcuPlcMapper;
    
    @Resource
    private BusiMcuPlcClusterMapper busiMcuPlcClusterMapper;
    
    @Resource
    private BusiMcuPlcDeptMapper busiMcuPlcDeptMapper;
    
    @Resource
    private BusiMcuPlcClusterMapMapper busiMcuPlcClusterMapMapper;

    /**
     * <pre>删除一个MCU</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    public void deleteMcuPlc(long bridgeHostId)
    {
        McuPlcBridge mcuPlcBridge = McuPlcBridgeCache.getInstance().get(bridgeHostId);
        if (mcuPlcBridge != null)
        {
            boolean hasMeeting = false;
            for (McuPlcConferenceContext conferenceContext : McuPlcConferenceContextCache.getInstance().values()) {
                if (conferenceContext.getBusiMcuPlc().getId() == mcuPlcBridge.getBusiMcuPlc().getId()) {
                    hasMeeting = true;
                }
            }

            if (hasMeeting)
            {
                throw new SystemException(1002435, "当前MCU有会议正在进行中，无法删除！");
            }

            BusiMcuPlc busiMcuPlcCon = new BusiMcuPlc();
            busiMcuPlcCon.setSpareMcuId(bridgeHostId);
            List<BusiMcuPlc> gs = busiMcuPlcMapper.selectBusiMcuPlcList(busiMcuPlcCon);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该MCU作为备用MCU已被其它MCU关联，不能删除！");
            }

            BusiMcuPlcDept busiMcuPlcDeptCon = new BusiMcuPlcDept();
            busiMcuPlcDeptCon.setMcuId(bridgeHostId);
            busiMcuPlcDeptCon.setMcuType(FmeType.SINGLE_NODE.getValue());
            List<BusiMcuPlcDept> busiMcuPlcDeptList = busiMcuPlcDeptMapper.selectBusiMcuPlcDeptList(busiMcuPlcDeptCon);
            if (!ObjectUtils.isEmpty(busiMcuPlcDeptList))
            {
                throw new SystemException(1000016, "该MCU正在被租户使用，不能删除！");
            }

            BusiMcuPlcClusterMap busiMcuPlcClusterMapCon = new BusiMcuPlcClusterMap();
            busiMcuPlcClusterMapCon.setMcuId(bridgeHostId);
            List<BusiMcuPlcClusterMap> busiMcuPlcClusterMapList = busiMcuPlcClusterMapMapper.selectBusiMcuPlcClusterMapList(busiMcuPlcClusterMapCon);
            if (!ObjectUtils.isEmpty(busiMcuPlcClusterMapList))
            {
                throw new SystemException(1000016, "该MCU正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            McuPlcBridgeCache.getInstance().delete(mcuPlcBridge);
            busiMcuPlcMapper.deleteBusiMcuPlcById(bridgeHostId);
        }

        logger.info("删除MCU命令下发成功，bridgeHostId: {}", bridgeHostId);
    }

    public BusiMcuPlcCluster getMcuPlcClusterById(long cid)
    {
        return McuPlcClusterCache.getInstance().get(cid);
    }

    /**
     * <pre>新增MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    public synchronized void addMcuPlc(BusiMcuPlc busiMcuPlc)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(busiMcuPlc.getIp())
                || ObjectUtils.isEmpty(busiMcuPlc.getName()) || ObjectUtils.isEmpty(busiMcuPlc.getProxyHost()) || ObjectUtils.isEmpty(busiMcuPlc.getProxyPort()))
        {
            throw new SystemException(1000000, "新增的MCU“名称”, “ip”, “代理服务器地址”和“代理服务器端口”不能为空！");
        }

        if (ObjectUtils.isEmpty(busiMcuPlc.getUsername()))
        {
            busiMcuPlc.setUsername("ttadmin");
        }

        if (ObjectUtils.isEmpty(busiMcuPlc.getPassword()))
        {
            busiMcuPlc.setPassword("P@rad1se");
        }

        if (ObjectUtils.isEmpty(busiMcuPlc.getAdminUsername()))
        {
            busiMcuPlc.setAdminUsername("admin");
        }

        if (ObjectUtils.isEmpty(busiMcuPlc.getAdminPassword()))
        {
            busiMcuPlc.setAdminPassword("P@rad1se");
        }

        if (ObjectUtils.isEmpty(busiMcuPlc.getPort()))
        {
            busiMcuPlc.setPort(80);
        }

        if (ObjectUtils.isEmpty(busiMcuPlc.getCallPort())) {
            busiMcuPlc.setCallPort(5060);
        }

        if (busiMcuPlc.getPort() <= 0 || busiMcuPlc.getPort() > 65535)
        {
            throw new SystemException(1000103, "端口号不正确");
        }

        if (busiMcuPlc.getCallPort() != null) {
            if (busiMcuPlc.getCallPort() <= 0 || busiMcuPlc.getCallPort() > 65535) {
                throw new SystemException(1000103, "呼叫端口号不正确");
            }
        }

        if (busiMcuPlc.getProxyPort() != null) {
            if (busiMcuPlc.getProxyPort() <= 0 || busiMcuPlc.getProxyPort() > 65535) {
                throw new SystemException(1000103, "代理端口号不正确");
            }
        }

        // 获取MCU组对象
        if (McuPlcBridgeCache.getInstance().getMcuPlcBridgeByBridgeAddress(McuIDBuilder.build(busiMcuPlc)) != null)
        {
            throw new SystemException(1000000, "新增的MCU“" + McuIDBuilder.build(busiMcuPlc) + "”已存在，请勿重复添加！");
        }

        BusiMcuPlc query = new BusiMcuPlc();
        query.setIp(busiMcuPlc.getIp());
        query.setPort(busiMcuPlc.getPort());
        List<BusiMcuPlc> bs = busiMcuPlcMapper.selectBusiMcuPlcList(query);
        BusiMcuPlc old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            busiMcuPlc.setId(old.getId());
            busiMcuPlc.setUpdateTime(new Date());
        }
        else
        {
            busiMcuPlc.setCreateTime(new Date());
        }

        busiMcuPlc.setCapacity(busiMcuPlc.getCapacity() == null ? 80 : busiMcuPlc.getCapacity());
        busiMcuPlcMapper.insertBusiMcuPlc(busiMcuPlc);
        McuPlcBridge mcuPlcBridge = new McuPlcBridge(busiMcuPlc);
        McuPlcBridgeCache.getInstance().update(mcuPlcBridge);
        logger.info("添加MCU并初始化成功：" + busiMcuPlc);
    }

    /**
     * <pre>修改MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    public void updateMcuPlc(BusiMcuPlc busiMcuPlc)
    {
        McuPlcBridge mcuPlcBridge = McuPlcBridgeCache.getInstance().get(busiMcuPlc.getId());
        if (mcuPlcBridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiMcuPlc busiMcuPlcExist  = mcuPlcBridge.getBusiMcuPlc();
            if ((busiMcuPlc.getIp() != null && !busiMcuPlcExist.getIp().equals(busiMcuPlc.getIp()))
                    || (busiMcuPlc.getPort() != null && !busiMcuPlcExist.getPort().equals(busiMcuPlc.getPort()))
                    || (busiMcuPlc.getStatus() != null && !busiMcuPlcExist.getStatus().equals(busiMcuPlc.getStatus())))
            {
                throw new SystemException(1000000, "MCU的“ip”, “端口不支持修改，若是要更改，请删除该MCU，重新创建！");
            }

            if (busiMcuPlc.getCallPort() != null) {
                if (busiMcuPlc.getCallPort() <= 0 || busiMcuPlc.getCallPort() > 65535) {
                    throw new SystemException(1000103, "呼叫端口号不正确");
                }
            }

            if (busiMcuPlc.getProxyPort() != null) {
                if (busiMcuPlc.getProxyPort() <= 0 || busiMcuPlc.getProxyPort() > 65535) {
                    throw new SystemException(1000103, "代理端口号不正确");
                }
            }

            // 设置基本属性
            busiMcuPlcExist.setName(busiMcuPlc.getName() == null ? busiMcuPlcExist.getName() : busiMcuPlc.getName());
            busiMcuPlcExist.setSpareMcuId(busiMcuPlc.getSpareMcuId());
            busiMcuPlcExist.setUpdateTime(new Date());
            busiMcuPlcExist.setCucmIp(busiMcuPlc.getCucmIp());
            busiMcuPlcExist.setCapacity(busiMcuPlc.getCapacity() == null ? 80 : busiMcuPlc.getCapacity());
            busiMcuPlcExist.setProxyHost(busiMcuPlc.getProxyHost() == null ? busiMcuPlcExist.getProxyHost() : busiMcuPlc.getProxyHost());
            busiMcuPlcExist.setProxyPort(busiMcuPlc.getProxyPort() == null ? busiMcuPlcExist.getProxyPort() : busiMcuPlc.getProxyPort());
            if (ObjectUtils.isEmpty(busiMcuPlcExist.getMcuDomain())) {
                busiMcuPlcExist.setMcuDomain("51vmr.com");
            }

            busiMcuPlcMapper.updateBusiMcuPlc(busiMcuPlcExist);
        }
    }

    /**
     * <pre>新增MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void addBusiMcuPlcCluster(BusiMcuPlcCluster busiMcuPlcCluster)
    {
        busiMcuPlcCluster.setCreateTime(new Date());
        busiMcuPlcClusterMapper.insertBusiMcuPlcCluster(busiMcuPlcCluster);

        // 添加缓存
        McuPlcClusterCache.getInstance().put(busiMcuPlcCluster.getId(), busiMcuPlcCluster);
        logger.info("添加MCU组成功: {}", busiMcuPlcCluster);
    }

    /**
     * <pre>删除MCU集群</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void deleteBusiMcuPlcCluster(long id)
    {
        BusiMcuPlcClusterMap busiMcuPlcClusterMapCon = new BusiMcuPlcClusterMap();
        busiMcuPlcClusterMapCon.setClusterId(id);
        List<BusiMcuPlcClusterMap> busiMcuPlcClusterMapList = busiMcuPlcClusterMapMapper.selectBusiMcuPlcClusterMapList(busiMcuPlcClusterMapCon);
        if (!ObjectUtils.isEmpty(busiMcuPlcClusterMapList))
        {
            throw new SystemException(1000013, "MCU集群的删除，请先删除该集群下所有MCU，再删除该MCU集群，最后再重新创建！");
        }

        BusiMcuPlcCluster con = new BusiMcuPlcCluster();
        con.setSpareMcuType(McuPlcType.CLUSTER.getValue());
        con.setSpareMcuId(id);
        List<BusiMcuPlcCluster> gs = busiMcuPlcClusterMapper.selectBusiMcuPlcClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }

        BusiMcuPlcDept con1 = new BusiMcuPlcDept();
        con1.setMcuId(id);
        con1.setMcuType(McuPlcType.CLUSTER.getValue());
        List<BusiMcuPlcDept> fds = busiMcuPlcDeptMapper.selectBusiMcuPlcDeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }

        int c = busiMcuPlcClusterMapper.deleteBusiMcuPlcClusterById(id);
        if (c > 0)
        {
            McuPlcClusterCache.getInstance().remove(id);
            logger.info("删除MCU集群成功，id: {}", id);
        }
    }

    /**
     * <pre>获取所有MCU组</pre>
     * @author lilinhai
     * @since 2021-01-21 15:54
     * @return
     */
    public List<ModelBean> getAllBusiMcuPlcCluster()
    {
        List<BusiMcuPlcCluster> gs = new ArrayList<>(McuPlcClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMcuPlcCluster busiMcuPlcCluster : gs)
        {
            ModelBean m = new ModelBean(busiMcuPlcCluster);
            m.put("bindDeptCount", DeptMcuPlcMappingCache.getInstance().getBindDeptCount(McuPlcType.CLUSTER, busiMcuPlcCluster.getId()));
            ms.add(m);
        }
        return ms;
    }

    /**
     * <pre>修改MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void updateBusiMcuPlcCluster(BusiMcuPlcCluster busiMcuPlcCluster)
    {
        busiMcuPlcCluster.setUpdateTime(new Date());
        busiMcuPlcClusterMapper.updateBusiMcuPlcCluster(busiMcuPlcCluster);
        McuPlcClusterCache.getInstance().put(busiMcuPlcCluster.getId(), busiMcuPlcClusterMapper.selectBusiMcuPlcClusterById(busiMcuPlcCluster.getId()));
        logger.info("修改MCU集群信息成功: {}", busiMcuPlcCluster);
    }
    
}
