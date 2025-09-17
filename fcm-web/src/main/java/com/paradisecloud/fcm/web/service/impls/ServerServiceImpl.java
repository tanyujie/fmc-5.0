package com.paradisecloud.fcm.web.service.impls;

import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.web.service.interfaces.IServerService;
import com.paradisecloud.fcm.web.task.SendServerInfoTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

@Service
public class ServerServiceImpl implements IServerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerServiceImpl.class);

    @Resource
    private TaskService taskService;

    @Override
    public String getServerTime() {
        String time = DateUtil.convertDateToString(new Timestamp(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");
        return time;
    }

    /**
     * 修改系统时间
     *
     * @param date 日期 yyyy-MM-dd
     * @param time 时间 HH:mm:ss
     */
    public boolean setServerTime(String date, String time) {
        String osName = System.getProperty("os.name");
        long oldTime = System.currentTimeMillis();
        try {
            // Window 系统
            if (osName.matches("^(?i)Windows.*$")) {
                String cmd;
                // 格式：yyyy-MM-dd
                cmd = " cmd /c date " + date;
                Runtime.getRuntime().exec(cmd);
                // 格式 HH:mm:ss
                cmd = " cmd /c time " + time;
                Runtime.getRuntime().exec(cmd);
            } else {
                // Linux 系统 格式：yyyy-MM-dd HH:mm:ss   date -s "2017-11-11 11:11:11"
                FileWriter excutefw = new FileWriter("/usr/updateSysTime.sh");
                BufferedWriter excutebw = new BufferedWriter(excutefw);
                excutebw.write("date -s \"" + date + " " + time + "\"\r\n");
                excutebw.close();
                excutefw.close();
                String cmd_date = "sh /usr/updateSysTime.sh";
                Runtime.getRuntime().exec(cmd_date);
            }
            String newTime = DateUtil.convertDateToString(new Timestamp(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");
            LOGGER.info("当前时间为：", newTime);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SendServerInfoTask sendServerInfoTask = new SendServerInfoTask("1", 10000, oldTime);
                    taskService.addTask(sendServerInfoTask);
                }
            }).start();

        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
