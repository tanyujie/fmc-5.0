package com.paradisecloud.fcm.mqtt.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiDingConferenceService;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingConferenceAppointmentService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudConferenceAppointmentService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudTemplateConferenceService;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplateConferenceService;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiFmqAllConferenceAppointmentService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.task.EndDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.conference.task.StartDownCascadeConferenceTask;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.service.task.NotifyTask;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2ConferenceAppointmentService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2TemplateConferenceService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentConferenceAppointmentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentTemplateConferenceService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.model.request.TemplateNode;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3ConferenceAppointmentService;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3TemplateConferenceService;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BusiFmqAllConferenceAppointmentServiceImpl implements IBusiFmqAllConferenceAppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(BusiFmqAllConferenceAppointmentServiceImpl.class);

    @Resource
    private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;
    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;
    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;
    @Resource
    private IBusiMcuZjConferenceAppointmentService busiMcuZjConferenceAppointmentService;
    @Resource
    private IBusiMcuZjConferenceService busiMcuZjConferenceService;
    @Resource
    private IBusiMcuPlcConferenceAppointmentService busiMcuPlcConferenceAppointmentService;
    @Resource
    private IBusiMcuPlcConferenceService busiMcuPlcConferenceService;
    @Resource
    private IBusiMcuKdcConferenceAppointmentService busiMcuKdcConferenceAppointmentService;
    @Resource
    private IBusiMcuDingConferenceAppointmentService busiMcuDingConferenceAppointmentService;
    @Resource
    private IBusiMcuKdcConferenceService busiMcuKdcConferenceService;
    @Resource
    private IBusiMcuSmc3ConferenceAppointmentService busiMcuSmc3ConferenceAppointmentService;
    @Resource
    private IBusiSmc3ConferenceService busiSmc3ConferenceService;
    @Resource
    private IBusiMcuSmc2ConferenceAppointmentService busiMcuSmc2ConferenceAppointmentService;
    @Resource
    private IBusiSmc2ConferenceService busiSmc2ConferenceService;
    @Resource
    private IBusiTencentConferenceService busiTencentConferenceService;
    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;
    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    @Resource
    private IBusiMcuZjTemplateConferenceService busiMcuZjTemplateConferenceService;
    @Resource
    private IBusiMcuPlcTemplateConferenceService busiMcuPlcTemplateConferenceService;
    @Resource
    private IBusiMcuKdcTemplateConferenceService busiMcuKdcTemplateConferenceService;
    @Resource
    private IBusiMcuSmc3TemplateConferenceService busiMcuSmc3TemplateConferenceService;
    @Resource
    private IBusiMcuSmc2TemplateConferenceService busiMcuSmc2TemplateConferenceService;
    @Resource
    private IBusiMcuTencentTemplateConferenceService busiMcuTencentTemplateConferenceService;
    @Resource
    private IBusiMcuHwcloudConferenceAppointmentService busiMcuHwcloudConferenceAppointmentService;
    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;
    @Resource
    private BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper;
    @Resource
    private BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper;
    @Resource
    private BusiMcuDingTemplateConferenceMapper busiMcuDingTemplateConferenceMapper;
    @Resource
    private TaskService taskService;
    @Resource
    private ViewTemplateParticipantMapper viewTemplateParticipantMapper;
    @Resource
    private IBusiMcuTencentConferenceAppointmentService busiMcuTencentConferenceAppointmentService;
    public  static String regex = "^\\d{4,6}$";
    public  static String regex_chair = "^\\d{6}$";
    @Resource
    private IBusiMcuHwcloudTemplateConferenceService busiMcuHwcloudTemplateConferenceService;
    @Resource
    private IBusiConferenceService busiConferenceService;
    @Resource
    private IBusiDingConferenceService busiDingConferenceService;
    @Resource
    private IBusiHwcloudConferenceService busiHwcloudConferenceService;

    @Resource
    private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;
    @Resource
    private BusiMcuZjConferenceAppointmentMapper busiMcuZjConferenceAppointmentMapper;
    @Resource
    private BusiMcuPlcConferenceAppointmentMapper busiMcuPlcConferenceAppointmentMapper;
    @Resource
    private BusiMcuKdcConferenceAppointmentMapper busiMcuKdcConferenceAppointmentMapper;
    @Resource
    private BusiMcuSmc3ConferenceAppointmentMapper busiMcuSmc3ConferenceAppointmentMapper;
    @Resource
    private BusiMcuSmc2ConferenceAppointmentMapper busiMcuSmc2ConferenceAppointmentMapper;
    @Resource
    private BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper;
    @Resource
    private BusiMcuDingConferenceAppointmentMapper busiMcuDingConferenceAppointmentMapper;
    @Resource
    private BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper;



    @Override
    public Map<String, Object> addConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment, String mcuTypeStr) {

        Map<String, Object> params = busiConferenceAppointment.getParams();

        if (!params.containsKey("businessFieldType")) {
            params.put("businessFieldType", 100);
        }
        if (!params.containsKey("duration")) {
            params.put("duration", 24);
        }
        if (!params.containsKey("isAutoCreateTemplate")) {
            params.put("isAutoCreateTemplate", 1);
        }
        if (!params.containsKey("repeatValue")) {
            params.put("repeatValue", 1);
        }
        if (!params.containsKey("status")) {
            params.put("status", 1);
        }
        if (!params.containsKey("supportLive")) {
            params.put("supportLive", 2);
        }
        if (!params.containsKey("supportRecord")) {
            params.put("supportRecord", 2);
        }
        if (!params.containsKey("type")) {
            params.put("type", 1);
        }
        if (!params.containsKey("isAutoCall")) {
            params.put("isAutoCall", 1);
        }
        if (!params.containsKey("defaultViewLayout")) {
            params.put("defaultViewLayout", "allEqual");
        }
        if (!params.containsKey("defaultViewIsDisplaySelf")) {
            params.put("defaultViewIsDisplaySelf", -1);
        }
        if (!params.containsKey("recordingEnabled")) {
            params.put("recordingEnabled", 2);
        }
        if (!params.containsKey("streamingEnabled")) {
            params.put("streamingEnabled", 2);
        }
        if (!params.containsKey("defaultViewIsBroadcast")) {
            params.put("defaultViewIsBroadcast", 2);
        }
        if (!params.containsKey("defaultViewIsFill")) {
            params.put("defaultViewIsFill", 1);
        }


        McuType mcuType = McuType.convert(mcuTypeStr);
        if (busiConferenceAppointment == null) {
            return null;
        }
        JSONObject jsonObject = (JSONObject) JSON.toJSON(busiConferenceAppointment);

        switch (mcuType) {
            case FME: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiConferenceAppointment.class);
                break;
            }
            case MCU_ZJ: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuZjConferenceAppointment.class);
                break;
            }
            case MCU_PLC: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuPlcConferenceAppointment.class);
                break;
            }
            case MCU_KDC: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuKdcConferenceAppointment.class);
                break;
            }
            case SMC3: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc3ConferenceAppointment.class);
                break;
            }
            case SMC2: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc2ConferenceAppointment.class);
                break;
            }

            case MCU_TENCENT: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuTencentConferenceAppointment.class);
                break;
            }
            case MCU_DING: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuDingConferenceAppointment.class);
                break;
            }
            case MCU_HWCLOUD: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuHwcloudConferenceAppointment.class);
                break;
            }
        }

        LoginUser loginUser = null;
        try {
            loginUser = SecurityUtils.getLoginUser();
        } catch (Exception e) {

        }
        if (loginUser == null) {
            loginUser = new LoginUser();
            SysUser sysUser = new SysUser();
            sysUser.setUserName("admin");
            loginUser.setUser(sysUser);
        }
        busiConferenceAppointment.setCreateBy(loginUser.getUsername());
        Integer type = busiConferenceAppointment.getType();
        try {
            if (type == null) {
                type = 1;
            }
            if (type != 1 && type != 3) {
                type = 2;
            }
            busiConferenceAppointment.setType(type);
            if (type == 2) {
                String startTimeStr = busiConferenceAppointment.getStartTime();
                String endTimeStr = busiConferenceAppointment.getEndTime();
                Date startTimeNew = new Date();
                String startTimeNewStr = DateUtil.convertDateToString(startTimeNew, "yyyy-MM-dd HH:mm:ss");
                Long diff = (Timestamp.valueOf(endTimeStr).getTime() - Timestamp.valueOf(startTimeStr).getTime()) / 1000;
                if (diff <= 0) {
                    diff = 60 * 120l;
                }
                Date endTimeNew = DateUtils.getDiffDate(startTimeNew, diff.intValue(), TimeUnit.SECONDS);
                String endTimeNewStr = DateUtil.convertDateToString(endTimeNew, "yyyy-MM-dd HH:mm:ss");
                busiConferenceAppointment.setStartTime(startTimeNewStr);
                busiConferenceAppointment.setEndTime(endTimeNewStr);
            }
            String endDate = null;
            if (type == 3) {
                Date startTimeNew = new Date();
                String startTimeNewStr = DateUtil.convertDateToString(startTimeNew, "yyyy-MM-dd HH:mm:ss");
                busiConferenceAppointment.setStartTime(startTimeNewStr);
                endDate = "9999-01-01 00:00:00";
                type = 2;
                busiConferenceAppointment.setEndTime(endDate);
            }
        } catch (Exception e) {
        }
        Map<String, Object> resultMap = null;
        switch (mcuType) {
            case FME: {
                resultMap = busiConferenceAppointmentService.insertBusiConferenceAppointment(busiConferenceAppointment);
                break;
            }
            case MCU_ZJ: {
                BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = (BusiMcuZjConferenceAppointment) busiConferenceAppointment;
                checkResource(busiMcuZjConferenceAppointment);
                resultMap = busiMcuZjConferenceAppointmentService.insertBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment);
                break;
            }
            case MCU_PLC: {
                BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = (BusiMcuPlcConferenceAppointment) busiConferenceAppointment;
                resultMap = busiMcuPlcConferenceAppointmentService.insertBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment);
                break;
            }
            case MCU_KDC: {
                BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = (BusiMcuKdcConferenceAppointment) busiConferenceAppointment;
                checkResource(busiMcuKdcConferenceAppointment);
                resultMap = busiMcuKdcConferenceAppointmentService.insertBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment);
                break;
            }
            case SMC3: {
                BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = (BusiMcuSmc3ConferenceAppointment) busiConferenceAppointment;
                // checkResource(busiMcuSmc3ConferenceAppointment);



                Map<String, Object> businessProperties = (Map<String, Object>) busiMcuSmc3ConferenceAppointment.getParams().get("businessProperties");
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                    busiMcuSmc3ConferenceAppointment.getParams().put("businessProperties",businessProperties);
                }
                businessProperties.put("mainMcuId",busiMcuSmc3ConferenceAppointment.getParams().get("mainMcuId"));
                businessProperties.put("mainMcuName",busiMcuSmc3ConferenceAppointment.getParams().get("mainMcuName"));
                businessProperties.put("mainServiceZoneId",busiMcuSmc3ConferenceAppointment.getParams().get("mainServiceZoneId"));
                businessProperties.put("mainServiceZoneName",busiMcuSmc3ConferenceAppointment.getParams().get("mainServiceZoneName"));

                businessProperties.put("videoProtocol",busiMcuSmc3ConferenceAppointment.getParams().get("videoProtocol"));
                businessProperties.put("videoResolution",busiMcuSmc3ConferenceAppointment.getParams().get("videoResolution"));
                businessProperties.put("audioProtocol",busiMcuSmc3ConferenceAppointment.getParams().get("audioProtocol"));
                businessProperties.put("streamService",busiMcuSmc3ConferenceAppointment.getParams().get("streamService"));
                resultMap = busiMcuSmc3ConferenceAppointmentService.insertBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment);
                break;
            }
            case SMC2: {
                BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment = (BusiMcuSmc2ConferenceAppointment) busiConferenceAppointment;
                resultMap = busiMcuSmc2ConferenceAppointmentService.insertBusiMcuSmc2ConferenceAppointment(busiMcuSmc2ConferenceAppointment);
                break;
            }

            case MCU_TENCENT: {
                BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = (BusiMcuTencentConferenceAppointment) busiConferenceAppointment;

                resultMap = busiMcuTencentConferenceAppointmentService.insertBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointment);
                break;
            }
            case MCU_DING: {
                BusiMcuDingConferenceAppointment busiMcuDingConferenceAppointment = (BusiMcuDingConferenceAppointment) busiConferenceAppointment;

                resultMap = busiMcuDingConferenceAppointmentService.insertBusiMcuDingConferenceAppointment(busiMcuDingConferenceAppointment);
                break;
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloudConferenceAppointment busiMcuhwcloudConferenceAppointment = (BusiMcuHwcloudConferenceAppointment) busiConferenceAppointment;

                resultMap = busiMcuHwcloudConferenceAppointmentService.insertBusiMcuHwcloudConferenceAppointment(busiMcuhwcloudConferenceAppointment);

                break;
            }
        }
        if (resultMap == null) {
            return null;
        }
        Integer success = 0;
        try {
            success = (Integer) resultMap.get("rows");
        } catch (Exception e) {
        }
        if (success > 0) {
            Long conferenceNumber = null;
            Long templateId = null;
            String tenantId = "";
            Long appointmentId = null;
            try {
                conferenceNumber = (Long) resultMap.get("conferenceNumber");
            } catch (Exception e) {

            }
            try {
                templateId = (Long) resultMap.get("templateId");
            } catch (Exception e) {

            }
            try {
                tenantId = (String) resultMap.get("tenantId");
            } catch (Exception e) {

            }
            try {
                appointmentId = (Long) resultMap.get("appointmentId");
            } catch (Exception e) {

            }
            if (type == 2) {
                if (templateId != null) {
                    try {
                        String contextKey = null;
                        switch (mcuType) {
                            case FME: {
                                contextKey = templateConferenceStartService.startTemplateConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiConferenceAppointmentService.updateBusiConferenceAppointment(busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case MCU_ZJ: {
                                contextKey = busiMcuZjConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuZjConferenceAppointmentService.updateBusiMcuZjConferenceAppointment((BusiMcuZjConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case MCU_PLC: {
                                contextKey = busiMcuPlcConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuPlcConferenceAppointmentService.updateBusiMcuPlcConferenceAppointment((BusiMcuPlcConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case MCU_KDC: {
                                contextKey = busiMcuKdcConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuKdcConferenceAppointmentService.updateBusiMcuKdcConferenceAppointment((BusiMcuKdcConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case SMC3: {
                                contextKey = busiSmc3ConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuSmc3ConferenceAppointmentService.updateBusiMcuSmc3ConferenceAppointment((BusiMcuSmc3ConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case SMC2: {
                                contextKey = busiSmc2ConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuSmc2ConferenceAppointmentService.updateBusiMcuSmc2ConferenceAppointment((BusiMcuSmc2ConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }

                            case MCU_TENCENT: {
                                contextKey = busiTencentConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuTencentConferenceAppointmentService.updateBusiMcuTencentConferenceAppointment((BusiMcuTencentConferenceAppointment) busiConferenceAppointment, false);
                                    }
                                }
                                break;
                            }
                            case MCU_DING: {
                                contextKey = busiTencentConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuDingConferenceAppointmentService.updateBusiMcuDingConferenceAppointment((BusiMcuDingConferenceAppointment) busiConferenceAppointment);
                                    }
                                }
                                break;
                            }
                            case MCU_HWCLOUD: {
                                contextKey = busiTencentConferenceService.startConference(templateId);
                                if (contextKey != null) {
                                    if (busiConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                        busiConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                        busiConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                        busiMcuHwcloudConferenceAppointmentService.updateBusiMcuHwcloudConferenceAppointment((BusiMcuHwcloudConferenceAppointment) busiConferenceAppointment,false);
                                    }
                                }
                                break;
                            }
                        }
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                        if (baseConferenceContext != null) {
                            StartDownCascadeConferenceTask startDownCascadeConferenceTask = new StartDownCascadeConferenceTask(baseConferenceContext.getId(), 0, baseConferenceContext);
                            BeanFactory.getBean(TaskService.class).addTask(startDownCascadeConferenceTask);
                            BusiConferenceAppointment conferenceAppointment = baseConferenceContext.getConferenceAppointment();
                            if (conferenceAppointment != null) {
                                NotifyTask notifyTask = new NotifyTask(baseConferenceContext.getId(), 10000, baseConferenceContext, conferenceAppointment.getId(), mcuTypeStr, "即时");
                                taskService.addTask(notifyTask);
                            }
                        }

                    } catch (Exception e) {
                    }
                }
            }
            NotifyTask notifyTask = new NotifyTask(appointmentId.toString(), 10000, null, appointmentId, mcuTypeStr, "预约");
            taskService.addTask(notifyTask);
        }
        int rows = 0;
        Object rowsObj = resultMap.get("rows");
        resultMap.put("mcuType", mcuType.getCode());
        if (rowsObj != null) {
            rows = (int) rowsObj;
            if (rows > 0) {
                return resultMap;
            }
        }
        return null;
    }

    @Override
    public int editConferenceAppointment(String apConferenceId, BusiConferenceAppointment busiConferenceAppointment) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(apConferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        if (busiConferenceAppointment != null) {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(busiConferenceAppointment);

            switch (mcuType) {
                case FME: {
                    busiConferenceAppointment = jsonObject.toJavaObject(BusiConferenceAppointment.class);
                    break;
                }
                case MCU_ZJ: {
                    busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuZjConferenceAppointment.class);
                    break;
                }
                case MCU_PLC: {
                    busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuPlcConferenceAppointment.class);
                    break;
                }
                case MCU_KDC: {
                    busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuKdcConferenceAppointment.class);
                    break;
                }
                case SMC3: {
                    busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc3ConferenceAppointment.class);
                    break;
                }
                case SMC2: {
                    busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc2ConferenceAppointment.class);
                    break;
                }

                case MCU_TENCENT: {
                    busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuTencentConferenceAppointment.class);
                    break;
                }
                case MCU_DING: {
                    busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuDingConferenceAppointment.class);
                    break;
                }
                case MCU_HWCLOUD: {
                    busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuHwcloudConferenceAppointment.class);
                    break;
                }
            }
        }
        ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType.getCode(), id);
        if (viewConferenceAppointment != null) {
            String contextKey = EncryptIdUtil.generateContextKey(viewConferenceAppointment.getTemplateId(), viewConferenceAppointment.getMcuType());
            busiConferenceAppointment.setTemplateId(viewConferenceAppointment.getTemplateId());
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            if (baseConferenceContext != null) {
                throw new SystemException("当前预约会议模板正在开会，不能修改！");
            }
        }
        switch (mcuType) {
            case FME: {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser == null) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName("admin");
                    loginUser.setUser(sysUser);
                }

                JSONObject params = new JSONObject(busiConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiTemplateConference busiTemplateConference = params.toJavaObject(BusiTemplateConference.class);
                busiTemplateConference.setId(busiConferenceAppointment.getTemplateId());

                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiTemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiTemplateDept> templateDepts = new ArrayList<>();
                if (templateDeptArr != null && templateDeptArr.size() > 0) {
                    for (int i = 0; i < templateDeptArr.size(); i++) {
                        templateDepts.add(templateDeptArr.getObject(i, BusiTemplateDept.class));
                    }
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

                busiTemplateConference.setBusinessFieldType(100);
                busiTemplateConference.setIsAutoCreateConferenceNumber(1);
                int c = busiTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);

                busiConferenceAppointment.setCreateBy(loginUser.getUsername());
                busiConferenceAppointment.setId(id);
                int i = busiConferenceAppointmentService.updateBusiConferenceAppointment(busiConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiConferenceAppointment.getId().toString(), 10000, null, busiConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case MCU_ZJ: {
                BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = (BusiMcuZjConferenceAppointment) busiConferenceAppointment;
                busiMcuZjConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser == null) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName("admin");
                    loginUser.setUser(sysUser);
                }

                JSONObject params = new JSONObject(busiMcuZjConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuZjTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuZjTemplateConference.class);
                busiTemplateConference.setId(busiMcuZjConferenceAppointment.getTemplateId());

                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuZjTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuZjTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuZjTemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiMcuZjTemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuZjTemplateDept.class));
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

                busiTemplateConference.setBusinessFieldType(100);
                busiTemplateConference.setIsAutoCreateConferenceNumber(1);
                int c = busiMcuZjTemplateConferenceService.updateBusiMcuZjTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                busiMcuZjConferenceAppointment.setCreateBy(loginUser.getUsername());
                int i = busiMcuZjConferenceAppointmentService.updateBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuZjConferenceAppointment.getId().toString(), 10000, null, busiMcuZjConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case MCU_PLC: {
                BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = (BusiMcuPlcConferenceAppointment) busiConferenceAppointment;
                busiMcuPlcConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser == null) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName("admin");
                    loginUser.setUser(sysUser);
                }
                busiMcuPlcConferenceAppointment.setCreateBy(loginUser.getUsername());
                JSONObject params = new JSONObject(busiMcuPlcConferenceAppointment.getParams());
                BusiMcuZjTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuZjTemplateConference.class);
                busiTemplateConference.setId(busiMcuPlcConferenceAppointment.getTemplateId());
                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                Long masterTerminalId = params.getLong("masterTerminalId");

                List<BusiMcuZjTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuZjTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiMcuZjTemplateParticipant.class);
                        Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(busiTemplateParticipant.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(busiTemplateParticipant.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(busiTemplateParticipant);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiMcuZjTemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    BusiMcuZjTemplateDept busiTemplateDept = templateDeptArr.getObject(i, BusiMcuZjTemplateDept.class);
                    Assert.notNull(busiTemplateDept.getDeptId(), "部门ID不能为空");
                    Assert.notNull(busiTemplateDept.getWeight(), "部门weight顺序不能为空");
                    templateDepts.add(busiTemplateDept);
                }

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }

                // 默认24小时
                busiTemplateConference.setDurationEnabled(1);// 开始会议时长限制
                busiTemplateConference.setDurationTime(1440);

                Integer muteType = busiTemplateConference.getMuteType();
                if (muteType == null || muteType != 0) {
                    muteType = 1;// 0 不静音 1 静音
                }
                busiTemplateConference.setMuteType(muteType);
                // 默认自动分屏
                busiTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
                busiTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
                busiTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
                busiTemplateConference.setPollingInterval(10);
                busiTemplateConference.setDefaultViewIsDisplaySelf(-1);
                busiTemplateConference.setDefaultViewLayoutGuest(AutomaticSplitScreen.LAYOUT);
                busiTemplateConference.setDefaultViewIsFillGuest(YesOrNo.YES.getValue());
                busiTemplateConference.setPollingIntervalGuest(10);

                int c = busiMcuZjTemplateConferenceService.insertBusiMcuZjTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                } else {
                    return 0;
                }
                int i = busiMcuPlcConferenceAppointmentService.updateBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuPlcConferenceAppointment.getId().toString(), 10000, null, busiMcuPlcConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case MCU_KDC: {
                BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = (BusiMcuKdcConferenceAppointment) busiConferenceAppointment;
                busiMcuKdcConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser == null) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName("admin");
                    loginUser.setUser(sysUser);
                }
                busiMcuKdcConferenceAppointment.setCreateBy(loginUser.getUsername());
                JSONObject params = new JSONObject(busiMcuKdcConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuKdcTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuKdcTemplateConference.class);

                busiTemplateConference.setId(id);

                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuKdcTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuKdcTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuKdcTemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiMcuKdcTemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuKdcTemplateDept.class));
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

                busiTemplateConference.setBusinessFieldType(100);
                busiTemplateConference.setIsAutoCreateConferenceNumber(1);
                int c = busiMcuKdcTemplateConferenceService.updateBusiMcuKdcTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());

                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                } else {
                    return 0;
                }
                int i = busiMcuKdcConferenceAppointmentService.updateBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuKdcConferenceAppointment.getId().toString(), 10000, null, busiMcuKdcConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case SMC3: {
                BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = (BusiMcuSmc3ConferenceAppointment) busiConferenceAppointment;
                busiMcuSmc3ConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser == null) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName("admin");
                    loginUser.setUser(sysUser);
                }
                busiMcuSmc3ConferenceAppointment.setCreateBy(loginUser.getUsername());

                JSONObject params = new JSONObject(busiMcuSmc3ConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuSmc3TemplateConference busiTemplateConference = params.toJavaObject(BusiMcuSmc3TemplateConference.class);

                busiTemplateConference.setId(busiMcuSmc3ConferenceAppointment.getTemplateId());
                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuSmc3TemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuSmc3TemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiMcuSmc3TemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuSmc3TemplateDept.class));
                }

                if (busiTemplateConference.getCreateType() == null) {
                    busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());
                }
                if (busiTemplateConference.getIsAutoCreateStreamUrl() != null && busiTemplateConference.getIsAutoCreateStreamUrl() == 1) {
                    busiTemplateConference.setStreamUrl(null);
                }
                Object confPresetParam = busiMcuSmc3ConferenceAppointment.getParams().get("confPresetParam");
                if(confPresetParam!=null){
                    busiTemplateConference.setConfPresetParam(JSONObject.parseObject(JSONObject.toJSONString(confPresetParam)));
                }

                Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                }
                businessProperties.put("mainMcuId",busiMcuSmc3ConferenceAppointment.getParams().get("mainMcuId"));
                businessProperties.put("mainMcuName",busiMcuSmc3ConferenceAppointment.getParams().get("mainMcuName"));
                businessProperties.put("mainServiceZoneId",busiMcuSmc3ConferenceAppointment.getParams().get("mainServiceZoneId"));
                businessProperties.put("mainServiceZoneName",busiMcuSmc3ConferenceAppointment.getParams().get("mainServiceZoneName"));

                businessProperties.put("videoProtocol",busiMcuSmc3ConferenceAppointment.getParams().get("videoProtocol"));
                businessProperties.put("videoResolution",busiMcuSmc3ConferenceAppointment.getParams().get("videoResolution"));
                businessProperties.put("audioProtocol",busiMcuSmc3ConferenceAppointment.getParams().get("audioProtocol"));
                businessProperties.put("streamService",busiMcuSmc3ConferenceAppointment.getParams().get("streamService"));
                busiTemplateConference.setBusinessProperties(businessProperties);

