package com.paradisecloud.fcm.web.task;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiOperationLogMapper;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.vo.OperationLogSearchVo;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class OperationLogDealScheduler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Scheduled(cron = "0 1 1 * * ?")
    public void run() {
        logger.info("监控操作日志记录启动成功！");
        Date date = new Date();
        int yearNew = Integer.valueOf(DateUtil.convertDateToString(date, "yyyy"));
        BusiOperationLogMapper busiOperationLogMapper = BeanFactory.getBean(BusiOperationLogMapper.class);
        try {
            OperationLogSearchVo busiOperationLog = new OperationLogSearchVo();
            busiOperationLog.setYear(String.valueOf(yearNew - 1));
            List<BusiOperationLog> busiOperationLogs = busiOperationLogMapper.selectBusiOperationLogListHistory(busiOperationLog);
            int size = busiOperationLogs.size();
            logger.info("busi_operation_log          ====》》" + size);
        } catch (Throwable e) {
            try {
                BusiOperationLog busiOperationLog = busiOperationLogMapper.selectBusiOperationLogFirst();
                if (busiOperationLog != null) {
                    Date time = busiOperationLog.getTime();
                    int year = Integer.valueOf(DateUtil.convertDateToString(time, "yyyy"));
                    if (year != yearNew) {
                        String tableName = "busi_operation_log";
                        busiOperationLogMapper.copyTable(tableName + "_" + year);
                        OperationLogSearchVo busiOperationLogTemp = new OperationLogSearchVo();
                        busiOperationLogTemp.setYear(String.valueOf(yearNew - 1));
                        List<BusiOperationLog> busiOperationLogs = busiOperationLogMapper.selectBusiOperationLogListHistory(busiOperationLogTemp);
                        int size = busiOperationLogs.size();
                        busiOperationLogMapper.clearTable(tableName);
                        logger.info(tableName + "_" + year + " ====》》" + size);
                    }
                }
            } catch (Exception e1){
                logger.error("监控操作日志记录出错！", e1);
            }
            logger.error("监控操作日志记录", e);
        } finally {
            logger.info("监控操作日志记录启动完成！");
        }
    }
}
