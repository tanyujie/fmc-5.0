package com.paradisecloud.fcm.web.model.mobile;

import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.utils.CauseUtils;
import com.sinhy.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 及时会议同步执行器
 * @author nj
 * @date 2022/6/23 10:55
 */
@Component
public class AppointmentConferenceSyncExecutor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;

    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;

    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;

    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;

    @Resource
    private IBusiConferenceService busiConferenceService;

    private volatile int conferenceEndTipMsgTimes;


    public synchronized void exec(BusiConferenceAppointment busiConferenceAppointment) {
        try {
            AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
            if (rr.isOK(busiConferenceAppointment.getRepeatDate())) {
                String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                Date start = null;
                Date end = null;
                if (rr == AppointmentConferenceRepeatRate.CUSTOM) {
                    start = DateUtils.convertToDate(busiConferenceAppointment.getStartTime());
                    end = DateUtils.convertToDate(busiConferenceAppointment.getEndTime());
                } else {
                    start = DateUtils.convertToDate(today + " " + busiConferenceAppointment.getStartTime());
                    end = DateUtils.convertToDate(today + " " + busiConferenceAppointment.getEndTime());
                }

                if (busiConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                Date curDate = new Date();
                if (curDate.after(start) && curDate.before(end)) {
                    try {
                        startConference(busiConferenceAppointment, end, curDate);
                    } catch (Throwable e) {
                        // 启动失败原因记录
                        setStartFailedInfo(busiConferenceAppointment, e);
                    }
                } else {
                    resetConferenceAppointmentStatus(busiConferenceAppointment);
                    if (rr == AppointmentConferenceRepeatRate.CUSTOM && curDate.after(end)) {
                        busiConferenceAppointmentService.deleteBusiConferenceAppointmentById(busiConferenceAppointment.getId());
                    }
                }
            } else {
                resetConferenceAppointmentStatus(busiConferenceAppointment);
            }
        } catch (Throwable e) {
            logger.error("预约会议任务运行出错：" + busiConferenceAppointment, e);
            // 启动失败原因记录
            setStartFailedInfo(busiConferenceAppointment, e);
        }
    }

    private void setStartFailedInfo(BusiConferenceAppointment busiConferenceAppointment, Throwable e) {
        // 启动失败原因记录
        busiConferenceAppointment.setStartFailedReason(CauseUtils.getRootCause(e));
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        busiConferenceAppointmentMapper.updateBusiConferenceAppointment(busiConferenceAppointment);
    }

    private void startConference(BusiConferenceAppointment busiConferenceAppointment, Date end, Date curDate) {
        // 任务没有处于挂断状态，则进行调度处理
        if (busiConferenceAppointment.getIsHangUp() == null || YesOrNo.convert(busiConferenceAppointment.getIsHangUp()) == YesOrNo.NO) {
            BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            String contextKey = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), McuType.FME.getCode());
            Long conferenceNumber = busiTemplateConference.getConferenceNumber();
            if (YesOrNo.convert(busiTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.YES) {
                if (conferenceNumber == null) {
                    templateConferenceStartService.startConference(busiConferenceAppointment.getTemplateId());
                    busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                    conferenceNumber = busiTemplateConference.getConferenceNumber();
                    ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
                } else {
                    startConference(busiConferenceAppointment, contextKey);
                }
            } else {
                startConference(busiConferenceAppointment, contextKey);
            }
            if (conferenceNumber != null) {
                ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
                cc.setConferenceAppointment(busiConferenceAppointment);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.APPOINTMENT_INFO, busiConferenceAppointment);

                if (conferenceEndTipMsgTimes == 0) {
                    long timeDiff = end.getTime() - curDate.getTime();
                    if (timeDiff < 10 * 60 * 1000) {
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
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, msg);
                        }
                    }
                }

                conferenceEndTipMsgTimes++;
                if (conferenceEndTipMsgTimes >= 6) {
                    conferenceEndTipMsgTimes = 0;
                }
            }

            if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                busiConferenceAppointmentMapper.updateBusiConferenceAppointment(busiConferenceAppointment);
            }
        }
    }

    private void startConference(BusiConferenceAppointment busiConferenceAppointment, String contextKey) {
        ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
        if (cc == null) {
            templateConferenceStartService.startConference(busiConferenceAppointment.getTemplateId());
            cc = ConferenceContextCache.getInstance().get(contextKey);
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "预约会议已开始");
        }
    }

    private void resetConferenceAppointmentStatus(BusiConferenceAppointment busiConferenceAppointment) {
        if (busiConferenceAppointment.getExtendMinutes() == null && busiConferenceAppointment.getIsHangUp() == null && busiConferenceAppointment.getIsStart() == null) {
            return;
        }

        BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
        Long conferenceNumber = busiTemplateConference.getConferenceNumber();
        if (conferenceNumber != null) {
            String contextKey = EncryptIdUtil.generateContextKey(busiTemplateConference.getId(), McuType.FME.getCode());
            ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
            if (cc != null) {
                reset(busiConferenceAppointment);
                busiConferenceService.endConference(contextKey, ConferenceEndType.COMMON.getValue(), EndReasonsType.AUTO_END);
            } else {
                reset(busiConferenceAppointment);
            }
        } else {
            reset(busiConferenceAppointment);
        }
    }

    private void reset(BusiConferenceAppointment busiConferenceAppointment) {
        busiConferenceAppointment.setExtendMinutes(null);
        busiConferenceAppointment.setIsHangUp(null);
        busiConferenceAppointment.setIsStart(null);
        busiConferenceAppointment.setStartFailedReason(null);
        busiConferenceAppointment.setUpdateTime(new Date());
        busiConferenceAppointmentMapper.updateBusiConferenceAppointment(busiConferenceAppointment);
    }

}
