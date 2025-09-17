package com.paradisecloud.fcm.service.task;

import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.ViewConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateParticipantMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.eunm.NotifyType;
import com.paradisecloud.fcm.service.notify.NotifyService;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 通过邮件和短信推送预约会议消息
 */
public class OpsNotifyTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpsNotifyTask.class);

    private  NotifyType notifyType;
    private String conferenceNumber;
    private  String phone;
    private String conferenceName;
    private  String startTime;
    private String endTime;

    public OpsNotifyTask(String id, long delayInMilliseconds,
                         String conferenceName,
                         String conferenceNumber,
                         String phone,
                         String startTime,
                         String endTime,
                         String notifyType) {
        super(id, delayInMilliseconds);
        this.phone = phone;
        this.notifyType = NotifyType.valueOf(notifyType);
        this.conferenceName=conferenceName;
        this.conferenceNumber=conferenceNumber;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    @Override
    public void run() {
        LOGGER.info("OPS管理员信息推送开始。ID:" + getId());

        NotifyService notifyService = BeanFactory.getBean(NotifyService.class);

        String[] params = null;
        /**
         * 有会议“{1}”将于{2}召开，会议号{3}。
         * 会议号{1}的会议“{2}”已于{3}开始。
         * 会议号{1}的会议“{2}”已于{3}结束。
         * 原定于{1} 召开的 “{2}” 会议取消
         */
        switch (notifyType){
            case ADMIN_MEETING_BOOK:
                params = new String[]{conferenceName,startTime,conferenceNumber};
                break;
            case ADMIN_MEETING_START:
                params = new String[]{conferenceNumber,conferenceName,startTime};
                break;
            case ADMIN_MEETING_END:
                params = new String[]{conferenceNumber,conferenceName,endTime};
                break;
            case ADMIN_MEETING_CANCEL:
                params = new String[]{startTime,conferenceName};
                break;
            case ADMIN_TENCENT_MEETING_START:
                params = new String[]{conferenceName,startTime,conferenceNumber};
                break;
        }
        if(Strings.isNotBlank(phone)){
            notifyService.notifySmsTemplate(phone, notifyType, params);

        }

    }
}
