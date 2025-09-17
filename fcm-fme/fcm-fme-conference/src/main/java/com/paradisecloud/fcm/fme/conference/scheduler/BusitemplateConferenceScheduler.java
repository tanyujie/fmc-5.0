package com.paradisecloud.fcm.fme.conference.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.conference.task.CheckAttendeeOnlineStatusTask;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author nj
 * @date 2022/7/25 14:24
 */
@Component
public class BusitemplateConferenceScheduler  extends Thread implements InitializingBean {

    public static final int _TEN_MIN = 10 * 60 * 1000;
    public static final int COUNT_6 = 6;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final int MAX_ENABLE_TIME = 24;

    private volatile int conferenceEndTipMsgTimes;
    private volatile boolean checkOnlineStatus = true;
    private volatile boolean checkAutoCall = true;

    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;

    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;

    @Resource
    private IBusiConferenceService busiConferenceService;

    @Resource
    private IAttendeeService attendeeService;

    @Override
    public void run()
    {
        logger.info("模板会议调度器启动成功！");
        ThreadUtils.sleep(10 * 1000);
        while (true)
        {
            try
            {
                BusiTemplateConference con = new BusiTemplateConference();
                List<BusiTemplateConference> bca = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(con);
                for (BusiTemplateConference busiTemplateConference : bca)
                {
                    exec(busiTemplateConference);
                }
                if (checkOnlineStatus) {
                    CheckAttendeeOnlineStatusTask checkAttendeeOnlineStatusTask = new CheckAttendeeOnlineStatusTask(McuType.FME.getCode(), 0);
                    TaskService taskService = BeanFactory.getBean(TaskService.class);
                    taskService.addTask(checkAttendeeOnlineStatusTask);
                }
                checkOnlineStatus = !checkOnlineStatus;
                checkAutoCall = !checkAutoCall;
            }
            catch (Throwable e)
            {
                logger.error("模板会议调度器执行错误", e);
            }
            finally
            {
                ThreadUtils.sleep(5000);
            }
        }
    }

    private synchronized void exec(BusiTemplateConference busiTemplateConference) {
        Date curDate = new Date();
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiTemplateConference.getId(), McuType.FME.getCode()));
        if (conferenceContext != null) {
            Attendee minutesAttendee = conferenceContext.getMinutesAttendee();
            if (minutesAttendee != null && minutesAttendee.isMeetingJoined()) {
                Integer asrTime = LicenseCache.getInstance().getAsrTime();
                if (OpsDataCache.getInstance().getAsrTime() != 0) {
                    asrTime = OpsDataCache.getInstance().getAsrTime();
                    if (asrTime == -1) {
                        asrTime = null;
                    }
                }
                if (asrTime != null) {
                    long meetingJoinedTime = minutesAttendee.getMeetingJoinedTime();
                    if (System.currentTimeMillis() - meetingJoinedTime > asrTime * 60000) {
                        IAttendeeService attendeeService = BeanFactory.getBean(IAttendeeService.class);
                        attendeeService.hangUp(conferenceContext.getId(), minutesAttendee.getId());
                        if (conferenceContext.getAttendeeById(minutesAttendee.getId()) != null) {
                            attendeeService.remove(conferenceContext.getId(), minutesAttendee.getId());
                        }
                        String text = "字幕使用时长" + asrTime + "分钟用完自动结束";
                        conferenceContext.minutesLog("------" + text + "------");
                        BusiOperationLog busiOperationLog = new BusiOperationLog();
                        busiOperationLog.setActionDetails(text);
                        busiOperationLog.setHistoryConferenceId(conferenceContext.getHistoryConference().getId());
                        busiOperationLog.setUserId(null);
                        busiOperationLog.setOperatorName("系统");
                        busiOperationLog.setTime(new Date());
                        busiOperationLog.setActionResult(2);
                        busiOperationLog.setIp("127.0.0.1");
                        busiOperationLog.setDeviceType("system");
                        IBusiOperationLogService busiOperationLogService = BeanFactory.getBean(IBusiOperationLogService.class);
                        busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                    }
                }
            }
            if (busiTemplateConference.getDurationEnabled() == 1) {
                if (judgmentDate(conferenceContext.getStartTime(), curDate, busiTemplateConference.getDurationTime())) {
                    busiConferenceService.endConference(conferenceContext.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.AUTO_END);
                } else {
                    if (conferenceEndTipMsgTimes == 0) {
                        Date endDate = DateUtils.addMinutes(conferenceContext.getStartTime(), busiTemplateConference.getDurationTime());
                        long timeDiff = endDate.getTime() - curDate.getTime();
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
                                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, msg);
                            }
                            if (timeDiff < 6) {
                                if (s >= 0 && s < 5) {
                                    BeanFactory.getBean(IMqttService.class).sendConferenceComingToEndMessage(conferenceContext, min, s);
                                }
                            }
                        }
                    }

                    conferenceEndTipMsgTimes++;
                    if (conferenceEndTipMsgTimes >= COUNT_6) {
                        conferenceEndTipMsgTimes = 0;
                    }
                }
            }

            // 离线重邀
            if (checkAutoCall) {
                if (conferenceContext.isAutoCallTerminal()) {
                    List<Attendee> outBoundTerminals = new ArrayList<>();
                    ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                        if (a instanceof TerminalAttendee) {
                            TerminalAttendee ta = (TerminalAttendee) a;

                            // 被叫由会控负责发起呼叫
                            if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                                if (!ta.isMeetingJoined() && !ta.isHangUp()) {
                                    if (ta.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue() && ta.getAutoCallTimes() < 10) {
                                        outBoundTerminals.add(ta);
                                    }
                                }
                            }
                        } else if (a instanceof InvitedAttendee) {
                            InvitedAttendee ia = (InvitedAttendee) a;

                            if (!ia.isMeetingJoined() && !ia.isHangUp()) {
                                if (ia.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue() && ia.getAutoCallTimes() < 10) {
                                    outBoundTerminals.add(ia);
                                }
                            }
                        }
                    });
                    for (Attendee attendee : outBoundTerminals) {
                        attendeeService.recall(conferenceContext.getId(), attendee.getId());
                        attendee.setAutoCallTimes(attendee.getAutoCallTimes() + 1);
                    }
                }
            }

            // 消除系统字幕
            if (conferenceContext.getSystemMessageEndTime() > 0 && System.currentTimeMillis() - conferenceContext.getSystemMessageEndTime() >= 0) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("messageText", "");
                    jsonObject.put("messageDuration", "permanent");
                    jsonObject.put("messagePosition", "top");
                    attendeeService.sendMessage(conferenceContext.getId(), jsonObject);
                    conferenceContext.setSystemMessageEndTime(0);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    /**判断是否超过24小时
     *
     * @param start
     * @param end
     * @return boolean
     */
    public static boolean judgmentDate(Date start, Date end)  {

        long cha = end.getTime() - start.getTime();
        if(cha<0){
            return false;
        }
        double result = cha * 1.0 / (1000 * 60 * 60);
        if(result>= MAX_ENABLE_TIME){
            return true;
        }else{
            return false;
        }

    }

    /**判断是否超过指定分钟数
     *
     * @param start
     * @param end
     * @return boolean
     */
    public static boolean judgmentDate(Date start, Date end,int durationTime)  {

        long cha = end.getTime() - start.getTime();
        if(cha<0){
            return false;
        }
        double result = cha * 1.0 / (1000 * 60 );
        if(durationTime==0){
            durationTime=1440;
        }
        if(result>= durationTime){
            return true;
        }else{
            return false;
        }

    }

}
