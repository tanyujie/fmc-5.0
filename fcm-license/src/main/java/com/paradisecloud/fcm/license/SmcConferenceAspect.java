package com.paradisecloud.fcm.license;

import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.smc.dao.model.BusiSmcDeptTemplate;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.dao.model.SmcTemplateTerminal;
import com.paradisecloud.smc.service.BusiSmcDeptTemplateService;
import com.paradisecloud.smc.service.IBusiSmcHistoryConferenceService;
import com.paradisecloud.smc.service.SmcParticipantsService;
import com.paradisecloud.smc.service.SmcTemplateTerminalService;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2023/4/14 16:28
 */
@Aspect
@Component
@Conditional(CheckEnvironmentCondition.class)
public class SmcConferenceAspect {

    @Resource
    private BusiSmcDeptTemplateService busiSmcDeptTemplateService;


    @Resource
    private SmcTemplateTerminalService smcTemplateTerminalService;

    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;

    @Resource
    private SmcParticipantsService smcParticipantsService;

    public SmcConferenceAspect() {
    }
    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.cascade.SmcCascadeConferencesController.startConferenceTemplateLocal(..))||execution(* com.paradisecloud.fcm.web.controller.cascade.SmcCascadeConferencesController.getCurrentConferenceInfoByLocalId(..))")
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
        boolean smcSchedule = (boolean) extra.get("smcSchedule");
        int limit = (int)extra.get("smcParticipantLimit");
        if (smcSchedule) {
            String participantLimitTime = (String) extra.get("smcParticipantLimitTime");
            long willTime = DateUtil.convertDateByString(participantLimitTime, null).getTime();
            if (System.currentTimeMillis() - willTime >= 0) {
                throw new CustomException("LICENSE到期 ,请联系供应商授权后使用");
            }
        } else {
            throw new CustomException("未授权 ,请联系供应商授权后使用");
        }



    }

    private void limitTerminal(JoinPoint joinPoint, int limit) {
        Map<String, Object> fieldsName = getFieldsName(joinPoint);
        Long id = (Long)fieldsName.get("id");
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplateById(id);
        AtomicInteger atomicInteger=new AtomicInteger();
        if(busiSmcDeptTemplate!=null){
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
        if(!CollectionUtils.isEmpty(busiSmcHistoryConferences)){
            // 活跃会议室的数量
            for (BusiSmcHistoryConference busiSmcHistoryConference : busiSmcHistoryConferences) {
                String conferenceId = busiSmcHistoryConference.getConferenceId();
                SmcParitipantsStateRep conferencesParticipantsState = smcParticipantsService.getConferencesParticipantsState(conferenceId, 0, 10000);

                List<SmcParitipantsStateRep.ContentDTO> content = conferencesParticipantsState.getContent();
                if (!CollectionUtils.isEmpty(content)) {
                    content.stream().forEach(m->{
                        if(Objects.equals(m.getState().getOnline(), Boolean.TRUE)){
                            atomicInteger.incrementAndGet();
                        }
                        if( atomicInteger.get()>limit){
                            throw new CustomException("license数量限制 ,请联系供应商授权后使用");
                        }
                    });
                }
            }
        }

        int count = atomicInteger.get();
        if(count> limit){
            throw new CustomException("license数量限制 ,请联系供应商授权后使用");
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
