/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : FmeHttpInvokerCache.java
 * Package : com.paradisecloud.sync.cache
 * 
 * @author lilinhai
 * 
 * @since 2020-12-16 12:19
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiFmeClusterMap;
import com.paradisecloud.fcm.dao.model.BusiFmeDept;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeProcessor;
import com.paradisecloud.fcm.fme.cache.exception.NoAvailableFmeBridgeException;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.FmeBridgeCluster;
import com.paradisecloud.fcm.fme.cache.model.FmeBridgeCollection;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.model.GenericValue;

/**
 * <pre>会议桥api工具对象缓存</pre>
 * 
 * @author lilinhai
 * @since 2020-12-16 12:19
 * @version V1.0
 */
public abstract class FmeBridgeCache
{
    
    private static final FmeBridgeCache INSTANCE = new FmeBridgeCache()
    {
    };
    
    /**
     * 正常可用的，属于启用类别
     */
    private Map<String, FmeBridge> ipToFmeBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 所有fme调用器缓存，key：会议桥的id
     */
    private Map<Long, FmeBridge> fmeBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 一个组对应的FME集合映射，key：groupId
     */
    private Map<Long, FmeBridgeCluster> fmeBridgeClusterMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-16 12:19
     */
    private FmeBridgeCache()
    {
    }
    
    /**
     * <pre>更新到全集缓存中</pre>
     * @author lilinhai
     * @since 2020-12-29 17:44 
     * @param fmeBridge void
     */
    public synchronized void update(FmeBridge fmeBridge)
    {
        
        // 添加ID映射
        fmeBridgeMap.put(fmeBridge.getBusiFme().getId(), fmeBridge);
        
        // 添加IP映射
        ipToFmeBridgeMap.put(fmeBridge.getBridgeAddress(), fmeBridge);
    }
    
    /**
     * 将fme添加到对应的fme组中（一个fme可以添加到多个fme组中，一个组可以添加多个fme，即多对多的关系）
     * @author lilinhai
     * @since 2021-03-17 15:27 
     * @param fmeId
     * @param fmeClusterId void
     */
    public synchronized void update(BusiFmeClusterMap fmeClusterMap)
    {
        FmeBridge fmeBridge = fmeBridgeMap.get(fmeClusterMap.getFmeId());
        Assert.notNull(fmeBridge, "fme绑定fme集群失败，fmeBridge桥找不到：" + fmeClusterMap.getFmeId());
        fmeBridge.setWeight(fmeClusterMap.getWeight());
        FmeBridgeCluster fmeBridgeCluster = getByFmeClusterId(fmeClusterMap.getClusterId());
        if (fmeBridgeCluster == null)
        {
            fmeBridgeCluster = new FmeBridgeCluster();
            fmeBridgeClusterMap.put(fmeClusterMap.getClusterId(), fmeBridgeCluster);
        }
        fmeBridgeCluster.addFmeBridge(fmeBridge);
    }
    
    /**
     * 从fme集群删除fme节点
     * @author lilinhai
     * @since 2021-03-19 18:34 
     * @param fmeClusterMap void
     */
    public synchronized void removeFmeFromCluster(BusiFmeClusterMap fmeClusterMap)
    {
        FmeBridgeCluster fmeBridgeCluster = getByFmeClusterId(fmeClusterMap.getClusterId());
        if (fmeBridgeCluster != null)
        {
            FmeBridge fmeBridge = fmeBridgeMap.get(fmeClusterMap.getFmeId());
            if (fmeBridge != null)
            {
                fmeBridgeCluster.remove(fmeBridge);
            }
        }
    }
    
    /**
     * <pre>根据组ID获取FME调用器集合</pre>
     * @author lilinhai
     * @since 2021-01-28 15:25 
     * @param clusterId
     * @return List<FmeHttpInvoker>
     */
    public FmeBridgeCluster getByFmeClusterId(long clusterId)
    {
        return fmeBridgeClusterMap.get(clusterId);
    }
    
