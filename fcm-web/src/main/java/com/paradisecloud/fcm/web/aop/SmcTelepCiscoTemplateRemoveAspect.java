package com.paradisecloud.fcm.web.aop;


import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author nj
 * @date 2022/8/2 10:49
 */
@Aspect
@Component
@Slf4j
public class SmcTelepCiscoTemplateRemoveAspect {

    private List<String> idsString=new CopyOnWriteArrayList();

    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    public SmcTelepCiscoTemplateRemoveAspect() {
    }

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.remove(..))")
    private void myPointCut() {


    }

    /**
     *  添加MCU
     * @param joinPoint
     */
    @Before("myPointCut()")
    public void before(JoinPoint joinPoint)   {

        Map<String, Object> fieldsName = getFieldsName(joinPoint);

        Long[] ids = (Long[]) fieldsName.get("ids");
        for (Long id : ids) {
            BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(id);
            if (busiTerminal != null)
            {
                if(TerminalType.isSMCSIP(busiTerminal.getType()) || TerminalType.isSMCIP(busiTerminal.getType()) || TerminalType.isFCMSIP(busiTerminal.getType()) || TerminalType.isFSBC(busiTerminal.getType()))

                if(busiTerminal.getBusinessProperties()!=null&&busiTerminal.getBusinessProperties().get("room_id") != null){
                    idsString.add((String)busiTerminal.getBusinessProperties().get("room_id"));
                }
            }
        }

    }

    /**
     *  添加MCU
     * @param joinPoint
     */
    @After("myPointCut()")
    public void after(JoinPoint joinPoint)   {
        if(CollectionUtils.isEmpty(idsString)){
            return;
        }
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(null);
        bridge.getSmcMeetingroomsInvoker().deleteRooms(idsString,bridge.getSmcportalTokenInvoker().getSystemHeaders());
        idsString.clear();
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
