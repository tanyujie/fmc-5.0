package com.paradisecloud.smc.service.impl;

import java.util.List;
import java.util.Date;
import java.util.Objects;

import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.request.AppointmentConference;
import com.paradisecloud.com.fcm.smc.modle.request.BusiSmcAppointmentConferenceQuery;
import com.paradisecloud.com.fcm.smc.modle.request.SmcAppointmentConferenceRequest;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConference;
import com.paradisecloud.smc.dao.model.BusiSmcDeptConference;
import com.paradisecloud.smc.dao.model.SmcTemplateTerminal;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcAppointmentConferenceMapper;
import com.paradisecloud.smc.service.BusiSmcDeptConferenceService;
import com.paradisecloud.smc.service.IBusiSmcAppointmentConferenceService;
import io.jsonwebtoken.lang.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import static com.paradisecloud.com.fcm.smc.modle.ConstAPI.SVC_VIDEO_RESOLUTION;
import static com.paradisecloud.com.fcm.smc.modle.ConstAPI.VIDEO_RESOLUTION;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author lilinhai
 * @date 2023-03-15
 */
@Service
public class BusiSmcAppointmentConferenceServiceImpl implements IBusiSmcAppointmentConferenceService
{
    @Resource
    private BusiSmcDeptConferenceService busiSmcDeptConferenceService;

    @Resource
    private BusiSmcAppointmentConferenceMapper busiSmcAppointmentConferenceMapper;



    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public BusiSmcAppointmentConference selectBusiSmcAppointmentConferenceById(Integer id)
    {
        return busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceById(id);
    }

