/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuTencentCacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version  V1.0
 */
package com.paradisecloud.fcm.tencent.service2.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.tencent.cache.*;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuTencent;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentDept;
import com.paradisecloud.fcm.tencent.service2.interfaces.IMcuTencentCacheService;
import com.sinhy.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <pre>MCU缓存业务处理实现类</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0
 */
@Service
public class McuTencentCacheServiceImpl implements IMcuTencentCacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiMcuTencentMapper busiMcuTencentMapper;

    @Resource
    private BusiMcuTencentClusterMapper busiMcuTencentClusterMapper;

    @Resource
    private BusiMcuTencentDeptMapper busiMcuTencentDeptMapper;

    @Resource
    private BusiMcuTencentClusterMapMapper busiMcuTencentClusterMapMapper;

    /**
     * <pre>删除一个MCU</pre>
     * @author lilinhai
     * @since 2020-12-20 11:12  void
     */
    @Override
    public void deleteMcuTencent(Long bridgeHostId)
    {
        TencentBridge TencentBridge = TencentBridgeCache.getInstance().getTencentBridgeMap().get(bridgeHostId);
        if (TencentBridge != null)
        {
            for (TencentConferenceContext conferenceContext : TencentConferenceContextCache.getInstance().values()) {
                if (Objects.equals(conferenceContext.getTencentBridge().getBusiTencent().getId(),TencentBridge.getBusiTencent().getId())) {
                    throw new SystemException(1002425, "当前MCU有会议正在进行中，无法删除！");
                }
            }

            BusiMcuTencent busiMcuTencentCon = new BusiMcuTencent();
            busiMcuTencentCon.setSpareSmcId(bridgeHostId);
            List<BusiMcuTencent> gs = busiMcuTencentMapper.selectBusiMcuTencentList(busiMcuTencentCon);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该MCU作为备用MCU已被其它MCU关联，不能删除！");
            }

            BusiMcuTencentDept busiMcuTencentDeptCon = new BusiMcuTencentDept();
            busiMcuTencentDeptCon.setMcuId(bridgeHostId);
            busiMcuTencentDeptCon.setMcuType(FmeType.SINGLE_NODE.getValue());
            List<BusiMcuTencentDept> busiMcuTencentDeptList = busiMcuTencentDeptMapper.selectBusiMcuTencentDeptList(busiMcuTencentDeptCon);
            if (!ObjectUtils.isEmpty(busiMcuTencentDeptList))
            {
                throw new SystemException(1000016, "该MCU正在被租户使用，不能删除！");
            }

            BusiMcuTencentClusterMap busiMcuTencentClusterMapCon = new BusiMcuTencentClusterMap();
            busiMcuTencentClusterMapCon.setMcuId(bridgeHostId);
            List<BusiMcuTencentClusterMap> busiMcuTencentClusterMapList = busiMcuTencentClusterMapMapper.selectBusiMcuTencentClusterMapList(busiMcuTencentClusterMapCon);
            if (!ObjectUtils.isEmpty(busiMcuTencentClusterMapList))
            {
                throw new SystemException(1000016, "该MCU正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            TencentBridgeCache.getInstance().removeTencent(TencentBridge);
            busiMcuTencentMapper.deleteBusiMcuTencentById(bridgeHostId);
        }

        logger.info("删除MCU命令下发成功，bridgeHostId: {}", bridgeHostId);
    }

    @Override
    public BusiMcuTencentCluster getMcuTencentClusterById(Long cid)
    {
        return TencentClusterCache.getInstance().get(cid);
    }

    /**
     * <pre>新增MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:10  void
     */
    @Override
    public synchronized void addMcuTencent(BusiMcuTencent busiMcuTencent)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(busiMcuTencent.getAppId())
                || ObjectUtils.isEmpty(busiMcuTencent.getName()) || ObjectUtils.isEmpty(busiMcuTencent.getSdkId()) || ObjectUtils.isEmpty(busiMcuTencent.getSecretId())||
        ObjectUtils.isEmpty(busiMcuTencent.getSecretKey()) )
        {
            throw new SystemException(1000000, "新增的MCU“名称”, “APPID”, “应用ID,密钥,密匙”不能为空！");
        }

        // 获取MCU组对象
        if (TencentBridgeCache.getInstance().getTencentBridgeMap().get(busiMcuTencent.getAppId()) != null)
        {
            throw new SystemException(1000000, "新增的MCU“" + busiMcuTencent.getAppId() + "”已存在，请勿重复添加！");
        }

        BusiMcuTencent query = new BusiMcuTencent();
        query.setAppId(busiMcuTencent.getAppId());
        List<BusiMcuTencent> bs = busiMcuTencentMapper.selectBusiMcuTencentList(query);
        BusiMcuTencent old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            busiMcuTencent.setId(old.getId());
            busiMcuTencent.setUpdateTime(new Date());
        }
        else
        {
            busiMcuTencent.setCreateTime(new Date());
        }

        busiMcuTencent.setCapacity(busiMcuTencent.getCapacity() == null ? 80 : busiMcuTencent.getCapacity());
        busiMcuTencentMapper.insertBusiMcuTencent(busiMcuTencent);
        TencentBridge mcuTencentBridge = new TencentBridge(busiMcuTencent);
        TencentBridgeCache.getInstance().update(mcuTencentBridge);
        logger.info("添加MCU并初始化成功：" + busiMcuTencent);
    }

    /**
     * <pre>修改MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:10  void
     */
    @Override
    public void updateMcuTencent(BusiMcuTencent busiMcuTencent)
    {
        TencentBridge mcuTencentBridge = TencentBridgeCache.getInstance().getTencentBridgeMap().get(busiMcuTencent.getId());
        if (mcuTencentBridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiMcuTencent busiMcuTencentExist  = mcuTencentBridge.getBusiTencent();
            if ((busiMcuTencent.getAppId() != null && !busiMcuTencentExist.getAppId().equals(busiMcuTencent.getAppId()))
                    || (busiMcuTencent.getStatus() != null && !busiMcuTencentExist.getStatus().equals(busiMcuTencent.getStatus())))
            {
                throw new SystemException(1000000, "MCU的ip不支持修改，若是要更改，请删除该MCU，重新创建");
            }



            // 设置基本属性
            busiMcuTencentExist.setName(busiMcuTencent.getName() == null ? busiMcuTencentExist.getName() : busiMcuTencent.getName());
            busiMcuTencentExist.setSpareSmcId(busiMcuTencent.getSpareSmcId());
            busiMcuTencentExist.setUpdateTime(new Date());
            busiMcuTencentExist.setAppId(busiMcuTencent.getAppId());
            busiMcuTencentExist.setSdkId(busiMcuTencent.getSdkId());
            busiMcuTencentExist.setSecretId(busiMcuTencent.getSecretId());
            busiMcuTencentExist.setSecretKey(busiMcuTencent.getSecretKey());

            busiMcuTencentMapper.updateBusiMcuTencent(busiMcuTencentExist);
        }
    }

    /**
     * <pre>新增MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:09  void
     */
    @Override
    public void addBusiMcuTencentCluster(BusiMcuTencentCluster busiMcuTencentCluster)
    {
        busiMcuTencentCluster.setCreateTime(new Date());
        busiMcuTencentClusterMapper.insertBusiMcuTencentCluster(busiMcuTencentCluster);

        // 添加缓存
        TencentClusterCache.getInstance().put(busiMcuTencentCluster.getId(), busiMcuTencentCluster);
        logger.info("添加MCU组成功: {}", busiMcuTencentCluster);
    }

    /**
     * <pre>删除MCU集群</pre>
     * @author lilinhai
     * @since 2020-12-20 11:09  void
     */
    @Override
    public void deleteBusiMcuTencentCluster(Long id)
    {
        BusiMcuTencentClusterMap busiMcuTencentClusterMapCon = new BusiMcuTencentClusterMap();
        busiMcuTencentClusterMapCon.setClusterId(id);
        List<BusiMcuTencentClusterMap> busiMcuTencentClusterMapList = busiMcuTencentClusterMapMapper.selectBusiMcuTencentClusterMapList(busiMcuTencentClusterMapCon);
        if (!ObjectUtils.isEmpty(busiMcuTencentClusterMapList))
        {
            throw new SystemException(1000012, "MCU集群的删除，请先删除该集群下所有MCU，再删除该MCU集群，最后再重新创建！");
        }

        BusiMcuTencentCluster con = new BusiMcuTencentCluster();
        con.setSpareMcuType(FmeType.CLUSTER.getValue());
        con.setSpareMcuId(id);
        List<BusiMcuTencentCluster> gs = busiMcuTencentClusterMapper.selectBusiMcuTencentClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }

        BusiMcuTencentDept con1 = new BusiMcuTencentDept();
        con1.setMcuId(id);
        con1.setMcuType(FmeType.CLUSTER.getValue());
        List<BusiMcuTencentDept> fds = busiMcuTencentDeptMapper.selectBusiMcuTencentDeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }

        int c = busiMcuTencentClusterMapper.deleteBusiMcuTencentClusterById(id);
        if (c > 0)
        {
            TencentClusterCache.getInstance().remove(id);
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
    public List<ModelBean> getAllBusiMcuTencentCluster()
    {
        List<BusiMcuTencentCluster> gs = new ArrayList<>(TencentClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMcuTencentCluster busiMcuTencentCluster : gs)
        {
            ModelBean m = new ModelBean(busiMcuTencentCluster);
            m.put("bindDeptCount", DeptTencentMappingCache.getInstance().getBindDeptCount(FmeType.CLUSTER, busiMcuTencentCluster.getId()));
            ms.add(m);
        }
        return ms;
    }

    /**
     * <pre>修改MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:09  void
     */
    @Override
    public void updateBusiMcuTencentCluster(BusiMcuTencentCluster busiMcuTencentCluster)
    {
        busiMcuTencentCluster.setUpdateTime(new Date());
        busiMcuTencentClusterMapper.updateBusiMcuTencentCluster(busiMcuTencentCluster);
        TencentClusterCache.getInstance().put(busiMcuTencentCluster.getId(), busiMcuTencentClusterMapper.selectBusiMcuTencentClusterById(busiMcuTencentCluster.getId()));
        logger.info("修改MCU集群信息成功: {}", busiMcuTencentCluster);
    }

}
