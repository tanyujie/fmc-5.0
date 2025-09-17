package com.paradisecloud.fcm.smc.core.template;


import com.paradisecloud.com.fcm.smc.modle.MeetingRoomCreateReq;
import com.paradisecloud.com.fcm.smc.modle.SMcipProtocolType;
import com.paradisecloud.com.fcm.smc.modle.TerminalParam;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.smc.service.SmcTerminalserice;
import com.paradisecloud.smc.service.TemplateService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.annotation.Resource;

public class SmcTemplateAddAspect {


    @Resource
    private TemplateService templateService;

    public SmcTemplateAddAspect() {
    }

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add(..))")
    private void myPointCut() {


    }

    @Before("myPointCut()")
    public void before(JoinPoint joinPoint) throws Exception {



        BusiTerminal terminal = (BusiTerminal)joinPoint.getTarget();
        if (TerminalType.isSMCSIP(terminal.getType())){

            MeetingRoomCreateReq meetingRoomReq = new MeetingRoomCreateReq();
            meetingRoomReq.setName(terminal.getName());
            meetingRoomReq.setProvisionEua("true");
            TerminalParam terminalParam = new TerminalParam();

            terminalParam.setTerminalType(SMcipProtocolType.SIP.name());
            terminalParam.setMiddleUri("");
            meetingRoomReq.setTerminalParam(terminalParam);

        }

    }
}
