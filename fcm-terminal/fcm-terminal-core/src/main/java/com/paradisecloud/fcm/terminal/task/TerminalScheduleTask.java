package com.paradisecloud.fcm.terminal.task;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiSipAccountAutoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 终端定时任务类
 */
@Component
public class TerminalScheduleTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiSipAccountAutoMapper busiSipAccountAutoMapper;

    /**
     * 每天02:00启动删除今天以前的数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteSipAccountAuto() {
        logger.info("删除SIP账号自动生成今天以前数据定时任务启动");
        Date dateTime = DateUtil.clearTime(new Date());
        int rows = busiSipAccountAutoMapper.deleteBusiSipAccountAutoOfOld(dateTime);
        logger.info("删除条数:" + rows);
    }
}
