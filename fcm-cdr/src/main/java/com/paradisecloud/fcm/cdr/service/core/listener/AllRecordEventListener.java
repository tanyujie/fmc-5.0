package com.paradisecloud.fcm.cdr.service.core.listener;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrAllCallLegNumDateService;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrAllCallNumDateService;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrAllTaskResultService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryAllConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllConference;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @author johnson liu
 * @date 2021/5/27 13:40
 */
@Component
@Transactional
public class AllRecordEventListener
{
    
    private static Logger logger = LoggerFactory.getLogger(AllRecordEventListener.class);
    
    @Resource
    private ICdrAllCallLegNumDateService cdrAllCallLegNumDateService;

    @Resource
    private ICdrAllCallNumDateService cdrAllCallNumDateService;
    
    @Resource
    private BusiHistoryAllConferenceMapper historyAllConferenceMapper;
    
    @Resource
    private ICdrAllTaskResultService iCdrAllTaskResultService;
    
    /**
     * 处理新增数据的事件:该事件在发布者的事务提交成功后触发
     **/
    @TransactionalEventListener
    public void handleAddEvent(AllRecordEvent event)
    {
        logger.info("开始处理报表事件:{}", event);
        String fmeIp = event.getFmeIp();
        
        // 统计会议数量和会议时长
        String date = DateUtil.convertDateToString(LocalDate.now(), null);
        ReportSearchVo searchVo = new ReportSearchVo();
        searchVo.setStartTime(DateUtil.convertDateByString(DateUtil.fillDateString(date, false), null));
        searchVo.setEndTime(DateUtil.convertDateByString(DateUtil.fillDateString(date, true), null));
        List<BusiHistoryAllConference> historyConferenceList = historyAllConferenceMapper.selectBySearchVo(searchVo);
        synchronized (this)
        {
            iCdrAllTaskResultService.updateMeetingNumAndDuration(historyConferenceList);
        }
        
        // 统计FME的当天的会议数量
        synchronized (this)
        {
            cdrAllCallNumDateService.updateByEvent(fmeIp);
        }
        logger.info("报表处理成功");
    }
    
    /**
     * 处理新增数据的事件:该事件在发布者的事务提交成功后触发
     **/
    @TransactionalEventListener
    public void handleMediaAddEvent(AllCallLegEndMediaEvent event)
    {
        logger.info("开始处理Media报表事件:{}", event);
        String fmeIp = event.getFmeIp();
        
        // 更新参会者的通话质量
        synchronized (this)
        {
            iCdrAllTaskResultService.updateCallQuality();
        }
        
        // 统计 FME的参会者数量
        synchronized (this)
        {
            cdrAllCallLegNumDateService.updateByEvent(fmeIp);
        }
        logger.info("Media报表处理成功");
    }
}