    /**
     * <pre>根据IP和端口号会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29 
     * @param bridgeAddress IP:PORT
     * @return List<FmeHttpInvoker>
     */
    public FmeBridge getFmeBridgeByBridgeAddress(String bridgeAddress)
    {
        return ipToFmeBridgeMap.get(bridgeAddress);
    }

    /**
     * <pre>根据IP获取会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29
     * @param ipAddress
     * @return List<FmeHttpInvoker>
     */
    public FmeBridge getFmeBridgeByBridgeAddressOnly(String ipAddress)
    {
        for (FmeBridge fmeBridge : ipToFmeBridgeMap.values()) {
            if (fmeBridge.getBridgeAddress().startsWith(ipAddress + ":")) {
                return fmeBridge;
            }
        }
        return null;
    }
    
    /**
     * <pre>根据数据库id获取对象</pre>
     * @author lilinhai
     * @since 2020-12-29 18:05 
     * @param busiFmeId
     * @return FmeHttpInvoker
     */
    public FmeBridge get(long busiFmeId)
    {
        return fmeBridgeMap.get(busiFmeId);
    }
    
    /**
     * <pre>根据ID获取会议桥组对象</pre>
     * @author lilinhai
     * @since 2021-01-04 14:31 
     * @param clusterId
     * @return BridgeHostGroup
     */
    public boolean isInUse(long clusterId)
    {
        FmeBridgeCluster fbc = getByFmeClusterId(clusterId);
        return fbc != null && !ObjectUtils.isEmpty(fbc.getFmeBridges());
    }
    
    /**
     * <pre>获取备用的FME的http调用器</pre>
     * @author lilinhai
     * @since 2021-01-04 14:27 
     * @return FmeHttpInvoker
     */
    public FmeBridge getSpareFmeBridge(FmeBridge fmeBridge)
    {
        if (fmeBridge.getBusiFme().getSpareFmeId() == null)
        {
            return null;
        }
        
        return getSpareFmeBridge(fmeBridge.getBusiFme().getSpareFmeId());
    }
    
    /**
     * <pre>获取备用的FME的http调用器</pre>
     * @author lilinhai
     * @since 2021-01-04 14:27 
     * @return FmeHttpInvoker
     */
    public FmeBridge getSpareFmeBridge(long spareFmeId)
    {
        FmeBridge spareFmeBridge = fmeBridgeMap.get(spareFmeId);
        return spareFmeBridge.isAvailable() ? spareFmeBridge : getSpareFmeBridge(spareFmeBridge);
    }
    
    /**
     * 删除fme桥
     * @author lilinhai
     * @since 2021-03-19 15:59 
     * @param fmeBridge void
     */
    public void delete(FmeBridge fmeBridge)
    {
        if (fmeBridge != null)
        {
            fmeBridge.setDeleted(true);
            
            // 销毁会议桥对象
            fmeBridge.destroy();
            FmeBridge removed = fmeBridgeMap.remove(fmeBridge.getBusiFme().getId());
            if (removed != null)
            {
                removed.getFmeLogger().logWebsocketInfo("从invokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除", true);
            }
            
            fmeBridgeClusterMap.forEach((k, v) -> {
                if (!ObjectUtils.isEmpty(v))
                {
                    v.remove(fmeBridge);
                }
            });
            
            removed = ipToFmeBridgeMap.remove(fmeBridge.getBridgeAddress());
            if (removed != null)
            {
                removed.getFmeLogger().logWebsocketInfo("从ipToInvokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除!", true);
            }
        }
    }
    
    /**
     * <pre>获取所有的FME桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<FmeBridge>
     */
    public List<FmeBridge> getFmeBridges()
    {
        return new ArrayList<>(fmeBridgeMap.values());
    }
    
