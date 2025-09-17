package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrEpsStatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McuZjDeleteEpsTask extends DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(McuZjDeleteEpsTask.class);

    private McuZjConferenceContext conferenceContext;
    private String epUserId;

    public McuZjDeleteEpsTask(String id, long delayInMilliseconds, McuZjConferenceContext conferenceContext, String epUserId) {
        super("delete_e_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.epUserId = epUserId;
    }

    @Override
    public void run() {
        LOGGER.info("MCU_ZJ删除会议终端开始。ID:" + getId());

        if (conferenceContext != null) {
            if (StringUtils.isNotEmpty(epUserId)) {
                // 删除会议室
                try {
                    CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                    ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_del_participants);
                    ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{epUserId});
                    conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                } catch (Exception e) {
                }
            }
        }
    }
}
