package com.paradisecloud.fcm.web.aop;


import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.smc3.model.MeetingRoomCreateReq;
import com.paradisecloud.smc3.model.MeetingRoomResponse;
import com.paradisecloud.smc3.model.SMcipProtocolType;
import com.paradisecloud.smc3.model.TerminalParam;
import com.paradisecloud.smc3.service.interfaces.Smc3DeviceroutesService;
import com.paradisecloud.smc3.service.interfaces.Smc3ServiceZoneId;
import com.paradisecloud.smc3.service.interfaces.Smc3Terminalserice;
import com.paradisecloud.smc3.service.interfaces.Smc3UserService;
import com.sinhy.spring.BeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/2 10:49
 */
@Aspect
@Component
@Slf4j
public class SmcTelepCiscoTemplateUpdateAspect {

    public static final String MEETINGROOM_NAME_EXIST = "MEETINGROOM_NAME_EXIST";
    public static final String TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST = "TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST";
    @Resource
    private Smc3Terminalserice smc3Terminalserice;

    @Resource
    private Smc3DeviceroutesService smc3DeviceroutesService;

    @Resource
    private Smc3ServiceZoneId smc3ServiceZoneId;
    @Resource
    private Smc3UserService smc3UserService;


    public SmcTelepCiscoTemplateUpdateAspect() {
    }

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.edit(..))")
    private void myPointCut() {


    }

    /**
     *  添加MCU
     * @param joinPoint
     */
    @After("myPointCut()")
    public void after(JoinPoint joinPoint)   {

        Map<String, Object> fieldsName = getFieldsName(joinPoint);

        BusiTerminal terminal = (BusiTerminal) fieldsName.get("busiTerminal");
        Map<String, Object> businessProperties = terminal.getBusinessProperties();
        if(businessProperties == null){
            businessProperties = new HashMap<>();
        }
        if (TerminalType.isSMCSIP(terminal.getType())) {
            if(!StringUtils.hasText(terminal.getAreaId())){
                throw new CustomException("所属区域不能为空");
            }
            if(!StringUtils.hasText(terminal.getOrganizationId())){
                throw new CustomException("所属组织不能为空");
            }
            MeetingRoomCreateReq meetingRoomReq = new MeetingRoomCreateReq();
            meetingRoomReq.setName(terminal.getName());
            meetingRoomReq.setProvisionEua("true");
            meetingRoomReq.setServiceZoneId(terminal.getServiceZoneId());
            meetingRoomReq.setAreaId(terminal.getAreaId());
            meetingRoomReq.setOrganizationId(terminal.getOrganizationId());

            TerminalParam terminalParam = new TerminalParam();
            BeanUtils.copyProperties(terminal.getTerminalParam(),terminalParam);
            meetingRoomReq.setTerminalParam(terminalParam);

            meetingRoomReq.setId((String)businessProperties.get("room_id"));
            MeetingRoomResponse meetingRoomResponse = smc3Terminalserice.update(meetingRoomReq);

            if(meetingRoomResponse != null){
                businessProperties.put("room_id", meetingRoomResponse.getId());
                businessProperties.put("serviceZoneId", meetingRoomResponse.getServiceZoneId());
                businessProperties.put("areaId", meetingRoomResponse.getAreaId());
                businessProperties.put("organizationId", meetingRoomResponse.getOrganizationId());
                businessProperties.put("terminalParam", terminal.getTerminalParam());
                MeetingRoomResponse.TerminalParamDTO terminalParam1 = meetingRoomResponse.getTerminalParam();
                String middleUri = terminalParam1.getMiddleUri();
                //版本升级后无code
                if(terminalParam1!=null){


                    MeetingRoomResponse.TerminalParamDTO.ActiveCodeDTO activeCode = terminalParam1.getActiveCode();
                    if (activeCode != null) {
                        terminal.setRemarks(activeCode.getCode());
                    }
                }
                terminal.setNumber(middleUri);

            }

            terminal.setBusinessProperties(businessProperties);
            BusiTerminalMapper bean = BeanFactory.getBean(BusiTerminalMapper.class);
            bean.updateBusiTerminal(terminal);

        }
        if (TerminalType.isSMCIP(terminal.getType())) {
            if(!StringUtils.hasText(terminal.getAreaId())){
                throw new CustomException("所属区域不能为空");
            }
            if(!StringUtils.hasText(terminal.getOrganizationId())){
                throw new CustomException("所属组织不能为空");
            }
            businessProperties.put("areaId", terminal.getAreaId());
            businessProperties.put("organizationId", terminal.getOrganizationId());
            MeetingRoomCreateReq meetingRoomReq = new MeetingRoomCreateReq();
            meetingRoomReq.setName(terminal.getName());
            meetingRoomReq.setProvisionEua("true");
            meetingRoomReq.setAreaId(terminal.getAreaId());
            meetingRoomReq.setOrganizationId(terminal.getOrganizationId());

            TerminalParam terminalParam = new TerminalParam();
            terminalParam.setTerminalType("vhd");
            terminalParam.setMiddleUri(terminal.getIp());
            terminalParam.setRate("1920 Kbit/s");
            terminalParam.setSecurityLevel("PUBLIC");
            terminalParam.setIpProtocolType(SMcipProtocolType.AUTO.name());
            meetingRoomReq.setTerminalParam(terminalParam);
            MeetingRoomResponse meetingRoomResponse = null;
            Object roomIdObj = businessProperties.get("room_id");
            if (roomIdObj != null) {
                meetingRoomReq.setId((String) roomIdObj);
                meetingRoomResponse = smc3Terminalserice.update(meetingRoomReq);
            } else {
                String res = smc3Terminalserice.addTerminal(meetingRoomReq);
                meetingRoomResponse = JSON.parseObject(res, MeetingRoomResponse.class);
            }

            if(meetingRoomResponse != null){
                businessProperties.put("room_id", meetingRoomResponse.getId());
                businessProperties.put("areaId", terminal.getAreaId());
                businessProperties.put("organizationId", meetingRoomResponse.getOrganizationId());
            }
            terminal.setBusinessProperties(businessProperties);
            BusiTerminalMapper bean = BeanFactory.getBean(BusiTerminalMapper.class);
            bean.updateBusiTerminal(terminal);
        }

    }

    private static Map<String, Object> getFieldsName(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = pnd.getParameterNames(method);
        Map<String, Object> paramMap = new HashMap<>(32);
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args[i]);
        }
        return paramMap;
    }

}
