package com.paradisecloud.fcm.license;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.DateUtil;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/2 11:43
 */
@Aspect
@Component
@Conditional(CheckEnvironmentCondition.class)
public class TerminalTypeAspect {


    public TerminalTypeAspect() {
    }

    @Pointcut("execution(* com.paradisecloud.fcm.web.controller.ops.OpsController.getAllTerminalType(..))")
    private void myPointCut() {


    }

    @Around("myPointCut()")
    public Object  arround(ProceedingJoinPoint joinPoint) throws Exception {
        Object result = null;
        LicenseContent licenseContent = null;
        try {
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
            licenseContent = licenseManager.verify();
        } catch (Exception e) {
            throw new CustomException("当前服务器没在授权范围内,请联系供应商授权后使用");
        }
        Map extra = (Map<String, Object>) licenseContent.getExtra();

        boolean schedule = (boolean) extra.get("schedule");

        if (schedule) {
            String participantLimitTime = (String) extra.get("participantLimitTime");
            long willTime = DateUtil.convertDateByString(participantLimitTime, null).getTime();
            if (System.currentTimeMillis() - willTime >= 0) {
                throw new CustomException("LICENSE到期 ,请联系供应商授权后使用");
            }
        }
        try {
            result = joinPoint.proceed();
        } catch (Throwable Throwable)
        {

        } finally
        {
            if (licenseContent != null) {
                String termianlType = (String) extra.get("termianlType");
                List<Map<String, Object>> res=new ArrayList<>();
                if(result!=null){
                    RestResponse restResponse=(RestResponse)result;
                    List<Map<String, Object>> all = (List<Map<String, Object>>)restResponse.getData();

                    for (Map<String, Object> ObjectMap : all) {

                        Object type = ObjectMap.get("type");
                        if(type!=null){
                            int t=(int)type;
                            if(Strings.isNotBlank(termianlType)){
                                boolean contains = termianlType.contains(String.valueOf(t));
                                if(contains){
                                    res.add(ObjectMap);
                                }
                            }

                        }
                    }
                    return RestResponse.success(res);
                }

            }
        }
      return null;
    }
}
