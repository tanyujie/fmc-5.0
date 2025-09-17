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
package com.paradisecloud.fcm.mcu.kdc.cache;

import com.paradisecloud.fcm.common.enumer.McuKdcType;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcDept;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridgeCollection;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>会议桥api工具对象缓存</pre>
 * 
 * @author lilinhai
 * @since 2020-12-16 12:19
 * @version V1.0
 */
public abstract class McuKdcBridgeCache
{
    
    private static final McuKdcBridgeCache INSTANCE = new McuKdcBridgeCache()
    {
    };
    
    /**
     * 正常可用的，属于启用类别
     */
    private Map<String, McuKdcBridge> ipToMcuKdcBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 所有fme调用器缓存，key：会议桥的id
     */
    private Map<Long, McuKdcBridge> mcuKdcBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-16 12:19
     */
    private McuKdcBridgeCache()
    {
    }
    
    /**
     * <pre>更新到全集缓存中</pre>
     * @author lilinhai
     * @since 2020-12-29 17:44 
     * @param mcuKdcBridge void
     */
    public synchronized void update(McuKdcBridge mcuKdcBridge)
    {
        
        // 添加ID映射
        mcuKdcBridgeMap.put(mcuKdcBridge.getBusiMcuKdc().getId(), mcuKdcBridge);
        
        // 添加IP映射
        ipToMcuKdcBridgeMap.put(mcuKdcBridge.getBridgeAddress(), mcuKdcBridge);
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean containsKey(Long id) {
        return mcuKdcBridgeMap.containsKey(id);
    }
    
    /**
     * <pre>根据IP和端口号会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29 
     * @param bridgeAddress IP:PORT
     * @return List<FmeHttpInvoker>
     */
    public McuKdcBridge getMcuKdcBridgeByBridgeAddress(String bridgeAddress)
    {
        return ipToMcuKdcBridgeMap.get(bridgeAddress);
    }

    /**
     * <pre>根据IP获取会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29
     * @param ipAddress
     * @return List<FmeHttpInvoker>
     */
    public McuKdcBridge getMcuKdcBridgeByBridgeAddressOnly(String ipAddress)
    {
        for (McuKdcBridge fmeBridge : ipToMcuKdcBridgeMap.values()) {
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
     * @param busiMcuKdcId
     * @return FmeHttpInvoker
     */
    public McuKdcBridge get(long busiMcuKdcId)
    {
        return mcuKdcBridgeMap.get(busiMcuKdcId);
    }
    
    /**
     * 删除fme桥
     * @author lilinhai
     * @since 2021-03-19 15:59 
     * @param mcuKdcBridge void
     */
    public void delete(McuKdcBridge mcuKdcBridge)
    {
        if (mcuKdcBridge != null)
        {
            mcuKdcBridge.setDeleted(true);
            
            // 销毁会议桥对象
            mcuKdcBridge.destroy();
            McuKdcBridge removed = mcuKdcBridgeMap.remove(mcuKdcBridge.getBusiMcuKdc().getId());
            if (removed != null)
            {
                removed.getMcuKdcLogger().logInfo("从invokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除");
            }
            
            removed = ipToMcuKdcBridgeMap.remove(mcuKdcBridge.getBridgeAddress());
            if (removed != null)
            {
                removed.getMcuKdcLogger().logInfo("从ipToInvokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除!");
            }
        }
    }
    
    /**
     * <pre>获取所有的MCU桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<McuKdcBridge>
     */
    public List<McuKdcBridge> getMcuKdcBridges()
    {
        return new ArrayList<>(mcuKdcBridgeMap.values());
    }
    
    public List<McuKdcBridge> getMcuKdcBridgesByDept(long deptId)
    {
        BusiMcuKdcDept busiMcuKdcDept = DeptMcuKdcMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuKdcDept != null)
        {
            if (busiMcuKdcDept.getMcuType().intValue() == McuKdcType.SINGLE_NODE.getValue())
            {
                McuKdcBridge fmeBridge = mcuKdcBridgeMap.get(busiMcuKdcDept.getMcuId());
                return valid(Arrays.asList(fmeBridge));
            }
        }
        String errInfo = "很抱歉，【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】当前无可用的MCU，请联系管理员配置您的MCU！";
        LoggerFactory.getLogger(getClass()).error(errInfo);
        throw new SystemException(1001002, errInfo);
    }
    
    /**
     * <pre>根据部门ID获取主用的所有MCU，按权重最大倒叙排列</pre>
     * @author lilinhai
     * @since 2021-01-26 15:21 
     * @param deptId
     * @return List<FmeHttpInvoker>
     */
    public McuKdcBridgeCollection getAvailableMcuKdcBridgesByDept(long deptId)
    {
        McuKdcBridgeCollection mcuXdBridgeCollection = new McuKdcBridgeCollection();
        BusiMcuKdcDept busiMcuKdcDept = DeptMcuKdcMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuKdcDept == null)
        {
            return null;
        }
        
        // 如果是单节点
        if (busiMcuKdcDept.getMcuType().intValue() == McuKdcType.SINGLE_NODE.getValue())
        {
            McuKdcBridge mcuXdBridge = mcuKdcBridgeMap.get(busiMcuKdcDept.getMcuId());
            
            // 可用则直接添加，不可用则转备用
            if (mcuXdBridge.isAvailable())
            {
                mcuXdBridgeCollection.addMcuKdcBridge(mcuXdBridge);
                mcuXdBridgeCollection.setMasterMcuKdcBridge(mcuXdBridge);
            }
        }
        
        return ObjectUtils.isEmpty(mcuXdBridgeCollection.getMcuKdcBridges()) ? null : mcuXdBridgeCollection;
    }
    
    /**
     * <pre>获取可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<BridgeApiUtil>
     */
    public List<McuKdcBridge> getAvailableMcuKdcBridges()
    {
        return getMcuKdcBridgesByStatus(true);
    }
    
    /**
     * <pre>获取不可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:26 
     * @return List<BridgeApiUtil>
     */
    public List<McuKdcBridge> getUnAvailableMcuKdcBridges()
    {
        return getMcuKdcBridgesByStatus(false);
    }
    
//    /**
//     * 从当前部门名下的所有fme中根据callId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param callId
//     * @return McuKdcBridge
//     */
//    public McuKdcBridge getMcuKdcBridgeByConferenceContext(ConferenceContext conferenceContext)
//    {
//        return getMcuKdcBridgeByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber(), true);
//    }
    
//    /**
//     * 从当前部门名下的所有fme中根据callId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param callId
//     * @return McuKdcBridge
//     */
//    public McuKdcBridge getMcuKdcBridgeByCallId(long deptId, String callId)
//    {
//        GenericValue<McuKdcBridge> genericValue = new GenericValue<>();
//        doBreakMcuKdcBridgeBusiness(deptId, new McuKdcBridgeAddpterProcessor()
//        {
//            @Override
//            public void process(McuKdcBridge fmeBridge)
//            {
//                Call call = fmeBridge.getDataCache().getCallByUuid(callId);
//                if (call != null)
//                {
//                    genericValue.setValue(fmeBridge);
//                    setBreak(true);
//                }
//            }
//        });
//        return genericValue.getValue();
//    }
//
//    /**
//     * 从当前部门名下的所有fme中根据coSpaceId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param coSpaceId
//     * @return McuKdcBridge
//     */
//    public McuKdcBridge getMcuKdcBridgeByCoSpaceId(long deptId, String coSpaceId)
//    {
//        return getMcuKdcBridgeByCoSpaceId(deptId, coSpaceId, true);
//    }
//
//    /**
//     * 从当前部门名下的所有fme中根据coSpaceId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param coSpaceId
//     * @return McuKdcBridge
//     */
//    public McuKdcBridge getMcuKdcBridgeByCoSpaceId(long deptId, String coSpaceId, boolean throwError)
//    {
//        GenericValue<McuKdcBridge> genericValue = new GenericValue<>();
//        doBreakMcuKdcBridgeBusiness(deptId, new McuKdcBridgeAddpterProcessor()
//        {
//            @Override
//            public void process(McuKdcBridge fmeBridge)
//            {
//                CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByUuid(coSpaceId);
//                if (coSpace != null)
//                {
//                    genericValue.setValue(fmeBridge);
//                    setBreak(true);
//                }
//            }
//        }, throwError);
//        return genericValue.getValue();
//    }
    
//    /**
//     * 从当前部门名下的所有fme中根据conferenceNumber获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param conferenceNumber
//     * @return McuKdcBridge
//     */
//    public McuKdcBridge getMcuKdcBridgeByConferenceNumber(long deptId, String conferenceNumber, boolean throwError)
//    {
//        GenericValue<McuKdcBridge> genericValue = new GenericValue<>();
//        doBreakMcuKdcBridgeBusiness(deptId, new McuKdcBridgeAddpterProcessor()
//        {
//            public void process(McuKdcBridge fmeBridge)
//            {
//                CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceNumber);
//                if (coSpace != null)
//                {
//                    genericValue.setValue(fmeBridge);
//                    setBreak(true);
//                }
//            }
//        }, throwError);
//        return genericValue.getValue();
//    }
//
//    /**
//     * 根据MCU参会者获取级联fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param conferenceNumber
//     * @return McuKdcBridge
//     */
//    public McuKdcBridge getCascadeMcuKdcBridgeByFmeAttendee(FmeAttendee attendee, boolean throwError)
//    {
//        return getMcuKdcBridgeByConferenceNumber(attendee.getCascadeDeptId(), attendee.getCascadeConferenceNumber(), throwError);
//    }
//
//    /**
//     * 根据已入会的参会者获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param conferenceNumber
//     * @return McuKdcBridge
//     */
//    public McuKdcBridge getMcuKdcBridgeByMeetingJoinedAttendee(Attendee attendee)
//    {
//        return getMcuKdcBridgeByCallId(attendee.getDeptId(), attendee.getCallId());
//    }
//
//    /**
//     * 从所有fme中根据callId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param callId
//     * @return McuKdcBridge
//     */
//    public McuKdcBridge getMcuKdcBridgeByCallId(String callId)
//    {
//        List<McuKdcBridge> fbs = getAvailableMcuKdcBridges();
//        if (!ObjectUtils.isEmpty(fbs))
//        {
//            for (McuKdcBridge fmeBridge : fbs)
//            {
//                Call call = fmeBridge.getDataCache().getCallByUuid(callId);
//                if (call != null)
//                {
//                    return fmeBridge;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 处理中断MCU桥业务
//     * @author lilinhai
//     * @since 2021-03-22 17:42
//     * @param deptId
//     * @param fmeBridgeProcessor void
//     */
//    public void doBreakMcuKdcBridgeBusiness(long deptId, McuKdcBridgeProcessor fmeBridgeProcessor)
//    {
//        doBreakMcuKdcBridgeBusiness(deptId, fmeBridgeProcessor, true);
//    }
//
//    /**
//     * 处理中断MCU桥业务
//     * @author lilinhai
//     * @since 2021-03-22 12:21
//     * @param deptId
//     * @param fmeBridgeProcessor
//     * @param throwError void
//     */
//    public void doBreakMcuKdcBridgeBusiness(long deptId, McuKdcBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        doMcuKdcBridgeBusiness(deptId, McuKdcBridgeProcessingStrategy.BREAK, fmeBridgeProcessor, throwError);
//    }
//
//    /**
//     * 处理遍历MCU桥业务
//     * @author lilinhai
//     * @since 2021-03-22 12:21
//     * @param deptId
//     * @param fmeBridgeProcessor
//     * @param throwError void
//     */
//    public void doTraverseMcuKdcBridgeBusiness(long deptId, McuKdcBridgeProcessor fmeBridgeProcessor)
//    {
//        doTraverseMcuKdcBridgeBusiness(deptId, fmeBridgeProcessor, true);
//    }
//
//    /**
//     * 处理遍历MCU桥业务
//     * @author lilinhai
//     * @since 2021-03-22 12:21
//     * @param deptId
//     * @param fmeBridgeProcessor
//     * @param throwError void
//     */
//    public void doTraverseMcuKdcBridgeBusiness(long deptId, McuKdcBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        doMcuKdcBridgeBusiness(deptId, McuKdcBridgeProcessingStrategy.TRAVERSE, fmeBridgeProcessor, throwError);
//    }
//
//    /**
//     * 处理随机MCU桥业务(MCU不存在，会报错)
//     * @author lilinhai
//     * @since 2021-03-22 12:21
//     * @param deptId
//     * @param fmeBridgeProcessor
//     * @param throwError void
//     */
//    public void doRandomMcuKdcBridgeBusiness(long deptId, McuKdcBridgeProcessor fmeBridgeProcessor)
//    {
//        doRandomMcuKdcBridgeBusiness(deptId, fmeBridgeProcessor, true);
//    }
//
//    /**
//     * 处理随机MCU桥业务
//     * @author lilinhai
//     * @since 2021-03-22 12:21
//     * @param deptId
//     * @param fmeBridgeProcessor
//     * @param throwError void
//     */
//    public void doRandomMcuKdcBridgeBusiness(long deptId, McuKdcBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        doMcuKdcBridgeBusiness(deptId, McuKdcBridgeProcessingStrategy.RANDOM, fmeBridgeProcessor, throwError);
//    }
//
//    /**
//     * 处理MCU桥业务
//     * @author lilinhai
//     * @since 2021-03-22 12:21
//     * @param deptId
//     * @param fmeBridgeProcessor
//     * @param fmeBridgeSelectionStrategy （MCU桥选择策略，支持随机，遍历和指定三种）
//     * @param throwError void
//     */
//    private void doMcuKdcBridgeBusiness(long deptId, McuKdcBridgeProcessingStrategy fmeBridgeProcessingStrategy, McuKdcBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        McuKdcBridgeCollection fmeBridgeCollection = getAvailableMcuKdcBridgesByDept(deptId);
//        if (fmeBridgeCollection == null || ObjectUtils.isEmpty(fmeBridgeCollection.getMcuKdcBridges()))
//        {
//            if (throwError)
//            {
//                String errInfo = "很抱歉，【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】当前无可用的MCU，请联系管理员配置您的MCU！";
//                LoggerFactory.getLogger(getClass()).error(errInfo);
//                throw new NoAvailableMcuKdcBridgeException(1001002, errInfo);
//            }
//            return;
//        }
//
//        List<McuKdcBridge> fbs = fmeBridgeCollection.getMcuKdcBridges();
//        if (!ObjectUtils.isEmpty(fmeBridgeProcessor.excludeFmeIps()))
//        {
//            for (McuKdcBridge fmeBridge : new ArrayList<>(fbs))
//            {
//                if (fmeBridgeProcessor.excludeFmeIps().contains(fmeBridge.getBusiFme().getIp()))
//                {
//                    fbs.remove(fmeBridge);
//                }
//            }
//
//            if (ObjectUtils.isEmpty(fbs))
//            {
//                if (throwError)
//                {
//                    String errInfo = "很抱歉，【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】无法找到合适的MCU节点去呼叫参会：" + fmeBridgeProcessor.excludeFmeIps();
//                    LoggerFactory.getLogger(getClass()).error(errInfo);
//                    throw new NoAvailableMcuKdcBridgeException(1001002, errInfo);
//                }
//                return;
//            }
//        }
//
//        // 集群就优先选主节点（权重值最大的）来呼叫
//        if (fmeBridgeCollection.getMasterMcuKdcBridge() != null)
//        {
//            fmeBridgeProcessingStrategy.process(fbs, fmeBridgeProcessor);
//        }
//        else
//        {
//            McuKdcBridgeProcessingStrategy.BREAK.process(fbs, fmeBridgeProcessor);
//        }
//    }
    
    /**
     * 根据可用状态获取桥集合
     * @author lilinhai
     * @since 2021-03-22 17:38 
     * @param isAvailable
     * @return List<McuKdcBridge>
     */
    private List<McuKdcBridge> getMcuKdcBridgesByStatus(boolean isAvailable)
    {
        List<McuKdcBridge> mcuKdcBridgeList = new ArrayList<>();
        Collection<McuKdcBridge> fhis = mcuKdcBridgeMap.values();
        for (Iterator<McuKdcBridge> iterator = fhis.iterator(); iterator.hasNext();)
        {
            McuKdcBridge mcuKdcBridge = iterator.next();
            if (mcuKdcBridge.isAvailable() == isAvailable)
            {
                mcuKdcBridgeList.add(mcuKdcBridge);
            }
        }
        return mcuKdcBridgeList;
    }
    
//    /**
//     * 添加可用的fme桥
//     * @author lilinhai
//     * @since 2021-03-19 16:00
//     * @param fmeBridgeCollection
//     * @param clusterId
//     * @param deptId void
//     */
//    private void addAvailableMcuKdcBridge(McuKdcBridgeCollection fmeBridgeCollection, long clusterId)
//    {
//        // 根据集群ID获取集群下可用的fme集合
//        McuKdcBridgeCluster fmeBridgeCluster = getByFmeClusterId(clusterId);
//        if (fmeBridgeCluster == null)
//        {
//            return;
//        }
//
//        List<McuKdcBridge> availableMcuKdcBridges0 = fmeBridgeCluster.getAvailableMcuKdcBridges();
//        if (!ObjectUtils.isEmpty(availableMcuKdcBridges0))
//        {
//            fmeBridgeCollection.setMasterMcuKdcBridge(fmeBridgeCluster.getMasterMcuKdcBridge());
//            for (McuKdcBridge fmeBridge : availableMcuKdcBridges0)
//            {
//                fmeBridgeCollection.addMcuKdcBridge(fmeBridge);
//            }
//        }
//
//        if (ObjectUtils.isEmpty(fmeBridgeCollection.getMcuKdcBridges()))
//        {
//            if (fmeBridgeCluster.getSpareFmeType() != null)
//            {
//                if (fmeBridgeCluster.getSpareFmeType() == FmeType.CLUSTER)
//                {
//                    this.addAvailableMcuKdcBridge(fmeBridgeCollection, fmeBridgeCluster.getSpareFmeId());
//                }
//                else
//                {
//                    McuKdcBridge fmeBridge = getSpareMcuKdcBridge(fmeBridgeCluster.getSpareFmeId());
//                    if (fmeBridge != null)
//                    {
//                        fmeBridgeCollection.addMcuKdcBridge(fmeBridge);
//                    }
//                }
//            }
//        }
//    }
    
    private List<McuKdcBridge> valid(List<McuKdcBridge> mbs)
    {
        for (McuKdcBridge fb : mbs)
        {
            if (fb.isInitializing())
            {
                throw new SystemException(1009485, "MCU" + (mbs.size() > 1 ? "集群" : "单") + "【" + fb.getBusiMcuKdc().getIp() + "】节点初始化中，即将就绪，请稍后...");
            }
        }
        return mbs;
    }
    
    public static McuKdcBridgeCache getInstance()
    {
        return INSTANCE;
    }
    
}
