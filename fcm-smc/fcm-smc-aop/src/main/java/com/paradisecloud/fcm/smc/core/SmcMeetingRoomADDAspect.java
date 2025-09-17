package com.paradisecloud.fcm.smc.core;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.DefaultServiceZoneIdRep;
import com.paradisecloud.com.fcm.smc.modle.MeetingRoomCreateReq;
import com.paradisecloud.com.fcm.smc.modle.SMcipProtocolType;
import com.paradisecloud.com.fcm.smc.modle.TerminalParam;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.smc.service.DeviceroutesService;
import com.paradisecloud.smc.service.SmcTerminalserice;
import com.paradisecloud.smc.service.SmcUserService;
import com.paradisecloud.smc.service.impl.SmcServiceZoneId;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author nj
 * @date 2022/8/2 10:49
 */
//@Aspect
//@Component
//@Slf4j
public class SmcMeetingRoomADDAspect {


    public static final String SMC_PASSWORD = "12345@abc";

    public static final String CUSTOMIZE_TERMINAL_TYPE = "CUSTOMIZE_TERMINAL";
    public static final String TRUSTED_ZONE = "TRUSTED_ZONE";
    public static final String KBIT_S = "1920 Kbit/s";
    public static final String MULTIMEDIA_CONF = "MULTIMEDIA_CONF";
    public static final String DEVICE_MANAGE = "DEVICE_MANAGE";
    @Resource
    private SmcTerminalserice smcTerminalserice;

    @Resource
    private DeviceroutesService deviceroutesService;

    @Resource
    private SmcServiceZoneId smcServiceZoneId;
    @Resource
    private SmcUserService smcUserService;



    public SmcMeetingRoomADDAspect() {
    }

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add(..))")
    private void myPointCut() {


    }

    /**
     * 自动创建 添加会议室
     * @param joinPoint
     */
    @Before("myPointCut()")
    public void before(JoinPoint joinPoint)   {



        BusiTerminal terminal = (BusiTerminal)joinPoint.getTarget();
         if (TerminalType.isSMCSIP(terminal.getType())){

             MeetingRoomCreateReq meetingRoomReq = new MeetingRoomCreateReq();
             meetingRoomReq.setName(terminal.getName());
             meetingRoomReq.setProvisionEua("true");

             DefaultServiceZoneIdRep smcServiceZoneId = this.smcServiceZoneId.getSmcServiceZoneId();
             String serviceZoneId = smcServiceZoneId.getContent().get(0).getId();
             String scIpAddress = smcServiceZoneId.getContent().get(0).getScIpAddress();
             UserInfoRep userInfo = smcUserService.getUserInfo();
             String id = userInfo.getAccount().getOrganization().getId();
             meetingRoomReq.setOrganizationId(id);
             meetingRoomReq.setServiceZoneId(serviceZoneId);
             TerminalParam terminalParam = new TerminalParam();
             String deviceroutes = deviceroutesService.getDeviceroutes(serviceZoneId);

             terminalParam.setTerminalType(CUSTOMIZE_TERMINAL_TYPE);
             terminalParam.setMiddleUri(deviceroutes);
             terminalParam.setNwZoneType(TRUSTED_ZONE);
             terminalParam.setRate(KBIT_S);
             terminalParam.setScRegisterAddress(scIpAddress);

             terminalParam.setIpProtocolType(SMcipProtocolType.AUTO.name());
             terminalParam.setLoginScName(terminal.getCredential());
             terminalParam.setLoginScPassword(SMC_PASSWORD);
             terminalParam.setLoginSmcName(terminal.getCredential());
             terminalParam.setLoginSmcPassword(SMC_PASSWORD);

             meetingRoomReq.setTerminalParam(terminalParam);
             ArrayList<String> serviceList = new ArrayList<>();
             serviceList.add(MULTIMEDIA_CONF);
             serviceList.add(DEVICE_MANAGE);
             terminalParam.setServiceList(serviceList);
             smcTerminalserice.addTerminal(meetingRoomReq);
        }

    }
}
