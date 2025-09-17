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
package com.paradisecloud.fcm.mcu.zj.cache;

import com.paradisecloud.fcm.common.enumer.McuZjType;
import com.paradisecloud.fcm.dao.model.BusiMcuZjDept;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridgeCollection;
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
public abstract class McuZjBridgeCache
{
    
    private static final McuZjBridgeCache INSTANCE = new McuZjBridgeCache()
    {
    };
    
    /**
     * 正常可用的，属于启用类别
     */
    private Map<String, McuZjBridge> ipToMcuZjBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * 所有fme调用器缓存，key：会议桥的id
     */
    private Map<Long, McuZjBridge> mcuXdBridgeMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-16 12:19
     */
    private McuZjBridgeCache()
    {
    }
    
    /**
     * <pre>更新到全集缓存中</pre>
     * @author lilinhai
     * @since 2020-12-29 17:44 
     * @param mcuZjBridge void
     */
    public synchronized void update(McuZjBridge mcuZjBridge)
    {
        
        // 添加ID映射
        mcuXdBridgeMap.put(mcuZjBridge.getBusiMcuZj().getId(), mcuZjBridge);
        
        // 添加IP映射
        ipToMcuZjBridgeMap.put(mcuZjBridge.getBridgeAddress(), mcuZjBridge);
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean containsKey(Long id) {
        return mcuXdBridgeMap.containsKey(id);
    }
    
    /**
     * <pre>根据IP和端口号会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29 
     * @param bridgeAddress IP:PORT
     * @return List<FmeHttpInvoker>
     */
    public McuZjBridge getMcuZjBridgeByBridgeAddress(String bridgeAddress)
    {
        return ipToMcuZjBridgeMap.get(bridgeAddress);
    }

    /**
     * <pre>根据IP获取会议桥</pre>
     * @author lilinhai
     * @since 2020-12-30 11:29
     * @param ipAddress
     * @return List<FmeHttpInvoker>
     */
    public McuZjBridge getMcuZjBridgeByBridgeAddressOnly(String ipAddress)
    {
        for (McuZjBridge fmeBridge : ipToMcuZjBridgeMap.values()) {
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
     * @param busiMcuZjId
     * @return FmeHttpInvoker
     */
    public McuZjBridge get(long busiMcuZjId)
    {
        return mcuXdBridgeMap.get(busiMcuZjId);
    }
    
    /**
     * 删除fme桥
     * @author lilinhai
     * @since 2021-03-19 15:59 
     * @param mcuZjBridge void
     */
    public void delete(McuZjBridge mcuZjBridge)
    {
        if (mcuZjBridge != null)
        {
            mcuZjBridge.setDeleted(true);
            
            // 销毁会议桥对象
            mcuZjBridge.destroy();
            McuZjBridge removed = mcuXdBridgeMap.remove(mcuZjBridge.getBusiMcuZj().getId());
            if (removed != null)
            {
                removed.getMcuZjLogger().logInfo("从invokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除");
            }
            
            removed = ipToMcuZjBridgeMap.remove(mcuZjBridge.getBridgeAddress());
            if (removed != null)
            {
                removed.getMcuZjLogger().logInfo("从ipToInvokerMap缓存移除本会议桥成功, 移除原因，会议桥被删除!");
            }
        }
    }
    
    /**
     * <pre>获取所有的MCU桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<McuZjBridge>
     */
    public List<McuZjBridge> getMcuZjBridges()
    {
        return new ArrayList<>(mcuXdBridgeMap.values());
    }
    
    public List<McuZjBridge> getMcuZjBridgesByDept(long deptId)
    {
        BusiMcuZjDept busiMcuZjDept = DeptMcuZjMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuZjDept != null)
        {
            if (busiMcuZjDept.getMcuType().intValue() == McuZjType.SINGLE_NODE.getValue())
            {
                McuZjBridge fmeBridge = mcuXdBridgeMap.get(busiMcuZjDept.getMcuId());
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
    public McuZjBridgeCollection getAvailableMcuZjBridgesByDept(long deptId)
    {
        McuZjBridgeCollection mcuXdBridgeCollection = new McuZjBridgeCollection();
        BusiMcuZjDept busiMcuZjDept = DeptMcuZjMappingCache.getInstance().getBindMcu(deptId);
        if (busiMcuZjDept == null)
        {
            return null;
        }
        
        // 如果是单节点
        if (busiMcuZjDept.getMcuType().intValue() == McuZjType.SINGLE_NODE.getValue())
        {
            McuZjBridge mcuXdBridge = mcuXdBridgeMap.get(busiMcuZjDept.getMcuId());
            
            // 可用则直接添加，不可用则转备用
            if (mcuXdBridge.isAvailable())
            {
                mcuXdBridgeCollection.addMcuZjBridge(mcuXdBridge);
                mcuXdBridgeCollection.setMasterMcuZjBridge(mcuXdBridge);
            }
        }
        
        return ObjectUtils.isEmpty(mcuXdBridgeCollection.getMcuZjBridges()) ? null : mcuXdBridgeCollection;
    }
    
    /**
     * <pre>获取可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:27 
     * @return List<BridgeApiUtil>
     */
    public List<McuZjBridge> getAvailableMcuZjBridges()
    {
        return getMcuZjBridgesByStatus(true);
    }
    
    /**
     * <pre>获取不可用的会议桥列表</pre>
     * @author lilinhai
     * @since 2020-12-16 12:26 
     * @return List<BridgeApiUtil>
     */
    public List<McuZjBridge> getUnAvailableMcuZjBridges()
    {
        return getMcuZjBridgesByStatus(false);
    }
    
//    /**
//     * 从当前部门名下的所有fme中根据callId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param callId
//     * @return McuZjBridge
//     */
//    public McuZjBridge getMcuZjBridgeByConferenceContext(ConferenceContext conferenceContext)
//    {
//        return getMcuZjBridgeByConferenceNumber(conferenceContext.getDeptId(), conferenceContext.getConferenceNumber(), true);
//    }
    
//    /**
//     * 从当前部门名下的所有fme中根据callId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param callId
//     * @return McuZjBridge
//     */
//    public McuZjBridge getMcuZjBridgeByCallId(long deptId, String callId)
//    {
//        GenericValue<McuZjBridge> genericValue = new GenericValue<>();
//        doBreakMcuZjBridgeBusiness(deptId, new McuZjBridgeAddpterProcessor()
//        {
//            @Override
//            public void process(McuZjBridge fmeBridge)
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
//     * @return McuZjBridge
//     */
//    public McuZjBridge getMcuZjBridgeByCoSpaceId(long deptId, String coSpaceId)
//    {
//        return getMcuZjBridgeByCoSpaceId(deptId, coSpaceId, true);
//    }
//
//    /**
//     * 从当前部门名下的所有fme中根据coSpaceId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param coSpaceId
//     * @return McuZjBridge
//     */
//    public McuZjBridge getMcuZjBridgeByCoSpaceId(long deptId, String coSpaceId, boolean throwError)
//    {
//        GenericValue<McuZjBridge> genericValue = new GenericValue<>();
//        doBreakMcuZjBridgeBusiness(deptId, new McuZjBridgeAddpterProcessor()
//        {
//            @Override
//            public void process(McuZjBridge fmeBridge)
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
//     * @return McuZjBridge
//     */
//    public McuZjBridge getMcuZjBridgeByConferenceNumber(long deptId, String conferenceNumber, boolean throwError)
//    {
//        GenericValue<McuZjBridge> genericValue = new GenericValue<>();
//        doBreakMcuZjBridgeBusiness(deptId, new McuZjBridgeAddpterProcessor()
//        {
//            public void process(McuZjBridge fmeBridge)
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
//     * @return McuZjBridge
//     */
//    public McuZjBridge getCascadeMcuZjBridgeByFmeAttendee(FmeAttendee attendee, boolean throwError)
//    {
//        return getMcuZjBridgeByConferenceNumber(attendee.getCascadeDeptId(), attendee.getCascadeConferenceNumber(), throwError);
//    }
//
//    /**
//     * 根据已入会的参会者获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param deptId
//     * @param conferenceNumber
//     * @return McuZjBridge
//     */
//    public McuZjBridge getMcuZjBridgeByMeetingJoinedAttendee(Attendee attendee)
//    {
//        return getMcuZjBridgeByCallId(attendee.getDeptId(), attendee.getCallId());
//    }
//
//    /**
//     * 从所有fme中根据callId获取fme桥
//     * @author lilinhai
//     * @since 2021-02-07 17:09
//     * @param callId
//     * @return McuZjBridge
//     */
//    public McuZjBridge getMcuZjBridgeByCallId(String callId)
//    {
//        List<McuZjBridge> fbs = getAvailableMcuZjBridges();
//        if (!ObjectUtils.isEmpty(fbs))
//        {
//            for (McuZjBridge fmeBridge : fbs)
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
//    public void doBreakMcuZjBridgeBusiness(long deptId, McuZjBridgeProcessor fmeBridgeProcessor)
//    {
//        doBreakMcuZjBridgeBusiness(deptId, fmeBridgeProcessor, true);
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
//    public void doBreakMcuZjBridgeBusiness(long deptId, McuZjBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        doMcuZjBridgeBusiness(deptId, McuZjBridgeProcessingStrategy.BREAK, fmeBridgeProcessor, throwError);
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
//    public void doTraverseMcuZjBridgeBusiness(long deptId, McuZjBridgeProcessor fmeBridgeProcessor)
//    {
//        doTraverseMcuZjBridgeBusiness(deptId, fmeBridgeProcessor, true);
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
//    public void doTraverseMcuZjBridgeBusiness(long deptId, McuZjBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        doMcuZjBridgeBusiness(deptId, McuZjBridgeProcessingStrategy.TRAVERSE, fmeBridgeProcessor, throwError);
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
//    public void doRandomMcuZjBridgeBusiness(long deptId, McuZjBridgeProcessor fmeBridgeProcessor)
//    {
//        doRandomMcuZjBridgeBusiness(deptId, fmeBridgeProcessor, true);
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
//    public void doRandomMcuZjBridgeBusiness(long deptId, McuZjBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        doMcuZjBridgeBusiness(deptId, McuZjBridgeProcessingStrategy.RANDOM, fmeBridgeProcessor, throwError);
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
//    private void doMcuZjBridgeBusiness(long deptId, McuZjBridgeProcessingStrategy fmeBridgeProcessingStrategy, McuZjBridgeProcessor fmeBridgeProcessor, boolean throwError)
//    {
//        McuZjBridgeCollection fmeBridgeCollection = getAvailableMcuZjBridgesByDept(deptId);
//        if (fmeBridgeCollection == null || ObjectUtils.isEmpty(fmeBridgeCollection.getMcuZjBridges()))
//        {
//            if (throwError)
//            {
//                String errInfo = "很抱歉，【" + SysDeptCache.getInstance().get(deptId).getDeptName() + "】当前无可用的MCU，请联系管理员配置您的MCU！";
//                LoggerFactory.getLogger(getClass()).error(errInfo);
//                throw new NoAvailableMcuZjBridgeException(1001002, errInfo);
//            }
//            return;
//        }
//
//        List<McuZjBridge> fbs = fmeBridgeCollection.getMcuZjBridges();
//        if (!ObjectUtils.isEmpty(fmeBridgeProcessor.excludeFmeIps()))
//        {
//            for (McuZjBridge fmeBridge : new ArrayList<>(fbs))
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
//                    throw new NoAvailableMcuZjBridgeException(1001002, errInfo);
//                }
//                return;
//            }
//        }
//
//        // 集群就优先选主节点（权重值最大的）来呼叫
//        if (fmeBridgeCollection.getMasterMcuZjBridge() != null)
//        {
//            fmeBridgeProcessingStrategy.process(fbs, fmeBridgeProcessor);
//        }
//        else
//        {
//            McuZjBridgeProcessingStrategy.BREAK.process(fbs, fmeBridgeProcessor);
//        }
//    }
    
    /**
     * 根据可用状态获取桥集合
     * @author lilinhai
     * @since 2021-03-22 17:38 
     * @param isAvailable
     * @return List<McuZjBridge>
     */
    private List<McuZjBridge> getMcuZjBridgesByStatus(boolean isAvailable)
    {
        List<McuZjBridge> mcuZjBridgeList = new ArrayList<>();
        Collection<McuZjBridge> fhis = mcuXdBridgeMap.values();
        for (Iterator<McuZjBridge> iterator = fhis.iterator(); iterator.hasNext();)
        {
            McuZjBridge mcuZjBridge = iterator.next();
            if (mcuZjBridge.isAvailable() == isAvailable)
            {
                mcuZjBridgeList.add(mcuZjBridge);
            }
        }
        return mcuZjBridgeList;
    }
    
//    /**
//     * 添加可用的fme桥
//     * @author lilinhai
//     * @since 2021-03-19 16:00
//     * @param fmeBridgeCollection
//     * @param clusterId
//     * @param deptId void
//     */
//    private void addAvailableMcuZjBridge(McuZjBridgeCollection fmeBridgeCollection, long clusterId)
//    {
//        // 根据集群ID获取集群下可用的fme集合
//        McuZjBridgeCluster fmeBridgeCluster = getByFmeClusterId(clusterId);
//        if (fmeBridgeCluster == null)
//        {
//            return;
//        }
//
//        List<McuZjBridge> availableMcuZjBridges0 = fmeBridgeCluster.getAvailableMcuZjBridges();
//        if (!ObjectUtils.isEmpty(availableMcuZjBridges0))
//        {
//            fmeBridgeCollection.setMasterMcuZjBridge(fmeBridgeCluster.getMasterMcuZjBridge());
//            for (McuZjBridge fmeBridge : availableMcuZjBridges0)
//            {
//                fmeBridgeCollection.addMcuZjBridge(fmeBridge);
//            }
//        }
//
//        if (ObjectUtils.isEmpty(fmeBridgeCollection.getMcuZjBridges()))
//        {
//            if (fmeBridgeCluster.getSpareFmeType() != null)
//            {
//                if (fmeBridgeCluster.getSpareFmeType() == FmeType.CLUSTER)
//                {
//                    this.addAvailableMcuZjBridge(fmeBridgeCollection, fmeBridgeCluster.getSpareFmeId());
//                }
//                else
//                {
//                    McuZjBridge fmeBridge = getSpareMcuZjBridge(fmeBridgeCluster.getSpareFmeId());
//                    if (fmeBridge != null)
//                    {
//                        fmeBridgeCollection.addMcuZjBridge(fmeBridge);
//                    }
//                }
//            }
//        }
//    }
    
    private List<McuZjBridge> valid(List<McuZjBridge> mbs)
    {
        for (McuZjBridge fb : mbs)
        {
            if (fb.isInitializing())
            {
                throw new SystemException(1009485, "MCU" + (mbs.size() > 1 ? "集群" : "单") + "【" + fb.getBusiMcuZj().getIp() + "】节点初始化中，即将就绪，请稍后...");
            }
        }
        return mbs;
    }
    
    public static McuZjBridgeCache getInstance()
    {
        return INSTANCE;
    }
    
}
