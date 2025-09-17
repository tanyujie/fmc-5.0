package com.paradisecloud.fcm.license;

import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/7/29 10:02
 */
//@Aspect
//@Component
public class MyVerifyAopAspect {

    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    public MyVerifyAopAspect() {

    }

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add(..))")
    private void myPointCut() {
    }

    @Before(value="myPointCut()")
    private void myBefor(JoinPoint joinpoint) throws Exception {
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        LicenseContent licenseContent = licenseManager.verify();

        Map extra = (Map<String,Object>)licenseContent.getExtra();
        boolean schedule = (boolean) extra.get("schedule");
        if(!schedule){
            amountLimit(joinpoint, extra);
        }else {
            String participantLimitTime = (String) extra.get("participantLimitTime");
            Date date = DateUtil.convertDateByString(participantLimitTime, null);
            if(date.getTime()-System.currentTimeMillis()<=0){
                amountLimit(joinpoint, extra);
            }
        }



    }

    private void amountLimit(JoinPoint joinpoint, Map extra) throws NoSuchFieldException, IllegalAccessException {
        int fcmCount;
        int fsbcCount;
        Integer fcmAmount = (Integer) extra.get("fcmAmount");
        Integer fsbcAmount = (Integer) extra.get("fsbcAmount");
        Object param = joinpoint.getArgs()[0];
        Field type = param.getClass().getDeclaredField("type");
        type.setAccessible(true);
        Integer integerType = (Integer)type.get(param);
        BusiTerminal terminal = new BusiTerminal();
        terminal.setBusinessFieldType(BusinessFieldType.COMMON.getValue());
        if(integerType==TerminalType.FCM_SIP.getId()){
            terminal.setType(TerminalType.FCM_SIP.getId());
            List<BusiTerminal> FCMBeans = busiTerminalMapper.selectBusiTerminalList(terminal);
            fcmCount=FCMBeans.size();
            if(fcmCount>=fcmAmount){
                throw new CustomException("已达到FCM账户数量限制上限,请联系供应商重新授权");
            }
        }else {
            terminal.setType(TerminalType.FSBC_SIP.getId());
            List<BusiTerminal> FSBCBeans = busiTerminalMapper.selectBusiTerminalList(terminal);
            fsbcCount=FSBCBeans.size();
            if(fsbcCount>=fsbcAmount){
                throw new CustomException("已达到FSBC账户数量限制上限,请联系供应商重新授权");
            }
        }
    }

}
