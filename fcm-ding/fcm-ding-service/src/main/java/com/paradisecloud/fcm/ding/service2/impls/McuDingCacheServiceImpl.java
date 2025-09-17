/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuDingCacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version  V1.0
 */
package com.paradisecloud.fcm.ding.service2.impls;

import com.paradisecloud.fcm.ding.service2.interfaces.IMcuDingCacheService;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuDing;
import com.paradisecloud.fcm.dao.model.BusiMcuDingCluster;
import com.paradisecloud.fcm.dao.model.BusiMcuDingClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMcuDingDept;
import com.paradisecloud.fcm.ding.cache.*;
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
public class McuDingCacheServiceImpl implements IMcuDingCacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiMcuDingMapper busiMcuDingMapper;

    @Resource
    private BusiMcuDingClusterMapper busiMcuDingClusterMapper;

    @Resource
    private BusiMcuDingDeptMapper busiMcuDingDeptMapper;

    @Resource
    private BusiMcuDingClusterMapMapper busiMcuDingClusterMapMapper;

    /**
     * <pre>删除一个MCU</pre>
     * @author lilinhai
     * @since 2020-12-20 11:12  void
     */
    @Override
    public void deleteMcuDing(Long bridgeHostId)
    {
        DingBridge DingBridge = DingBridgeCache.getInstance().getDingBridgeMap().get(bridgeHostId);
        if (DingBridge != null)
        {
            for (DingConferenceContext conferenceContext : DingConferenceContextCache.getInstance().values()) {
                if (Objects.equals(conferenceContext.getDingBridge().getBusiDing().getId(),DingBridge.getBusiDing().getId())) {
                    throw new SystemException(1002425, "当前MCU有会议正在进行中，无法删除！");
                }
            }

            BusiMcuDing busiMcuDingCon = new BusiMcuDing();
            busiMcuDingCon.setSpareSmcId(bridgeHostId);
            List<BusiMcuDing> gs = busiMcuDingMapper.selectBusiMcuDingList(busiMcuDingCon);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该MCU作为备用MCU已被其它MCU关联，不能删除！");
            }

            BusiMcuDingDept busiMcuDingDeptCon = new BusiMcuDingDept();
            busiMcuDingDeptCon.setMcuId(bridgeHostId);
            busiMcuDingDeptCon.setMcuType(FmeType.SINGLE_NODE.getValue());
            List<BusiMcuDingDept> busiMcuDingDeptList = busiMcuDingDeptMapper.selectBusiMcuDingDeptList(busiMcuDingDeptCon);
            if (!ObjectUtils.isEmpty(busiMcuDingDeptList))
            {
                throw new SystemException(1000016, "该MCU正在被租户使用，不能删除！");
            }

            BusiMcuDingClusterMap busiMcuDingClusterMapCon = new BusiMcuDingClusterMap();
            busiMcuDingClusterMapCon.setMcuId(bridgeHostId);
            List<BusiMcuDingClusterMap> busiMcuDingClusterMapList = busiMcuDingClusterMapMapper.selectBusiMcuDingClusterMapList(busiMcuDingClusterMapCon);
            if (!ObjectUtils.isEmpty(busiMcuDingClusterMapList))
            {
                throw new SystemException(1000016, "该MCU正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            DingBridgeCache.getInstance().removeDing(DingBridge);
            busiMcuDingMapper.deleteBusiMcuDingById(bridgeHostId);
        }

        logger.info("删除MCU命令下发成功，bridgeHostId: {}", bridgeHostId);
    }

    @Override
    public BusiMcuDingCluster getMcuDingClusterById(Long cid)
    {
        return DingClusterCache.getInstance().get(cid);
    }

    /**
     * <pre>新增MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:10  void
     */
    @Override
    public synchronized void addMcuDing(BusiMcuDing busiMcuDing)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(busiMcuDing.getAppId())
                || ObjectUtils.isEmpty(busiMcuDing.getName()) || ObjectUtils.isEmpty(busiMcuDing.getSdkId()) || ObjectUtils.isEmpty(busiMcuDing.getSecretId())||
        ObjectUtils.isEmpty(busiMcuDing.getSecretKey()) )
        {
            throw new SystemException(1000000, "新增的MCU“名称”, “APPID”, “应用ID,密钥,密匙”不能为空！");
        }

        // 获取MCU组对象
        if (DingBridgeCache.getInstance().getDingBridgeMap().get(busiMcuDing.getAppId()) != null)
        {
            throw new SystemException(1000000, "新增的MCU“" + busiMcuDing.getAppId() + "”已存在，请勿重复添加！");
        }

        BusiMcuDing query = new BusiMcuDing();
        query.setAppId(busiMcuDing.getAppId());
        List<BusiMcuDing> bs = busiMcuDingMapper.selectBusiMcuDingList(query);
        BusiMcuDing old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            busiMcuDing.setId(old.getId());
            busiMcuDing.setUpdateTime(new Date());
        }
        else
        {
            busiMcuDing.setCreateTime(new Date());
        }

        busiMcuDing.setCapacity(busiMcuDing.getCapacity() == null ? 80 : busiMcuDing.getCapacity());
        busiMcuDingMapper.insertBusiMcuDing(busiMcuDing);
        DingBridge mcuDingBridge = new DingBridge(busiMcuDing);
        DingBridgeCache.getInstance().update(mcuDingBridge);
        logger.info("添加MCU并初始化成功：" + busiMcuDing);
    }

    /**
     * <pre>修改MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:10  void
     */
    @Override
    public void updateMcuDing(BusiMcuDing busiMcuDing)
    {
        DingBridge mcuDingBridge = DingBridgeCache.getInstance().getDingBridgeMap().get(busiMcuDing.getId());
        if (mcuDingBridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiMcuDing busiMcuDingExist  = mcuDingBridge.getBusiDing();
            if ((busiMcuDing.getAppId() != null && !busiMcuDingExist.getAppId().equals(busiMcuDing.getAppId()))
                    || (busiMcuDing.getStatus() != null && !busiMcuDingExist.getStatus().equals(busiMcuDing.getStatus())))
            {
                throw new SystemException(1000000, "MCU的ip不支持修改，若是要更改，请删除该MCU，重新创建");
            }



            // 设置基本属性
            busiMcuDingExist.setName(busiMcuDing.getName() == null ? busiMcuDingExist.getName() : busiMcuDing.getName());
            busiMcuDingExist.setSpareSmcId(busiMcuDing.getSpareSmcId());
            busiMcuDingExist.setUpdateTime(new Date());
            busiMcuDingExist.setAppId(busiMcuDing.getAppId());
            busiMcuDingExist.setSdkId(busiMcuDing.getSdkId());
            busiMcuDingExist.setSecretId(busiMcuDing.getSecretId());
            busiMcuDingExist.setSecretKey(busiMcuDing.getSecretKey());

            busiMcuDingMapper.updateBusiMcuDing(busiMcuDingExist);
        }
    }

    /**
     * <pre>新增MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:09  void
     */
    @Override
    public void addBusiMcuDingCluster(BusiMcuDingCluster busiMcuDingCluster)
    {
        busiMcuDingCluster.setCreateTime(new Date());
        busiMcuDingClusterMapper.insertBusiMcuDingCluster(busiMcuDingCluster);

        // 添加缓存
        DingClusterCache.getInstance().put(busiMcuDingCluster.getId(), busiMcuDingCluster);
        logger.info("添加MCU组成功: {}", busiMcuDingCluster);
    }

    /**
     * <pre>删除MCU集群</pre>
     * @author lilinhai
     * @since 2020-12-20 11:09  void
     */
    @Override
    public void deleteBusiMcuDingCluster(Long id)
    {
        BusiMcuDingClusterMap busiMcuDingClusterMapCon = new BusiMcuDingClusterMap();
        busiMcuDingClusterMapCon.setClusterId(id);
        List<BusiMcuDingClusterMap> busiMcuDingClusterMapList = busiMcuDingClusterMapMapper.selectBusiMcuDingClusterMapList(busiMcuDingClusterMapCon);
        if (!ObjectUtils.isEmpty(busiMcuDingClusterMapList))
        {
            throw new SystemException(1000012, "MCU集群的删除，请先删除该集群下所有MCU，再删除该MCU集群，最后再重新创建！");
        }

        BusiMcuDingCluster con = new BusiMcuDingCluster();
        con.setSpareMcuType(FmeType.CLUSTER.getValue());
        con.setSpareMcuId(id);
        List<BusiMcuDingCluster> gs = busiMcuDingClusterMapper.selectBusiMcuDingClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }

        BusiMcuDingDept con1 = new BusiMcuDingDept();
        con1.setMcuId(id);
        con1.setMcuType(FmeType.CLUSTER.getValue());
        List<BusiMcuDingDept> fds = busiMcuDingDeptMapper.selectBusiMcuDingDeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }

        int c = busiMcuDingClusterMapper.deleteBusiMcuDingClusterById(id);
        if (c > 0)
        {
            DingClusterCache.getInstance().remove(id);
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
    public List<ModelBean> getAllBusiMcuDingCluster()
    {
        List<BusiMcuDingCluster> gs = new ArrayList<>(DingClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMcuDingCluster busiMcuDingCluster : gs)
        {
            ModelBean m = new ModelBean(busiMcuDingCluster);
            m.put("bindDeptCount", DeptDingMappingCache.getInstance().getBindDeptCount(FmeType.CLUSTER, busiMcuDingCluster.getId()));
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
    public void updateBusiMcuDingCluster(BusiMcuDingCluster busiMcuDingCluster)
    {
        busiMcuDingCluster.setUpdateTime(new Date());
        busiMcuDingClusterMapper.updateBusiMcuDingCluster(busiMcuDingCluster);
        DingClusterCache.getInstance().put(busiMcuDingCluster.getId(), busiMcuDingClusterMapper.selectBusiMcuDingClusterById(busiMcuDingCluster.getId()));
        logger.info("修改MCU集群信息成功: {}", busiMcuDingCluster);
    }

}