    @Override
    public BusiSmcAppointmentConference selectBusiSmcAppointmentConferenceByConferenceId(String conferenceId) {

        return busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceByConferenceId(conferenceId);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param busiSmcAppointmentConference 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceList(BusiSmcAppointmentConference busiSmcAppointmentConference)
    {
        return busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceList(busiSmcAppointmentConference);
    }

    @Override
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceQuery(BusiSmcAppointmentConferenceQuery query) {
        Long deptId = query.getDeptId();
        return busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceQuery(deptId, query.getSearchKey(),query.getStartTime(),query.getEndTime(),2);
    }

    @Override
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByStartTime() {
        return busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceByStartTime();
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param busiSmcAppointmentConference 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertBusiSmcAppointmentConference(BusiSmcAppointmentConference busiSmcAppointmentConference)
    {

        return busiSmcAppointmentConferenceMapper.insertBusiSmcAppointmentConference(busiSmcAppointmentConference);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param busiSmcAppointmentConference 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateBusiSmcAppointmentConference(BusiSmcAppointmentConference busiSmcAppointmentConference)
    {
        return busiSmcAppointmentConferenceMapper.updateBusiSmcAppointmentConference(busiSmcAppointmentConference);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcAppointmentConferenceByIds(Integer[] ids)
    {
        return busiSmcAppointmentConferenceMapper.deleteBusiSmcAppointmentConferenceByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteBusiSmcAppointmentConferenceById(Integer id)
    {
        return busiSmcAppointmentConferenceMapper.deleteBusiSmcAppointmentConferenceById(id);
    }

    @Override
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceHistoryQuery(String convertDateToString) {
        return busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceHistoryQuery(convertDateToString);
    }

    @Override
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByStartTimeLt() {
        return busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceByStartTimeLt();
    }

    @Override
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByTime(Date date) {
        return busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceByTime(date);
    }

    @Override
    public List<BusiSmcAppointmentConference> selectBusiSmcAppointmentConferenceByStartTimeLtNoExisTHistory() {
        return busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceByStartTimeLtNoExisTHistory();
    }


    public  static SmcAppointmentConferenceRequest buildSmcAppointmentConference(BusiSmcAppointmentConference busiSmcAppointmentConference) {
        String scheduleStartTime = busiSmcAppointmentConference.getScheduleStartTime();

        Integer amcRecord = busiSmcAppointmentConference.getAmcRecord();
        Integer rate = busiSmcAppointmentConference.getRate();
        Integer autoMute = busiSmcAppointmentConference.getAutoMute();
        String type = busiSmcAppointmentConference.getType();
        Integer maxParticipantNum = busiSmcAppointmentConference.getMaxParticipantNum();
        Integer voiceActive = busiSmcAppointmentConference.getVoiceActive();
        Integer supportLive = busiSmcAppointmentConference.getSupportLive();
        Integer supportRecord = busiSmcAppointmentConference.getSupportRecord();
        Integer enableDataConf = busiSmcAppointmentConference.getEnableDataConf();

        SmcAppointmentConferenceRequest appointmentConferenceRequest = new SmcAppointmentConferenceRequest();
        AppointmentConference appointmentConference = new AppointmentConference();

        String guestPassword = busiSmcAppointmentConference.getGuestPassword();
        String chairmanPassword = busiSmcAppointmentConference.getChairmanPassword();

        appointmentConference.setGuestPassword(guestPassword==null?"":guestPassword);
        appointmentConference.setChairmanPassword(chairmanPassword==null?"":chairmanPassword);
        appointmentConference.setConferenceTimeType(ConferenceTimeType.valueOf(busiSmcAppointmentConference.getConferenceTimeType()));
        appointmentConference.setDuration(busiSmcAppointmentConference.getDuration());
        appointmentConference.setScheduleStartTime(scheduleStartTime);
        appointmentConference.setSubject(busiSmcAppointmentConference.getSubject());
        appointmentConference.setVmrNumber("");

        SmcAppointmentConferenceRequest.MultiConferenceServiceDTO multiConferenceServiceDTO = new SmcAppointmentConferenceRequest.MultiConferenceServiceDTO();

        SmcAppointmentConferenceRequest.MultiConferenceServiceDTO.ConferenceCapabilitySettingDTO conferenceCapabilitySetting = new SmcAppointmentConferenceRequest.MultiConferenceServiceDTO.ConferenceCapabilitySettingDTO();
        conferenceCapabilitySetting.setSvcVideoResolution(SVC_VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setVideoResolution(VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setAmcRecord(amcRecord==1?true:false);
        conferenceCapabilitySetting.setAudioProtocol("AAC_LD_S");
        conferenceCapabilitySetting.setAudioRecord(false);
        conferenceCapabilitySetting.setAutoRecord(false);
        conferenceCapabilitySetting.setCheckInDuration(10);
        conferenceCapabilitySetting.setDataConfProtocol("DATA_RESOLUTION_STANDARD");
        conferenceCapabilitySetting.setEnableCheckIn(false);
        conferenceCapabilitySetting.setEnableDataConf(false);
        conferenceCapabilitySetting.setEnableFec(false);
        conferenceCapabilitySetting.setEnableLiveBroadcast(false);
        conferenceCapabilitySetting.setEnableRecord(false);
        conferenceCapabilitySetting.setMediaEncrypt("NOT_ENCRYPT_MODE");
        conferenceCapabilitySetting.setRate(rate);
        conferenceCapabilitySetting.setReserveResource(0);
        conferenceCapabilitySetting.setSvcRate(3840);
        conferenceCapabilitySetting.setVideoProtocol("H265");
        conferenceCapabilitySetting.setType(type);
        conferenceCapabilitySetting.setVideoProtocol("H264_BP");
        conferenceCapabilitySetting.setSrcLang("CHINESE");

        conferenceCapabilitySetting.setEnableRecord(supportRecord == 1 ? true : false);
        conferenceCapabilitySetting.setEnableLiveBroadcast(supportLive == 1 ? true : false);
        conferenceCapabilitySetting.setEnableDataConf(enableDataConf==1?true:false);
        multiConferenceServiceDTO.setConferenceCapabilitySetting(conferenceCapabilitySetting);

        SmcAppointmentConferenceRequest.MultiConferenceServiceDTO.ConferencePolicySettingDTO policySettingDTO = new SmcAppointmentConferenceRequest.MultiConferenceServiceDTO.ConferencePolicySettingDTO();
        policySettingDTO.setAutoEnd(true);
        policySettingDTO.setAutoExtend(true);
        policySettingDTO.setAutoMute(autoMute==1?true:false);
        policySettingDTO.setChairmanPassword(chairmanPassword==null?"":chairmanPassword);
        policySettingDTO.setGuestPassword(guestPassword==null?"":guestPassword);
        policySettingDTO.setLanguage(1);
        policySettingDTO.setVoiceActive(voiceActive==1?true:false);
        policySettingDTO.setMaxParticipantNum(maxParticipantNum);
        policySettingDTO.setReleaseParticipantRes(false);
        multiConferenceServiceDTO.setConferencePolicySetting(policySettingDTO);


        SmcAppointmentConferenceRequest.StreamServiceDTO streamServiceDTO = new SmcAppointmentConferenceRequest.StreamServiceDTO();
        streamServiceDTO.setSupportMinutes(false);
        streamServiceDTO.setAmcRecord(amcRecord==1?true:false);
        streamServiceDTO.setSupportLive(supportLive==1?true:false);
        streamServiceDTO.setAutoRecord(supportRecord==1?true:false);
        streamServiceDTO.setAudioRecord(false);
        streamServiceDTO.setAutoRecord(false);
        appointmentConferenceRequest.setStreamService(streamServiceDTO);

        SmcAppointmentConferenceRequest.SubtitleServiceDTO subtitleServiceDTO = new SmcAppointmentConferenceRequest.SubtitleServiceDTO();
        subtitleServiceDTO.setEnableSubtitle(false);
        subtitleServiceDTO.setSrcLang("CHINESE");
        appointmentConferenceRequest.setSubtitleService(subtitleServiceDTO);
        appointmentConferenceRequest.setConference(appointmentConference);
        appointmentConferenceRequest.setMultiConferenceService(multiConferenceServiceDTO);


        SmcAppointmentConferenceRequest.CheckInServiceDTO checkInServiceDTO = new SmcAppointmentConferenceRequest.CheckInServiceDTO();
        checkInServiceDTO.setEnableCheckIn(false);
        checkInServiceDTO.setCheckInDuration(10);
        appointmentConferenceRequest.setCheckInService(checkInServiceDTO);


        SmcAppointmentConferenceRequest.ConfPresetParamDTO confPresetParamDTO = new SmcAppointmentConferenceRequest.ConfPresetParamDTO();
        appointmentConferenceRequest.setConfPresetParam(confPresetParamDTO);



        return appointmentConferenceRequest;

    }
}
