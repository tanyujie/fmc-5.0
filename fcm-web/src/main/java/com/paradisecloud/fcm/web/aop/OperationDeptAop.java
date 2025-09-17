package com.paradisecloud.fcm.web.aop;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.dao.mapper.BusiPickerMapper;
import com.paradisecloud.fcm.dao.model.BusiPicker;
import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
import com.paradisecloud.system.dao.mapper.SysConfigMapper;
import com.paradisecloud.system.dao.model.SysConfig;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Aspect
@Component
public class OperationDeptAop {

    protected final Logger log = LoggerFactory.getLogger(BaseController.class);



    @Pointcut("@annotation(io.swagger.v3.oas.annotations.Operation)")
    public void operation() {
    }

    @Around("operation()")
    public Object opDepIt(ProceedingJoinPoint point) throws Throwable {
        List<Integer> deptIds = new ArrayList<>();
        try {
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            HttpServletRequest request = sra.getRequest();
            String requestURI = request.getRequestURI();

            BusiPickerMapper busiPickerMapper = BeanFactory.getBean(BusiPickerMapper.class);
            BusiPicker busiPicker1 = new BusiPicker();
            busiPicker1.setDeptType("API");
            List<BusiPicker> busiPickers = busiPickerMapper.selectBusiPickerList(busiPicker1);
            if (CollectionUtils.isNotEmpty(busiPickers)) {
                for (BusiPicker busiPicker : busiPickers) {
                    deptIds.add(busiPicker.getDeptId());
                }
            }
            if (CollectionUtils.isNotEmpty(deptIds)) {
                SysConfigMapper sysConfigMapper = BeanFactory.getBean(SysConfigMapper.class);
                SysConfig sysConfig_q = new SysConfig();
                sysConfig_q.setConfigKey("conference.pick.enable");
                SysConfig sysConfig = sysConfigMapper.selectConfig(sysConfig_q);
                if (sysConfig != null && sysConfig.getConfigValue().equals("1")) {
                    Long deptId = AuthenticationUtil.getDeptId();
                    if(deptId!=null){
                        if (deptIds.contains(deptId.intValue())) {
                            Random random = new Random();
                            int delay = 1 + random.nextInt(10);
                            if (requestURI.contains("/busi/mcu/all/attendee/changeMaster") || requestURI.contains("/busi/mcu/all/mulitiPicPoll/chairman/participantMultiPicPoll/operate")) {
                               if(delay>5){
                                 return null;
                               }
                            }
                            Threads.sleep(delay * 1000);
                        }
                    }

                }
            }
        } catch (Exception e) {

        }

        return point.proceed();
    }


}