//                Object cascadeNodes = busiMcuSmc3ConferenceAppointment.getParams().get("cascadeNodes");
//                if(cascadeNodes!=null){
//                    JSONObject jsonObject1 = new JSONObject();
//                    jsonObject1.put("cascadeNodes",cascadeNodes);
//                    JSONArray templateNodesJson = jsonObject1.getJSONArray("cascadeNodes");
//                    if(templateNodesJson!=null){
//                        busiTemplateConference.setCategory("CASCADE");
//                        busiTemplateConference.setIsAutoCreateConferenceNumber(1);
//                        busiTemplateConference.setBusinessFieldType(100);
//                        busiTemplateConference.setCreateType(2);
//                    }
//                    busiTemplateConference.setCascadeNodes(JSONArray.toJSONString(templateNodesJson));
//                }

                Object cascadeNodesTemp = busiMcuSmc3ConferenceAppointment.getParams().get("cascadeNodesTemp");
                if(cascadeNodesTemp!=null){
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("cascadeNodesTemp",cascadeNodesTemp);
                    JSONArray templateNodesTempJson = jsonObject1.getJSONArray("cascadeNodesTemp");
                    if(templateNodesTempJson!=null){
                        busiTemplateConference.setCategory("CASCADE");
                        busiTemplateConference.setIsAutoCreateConferenceNumber(1);
                        busiTemplateConference.setBusinessFieldType(100);
                        busiTemplateConference.setCreateType(2);
                    }
                    busiTemplateConference.setCascadeNodesTemp(JSONArray.toJSONString(templateNodesTempJson));
                }

                busiTemplateConference.setChairmanPassword((String)busiMcuSmc3ConferenceAppointment.getParams().get("chairmanPassword"));
                busiTemplateConference.setGuestPassword((String)busiMcuSmc3ConferenceAppointment.getParams().get("guestPassword"));
                if(busiMcuSmc3ConferenceAppointment.getParams().get("maxParticipantNum")==null){
                    busiTemplateConference.setMaxParticipantNum(500);
                }else {
                    busiTemplateConference.setMaxParticipantNum((Integer) busiMcuSmc3ConferenceAppointment.getParams().get("maxParticipantNum"));
                }
                busiTemplateConference.setName((String)busiMcuSmc3ConferenceAppointment.getParams().get("conferenceName"));
                busiTemplateConference.setMuteType((Integer) busiMcuSmc3ConferenceAppointment.getParams().get("muteType"));
                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(busiMcuSmc3ConferenceAppointment.getTemplateId());
                busiTemplateConference.setSmcTemplateId(busiMcuSmc3TemplateConference.getSmcTemplateId());
                busiTemplateConference.setBusinessFieldType(100);
                busiTemplateConference.setIsAutoCreateConferenceNumber(1);
                int c = busiMcuSmc3TemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {

                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());

                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                } else {
                    return 0;
                }
                int i = busiMcuSmc3ConferenceAppointmentService.updateBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuSmc3ConferenceAppointment.getId().toString(), 10000, null, busiMcuSmc3ConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case SMC2: {
                BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment = (BusiMcuSmc2ConferenceAppointment) busiConferenceAppointment;
                busiMcuSmc2ConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser == null) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName("admin");
                    loginUser.setUser(sysUser);
                }
                busiMcuSmc2ConferenceAppointment.setCreateBy(loginUser.getUsername());

                JSONObject params = new JSONObject(busiMcuSmc2ConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuSmc2TemplateConference busiTemplateConference = params.toJavaObject(BusiMcuSmc2TemplateConference.class);
                busiTemplateConference.setName((String) params.get("conferenceName"));
                busiTemplateConference.setId(id);
                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuSmc2TemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuSmc2TemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuSmc2TemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                JSONArray templateDeptArr = params.getJSONArray("templateDepts");
                List<BusiMcuSmc2TemplateDept> templateDepts = new ArrayList<>();
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuSmc2TemplateDept.class));
                }

                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());


                busiTemplateConference.setChairmanPassword((String)busiMcuSmc2ConferenceAppointment.getParams().get("chairmanPassword"));
                busiTemplateConference.setGuestPassword((String)busiMcuSmc2ConferenceAppointment.getParams().get("guestPassword"));
                if(busiMcuSmc2ConferenceAppointment.getParams().get("maxParticipantNum")==null){
                    busiTemplateConference.setMaxParticipantNum(500);
                }else {
                    busiTemplateConference.setMaxParticipantNum((Integer) busiMcuSmc2ConferenceAppointment.getParams().get("maxParticipantNum"));
                }
                busiTemplateConference.setName((String)busiMcuSmc2ConferenceAppointment.getParams().get("conferenceName"));

                BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiMcuSmc2ConferenceAppointment.getTemplateId());
                busiTemplateConference.setId(busiMcuSmc2TemplateConference.getId());
                busiTemplateConference.setGuestPassword((String)busiMcuSmc2ConferenceAppointment.getParams().get("password"));
                busiTemplateConference.setConferencePassword((String)busiMcuSmc2ConferenceAppointment.getParams().get("password"));
                busiTemplateConference.setMuteType(busiMcuSmc2TemplateConference.getParams().get("mutuType")==null?2:(Integer) busiMcuSmc2TemplateConference.getParams().get("mutuType"));
                busiTemplateConference.setBusinessFieldType(100);
                busiTemplateConference.setIsAutoCreateConferenceNumber(1);
                int c = busiMcuSmc2TemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, templateDepts);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                } else {
                    return 0;
                }
                int i = busiMcuSmc2ConferenceAppointmentService.updateBusiMcuSmc2ConferenceAppointment(busiMcuSmc2ConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuSmc2ConferenceAppointment.getId().toString(), 10000, null, busiMcuSmc2ConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return 0;
            }
            case MCU_TENCENT: {
                BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = (BusiMcuTencentConferenceAppointment) busiConferenceAppointment;
                busiMcuTencentConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser == null) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName("admin");
                    loginUser.setUser(sysUser);
                }
                busiMcuTencentConferenceAppointment.setCreateBy(loginUser.getUsername());

                JSONObject params = new JSONObject(busiMcuTencentConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuTencentTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuTencentTemplateConference.class);
                busiTemplateConference.setId(id);
                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuTencentTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuTencentTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuTencentTemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序


                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

                if(busiMcuTencentConferenceAppointment.getParams().get("chairmanPassword")!=null){
                    String passwordStr_chair = (String) busiMcuTencentConferenceAppointment.getParams().get("chairmanPassword");
                    Pattern pattern = Pattern.compile(regex_chair);
                    // 创建匹配器
                    Matcher matcher = pattern.matcher(passwordStr_chair);
                    if (!matcher.matches()) {
                        throw new CustomException("密码格式不正确,密码是6位的数字");
                    }
                    busiTemplateConference.setChairmanPassword(passwordStr_chair);
                }

                if(busiMcuTencentConferenceAppointment.getParams().get("password")!=null){
                    String passwordStr = (String) busiMcuTencentConferenceAppointment.getParams().get("password");
                    if(Strings.isNotBlank(passwordStr)){
                        Pattern pattern = Pattern.compile(regex);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher(passwordStr);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,密码是4到6位的数字");
                        }
                        busiTemplateConference.setGuestPassword(passwordStr);
                        busiTemplateConference.setConferencePassword(passwordStr);
                    }
                }

                if(busiMcuTencentConferenceAppointment.getParams().get("supportLive")!=null){
                    busiTemplateConference.setRecordingEnabled((Integer) busiMcuTencentConferenceAppointment.getParams().get("supportLive"));
                }
                if(busiMcuTencentConferenceAppointment.getParams().get("supportRecord")!=null){
                    busiTemplateConference.setRecordingEnabled((Integer) busiMcuTencentConferenceAppointment.getParams().get("supportRecord"));
                }
                busiTemplateConference.setMuteType(busiMcuTencentConferenceAppointment.getParams().get("muteType")==null?2:(Integer) busiMcuTencentConferenceAppointment.getParams().get("muteType"));
                if(busiMcuTencentConferenceAppointment.getParams().get("maxParticipantNum")==null){
                    busiTemplateConference.setMaxParticipantNum(500);
                }else {
                    busiTemplateConference.setMaxParticipantNum((Integer) busiMcuTencentConferenceAppointment.getParams().get("maxParticipantNum"));
                }
                busiTemplateConference.setName((String)busiMcuTencentConferenceAppointment.getParams().get("conferenceName"));

                busiTemplateConference.setId(viewConferenceAppointment.getTemplateId());

                Map<String, Object> businessProperties = (Map<String, Object>) busiMcuTencentConferenceAppointment.getParams().get("businessProperties");
                if(businessProperties==null){
                    businessProperties=new HashMap<>();
                    busiMcuTencentConferenceAppointment.getParams().put("businessProperties",businessProperties);
                }
                businessProperties.put("attendees",busiMcuTencentConferenceAppointment.getParams().get("templateParticipants"));
                busiTemplateConference.setBusinessProperties(businessProperties);

                busiTemplateConference.setBusinessFieldType(100);
                int c = busiMcuTencentTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, null);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                } else {
                    return 0;
                }
                int i = busiMcuTencentConferenceAppointmentService.updateBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuTencentConferenceAppointment.getId().toString(), 10000, null, busiMcuTencentConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case MCU_HWCLOUD: {
                BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = (BusiMcuHwcloudConferenceAppointment) busiConferenceAppointment;
                busiMcuHwcloudConferenceAppointment.setId(id);
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser == null) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName("admin");
                    loginUser.setUser(sysUser);
                }
                busiMcuHwcloudConferenceAppointment.setCreateBy(loginUser.getUsername());

                JSONObject params = new JSONObject(busiMcuHwcloudConferenceAppointment.getParams());
                Long masterTerminalId = params.getLong("masterTerminalId");
                BusiMcuTencentTemplateConference busiTemplateConference = params.toJavaObject(BusiMcuTencentTemplateConference.class);
                busiTemplateConference.setId(id);
                JSONArray busiTemplateParticipantArr = params.getJSONArray("templateParticipants");
                List<BusiMcuTencentTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
                if (busiTemplateParticipantArr != null) {
                    for (int i = 0; i < busiTemplateParticipantArr.size(); i++) {
                        BusiMcuTencentTemplateParticipant p = busiTemplateParticipantArr.getObject(i, BusiMcuTencentTemplateParticipant.class);
                        p.setId(null);
                        Assert.notNull(p.getTerminalId(), "参会者终端ID不能为空");
                        Assert.notNull(p.getWeight(), "参会者weight顺序不能为空");
                        Assert.notNull(p.getAttendType(), "参会者入会/直播类型不能为空");
                        busiTemplateParticipants.add(p);
                    }
                }

                // 部门顺序
                busiTemplateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

                if(busiMcuHwcloudConferenceAppointment.getParams().get("chairmanPassword")!=null){
                    String passwordStr_chair = (String) busiMcuHwcloudConferenceAppointment.getParams().get("chairmanPassword");
                    Pattern pattern = Pattern.compile(regex_chair);
                    // 创建匹配器
                    Matcher matcher = pattern.matcher(passwordStr_chair);
                    if (!matcher.matches()) {
                        throw new CustomException("密码格式不正确,密码是6位的数字");
                    }
                    busiTemplateConference.setChairmanPassword(passwordStr_chair);
                }

                if(busiMcuHwcloudConferenceAppointment.getParams().get("password")!=null){
                    String passwordStr = (String) busiMcuHwcloudConferenceAppointment.getParams().get("password");
                    if(Strings.isNotBlank(passwordStr)){
                        Pattern pattern = Pattern.compile(regex);
                        // 创建匹配器
                        Matcher matcher = pattern.matcher(passwordStr);
                        if (!matcher.matches()) {
                            throw new CustomException("密码格式不正确,密码是4到6位的数字");
                        }
                        busiTemplateConference.setGuestPassword(passwordStr);
                        busiTemplateConference.setConferencePassword(passwordStr);
                    }
                }

                if(busiMcuHwcloudConferenceAppointment.getParams().get("supportLive")!=null){
                    busiTemplateConference.setRecordingEnabled((Integer) busiMcuHwcloudConferenceAppointment.getParams().get("supportLive"));
                }
                if(busiMcuHwcloudConferenceAppointment.getParams().get("supportRecord")!=null){
                    busiTemplateConference.setRecordingEnabled((Integer) busiMcuHwcloudConferenceAppointment.getParams().get("supportRecord"));
                }
                busiTemplateConference.setMuteType(busiMcuHwcloudConferenceAppointment.getParams().get("muteType")==null?2:(Integer) busiMcuHwcloudConferenceAppointment.getParams().get("muteType"));
                if(busiMcuHwcloudConferenceAppointment.getParams().get("maxParticipantNum")==null){
                    busiTemplateConference.setMaxParticipantNum(500);
                }else {
                    busiTemplateConference.setMaxParticipantNum((Integer) busiMcuHwcloudConferenceAppointment.getParams().get("maxParticipantNum"));
                }
                busiTemplateConference.setName((String)busiMcuHwcloudConferenceAppointment.getParams().get("conferenceName"));

                BusiMcuTencentTemplateConference busiMcuTencentTemplateConference = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(busiMcuHwcloudConferenceAppointment.getTemplateId());
                busiTemplateConference.setId(busiMcuTencentTemplateConference.getId());

                busiTemplateConference.setBusinessFieldType(100);
                int c = busiMcuTencentTemplateConferenceService.updateBusiTemplateConference(busiTemplateConference, masterTerminalId, busiTemplateParticipants, null);
                if (c > 0) {
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), mcuType.getCode());
                    // 分屏
                    try {
                        if (params.containsKey("viewConfigInfo")) {
                            JSONObject jsonObjectViewConfigInfo = params.getJSONObject("viewConfigInfo");
                            updateDefaultViewConfigInfo(jsonObjectViewConfigInfo, conferenceId);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }

                } else {
                    return 0;
                }
                int i = busiMcuHwcloudConferenceAppointmentService.updateBusiMcuHwcloudConferenceAppointment(busiMcuHwcloudConferenceAppointment, true);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(busiMcuHwcloudConferenceAppointment.getId().toString(), 10000, null, busiMcuHwcloudConferenceAppointment.getId(), mcuType.getCode(), "修改了预约");
                    taskService.addTask(notifyTask);
                }
                return i;
            }
        }
        return 0;
    }

    @Override
    public int removeConferenceAppointment(String apConferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(apConferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();

        ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType.getCode(), id);
        ViewTemplateConference viewTemplateConference = null;
        List<ViewTemplateParticipant> viewTemplateParticipantList = null;
        if (viewConferenceAppointment != null) {
            String contextKey = EncryptIdUtil.generateContextKey(viewConferenceAppointment.getTemplateId(), viewConferenceAppointment.getMcuType());
            viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType.getCode(), viewConferenceAppointment.getTemplateId());
            ViewTemplateParticipant viewTemplateParticipantTemp = new ViewTemplateParticipant();
            viewTemplateParticipantTemp.setMcuType(mcuType.getCode());
            viewTemplateParticipantTemp.setTemplateConferenceId(viewConferenceAppointment.getTemplateId());
            viewTemplateParticipantList = viewTemplateParticipantMapper.selectViewTemplateParticipantList(viewTemplateParticipantTemp);
            BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
            if (baseConferenceContext != null) {
                throw new SystemException("当前预约会议模板正在开会，不能删除！");
            }
        }

        switch (mcuType) {
            case FME: {
                int i = busiConferenceAppointmentService.deleteBusiConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 10000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case MCU_ZJ: {
                int i = busiMcuZjConferenceAppointmentService.deleteBusiMcuZjConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 10000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case MCU_PLC: {
                int i = busiMcuPlcConferenceAppointmentService.deleteBusiMcuPlcConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 10000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case MCU_KDC: {
                int i = busiMcuKdcConferenceAppointmentService.deleteBusiMcuKdcConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 10000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case SMC3: {
                int i = busiMcuSmc3ConferenceAppointmentService.deleteBusiMcuSmc3ConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 10000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case SMC2: {
                int i = busiMcuSmc2ConferenceAppointmentService.deleteBusiMcuSmc2ConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 10000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case MCU_TENCENT: {
                int i = busiMcuTencentConferenceAppointmentService.deleteBusiMcuTencentConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 10000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return i;
            }
            case MCU_DING: {
                int i = busiMcuDingConferenceAppointmentService.deleteBusiMcuDingConferenceAppointmentById(id);
                if (i > 0) {
                    NotifyTask notifyTask = new NotifyTask(viewConferenceAppointment.getId() + "", 10000, null, viewConferenceAppointment.getId(), mcuType.getCode(), "取消", viewConferenceAppointment, viewTemplateConference, viewTemplateParticipantList);
                    taskService.addTask(notifyTask);
                }
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean endConference(String conferenceId, int endType) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                busiConferenceService.endConference(conferenceId, endType, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }
            case MCU_ZJ: {
                busiMcuZjConferenceService.endConference(conferenceId, endType);
                break;
            }
            case MCU_PLC: {
                busiMcuPlcConferenceService.endConference(conferenceId, endType);
                break;
            }
            case MCU_KDC: {
                busiMcuKdcConferenceService.endConference(conferenceId, endType);
                break;
            }
            case SMC3: {
                busiSmc3ConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }
            case SMC2: {
                busiSmc2ConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }
            case MCU_TENCENT: {
                busiTencentConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }

            case MCU_DING: {
                busiDingConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }

            case MCU_HWCLOUD: {
                busiHwcloudConferenceService.endConference(conferenceId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                break;
            }
        }
        if (baseConferenceContext != null) {
            EndDownCascadeConferenceTask endDownCascadeConferenceTask = new EndDownCascadeConferenceTask(conferenceId, 0, baseConferenceContext, endType);
            BeanFactory.getBean(TaskService.class).addTask(endDownCascadeConferenceTask);
        }
        return true;
    }

    @Override
    public int onlyEditConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment, String mcuTypeStr) {
        McuType mcuType = McuType.convert(mcuTypeStr);
        int i = 0;
        JSONObject jsonObject = (JSONObject) JSON.toJSON(busiConferenceAppointment);

        switch (mcuType) {
            case FME: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiConferenceAppointment.class);
                i = busiConferenceAppointmentMapper.updateBusiConferenceAppointment(busiConferenceAppointment);
                break;
            }
            case MCU_ZJ: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuZjConferenceAppointment.class);
                BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = (BusiMcuZjConferenceAppointment) busiConferenceAppointment;
                i = busiMcuZjConferenceAppointmentMapper.updateBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment);
                break;
            }
            case MCU_PLC: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuPlcConferenceAppointment.class);
                BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = (BusiMcuPlcConferenceAppointment) busiConferenceAppointment;
                i = busiMcuPlcConferenceAppointmentMapper.updateBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment);
                break;
            }
            case MCU_KDC: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuKdcConferenceAppointment.class);
                BusiMcuKdcConferenceAppointment busiMcuKdcConferenceAppointment = (BusiMcuKdcConferenceAppointment) busiConferenceAppointment;
                i = busiMcuKdcConferenceAppointmentMapper.updateBusiMcuKdcConferenceAppointment(busiMcuKdcConferenceAppointment);
                break;
            }
            case SMC3: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc3ConferenceAppointment.class);
                BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment = (BusiMcuSmc3ConferenceAppointment) busiConferenceAppointment;
                i = busiMcuSmc3ConferenceAppointmentMapper.updateBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment);
                break;
            }
            case SMC2: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuSmc2ConferenceAppointment.class);
                BusiMcuSmc2ConferenceAppointment busiMcuSmc2ConferenceAppointment = (BusiMcuSmc2ConferenceAppointment) busiConferenceAppointment;
                i = busiMcuSmc2ConferenceAppointmentMapper.updateBusiMcuSmc2ConferenceAppointment(busiMcuSmc2ConferenceAppointment);
                break;
            }
            case MCU_TENCENT: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuTencentConferenceAppointment.class);
                BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = (BusiMcuTencentConferenceAppointment) busiConferenceAppointment;
                i = busiMcuTencentConferenceAppointmentMapper.updateBusiMcuTencentConferenceAppointment(busiMcuTencentConferenceAppointment);
                break;
            }
            case MCU_DING: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuDingConferenceAppointment.class);
                BusiMcuDingConferenceAppointment busiMcuDingConferenceAppointment = (BusiMcuDingConferenceAppointment) busiConferenceAppointment;
                i = busiMcuDingConferenceAppointmentMapper.updateBusiMcuDingConferenceAppointment(busiMcuDingConferenceAppointment);
                break;
            }
            case MCU_HWCLOUD: {
                busiConferenceAppointment = jsonObject.toJavaObject(BusiMcuHwcloudConferenceAppointment.class);
                BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = (BusiMcuHwcloudConferenceAppointment) busiConferenceAppointment;
                i = busiMcuHwcloudConferenceAppointmentMapper.updateBusiMcuHwcloudConferenceAppointment(busiMcuHwcloudConferenceAppointment);
                break;
            }
        }
        if (i > 0) {
            IMqttService bean = BeanFactory.getBean(IMqttService.class);
            BusiConferenceAppointment appointmentCache = bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_TENCENT.getCode()));
            if (appointmentCache != null) {
                bean.updateAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_TENCENT.getCode()), busiConferenceAppointment);
            }
        }
        return i;
    }

    private void checkResource(BusiMcuZjConferenceAppointment busiConferenceAppointment) {
        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiConferenceAppointment.getDeptId()).getMasterMcuZjBridge();
        if (mcuZjBridge != null) {
            if (mcuZjBridge.getUsedResourceCount() >= mcuZjBridge.getSystemResourceCount()) {
                if (busiConferenceAppointment.getType() == 2) {
                    throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试或者稍后再试。");
                } else if (busiConferenceAppointment.getType() == 1) {
                    String startTimeStr = busiConferenceAppointment.getStartTime();
                    Date startTime = DateUtil.convertDateByString(startTimeStr, "");
                    if (startTime.getTime() - System.currentTimeMillis() < 7200000) {
                        throw new SystemException(1, "MCU资源已耗尽，创建2小时内的预约会议请先关闭一些会议后重试或者稍后再试。");
                    }
                }
            } else {
                if (mcuZjBridge.getUsedResourceCount() > 0) {
                    SourceTemplate sourceTemplate = mcuZjBridge.getDefaultSourceTemplate();
                    Object resourceTemplateIdObj = busiConferenceAppointment.getParams().get("resourceTemplateId");
                    if (resourceTemplateIdObj != null) {
                        Integer resourceTemplateId = null;
                        try {
                            resourceTemplateId = (Integer) resourceTemplateIdObj;
                            SourceTemplate sourceTemplateT = mcuZjBridge.getSourceTemplateById(resourceTemplateId);
                            if (sourceTemplateT != null) {
                                sourceTemplate = sourceTemplateT;
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (sourceTemplate.getEvaluationResourceCount() + mcuZjBridge.getUsedResourceCount() > mcuZjBridge.getSystemResourceCount()) {
                        throw new SystemException(1, "MCU资源不足，请关闭一些会议后重试。");
                    }
                }
            }
        }
    }

    private void checkResource(BusiMcuKdcConferenceAppointment busiConferenceAppointment) {
        McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(busiConferenceAppointment.getDeptId()).getMasterMcuKdcBridge();
        if (mcuKdcBridge != null) {
            if (mcuKdcBridge.getUsedResourceCount() >= mcuKdcBridge.getSystemResourceCount()) {
                if (busiConferenceAppointment.getType() == 2) {
                    throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试或者稍后再试。");
                } else if (busiConferenceAppointment.getType() == 1) {
                    String startTimeStr = busiConferenceAppointment.getStartTime();
                    Date startTime = DateUtil.convertDateByString(startTimeStr, "");
                    if (startTime.getTime() - System.currentTimeMillis() < 7200000) {
                        throw new SystemException(1, "MCU资源已耗尽，创建2小时内的预约会议请先关闭一些会议后重试或者稍后再试。");
                    }
                }
            }
        }
    }

    private void checkResource(BusiMcuSmc3ConferenceAppointment busiConferenceAppointment) {
        Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiConferenceAppointment.getDeptId());
        if (smc3Bridge != null) {
            if (smc3Bridge.getUsedResourceCount() >= smc3Bridge.getSystemResourceCount()) {
                if (busiConferenceAppointment.getType() == 2) {
                    throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试或者稍后再试。");
                } else if (busiConferenceAppointment.getType() == 1) {
                    String startTimeStr = busiConferenceAppointment.getStartTime();
                    Date startTime = DateUtil.convertDateByString(startTimeStr, "");
                    if (startTime.getTime() - new Date().getTime() < 7200000) {
                        throw new SystemException(1, "MCU资源已耗尽，创建2小时内的预约会议请先关闭一些会议后重试或者稍后再试。");
                    }
                }
            }
        }
    }

    private void checkResource(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment) {
        Smc2Bridge smc3Bridge = Smc2BridgeCache.getInstance().getBridgesByDept(busiConferenceAppointment.getDeptId());
        if (smc3Bridge != null) {
//            if (smc3Bridge.getUsedResourceCount() >= smc3Bridge.getSystemResourceCount()) {
//                if (busiConferenceAppointment.getType() == 2) {
//                    throw new SystemException(1, "MCU资源已耗尽，请关闭一些会议后重试或者稍后再试。");
//                } else if (busiConferenceAppointment.getType() == 1) {
//                    String startTimeStr = busiConferenceAppointment.getStartTime();
//                    Date startTime = DateUtil.convertDateByString(startTimeStr, "");
//                    if (startTime.getTime() - new Date().getTime() < 7200000) {
//                        throw new SystemException(1, "MCU资源已耗尽，创建2小时内的预约会议请先关闭一些会议后重试或者稍后再试。");
//                    }
//                }
//            }
        }
    }

    private ModelBean getTemplateInfo(String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                ModelBean modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_ZJ: {
                ModelBean modelBean = busiMcuZjTemplateConferenceService.selectBusiMcuZjTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_PLC: {
                ModelBean modelBean = busiMcuPlcTemplateConferenceService.selectBusiMcuPlcTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_KDC: {
                ModelBean modelBean = busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case SMC3: {
                ModelBean modelBean = busiMcuSmc3TemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                Object templateConference = modelBean.get("templateConference");
                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuSmc3TemplateConference.class);
//                String cascadeNodes = busiMcuSmc3TemplateConference.getCascadeNodes();
//                if(Strings.isNotBlank(cascadeNodes)&&!Objects.equals("null",cascadeNodes)){
//                    modelBean.put("cascadeNodes",JSONArray.parseArray(cascadeNodes, TemplateNode.class));
//                }
                String cascadeNodesTemp = busiMcuSmc3TemplateConference.getCascadeNodesTemp();
                if(Strings.isNotBlank(cascadeNodesTemp)&&!Objects.equals("null",cascadeNodesTemp)){
                    modelBean.put("cascadeNodesTemp",JSONArray.parseArray(cascadeNodesTemp, TemplateNode.class));
                }
                Map<String, Object> businessProperties = busiMcuSmc3TemplateConference.getBusinessProperties();
                if(businessProperties!=null){
                    ModelBean tmb = new ModelBean(busiMcuSmc3TemplateConference);
                    tmb.put("videoProtocol", businessProperties.get("videoProtocol"));
                    tmb.put("audioProtocol", businessProperties.get("audioProtocol"));
                    tmb.put("mainMcuId", businessProperties.get("mainMcuId"));
                    tmb.put("mainServiceZoneId", businessProperties.get("mainServiceZoneId"));
                    tmb.put("mainServiceZoneName", businessProperties.get("mainServiceZoneName"));
                    tmb.put("videoResolution", businessProperties.get("videoResolution"));
                    tmb.put("mainMcuName", businessProperties.get("mainMcuName"));
                    tmb.put("streamService", businessProperties.get("streamService"));
                    modelBean.put("templateConference",tmb);
                }

                return modelBean;
            }
            case SMC2: {
                ModelBean modelBean = busiMcuSmc2TemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }
            case MCU_TENCENT: {
                ModelBean modelBean = busiMcuTencentTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                return modelBean;
            }

            case MCU_HWCLOUD: {
                ModelBean modelBean = busiMcuHwcloudTemplateConferenceService.selectBusiTemplateConferenceById(id);
                modelBean.put("conferenceId", conferenceId);
                modelBean.put("mcuType", mcuType.getCode());
                modelBean.put("mcuTypeAlias", mcuType.getAlias());
                List<ModelBean> cascadeTemplateConferences = new ArrayList<>();
                ViewTemplateConference viewTemplateConferenceCascadeCon = new ViewTemplateConference();
                viewTemplateConferenceCascadeCon.setUpCascadeId(id);
                viewTemplateConferenceCascadeCon.setUpCascadeMcuType(mcuType.getCode());
                List<ViewTemplateConference> viewTemplateConferenceCascadeList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCascadeCon);
                for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceCascadeList) {
                    ModelBean modelBeanCascade = getTemplateInfo(viewTemplateConference.getConferenceId());
                    if (modelBeanCascade != null) {
                        cascadeTemplateConferences.add(modelBeanCascade);
                    }
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (cascadeTemplateConferences.size() > 0) {
                    modelBean.put("cascadeTemplateConferences", cascadeTemplateConferences);
                }
                if (modelBean.containsKey("upCascadeConferenceId")) {
                    modelBean.put("isDownCascade", true);
                } else {
                    modelBean.put("isDownCascade", false);
                }
                if (viewTemplateConferenceCascadeList.size() > 0) {
                    modelBean.put("isUpCascade", true);
                } else {
                    modelBean.put("isUpCascade", false);
                }
                Object templateConference = modelBean.get("templateConference");
                BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference = JSONObject.parseObject(JSONObject.toJSONString(templateConference), BusiMcuHwcloudTemplateConference.class);
                Map<String, Object> businessProperties = busiMcuHwcloudTemplateConference.getBusinessProperties();
                if (businessProperties != null) {
                    ModelBean tmb = new ModelBean(busiMcuHwcloudTemplateConference);
                    tmb.put("callInRestriction", businessProperties.get("callInRestriction"));
                    tmb.put("enableWaitingRoom", businessProperties.get("enableWaitingRoom"));
                    tmb.put("allowGuestStartConf", businessProperties.get("allowGuestStartConf"));
                    tmb.put("isSendNotify", businessProperties.get("isSendNotify"));
                    tmb.put("isSendCalendar", businessProperties.get("isSendCalendar"));
                    modelBean.put("templateConference", tmb);
                    modelBean.put("templateParticipants", businessProperties.get("attendees"));
                }

                return modelBean;
            }
        }
        return null;
    }

    private void deleteCascadeTemplateConference(ViewTemplateConference viewTemplateConference) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                busiTemplateConferenceMapper.deleteBusiTemplateConferenceById(templateId);
            }
            case MCU_ZJ: {
                busiMcuZjTemplateConferenceMapper.deleteBusiMcuZjTemplateConferenceById(templateId);
            }
            case MCU_PLC: {
                busiMcuPlcTemplateConferenceMapper.deleteBusiMcuPlcTemplateConferenceById(templateId);
            }
            case MCU_KDC: {
                busiMcuKdcTemplateConferenceMapper.deleteBusiMcuKdcTemplateConferenceById(templateId);
            }
            case SMC3: {
                busiMcuSmc3TemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
            case SMC2: {
                busiMcuSmc2TemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
            case MCU_TENCENT: {
                busiMcuTencentTemplateConferenceService.deleteBusiTemplateConferenceById(templateId);
            }
        }
    }

    private void updateCascadeTemplateConference(ViewTemplateConference viewTemplateConference, String upCascadeConferenceId, int upCascadeIndex) {
        updateCascadeTemplateConference(viewTemplateConference, upCascadeConferenceId, UpCascadeType.SELECT_TEMPLATE_OUT_MEETING, upCascadeIndex);
    }

    private void updateCascadeTemplateConference(ViewTemplateConference viewTemplateConference, String upCascadeConferenceId, UpCascadeType upCascadeType, int upCascadeIndex) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiTemplateConference busiTemplateConferenceUpdate = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiTemplateConferenceUpdate = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiTemplateConferenceUpdate = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiTemplateConferenceUpdate = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC3: {
                BusiMcuSmc3TemplateConference busiTemplateConferenceUpdate = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC2: {
                BusiMcuSmc2TemplateConference busiTemplateConferenceUpdate = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_TENCENT: {
                BusiMcuTencentTemplateConference busiTemplateConferenceUpdate = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

            case MCU_DING: {
                BusiMcuDingTemplateConference busiTemplateConferenceUpdate = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(upCascadeConferenceId);
                    busiTemplateConferenceUpdate.setUpCascadeId(conferenceIdVo.getId());
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(conferenceIdVo.getMcuType().getCode());
                    busiTemplateConferenceUpdate.setUpCascadeType(upCascadeType.getCode());
                    busiTemplateConferenceUpdate.setUpCascadeIndex(upCascadeIndex);
                    busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

        }
    }

    private void recoverUpdateCascadeTemplateConference(ViewTemplateConference viewTemplateConference) {
        Long templateId = viewTemplateConference.getId();
        String mcuTypeStr = viewTemplateConference.getMcuType();
        McuType mcuType = McuType.convert(mcuTypeStr);
        switch (mcuType) {
            case FME: {
                BusiTemplateConference busiTemplateConferenceUpdate = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiTemplateConferenceUpdate = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiTemplateConferenceUpdate = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiTemplateConferenceUpdate = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC3: {
                BusiMcuSmc3TemplateConference busiTemplateConferenceUpdate = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
            case SMC2: {
                BusiMcuSmc2TemplateConference busiTemplateConferenceUpdate = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

            case MCU_TENCENT: {
                BusiMcuTencentTemplateConference busiTemplateConferenceUpdate = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }

            case MCU_DING: {
                BusiMcuDingTemplateConference busiTemplateConferenceUpdate = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(templateId);
                if (busiTemplateConferenceUpdate != null) {
                    busiTemplateConferenceUpdate.setUpCascadeId(null);
                    busiTemplateConferenceUpdate.setUpCascadeMcuType(null);
                    busiTemplateConferenceUpdate.setUpCascadeType(null);
                    busiTemplateConferenceUpdate.setUpCascadeIndex(null);
                    busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConferenceUpdate);
                }
                break;
            }
        }
        BaseConferenceContext conferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(viewTemplateConference.getConferenceId()));
        if (conferenceContext != null) {
            conferenceContext.setUpCascadeConferenceId(null);
            conferenceContext.setUpCascadeIndex(null);
            conferenceContext.setUpCascadeRemoteParty(null);
        }
    }

    public RestResponse updateDefaultViewConfigInfo(JSONObject jsonObj, String conferenceId) {
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasConferenceId(conferenceId);
        Long id = conferenceIdVo.getId();
        McuType mcuType = conferenceIdVo.getMcuType();
        switch (mcuType) {
            case FME: {
                busiTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
                return RestResponse.success();
            }
            case MCU_ZJ: {
                BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(id);
                if (busiMcuZjTemplateConference != null) {
                    McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjTemplateConference.getDeptId()).getMasterMcuZjBridge();
                    if (mcuZjBridge != null) {
                        SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(busiMcuZjTemplateConference.getResourceTemplateId());
                        if (sourceTemplate.getSingle_view() == 1) {
                            if (jsonObj.containsKey("guestDefaultViewData")) {
                                JSONObject jsonObjectGuest = jsonObj.getJSONObject("guestDefaultViewData");
                                busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfoForGuest(jsonObjectGuest, id);
                            } else if (jsonObj.containsKey("speakerDefaultViewData")) {
                                JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                                busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfoForGuest(jsonObjectSpeaker, id);
                            }
                        } else {
                            JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                            busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfo(jsonObjectSpeaker, id);
                            if (jsonObj.containsKey("guestDefaultViewData")) {
                                JSONObject jsonObjectGuest = jsonObj.getJSONObject("guestDefaultViewData");
                                busiMcuZjTemplateConferenceService.updateDefaultViewConfigInfoForGuest(jsonObjectGuest, id);
                            }
                        }
                        return RestResponse.success();
                    }
                }
            }
            case MCU_PLC: {
                BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(id);
                if (busiMcuPlcTemplateConference != null) {
                    McuPlcBridge mcuPlcBridge = McuPlcBridgeCache.getInstance().getAvailableMcuPlcBridgesByDept(busiMcuPlcTemplateConference.getDeptId()).getMasterMcuPlcBridge();
                    if (mcuPlcBridge != null) {
                        JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                        busiMcuPlcTemplateConferenceService.updateDefaultViewConfigInfo(jsonObjectSpeaker, id);
                        return RestResponse.success();
                    }
                }
            }
            case MCU_KDC: {
                BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(id);
                if (busiMcuKdcTemplateConference != null) {
                    McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(busiMcuKdcTemplateConference.getDeptId()).getMasterMcuKdcBridge();
                    if (mcuKdcBridge != null) {
                        JSONObject jsonObjectSpeaker = jsonObj.getJSONObject("speakerDefaultViewData");
                        busiMcuKdcTemplateConferenceService.updateDefaultViewConfigInfo(jsonObjectSpeaker, id);
                        return RestResponse.success();
                    }
                }
            }
            case SMC3: {
                busiMcuSmc3TemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
            }
            case SMC2: {
                busiMcuSmc2TemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
            }
            case MCU_TENCENT: {
                busiMcuTencentTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, id);
            }
        }
        return RestResponse.fail();
    }
}
