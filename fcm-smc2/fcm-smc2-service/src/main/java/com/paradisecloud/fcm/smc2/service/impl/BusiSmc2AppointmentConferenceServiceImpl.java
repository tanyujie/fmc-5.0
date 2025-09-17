package com.paradisecloud.fcm.smc2.service.impl;

import com.alibaba.fastjson.util.TypeUtils;
import com.paradisecloud.com.fcm.smc.modle.ConferenceTimeType;
import com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate;
import com.paradisecloud.com.fcm.smc.modle.request.BusiSmcAppointmentConferenceQuery;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.smc2.conference.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.dao.mapper.BusiSmc2AppointmentConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmc2DeptTemplateMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmc2TemplateTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiSmc2AppointmentConference;
import com.paradisecloud.fcm.dao.model.BusiSmc2DeptTemplate;
import com.paradisecloud.fcm.smc2.model.BusiSmc2AppointmentConferenceRequest;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2AppointmentConferenceService;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2DeptTemplateService;
import com.paradisecloud.fcm.smc2.task.Smc2DelayTaskService;
import com.paradisecloud.fcm.smc2.task.DeleteTemplateTaskSmc2;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2023-05-04
 */
@Service
@Transactional
public class BusiSmc2AppointmentConferenceServiceImpl implements IBusiSmc2AppointmentConferenceService 
{
    @Resource
    private BusiSmc2AppointmentConferenceMapper busiSmc2AppointmentConferenceMapper;
    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;
    @Resource
    private IBusiSmc2DeptTemplateService iBusiSmc2DeptTemplateService;
    @Resource
    private BusiSmc2TemplateTerminalMapper busiSmc2TemplateTerminalMapper;
    @Resource
    private BusiSmc2DeptTemplateMapper busiSmc2DeptTemplateMapper;
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmc2AppointmentConference selectBusiSmc2AppointmentConferenceById(Integer id)
    {
        return busiSmc2AppointmentConferenceMapper.selectBusiSmc2AppointmentConferenceById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmc2AppointmentConference 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmc2AppointmentConference> selectBusiSmc2AppointmentConferenceList(BusiSmc2AppointmentConference busiSmc2AppointmentConference)
    {
        return busiSmc2AppointmentConferenceMapper.selectBusiSmc2AppointmentConferenceList(busiSmc2AppointmentConference);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param appointmentConferenceRequest 【请填写功能名称】
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object insertBusiSmc2AppointmentConference(BusiSmc2AppointmentConferenceRequest appointmentConferenceRequest) throws Exception {

        String scheduleStartTime = appointmentConferenceRequest.getScheduleStartTime();
        String conferenceTimeType = appointmentConferenceRequest.getConferenceTimeType();
        Date date;
        if(Objects.equals(conferenceTimeType, ConferenceTimeType.INSTANT_CONFERENCE.name())){
            date=new Date();
        }else {
            date=DateUtil.convertDateByString(scheduleStartTime,null);
        }
        Integer duration = appointmentConferenceRequest.getDuration();
        Date endDate = DateUtils.addMinutes(date, duration);


        BusiSmc2AppointmentConference busiSmc2AppointmentConference = new BusiSmc2AppointmentConference();
        BeanUtils.copyProperties(appointmentConferenceRequest,busiSmc2AppointmentConference);
        busiSmc2AppointmentConference.setDeptId(appointmentConferenceRequest.getDeptId());
        busiSmc2AppointmentConference.setStartDate(DateUtil.convertDateToString(date,null));
        busiSmc2AppointmentConference.setEndDate(DateUtil.convertDateToString(endDate,null));
        String accessCode = busiSmc2AppointmentConference.getAccessCode();
        if (Strings.isBlank(accessCode))
        {
            BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(TypeUtils.castToLong(accessCode));
            if (busiConferenceNumber == null)
            {
                // 根据输入的号码初始化号码，再自动创建模板
                busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(Long.valueOf(appointmentConferenceRequest.getDeptId().longValue()), McuType.SMC2.getCode());
                accessCode=busiConferenceNumber.getId().toString();
                busiSmc2AppointmentConference.setAccessCode(accessCode);
            }
        }
        // 绑定终端归属部门
        if (busiSmc2AppointmentConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long deptId = loginUser.getUser().getDeptId();
            if(deptId==null){
                busiSmc2AppointmentConference.setDeptId(Long.valueOf(1));
            }else {
                busiSmc2AppointmentConference.setDeptId(loginUser.getUser().getDeptId());
            }


        }
        if (busiSmc2AppointmentConference.getDeptId() == null)
        {
            throw new SystemException(1004543, "添加会议模板，部门ID不能为空！");
        }
        if (busiSmc2AppointmentConference.getCreateUser() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiSmc2AppointmentConference.setCreateUser(loginUser.getUser().getUserName());
            }
        }
        busiSmc2AppointmentConference.setCreateTime(new Date());
        busiSmc2AppointmentConference.setActive(2);
        if(Objects.equals(conferenceTimeType, ConferenceTimeType.INSTANT_CONFERENCE.name())){
            busiSmc2AppointmentConference.setActive(1);
        }
        int c = busiSmc2AppointmentConferenceMapper.insertBusiSmc2AppointmentConference(busiSmc2AppointmentConference);

        if (c > 0)
        {
            BusiSmc2DeptTemplate busiSmc2DeptTemplate = new BusiSmc2DeptTemplate();
            com.paradisecloud.common.utils.bean.BeanUtils.copyProperties(appointmentConferenceRequest,busiSmc2DeptTemplate);
            busiSmc2DeptTemplate.setTemplateName(appointmentConferenceRequest.getSubject());
            if(appointmentConferenceRequest.getDeptId()!=null){
                busiSmc2DeptTemplate.setDeptId(appointmentConferenceRequest.getDeptId().intValue());
            }
            SmcConferenceTemplate smcConferenceTemplate1 = iBusiSmc2DeptTemplateService.insertBusiSmc2DeptTemplate(busiSmc2DeptTemplate, appointmentConferenceRequest.getTemplateTerminalList());
            if(smcConferenceTemplate1!=null){
                busiSmc2AppointmentConference.setSmc2TemplateId(Integer.valueOf(smcConferenceTemplate1.getId()));
                busiSmc2AppointmentConference.setIsStart(1);
                busiSmc2AppointmentConferenceMapper.updateBusiSmc2AppointmentConference(busiSmc2AppointmentConference);
                if(Objects.equals(conferenceTimeType, ConferenceTimeType.INSTANT_CONFERENCE.name())){
                    new Thread(()->{
                        try {
                            Thread.sleep(200);
                            startAppointmentConference(busiSmc2AppointmentConference, smcConferenceTemplate1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }

            }else {
                throw new SystemException(1004544, "添加会议模板失敗！");
            }
        }
        return busiSmc2AppointmentConference;
    }

    private void startAppointmentConference(BusiSmc2AppointmentConference busiSmc2AppointmentConference, SmcConferenceTemplate smcConferenceTemplate1) {


        BusiSmc2DeptTemplate busiSmc2DeptTemplate1 = new BusiSmc2DeptTemplate();
        busiSmc2DeptTemplate1.setSmc2TemplateId(Integer.valueOf(smcConferenceTemplate1.getId()));
        List<BusiSmc2DeptTemplate> busiSmc2DeptTemplates = iBusiSmc2DeptTemplateService.selectBusiSmc2DeptTemplateList(busiSmc2DeptTemplate1);
        BusiSmc2DeptTemplate busiSmc2DeptTemplate2 = busiSmc2DeptTemplates.get(0);
        new StartTemplateConference().startTemplateConference(busiSmc2DeptTemplate2.getId());

    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param appointmentConferenceRequest 【请填写功能名称】
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBusiSmc2AppointmentConference(BusiSmc2AppointmentConferenceRequest appointmentConferenceRequest)
    {

        BusiSmc2AppointmentConference busiSmc2AppointmentConference =selectBusiSmc2AppointmentConferenceById(appointmentConferenceRequest.getId());
        int smc2TemplateId = busiSmc2AppointmentConference.getSmc2TemplateId();

        BeanUtils.copyProperties(appointmentConferenceRequest,busiSmc2AppointmentConference);
        busiSmc2AppointmentConference.setDeptId(appointmentConferenceRequest.getDeptId());
        busiSmc2AppointmentConference.setStartDate(appointmentConferenceRequest.getScheduleStartTime());
        Date endDate = org.apache.commons.lang.time.DateUtils.addMinutes(DateUtil.convertDateByString(appointmentConferenceRequest.getScheduleStartTime(),null), appointmentConferenceRequest.getDuration());
        busiSmc2AppointmentConference.setEndDate(DateUtil.convertDateToString(endDate,null));
        String accessCode = busiSmc2AppointmentConference.getAccessCode();
        if (Strings.isBlank(accessCode))
        {
            BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(TypeUtils.castToLong(accessCode));
            if (busiConferenceNumber == null)
            {
                // 根据输入的号码初始化号码，再自动创建模板
                busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(Long.valueOf(appointmentConferenceRequest.getDeptId().longValue()), McuType.SMC2.getCode());
                accessCode=busiConferenceNumber.getId().toString();
                busiSmc2AppointmentConference.setAccessCode(accessCode);
            }
        }
        busiSmc2AppointmentConference.setUpdateTime(new Date());

        BusiSmc2DeptTemplate busiSmc2DeptTemplate1 = new BusiSmc2DeptTemplate();
        busiSmc2DeptTemplate1.setSmc2TemplateId(smc2TemplateId);

        List<BusiSmc2DeptTemplate> busiSmc2DeptTemplates = busiSmc2DeptTemplateMapper.selectBusiSmc2DeptTemplateList(busiSmc2DeptTemplate1);
        if (CollectionUtils.isEmpty(busiSmc2DeptTemplates)) {
          throw new CustomException("模版不存在");
        }
        BusiSmc2DeptTemplate busiSmc2DeptTemplate = busiSmc2DeptTemplates.get(0);
        Integer id = busiSmc2DeptTemplate.getId();

        busiSmc2TemplateTerminalMapper.deleteBusiSmc2TemplateTerminalByTemplateId(busiSmc2DeptTemplates.get(0).getId());
        com.paradisecloud.common.utils.bean.BeanUtils.copyProperties(busiSmc2AppointmentConference,busiSmc2DeptTemplates.get(0));
        busiSmc2DeptTemplate.setTemplateName(appointmentConferenceRequest.getSubject());
        busiSmc2DeptTemplate.setSmc2TemplateId(smc2TemplateId);
        busiSmc2DeptTemplate.setId(id);
        SmcConferenceTemplate smcConferenceTemplate1 = iBusiSmc2DeptTemplateService.updateBusiSmc2DeptTemplate(busiSmc2DeptTemplate, appointmentConferenceRequest.getTemplateTerminalList());
        if(smcConferenceTemplate1==null){
            throw new SystemException(1004544, "修改预约会议模板失敗！");
        }
        String conferenceTimeType = appointmentConferenceRequest.getConferenceTimeType();
        int i = 0;
        if(Objects.equals(conferenceTimeType, ConferenceTimeType.INSTANT_CONFERENCE.name())){
            busiSmc2AppointmentConference.setActive(1);
            i= busiSmc2AppointmentConferenceMapper.updateBusiSmc2AppointmentConference(busiSmc2AppointmentConference);
            new Thread(()->{
                try {
                    Thread.sleep(200);
                    startAppointmentConference(busiSmc2AppointmentConference, smcConferenceTemplate1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            i =  busiSmc2AppointmentConferenceMapper.updateBusiSmc2AppointmentConference(busiSmc2AppointmentConference);
        }
        return i;
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2AppointmentConferenceByIds(Integer[] ids)
    {
        return busiSmc2AppointmentConferenceMapper.deleteBusiSmc2AppointmentConferenceByIds(ids);
    }

    @Resource
    private Smc2DelayTaskService smc2DelayTaskService;

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmc2AppointmentConferenceById(Integer id)
    {
        BusiSmc2AppointmentConference busiSmcAppointmentConference =selectBusiSmc2AppointmentConferenceById(id);
        int smc2TemplateId = busiSmcAppointmentConference.getSmc2TemplateId();
        BusiSmc2DeptTemplate busiSmc2DeptTemplate1 = new BusiSmc2DeptTemplate();
        busiSmc2DeptTemplate1.setSmc2TemplateId(smc2TemplateId);
        List<BusiSmc2DeptTemplate> busiSmc2DeptTemplates = iBusiSmc2DeptTemplateService.selectBusiSmc2DeptTemplateList(busiSmc2DeptTemplate1);
        BusiSmc2DeptTemplate busiSmc2DeptTemplate = busiSmc2DeptTemplates.get(0);
        iBusiSmc2DeptTemplateService.deleteBusiSmc2DeptTemplateById(busiSmc2DeptTemplate.getId());
        DeleteTemplateTaskSmc2 deleteTemplateTask = new DeleteTemplateTaskSmc2(smc2TemplateId + "", 200, (long) smc2TemplateId);
        smc2DelayTaskService.addTask(deleteTemplateTask);
        return busiSmc2AppointmentConferenceMapper.deleteBusiSmc2AppointmentConferenceById(id);
    }

    @Override
    public List<BusiSmc2AppointmentConference> selectBusiSmcAppointmentConferenceQuery(BusiSmcAppointmentConferenceQuery query) {
        Long deptId = query.getDeptId();
        return busiSmc2AppointmentConferenceMapper.selectBusiSmcAppointmentConferenceQuery(deptId, query.getSearchKey(),query.getStartTime(),query.getEndTime(),2);
    }

}
