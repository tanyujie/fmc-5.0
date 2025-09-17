package com.paradisecloud.fcm.service.task;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.ViewConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateParticipantMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.notify.NotifyService;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过邮件和短信推送预约会议消息
 */
public class NotifyTask extends Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyTask.class);

    private BaseConferenceContext conferenceContext;
    private Long appointmentId;
    private String mcuTypeStr;
    private String actionStr;
    private ViewConferenceAppointment viewConferenceAppointment;
    private ViewTemplateConference viewTemplateConference;
    private List<ViewTemplateParticipant> viewTemplateParticipantList;

    public NotifyTask(String id, long delayInMilliseconds,
                      BaseConferenceContext conferenceContext,
                      Long appointmentId, String mcuTypeStr,
                      String actionStr) {
        super("_NotifyTask_"+id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.appointmentId = appointmentId;
        this.mcuTypeStr = mcuTypeStr;
        this.actionStr = actionStr;
    }

    public NotifyTask(String id, long delayInMilliseconds,
                      BaseConferenceContext conferenceContext,
                      Long appointmentId, String mcuTypeStr,
                      String actionStr,
                      ViewConferenceAppointment viewConferenceAppointment,
                      ViewTemplateConference viewTemplateConference,
                      List<ViewTemplateParticipant> viewTemplateParticipantList) {
        super(id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.appointmentId = appointmentId;
        this.mcuTypeStr = mcuTypeStr;
        this.actionStr = actionStr;
        this.viewConferenceAppointment = viewConferenceAppointment;
        this.viewTemplateConference = viewTemplateConference;
        this.viewTemplateParticipantList = viewTemplateParticipantList;
    }


    @Override
    public void run() {
        LOGGER.info("信息推送开始。ID:" + getId());
        List<Long> terminalIdList = new ArrayList<>();
        String createUserName = null;
        String startTime = null;
        String endTime = null;
        String conferenceName = null;

        ViewConferenceAppointmentMapper viewConferenceAppointmentMapper = BeanFactory.getBean(ViewConferenceAppointmentMapper.class);
        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
        ViewTemplateParticipantMapper viewTemplateParticipantMapper = BeanFactory.getBean(ViewTemplateParticipantMapper.class);
        BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
        SysUserMapper sysUserMapper = BeanFactory.getBean(SysUserMapper.class);
        NotifyService notifyService = BeanFactory.getBean(NotifyService.class);

        if (conferenceContext != null) {
            conferenceName = conferenceContext.getName();
            BusiConferenceAppointment conferenceAppointment = conferenceContext.getConferenceAppointment();
            if (conferenceAppointment != null) {
                createUserName = conferenceAppointment.getCreateBy();

                startTime = DateUtil.convertDateToString(conferenceContext.getStartTime(), "");
                endTime = DateUtil.convertDateToString(conferenceContext.getEndTime(), "");
                if (conferenceContext.getMasterAttendee() != null) {
                    BaseAttendee masterAttendee = conferenceContext.getMasterAttendee();
                    if (masterAttendee != null && masterAttendee.getTerminalId() != null) {
                        terminalIdList.add(masterAttendee.getTerminalId());
                    }
                }

                ArrayList<BaseAttendee> arrayList = new ArrayList<>(conferenceContext.getAttendees());
                for (BaseAttendee baseAttendee : arrayList) {
                    if (baseAttendee != null && baseAttendee.getTerminalId() != null) {
                        terminalIdList.add(baseAttendee.getTerminalId());
                    }
                }

                for (Object deptIdObj : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    if (ObjectUtils.isNotEmpty(deptIdObj)) {
                        if (deptIdObj instanceof Long) {
                            Long deptId = (Long) deptIdObj;
                            List<BaseAttendee> attendees = (List<BaseAttendee>) conferenceContext.getCascadeAttendeesMap().get(deptId);
                            if (attendees != null) {
                                for (BaseAttendee attendee : attendees) {
                                    if (attendee != null && attendee.getTerminalId() != null) {
                                        terminalIdList.add(attendee.getTerminalId());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuTypeStr, appointmentId);
            if (viewConferenceAppointment == null) {
                viewConferenceAppointment = this.viewConferenceAppointment;
            }
            if (viewConferenceAppointment != null) {
                startTime = viewConferenceAppointment.getStartTime();
                endTime = viewConferenceAppointment.getEndTime();
                Long templateId = viewConferenceAppointment.getTemplateId();
                createUserName = viewConferenceAppointment.getCreateBy();
                if (templateId != null) {
                    ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuTypeStr, templateId);
                    if (viewTemplateConference == null) {
                        viewTemplateConference = this.viewTemplateConference;
                    }
                    if (viewTemplateConference != null) {
                        conferenceName = viewTemplateConference.getName();
                        ViewTemplateParticipant viewTemplateParticipantTemp = new ViewTemplateParticipant();
                        viewTemplateParticipantTemp.setMcuType(mcuTypeStr);
                        viewTemplateParticipantTemp.setTemplateConferenceId(templateId);
                        List<ViewTemplateParticipant> viewTemplateParticipantList = viewTemplateParticipantMapper.selectViewTemplateParticipantList(viewTemplateParticipantTemp);
                        if (this.viewTemplateParticipantList != null && this.viewTemplateParticipantList.size() > 0) {
                            viewTemplateParticipantList = this.viewTemplateParticipantList;
                        }
                        if (viewTemplateParticipantList != null && viewTemplateParticipantList.size() > 0) {
                            for (ViewTemplateParticipant viewTemplateParticipant : viewTemplateParticipantList) {
                                Long terminalId = viewTemplateParticipant.getTerminalId();
                                terminalIdList.add(terminalId);
                            }
                        }
                    }
                }
            }
        }

        if (terminalIdList != null && terminalIdList.size() > 0) {
            for (Long terminalId : terminalIdList) {
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(terminalId);
                if (busiUserTerminal != null) {
                    Boolean isEmail = false;
                    Boolean isPhone = false;
                    Long userId = busiUserTerminal.getUserId();
                    SysUser sysUser = sysUserMapper.selectUserById(userId);
                    String content = "     " + actionStr + "的会议！" + "\n" +
                            "\n" +
                            "     " + "会议名：" + conferenceName + "\n" +
                            "     " + "会议时间：" + startTime + " --- " + endTime + "\n" +
                            "     " + "创建者：" + createUserName + "\n";

                    String email = sysUser.getEmail();
                    String phonenumber = sysUser.getPhonenumber();

                    if (StringUtils.isNotEmpty(email)) {
                        isEmail = true;
                    }
                    if (StringUtils.isNotEmpty(phonenumber)) {
                        isPhone = true;
                    }

                    if (isEmail || isPhone) {
                        if (isEmail) {
                            notifyService.notifyMail("会议通知", content, email);
                        }
                        if (isPhone) {
                            notifyService.notifySms(phonenumber, content);
                        }
                    }
                }
            }
        }
    }
}
