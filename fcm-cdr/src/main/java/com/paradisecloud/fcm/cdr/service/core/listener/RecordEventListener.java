package com.paradisecloud.fcm.cdr.service.core.listener;

import com.paradisecloud.fcm.cdr.service.core.cache.CdrCache;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegNumDateService;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallNumDateService;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrTaskResultService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.CdrCall;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.List;

/**
 * @author johnson liu
 * @date 2021/5/27 13:40
 */
@Component
@Transactional
public class RecordEventListener
{
    
    private static Logger logger = LoggerFactory.getLogger(RecordEventListener.class);
    
    @Autowired
    private ICdrCallLegNumDateService cdrCallLegNumDateService;
    
    @Autowired
    private ICdrCallNumDateService cdrCallNumDateService;
    
    @Autowired
    private BusiHistoryConferenceMapper historyConferenceMapper;
    
    @Autowired
    private ICdrTaskResultService iCdrTaskResultService;
    
    /**
     * 处理新增数据的事件:该事件在发布者的事务提交成功后触发
     **/
    @TransactionalEventListener
    public void handleAddEvent(RecordEvent event)
    {
        logger.info("开始处理报表事件:{}", event);
        CdrCall cdrCall = event.getCdrCall();
        Long deptId = event.getDeptId();
        
        // 统计会议数量和会议时长
        synchronized (this)
        {
            iCdrTaskResultService.updateMeetingNumAndDuration(deptId);
        }
        
        // 统计FME的当天的会议数量
        CdrCache.getInstance().removeCdrCallMap(cdrCall.getCdrId());
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByCallId(deptId, cdrCall.getCdrId());
        if (fmeBridge == null)
        {
            logger.info("报表处理结束");
            return;
        }
        String fmeIp = fmeBridge.getBusiFme().getIp();
        synchronized (this)
        {
            cdrCallNumDateService.updateByEvent(deptId, fmeIp);
        }
        logger.info("报表处理成功");
    }
    
    /**
     * 处理新增数据的事件:该事件在发布者的事务提交成功后触发
     **/
    @TransactionalEventListener
    public void handleMediaAddEvent(CallLegEndMediaEvent event)
    {
        logger.info("开始处理Media报表事件:{}", event);
        Long deptId = event.getDeptId();
        String callId = event.getCallId();
        
        // 更新参会者的通话质量
        synchronized (this)
        {
            iCdrTaskResultService.updateCallQuality(deptId);
        }
        
        // 统计 FME的参会者数量
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByCallId(deptId, callId);
        if (fmeBridge == null)
        {
            logger.info("Media报表处理结束");
            return;
        }
        String fmeIp = fmeBridge.getBusiFme().getIp();
        synchronized (this)
        {
            cdrCallLegNumDateService.updateByEvent(deptId, fmeIp);
        }
        logger.info("Media报表处理成功");
    }
}
