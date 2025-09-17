/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuHwcloudCacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version  V1.0
 */
package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloud;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudDept;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IMcuHwcloudCacheService;
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
public class McuHwcloudCacheServiceImpl implements IMcuHwcloudCacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiMcuHwcloudMapper busiMcuHwcloudMapper;

    @Resource
    private BusiMcuHwcloudClusterMapper busiMcuHwcloudClusterMapper;

    @Resource
    private BusiMcuHwcloudDeptMapper busiMcuHwcloudDeptMapper;

    @Resource
    private BusiMcuHwcloudClusterMapMapper busiMcuHwcloudClusterMapMapper;

    /**
     * <pre>删除一个MCU</pre>
     * @author lilinhai
     * @since 2020-12-20 11:12  void
     */
    @Override
    public void deleteMcuHwcloud(Long bridgeHostId)
    {
        HwcloudBridge HwcloudBridge = HwcloudBridgeCache.getInstance().getHwcloudBridgeMap().get(bridgeHostId);
        if (HwcloudBridge != null)
        {
            for (HwcloudConferenceContext conferenceContext : HwcloudConferenceContextCache.getInstance().values()) {
                if (Objects.equals(conferenceContext.getHwcloudBridge().getBusiHwcloud().getId(),HwcloudBridge.getBusiHwcloud().getId())) {
                    throw new SystemException(1002425, "当前MCU有会议正在进行中，无法删除！");
                }
            }

            BusiMcuHwcloud busiMcuHwcloudCon = new BusiMcuHwcloud();
            busiMcuHwcloudCon.setSpareSmcId(bridgeHostId);
            List<BusiMcuHwcloud> gs = busiMcuHwcloudMapper.selectBusiMcuHwcloudList(busiMcuHwcloudCon);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该MCU作为备用MCU已被其它MCU关联，不能删除！");
            }

            BusiMcuHwcloudDept busiMcuHwcloudDeptCon = new BusiMcuHwcloudDept();
            busiMcuHwcloudDeptCon.setMcuId(bridgeHostId);
            busiMcuHwcloudDeptCon.setMcuType(FmeType.SINGLE_NODE.getValue());
            List<BusiMcuHwcloudDept> busiMcuHwcloudDeptList = busiMcuHwcloudDeptMapper.selectBusiMcuHwcloudDeptList(busiMcuHwcloudDeptCon);
            if (!ObjectUtils.isEmpty(busiMcuHwcloudDeptList))
            {
                throw new SystemException(1000016, "该MCU正在被租户使用，不能删除！");
            }

            BusiMcuHwcloudClusterMap busiMcuHwcloudClusterMapCon = new BusiMcuHwcloudClusterMap();
            busiMcuHwcloudClusterMapCon.setMcuId(bridgeHostId);
            List<BusiMcuHwcloudClusterMap> busiMcuHwcloudClusterMapList = busiMcuHwcloudClusterMapMapper.selectBusiMcuHwcloudClusterMapList(busiMcuHwcloudClusterMapCon);
            if (!ObjectUtils.isEmpty(busiMcuHwcloudClusterMapList))
            {
                throw new SystemException(1000016, "该MCU正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            HwcloudBridgeCache.getInstance().removeHwcloud(HwcloudBridge);
            busiMcuHwcloudMapper.deleteBusiMcuHwcloudById(bridgeHostId);
        }

        logger.info("删除MCU命令下发成功，bridgeHostId: {}", bridgeHostId);
    }

    @Override
    public BusiMcuHwcloudCluster getMcuHwcloudClusterById(Long cid)
    {
        return HwcloudClusterCache.getInstance().get(cid);
    }

    /**
     * <pre>新增MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:10  void
     */
    @Override
    public synchronized void addMcuHwcloud(BusiMcuHwcloud busiMcuHwcloud)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(busiMcuHwcloud.getAppId())
                || ObjectUtils.isEmpty(busiMcuHwcloud.getName()) || ObjectUtils.isEmpty(busiMcuHwcloud.getAppId()) || ObjectUtils.isEmpty(busiMcuHwcloud.getAppKey()))
        {
            throw new SystemException(1000000, "新增的MCU“名称”, “APPID”, “应用ID,密钥,密匙”不能为空！");
        }

        // 获取MCU组对象
        if (HwcloudBridgeCache.getInstance().getHwcloudBridgeMap().get(busiMcuHwcloud.getAppId()) != null)
        {
            throw new SystemException(1000000, "新增的MCU“" + busiMcuHwcloud.getAppId() + "”已存在，请勿重复添加！");
        }

        BusiMcuHwcloud query = new BusiMcuHwcloud();
        query.setAppId(busiMcuHwcloud.getAppId());
        List<BusiMcuHwcloud> bs = busiMcuHwcloudMapper.selectBusiMcuHwcloudList(query);
        BusiMcuHwcloud old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            busiMcuHwcloud.setId(old.getId());
            busiMcuHwcloud.setUpdateTime(new Date());
        }
        else
        {
            busiMcuHwcloud.setCreateTime(new Date());
        }

        busiMcuHwcloud.setCapacity(busiMcuHwcloud.getCapacity() == null ? 80 : busiMcuHwcloud.getCapacity());
        busiMcuHwcloudMapper.insertBusiMcuHwcloud(busiMcuHwcloud);
        HwcloudBridge mcuHwcloudBridge = new HwcloudBridge(busiMcuHwcloud);
        HwcloudBridgeCache.getInstance().update(mcuHwcloudBridge);
        logger.info("添加MCU并初始化成功：" + busiMcuHwcloud);
    }

    /**
     * <pre>修改MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:10  void
     */
    @Override
    public void updateMcuHwcloud(BusiMcuHwcloud busiMcuHwcloud)
    {
        HwcloudBridge mcuHwcloudBridge = HwcloudBridgeCache.getInstance().getHwcloudBridgeMap().get(busiMcuHwcloud.getId());
        if (mcuHwcloudBridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiMcuHwcloud busiMcuHwcloudExist  = mcuHwcloudBridge.getBusiHwcloud();
            if ((busiMcuHwcloud.getAppId() != null && !busiMcuHwcloudExist.getAppId().equals(busiMcuHwcloud.getAppId()))
                    || (busiMcuHwcloud.getStatus() != null && !busiMcuHwcloudExist.getStatus().equals(busiMcuHwcloud.getStatus())))
            {
                throw new SystemException(1000000, "MCU的ip不支持修改，若是要更改，请删除该MCU，重新创建");
            }



            // 设置基本属性
            busiMcuHwcloudExist.setName(busiMcuHwcloud.getName() == null ? busiMcuHwcloudExist.getName() : busiMcuHwcloud.getName());
            busiMcuHwcloudExist.setSpareSmcId(busiMcuHwcloud.getSpareSmcId());
            busiMcuHwcloudExist.setUpdateTime(new Date());
            busiMcuHwcloudExist.setAppId(busiMcuHwcloud.getAppId());
            busiMcuHwcloudExist.setAppKey(busiMcuHwcloud.getAppKey());

            busiMcuHwcloudMapper.updateBusiMcuHwcloud(busiMcuHwcloudExist);
        }
    }

    /**
     * <pre>新增MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:09  void
     */
    @Override
    public void addBusiMcuHwcloudCluster(BusiMcuHwcloudCluster busiMcuHwcloudCluster)
    {
        busiMcuHwcloudCluster.setCreateTime(new Date());
        busiMcuHwcloudClusterMapper.insertBusiMcuHwcloudCluster(busiMcuHwcloudCluster);

        // 添加缓存
        HwcloudClusterCache.getInstance().put(busiMcuHwcloudCluster.getId(), busiMcuHwcloudCluster);
        logger.info("添加MCU组成功: {}", busiMcuHwcloudCluster);
    }

    /**
     * <pre>删除MCU集群</pre>
     * @author lilinhai
     * @since 2020-12-20 11:09  void
     */
    @Override
    public void deleteBusiMcuHwcloudCluster(Long id)
    {
        BusiMcuHwcloudClusterMap busiMcuHwcloudClusterMapCon = new BusiMcuHwcloudClusterMap();
        busiMcuHwcloudClusterMapCon.setClusterId(id);
        List<BusiMcuHwcloudClusterMap> busiMcuHwcloudClusterMapList = busiMcuHwcloudClusterMapMapper.selectBusiMcuHwcloudClusterMapList(busiMcuHwcloudClusterMapCon);
        if (!ObjectUtils.isEmpty(busiMcuHwcloudClusterMapList))
        {
            throw new SystemException(1000012, "MCU集群的删除，请先删除该集群下所有MCU，再删除该MCU集群，最后再重新创建！");
        }

        BusiMcuHwcloudCluster con = new BusiMcuHwcloudCluster();
        con.setSpareMcuType(FmeType.CLUSTER.getValue());
        con.setSpareMcuId(id);
        List<BusiMcuHwcloudCluster> gs = busiMcuHwcloudClusterMapper.selectBusiMcuHwcloudClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }

        BusiMcuHwcloudDept con1 = new BusiMcuHwcloudDept();
        con1.setMcuId(id);
        con1.setMcuType(FmeType.CLUSTER.getValue());
        List<BusiMcuHwcloudDept> fds = busiMcuHwcloudDeptMapper.selectBusiMcuHwcloudDeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }

        int c = busiMcuHwcloudClusterMapper.deleteBusiMcuHwcloudClusterById(id);
        if (c > 0)
        {
            HwcloudClusterCache.getInstance().remove(id);
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
    public List<ModelBean> getAllBusiMcuHwcloudCluster()
    {
        List<BusiMcuHwcloudCluster> gs = new ArrayList<>(HwcloudClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMcuHwcloudCluster busiMcuHwcloudCluster : gs)
        {
            ModelBean m = new ModelBean(busiMcuHwcloudCluster);
            m.put("bindDeptCount", DeptHwcloudMappingCache.getInstance().getBindDeptCount(FmeType.CLUSTER, busiMcuHwcloudCluster.getId()));
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
    public void updateBusiMcuHwcloudCluster(BusiMcuHwcloudCluster busiMcuHwcloudCluster)
    {
        busiMcuHwcloudCluster.setUpdateTime(new Date());
        busiMcuHwcloudClusterMapper.updateBusiMcuHwcloudCluster(busiMcuHwcloudCluster);
        HwcloudClusterCache.getInstance().put(busiMcuHwcloudCluster.getId(), busiMcuHwcloudClusterMapper.selectBusiMcuHwcloudClusterById(busiMcuHwcloudCluster.getId()));
        logger.info("修改MCU集群信息成功: {}", busiMcuHwcloudCluster);
    }

}
