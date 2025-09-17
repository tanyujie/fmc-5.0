package com.paradisecloud.fcm.web.task;



import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.task.DelayTask;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class McuZteMutePacketEndConferenceTask extends Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(McuZteMutePacketEndConferenceTask.class);
    private String conferenceId;

    public McuZteMutePacketEndConferenceTask(String id, long delayInMilliseconds, String  conferenceId) {
        super("McuZte_Packet_E" + id, delayInMilliseconds);
        this.conferenceId = conferenceId;
    }

    @Override
    public void run() {



        IBusiConferenceService busiConferenceService = BeanFactory.getBean(IBusiConferenceService.class);
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);

        IBusiTemplateConferenceService busiTemplateConferenceService = BeanFactory.getBean(IBusiTemplateConferenceService.class);

        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceContext cc=(ConferenceContext)baseConferenceContext;

        cc.setPacketConferenceId(null);
        busiConferenceService.endConference(conferenceId, 1, EndReasonsType.ADMINISTRATOR_HANGS_UP);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        busiTemplateConferenceService.deleteBusiTemplateConferenceById(conferenceIdVo.getId());
        return ;
    }
}
