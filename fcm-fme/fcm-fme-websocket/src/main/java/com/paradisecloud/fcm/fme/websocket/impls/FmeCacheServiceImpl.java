/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeCacheServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai 
 * @since 2020-12-25 11:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.impls;

import java.util.*;

import com.paradisecloud.fcm.terminal.fs.common.SshRemoteServerOperateOrdinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.mapper.BusiFmeClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiFmeClusterMapper;
import com.paradisecloud.fcm.dao.mapper.BusiFmeDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiFmeMapper;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.dao.model.BusiFmeCluster;
import com.paradisecloud.fcm.dao.model.BusiFmeClusterMap;
import com.paradisecloud.fcm.dao.model.BusiFmeDept;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.FmeClusterCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.core.FmeIDBuilder;
import com.paradisecloud.fcm.fme.websocket.async.WebsocketReconnecter;
import com.paradisecloud.fcm.fme.websocket.interfaces.IFmeCacheService;
import com.paradisecloud.fcm.fme.websocket.model.BusiFmeDBSynchronizer;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.CauseUtils;

/**  
 * <pre>FME缓存业务处理实现类</pre>
 * @author lilinhai
 * @since 2020-12-25 11:17
 * @version V1.0  
 */
@Service
public class FmeCacheServiceImpl implements IFmeCacheService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BusiFmeMapper busiFmeMapper;
    
    @Autowired
    private BusiFmeClusterMapper busiFmeClusterMapper;
    
    @Autowired
    private BusiFmeDeptMapper busiFmeDeptMapper;
    
    @Autowired
    private BusiFmeClusterMapMapper busiFmeClusterMapMapper;
    
    @Override
    public void initFmeBridge(FmeBridge fmeBridge)
    {
        WebsocketReconnecter.getInstance().add(fmeBridge);
    }
    
    /**
     * <pre>删除一个FME</pre>
     * @author lilinhai
     * @since 2020-12-30 11:12  void
     */
    public void deleteFme(long bridgeHostId)
    {
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(bridgeHostId);
        if (fmeBridge != null)
        {
            Map<String, ConferenceContext> ccMap = new HashMap<>();
            fmeBridge.getDataCache().eachCoSpace((coSpace) -> {

                ConferenceContext conferenceContextExist = null;
                Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(coSpace.getUri());
                if (conferenceContextList != null && conferenceContextList.size() > 0) {
                    for (ConferenceContext conferenceContextTemp : conferenceContextList) {
                        if (coSpace.getId().equals(conferenceContextTemp.getCoSpaceId())) {
                            conferenceContextExist = conferenceContextTemp;
                            break;
                        }
                    }
                }

                ConferenceContext cc = conferenceContextExist;
                if (cc != null)
                {
                    ccMap.put(cc.getId(), cc);
                }
            });
            
            if (!ccMap.isEmpty())
            {
                throw new SystemException(1002435, "当前FME有会议正在进行中，无法删除！");
            }
            
            BusiFme con = new BusiFme();
            con.setSpareFmeId(bridgeHostId);
            List<BusiFme> gs = busiFmeMapper.selectBusiFmeList(con);
            if (!ObjectUtils.isEmpty(gs))
            {
                throw new SystemException(1000016, "该FME作为备用FME已被其它FME关联，不能删除！");
            }
            
            BusiFmeDept con1 = new BusiFmeDept();
            con1.setFmeId(bridgeHostId);
            con1.setFmeType(FmeType.SINGLE_NODE.getValue());
            List<BusiFmeDept> fds = busiFmeDeptMapper.selectBusiFmeDeptList(con1);
            if (!ObjectUtils.isEmpty(fds))
            {
                throw new SystemException(1000016, "该FME正在被租户使用，不能删除！");
            }
            
            BusiFmeClusterMap con2 = new BusiFmeClusterMap();
            con2.setFmeId(bridgeHostId);
            List<BusiFmeClusterMap> fcms = busiFmeClusterMapMapper.selectBusiFmeClusterMapList(con2);
            if (!ObjectUtils.isEmpty(fcms))
            {
                throw new SystemException(1000016, "该FME正在集群中，无法删除，请先从集群中剔除，再删除！");
            }
            FmeBridgeCache.getInstance().delete(fmeBridge);
        }
        
        logger.info("删除FME命令下发成功，bridgeHostId: {}", bridgeHostId);
    }
    
    /**
     * <pre>清理指定fme数据缓存</pre>
     * @author lilinhai
     * @since 2020-12-31 13:42
     * @param fmeBridge
     * @see com.paradisecloud.fcm.fme.websocket.interfaces.IFmeCacheService#clearFmeDataCache(com.paradisecloud.fcm.fme.cache.model.FmeBridge)
     */
    public void clearFmeDataCache(FmeBridge fmeBridge)
    {
        fmeBridge.getDataCache().clearAndDestroy();
    }
    
    public BusiFmeCluster getFmeClusterById(long cid)
    {
        return FmeClusterCache.getInstance().get(cid);
    }
    
    /**
     * <pre>新增FME，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    public synchronized void addFme(BusiFme bh)
    {
        // 字段空值校验
        if (ObjectUtils.isEmpty(bh.getIp())
                || ObjectUtils.isEmpty(bh.getName()))
        {
            throw new SystemException(1000000, "新增的FME“名称”, “ip”, “端口”,“权重”和“归属组”不能为空！");
        }
        
        if (ObjectUtils.isEmpty(bh.getUsername()))
        {
            bh.setUsername("ttadmin");
        }
        
        if (ObjectUtils.isEmpty(bh.getPassword()))
        {
            bh.setPassword("tTcl0uds@cn");
        }
        
        if (ObjectUtils.isEmpty(bh.getAdminUsername()))
        {
            bh.setAdminUsername("ttadmin");
        }
        
        if (ObjectUtils.isEmpty(bh.getAdminPassword()))
        {
            bh.setAdminPassword("tTcl0uds@cn");
        }
        
        if (bh.getPort() == null)
        {
            bh.setPort(9443);
        }
        
        if (bh.getPort() <= 0 || bh.getPort() > 65535)
        {
            throw new SystemException(1000103, "端口号不正确");
        }
        
        // 获取FME组对象
        if (FmeBridgeCache.getInstance().getFmeBridgeByBridgeAddress(FmeIDBuilder.build(bh)) != null)
        {
            throw new SystemException(1000000, "新增的FME“" + FmeIDBuilder.build(bh) + "”已存在，请勿重复添加！");
        }
        
        BusiFme query = new BusiFme();
        query.setIp(bh.getIp());
        query.setPort(bh.getPort());
        List<BusiFme> bs = busiFmeMapper.selectBusiFmeList(query);
        BusiFme old = null;
        if (!ObjectUtils.isEmpty(bs))
        {
            old = bs.get(0);
            bh.setId(old.getId());
            bh.setUpdateTime(new Date());
        }
        else
        {
            bh.setCreateTime(new Date());
        }
        
        bh.setCapacity(bh.getCapacity() == null ? 80 : bh.getCapacity());
        busiFmeMapper.insertBusiFme(bh);
        FmeBridge fmeBridge = new FmeBridge(bh);
        FmeBridgeCache.getInstance().update(fmeBridge);
        this.initFmeBridge(fmeBridge);
        logger.info("添加FME并初始化成功：" + bh);
    }
    
    /**
     * <pre>修改FME，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:10  void
     */
    public void updateFme(BusiFme bh)
    {
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(bh.getId());
        if (fmeBridge != null)
        {
            // 取出内存对象，做属性拷贝
            BusiFme bhMemory = fmeBridge.getBusiFme();
            if ((bh.getIp() != null && !bhMemory.getIp().equals(bh.getIp()))
                    || (bh.getPort() != null && !bhMemory.getPort().equals(bh.getPort()))
                    || (bh.getStatus() != null && !bhMemory.getStatus().equals(bh.getStatus())))
            {
                throw new SystemException(1000000, "FME的“ip”, “端口不支持修改，若是要更改，请删除该FME，重新创建！");
            }
            
            // 设置基本属性
            bhMemory.setName(bh.getName() == null ? bhMemory.getName() : bh.getName());
            bhMemory.setSpareFmeId(bh.getSpareFmeId());
            bhMemory.setUpdateTime(new Date());
            bhMemory.setCucmIp(bh.getCucmIp());
            bhMemory.setCapacity(bh.getCapacity() == null ? 80 : bh.getCapacity());
            
            // 异步更新数据库
            BusiFmeDBSynchronizer.getInstance().add(bhMemory);
        }
    }
    
    /**
     * <pre>新增FME组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void addBusiFmeCluster(BusiFmeCluster busiFmeCluster)
    {
        busiFmeCluster.setCreateTime(new Date());
        busiFmeClusterMapper.insertBusiFmeCluster(busiFmeCluster);
        
        // 添加缓存
        FmeClusterCache.getInstance().put(busiFmeCluster.getId(), busiFmeCluster);
        logger.info("添加FME组成功: {}", busiFmeCluster);
    }
    
    /**
     * <pre>删除FME集群</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void deleteBusiFmeCluster(long id)
    {
        if (FmeBridgeCache.getInstance().isInUse(id))
        {
            throw new SystemException(1000013, "FME集群的删除，请先删除该集群下所有FME，再删除该FME集群，最后再重新创建！");
        }
        
        BusiFmeCluster con = new BusiFmeCluster();
        con.setSpareFmeType(FmeType.CLUSTER.getValue());
        con.setSpareFmeId(id);
        List<BusiFmeCluster> gs = busiFmeClusterMapper.selectBusiFmeClusterList(con);
        if (!ObjectUtils.isEmpty(gs))
        {
            throw new SystemException(1000016, "该集群作为备用集群已被其集群关联，不能删除！");
        }
        
        BusiFmeDept con1 = new BusiFmeDept();
        con1.setFmeId(id);
        con1.setFmeType(FmeType.CLUSTER.getValue());
        List<BusiFmeDept> fds = busiFmeDeptMapper.selectBusiFmeDeptList(con1);
        if (!ObjectUtils.isEmpty(fds))
        {
            throw new SystemException(1000016, "该集群正在被租户使用，不能删除！");
        }
        
        int c = busiFmeClusterMapper.deleteBusiFmeClusterById(id);
        if (c > 0)
        {
            FmeClusterCache.getInstance().remove(id);
            logger.info("删除FME集群成功，id: {}", id);
        }
    }
    
    /**
     * <pre>获取所有FME组</pre>
     * @author lilinhai
     * @since 2021-01-21 15:54 
     * @return
     * @see com.paradisecloud.fcm.fme.websocket.interfaces.IFmeCacheService#getAllBusiFmeCluster()
     */
    public List<ModelBean> getAllBusiFmeCluster()
    {
        List<BusiFmeCluster> gs = new ArrayList<>(FmeClusterCache.getInstance().values());
        List<ModelBean> ms = new ArrayList<>();
        for (BusiFmeCluster busiFmeCluster : gs)
        {
            ModelBean m = new ModelBean(busiFmeCluster);
            m.put("bindDeptCount", DeptFmeMappingCache.getInstance().getBindDeptCount(FmeType.CLUSTER, busiFmeCluster.getId()));
            ms.add(m);
        }
        return ms;
    }
    
    /**
     * <pre>修改FME组，需要考虑状态修改后的业务处理，如禁用等</pre>
     * @author lilinhai
     * @since 2020-12-30 11:09  void
     */
    public void updateBusiFmeCluster(BusiFmeCluster busiFmeCluster)
    {
        busiFmeCluster.setUpdateTime(new Date());
        busiFmeClusterMapper.updateBusiFmeCluster(busiFmeCluster);
        FmeClusterCache.getInstance().put(busiFmeCluster.getId(), busiFmeClusterMapper.selectBusiFmeClusterById(busiFmeCluster.getId()));
        logger.info("修改FME集群信息成功: {}", busiFmeCluster);
    }

    @Override
    public void mtuSettings(Long fmeId, int intValue)
    {
        try
        {
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().get(fmeId);
            Assert.notNull(fmeBridge, "FME不存在：" + fmeId);
            fmeBridge.getJschInvoker().execCmd("iface a mtu " + intValue);
        }
        catch (Exception e)
        {
            logger.error("MUT设置失败：", e);
            throw new RuntimeException("MUT设置失败：" + CauseUtils.getRootCause(e));
        }
    }

    /**
     * 根据IP ping 终端
     *
     * 连接fme 3分钟无动作  自动断开
     *
     * @param Ip
     */
    @Override
    public String pingIp(String ip,long id) {
        String str = null;
        BusiFme busiFme = FmeBridgeCache.getInstance().get(id).getBusiFme();
        String pingIp = "ping " + ip;
        SshRemoteServerOperateOrdinary sshRemoteServerOperateOrdinary = new SshRemoteServerOperateOrdinary();

        if(busiFme != null){
            try {
                sshRemoteServerOperateOrdinary.sshRemoteCallLogin(busiFme.getIp(),busiFme.getUsername(),busiFme.getPassword(),22);

                if (sshRemoteServerOperateOrdinary.isLogined()){
                    str = sshRemoteServerOperateOrdinary.execCommand(pingIp);
                    System.out.println(str);
                    return str;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                sshRemoteServerOperateOrdinary.closeSession();
            }
        }

        return str;
    }
    
}
