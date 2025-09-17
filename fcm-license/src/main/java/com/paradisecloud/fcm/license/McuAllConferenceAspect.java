package com.paradisecloud.fcm.license;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.smc.dao.model.BusiSmcDeptTemplate;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.dao.model.SmcTemplateTerminal;
import com.paradisecloud.smc.service.BusiSmcDeptTemplateService;
import com.paradisecloud.smc.service.IBusiSmcHistoryConferenceService;
import com.paradisecloud.smc.service.SmcParticipantsService;
import com.paradisecloud.smc.service.SmcTemplateTerminalService;
import com.sinhy.spring.BeanFactory;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2023/4/14 16:28
 */
@Aspect
@Component
@Conditional(CheckEnvironmentCondition.class)
public class McuAllConferenceAspect {

    @Resource
    private BusiSmcDeptTemplateService busiSmcDeptTemplateService;


    @Resource
    private SmcTemplateTerminalService smcTemplateTerminalService;

    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;

    @Resource
    private SmcParticipantsService smcParticipantsService;

    public McuAllConferenceAspect() {
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

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.mcu.all.BusiAllConferenceController.startByTemplate(..))" +
            "||execution(* com.paradisecloud.fcm.web.controller.mobile.web.MobileWebAllConferenceController.startByTemplate(..))" +
            "||execution(* com.paradisecloud.fcm.web.controller.mobile.MobileConferenceAppointmentController.add(..))" +
            "||execution(* com.paradisecloud.fcm.web.controller.mobile.web.MobileNewWebConferenceAppointmentController.add(..))" +
            "||execution(* com.paradisecloud.fcm.web.controller.mcu.all.BusiAllConferenceAppointmentController.add(..))" +
            "||execution(* com.paradisecloud.fcm.web.controller.mobile.MobileTemplateConferenceController.add(..))")
    private void myPointCut() {


    }

    @Before("myPointCut()")
    public void before(JoinPoint joinPoint) throws Exception {

        LicenseContent licenseContent = null;
        try {
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
            licenseContent = licenseManager.verify();
        } catch (Exception e) {
            throw new CustomException("当前服务器没在授权范围内,请联系供应商授权后使用");
        }

        Map extra = (Map<String, Object>) licenseContent.getExtra();
        boolean schedule = (boolean) extra.get("schedule");
        int participantLimit = (int) extra.get("participantLimit");
        int conferenceLimit = (int) extra.get("conferenceLimit");
        if (schedule) {
            String participantLimitTime = (String) extra.get("participantLimitTime");
            long willTime = DateUtil.convertDateByString(participantLimitTime, null).getTime();
            if (System.currentTimeMillis() - willTime >= 0) {
                throw new CustomException("LICENSE到期 ,请联系供应商授权后使用");
            }
        }
        int count = 0;
        int countAttendee = 0;
        Collection<BaseConferenceContext> values = AllConferenceContextCache.getInstance().values();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(values)) {
            for (BaseConferenceContext value : values) {
                if (value.isStart()) {
                    List attendees = value.getAttendees();
                    List masterAttendees = value.getMasterAttendees();
                    BaseAttendee masterAttendee = value.getMasterAttendee();
                    if(masterAttendee!=null){
                        countAttendee=countAttendee+1;
                    }
                    countAttendee= countAttendee+attendees.size();
                    countAttendee= countAttendee+masterAttendees.size();
                    count++;
                }
                if (count > conferenceLimit - 1) {
                    throw new CustomException("超过会议数量：" + conferenceLimit + ",请联系供应商授权后使用");
                }

            }
        }
        //预约会议限制
        Map<String, Object> fieldsName = getFieldsName(joinPoint);
        Map<String, Object> jsonObject = (Map<String, Object>) fieldsName.get("jsonObject");
        if (jsonObject != null) {
            Map<String, Object> params = (Map<String, Object>) jsonObject.get("params");
            if (params != null) {
                Object templateParticipants = params.get("templateParticipants");
//                Object businessProperties = params.get("businessProperties");
//                if (businessProperties != null) {
//                    if (businessProperties instanceof HashMap) {
//                        HashMap<String, Object> businessPropertiesMap = (HashMap<String, Object>) businessProperties;
//                        Object quality = businessPropertiesMap.get("quality");
//                        if (quality != null) {
//                            if (quality.toString().contains("1080")) {
//                                participantLimit = participantLimit / 2;
//                            }
//                        }
//
//                    }
//                }
                if (templateParticipants != null) {
                    // 转换为 JSONArray
                    JSONArray jsonArray = null;
                    if (templateParticipants instanceof JSONArray) {
                        jsonArray = (JSONArray) templateParticipants;
                        int size = jsonArray.size();
                        if (size+countAttendee > participantLimit) {
                            throw new CustomException("会议与会者："+countAttendee+"邀请数量"+size+"超过会议方数:" + participantLimit + ",请联系供应商授权后使用");
                        }

                    }
                    if (templateParticipants instanceof ArrayList) {
                        int size= ((ArrayList<?>) templateParticipants).size();
                        if (size+countAttendee > participantLimit) {
                            throw new CustomException("会议与会者："+countAttendee+"邀请数量"+size+"超过会议方数:" + participantLimit + ",请联系供应商授权后使用");
                        }

                    }
                }
            }
        }


    }

    //@AfterReturning(pointcut  = "myPointCut()",returning = "result")
    public void aspectAfterReturning(JoinPoint joinPoint, Object result) {

        BusiOpsInfoMapper busiOpsInfoMapper = BeanFactory.getBean(BusiOpsInfoMapper.class);
        BusiOpsInfo busiOpsInfo = new BusiOpsInfo();

        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(busiOpsInfo);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(busiOpsInfos)) {
            return;
        }
        BusiOpsInfo busiOpsInfo1 = busiOpsInfos.get(0);
        String fmeIp = busiOpsInfo1.getFmeIp();
        if (Strings.isBlank(fmeIp)) {
            return;
        }

        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByBridgeAddressOnly(fmeIp);

        if (fmeBridge == null) {
            return;
        }
        Collection<BaseConferenceContext> values = AllConferenceContextCache.getInstance().values();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(values)) {
            for (BaseConferenceContext value : values) {
                if (value.isStart()) {


                }


            }
        }
    }

    private void limitTerminal(JoinPoint joinPoint, int limit) {
        Map<String, Object> fieldsName = getFieldsName(joinPoint);
        Long id = (Long) fieldsName.get("id");
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplateById(id);
        AtomicInteger atomicInteger = new AtomicInteger();
        if (busiSmcDeptTemplate != null) {
            List<SmcTemplateTerminal> templateTerminals = smcTemplateTerminalService.list(busiSmcDeptTemplate.getSmcTemplateId());
            //级联处理
            if (!CollectionUtils.isEmpty(templateTerminals)) {
                templateTerminals.stream().forEach(s -> {
                    BusiTerminal terminal = TerminalCache.getInstance().get(s.getTerminalId());
                    Integer onlineStatus = terminal.getOnlineStatus();
                    atomicInteger.incrementAndGet();
                });
            }
        }

        BusiSmcHistoryConference historyConference = new BusiSmcHistoryConference();
        historyConference.setEndStatus(2);
        List<BusiSmcHistoryConference> busiSmcHistoryConferences = smcHistoryConferenceService.selectBusiSmcHistoryConferenceList(historyConference);
        if (!CollectionUtils.isEmpty(busiSmcHistoryConferences)) {
            // 活跃会议室的数量
            for (BusiSmcHistoryConference busiSmcHistoryConference : busiSmcHistoryConferences) {
                String conferenceId = busiSmcHistoryConference.getConferenceId();
                SmcParitipantsStateRep conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10000);

                List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();
                if (!CollectionUtils.isEmpty(content)) {
                    content.stream().forEach(m -> {
                        if (Objects.equals(m.getState().getOnline(), Boolean.TRUE)) {
                            atomicInteger.incrementAndGet();
                        }
                        if (atomicInteger.get() > limit) {
                            throw new CustomException("license数量限制 ,请联系供应商授权后使用");
                        }
                    });
                }
            }
        }

        int count = atomicInteger.get();
        if (count > limit) {
            throw new CustomException("license数量限制 ,请联系供应商授权后使用");
        }
    }
}
