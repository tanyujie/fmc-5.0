package com.paradisecloud.fcm.huaweicloud.huaweicloud.scheduler;


import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudService;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;


/**
 * @author nj
 * @date 2023/7/24 14:14
 */
@Component
public class HwcloudConferenceContextMonitorThread extends Thread implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final int _TEN_MIN = 10 * 60 * 1000;
    public static final int COUNT_6 = 6;
    private volatile int conferenceEndTipMsgTimes;


    @Override
    public void run() {

        while (true) {
            try {
                Collection<HwcloudConferenceContext> values = HwcloudConferenceContextCache.getInstance().values();
                if (CollectionUtils.isEmpty(values)) {
                    return;
                }
                for (HwcloudConferenceContext value : values) {
                    if (value != null && value.isStart()) {
                        exec(value);
                    }
                }

            } catch (Throwable e) {
                logger.error("会议状态检查错误", e);
            } finally {
                ThreadUtils.sleep(10000);
            }
        }
    }

    private void exec(HwcloudConferenceContext value) {
        Date startTime = value.getStartTime();
        Integer durationTime = value.getDurationTime();
        Date curDate = new Date();
        if(curDate.getTime()>value.getEndTime().getTime()){
            //结束会议
            IBusiHwcloudConferenceService hwcloudConferenceService = BeanFactory.getBean(IBusiHwcloudConferenceService.class);
            hwcloudConferenceService.endConference(value.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.AUTO_END);
        }
        if (conferenceEndTipMsgTimes == 0) {
        //    Date endDate = DateUtils.addMinutes(startTime, durationTime);

            long timeDiff = value.getEndTime().getTime() - curDate.getTime();
            if (timeDiff < _TEN_MIN) {
                long min = timeDiff / (60 * 1000);
                long s = (timeDiff / 1000) % 60;
                StringBuilder msg = new StringBuilder();
                msg.append("距离会议结束还剩【");
                if (min > 0) {
                    msg.append(min).append("分");
                }
                if (s > 0) {
                    msg.append(s).append("秒");
                }

                if (s > 0 || min > 0) {
                    msg.append("】请做好散会准备，如需继续进行本会议，请延长本会议结束时间！");
                    HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(value, WebsocketMessageType.MESSAGE_TIP, msg);
                }
//                if (timeDiff < 6) {
//                    if (s >= 0 && s < 5) {
//                        BeanFactory.getBean(IMqttService.class).sendConferenceComingToEndMessage(value, min, s);
//                    }
//                }
            }
        }

        conferenceEndTipMsgTimes++;
        if (conferenceEndTipMsgTimes >= COUNT_6) {
            conferenceEndTipMsgTimes = 0;
        }


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
