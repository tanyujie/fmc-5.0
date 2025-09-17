package com.paradisecloud.fcm.cdr.service.task;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.sinhy.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 历史数据定时任务类
 */
@Component
public class HistoryDateScheduleTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Resource
    private BusiHistoryCallMapper busiHistoryCallMapper;
    @Resource
    private BusiHistoryParticipantTerminalMapper busiHistoryParticipantTerminalMapper;
    @Resource
    private CdrCallMapper cdrCallMapper;
    @Resource
    private CdrCallLegStartMapper cdrCallLegStartMapper;
    @Resource
    private CdrCallLegEndMapper cdrCallLegEndMapper;
    @Resource
    private CdrCallLegEndAlarmMapper cdrCallLegEndAlarmMapper;
    @Resource
    private CdrCallLegEndMediaInfoMapper cdrCallLegEndMediaInfoMapper;
    @Resource
    private CdrCallLegUpdateMapper cdrCallLegUpdateMapper;
    @Resource
    private CdrCallLegNumDateMapper cdrCallLegNumDateMapper;
    @Resource
    private CdrStreamingMapper cdrStreamingMapper;
    @Resource
    private CdrTaskResultMapper cdrTaskResultMapper;
    @Resource
    private CdrTerminalUsageMapper cdrTerminalUsageMapper;

    /**
     * 每天03:00启动删除一年以前的数据
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteHistoryData() {
        logger.info("删除一年以前数据定时任务启动");

        Date calcDate = DateUtils.getDiffDate(-365, TimeUnit.DAYS);
        Date beforeDate = DateUtil.clearTime(calcDate);
        logger.info("删除日期:" + DateUtil.convertDateToString(beforeDate, "yyyy-MM-dd HH:mm:ss"));
        int rows;
        // busi_history_conference
        rows = busiHistoryConferenceMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call:" + rows + "条");
        // busi_history_participant
        rows = busiHistoryParticipantMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call:" + rows + "条");
        // busi_history_call
        rows = busiHistoryCallMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call:" + rows + "条");
        // busi_history_participant_terminal
        rows = busiHistoryParticipantTerminalMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call:" + rows + "条");
        // cdr_call
        rows = cdrCallMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call:" + rows + "条");
        // cdr_call_leg_start
        rows = cdrCallLegStartMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call_leg_start：" + rows + "条");
        // cdr_call_leg_end
        rows = cdrCallLegEndMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call_leg_end:" + rows + "条");
        // cdr_call_leg_end_alarm
        rows = cdrCallLegEndAlarmMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call_leg_end_alarm:" + rows + "条");
        // cdr_call_leg_end_media_info
        rows = cdrCallLegEndMediaInfoMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call_leg_end_media_info:" + rows + "条");
        // cdr_call_leg_end_update
        rows = cdrCallLegUpdateMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call_leg_update:" + rows + "条");
        // cdr_call_leg_end_num_date
        rows = cdrCallLegNumDateMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_call_leg_num_date:" + rows + "条");
        // cdr_streaming
        rows = cdrStreamingMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_streaming:" + rows + "条");
        // cdr_task_result
        rows = cdrTaskResultMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_task_result:" + rows + "条");
        // cdr_terminal_usage
        rows = cdrTerminalUsageMapper.deleteHistory(beforeDate);
        logger.info("删除cdr_terminal_usage:" + rows + "条");

        logger.info("删除一年以前数据定时任务结束");
    }
}
