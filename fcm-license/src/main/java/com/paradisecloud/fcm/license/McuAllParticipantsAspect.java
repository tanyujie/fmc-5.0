package com.paradisecloud.fcm.license;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.smc.service.IBusiSmcHistoryConferenceService;
import com.paradisecloud.smc.service.SmcParticipantsService;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2023/4/17 14:52
 */
@Aspect
@Component
@Conditional(CheckEnvironmentCondition.class)
public class McuAllParticipantsAspect {

    @Resource
    private SmcParticipantsService smcParticipantsService;

    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;

    public McuAllParticipantsAspect() {
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

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.mcu.all.AttendeeForAllController.invite(..))" +
            "|| execution(* com.paradisecloud.fcm.web.controller.mcu.all.AttendeeForAllController.batchInvite(..))"+
            "|| execution(*  com.paradisecloud.fcm.web.controller.mobile.web.MobileWebAttendeeForAllController.batchInvite(..))"+
            "|| execution(*  com.paradisecloud.fcm.web.controller.mobile.web.MobileWebAttendeeForAllController.invite(..))"
    )
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
        if (schedule) {
            String participantLimitTime = (String) extra.get("participantLimitTime");
            long willTime = DateUtil.convertDateByString(participantLimitTime, null).getTime();
            if (System.currentTimeMillis() - willTime >= 0) {
                throw new CustomException("LICENSE到期 ,请联系供应商授权后使用");
            }
        }

        Map<String, Object> fieldsName = getFieldsName(joinPoint);
        Object conferenceId = fieldsName.get("conferenceId");
        if(conferenceId!=null){
            String contextKey = EncryptIdUtil.parasToContextKey((String) conferenceId);
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            if (baseConferenceContext != null) {
                int total=0;
                BaseAttendee masterAttendee = baseConferenceContext.getMasterAttendee();
                if(masterAttendee!=null){
                    total++;
                }
                List attendees = baseConferenceContext.getAttendees();
                if(!CollectionUtils.isEmpty(attendees)){
                    total=total+attendees.size();
                }
                List masterAttendees = baseConferenceContext.getMasterAttendees();
                if(!CollectionUtils.isEmpty(masterAttendees)){
                    total=total+masterAttendees.size();
                }

                if(baseConferenceContext instanceof ConferenceContext){
                    ConferenceContext conferenceContext=(ConferenceContext)baseConferenceContext;

                    Map<String, Object> businessProperties = conferenceContext.getBusinessProperties();
                    if(businessProperties!=null){
                        Object quality = businessProperties.get("quality");
                        if(quality!=null){
                            String  qualityStr = quality.toString();
                            if(qualityStr.contains("1080")){
                                participantLimit=participantLimit/2;
                            }
                        }
                    }

                }

                Object attendeeList_Obj = fieldsName.get("attendeeList");
                if(attendeeList_Obj!=null){
                    List<Object> attendeeList =  (List<Object>)fieldsName.get("attendeeList");
                    if(!CollectionUtils.isEmpty(attendeeList)){
                        if(attendeeList.size()+total>participantLimit){
                            throw new CustomException("超过与会者数量：" + participantLimit + ",请联系供应商授权后使用");
                        }
                    }
                }

                if (total > participantLimit - 1) {
                    throw new CustomException("超过与会者数量：" + participantLimit + ",请联系供应商授权后使用");
                }
            }

        }


    }
}
