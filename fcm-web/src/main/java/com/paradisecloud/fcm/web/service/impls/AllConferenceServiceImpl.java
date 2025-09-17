package com.paradisecloud.fcm.web.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudConferenceAppointmentService;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.eunm.NotifyType;
import com.paradisecloud.fcm.service.interfaces.IAllConferenceService;
import com.paradisecloud.fcm.service.model.CloudConference;
import com.paradisecloud.fcm.service.task.CloudSmsLocaltoRemoteTask;
import com.paradisecloud.fcm.service.util.HuaweiCloudUtil;
import com.paradisecloud.fcm.service.util.TencentCloudUtil;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentConferenceAppointmentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.paradisecloud.fcm.web.task.ConferenceTakeSnapshotPdfTask;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AllConferenceServiceImpl implements IAllConferenceService {
    protected final Logger logger = LoggerFactory.getLogger(AllConferenceServiceImpl.class);

    @Resource
    private BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper;
    @Resource
    private IBusiMcuTencentConferenceAppointmentService busiMcuTencentConferenceAppointmentService;
    @Resource
    private BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper;
    @Resource
    private IBusiMcuHwcloudConferenceAppointmentService busiMcuHwcloudConferenceAppointmentService;
    @Resource
    private IBusiConferenceService busiConferenceService;
    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    @Resource
    private IBusiTencentConferenceService busiTencentConferenceService;
    @Resource
    private IBusiHwcloudConferenceService busiHwcloudConferenceService;

    @Override
    public void processAfterEndConference(BaseConferenceContext baseConferenceContext) {
        if (baseConferenceContext instanceof ConferenceContext) {
            ConferenceContext conferenceContext = (ConferenceContext) baseConferenceContext;
            processAfterEndConferenceForFme(conferenceContext);
        }
    }

    private void processAfterEndConferenceForFme(ConferenceContext conferenceContext) {
        try {
            String region = ExternalConfigCache.getInstance().getRegion();
            logger.info("发送短信验证是否是ops:" + region);
            if (Objects.equals("ops", region)) {
                String cfname = conferenceContext.getName();
                String cfNumber = conferenceContext.getConferenceNumber();
                Date startTime1 = conferenceContext.getStartTime();
                String endTime = DateUtil.convertDateToString(new Date(), null);
                SysUserMapper sysUserMapper = BeanFactory.getBean(SysUserMapper.class);
                SysUser admin = sysUserMapper.selectUserByUserName("admin");
                if (admin != null && admin.getPhonenumber() != null) {
                    TaskService taskService = BeanFactory.getBean(TaskService.class);
                    CloudSmsLocaltoRemoteTask cloudSmsLocaltoRemoteTask = new CloudSmsLocaltoRemoteTask(conferenceContext.getId().toString(), 1000, cfname, cfNumber, admin.getPhonenumber(), DateUtil.convertDateToString(startTime1, null), endTime, NotifyType.ADMIN_MEETING_END);
                    taskService.addTask(cloudSmsLocaltoRemoteTask);
                }
            }


        } catch (Exception e) {
            logger.info("发送短信验 error" + e.getMessage());
        }

        try {
            ConferenceTakeSnapshotPdfTask conferenceTakeSnapshotPdfTask = new ConferenceTakeSnapshotPdfTask(conferenceContext.getId(), 5000,conferenceContext);
            conferenceTakeSnapshotPdfTask.start();

        } catch (Exception e) {
            logger.info("纪要生成 error" + e.getMessage());
        }

        try {
            List<CloudConference> cloudConferenceList = conferenceContext.getCloudConferenceList();
            if (cloudConferenceList != null && cloudConferenceList.size() > 0) {
                for (CloudConference cloudConference : cloudConferenceList) {
                    String mcuTypStr = cloudConference.getCascadeMcuType();
                    McuType cloudmcuType = McuType.convert(mcuTypStr);
                    switch (cloudmcuType) {
                        case MCU_TENCENT: {
                            TencentCloudUtil.endConference(cloudConference);
                            break;
                        }
                        case MCU_HWCLOUD: {
                            HuaweiCloudUtil.endConference(cloudConference);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.info("TencentCloudUtil  endConference error" + e.getMessage());
        }
        try {
            ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
            ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
            viewTemplateConferenceCon.setUpCascadeId(conferenceContext.getTemplateConferenceId());
            viewTemplateConferenceCon.setUpCascadeMcuType(McuType.FME.getCode());
            List<ViewTemplateConference> downCascadeList = viewTemplateConferenceMapper.selectViewTemplateConferenceList(viewTemplateConferenceCon);
            for (ViewTemplateConference viewTemplateConference : downCascadeList) {
                if (McuType.MCU_TENCENT.getCode().equals(viewTemplateConference.getMcuType())) {
                    List<BusiMcuTencentConferenceAppointment> busiMcuTencentConferenceAppointmentList = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentByTemplateId(viewTemplateConference.getId());
                    if (busiMcuTencentConferenceAppointmentList.size() > 0) {
                        BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuTencentConferenceAppointmentList.get(0);
                        if (busiMcuTencentConferenceAppointment.getIsCloudConference() != null && busiMcuTencentConferenceAppointment.getIsCloudConference() == 1) {
                            try {
                                busiMcuTencentConferenceAppointmentService.deleteBusiMcuTencentConferenceAppointmentById(busiMcuTencentConferenceAppointment.getId());
                            } catch (Exception e) {
                            }
                        }
                    }
                } else if (McuType.MCU_HWCLOUD.getCode().equals(viewTemplateConference.getMcuType())) {
                    List<BusiMcuHwcloudConferenceAppointment> busiMcuHwcloudConferenceAppointmentList = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentByTemplateId(viewTemplateConference.getId());
                    if (busiMcuHwcloudConferenceAppointmentList.size() > 0) {
                        BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = busiMcuHwcloudConferenceAppointmentList.get(0);
                        if (busiMcuHwcloudConferenceAppointment.getIsCloudConference() != null && busiMcuHwcloudConferenceAppointment.getIsCloudConference() == 1) {
                            try {
                                busiMcuHwcloudConferenceAppointmentService.deleteBusiMcuHwcloudConferenceAppointmentById(busiMcuHwcloudConferenceAppointment.getId());
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        try {
            String packetConferenceId = conferenceContext.getPacketConferenceId();
            if (Strings.isNotBlank(packetConferenceId)) {
                busiConferenceService.endConference(packetConferenceId, 1, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                ConferenceIdVo conferenceIdVoP = EncryptIdUtil.parasConferenceId(packetConferenceId);
                busiTemplateConferenceService.deleteBusiTemplateConferenceById(conferenceIdVoP.getId());
            }

        } catch (Exception e) {
            logger.info("TencentCloudUtil  endConference error" + e.getMessage());
        }

        try {
            BusiTemplateConference btc = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(conferenceContext.getTemplateConferenceId());
            if (btc != null) {
                Map<String, Object> businessProperties = btc.getBusinessProperties();
                if (businessProperties != null) {
                    Object cloudMcuType = businessProperties.get("cloudMcuType");
                    if (cloudMcuType != null) {
                        if(cloudMcuType instanceof JSONArray){
                            JSONArray jsonArray=(JSONArray) cloudMcuType;
                            for (Object obj : jsonArray) {
                                McuType mcuType_cloud = McuType.convert(obj.toString());
                                switch (mcuType_cloud) {
                                    case MCU_TENCENT: {
                                        String cloudtencentId = conferenceContext.getCloudtencentId();
                                        if (cloudtencentId != null) {
                                            busiTencentConferenceService.endConference(conferenceContext.getId(), EndReasonsType.ADMINISTRATOR_HANGS_UP);
                                        }
                                        break;
                                    }
                                    case MCU_HWCLOUD: {
                                        String cloudId = conferenceContext.getCloudHwcloudId();
                                        if (cloudId != null) {
                                            busiHwcloudConferenceService.endConference(conferenceContext.getId(), EndReasonsType.ADMINISTRATOR_HANGS_UP);
                                        }
                                        break;
                                    }
                                }
                            }

                        } else if( cloudMcuType instanceof String){
                            if (Strings.isNotBlank((String) cloudMcuType)) {
                                McuType mcuType_cloud = McuType.convert(cloudMcuType.toString().toUpperCase());
                                switch (mcuType_cloud) {
                                    case MCU_TENCENT: {
                                        String cloudtencentId = conferenceContext.getCloudtencentId();
                                        if (cloudtencentId != null) {
                                            busiTencentConferenceService.endConference(conferenceContext.getId(), EndReasonsType.ADMINISTRATOR_HANGS_UP);
                                        }
                                        break;
                                    }
                                    case MCU_HWCLOUD: {
                                        String cloudId = conferenceContext.getCloudHwcloudId();
                                        if (cloudId != null) {
                                            busiHwcloudConferenceService.endConference(conferenceContext.getId(), EndReasonsType.ADMINISTRATOR_HANGS_UP);
                                        }
                                        break;
                                    }

                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