    public List<FmeBridge> getFmeBridgesByDept(long deptId)
    {
        BusiFmeDept busiFmeDept = DeptFmeMappingCache.getInstance().getBindFme(deptId);
        if (busiFmeDept != null)
        {
            if (busiFmeDept.getFmeType().intValue() == FmeType.SINGLE_NODE.getValue())
            {
                FmeBridge fmeBridge = fmeBridgeMap.get(busiFmeDept.getFmeId());
                return valid(Arrays.asList(fmeBridge));
            }
            else
            {
                FmeBridgeCluster fmeBridgeCluster = getByFmeClusterId(busiFmeDept.getFmeId());
                if (fmeBridgeCluster != null)
                {
                    return valid(fmeBridgeCluster.getFmeBridges());
                }
            }
        }
        String errInfo = "很抱歉，【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】当前无可用的MCU，请联系管理员配置您的MCU！";
        LoggerFactory.getLogger(getClass()).error(errInfo);
        throw new NoAvailableFmeBridgeException(1001002, errInfo);
    }
    
    /**
     * <pre>根据部门ID获取主用的所有FME，按权重最大倒叙排列</pre>
     * @author lilinhai
     * @since 2021-01-26 15:21 
     * @param deptId
     * @return List<FmeHttpInvoker>
     */
    public FmeBridgeCollection getAvailableFmeBridgesByDept(long deptId)
    {
        FmeBridgeCollection fmeBridgeCollection = new FmeBridgeCollection();
        BusiFmeDept busiFmeDept = DeptFmeMappingCache.getInstance().getBindFme(deptId);
        if (busiFmeDept == null)
        {
            return null;
        }
        
        // 如果是单节点
        if (busiFmeDept.getFmeType().intValue() == FmeType.SINGLE_NODE.getValue())
        {
            FmeBridge fmeBridge = fmeBridgeMap.get(busiFmeDept.getFmeId());
            
            // 可用则直接添加，不可用则转备用
            if (fmeBridge.isAvailable())
            {
                fmeBridgeCollection.addFmeBridge(fmeBridge);
            }
            // 转备用
            else
            {
                fmeBridge = getSpareFmeBridge(fmeBridge);
                if (fmeBridge != null)
                {
                    fmeBridgeCollection.addFmeBridge(fmeBridge);
                }
            }
        }
        else
        {
            // 根据集群ID获取集群下可用的fme集合
            this.addAvailableFmeBridge(fmeBridgeCollection, busiFmeDept.getFmeId());
        }
        
        return ObjectUtils.isEmpty(fmeBridgeCollection.getFmeBridges()) ? null : fmeBridgeCollection;
    }
    
    /**
     * <pre>获取可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<BridgeApiUtil>
     */
    public List<FmeBridge> getAvailableFmeBridges()
    {
        return getFmeBridgesByStatus(true);
    }
    
    /**
     * <pre>获取不可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:26 
     * @return List<BridgeApiUtil>
     */
    public List<FmeBridge> getUnAvailableFmeBridges()
    {
        return getFmeBridgesByStatus(false);
    }
    
