/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuZteCacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version  V1.0
 */
package com.paradisecloud.fcm.zte.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.common.enumer.McuZteType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuZte;
import com.paradisecloud.fcm.dao.model.BusiMcuZteCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuZteClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMcuZteDept;
import com.paradisecloud.fcm.zte.cache.DeptMcuZteMappingCache;
import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.McuZteClusterCache;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.core.McuIDBuilder;
import com.paradisecloud.fcm.zte.service.interfaces.IMcuZteCacheService;
import com.sinhy.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>MCU缓存业务处理实现类</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0
 */
@Service
public class McuZteCacheServiceImpl implements IMcuZteCacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiMcuZteMapper busiMcuZteMapper;

    @Resource
    private BusiMcuZteClusterMapper busiMcuZteClusterMapper;

    @Resource
    private BusiMcuZteDeptMapper busiMcuZteDeptMapper;

    @Resource
    private BusiMcuZteClusterMapMapper busiMcuZteClusterMapMapper;

    /**
     * <pre>删除一个MCU</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    @Override
    public void deleteMcuZte(long bridgeHostId)
    {
        McuZteBridge mcuZteBridge = McuZteBridgeCache.getInstance().get(bridgeHostId);
        if (mcuZteBridge != null)
        {
            boolean hasMeeting = false;
            for (McuZteConferenceContext conferenceContext : McuZteConferenceContextCache.getInstance().values()) {
                if (conferenceContext.getBusiMcuZte().getId() == mcuZteBridge.getBusiMcuZte().getId()) {
                    hasMeeting = true;
                }
            }

            if (hasMeeting)
            {
                throw new SystemException(1002435, "当前MCU有会议正在进行中，无法删除！");
            }

            BusiMcuZte busiMcuZteCon = new BusiMcuZte();
            busiMcuZteCon.setSpareMcuId(bridgeHostId);
            List<BusiMcuZte> gs = busiMcuZteMapper.selectBusiMcuZteList(busiMcuZteCon);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该MCU作为备用MCU已被其它MCU关联，不能删除！");
            }

            BusiMcuZteDept busiMcuZteDeptCon = new BusiMcuZteDept();
            busiMcuZteDeptCon.setMcuId(bridgeHostId);
            busiMcuZteDeptCon.setMcuType(FmeType.SINGLE_NODE.getValue());
            List<BusiMcuZteDept> busiMcuZteDeptList = busiMcuZteDeptMapper.selectBusiMcuZteDeptList(busiMcuZteDeptCon);
            if (!ObjectUtils.isEmpty(busiMcuZteDeptList))
            {
                throw new SystemException(1000016, "该MCU正在被租户使用，不能删除！");
            }

            BusiMcuZteClusterMap busiMcuZteClusterMapCon = new BusiMcuZteClusterMap();
            busiMcuZteClusterMapCon.setMcuId(bridgeHostId);
            List<BusiMcuZteClusterMap> busiMcuZteClusterMapList = busiMcuZteClusterMapMapper.selectBusiMcuZteClusterMapList(busiMcuZteClusterMapCon);
            if (!ObjectUtils.isEmpty(busiMcuZteClusterMapList))
            {
                throw new SystemException(1000016, "该MCU正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            McuZteBridgeCache.getInstance().delete(mcuZteBridge);
            busiMcuZteMapper.deleteBusiMcuZteById(bridgeHostId);
        }

        logger.info("删除MCU命令下发成功，bridgeHostId: {}", bridgeHostId);
    }

    @Override
    public BusiMcuZteCluster getMcuZteClusterById(long cid)
    {
        return McuZteClusterCache.getInstance().get(cid);
    }

    /**
     * <pre>新增MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    @Override
    @Transactional
    public synchronized void addMcuZte(BusiMcuZte busiMcuZte)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(busiMcuZte.getIp())
                || ObjectUtils.isEmpty(busiMcuZte.getName()) || ObjectUtils.isEmpty(busiMcuZte.getPort())|| ObjectUtils.isEmpty(busiMcuZte.getUsername())|| ObjectUtils.isEmpty(busiMcuZte.getPassword()))
        {
            throw new SystemException(1000000, "新增的MCU“名称”, “ip”, “端口”和“账户密码”不能为空！");
        }

        if (ObjectUtils.isEmpty(busiMcuZte.getPort()))
        {
            busiMcuZte.setPort(8080);
        }

        if (ObjectUtils.isEmpty(busiMcuZte.getCallPort())) {
            busiMcuZte.setCallPort(5060);
        }

        if (busiMcuZte.getPort() <= 0 || busiMcuZte.getPort() > 65535)
        {
            throw new SystemException(1000103, "端口号不正确");
        }

        if (busiMcuZte.getCallPort() != null) {
            if (busiMcuZte.getCallPort() <= 0 || busiMcuZte.getCallPort() > 65535) {
                throw new SystemException(1000103, "呼叫端口号不正确");
            }
        }

        if (busiMcuZte.getProxyPort() != null) {
            if (busiMcuZte.getProxyPort() <= 0 || busiMcuZte.getProxyPort() > 65535) {
                throw new SystemException(1000103, "代理端口号不正确");
            }
        }

        // 获取MCU组对象
        if (McuZteBridgeCache.getInstance().getMcuZteBridgeByBridgeAddress(McuIDBuilder.build(busiMcuZte)) != null)
        {
            throw new SystemException(1000000, "新增的MCU“" + McuIDBuilder.build(busiMcuZte) + "”已存在，请勿重复添加！");
        }

        BusiMcuZte query = new BusiMcuZte();
        query.setIp(busiMcuZte.getIp());
        query.setPort(busiMcuZte.getPort());
        List<BusiMcuZte> bs = busiMcuZteMapper.selectBusiMcuZteList(query);
        BusiMcuZte old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            busiMcuZte.setId(old.getId());
            busiMcuZte.setUpdateTime(new Date());
        }
        else
        {
            busiMcuZte.setCreateTime(new Date());
        }

        busiMcuZte.setCapacity(busiMcuZte.getCapacity() == null ? 80 : busiMcuZte.getCapacity());
        busiMcuZteMapper.insertBusiMcuZte(busiMcuZte);
        McuZteBridge mcuZteBridge = new McuZteBridge(busiMcuZte);
        McuZteBridgeCache.getInstance().update(mcuZteBridge);
        logger.info("添加MCU并初始化成功：" + busiMcuZte);
    }

    /**
     * <pre>修改MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    @Override
    public void updateMcuZte(BusiMcuZte busiMcuZte)
    {
        McuZteBridge mcuZteBridge = McuZteBridgeCache.getInstance().get(busiMcuZte.getId());
        if (mcuZteBridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiMcuZte busiMcuZteExist  = mcuZteBridge.getBusiMcuZte();
            if ((busiMcuZte.getIp() != null && !busiMcuZteExist.getIp().equals(busiMcuZte.getIp()))
                    || (busiMcuZte.getPort() != null && !busiMcuZteExist.getPort().equals(busiMcuZte.getPort()))
                    || (busiMcuZte.getStatus() != null && !busiMcuZteExist.getStatus().equals(busiMcuZte.getStatus())))
            {
                throw new SystemException(1000000, "MCU的“ip”, “端口不支持修改，若是要更改，请删除该MCU，重新创建！");
            }

            if (busiMcuZte.getCallPort() != null) {
                if (busiMcuZte.getCallPort() <= 0 || busiMcuZte.getCallPort() > 65535) {
                    throw new SystemException(1000103, "呼叫端口号不正确");
                }
            }

            if (busiMcuZte.getProxyPort() != null) {
                if (busiMcuZte.getProxyPort() <= 0 || busiMcuZte.getProxyPort() > 65535) {
                    throw new SystemException(1000103, "代理端口号不正确");
                }
            }

            // 设置基本属性
            busiMcuZteExist.setName(busiMcuZte.getName() == null ? busiMcuZteExist.getName() : busiMcuZte.getName());
            busiMcuZteExist.setSpareMcuId(busiMcuZte.getSpareMcuId());
            busiMcuZteExist.setUpdateTime(new Date());
            busiMcuZteExist.setCucmIp(busiMcuZte.getCucmIp());
            busiMcuZteExist.setCapacity(busiMcuZte.getCapacity() == null ? 80 : busiMcuZte.getCapacity());
            busiMcuZteExist.setProxyHost(busiMcuZte.getProxyHost() == null ? busiMcuZteExist.getProxyHost() : busiMcuZte.getProxyHost());
            busiMcuZteExist.setProxyPort(busiMcuZte.getProxyPort() == null ? busiMcuZteExist.getProxyPort() : busiMcuZte.getProxyPort());
            if (ObjectUtils.isEmpty(busiMcuZteExist.getMcuDomain())) {
                busiMcuZteExist.setMcuDomain("ttcloud.com");
            }

            busiMcuZteMapper.updateBusiMcuZte(busiMcuZteExist);
        }
    }

    /**
     * <pre>新增MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    @Override
    public void addBusiMcuZteCluster(BusiMcuZteCluster busiMcuZteCluster)
    {
        busiMcuZteCluster.setCreateTime(new Date());
        busiMcuZteClusterMapper.insertBusiMcuZteCluster(busiMcuZteCluster);

        // 添加缓存
        McuZteClusterCache.getInstance().put(busiMcuZteCluster.getId(), busiMcuZteCluster);
        logger.info("添加MCU组成功: {}", busiMcuZteCluster);
    }

    /**
     * <pre>删除MCU集群</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    @Override
    public void deleteBusiMcuZteCluster(long id)
    {
        BusiMcuZteClusterMap busiMcuZteClusterMapCon = new BusiMcuZteClusterMap();
        busiMcuZteClusterMapCon.setClusterId(id);
        List<BusiMcuZteClusterMap> busiMcuZteClusterMapList = busiMcuZteClusterMapMapper.selectBusiMcuZteClusterMapList(busiMcuZteClusterMapCon);
        if (!ObjectUtils.isEmpty(busiMcuZteClusterMapList))
        {
            throw new SystemException(1000013, "MCU集群的删除，请先删除该集群下所有MCU，再删除该MCU集群，最后再重新创建！");
        }

        BusiMcuZteCluster con = new BusiMcuZteCluster();
        con.setSpareMcuType(McuZteType.CLUSTER.getValue());
        con.setSpareMcuId(id);
        List<BusiMcuZteCluster> gs = busiMcuZteClusterMapper.selectBusiMcuZteClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }

        BusiMcuZteDept con1 = new BusiMcuZteDept();
        con1.setMcuId(id);
        con1.setMcuType(McuZteType.CLUSTER.getValue());
        List<BusiMcuZteDept> fds = busiMcuZteDeptMapper.selectBusiMcuZteDeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }

        int c = busiMcuZteClusterMapper.deleteBusiMcuZteClusterById(id);
        if (c > 0)
        {
            McuZteClusterCache.getInstance().remove(id);
            logger.info("删除MCU集群成功，id: {}", id);
        }
    }

    /**
     * <pre>获取所有MCU组</pre>
     * @author lilinhai
     * @since 2021-01-21 15:54
     * @return
     */
    @Override
    public List<ModelBean> getAllBusiMcuZteCluster()
    {
        List<BusiMcuZteCluster> gs = new ArrayList<>(McuZteClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMcuZteCluster busiMcuZteCluster : gs)
        {
            ModelBean m = new ModelBean(busiMcuZteCluster);
            m.put("bindDeptCount", DeptMcuZteMappingCache.getInstance().getBindDeptCount(McuZteType.CLUSTER, busiMcuZteCluster.getId()));
            ms.add(m);
        }
        return ms;
    }

    /**
     * <pre>修改MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    @Override
    public void updateBusiMcuZteCluster(BusiMcuZteCluster busiMcuZteCluster)
    {
        busiMcuZteCluster.setUpdateTime(new Date());
        busiMcuZteClusterMapper.updateBusiMcuZteCluster(busiMcuZteCluster);
        McuZteClusterCache.getInstance().put(busiMcuZteCluster.getId(), busiMcuZteClusterMapper.selectBusiMcuZteClusterById(busiMcuZteCluster.getId()));
        logger.info("修改MCU集群信息成功: {}", busiMcuZteCluster);
    }

}
