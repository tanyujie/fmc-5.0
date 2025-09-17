/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuSmc3CacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.cache.DeptSmc3MappingCache;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.cache.Smc3ClusterCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3ClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3ClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3DeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3Mapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Cluster;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3ClusterMap;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3Dept;
import com.paradisecloud.smc3.service.interfaces.IMcuSmc3CacheService;
import com.paradisecloud.smc3.websocket.client.Smc3WebsocketReconnecter;
import com.sinhy.exception.SystemException;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
public class McuSmc3CacheServiceImpl implements IMcuSmc3CacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiMcuSmc3Mapper busiMcuSmc3Mapper;
    
    @Resource
    private BusiMcuSmc3ClusterMapper busiMcuSmc3ClusterMapper;
    
    @Resource
    private BusiMcuSmc3DeptMapper busiMcuSmc3DeptMapper;
    
    @Resource
    private BusiMcuSmc3ClusterMapMapper busiMcuSmc3ClusterMapMapper;

    /**
     * <pre>删除一个MCU</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    @Override
    public void deleteMcuSmc3(Long bridgeHostId)
    {
        Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getIdToTeleBridgeMap().get(bridgeHostId);
        if (smc3Bridge != null)
        {
            for (Smc3ConferenceContext conferenceContext : Smc3ConferenceContextCache.getInstance().values()) {
                if (Objects.equals(conferenceContext.getSmc3Bridge().getBusiSMC().getId(),smc3Bridge.getBusiSMC().getId())) {
                    throw new SystemException(1002435, "当前MCU有会议正在进行中，无法删除！");
                }
            }

            BusiMcuSmc3 busiMcuSmc3Con = new BusiMcuSmc3();
            busiMcuSmc3Con.setSpareMcuId(bridgeHostId);
            List<BusiMcuSmc3> gs = busiMcuSmc3Mapper.selectBusiMcuSmc3List(busiMcuSmc3Con);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该MCU作为备用MCU已被其它MCU关联，不能删除！");
            }

            BusiMcuSmc3Dept busiMcuSmc3DeptCon = new BusiMcuSmc3Dept();
            busiMcuSmc3DeptCon.setMcuId(bridgeHostId);
            busiMcuSmc3DeptCon.setMcuType(FmeType.SINGLE_NODE.getValue());
            List<BusiMcuSmc3Dept> busiMcuSmc3DeptList = busiMcuSmc3DeptMapper.selectBusiMcuSmc3DeptList(busiMcuSmc3DeptCon);
            if (!ObjectUtils.isEmpty(busiMcuSmc3DeptList))
            {
                throw new SystemException(1000016, "该MCU正在被租户使用，不能删除！");
            }

            BusiMcuSmc3ClusterMap busiMcuSmc3ClusterMapCon = new BusiMcuSmc3ClusterMap();
            busiMcuSmc3ClusterMapCon.setMcuId(bridgeHostId);
            List<BusiMcuSmc3ClusterMap> busiMcuSmc3ClusterMapList = busiMcuSmc3ClusterMapMapper.selectBusiMcuSmc3ClusterMapList(busiMcuSmc3ClusterMapCon);
            if (!ObjectUtils.isEmpty(busiMcuSmc3ClusterMapList))
            {
                throw new SystemException(1000016, "该MCU正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            Smc3BridgeCache.getInstance().remove(smc3Bridge);
            busiMcuSmc3Mapper.deleteBusiMcuSmc3ById(bridgeHostId);
        }

        logger.info("删除MCU命令下发成功，bridgeHostId: {}", bridgeHostId);
    }

    @Override
    public BusiMcuSmc3Cluster getMcuSmc3ClusterById(Long cid)
    {
        return Smc3ClusterCache.getInstance().get(cid);
    }

    /**
     * <pre>新增MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    @Override
    public synchronized void addMcuSmc3(BusiMcuSmc3 busiMcuSmc3)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(busiMcuSmc3.getIp())
                || ObjectUtils.isEmpty(busiMcuSmc3.getName()) || ObjectUtils.isEmpty(busiMcuSmc3.getIp()) || ObjectUtils.isEmpty(busiMcuSmc3.getScUrl())||
        ObjectUtils.isEmpty(busiMcuSmc3.getMeetingUsername()) || ObjectUtils.isEmpty(busiMcuSmc3.getMeetingPassword())||
        ObjectUtils.isEmpty(busiMcuSmc3.getUsername()) || ObjectUtils.isEmpty(busiMcuSmc3.getPassword()))
        {
            throw new SystemException(1000000, "新增的MCU“名称”, “ip”, “账户”和“密码”不能为空！");
        }


        if (ObjectUtils.isEmpty(busiMcuSmc3.getPort()))
        {
            busiMcuSmc3.setPort(443);
        }

        if (ObjectUtils.isEmpty(busiMcuSmc3.getMcuDomain())) {
            busiMcuSmc3.setMcuDomain("ttCloud.com");
        }

        if (ObjectUtils.isEmpty(busiMcuSmc3.getCallPort())) {
            busiMcuSmc3.setCallPort(5060);
        }

        if (busiMcuSmc3.getPort() <= 0 || busiMcuSmc3.getPort() > 65535)
        {
            throw new SystemException(1000103, "端口号不正确");
        }

        if (busiMcuSmc3.getCallPort() != null) {
            if (busiMcuSmc3.getCallPort() <= 0 || busiMcuSmc3.getCallPort() > 65535) {
                throw new SystemException(1000103, "呼叫端口号不正确");
            }
        }

        if (busiMcuSmc3.getProxyPort() != null) {
            if (busiMcuSmc3.getProxyPort() <= 0 || busiMcuSmc3.getProxyPort() > 65535) {
                throw new SystemException(1000103, "代理端口号不正确");
            }
        }

        // 获取MCU组对象
        if (Smc3BridgeCache.getInstance().getIpToTeleBridgeMap().get(busiMcuSmc3.getIp()) != null)
        {
            throw new SystemException(1000000, "新增的MCU“" + busiMcuSmc3.getIp() + "”已存在，请勿重复添加！");
        }

        BusiMcuSmc3 query = new BusiMcuSmc3();
        query.setIp(busiMcuSmc3.getIp());
        query.setPort(busiMcuSmc3.getPort());
        List<BusiMcuSmc3> bs = busiMcuSmc3Mapper.selectBusiMcuSmc3List(query);
        BusiMcuSmc3 old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            busiMcuSmc3.setId(old.getId());
            busiMcuSmc3.setUpdateTime(new Date());
        }
        else
        {
            busiMcuSmc3.setCreateTime(new Date());
        }


        busiMcuSmc3Mapper.insertBusiMcuSmc3(busiMcuSmc3);
        Smc3Bridge mcuSmc3Bridge = new Smc3Bridge(busiMcuSmc3);
        Smc3BridgeCache.getInstance().update(mcuSmc3Bridge);
        busiMcuSmc3.setCapacity(busiMcuSmc3.getCapacity() == null ? 80 : busiMcuSmc3.getCapacity());
        Smc3WebsocketReconnecter.getInstance().add(mcuSmc3Bridge);
        logger.info("添加MCU并初始化成功：" + busiMcuSmc3);
    }

    /**
     * <pre>修改MCU，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    @Override
    public void updateMcuSmc3(BusiMcuSmc3 busiMcuSmc3)
    {
        Smc3Bridge mcuSmc3Bridge = Smc3BridgeCache.getInstance().getIdToTeleBridgeMap().get(busiMcuSmc3.getId());
        if (mcuSmc3Bridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiMcuSmc3 busiMcuSmc3Exist  = mcuSmc3Bridge.getBusiSMC();
            if ((busiMcuSmc3.getIp() != null && !busiMcuSmc3Exist.getIp().equals(busiMcuSmc3.getIp()))
                    || (busiMcuSmc3.getStatus() != null && !busiMcuSmc3Exist.getStatus().equals(busiMcuSmc3.getStatus())))
            {
                throw new SystemException(1000000, "MCU的ip不支持修改，若是要更改，请删除该MCU，重新创建");
            }


            if(Strings.isNotBlank(busiMcuSmc3.getUsername())&&Strings.isNotBlank(busiMcuSmc3.getPassword())){
                busiMcuSmc3Exist.setUsername(busiMcuSmc3.getUsername());
                busiMcuSmc3Exist.setPassword(busiMcuSmc3.getPassword());

                Collection<Smc3ConferenceContext> values = Smc3ConferenceContextCache.getInstance().values();
                if(!CollectionUtils.isEmpty(values)){
                    for (Smc3ConferenceContext value : values) {
                        if(value.isStart()){
                            Long id1 = value.getSmc3Bridge().getBusiSMC().getId();
                            Long id2 = mcuSmc3Bridge.getBusiSMC().getId();
                            if(Objects.equals(id1,id2)){
                                throw new SystemException(1000016, "MCU节点的下有正在召开的会议不能修改！");
                            }
                        }
                    }
                }
            }

            if(Strings.isNotBlank(busiMcuSmc3.getMeetingUsername())&&Strings.isNotBlank(busiMcuSmc3.getMeetingPassword())){
                busiMcuSmc3Exist.setMeetingUsername(busiMcuSmc3.getMeetingUsername());
                busiMcuSmc3Exist.setMeetingPassword(busiMcuSmc3.getMeetingPassword());

                Collection<Smc3ConferenceContext> values = Smc3ConferenceContextCache.getInstance().values();
                if(!CollectionUtils.isEmpty(values)){
                    for (Smc3ConferenceContext value : values) {
                        if(value.isStart()){
                            Long id1 = value.getSmc3Bridge().getBusiSMC().getId();
                            Long id2 = mcuSmc3Bridge.getBusiSMC().getId();
                            if(Objects.equals(id1,id2)){
                                throw new SystemException(1000016, "MCU节点的下有正在召开的会议不能修改！");
                            }
                        }
                    }
                }
            }



            // 设置基本属性
            busiMcuSmc3Exist.setName(busiMcuSmc3.getName() == null ? busiMcuSmc3Exist.getName() : busiMcuSmc3.getName());
            busiMcuSmc3Exist.setSpareMcuId(busiMcuSmc3.getSpareMcuId());
            busiMcuSmc3Exist.setUpdateTime(new Date());
            busiMcuSmc3Exist.setCucmIp(busiMcuSmc3.getCucmIp());
            busiMcuSmc3Exist.setCapacity(busiMcuSmc3.getCapacity() == null ? 80 : busiMcuSmc3.getCapacity());
            busiMcuSmc3Exist.setProxyHost(busiMcuSmc3.getProxyHost() == null ? busiMcuSmc3Exist.getProxyHost() : busiMcuSmc3.getProxyHost());
            busiMcuSmc3Exist.setProxyPort(busiMcuSmc3.getProxyPort() == null ? busiMcuSmc3Exist.getProxyPort() : busiMcuSmc3.getProxyPort());
            if (ObjectUtils.isEmpty(busiMcuSmc3Exist.getMcuDomain())) {
                busiMcuSmc3Exist.setMcuDomain("ttCloud.com");
            }

            busiMcuSmc3Mapper.updateBusiMcuSmc3(busiMcuSmc3Exist);
        }
    }

    /**
     * <pre>新增MCU组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    @Override
    public void addBusiMcuSmc3Cluster(BusiMcuSmc3Cluster busiMcuSmc3Cluster)
    {
        busiMcuSmc3Cluster.setCreateTime(new Date());
        busiMcuSmc3ClusterMapper.insertBusiMcuSmc3Cluster(busiMcuSmc3Cluster);

        // 添加缓存
        Smc3ClusterCache.getInstance().put(busiMcuSmc3Cluster.getId(), busiMcuSmc3Cluster);
        logger.info("添加MCU组成功: {}", busiMcuSmc3Cluster);
    }

    /**
     * <pre>删除MCU集群</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    @Override
    public void deleteBusiMcuSmc3Cluster(Long id)
    {
        BusiMcuSmc3ClusterMap busiMcuSmc3ClusterMapCon = new BusiMcuSmc3ClusterMap();
        busiMcuSmc3ClusterMapCon.setClusterId(id);
        List<BusiMcuSmc3ClusterMap> busiMcuSmc3ClusterMapList = busiMcuSmc3ClusterMapMapper.selectBusiMcuSmc3ClusterMapList(busiMcuSmc3ClusterMapCon);
        if (!ObjectUtils.isEmpty(busiMcuSmc3ClusterMapList))
        {
            throw new SystemException(1000013, "MCU集群的删除，请先删除该集群下所有MCU，再删除该MCU集群，最后再重新创建！");
        }

        BusiMcuSmc3Cluster con = new BusiMcuSmc3Cluster();
        con.setSpareMcuType(FmeType.CLUSTER.getValue());
        con.setSpareMcuId(id);
        List<BusiMcuSmc3Cluster> gs = busiMcuSmc3ClusterMapper.selectBusiMcuSmc3ClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }

        BusiMcuSmc3Dept con1 = new BusiMcuSmc3Dept();
        con1.setMcuId(id);
        con1.setMcuType(FmeType.CLUSTER.getValue());
        List<BusiMcuSmc3Dept> fds = busiMcuSmc3DeptMapper.selectBusiMcuSmc3DeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }

        int c = busiMcuSmc3ClusterMapper.deleteBusiMcuSmc3ClusterById(id);
        if (c > 0)
        {
            Smc3ClusterCache.getInstance().remove(id);
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
    public List<ModelBean> getAllBusiMcuSmc3Cluster()
    {
        List<BusiMcuSmc3Cluster> gs = new ArrayList<>(Smc3ClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiMcuSmc3Cluster busiMcuSmc3Cluster : gs)
        {
            ModelBean m = new ModelBean(busiMcuSmc3Cluster);
            m.put("bindDeptCount", DeptSmc3MappingCache.getInstance().getBindDeptCount(FmeType.CLUSTER, busiMcuSmc3Cluster.getId()));
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
    public void updateBusiMcuSmc3Cluster(BusiMcuSmc3Cluster busiMcuSmc3Cluster)
    {
        busiMcuSmc3Cluster.setUpdateTime(new Date());
        busiMcuSmc3ClusterMapper.updateBusiMcuSmc3Cluster(busiMcuSmc3Cluster);
        Smc3ClusterCache.getInstance().put(busiMcuSmc3Cluster.getId(), busiMcuSmc3ClusterMapper.selectBusiMcuSmc3ClusterById(busiMcuSmc3Cluster.getId()));
        logger.info("修改MCU集群信息成功: {}", busiMcuSmc3Cluster);
    }
    
}