    /**
     * 从当前部门名下的所有fme中根据callId获取fme桥
     * @author lilinhai
     * @since 2021-02-07 17:09 
     * @param deptId
     * @param callId
     * @return FmeBridge
     */
    public FmeBridge getFmeBridgeByConferenceContext(ConferenceContext conferenceContext)
    {
        return getFmeBridgeByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber(), true);
    }
    
    /**
     * 从当前部门名下的所有fme中根据callId获取fme桥
     * @author lilinhai
     * @since 2021-02-07 17:09 
     * @param deptId
     * @param callId
     * @return FmeBridge
     */
    public FmeBridge getFmeBridgeByCallId(long deptId, String callId)
    {
        GenericValue<FmeBridge> genericValue = new GenericValue<>();
        doBreakFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
        {
            @Override
            public void process(FmeBridge fmeBridge)
            {
                Call call = fmeBridge.getDataCache().getCallByUuid(callId);
                if (call != null)
                {
                    genericValue.setValue(fmeBridge);
                    setBreak(true);
                }
            }
        });
        return genericValue.getValue();
    }
    
    /**
     * 从当前部门名下的所有fme中根据coSpaceId获取fme桥
     * @author lilinhai
     * @since 2021-02-07 17:09 
     * @param deptId
     * @param coSpaceId
     * @return FmeBridge
     */
    public FmeBridge getFmeBridgeByCoSpaceId(long deptId, String coSpaceId)
    {
        return getFmeBridgeByCoSpaceId(deptId, coSpaceId, true);
    }
    
    /**
     * 从当前部门名下的所有fme中根据coSpaceId获取fme桥
     * @author lilinhai
     * @since 2021-02-07 17:09 
     * @param deptId
     * @param coSpaceId
     * @return FmeBridge
     */
    public FmeBridge getFmeBridgeByCoSpaceId(long deptId, String coSpaceId, boolean throwError)
    {
        GenericValue<FmeBridge> genericValue = new GenericValue<>();
        doBreakFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
        {
            @Override
            public void process(FmeBridge fmeBridge)
            {
                CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByUuid(coSpaceId);
                if (coSpace != null)
                {
                    genericValue.setValue(fmeBridge);
                    setBreak(true);
                }
            }
        }, throwError);
        return genericValue.getValue();
    }
    
    /**
     * 从当前部门名下的所有fme中根据conferenceNumber获取fme桥
     * @author lilinhai
     * @since 2021-02-07 17:09 
     * @param deptId
     * @param conferenceNumber
     * @return FmeBridge
     */
    public FmeBridge getFmeBridgeByConferenceNumber(long deptId, String conferenceNumber, boolean throwError)
    {
        GenericValue<FmeBridge> genericValue = new GenericValue<>();
        doBreakFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceNumber);
                if (coSpace != null)
                {
                    genericValue.setValue(fmeBridge);
                    setBreak(true);
                }
            }
        }, throwError);
        return genericValue.getValue();
    }
    
    /**
     * 根据FME参会者获取级联fme桥
     * @author lilinhai
     * @since 2021-02-07 17:09 
     * @param deptId
     * @param conferenceNumber
     * @return FmeBridge
     */
    public FmeBridge getCascadeFmeBridgeByFmeAttendee(FmeAttendee attendee, boolean throwError)
    {
        return getFmeBridgeByConferenceNumber(attendee.getCascadeDeptId(), attendee.getCascadeConferenceNumber(), throwError);
    }
    
    /**
     * 根据已入会的参会者获取fme桥
     * @author lilinhai
     * @since 2021-02-07 17:09 
     * @param deptId
     * @param conferenceNumber
     * @return FmeBridge
     */
    public FmeBridge getFmeBridgeByMeetingJoinedAttendee(Attendee attendee)
    {
        return getFmeBridgeByCallId(attendee.getDeptId(), attendee.getCallId());
    }
    
    /**
     * 从所有fme中根据callId获取fme桥
     * @author lilinhai
     * @since 2021-02-07 17:09 
     * @param callId
     * @return FmeBridge
     */
    public FmeBridge getFmeBridgeByCallId(String callId)
    {
        List<FmeBridge> fbs = getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(fbs))
        {
            for (FmeBridge fmeBridge : fbs)
            {
                Call call = fmeBridge.getDataCache().getCallByUuid(callId);
                if (call != null)
                {
                    return fmeBridge;
                }
            }
        }
        return null;
    }
    
    /**
     * 处理中断FME桥业务
     * @author lilinhai
     * @since 2021-03-22 17:42 
     * @param deptId
     * @param fmeBridgeProcessor void
     */
    public void doBreakFmeBridgeBusiness(long deptId, FmeBridgeProcessor fmeBridgeProcessor)
    {
        doBreakFmeBridgeBusiness(deptId, fmeBridgeProcessor, true);
    }
    
    /**
     * 处理中断FME桥业务
     * @author lilinhai
     * @since 2021-03-22 12:21 
     * @param deptId
     * @param fmeBridgeProcessor
     * @param throwError void
     */
    public void doBreakFmeBridgeBusiness(long deptId, FmeBridgeProcessor fmeBridgeProcessor, boolean throwError)
    {
        doFmeBridgeBusiness(deptId, FmeBridgeProcessingStrategy.BREAK, fmeBridgeProcessor, throwError);
    }
    
    /**
     * 处理遍历FME桥业务
     * @author lilinhai
     * @since 2021-03-22 12:21 
     * @param deptId
     * @param fmeBridgeProcessor
     * @param throwError void
     */
    public void doTraverseFmeBridgeBusiness(long deptId, FmeBridgeProcessor fmeBridgeProcessor)
    {
        doTraverseFmeBridgeBusiness(deptId, fmeBridgeProcessor, true);
    }
    
    /**
     * 处理遍历FME桥业务
     * @author lilinhai
     * @since 2021-03-22 12:21 
     * @param deptId
     * @param fmeBridgeProcessor
     * @param throwError void
     */
    public void doTraverseFmeBridgeBusiness(long deptId, FmeBridgeProcessor fmeBridgeProcessor, boolean throwError)
    {
        doFmeBridgeBusiness(deptId, FmeBridgeProcessingStrategy.TRAVERSE, fmeBridgeProcessor, throwError);
    }
    
    /**
     * 处理随机FME桥业务(FME不存在，会报错)
     * @author lilinhai
     * @since 2021-03-22 12:21 
     * @param deptId
     * @param fmeBridgeProcessor
     * @param throwError void
     */
    public void doRandomFmeBridgeBusiness(long deptId, FmeBridgeProcessor fmeBridgeProcessor)
    {
        doRandomFmeBridgeBusiness(deptId, fmeBridgeProcessor, true);
    }
    
    /**
     * 处理随机FME桥业务
     * @author lilinhai
     * @since 2021-03-22 12:21 
     * @param deptId
     * @param fmeBridgeProcessor
     * @param throwError void
     */
    public void doRandomFmeBridgeBusiness(long deptId, FmeBridgeProcessor fmeBridgeProcessor, boolean throwError)
    {
        doFmeBridgeBusiness(deptId, FmeBridgeProcessingStrategy.RANDOM, fmeBridgeProcessor, throwError);
    }
    
    /**
     * 处理FME桥业务
     * @author lilinhai
     * @since 2021-03-22 12:21 
     * @param deptId
     * @param fmeBridgeProcessor
     * @param fmeBridgeSelectionStrategy （FME桥选择策略，支持随机，遍历和指定三种）
     * @param throwError void
     */
    private void doFmeBridgeBusiness(long deptId, FmeBridgeProcessingStrategy fmeBridgeProcessingStrategy, FmeBridgeProcessor fmeBridgeProcessor, boolean throwError)
    {
        FmeBridgeCollection fmeBridgeCollection = getAvailableFmeBridgesByDept(deptId);
        if (fmeBridgeCollection == null || ObjectUtils.isEmpty(fmeBridgeCollection.getFmeBridges()))
        {
            if (throwError)
            {
                String errInfo = "很抱歉，【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】当前无可用的MCU，请联系管理员配置您的MCU！";
                LoggerFactory.getLogger(getClass()).error(errInfo);
                throw new NoAvailableFmeBridgeException(1001002, errInfo);
            }
            return;
        }
        
        List<FmeBridge> fbs = fmeBridgeCollection.getFmeBridges();
        if (!ObjectUtils.isEmpty(fmeBridgeProcessor.excludeFmeIps()))
        {
            for (FmeBridge fmeBridge : new ArrayList<>(fbs))
            {
                if (fmeBridgeProcessor.excludeFmeIps().contains(fmeBridge.getBusiFme().getIp()))
                {
                    fbs.remove(fmeBridge);
                }
            }
            
            if (ObjectUtils.isEmpty(fbs))
            {
                if (throwError)
                {
                    String errInfo = "很抱歉，【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】无法找到合适的FME节点去呼叫参会：" + fmeBridgeProcessor.excludeFmeIps();
                    LoggerFactory.getLogger(getClass()).error(errInfo);
                    throw new NoAvailableFmeBridgeException(1001002, errInfo);
                }
                return;
            }
        }
        
        // 集群就优先选主节点（权重值最大的）来呼叫
        if (fmeBridgeCollection.getMasterFmeBridge() != null)
        {
            fmeBridgeProcessingStrategy.process(fbs, fmeBridgeProcessor);
        }
        else
        {
            FmeBridgeProcessingStrategy.BREAK.process(fbs, fmeBridgeProcessor);
        }
    }
    
    /**
     * 根据可用状态获取桥集合
     * @author lilinhai
     * @since 2021-03-22 17:38 
     * @param isAvailable
     * @return List<FmeBridge>
     */
    private List<FmeBridge> getFmeBridgesByStatus(boolean isAvailable)
    {
        List<FmeBridge> fmeHttpInvokers = new ArrayList<>();
        Collection<FmeBridge> fhis = fmeBridgeMap.values();
        for (Iterator<FmeBridge> iterator = fhis.iterator(); iterator.hasNext();)
        {
            FmeBridge fmeHttpInvoker = iterator.next();
            if (fmeHttpInvoker.isAvailable() == isAvailable)
            {
                fmeHttpInvokers.add(fmeHttpInvoker);
            }
        }
        return fmeHttpInvokers;
    }
    
    /**
     * 添加可用的fme桥
     * @author lilinhai
     * @since 2021-03-19 16:00 
     * @param fmeBridgeCollection
     * @param clusterId
     * @param deptId void
     */
    private void addAvailableFmeBridge(FmeBridgeCollection fmeBridgeCollection, long clusterId)
    {
        // 根据集群ID获取集群下可用的fme集合
        FmeBridgeCluster fmeBridgeCluster = getByFmeClusterId(clusterId);
        if (fmeBridgeCluster == null)
        {
            return;
        }
        
        List<FmeBridge> availableFmeBridges0 = fmeBridgeCluster.getAvailableFmeBridges();
        if (!ObjectUtils.isEmpty(availableFmeBridges0))
        {
            fmeBridgeCollection.setMasterFmeBridge(fmeBridgeCluster.getMasterFmeBridge());
            for (FmeBridge fmeBridge : availableFmeBridges0)
            {
                fmeBridgeCollection.addFmeBridge(fmeBridge);
            }
        }
        
        if (ObjectUtils.isEmpty(fmeBridgeCollection.getFmeBridges()))
        {
            if (fmeBridgeCluster.getSpareFmeType() != null)
            {
                if (fmeBridgeCluster.getSpareFmeType() == FmeType.CLUSTER)
                {
                    this.addAvailableFmeBridge(fmeBridgeCollection, fmeBridgeCluster.getSpareFmeId());
                }
                else
                {
                    FmeBridge fmeBridge = getSpareFmeBridge(fmeBridgeCluster.getSpareFmeId());
                    if (fmeBridge != null)
                    {
                        fmeBridgeCollection.addFmeBridge(fmeBridge);
                    }
                }
            }
        }
    }
    
    private List<FmeBridge> valid(List<FmeBridge> fbs)
    {
        for (FmeBridge fb : fbs)
        {
            if (fb.isInitializing())
            {
                throw new SystemException(1009485, "FME" + (fbs.size() > 1 ? "集群" : "单") + "【" + fb.getBusiFme().getIp() + "】节点初始化中，即将就绪，请稍后...");
            }
        }
        return fbs;
    }
    
    public static FmeBridgeCache getInstance()
    {
        return INSTANCE;
    }
    
}
