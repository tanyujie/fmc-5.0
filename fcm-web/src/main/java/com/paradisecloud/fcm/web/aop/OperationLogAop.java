package com.paradisecloud.fcm.web.aop;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceApprovalMapper;
import com.paradisecloud.fcm.dao.mapper.ViewConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;
import com.paradisecloud.fcm.mqtt.constant.InstantMeetingParam;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.interfaces.ITerminalActionService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class OperationLogAop {

    protected final Logger log = LoggerFactory.getLogger(BaseController.class);

    @Resource
    private BusiConferenceApprovalMapper busiConferenceApprovalMapper;
    @Resource
    private IBusiOperationLogService busiOperationLogService;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private IBusiHistoryConferenceService busiHistoryConferenceService;
    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;
    @Resource
    private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;

    @Pointcut("@annotation(io.swagger.v3.oas.annotations.Operation)")
    public void operation() {
    }

    @Around("operation()")
    public Object recordLog(ProceedingJoinPoint point) throws Throwable {
        Map<String, Object> proceedMap = new HashMap<>();
        proceedMap.put("proceed", false);
        proceedMap.put("throwable", null);
        try {
            /* 读取ActionLog注解消息 */
            MethodSignature signature = (MethodSignature) (point.getSignature());
            Method targetMethod = signature.getMethod();
            io.swagger.v3.oas.annotations.Operation anno =
                    targetMethod.getAnnotation(io.swagger.v3.oas.annotations.Operation.class);
            if (anno != null && StringUtils.isNotEmpty(anno.description())) {
                String actionDetails = anno.description();
                BusiOperationLog busiOperationLog = new BusiOperationLog();
                String summary = anno.summary();

                if (StringUtils.isNotEmpty(summary) && summary.equals("FMQ")) {
                    fmq(point, busiOperationLog, actionDetails);
                } else {
                    try {
                        webController(point, busiOperationLog, actionDetails, proceedMap);
                        Boolean proceedTemp = (Boolean) proceedMap.get("proceed");
                        if (proceedTemp) {
                            Object proceedObj = proceedMap.get("proceedObj");
                            return proceedObj;
                        }
                    } catch (Exception e) {
                        Boolean proceedTemp = (Boolean) proceedMap.get("proceed");
                        if (proceedTemp) {
                            Object proceedObj = proceedMap.get("proceedObj");
                            return proceedObj;
                        } else {
                            Object throwableObj = proceedMap.get("throwable");
                            if (throwableObj != null) {
                                throw (Throwable) throwableObj;
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.info("OperationLogAop === >>>   " + e.getMessage());
            throw e;
        }
        Object throwableObj = proceedMap.get("throwable");
        if (throwableObj != null) {
            throw (Throwable) throwableObj;
        }

        return point.proceed();
    }

    private void fmq(ProceedingJoinPoint point, BusiOperationLog busiOperationLog, String actionDetails) {
        IBusiOperationLogService busiOperationLogService = BeanFactory.getBean(IBusiOperationLogService.class);
        ITerminalActionService iTerminalActionService = BeanFactory.getBean(ITerminalActionService.class);
        Integer actionResult = 1;
        Long historyConferenceId = null;
        BusiTerminal busiTerminal = null;

        MethodSignature signature = (MethodSignature) (point.getSignature());
        Method targetMethod = signature.getMethod();
        io.swagger.v3.oas.annotations.Operation anno =
                targetMethod.getAnnotation(io.swagger.v3.oas.annotations.Operation.class);
        Map<String, Object> parameter = getParameter(point);
        JSONObject jsonS = null;
        if (parameter.containsKey("jsonS")) {
            Object jsonSObj = parameter.get("jsonS");
            if (ObjectUtils.isNotEmpty(jsonSObj)) {
                jsonS = (JSONObject) jsonSObj;
                String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
                busiTerminal = TerminalCache.getInstance().getBySn(sn);
            }
        }
        if (parameter.containsKey("clientId")) {
            Object clientIdObj = parameter.get("clientId");
            if (ObjectUtils.isNotEmpty(clientIdObj)) {
                String clientId = (String) clientIdObj;
                busiTerminal = TerminalCache.getInstance().getBySn(clientId);
            }
        } else {
            String sn = jsonS.getString(MqttConfigConstant.CLIENTID);
            busiTerminal = TerminalCache.getInstance().getBySn(sn);
        }

        if (busiTerminal != null && jsonS != null) {
            String conferenceNum = jsonS.getString(InstantMeetingParam.CONFERENCENUM);
            if (StringUtils.isNotEmpty(conferenceNum)) {
                BaseConferenceContext mcuZjConferenceContext = AllConferenceContextCache.getInstance().get(conferenceNum);
                boolean mcuZjConferenceContextIsNull = false;
                BaseAttendee attendeeById = null;
                if (mcuZjConferenceContext != null) {
                    mcuZjConferenceContextIsNull = true;
                    String attendeeId = jsonS.getString(MqttConfigConstant.ATTENDEE_ID);
                    if (StringUtils.isNotEmpty(attendeeId)) {
                        attendeeById = mcuZjConferenceContext.getAttendeeById(attendeeId);
                    }
                    actionDetails = "会议号：" + mcuZjConferenceContext.getTenantId() + mcuZjConferenceContext.getConferenceNumber() + actionDetails;
                }

                boolean canControlConference = iTerminalActionService.canControlConference(busiTerminal.getSn(), conferenceNum);
                if (!canControlConference) {
                    actionResult = 2;
                    actionDetails += "没有操作权限！";
                } else {
                    String operationId = anno.operationId();
                    if (StringUtils.isNotEmpty(operationId)) {
                        switch (operationId) {
                            case "terminalCreateConference": {
                                String theme = jsonS.getString(InstantMeetingParam.THEME);
                                actionDetails += " 名称：" + theme + ";  操作成功";
                                break;
                            }
                            case "hostExtendMinutes": {
                                Integer minutes = jsonS.getInteger(InstantMeetingParam.MINUTES);
                                actionDetails += " 延长时间： " + minutes + ";  操作成功";
                                break;
                            }
                            case "recordConference": {
                                Boolean recording = jsonS.getBoolean(InstantMeetingParam.RECORDING_STATUS);
                                if (recording) {
                                    actionDetails += " 开启" + ";  操作成功";
                                } else {
                                    actionDetails += " 关闭" + ";  操作成功";
                                }
                                break;
                            }
                            case "setConferenceCaption": {
                                String message = jsonS.getString(InstantMeetingParam.MESSAGE_TEXT);
                                actionDetails += " 消息内容：" + message + ";  操作成功";
                                break;
                            }
                            default: {
                                if (mcuZjConferenceContextIsNull) {
                                    actionDetails += "与会者： " + attendeeById.getName() + ";  操作成功";
                                }
                                break;
                            }
                        }
                    }
                }


                busiOperationLog.setActionDetails(actionDetails);
                busiOperationLog.setHistoryConferenceId(historyConferenceId);

                busiOperationLog.setUserId(busiTerminal.getCreateUserId());
                busiOperationLog.setOperatorName(busiTerminal.getName());
                busiOperationLog.setTime(new Date());
                busiOperationLog.setActionResult(actionResult);
                busiOperationLog.setIp(busiTerminal.getIntranetIp());
                busiOperationLog.setDeviceType(busiTerminal.getAppType());

                busiOperationLogService.insertBusiOperationLog(busiOperationLog);
            }
        }
    }

    private void webController(ProceedingJoinPoint point, BusiOperationLog busiOperationLog, String actionDetails, Map<String, Object> proceedMap) {
        Integer actionResult = 2;
        Long historyConferenceId = null;

        MethodSignature signature = (MethodSignature) (point.getSignature());
        Method targetMethod = signature.getMethod();
        io.swagger.v3.oas.annotations.Operation anno =
                targetMethod.getAnnotation(io.swagger.v3.oas.annotations.Operation.class);
        String fullConferenceNumber = null;
        Map<String, Object> parameter = getParameter(point);
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        String requestURI = request.getRequestURI();
        Boolean isOk = false;
        if (requestURI.contains("/busi/mcu/all/templateConference") || requestURI.contains("/busi/mcu/all/conferenceAppointment")) {
            if (parameter.containsKey("conferenceId")) {
                Object conferenceIdObj = parameter.get("conferenceId");
                if (ObjectUtils.isNotEmpty(conferenceIdObj)) {
                    String conferenceId = (String) conferenceIdObj;
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
                    Long id = conferenceIdVo.getId();
                    McuType mcuType = conferenceIdVo.getMcuType();
                    ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType.getCode(), id);
                    if (viewTemplateConference != null) {
                        fullConferenceNumber = viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber();
                        actionDetails += " [Id:" + viewTemplateConference.getId() + "  模板名:" + viewTemplateConference.getName() + "  会议号:" + viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber() + "] ";
                        isOk = true;
                    }
                }
            }
        }

        if (requestURI.contains("/ops")) {
            String description = anno.description();
            if (description.equals("修改opsIP地址")) {
                Object busiOpsInfo = parameter.get("busiOpsInfo");

                BusiOpsInfo busiOpsInfo1=(BusiOpsInfo)busiOpsInfo;
                String ipAddress = busiOpsInfo1.getIpAddress();
                String fmeIp = busiOpsInfo1.getFmeIp();
                String gatewayName = busiOpsInfo1.getGatewayName();
                String subnetMask = busiOpsInfo1.getSubnetMask();

                actionDetails = actionDetails+" 新IP:"+ipAddress+" 新FMEIP:"+fmeIp+" 网关:"+gatewayName+" 子网掩码:"+subnetMask;

            }
        }

        if (requestURI.contains("/busi/mcu/all/conferenceAppointment")) {
            if (parameter.containsKey("apConferenceId")) {
                Object apConferenceIdObj = parameter.get("apConferenceId");
                if (ObjectUtils.isNotEmpty(apConferenceIdObj)) {
                    String apConferenceId = (String) apConferenceIdObj;
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(apConferenceId);
                    Long id = conferenceIdVo.getId();
                    McuType mcuType = conferenceIdVo.getMcuType();
                    ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType.getCode(), id);
                    String details = "";
                    if (viewConferenceAppointment != null) {
                        ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType.getCode(), viewConferenceAppointment.getTemplateId());
                        if (viewTemplateConference != null) {
                            details += " [Id:" + viewTemplateConference.getId() + "  模板名:" + viewTemplateConference.getName() + "  会议号:" + (StringUtils.isNotEmpty(viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber()) ? viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber() : "空") + "] ";
                        }
                    }
                    actionDetails = actionDetails + details;
                    isOk = true;
                }
            }
        }

        if (StringUtils.isEmpty(fullConferenceNumber)) {
            if (requestURI.contains("/busi/mcu/all/attendee") || requestURI.contains("/busi/mcu/all/conference")) {
                if (requestURI.contains("/busi/mcu/all/conference/stream/")) {
                    if (parameter.containsKey("conferenceId")) {
                        Object conferenceIdObj = parameter.get("conferenceId");
                        if (ObjectUtils.isNotEmpty(conferenceIdObj)) {
                            String conferenceId = (String) conferenceIdObj;
                            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                            if (baseConferenceContext != null) {
                                String conferenceNumber = baseConferenceContext.getConferenceNumber();
                                String url = null;
                                if (parameter.containsKey("json")) {
                                    Object jsonObj = parameter.get("json");
                                    if (ObjectUtils.isNotEmpty(jsonObj)) {
                                        JSONObject jsonObject = (JSONObject) jsonObj;
                                        url = jsonObject.getString("streamingUrl");
                                    }
                                }

                                if (parameter.containsKey("enabled")) {
                                    Object enabledObj = parameter.get("enabled");
                                    if (ObjectUtils.isNotEmpty(enabledObj)) {
                                        Boolean enabled = (Boolean) enabledObj;
                                        if (enabled) {

                                            actionDetails = "会议号：" + baseConferenceContext.getTenantId() + conferenceNumber + " URL: " + url + "  " + "开启" + actionDetails;
                                        } else {
                                            actionDetails = "会议号：" + baseConferenceContext.getTenantId() + conferenceNumber + "  " + "结束" + actionDetails;
                                        }
                                    }
                                }

                                BusiHistoryConference historyConference = baseConferenceContext.getHistoryConference();
                                if (historyConference != null) {
                                    historyConferenceId = historyConference.getId();
                                }
                            }
                        }
                    }
                } else if (requestURI.contains("/busi/mcu/all/conference/minutes/")) {
                    if (parameter.containsKey("conferenceId")) {
                        Object conferenceIdObj = parameter.get("conferenceId");
                        if (ObjectUtils.isNotEmpty(conferenceIdObj)) {
                            String conferenceId = (String) conferenceIdObj;
                            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                            if (baseConferenceContext != null) {
                                String conferenceNumber = baseConferenceContext.getConferenceNumber();

                                if (parameter.containsKey("enabled")) {
                                    Object enabledObj = parameter.get("enabled");
                                    if (ObjectUtils.isNotEmpty(enabledObj)) {
                                        Boolean enabled = (Boolean) enabledObj;
                                        if (enabled) {

                                            actionDetails = "会议号：" + baseConferenceContext.getTenantId() + conferenceNumber + "  开启" + actionDetails;
                                        } else {
                                            actionDetails = "会议号：" + baseConferenceContext.getTenantId() + conferenceNumber + "  结束" + actionDetails;
                                        }
                                    }
                                }

                                BusiHistoryConference historyConference = baseConferenceContext.getHistoryConference();
                                if (historyConference != null) {
                                    historyConferenceId = historyConference.getId();
                                }
                            }
                        }
                    }
                } else {
                    if (parameter.containsKey("conferenceId")) {
                        Object conferenceIdObj = parameter.get("conferenceId");
                        BaseConferenceContext baseConferenceContext = null;
                        if (ObjectUtils.isNotEmpty(conferenceIdObj)) {
                            String conferenceId = (String) conferenceIdObj;
                            baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                            if (baseConferenceContext != null) {
                                String conferenceNumber = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber();
                                actionDetails = "会议号：" + conferenceNumber + "  " + actionDetails;
                                BusiHistoryConference historyConference = baseConferenceContext.getHistoryConference();
                                if (historyConference != null) {
                                    historyConferenceId = historyConference.getId();
                                }
                            }
                        }
                        if (parameter.containsKey("attendeeId")) {
                            Object attendeeIdObj = parameter.get("attendeeId");
                            if (ObjectUtils.isNotEmpty(attendeeIdObj)) {
                                String attendeeId = (String) attendeeIdObj;
                                if (baseConferenceContext != null) {
                                    BaseAttendee attendeeById = baseConferenceContext.getAttendeeById(attendeeId);
                                    if (attendeeById != null) {
                                        String name = attendeeById.getName();
                                        actionDetails += " 与会者：" + name;
                                    }
                                }
                            }
                        }
                        if (requestURI.contains("/sendMessage/") && parameter.containsKey("jsonObject")) {
                            Object object = parameter.get("jsonObject");
                            if (ObjectUtils.isNotEmpty(object)) {
                                JSONObject jsonObject = (JSONObject) object;
                                String messageText = jsonObject.getString("messageText");
                                if (StringUtils.isNotEmpty(messageText)) {
                                    actionDetails += " 消息内容：" + messageText;
                                }
                            }
                        }
                        if (requestURI.contains("/setMessageBannerText/") && parameter.containsKey("jsonObject")) {
                            Object object = parameter.get("jsonObject");
                            if (ObjectUtils.isNotEmpty(object)) {
                                JSONObject jsonObject = (JSONObject) object;
                                String messageBannerText = jsonObject.getString("messageBannerText");
                                Boolean enable = jsonObject.getBoolean("enable");
                                if (enable != null) {
                                    if (enable) {
                                        actionDetails += " 开启，横幅内容：" + messageBannerText;
                                    } else {
                                        actionDetails += " 关闭，横幅内容：" + messageBannerText;
                                    }
                                }
                            }
                        }
                        if (requestURI.contains("/batchInvite/") && parameter.containsKey("terminalIds")) {
                            Object object = parameter.get("terminalIds");
                            if (ObjectUtils.isNotEmpty(object)) {
                                List<Long> terminalIds = (List<Long>) object;
                                String name = "";
                                if (terminalIds != null && terminalIds.size() > 0) {
                                    for (Long terminalId : terminalIds) {
                                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
                                        name += " [" + busiTerminal.getName() + "] ";
                                    }
                                }
                                actionDetails += " 终端：" + name;
                            }
                        }
                        if (requestURI.contains("/inviteByUri/") && parameter.containsKey("jsonObj")) {
                            Object object = parameter.get("jsonObj");
                            if (ObjectUtils.isNotEmpty(object)) {
                                JSONObject jsonObject = (JSONObject) object;
                                String url = jsonObject.getString("uri");
                                if (StringUtils.isNotEmpty(url)) {
                                    actionDetails += " URI：" + url;
                                }
                            }
                        }
                    }
                }
            }
            if (requestURI.contains("/busi/mcu/all/recording/changeRecordingStatus/")) {
                if (parameter.containsKey("conferenceId")) {
                    Object conferenceIdObj = parameter.get("conferenceId");
                    if (ObjectUtils.isNotEmpty(conferenceIdObj)) {
                        String conferenceId = (String) conferenceIdObj;
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                        if (baseConferenceContext != null) {
                            if (parameter.containsKey("jsonObject")) {
                                Object jsonObjectObj = parameter.get("jsonObject");
                                if (ObjectUtils.isNotEmpty(jsonObjectObj)) {
                                    JSONObject jsonObject = (JSONObject) jsonObjectObj;
                                    boolean flag = jsonObject.getBoolean("recording");
                                    if (flag) {
                                        actionDetails = "会议号:" + baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "  " + "开启" + actionDetails;
                                    } else {
                                        actionDetails = "会议号:" + baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "  " + "结束" + actionDetails;
                                    }
                                }
                            }

                            BusiHistoryConference historyConference = baseConferenceContext.getHistoryConference();
                            if (historyConference != null) {
                                historyConferenceId = historyConference.getId();
                            }
                        }
                    }
                }
            }
        }
        if (requestURI.contains("/busi/terminal")) {
            if (anno != null && StringUtils.isNotEmpty(anno.description())) {
                String description = anno.description();
                if (description.equals("新增终端")) {
                    if (parameter.containsKey("id")) {
                        Object idObj = parameter.get("id");
                        if (ObjectUtils.isNotEmpty(idObj)) {
                            Long id = (Long) idObj;
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                            if (busiTerminal != null) {
                                actionDetails += " [Id:" + busiTerminal.getId() + "  名称:" + busiTerminal.getName() + "] ";
                                isOk = true;
                            }
                        }
                    }
                }
                if (description.equals("修改终端")) {
                    if (parameter.containsKey("id")) {
                        Object idObj = parameter.get("id");
                        if (ObjectUtils.isNotEmpty(idObj)) {
                            Long id = (Long) idObj;
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                            if (busiTerminal != null) {
                                actionDetails += " [Id:" + busiTerminal.getId() + "  名称:" + busiTerminal.getName() + "] ";
                                isOk = true;
                            }
                        }
                    }
                } else {
                    if (description.equals("删除终端")) {
                        if (parameter.containsKey("ids")) {
                            Object idObj = parameter.get("ids");
                            if (ObjectUtils.isNotEmpty(idObj)) {
                                Long[] ids = (Long[]) idObj;
                                String details = "";
                                for (Long id : ids) {
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                                    if (busiTerminal != null) {
                                        details += " [Id:" + busiTerminal.getId() + "  名称:" + busiTerminal.getName() + "] ";
                                    }
                                }
                                actionDetails = actionDetails + details;
                                isOk = true;
                            }
                        }
                    }
                }
                if (description.equals("导出终端列表")) {
                    if (parameter.containsKey("ids")) {
                        Object idObj = parameter.get("ids");
                        if (ObjectUtils.isNotEmpty(idObj)) {
                            Long[] ids = (Long[]) idObj;
                            String details = "";
                            for (Long id : ids) {
                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(id);
                                if (busiTerminal != null) {
                                    details += " [Id:" + busiTerminal.getId() + "  名称:" + busiTerminal.getName() + "] ";
                                }
                            }
                            actionResult = 1;
                            actionDetails = actionDetails + details;
                            isOk = true;
                        }
                    }
                }
            }
        }
        if (requestURI.contains("/busi/user")) {
            if (anno != null && StringUtils.isNotEmpty(anno.description())) {
                String description = anno.description();
                if (description.equals("修改用户")) {
                    if (parameter.containsKey("user")) {
                        Object userObj = parameter.get("user");
                        if (ObjectUtils.isNotEmpty(userObj)) {
                            BusiUser busiUser = (BusiUser) userObj;
                            if (busiUser != null) {
                                actionDetails += " [Id:" + busiUser.getUserId() + "  名称:" + busiUser.getNickName() + "] ";
                                isOk = true;
                            }
                        }
                    }
                } else {
                    if (description.equals("删除用户")) {
                        if (parameter.containsKey("userIds")) {
                            Object idObj = parameter.get("userIds");
                            if (ObjectUtils.isNotEmpty(idObj)) {
                                Long[] ids = (Long[]) idObj;
                                String details = "";
                                for (Long id : ids) {
                                    SysUser sysUser = sysUserMapper.selectUserById(id);
                                    if (sysUser != null) {
                                        details += " [Id:" + sysUser.getUserId() + "  名称:" + sysUser.getNickName() + "] ";
                                    }
                                }
                                actionDetails = actionDetails + details;
                                isOk = true;
                            }
                        }
                    }
                }
                if (description.equals("用户重置密码")) {
                    if (parameter.containsKey("user")) {
                        Object userObj = parameter.get("user");
                        if (ObjectUtils.isNotEmpty(userObj)) {
                            BusiUser busiUser = (BusiUser) userObj;
                            if (busiUser != null && busiUser.getUserId() != null) {
                                SysUser sysUser = sysUserMapper.selectUserById(busiUser.getUserId());
                                if (sysUser != null) {
                                    actionDetails += " [Id:" + sysUser.getUserId() + "  名称:" + sysUser.getNickName() + "] ";
                                    isOk = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (requestURI.contains("/busi/history/downHistory/")) {
            String description = anno.description();
            if (StringUtils.isNotEmpty(description) && description.equals("导出历史会议列表")) {
                if (parameter.containsKey("ids")) {
                    Object idObj = parameter.get("ids");
                    if (ObjectUtils.isNotEmpty(idObj)) {
                        Long[] ids = (Long[]) idObj;
                        String details = "";
                        for (Long id : ids) {
                            BusiHistoryConference busiHistoryConference = busiHistoryConferenceService.selectBusiHistoryConferenceById(id);
                            if (busiHistoryConference != null) {
                                details += " [Id:" + busiHistoryConference.getId() + "  模板名:" + busiHistoryConference.getName() + "] ";
                            }
                        }
                        actionDetails = actionDetails + details;
                        isOk = true;
                    }
                }
            }
        }

        if (requestURI.contains("/busi/conferenceApproval/")) {
            String description = anno.description();
            if (StringUtils.isNotEmpty(description)) {
                if (parameter.containsKey("id")) {
                    Object idObj = parameter.get("id");
                    if (ObjectUtils.isNotEmpty(idObj)) {
                        Long id = (Long) idObj;
                        BusiConferenceApproval busiConferenceApprovalExist = busiConferenceApprovalMapper.selectBusiConferenceApprovalById(id);
                        if (busiConferenceApprovalExist != null) {
                            Long appointmentConferenceId = busiConferenceApprovalExist.getAppointmentConferenceId();
                            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(String.valueOf(appointmentConferenceId)));
                            ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(baseConferenceContext.getMcuType(), baseConferenceContext.getTemplateConferenceId());
                            if (viewTemplateConference != null) {
                                actionDetails = " [Id:" + viewTemplateConference.getId() + "  模板名:" + viewTemplateConference.getName() + "  会议号:" + (StringUtils.isNotEmpty(viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber()) ? viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber() : "空") + "] ";
                                isOk = true;
                            }
                        }
                    }
                }
            }
        }

        if (requestURI.contains("/busi/dept")) {
            String description = anno.description();
            if (StringUtils.isNotEmpty(description) && description.equals("删除部门")) {
                if (parameter.containsKey("deptId")) {
                    Object deptObj = parameter.get("deptId");
                    if (ObjectUtils.isNotEmpty(deptObj)) {
                        Long deptId = (Long) deptObj;
                        SysDept sysDept = SysDeptCache.getInstance().get(deptId);
                        String details = "";
                        if (sysDept != null) {
                            details += " [Id:" + sysDept.getDeptId() + "  名称:" + sysDept.getDeptName() + "] ";
                            actionDetails = actionDetails + details;
                        }
                        isOk = true;
                    }
                }
            }
        }

        Object proceed = null;
        try {
            proceed = point.proceed();
            proceedMap.put("proceed", true);
            proceedMap.put("proceedObj", proceed);
        } catch (Exception e) {
            proceedMap.put("throwable", e);
            if (!(e instanceof CustomException || e instanceof SystemException)) {
                return;
            }
        } catch (Throwable t) {
            proceedMap.put("throwable", t);
            if (!(t instanceof CustomException || t instanceof SystemException)) {
                return;
            }
        }

        if (ObjectUtils.isNotEmpty(proceed) && !isOk) {
            RestResponse restResponse = (RestResponse) proceed;
            if (requestURI.contains("/busi/mcu/all/conferenceAppointment")) {
                String description = anno.description();
                if (StringUtils.isNotEmpty(description)) {
                    if (parameter.containsKey("apConferenceId")) {
                        Object apConferenceIdObj = parameter.get("apConferenceId");
                        if (ObjectUtils.isNotEmpty(apConferenceIdObj)) {
                            String apConferenceId = (String) apConferenceIdObj;
                            ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(apConferenceId);
                            Long id = conferenceIdVo.getId();
                            McuType mcuType = conferenceIdVo.getMcuType();
                            ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType.getCode(), id);
                            String details = "";
                            if (viewConferenceAppointment != null) {
                                ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType.getCode(), viewConferenceAppointment.getTemplateId());
                                if (viewTemplateConference != null) {
                                    details += " [Id:" + viewTemplateConference.getId() + "  模板名:" + viewTemplateConference.getName() + "  会议号:" + (StringUtils.isNotEmpty(viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber()) ? viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber() : "空") + "] ";
                                }
                            }
                            actionDetails = actionDetails + details;
                        }
                    } else {
                        if (parameter.containsKey("jsonObject")) {
                            Object jsonObject = parameter.get("jsonObject");
                            if (ObjectUtils.isNotEmpty(jsonObject)) {
                                Object dataObj = restResponse.getData();
                                if (ObjectUtils.isNotEmpty(dataObj)) {
                                    if (dataObj instanceof Map) {
                                        Map<String, Object> data = (Map<String, Object>) dataObj;
                                        Long templateId = (Long) data.get("templateId");
                                        String mcuType = String.valueOf(data.get("mcuType"));
                                        String details = "";
                                        ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType, templateId);
                                        if (viewTemplateConference != null) {
                                            details += " [Id:" + viewTemplateConference.getId() + "  模板名:" + viewTemplateConference.getName() + "  会议号:" + (StringUtils.isNotEmpty(viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber()) ? viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber() : "空") + "] ";
                                        }
                                        actionDetails = actionDetails + details;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (requestURI.contains("/busi/mcu/all/templateConference")) {
                String description = anno.description();
                if (StringUtils.isNotEmpty(description)) {
                    if (parameter.containsKey("conferenceId")) {
                        Object conferenceIdObj = parameter.get("conferenceId");
                        if (ObjectUtils.isNotEmpty(conferenceIdObj)) {
                            String conferenceId = (String) conferenceIdObj;
                            ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
                            Long id = conferenceIdVo.getId();
                            McuType mcuType = conferenceIdVo.getMcuType();
                            ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType.getCode(), id);
                            if (viewTemplateConference != null && viewTemplateConference.getConferenceNumber() != null) {
                                String conferenceNumber = viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber();
                                actionDetails = "会议号：" + conferenceNumber + "  " + "模板名称：" + viewTemplateConference.getName() + "  " + "id：" + viewTemplateConference.getId() + "  " + actionDetails;
                                BaseConferenceContext mcuZjConferenceContext = AllConferenceContextCache.getInstance().get(conferenceNumber);
                                if (mcuZjConferenceContext != null) {
                                    BusiHistoryConference historyConference = mcuZjConferenceContext.getHistoryConference();
                                    if (historyConference != null) {
                                        historyConferenceId = historyConference.getId();
                                    }
                                }
                            }
                        }
                    } else {
                        if (parameter.containsKey("jsonObj")) {
                            Object jsonObjObj = parameter.get("jsonObj");
                            if (ObjectUtils.isNotEmpty(jsonObjObj)) {
                                Object dataObj = restResponse.getData();
                                if (ObjectUtils.isNotEmpty(dataObj)) {
                                    if (dataObj instanceof ModelBean) {
                                        ModelBean data = (ModelBean) dataObj;
                                        ModelBean templateConference = (ModelBean) data.get("templateConference");
                                        String details = "";
                                        if (templateConference != null) {
                                            Long id = (Long) templateConference.get("id");
                                            String mcuType = String.valueOf(data.get("McuType"));
                                            ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType, id);
                                            if (viewTemplateConference != null) {
                                                details += " [Id:" + viewTemplateConference.getId() + "  模板名:" + viewTemplateConference.getName() + "  会议号:" + (StringUtils.isNotEmpty(viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber()) ? viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber() : "空") + "] ";
                                            }
                                        }
                                        actionDetails = actionDetails + details;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (requestURI.contains("/busi/terminal")) {
                if (anno != null && StringUtils.isNotEmpty(anno.description())) {
                    String description = anno.description();
                    if (description.equals("新增终端")) {
                        Object dataObj = restResponse.getData();
                        if (ObjectUtils.isNotEmpty(dataObj)) {
                            if (dataObj instanceof BusiTerminal) {
                                BusiTerminal busiTerminal = (BusiTerminal) dataObj;
                                if (busiTerminal != null) {
                                    actionDetails += " [Id:" + busiTerminal.getId() + "  名称:" + busiTerminal.getName() + "] ";
                                }
                            }
                        }
                    }
                }
            }

            if (requestURI.contains("/busi/terminalAction")) {
                if (anno != null && StringUtils.isNotEmpty(anno.description())) {
                    String description = anno.description();
                    if (description.equals("注册终端")) {
                        Object dataObj = restResponse.getData();
                        if (ObjectUtils.isNotEmpty(dataObj)) {
                            if (dataObj instanceof BusiTerminal) {
                                BusiTerminal busiTerminal = (BusiTerminal) dataObj;
                                if (busiTerminal != null) {
                                    actionDetails += " [Id:" + busiTerminal.getId() + "  名称:" + busiTerminal.getName() + "] ";
                                }
                            }
                        }
                    }
                }
            }
            if (requestURI.contains("/busi/dept")) {
                String description = anno.description();
                if (StringUtils.isNotEmpty(description) && (description.equals("新增部门") || description.equals("修改部门"))) {
                    if (parameter.containsKey("dept")) {
                        Object deptObj = parameter.get("dept");
                        if (ObjectUtils.isNotEmpty(deptObj)) {
                            SysDept dept = (SysDept) deptObj;
                            if (dept.getDeptId() != null) {
                                String details = "";
                                SysDept sysDept = SysDeptCache.getInstance().get(dept.getDeptId());
                                if (sysDept != null) {
                                    details += " [Id:" + sysDept.getDeptId() + "  名称:" + sysDept.getDeptName() + "] ";
                                    actionDetails = actionDetails + details;
                                }
                                isOk = true;
                            } else {
                                Object dataObj = restResponse.getData();
                                if (ObjectUtils.isNotEmpty(dataObj)) {
                                    if (dataObj instanceof SysDept) {
                                        SysDept sysDept = (SysDept) dataObj;
                                        String details = "";
                                        if (sysDept != null) {
                                            details += " [Id:" + sysDept.getDeptId() + "  名称:" + sysDept.getDeptName() + "] ";
                                            actionDetails = actionDetails + details;
                                        }
                                        isOk = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestURI.contains("/busi/user")) {
                if (anno != null && StringUtils.isNotEmpty(anno.description())) {
                    String description = anno.description();
                    if (description.equals("新增用户")) {
                        if (parameter.containsKey("user")) {
                            Object userObj = parameter.get("user");
                            if (ObjectUtils.isNotEmpty(userObj)) {
                                if (userObj instanceof BusiUser) {
                                    BusiUser busiUser = (BusiUser) userObj;
                                    if (busiUser != null) {
                                        actionDetails += " [Id:" + busiUser.getUserId() + "  名称:" + busiUser.getNickName() + "] ";
                                        isOk = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (requestURI.contains("/busi/mcu/all/conference")) {
                Object conferenceIdObj = null;
                if (anno != null && StringUtils.isNotEmpty(anno.description())) {
                    String description = anno.description();
                    if (description.equals("开始会议")) {
                        Object dataObj = restResponse.getData();
                        if (ObjectUtils.isNotEmpty(dataObj)) {
                            conferenceIdObj = dataObj;
                        }
                    }
                }

                if (parameter.containsKey("conferenceId")) {
                    BaseConferenceContext baseConferenceContext = null;
                    if (ObjectUtils.isNotEmpty(conferenceIdObj)) {
                        String conferenceId = (String) conferenceIdObj;
                        baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceId));
                        if (baseConferenceContext != null) {
                            String conferenceNumber = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber();
                            actionDetails = "会议号：" + conferenceNumber + "  " + actionDetails;
                            BusiHistoryConference historyConference = baseConferenceContext.getHistoryConference();
                            if (historyConference != null) {
                                historyConferenceId = historyConference.getId();
                            }
                        }
                    }
                }
            }

        }

        LoginUser loginUser = SecurityUtils.getLoginUser();

        if (ObjectUtils.isNotEmpty(proceed)) {
            RestResponse restResponse = (RestResponse) proceed;

            if (restResponse.isSuccess()) {
                actionResult = 1;
                actionDetails += ";  操作成功";
            } else {
                if (StringUtils.isNotEmpty(restResponse.getMessage())) {
                    actionDetails += ";  " + restResponse.getMessage();
                } else {
                    actionDetails += ";  " + "操作失败";
                }
            }
        } else {
            String description = anno.description();
            if (description.equals("导出终端列表")) {
                actionDetails += ";  " + "操作成功";
            } else {
                Object throwableObj = proceedMap.get("throwable");
                if (throwableObj != null) {
                    Throwable throwable = (Throwable) proceedMap.get("throwable");
                    if (throwable instanceof CustomException || throwable instanceof SystemException) {
                        actionDetails += ";  " + throwable.getMessage();
                    } else {
                        actionDetails += ";  " + "操作失败";
                    }
                }
            }
        }

        busiOperationLog.setActionDetails(actionDetails);
        busiOperationLog.setHistoryConferenceId(historyConferenceId);

        busiOperationLog.setUserId(loginUser.getUser().getUserId());
        busiOperationLog.setOperatorName(loginUser.getUser().getNickName());
        busiOperationLog.setTime(new Date());
        busiOperationLog.setActionResult(actionResult);
        busiOperationLog.setIp(loginUser.getIpaddr());
        busiOperationLog.setDeviceType(loginUser.getBrowser());

        busiOperationLogService.insertBusiOperationLog(busiOperationLog);
    }

    public Map<String, Object> getParameter(JoinPoint joinPoint) {
        // 打印请求内容
        try {
            // 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
            Object[] objs = joinPoint.getArgs();
            String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames(); // 参数名
            Map<String, Object> paramMap = new HashMap<String, Object>();
            for (int i = 0; i < objs.length; i++) {
                if (!(objs[i] instanceof ExtendedServletRequestDataBinder) && !(objs[i] instanceof HttpServletResponseWrapper)) {
                    paramMap.put(argNames[i], objs[i]);
                }
            }
            if (paramMap.size() > 0) {
                return paramMap;
            }
        } catch (Exception e) {
            log.error("getParameter:", e);
        }
        return null;
    }

}
