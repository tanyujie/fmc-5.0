/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ExtendMinutes.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:28
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.common.enumer.AppointmentConferenceRepeatRate;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.exception.SystemException;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;

/**  
 * <pre>请加上该类的描述</pre>
 * @author sinhy
 * @since 2021-09-18 11:28
 * @version V1.0  
 */
public class ExtendMinutes extends ProxyMethod
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:28 
     * @param method 
     */
    public ExtendMinutes(Method method)
    {
        super(method);
    }
    
    public BusiConferenceAppointment extendMinutes(String conferenceId, int minutes)
    {
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
        if (cc == null) {
            throw new CustomException("会议不存在或者未开始");
        }
        BusiConferenceAppointment conferenceAppointment = cc.getConferenceAppointment();
        if (conferenceAppointment != null)
        {
            conferenceAppointment = BeanFactory.getBean(BusiConferenceAppointmentMapper.class).selectBusiConferenceAppointmentById(conferenceAppointment.getId());
            if (conferenceAppointment != null)
            {
                conferenceAppointment.setExtendMinutes(conferenceAppointment.getExtendMinutes() != null ? (conferenceAppointment.getExtendMinutes() + minutes) : minutes);
                AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(conferenceAppointment.getRepeatRate());
                Date end = null;
                if (rr == AppointmentConferenceRepeatRate.CUSTOM)
                {
                    end = DateUtils.convertToDate(conferenceAppointment.getEndTime());
                }
                else
                {
                    String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                    end = DateUtils.convertToDate(today + " " + conferenceAppointment.getEndTime());
                }
                
                if (conferenceAppointment.getExtendMinutes() != null)
                {
                    end = DateUtils.getDiffDate(end, conferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }
                
                BusiConferenceAppointment con = new BusiConferenceAppointment();
                con.setTemplateId(conferenceAppointment.getTemplateId());
                List<BusiConferenceAppointment> cas = BeanFactory.getBean(BusiConferenceAppointmentMapper.class).selectBusiConferenceAppointmentList(con);
                if (!ObjectUtils.isEmpty(cas))
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String endTime = sdf.format(end);
                    for (BusiConferenceAppointment busiConferenceAppointment2 : cas)
                    {
                        if (busiConferenceAppointment2.getId().longValue() != conferenceAppointment.getId().longValue())
                        {
                            if (endTime.compareTo(busiConferenceAppointment2.getStartTime()) >= 0 && endTime.compareTo(busiConferenceAppointment2.getEndTime()) <= 0)
                            {
                                throw new SystemException(1008435, "延长会议结束时间失败：延长后的结束时间已存在相同模板的预约会议！");
                            }
                        }
                    }
                }
                
                BeanFactory.getBean(BusiConferenceAppointmentMapper.class).updateBusiConferenceAppointment(conferenceAppointment);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, "会议结束时间延长至：" + DateUtils.formatTo(DateTimeFormatPattern.PATTERN_11, end));
            }
        }else {
            Long templateConferenceId = cc.getTemplateConferenceId();
            BusiTemplateConference tc = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(templateConferenceId);
            Integer durationTime = tc.getDurationTime();
            tc.setDurationTime(durationTime+minutes);
            BeanFactory.getBean(BusiTemplateConferenceMapper.class).updateBusiTemplateConference(tc);
            cc.setDurationTime(durationTime+minutes);
        }

        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(cc);
        return conferenceAppointment;
    }
}
