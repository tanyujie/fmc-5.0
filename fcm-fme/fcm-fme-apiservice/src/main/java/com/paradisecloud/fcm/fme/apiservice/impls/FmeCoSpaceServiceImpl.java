/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : McuNodeDataSyncServiceImpl.java
 * Package : com.paradisecloud.service.impls
 * 
 * @author lilinhai
 * 
 * @since 2020-12-07 13:38
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.apiservice.impls;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.paradisecloud.fcm.fme.model.response.cospace.ActiveCoSpacesResponse;
import com.paradisecloud.fcm.fme.model.response.cospace.CoSpaceInfoResponse;
import com.paradisecloud.fcm.fme.model.response.cospace.CoSpacesResponse;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.ThreadUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>MCU节点数据同步服务实现类</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2020-12-07 13:38
 */
@Service
public class FmeCoSpaceServiceImpl implements ICoSpaceService {

    @Autowired
    private IBusiCallLegProfileService callLegProfileService;

    private ReentrantLock lock = new ReentrantLock();

    /**
     * <pre>同步MCU集群端的会议室数据</pre>
     *
     * @param fmeBridge
     * @author lilinhai
     * @see com.paradisecloud.sync.service.interfaces.IMcuNodeDataSyncService#syncCoSpaces(com.paradisecloud.fcm.fme.cache.model.fmeinvoker.FmeBridge.FmeHttpInvoker.BridgeApiUtil)
     * @since 2020-12-08 10:03
     */
    @Override
    public void syncCoSpaces(FmeBridge fmeBridge)
    {
        try
        {
            int offset = 0;
            AtomicInteger totalCount = new AtomicInteger();
            while (true)
            {
                CoSpacesResponse spacesResponse = fmeBridge.getCoSpaceInvoker().getCoSpaces(offset);
                if (spacesResponse != null)
                {
                    ActiveCoSpacesResponse activeCoSpacesResponse = spacesResponse.getCoSpaces();
                    List<CoSpace> coSpaces = activeCoSpacesResponse.getCoSpace();
                    if (coSpaces != null)
                    {
                        // 业务处理
                        doCoSpaceService(coSpaces, fmeBridge);
                        Integer total = activeCoSpacesResponse.getTotal();
                        totalCount.addAndGet(coSpaces.size());
                        if (totalCount.get() < total.intValue())
                        {
                            offset = totalCount.get();
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
            }

            fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace data sync complete: " + fmeBridge.getDataCache().getCoSpaceCount(), true);
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace sync error Top", true, e);
        }
    }

    private void doCoSpaceService(List<CoSpace> coSpaces, FmeBridge fmeBridge)
    {
        for (CoSpace coSpace : coSpaces)
        {
            updateCoSpaceCache(fmeBridge, coSpace.getId());
        }
    }
    
    public void updateCoSpaceCache(FmeBridge fmeBridge, String coSpaceId)
    {
        CoSpace coSpace = null;
        try
        {
            CoSpaceInfoResponse coSpaceInfoResponse = fmeBridge.getCoSpaceInvoker().getCoSpaceInfo(coSpaceId);
            if (coSpaceInfoResponse != null && coSpaceInfoResponse.getCoSpace() != null)
            {
                coSpace = coSpaceInfoResponse.getCoSpace();
                if (!ObjectUtils.isEmpty(coSpace.getUri()))
                {
                    fmeBridge.getDataCache().update(coSpaceInfoResponse.getCoSpace());
                    fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
                    {
                        public void process(FmeBridge fmeBridge)
                        {
                            fmeBridge.getDataCache().update(coSpaceInfoResponse.getCoSpace());
                        }
                    });
                    fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace data sync: " + coSpace, true);
                }
                else
                {
                    fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace data sync, 发现会议号为空的coSpace记录: " + coSpace, true);
                }
            }
            else
            {
                fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace not found: " + coSpaceId, true);
            }
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace sync error: " + coSpaceId, true, e);
        }
    }

    /**
     * 根据会议号获取coSpace，没有则自动创建
     *
     * @param conferenceNumber
     * @return
     * @author lilinhai
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.ICoSpaceService#getCoSpaceByConferenceNumber(com.paradisecloud.fcm.fme.cache.model.FmeBridge, java.lang.String)
     * @since 2021-03-15 16:24
     */
    @Override
    public CoSpace getCoSpaceByConferenceNumber(Long deptId, String conferenceNumber) {
        FmeBridge fmeBridge = BridgeUtils.getFmeBridgeByDeptIdAndConferenceNumber(deptId, conferenceNumber, false);
        return getCoSpaceByConferenceNumber(fmeBridge, conferenceNumber);
    }

    @Override
    public CoSpace getCoSpaceByConferenceNumber(FmeBridge fmeBridge, String conferenceNumber) {

        lock.lock();
        try {
            CoSpace coSpace = null;

            // 根据会议号获取coSpace，没有则自动创建
            int i = 0;
            while ((i++) < 3) {
                try {
                    coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceNumber);
                    if (coSpace != null) {
                        return coSpace;
                    }
                    String coSpaceId = fmeBridge.getCoSpaceInvoker().createCoSpace(new CoSpaceParamBuilder().conferenceNumber(conferenceNumber).build());
                    CoSpaceInfoResponse coSpaceInfoResponse = fmeBridge.getCoSpaceInvoker().getCoSpaceInfo(coSpaceId);
                    if (coSpaceInfoResponse != null && coSpaceInfoResponse.getCoSpace() != null) {
                        coSpace = coSpaceInfoResponse.getCoSpace();
                        if (!ObjectUtils.isEmpty(coSpaceInfoResponse.getCoSpace().getUri())) {
                            fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
                                public void process(FmeBridge fmeBridge) {
                                    fmeBridge.getDataCache().update(coSpaceInfoResponse.getCoSpace());
                                    fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace data sync: " + coSpaceInfoResponse.getCoSpace(), true);
                                }
                            });
                            return coSpace;
                        } else {
                            fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace data sync, 发现会议号为空的coSpace记录: " + coSpaceInfoResponse.getCoSpace(), true);
                        }
                    }
                } catch (Throwable e) {
                    try {
                        ThreadUtils.sleep(100);
                        fmeBridge.getFmeLogger().logWebsocketInfo("createCoSpace error, begin sync all cospaces: " + conferenceNumber, true, e);
                        fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
                            public void process(FmeBridge fmeBridge) {
                                syncCoSpaces(fmeBridge);
                            }
                        });
                    } catch (Exception ex) {
                    }
                }
            }

            return coSpace;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 修改入会方案为默认的
     * @author lilinhai
     * @since 2021-03-15 17:34 
     * @param fmeBridge
     * @param deptId
     * @param coSpace
     * @param callLegProfileId void
     */
    public void updateCoSpaceCallLegProfile(FmeBridge fmeBridge, Long deptId, CoSpace coSpace)
    {
        String callLegProfileId = callLegProfileService.createDefaultCalllegProfile(fmeBridge, deptId);
        
        // 修改入会方案
        updateCoSpaceCallLegProfile(fmeBridge, coSpace, callLegProfileId);
    }
    
    /**
     * 修改入会方案
     * @author lilinhai
     * @since 2021-03-15 17:34 
     * @param fmeBridge
     * @param coSpace
     * @param callLegProfileId void
     */
    public void updateCoSpaceCallLegProfile(FmeBridge fmeBridge, CoSpace coSpace, String callLegProfileId)
    {
        // 检测入会方案是否绑定，没有绑定，则绑定默认方案
        if (ObjectUtils.isEmpty(coSpace.getCallLegProfile()) || !coSpace.getCallLegProfile().equals(callLegProfileId))
        {
            updateCoSpace(fmeBridge, coSpace, new CoSpaceParamBuilder().callLegProfile(callLegProfileId));
        }
    }
    
    /**
     * 修改入会方案
     * @author lilinhai
     * @since 2021-03-15 17:34 
     * @param fmeBridge
     * @param coSpace
     * @param callLegProfileId void
     */
    public void updateCoSpace(FmeBridge fmeBridge, CoSpace coSpace, CoSpaceParamBuilder coSpaceParamBuilder)
    {
        lock.lock();
        try {
            if (coSpace != null && fmeBridge != null && coSpaceParamBuilder != null)
            {
                RestResponse rr = fmeBridge.getCoSpaceInvoker().updateCoSpace(coSpace.getId(), coSpaceParamBuilder.build());
                if (!rr.isSuccess())
                {
                    if (rr.getMessage().contains("coSpaceDoesNotExist"))
                    {
                        // 删除内存coSpace
                        recoveryCospace(fmeBridge, coSpace.getUri());
                        coSpace = getCoSpaceByConferenceNumber(fmeBridge, coSpace.getUri());
                        updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
                    }
                    else
                    {
                        throw new SystemException(1008435, rr.getMessage());
                    }
                }
                CoSpaceInfoResponse coSpaceInfoResponse = fmeBridge.getCoSpaceInvoker().getCoSpaceInfo(coSpace.getId());
                if (coSpaceInfoResponse != null && coSpaceInfoResponse.getCoSpace() != null)
                {
                    fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
                    {
                        public void process(FmeBridge fmeBridge)
                        {
                            fmeBridge.getDataCache().update(coSpaceInfoResponse.getCoSpace());
                        }
                    });
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void recoveryCospace(Long deptId, String conferenceNumber)
    {
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceNumber(deptId, conferenceNumber, false);
        recoveryCospace(fmeBridge, conferenceNumber);
    }
    
    public void recoveryCospace(FmeBridge fmeBridge, String conferenceNumber)
    {
        if (fmeBridge == null)
        {
            return;
        }
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceNumber);
        if (coSpace != null)
        {
            try
            {
                fmeBridge.getCoSpaceInvoker().deleteCoSpace(coSpace.getId());
            }
            catch (Exception e)
            {
                LoggerFactory.getLogger(getClass()).error("recoveryCospace fail: ", e);
            }
            
            fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().deleteCoSpace(coSpace.getId());
                }
            });
        }
    }
    
}
