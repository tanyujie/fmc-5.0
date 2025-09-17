package com.paradisecloud.fcm.license;

import com.paradisecloud.com.fcm.smc.modle.ParticipantReqDto;
import com.paradisecloud.com.fcm.smc.modle.mix.CreateParticipantsReq;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.service.IBusiSmcHistoryConferenceService;
import com.paradisecloud.smc.service.SmcParticipantsService;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
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
 * @date 2023/4/17 14:52
 */
//@Aspect
//@Component
//@Conditional(CheckEnvironmentCondition.class)
public class SmcParticipantsAspect {

    @Resource
    private SmcParticipantsService smcParticipantsService;

    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;

    public SmcParticipantsAspect() {
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

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.smc.SmcParticipantsController.addParticipants(..))|| execution(* com.paradisecloud.fcm.web.controller.smc.SmcParticipantsController.batchInviteParticipants(..))")
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
        int limit = (int) extra.get("smcParticipantLimit");
        Map<String, Object> fieldsName = getFieldsName(joinPoint);
        CreateParticipantsReq createParticipantsReq = (CreateParticipantsReq) fieldsName.get("createParticipantsReq");
        List<ParticipantReqDto> participants = createParticipantsReq.getParticipants();
        if (!CollectionUtils.isEmpty(participants)&&participants.size()>0) {
            AtomicInteger atomicInteger = new AtomicInteger();
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
                            if (atomicInteger.get() + participants.size() > limit) {
                                throw new CustomException("license数量限制 ,请联系供应商授权后使用");
                            }
                        });
                    }
                }
            }
        }


    }
}
