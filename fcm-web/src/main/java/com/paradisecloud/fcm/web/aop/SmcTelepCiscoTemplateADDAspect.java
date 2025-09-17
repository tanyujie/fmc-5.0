package com.paradisecloud.fcm.web.aop;


import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.telep.cache.TeleBridgeCache;
import com.paradisecloud.fcm.telep.cache.TelepBridge;
import com.paradisecloud.fcm.telep.dao.model.BusiTele;
import com.paradisecloud.fcm.telep.service.interfaces.IBusiTeleService;
import com.paradisecloud.smc3.model.MeetingRoomCreateReq;
import com.paradisecloud.smc3.model.MeetingRoomResponse;
import com.paradisecloud.smc3.model.SMcipProtocolType;
import com.paradisecloud.smc3.model.TerminalParam;
import com.paradisecloud.smc3.service.interfaces.Smc3Terminalserice;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/2 10:49
 */
@Aspect
@Component
@Slf4j
public class SmcTelepCiscoTemplateADDAspect {


    public static final String MEETINGROOM_NAME_EXIST = "MEETINGROOM_NAME_EXIST";
    public static final String TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST = "TERMINAL_PARAM_LOGIN_SMC_NAME_EXIST";
    @Resource
    private Smc3Terminalserice smc3Terminalserice;

    @Resource
    private IBusiTeleService iBusiTeleService;



    public SmcTelepCiscoTemplateADDAspect() {
    }

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add(..))")
    private void myPointCut() {


    }

    /**
     *  添加MCU
     * @param joinPoint
     */
    @Before("myPointCut()")
    public void before(JoinPoint joinPoint)   {

        Map<String, Object> fieldsName = getFieldsName(joinPoint);

        BusiTerminal terminal = (BusiTerminal)fieldsName.get("busiTerminal");
        if (TerminalType.isMcuTemplateCisco(terminal.getType())){

            String number = terminal.getNumber();
            if(!StringUtils.hasText(number)){
                throw new CustomException("请在号码处输入会议数字ID");
            }

            String credential = terminal.getCredential();
            String ip = terminal.getIp();
            String password = terminal.getPassword();

            if(!StringUtils.hasText(ip)){
                throw new CustomException("IP不能为空");
            }
            if(StringUtils.hasText(credential)&&StringUtils.hasText(password)){
                BusiTele busiTele = new BusiTele();
                busiTele.setIp(ip);
                List<BusiTele> busiTeles = iBusiTeleService.selectBusiTeleList(busiTele);
                if(CollectionUtils.isEmpty(busiTeles)){
                    busiTele.setAdminUsername(credential);
                    busiTele.setAdminPassword(password);
                    busiTele.setStatus(1);
                    busiTele.setCreateTime(new Date());
                    iBusiTeleService.insertBusiTele(busiTele);

                    TelepBridge telepBridge = new TelepBridge(busiTele);
                    TeleBridgeCache.getInstance().update(telepBridge);
                }
                terminal.setCredential(null);
                terminal.setPassword(null);
            }
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
            meetingRoomReq.setDeptId(terminal.getDeptId());

            TerminalParam terminalParam = new TerminalParam();
            BeanUtils.copyProperties(terminal.getTerminalParam(),terminalParam);
            meetingRoomReq.setTerminalParam(terminalParam);
            String res = smc3Terminalserice.addTerminal(meetingRoomReq);

            MeetingRoomResponse meetingRoomResponse = JSON.parseObject(res, MeetingRoomResponse.class);
            if(meetingRoomResponse!=null){
                Map<String, Object> params=new HashMap<>();
                params.put("room_id",meetingRoomResponse.getId());
                params.put("organizationId",terminal.getOrganizationId());
                params.put("serviceZoneId",terminal.getServiceZoneId());
                terminal.setBusinessProperties(params);
                MeetingRoomResponse.TerminalParamDTO terminalParam1 = meetingRoomResponse.getTerminalParam();
                String middleUri = terminalParam1.getMiddleUri();
                //版本升级后无code
                if(terminalParam1!=null){
                    //查询激活码
                    MeetingRoomResponse.TerminalParamDTO.ActiveCodeDTO activeCode = terminalParam1.getActiveCode();
                    if (activeCode != null) {
                        terminal.setCode(activeCode.getCode());
                        params.put("activeCode",activeCode.getCode());
                    }
                }
                terminal.setNumber(middleUri);

            }
            terminal.setOnlineStatus(2);
            Map<String, Object> businessProperties = terminal.getBusinessProperties();
            if(businessProperties==null){
                businessProperties=new HashMap<>();
            }
            businessProperties.put("codeId",meetingRoomResponse.getId());
            businessProperties.put("serviceZoneId",terminal.getServiceZoneId());
            businessProperties.put("areaId",terminal.getAreaId());
            businessProperties.put("terminalParam",terminal.getTerminalParam());
            businessProperties.put("scRegisterAddress",terminal.getTerminalParam().getScRegisterAddress());
            terminal.setBusinessProperties(businessProperties);
        }
        if (TerminalType.isSMCIP(terminal.getType())) {
            if(!StringUtils.hasText(terminal.getAreaId())){
                throw new CustomException("所属区域不能为空");
            }
            if(!StringUtils.hasText(terminal.getOrganizationId())){
                throw new CustomException("所属组织不能为空");
            }
            Map<String, Object> businessProperties = terminal.getBusinessProperties();
            if(businessProperties == null){
                businessProperties = new HashMap<>();
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
            String res = smc3Terminalserice.addTerminal(meetingRoomReq);

            MeetingRoomResponse meetingRoomResponse = JSON.parseObject(res, MeetingRoomResponse.class);
            if(meetingRoomResponse != null){
                businessProperties.put("room_id",meetingRoomResponse.getId());
            }
            terminal.setBusinessProperties(businessProperties);
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
