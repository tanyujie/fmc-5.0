/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallServiceImpl.java
 * Package     : com.paradisecloud.sync.service.impls
 * @author lilinhai 
 * @since 2020-12-09 10:15
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.impls;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.response.call.ActiveCallsResponse;
import com.paradisecloud.fcm.fme.model.response.call.CallInfoResponse;
import com.paradisecloud.fcm.fme.model.response.call.CallsResponse;

/**  
 * <pre>处理call的业务接口</pre>
 * @author lilinhai
 * @since 2020-12-09 10:15
 * @version V1.0  
 */
@Service
public class FmeCallServiceImpl implements ICallService
{
  
    @Autowired
    private ICoSpaceService fmeCoSpaceSyncService;
    
    /**
     * 创建Call
     * @author lilinhai
     * @since 2021-03-17 14:11 
     * @param deptId
     * @param conferenceNumber
     * @param conferenceName
     * @return
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.ICallService#createCall(com.paradisecloud.fcm.fme.cache.model.FmeBridge, java.lang.Long, java.lang.String, java.lang.String)
     */
    public Call createCall(Long deptId, String conferenceNumber, String conferenceName)
    {
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceNumber(deptId, conferenceNumber, false);
        return createCall(fmeBridge, conferenceNumber, conferenceName);
    }
    
    /**
     * 创建Call
     * @author lilinhai
     * @since 2021-03-22 15:41 
     * @param fmeBridge
     * @param deptId
     * @param conferenceNumber
     * @param conferenceName
     * @return Call
     */
    public Call createCall(FmeBridge fmeBridge, String conferenceNumber, String conferenceName)
    {
        // 创建call，先从缓存看看call是否存在，存在则不用创建
        CoSpace coSpace = fmeCoSpaceSyncService.getCoSpaceByConferenceNumber(fmeBridge, conferenceNumber);
        
        // 获取call，不存在则创建
        Call call = fmeBridge.getDataCache().getCallByCoSpaceUuid(coSpace.getId());
        
        // 如果call存在
        if (call != null)
        {
            return call;
        }
        else
        {
            String callId = fmeBridge.getCallInvoker().createCall(coSpace.getId(), conferenceName);
            CallInfoResponse callInfoResponse = fmeBridge.getCallInvoker().getCallInfo(callId);
            if (callInfoResponse != null && callInfoResponse.getCall() != null)
            {
                call = callInfoResponse.getCall();
                fmeBridge.getDataCache().update(call);
            }
        }
        return call;
    }
    
    /**
     * <pre>同步MCU集群端的活跃会议室映射数据</pre>
     * 
     * @author lilinhai
     * @since 2020-12-08 10:03
     * @param fmeBridge
     * @see com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService.interfaces.IFmeCoSpaceSyncService#syncCoSpaces(com.paradisecloud.fcm.fme.cache.model.fmeinvoker.FmeBridge.FmeHttpInvoker.BridgeApiUtil)
     */
    public void syncCall(FmeBridge fmeBridge, CallConsumptionProcessor callConsumptionProcessor)
    {
        try
        {
            int offset = 0;
            AtomicInteger totalCount = new AtomicInteger();
            while (true)
            {
                CallsResponse callsResponse = fmeBridge.getCallInvoker().getCalls(offset);
                if (callsResponse != null)
                {
                    ActiveCallsResponse activeCallsResponse = callsResponse.getCalls();
                    if (activeCallsResponse != null)
                    {
                        List<Call> calls = activeCallsResponse.getCall();
                        if (calls != null)
                        {
                            // 业务处理
                            doCallService(calls, fmeBridge, callConsumptionProcessor);
                            Integer total = activeCallsResponse.getTotal();
                            totalCount.addAndGet(calls.size());
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
                else
                {
                    break;
                }
            }
            
            fmeBridge.getFmeLogger().logWebsocketInfo("Call data sync complete: " + fmeBridge.getDataCache().getCallCount(), true);
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("Call data sync error", true, e);
        }
    }
    
    /**
     * <pre>同步单个call</pre>
     * @author lilinhai
     * @since 2021-02-02 12:02 
     * @param fmeBridge
     * @param call void
     */
    public void syncCall(FmeBridge fmeBridge, String call)
    {
        // 添加call-coSpace ID映射
        CallInfoResponse callInfoResponse = fmeBridge.getCallInvoker().getCallInfo(call);
        if (callInfoResponse != null && callInfoResponse.getCall() != null)
        {
            fmeBridge.getDataCache().update(callInfoResponse.getCall());
            fmeBridge.getFmeLogger().logWebsocketInfo("Call Update：" + callInfoResponse.getCall().getId(), true);
        }
        else
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("Call not found in FME：" + call + ", because the call has been deleted!", true);
        }
    }
    
    private void doCallService(List<Call> calls, FmeBridge fmeBridge, CallConsumptionProcessor callConsumptionProcessor)
    {
        for (Call call : calls)
        {
            try
            {
                callConsumptionProcessor.process(fmeBridge, call);
            }
            catch (Throwable e)
            {
                fmeBridge.getFmeLogger().logWebsocketInfo("syncCall error:", true, e);
            }
        }
    }
    
}
