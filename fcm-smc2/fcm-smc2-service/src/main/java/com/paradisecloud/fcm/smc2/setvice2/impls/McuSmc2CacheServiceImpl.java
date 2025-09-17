/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuSmc2CacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.setvice2.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.smc2.cache.*;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2ClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2ClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2DeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2Mapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Cluster;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2ClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Dept;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IMcuSmc2CacheService;
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
public class McuSmc2CacheServiceImpl implements IMcuSmc2CacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiMcuSmc2Mapper busiMcuSmc2Mapper;
    
    @Resource
    private BusiMcuSmc2ClusterMapper busiMcuSmc2ClusterMapper;
    
    @Resource
    private BusiMcuSmc2DeptMapper busiMcuSmc2DeptMapper;
    
    @Resource
    private BusiMcuSmc2ClusterMapMapper busiMcuSmc2ClusterMapMapper;

    /**
     * <pre>删除一个MCU</pre>
     * @author lilinhai
     * @since 2020-12-20 11:12  void
     */
    @Override
    public void deleteMcuSmc2(Long bridgeHostId)
    {
        Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().getSmc2BridgeMap().get(bridgeHostId);
        if (smc2Bridge != null)
        {
            for (Smc2ConferenceContext conferenceContext : Smc2ConferenceContextCache.getInstance().values()) {
                if (Objects.equals(conferenceContext.getSmc2Bridge().getBusiSmc2().getId(),smc2Bridge.getBusiSmc2().getId())) {
                    throw new SystemException(1002425, "当前MCU有会议正在进行中，无法删除！");
                }
            }

            BusiMcuSmc2 busiMcuSmc2Con = new BusiMcuSmc2();
            busiMcuSmc2Con.setSpareMcuId(bridgeHostId);
            List<BusiMcuSmc2> gs = busiMcuSmc2Mapper.selectBusiMcuSmc2List(busiMcuSmc2Con);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该MCU作为备用MCU已被其它MCU关联，不能删除！");
            }

            BusiMcuSmc2Dept busiMcuSmc2DeptCon = new BusiMcuSmc2Dept();
            busiMcuSmc2DeptCon.setMcuId(bridgeHostId);
            busiMcuSmc2DeptCon.setMcuType(FmeType.SINGLE_NODE.getValue());
            List<BusiMcuSmc2Dept> busiMcuSmc2DeptList = busiMcuSmc2DeptMapper.selectBusiMcuSmc2DeptList(busiMcuSmc2DeptCon);
            if (!ObjectUtils.isEmpty(busiMcuSmc2DeptList))
            {
                throw new SystemException(1000016, "该MCU正在被租户使用，不能删除！");
            }

            BusiMcuSmc2ClusterMap busiMcuSmc2ClusterMapCon = new BusiMcuSmc2ClusterMap();
            busiMcuSmc2ClusterMapCon.setMcuId(bridgeHostId);
            List<BusiMcuSmc2ClusterMap> busiMcuSmc2ClusterMapList = busiMcuSmc2ClusterMapMapper.selectBusiMcuSmc2ClusterMapList(busiMcuSmc2ClusterMapCon);
            if (!ObjectUtils.isEmpty(busiMcuSmc2ClusterMapList))
            {
                throw new SystemException(1000016, "该MCU正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            Smc2BridgeCache.getInstance().removeSmc2(smc2Bridge);
            busiMcuSmc2Mapper.deleteBusiMcuSmc2ById(bridgeHostId);
        }

        logger.info("删除MCU命令下发成功，bridgeHostId: {}", bridgeHostId);
    }

    @Override
    public BusiMcuSmc2Cluster getMcuSmc2ClusterById(Long cid)
    {
        return Smc2ClusterCache.getInstance().get(cid);
    }

    /**
     * <pre>新增MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:10  void
     */
    @Override
    public synchronized void addMcuSmc2(BusiMcuSmc2 busiMcuSmc2)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(busiMcuSmc2.getIp())
                || ObjectUtils.isEmpty(busiMcuSmc2.getName())  || ObjectUtils.isEmpty(busiMcuSmc2.getScUrl())||
        ObjectUtils.isEmpty(busiMcuSmc2.getUsername()) || ObjectUtils.isEmpty(busiMcuSmc2.getPassword()))
        {
            throw new SystemException(1000000, "新增的MCU“名称”, “ip”, “账户”和“密码”不能为空！");
        }


        if (ObjectUtils.isEmpty(busiMcuSmc2.getPort()))
        {
            busiMcuSmc2.setPort(443);
        }

        if (ObjectUtils.isEmpty(busiMcuSmc2.getMcuDomain())) {
            busiMcuSmc2.setMcuDomain("ttCloud.com");
        }

        if (ObjectUtils.isEmpty(busiMcuSmc2.getCallPort())) {
            busiMcuSmc2.setCallPort(5060);
        }

        if (busiMcuSmc2.getPort() <= 0 || busiMcuSmc2.getPort() > 65525)
        {
            throw new SystemException(1000102, "端口号不正确");
        }

        if (busiMcuSmc2.getCallPort() != null) {
            if (busiMcuSmc2.getCallPort() <= 0 || busiMcuSmc2.getCallPort() > 65525) {
                throw new SystemException(1000102, "呼叫端口号不正确");
            }
        }

        if (busiMcuSmc2.getProxyPort() != null) {
            if (busiMcuSmc2.getProxyPort() <= 0 || busiMcuSmc2.getProxyPort() > 65525) {
                throw new SystemException(1000102, "代理端口号不正确");
            }
        }

        // 获取MCU组对象
        if (Smc2BridgeCache.getInstance().getIpToSmc2BridgeMap().get(busiMcuSmc2.getIp()) != null)
        {
            throw new SystemException(1000000, "新增的MCU“" + busiMcuSmc2.getIp() + "”已存在，请勿重复添加！");
        }

        BusiMcuSmc2 query = new BusiMcuSmc2();
        query.setIp(busiMcuSmc2.getIp());
        query.setPort(busiMcuSmc2.getPort());
        List<BusiMcuSmc2> bs = busiMcuSmc2Mapper.selectBusiMcuSmc2List(query);
        BusiMcuSmc2 old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            busiMcuSmc2.setId(old.getId());
            busiMcuSmc2.setUpdateTime(new Date());
        }
        else
        {
            busiMcuSmc2.setCreateTime(new Date());
        }

        busiMcuSmc2.setCapacity(busiMcuSmc2.getCapacity() == null ? 80 : busiMcuSmc2.getCapacity());
        busiMcuSmc2Mapper.insertBusiMcuSmc2(busiMcuSmc2);
        Smc2Bridge mcuSmc2Bridge = new Smc2Bridge(busiMcuSmc2);
        Smc2BridgeCache.getInstance().update(mcuSmc2Bridge);
        logger.info("添加MCU并初始化成功：" + busiMcuSmc2);
    }

    /**
     * <pre>修改MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:10  void
     */
    @Override
    public void updateMcuSmc2(BusiMcuSmc2 busiMcuSmc2)
    {
        Smc2Bridge mcuSmc2Bridge = Smc2BridgeCache.getInstance().getIpToSmc2BridgeMap().get(busiMcuSmc2.getId());
        if (mcuSmc2Bridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiMcuSmc2 busiMcuSmc2Exist  = mcuSmc2Bridge.getBusiSmc2();
            if ((busiMcuSmc2.getIp() != null && !busiMcuSmc2Exist.getIp().equals(busiMcuSmc2.getIp()))
                    || (busiMcuSmc2.getStatus() != null && !busiMcuSmc2Exist.getStatus().equals(busiMcuSmc2.getStatus())))
            {
                throw new SystemException(1000000, "MCU的ip不支持修改，若是要更改，请删除该MCU，重新创建");
            }

            if (busiMcuSmc2.getCallPort() != null) {
                if (busiMcuSmc2.getCallPort() <= 0 || busiMcuSmc2.getCallPort() > 65525) {
                    throw new SystemException(1000102, "呼叫端口号不正确");
                }
            }

            if (busiMcuSmc2.getProxyPort() != null) {
                if (busiMcuSmc2.getProxyPort() <= 0 || busiMcuSmc2.getProxyPort() > 65525) {
                    throw new SystemException(1000102, "代理端口号不正确");
                }
            }

            // 设置基本属性
            busiMcuSmc2Exist.setName(busiMcuSmc2.getName() == null ? busiMcuSmc2Exist.getName() : busiMcuSmc2.getName());
            busiMcuSmc2Exist.setSpareMcuId(busiMcuSmc2.getSpareMcuId());
            busiMcuSmc2Exist.setUpdateTime(new Date());
            busiMcuSmc2Exist.setCucmIp(busiMcuSmc2.getCucmIp());
            busiMcuSmc2Exist.setCapacity(busiMcuSmc2.getCapacity() == null ? 80 : busiMcuSmc2.getCapacity());
            busiMcuSmc2Exist.setProxyHost(busiMcuSmc2.getProxyHost() == null ? busiMcuSmc2Exist.getProxyHost() : busiMcuSmc2.getProxyHost());
            busiMcuSmc2Exist.setProxyPort(busiMcuSmc2.getProxyPort() == null ? busiMcuSmc2Exist.getProxyPort() : busiMcuSmc2.getProxyPort());
            if (ObjectUtils.isEmpty(busiMcuSmc2Exist.getMcuDomain())) {
                busiMcuSmc2Exist.setMcuDomain("ttCloud.com");
            }

            busiMcuSmc2Mapper.updateBusiMcuSmc2(busiMcuSmc2Exist);
        }
    }

    /**
     * <pre>新增MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-20 11:09  void
     */
    @Override
    public void addBusiMcuSmc2Cluster(BusiMcuSmc2Cluster busiMcuSmc2Cluster)
    {
        busiMcuSmc2Cluster.setCreateTime(new Date());
        busiMcuSmc2ClusterMapper.insertBusiMcuSmc2Cluster(busiMcuSmc2Cluster);

        // 添加缓存
        Smc2ClusterCache.getInstance().put(busiMcuSmc2Cluster.getId(), busiMcuSmc2Cluster);
        logger.info("添加MCU组成功: {}", busiMcuSmc2Cluster);
    }

    /**
     * <pre>删除MCU集群</pre>
     * @author lilinhai
     * @since 2020-12-20 11:09  void
     */
    @Override
    public void deleteBusiMcuSmc2Cluster(Long id)
    {
        BusiMcuSmc2ClusterMap busiMcuSmc2ClusterMapCon = new BusiMcuSmc2ClusterMap();
        busiMcuSmc2ClusterMapCon.setClusterId(id);
        List<BusiMcuSmc2ClusterMap> busiMcuSmc2ClusterMapList = busiMcuSmc2ClusterMapMapper.selectBusiMcuSmc2ClusterMapList(busiMcuSmc2ClusterMapCon);
        if (!ObjectUtils.isEmpty(busiMcuSmc2ClusterMapList))
        {
            throw new SystemException(1000012, "MCU集群的删除，请先删除该集群下所有MCU，再删除该MCU集群，最后再重新创建！");
        }

        BusiMcuSmc2Cluster con = new BusiMcuSmc2Cluster();
        con.setSpareMcuType(FmeType.CLUSTER.getValue());
        con.setSpareMcuId(id);
        List<BusiMcuSmc2Cluster> gs = busiMcuSmc2ClusterMapper.selectBusiMcuSmc2ClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }

        BusiMcuSmc2Dept con1 = new BusiMcuSmc2Dept();
        con1.setMcuId(id);
        con1.setMcuType(FmeType.CLUSTER.getValue());
        List<BusiMcuSmc2Dept> fds = busiMcuSmc2DeptMapper.selectBusiMcuSmc2DeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }

        int c = busiMcuSmc2ClusterMapper.deleteBusiMcuSmc2ClusterById(id);
        if (c > 0)
        {
            Smc2ClusterCache.getInstance().remove(id);
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
    public List<ModelBean> getAllBusiMcuSmc2Cluster()
    {
        List<BusiMcuSmc2Cluster> gs = new ArrayList<>(Smc2ClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMcuSmc2Cluster busiMcuSmc2Cluster : gs)
        {
            ModelBean m = new ModelBean(busiMcuSmc2Cluster);
            m.put("bindDeptCount", DeptSmc2MappingCache.getInstance().getBindDeptCount(FmeType.CLUSTER, busiMcuSmc2Cluster.getId()));
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
    public void updateBusiMcuSmc2Cluster(BusiMcuSmc2Cluster busiMcuSmc2Cluster)
    {
        busiMcuSmc2Cluster.setUpdateTime(new Date());
        busiMcuSmc2ClusterMapper.updateBusiMcuSmc2Cluster(busiMcuSmc2Cluster);
        Smc2ClusterCache.getInstance().put(busiMcuSmc2Cluster.getId(), busiMcuSmc2ClusterMapper.selectBusiMcuSmc2ClusterById(busiMcuSmc2Cluster.getId()));
        logger.info("修改MCU集群信息成功: {}", busiMcuSmc2Cluster);
    }
    
}
