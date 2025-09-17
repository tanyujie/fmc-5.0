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
package com.paradisecloud.fcm.mcu.plc.cache;

import com.paradisecloud.fcm.common.enumer.McuPlcType;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcDept;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridgeCollection;
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
public abstract class McuPlcBridgeCache
{
    
    private static final McuPlcBridgeCache INSTANCE = new McuPlcBridgeCache()
    {
    };
    
    /**
     * 正常可用的，属于启用类别
     */
    private Map<String, McuPlcBridge> ipToMcuPlcBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 所有fme调用器缓存，key：会议桥的id
     */
    private Map<Long, McuPlcBridge> mcuPlcBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-16 12:19
     */
    private McuPlcBridgeCache()
    {
    }
    
    /**
     * <pre>更新到全集缓存中</pre>
     * @author lilinhai
     * @since 2020-12-29 17:44 
     * @param mcuPlcBridge void
     */
    public synchronized void update(McuPlcBridge mcuPlcBridge)
    {
        
        // 添加ID映射
        mcuPlcBridgeMap.put(mcuPlcBridge.getBusiMcuPlc().getId(), mcuPlcBridge);
        
        // 添加IP映射
        ipToMcuPlcBridgeMap.put(mcuPlcBridge.getBridgeAddress(), mcuPlcBridge);
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean containsKey(Long id) {
        return mcuPlcBridgeMap.containsKey(id);
    }
    
    /**
     * <pre>根据IP和端口号会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29 
     * @param bridgeAddress IP:PORT
     * @return List<FmeHttpInvoker>
     */
    public McuPlcBridge getMcuPlcBridgeByBridgeAddress(String bridgeAddress)
    {
        return ipToMcuPlcBridgeMap.get(bridgeAddress);
    }

    /**
     * <pre>根据IP获取会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29
     * @param ipAddress
     * @return List<FmeHttpInvoker>
     */
    public McuPlcBridge getMcuPlcBridgeByBridgeAddressOnly(String ipAddress)
    {
        for (McuPlcBridge fmeBridge : ipToMcuPlcBridgeMap.values()) {
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
     * @param busiMcuPlcId
     * @return FmeHttpInvoker
     */
    public McuPlcBridge get(long busiMcuPlcId)
    {
        return mcuPlcBridgeMap.get(busiMcuPlcId);
    }
    
    /**
     * 删除fme桥
     * @author lilinhai
     * @since 2021-03-19 15:59 
     * @param mcuPlcBridge void
     */
    public void delete(McuPlcBridge mcuPlcBridge)
    {
        if (mcuPlcBridge != null)
        {
            mcuPlcBridge.setDeleted(true);
            
            // 销毁会议桥对象
            mcuPlcBridge.destroy();
            McuPlcBridge removed = mcuPlcBridgeMap.remove(mcuPlcBridge.getBusiMcuPlc().getId());
            if (removed != null)
            {
                removed.getMcuPlcLogger().logInfo("从invokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除");
            }
            
            removed = ipToMcuPlcBridgeMap.remove(mcuPlcBridge.getBridgeAddress());
            if (removed != null)
            {
                removed.getMcuPlcLogger().logInfo("从ipToInvokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除!");
            }
        }
    }
    
    /**
     * <pre>获取所有的MCU桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<McuPlcBridge>
     */
    public List<McuPlcBridge> getMcuPlcBridges()
    {
        return new ArrayList<>(mcuPlcBridgeMap.values());
    }
    
    public List<McuPlcBridge> getMcuPlcBridgesByDept(long deptId)
    {
        BusiMcuPlcDept busiMcuPlcDept = DeptMcuPlcMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuPlcDept != null)
        {
            if (busiMcuPlcDept.getMcuType().intValue() == McuPlcType.SINGLE_NODE.getValue())
            {
                McuPlcBridge fmeBridge = mcuPlcBridgeMap.get(busiMcuPlcDept.getMcuId());
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
    public McuPlcBridgeCollection getAvailableMcuPlcBridgesByDept(long deptId)
    {
        McuPlcBridgeCollection mcuXdBridgeCollection = new McuPlcBridgeCollection();
        BusiMcuPlcDept busiMcuPlcDept = DeptMcuPlcMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuPlcDept == null)
        {
            return null;
        }
        
        // 如果是单节点
        if (busiMcuPlcDept.getMcuType().intValue() == McuPlcType.SINGLE_NODE.getValue())
        {
            McuPlcBridge mcuXdBridge = mcuPlcBridgeMap.get(busiMcuPlcDept.getMcuId());
            
            // 可用则直接添加，不可用则转备用
            if (mcuXdBridge.isAvailable())
            {
                mcuXdBridgeCollection.addMcuPlcBridge(mcuXdBridge);
                mcuXdBridgeCollection.setMasterMcuPlcBridge(mcuXdBridge);
            }
        }
        
        return ObjectUtils.isEmpty(mcuXdBridgeCollection.getMcuPlcBridges()) ? null : mcuXdBridgeCollection;
    }
    
    /**
     * <pre>获取可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<BridgeApiUtil>
     */
    public List<McuPlcBridge> getAvailableMcuPlcBridges()
    {
        return getMcuPlcBridgesByStatus(true);
    }
    
    /**
     * <pre>获取不可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:26 
     * @return List<BridgeApiUtil>
     */
    public List<McuPlcBridge> getUnAvailableMcuPlcBridges()
    {
        return getMcuPlcBridgesByStatus(false);
    }
    
//    /**
//     * 从当前部门名下的所有fme中根据callId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param callId
//     * @return McuPlcBridge
//     */
//    public McuPlcBridge getMcuPlcBridgeByConferenceContext(ConferenceContext conferenceContext)
//    {
//        return getMcuPlcBridgeByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber(), true);
//    }
    
//    /**
//     * 从当前部门名下的所有fme中根据callId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param callId
//     * @return McuPlcBridge
//     */
//    public McuPlcBridge getMcuPlcBridgeByCallId(long deptId, String callId)
//    {
//        GenericValue<McuPlcBridge> genericValue = new GenericValue<>();
//        doBreakMcuPlcBridgeBusiness(deptId, new McuPlcBridgeAddpterProcessor()
//        {
//            @Override
//            public void process(McuPlcBridge fmeBridge)
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
//     * @return McuPlcBridge
//     */
//    public McuPlcBridge getMcuPlcBridgeByCoSpaceId(long deptId, String coSpaceId)
//    {
//        return getMcuPlcBridgeByCoSpaceId(deptId, coSpaceId, true);
//    }
//
//    /**
//     * 从当前部门名下的所有fme中根据coSpaceId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param coSpaceId
//     * @return McuPlcBridge
//     */
//    public McuPlcBridge getMcuPlcBridgeByCoSpaceId(long deptId, String coSpaceId, boolean throwError)
//    {
//        GenericValue<McuPlcBridge> genericValue = new GenericValue<>();
//        doBreakMcuPlcBridgeBusiness(deptId, new McuPlcBridgeAddpterProcessor()
//        {
//            @Override
//            public void process(McuPlcBridge fmeBridge)
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
//     * @return McuPlcBridge
//     */
//    public McuPlcBridge getMcuPlcBridgeByConferenceNumber(long deptId, String conferenceNumber, boolean throwError)
//    {
//        GenericValue<McuPlcBridge> genericValue = new GenericValue<>();
//        doBreakMcuPlcBridgeBusiness(deptId, new McuPlcBridgeAddpterProcessor()
//        {
//            public void process(McuPlcBridge fmeBridge)
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
//     * @return McuPlcBridge
//     */
//    public McuPlcBridge getCascadeMcuPlcBridgeByFmeAttendee(FmeAttendee attendee, boolean throwError)
//    {
//        return getMcuPlcBridgeByConferenceNumber(attendee.getCascadeDeptId(), attendee.getCascadeConferenceNumber(), throwError);
//    }
//
//    /**
//     * 根据已入会的参会者获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param conferenceNumber
//     * @return McuPlcBridge
//     */
//    public McuPlcBridge getMcuPlcBridgeByMeetingJoinedAttendee(Attendee attendee)
//    {
//        return getMcuPlcBridgeByCallId(attendee.getDeptId(), attendee.getCallId());
//    }
//
//    /**
//     * 从所有fme中根据callId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param callId
//     * @return McuPlcBridge
//     */
//    public McuPlcBridge getMcuPlcBridgeByCallId(String callId)
//    {
//        List<McuPlcBridge> fbs = getAvailableMcuPlcBridges();
//        if (!ObjectUtils.isEmpty(fbs))
//        {
//            for (McuPlcBridge fmeBridge : fbs)
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
//    public void doBreakMcuPlcBridgeBusiness(long deptId, McuPlcBridgeProcessor fmeBridgeProcessor)
//    {
//        doBreakMcuPlcBridgeBusiness(deptId, fmeBridgeProcessor, true);
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
//    public void doBreakMcuPlcBridgeBusiness(long deptId, McuPlcBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        doMcuPlcBridgeBusiness(deptId, McuPlcBridgeProcessingStrategy.BREAK, fmeBridgeProcessor, throwError);
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
//    public void doTraverseMcuPlcBridgeBusiness(long deptId, McuPlcBridgeProcessor fmeBridgeProcessor)
//    {
//        doTraverseMcuPlcBridgeBusiness(deptId, fmeBridgeProcessor, true);
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
//    public void doTraverseMcuPlcBridgeBusiness(long deptId, McuPlcBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        doMcuPlcBridgeBusiness(deptId, McuPlcBridgeProcessingStrategy.TRAVERSE, fmeBridgeProcessor, throwError);
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
//    public void doRandomMcuPlcBridgeBusiness(long deptId, McuPlcBridgeProcessor fmeBridgeProcessor)
//    {
//        doRandomMcuPlcBridgeBusiness(deptId, fmeBridgeProcessor, true);
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
//    public void doRandomMcuPlcBridgeBusiness(long deptId, McuPlcBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        doMcuPlcBridgeBusiness(deptId, McuPlcBridgeProcessingStrategy.RANDOM, fmeBridgeProcessor, throwError);
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
//    private void doMcuPlcBridgeBusiness(long deptId, McuPlcBridgeProcessingStrategy fmeBridgeProcessingStrategy, McuPlcBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        McuPlcBridgeCollection fmeBridgeCollection = getAvailableMcuPlcBridgesByDept(deptId);
//        if (fmeBridgeCollection == null || ObjectUtils.isEmpty(fmeBridgeCollection.getMcuPlcBridges()))
//        {
//            if (throwError)
//            {
//                String errInfo = "很抱歉，【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】当前无可用的MCU，请联系管理员配置您的MCU！";
//                LoggerFactory.getLogger(getClass()).error(errInfo);
//                throw new NoAvailableMcuPlcBridgeException(1001002, errInfo);
//            }
//            return;
//        }
//
//        List<McuPlcBridge> fbs = fmeBridgeCollection.getMcuPlcBridges();
//        if (!ObjectUtils.isEmpty(fmeBridgeProcessor.excludeFmeIps()))
//        {
//            for (McuPlcBridge fmeBridge : new ArrayList<>(fbs))
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
//                    throw new NoAvailableMcuPlcBridgeException(1001002, errInfo);
//                }
//                return;
//            }
//        }
//
//        // 集群就优先选主节点（权重值最大的）来呼叫
//        if (fmeBridgeCollection.getMasterMcuPlcBridge() != null)
//        {
//            fmeBridgeProcessingStrategy.process(fbs, fmeBridgeProcessor);
//        }
//        else
//        {
//            McuPlcBridgeProcessingStrategy.BREAK.process(fbs, fmeBridgeProcessor);
//        }
//    }
    
    /**
     * 根据可用状态获取桥集合
     * @author lilinhai
     * @since 2021-03-22 17:38 
     * @param isAvailable
     * @return List<McuPlcBridge>
     */
    private List<McuPlcBridge> getMcuPlcBridgesByStatus(boolean isAvailable)
    {
        List<McuPlcBridge> mcuPlcBridgeList = new ArrayList<>();
        Collection<McuPlcBridge> fhis = mcuPlcBridgeMap.values();
        for (Iterator<McuPlcBridge> iterator = fhis.iterator(); iterator.hasNext();)
        {
            McuPlcBridge mcuPlcBridge = iterator.next();
            if (mcuPlcBridge.isAvailable() == isAvailable)
            {
                mcuPlcBridgeList.add(mcuPlcBridge);
            }
        }
        return mcuPlcBridgeList;
    }
    
//    /**
//     * 添加可用的fme桥
//     * @author lilinhai
//     * @since 2021-03-19 16:00
//     * @param fmeBridgeCollection
//     * @param clusterId
//     * @param deptId void
//     */
//    private void addAvailableMcuPlcBridge(McuPlcBridgeCollection fmeBridgeCollection, long clusterId)
//    {
//        // 根据集群ID获取集群下可用的fme集合
//        McuPlcBridgeCluster fmeBridgeCluster = getByFmeClusterId(clusterId);
//        if (fmeBridgeCluster == null)
//        {
//            return;
//        }
//
//        List<McuPlcBridge> availableMcuPlcBridges0 = fmeBridgeCluster.getAvailableMcuPlcBridges();
//        if (!ObjectUtils.isEmpty(availableMcuPlcBridges0))
//        {
//            fmeBridgeCollection.setMasterMcuPlcBridge(fmeBridgeCluster.getMasterMcuPlcBridge());
//            for (McuPlcBridge fmeBridge : availableMcuPlcBridges0)
//            {
//                fmeBridgeCollection.addMcuPlcBridge(fmeBridge);
//            }
//        }
//
//        if (ObjectUtils.isEmpty(fmeBridgeCollection.getMcuPlcBridges()))
//        {
//            if (fmeBridgeCluster.getSpareFmeType() != null)
//            {
//                if (fmeBridgeCluster.getSpareFmeType() == FmeType.CLUSTER)
//                {
//                    this.addAvailableMcuPlcBridge(fmeBridgeCollection, fmeBridgeCluster.getSpareFmeId());
//                }
//                else
//                {
//                    McuPlcBridge fmeBridge = getSpareMcuPlcBridge(fmeBridgeCluster.getSpareFmeId());
//                    if (fmeBridge != null)
//                    {
//                        fmeBridgeCollection.addMcuPlcBridge(fmeBridge);
//                    }
//                }
//            }
//        }
//    }
    
    private List<McuPlcBridge> valid(List<McuPlcBridge> mbs)
    {
        for (McuPlcBridge fb : mbs)
        {
            if (fb.isInitializing())
            {
                throw new SystemException(1009485, "MCU" + (mbs.size() > 1 ? "集群" : "单") + "【" + fb.getBusiMcuPlc().getIp() + "】节点初始化中，即将就绪，请稍后...");
            }
        }
        return mbs;
    }
    
    public static McuPlcBridgeCache getInstance()
    {
        return INSTANCE;
    }
    
}
