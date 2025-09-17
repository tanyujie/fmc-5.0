package com.paradisecloud.fcm.license;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/2 11:43
 */
@Aspect
@Component
@Conditional(CheckEnvironmentCondition.class)
public class LiveAspect {





    public LiveAspect() {
    }

    @Pointcut("execution(* com.paradisecloud.fcm.web.controller.mcu.all.BusiAllConferenceController.stream(..))||execution(* com.paradisecloud.fcm.web.controller.mobile.MobileBusiConferenceController.stream(..))||execution(* com.paradisecloud.fcm.web.controller.mobile.web.MobileWebAllConferenceController.stream(..))")
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

        Map extra = (Map<String,Object>)licenseContent.getExtra();
        boolean schedule = (boolean) extra.get("schedule");
        int limit = (int)extra.get("liveLimit");
        if (schedule) {
            String participantLimitTime = (String) extra.get("participantLimitTime");
            long willTime = DateUtil.convertDateByString(participantLimitTime, null).getTime();
            if (System.currentTimeMillis() - willTime >= 0) {
                throw new CustomException("LICENSE到期 ,请联系供应商授权后使用");
            }
        }
//        if(extra.get("cloudLive")!=null){
//            boolean cloudLive = (boolean) extra.get("cloudLive");
//            if(!cloudLive){
//                Map<String, Object> fieldsName = getFieldsName(joinPoint);
//                Object conferenceId = fieldsName.get("conferenceId");
//                if(conferenceId!=null){
//                    String contextKey = EncryptIdUtil.parasToContextKey((String) conferenceId);
//                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
//                    if (baseConferenceContext != null) {
//                        Integer streamingEnabled = baseConferenceContext.getStreamingEnabled();
//                        if(streamingEnabled==3){
//                            throw new CustomException("不能开启云直播 ,请联系供应商授权后使用");
//                        }
//
//                    }
//                }
//            }
//        }



        Collection<BaseConferenceContext> values = AllConferenceContextCache.getInstance().values();
        Integer count = 0;
        if(CollectionUtils.isNotEmpty(values)){
            for (BaseConferenceContext value : values) {
                Integer liveTerminalCount = value.getLiveTerminalCount();
                if(liveTerminalCount != null ){
                    count += liveTerminalCount;
                }
            }

        }
        if(count >= limit){
            throw new CustomException("LICENSE并发数达到上限,请联系供应商授权后使用");
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
