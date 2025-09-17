package com.paradisecloud.fcm.web.service.impls;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.BusiLiveBroadcastVo;
import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;
import com.paradisecloud.fcm.mqtt.cache.AppointmentCache;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.web.cache.LiveBroadcastCache;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveBroadcastAppointmentMapService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveBroadcastService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllConferenceAppointmentService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiAllMcuService;
import com.paradisecloud.fcm.web.task.LiveRecordsTask;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

import static com.paradisecloud.fcm.common.enumer.McuType.*;

/**
 * 直播记录Service业务层处理
 *
 * @author lilinhai
 * @date 2024-05-07
 */
@Service
public class BusiLiveBroadcastServiceImpl implements IBusiLiveBroadcastService
{
    @Resource
    private BusiLiveBroadcastMapper busiLiveBroadcastMapper;
    @Resource
    private IBusiAllConferenceAppointmentService busiAllConferenceAppointmentService;
    @Resource
    private IBusiAllMcuService busiAllMcuService;
    @Resource
    private IBusiLiveBroadcastAppointmentMapService busiLiveBroadcastAppointmentMapService;
    @Resource
    private ViewConferenceAppointmentMapper viewConferenceAppointmentMapper;
    @Resource
    private ViewTemplateParticipantMapper viewTemplateParticipantMapper;
    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    @Resource
    private BusiMcuZjTemplateParticipantMapper busiMcuZjTemplateParticipantMapper;
    @Resource
    private BusiMcuSmc2TemplateParticipantMapper busiMcuSmc2TemplateParticipantMapper;
    @Resource
    private BusiMcuSmc3TemplateParticipantMapper busiMcuSmc3TemplateParticipantMapper;
    @Resource
    private BusiTemplateParticipantMapper busiTemplateParticipantMapper;
    @Resource
    private TaskService taskService;

    /**
     * 查询直播记录
     *
     * @param id 直播记录ID
     * @return 直播记录
     */
    @Override
    public BusiLiveBroadcast selectBusiLiveBroadcastById(Long id)
    {
        BusiLiveBroadcast busiLiveBroadcast = busiLiveBroadcastMapper.selectBusiLiveBroadcastById(id);
        if (busiLiveBroadcast != null) {
            Integer type = busiLiveBroadcast.getType();
            if (type == 1) {
                BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap = new BusiLiveBroadcastAppointmentMap();
                busiLiveBroadcastAppointmentMap.setLiveBroadcastId(busiLiveBroadcast.getId());
                List<BusiLiveBroadcastAppointmentMap> busiLiveBroadcastAppointmentMaps = busiLiveBroadcastAppointmentMapService.selectBusiLiveBroadcastAppointmentMapList(busiLiveBroadcastAppointmentMap);
                if (busiLiveBroadcastAppointmentMaps != null && busiLiveBroadcastAppointmentMaps.size() > 0) {
                    BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMapTemp = busiLiveBroadcastAppointmentMaps.get(0);
                    Long appointmentId = busiLiveBroadcastAppointmentMapTemp.getAppointmentId();
                    String mcuType = busiLiveBroadcastAppointmentMapTemp.getMcuType();
                    ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType, appointmentId);
                    if (viewConferenceAppointment != null) {
                        Long templateId = viewConferenceAppointment.getTemplateId();
                        ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType, templateId);
                        if (viewTemplateConference != null) {
                            busiLiveBroadcast.getParams().put("conferenceNumber", viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber());
                        }
                    }
                }
            }
            Long idL = busiLiveBroadcast.getId();
            Long deptId = busiLiveBroadcast.getDeptId();
            List<String> streamUrlList = createStreamUrlList(deptId, "l" + idL);
            Map<String, Object> params = busiLiveBroadcast.getParams();
            params.put("StreamUrlList", streamUrlList);
        }
        return busiLiveBroadcast;
    }

    /**
     * 查询直播记录列表
     *
     * @param busiLiveBroadcast 直播记录
     * @return 直播记录
     */
    @Override
    public List<BusiLiveBroadcast> selectBusiLiveBroadcastList(BusiLiveBroadcastVo busiLiveBroadcast)
    {
        List<BusiLiveBroadcast> busiLiveBroadcastList = busiLiveBroadcastMapper.selectBusiLiveBroadcastList(busiLiveBroadcast);
        for (BusiLiveBroadcast liveBroadcast : busiLiveBroadcastList) {
            if (liveBroadcast != null) {
                Integer type = liveBroadcast.getType();
                if (type == 1) {
                    BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap = new BusiLiveBroadcastAppointmentMap();
                    busiLiveBroadcastAppointmentMap.setLiveBroadcastId(liveBroadcast.getId());
                    List<BusiLiveBroadcastAppointmentMap> busiLiveBroadcastAppointmentMaps = busiLiveBroadcastAppointmentMapService.selectBusiLiveBroadcastAppointmentMapList(busiLiveBroadcastAppointmentMap);
                    if (busiLiveBroadcastAppointmentMaps != null && busiLiveBroadcastAppointmentMaps.size() > 0) {
                        BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMapTemp = busiLiveBroadcastAppointmentMaps.get(0);
                        Long appointmentId = busiLiveBroadcastAppointmentMapTemp.getAppointmentId();
                        String mcuType = busiLiveBroadcastAppointmentMapTemp.getMcuType();
                        ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuType, appointmentId);
                        if (viewConferenceAppointment != null) {
                            Long templateId = viewConferenceAppointment.getTemplateId();
                            ViewTemplateConference viewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(mcuType, templateId);
                            if (viewTemplateConference != null) {
                                liveBroadcast.getParams().put("conferenceNumber", viewTemplateConference.getTenantId() + viewTemplateConference.getConferenceNumber());
                            }
                        }
                    }
                }

                Long id = liveBroadcast.getId();
                Long deptId = liveBroadcast.getDeptId();
                List<String> streamUrlList = createStreamUrlList(deptId, "l" + id);
                Map<String, Object> params = liveBroadcast.getParams();
                params.put("StreamUrlList", streamUrlList);
            }

        }
        return busiLiveBroadcastList;
    }

    /**
     * 新增直播记录
     *
     * @param busiLiveBroadcast 直播记录
     * @return 结果
     */
    @Override
    public int insertBusiLiveBroadcast(BusiLiveBroadcast busiLiveBroadcast)
    {
        busiLiveBroadcast.setCreateTime(new Date());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiLiveBroadcast.setCreateBy(loginUser.getUser().getUserName());
        }
        Assert.isTrue(busiLiveBroadcast.getStartTime() != null, "开始时间不能为空");
        Assert.isTrue(busiLiveBroadcast.getEndTime() != null, "结束时间不能为空");
        Assert.isTrue(busiLiveBroadcast.getName() != null, "直播名称不能为空");
        Assert.isTrue(busiLiveBroadcast.getType() != null, "直播类型不能为空");
        Assert.isTrue(busiLiveBroadcast.getDeptId() != null, "部门id不能为空");
        Assert.isTrue(busiLiveBroadcast.getPlaybackEnabled() != null, "是否开启回放不能为空");
        Assert.isTrue(busiLiveBroadcast.getCommentsEnabled() != null, "是否开启评论不能为空");
        Assert.isTrue(busiLiveBroadcast.getGiftsEnabled() != null, "是否开启送礼物不能为空");

        Integer status = busiLiveBroadcast.getStatus();
        if (status == null) {
            busiLiveBroadcast.setStatus(1);
        }
        Integer isStart = busiLiveBroadcast.getIsStart();
        if (isStart == null) {
            busiLiveBroadcast.setIsStart(2);
        }

        Date currentTime = new Date();
        Date diffDate = DateUtils.getDiffDate(currentTime, -60, TimeUnit.SECONDS);
        Date startTimeTemp = DateUtils.convertToDate(busiLiveBroadcast.getStartTime());
        Date endTimeTemp = DateUtils.convertToDate(busiLiveBroadcast.getEndTime());
        if (startTimeTemp.after(endTimeTemp)) {
            throw new CustomException("开始时间和结束时间不正确！");
        }
        if (startTimeTemp.before(diffDate)) {
            throw new CustomException("不能预约过期的时间！");
        }

        Long deptId = busiLiveBroadcast.getDeptId();
        String streamingUrl = createStreamingUrl(deptId);
        if (StringUtils.isEmpty(streamingUrl)) {
            Assert.isTrue(false, "不能创建直播，该部门未绑定直播服务器！");
        }
        streamingUrl = streamingUrl + "l";
        Integer type = busiLiveBroadcast.getType();
        Long appointmentId = null;
        String mcuType = "";
        if (type == 1) {
            List<McuTypeVo> mcuTypeList = busiAllMcuService.getLiveMcuTypeList(deptId);
            if (mcuTypeList != null && mcuTypeList.size() > 0 ) {
                mcuType = mcuTypeList.get(0).getCode();
            }
            BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
            busiConferenceAppointment.setDeptId(deptId);

            String startTimeStr = busiLiveBroadcast.getStartTime();
            Date startTime = DateUtil.convertDateByString(startTimeStr, null);
            startTime = DateUtils.getDiffDate(startTime, -10, TimeUnit.MINUTES);
            startTimeStr = DateUtil.convertDateToString(startTime, null);

            String endTimeStr = busiLiveBroadcast.getEndTime();
            busiConferenceAppointment.setStartTime(startTimeStr);
            busiConferenceAppointment.setEndTime(endTimeStr);
            busiConferenceAppointment.setIsAutoCreateTemplate(1);
            busiConferenceAppointment.setRepeatRate(1);
            busiConferenceAppointment.setStatus(1);
            busiConferenceAppointment.setType(10);

            BusiConferenceAppointment conferenceAppointment = createConferenceAppointment(busiConferenceAppointment, busiLiveBroadcast);
            try {
                Map<String, Object> map = busiAllConferenceAppointmentService.addConferenceAppointment(conferenceAppointment, mcuType);
                appointmentId = (Long) map.get("appointmentId");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int i = busiLiveBroadcastMapper.insertBusiLiveBroadcast(busiLiveBroadcast);
        if (i > 0) {
            streamingUrl = streamingUrl + busiLiveBroadcast.getId();
            busiLiveBroadcast.setStreamUrl(streamingUrl);
            int i1 = busiLiveBroadcastMapper.updateBusiLiveBroadcast(busiLiveBroadcast);
            if (i1 > 0) {
                if (appointmentId != null) {
                    BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap = new BusiLiveBroadcastAppointmentMap();
                    busiLiveBroadcastAppointmentMap.setAppointmentId(appointmentId);
                    busiLiveBroadcastAppointmentMap.setLiveBroadcastId(busiLiveBroadcast.getId());
                    busiLiveBroadcastAppointmentMap.setMcuType(mcuType);
                    busiLiveBroadcastAppointmentMap.setCreateBy(busiLiveBroadcast.getCreateBy());
                    busiLiveBroadcastAppointmentMap.setCreateTime(new Date());
                    int i2 = busiLiveBroadcastAppointmentMapService.insertBusiLiveBroadcastAppointmentMap(busiLiveBroadcastAppointmentMap);
                    if (i2 > 0) {
                        LiveBroadcastCache.getInstance().addMap(busiLiveBroadcastAppointmentMap);
                    }
                }
                LiveBroadcastCache.getInstance().add(busiLiveBroadcast);
            }
        }
        return i;
    }

    /**
     * 修改直播记录
     *
     * @param busiLiveBroadcast 直播记录
     * @return 结果
     */
    @Override
    public int updateBusiLiveBroadcast(BusiLiveBroadcast busiLiveBroadcast)
    {

        Assert.isTrue(busiLiveBroadcast.getStartTime() != null, "开始时间不能为空");
        Assert.isTrue(busiLiveBroadcast.getEndTime() != null, "结束时间不能为空");
        Assert.isTrue(busiLiveBroadcast.getName() != null, "直播名称不能为空");
        Assert.isTrue(busiLiveBroadcast.getType() != null, "直播类型不能为空");
        Assert.isTrue(busiLiveBroadcast.getDeptId() != null, "部门id不能为空");
        Assert.isTrue(busiLiveBroadcast.getPlaybackEnabled() != null, "是否开启回放不能为空");
        Assert.isTrue(busiLiveBroadcast.getCommentsEnabled() != null, "是否开启评论不能为空");
        Assert.isTrue(busiLiveBroadcast.getGiftsEnabled() != null, "是否开启送礼物不能为空");

        Date currentTime = new Date();
        Date diffDate = DateUtils.getDiffDate(currentTime, -60, TimeUnit.SECONDS);
        Date startTimeTemp = DateUtils.convertToDate(busiLiveBroadcast.getStartTime());
        Date endTimeTemp = DateUtils.convertToDate(busiLiveBroadcast.getEndTime());
        if (startTimeTemp.after(endTimeTemp)) {
            throw new CustomException("开始时间和结束时间不正确！");
        }
        if (startTimeTemp.before(diffDate)) {
            throw new CustomException("不能预约过期的时间！");
        }

        busiLiveBroadcast.setUpdateTime(new Date());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            busiLiveBroadcast.setUpdateBy(loginUser.getUser().getUserName());
        }
        BusiLiveBroadcast busiLiveBroadcastTemp = busiLiveBroadcastMapper.selectBusiLiveBroadcastById(busiLiveBroadcast.getId());
        if (busiLiveBroadcastTemp.getIsStart() != null && busiLiveBroadcastTemp.getIsStart() == 1) {
            throw new SystemException("直播一开始不能修改！");
        }
        busiLiveBroadcast.setType(busiLiveBroadcastTemp.getType());

        Integer type = busiLiveBroadcast.getType();
        Long deptId = busiLiveBroadcast.getDeptId();
        if (type == 1) {
            Integer terminalId = busiLiveBroadcast.getTerminalId();
            Integer terminalIdOld = busiLiveBroadcastTemp.getTerminalId();
            if (terminalId != terminalIdOld) {
                BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
                busiConferenceAppointment.setDeptId(deptId);

                String startTimeStr = busiLiveBroadcast.getStartTime();
                Date startTime = DateUtil.convertDateByString(startTimeStr, null);
                startTime = DateUtils.getDiffDate(startTime, -10, TimeUnit.MINUTES);
                startTimeStr = DateUtil.convertDateToString(startTime, null);

                String endTimeStr = busiLiveBroadcast.getEndTime();
                busiConferenceAppointment.setStartTime(startTimeStr);
                busiConferenceAppointment.setEndTime(endTimeStr);
                busiConferenceAppointment.setIsAutoCreateTemplate(1);
                busiConferenceAppointment.setRepeatRate(1);
                busiConferenceAppointment.setStatus(1);
                busiConferenceAppointment.setType(1);

                BusiConferenceAppointment conferenceAppointment = createConferenceAppointment(busiConferenceAppointment, busiLiveBroadcast);
                BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap = new BusiLiveBroadcastAppointmentMap();
                busiLiveBroadcastAppointmentMap.setLiveBroadcastId(busiLiveBroadcast.getId());
                List<BusiLiveBroadcastAppointmentMap> busiLiveBroadcastAppointmentMaps = busiLiveBroadcastAppointmentMapService.selectBusiLiveBroadcastAppointmentMapList(busiLiveBroadcastAppointmentMap);
                if (busiLiveBroadcastAppointmentMaps != null && busiLiveBroadcastAppointmentMaps.size() > 0) {
                    BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMapTemp = busiLiveBroadcastAppointmentMaps.get(0);
                    Long appointmentId = busiLiveBroadcastAppointmentMapTemp.getAppointmentId();
                    String mcuTypeStr = busiLiveBroadcastAppointmentMapTemp.getMcuType();
                    ViewConferenceAppointment viewConferenceAppointment = viewConferenceAppointmentMapper.selectViewConferenceAppointmentById(mcuTypeStr, appointmentId);
                    if (viewConferenceAppointment != null) {
                        Long templateParticipantId = null;

                        Long templateId = viewConferenceAppointment.getTemplateId();
                        ViewTemplateParticipant viewTemplateParticipant = new ViewTemplateParticipant();
                        viewTemplateParticipant.setTemplateConferenceId(templateId);
                        viewTemplateParticipant.setMcuType(mcuTypeStr);
                        viewTemplateParticipant.setTerminalId(Long.valueOf(busiLiveBroadcastTemp.getTerminalId()));
                        List<ViewTemplateParticipant> viewTemplateParticipants = viewTemplateParticipantMapper.selectViewTemplateParticipantList(viewTemplateParticipant);
                        if (viewTemplateParticipants != null && viewTemplateParticipants.size() > 0) {
                            ViewTemplateParticipant viewTemplateParticipantTemp = viewTemplateParticipants.get(0);
                            templateParticipantId = viewTemplateParticipantTemp.getId();
                        }
                        McuType convert = convert(mcuTypeStr);
                        switch (convert) {
                            case FME -> {

                                BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantMapper.selectBusiTemplateParticipantById(templateParticipantId);
                                busiTemplateParticipant.setTerminalId(Long.valueOf(busiLiveBroadcast.getTerminalId()));
                                busiTemplateParticipantMapper.updateBusiTemplateParticipant(busiTemplateParticipant);
                                break;
                            }
                            case SMC3 -> {
                                BusiMcuSmc3TemplateParticipant busiMcuSmc3TemplateParticipant = busiMcuSmc3TemplateParticipantMapper.selectBusiMcuSmc3TemplateParticipantById(templateParticipantId);
                                busiMcuSmc3TemplateParticipant.setTerminalId(Long.valueOf(busiLiveBroadcast.getTerminalId()));
                                busiMcuSmc3TemplateParticipantMapper.updateBusiMcuSmc3TemplateParticipant(busiMcuSmc3TemplateParticipant);
                                break;
                            }
                            case SMC2 -> {
                                BusiMcuSmc2TemplateParticipant busiMcuSmc2TemplateParticipant = busiMcuSmc2TemplateParticipantMapper.selectBusiMcuSmc2TemplateParticipantById(templateParticipantId);
                                busiMcuSmc2TemplateParticipant.setTerminalId(Long.valueOf(busiLiveBroadcast.getTerminalId()));
                                busiMcuSmc2TemplateParticipantMapper.updateBusiMcuSmc2TemplateParticipant(busiMcuSmc2TemplateParticipant);
                                break;
                            }
                            case MCU_ZJ -> {
                                BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = busiMcuZjTemplateParticipantMapper.selectBusiMcuZjTemplateParticipantById(templateParticipantId);
                                busiMcuZjTemplateParticipant.setTerminalId(Long.valueOf(busiLiveBroadcast.getTerminalId()));
                                busiMcuZjTemplateParticipantMapper.updateBusiMcuZjTemplateParticipant(busiMcuZjTemplateParticipant);
                                break;
                            }
                            default -> {
                                break;
                            }
                        }
                    }

                    busiAllConferenceAppointmentService.onlyEditConferenceAppointment(conferenceAppointment, mcuTypeStr);
                }

            }
        }

        int i = busiLiveBroadcastMapper.updateBusiLiveBroadcast(busiLiveBroadcast);
        if (i > 0) {
            LiveBroadcastCache.getInstance().add(busiLiveBroadcast);
        }
        return i;
    }

    /**
     * 批量删除直播记录
     *
     * @param ids 需要删除的直播记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveBroadcastByIds(Long[] ids)
    {
        int i = 0;
        for (Long id : ids) {
            i += deleteBusiLiveBroadcastById(id);
        }
        return i;
    }

    /**
     * 删除直播记录信息
     *
     * @param id 直播记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiLiveBroadcastById(Long id)
    {
        int i = 0;
        BusiLiveBroadcast busiLiveBroadcast = busiLiveBroadcastMapper.selectBusiLiveBroadcastById(id);
        if (busiLiveBroadcast.getStatus() != 3 && busiLiveBroadcast.getIsStart() == null || busiLiveBroadcast.getIsStart() == 2) {
            Integer type = busiLiveBroadcast.getType();
            if (type == 1) {
                BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap = new BusiLiveBroadcastAppointmentMap();
                busiLiveBroadcastAppointmentMap.setLiveBroadcastId(id);
                List<BusiLiveBroadcastAppointmentMap> busiLiveBroadcastAppointmentMaps = busiLiveBroadcastAppointmentMapService.selectBusiLiveBroadcastAppointmentMapList(busiLiveBroadcastAppointmentMap);
                for (BusiLiveBroadcastAppointmentMap liveBroadcastAppointmentMap : busiLiveBroadcastAppointmentMaps) {
                    Long appointmentId = liveBroadcastAppointmentMap.getAppointmentId();
                    String mcuType = liveBroadcastAppointmentMap.getMcuType();
                    if (appointmentId != null && StringUtils.isNotEmpty(mcuType)) {
                        String contextKey = EncryptIdUtil.generateContextKey(appointmentId, mcuType);
                        String generateEncryptId = EncryptIdUtil.generateEncryptId(contextKey);
                        if (StringUtils.isNotEmpty(generateEncryptId)) {
                            busiAllConferenceAppointmentService.removeConferenceAppointment(generateEncryptId);
                        }
                    }
                }
            }
            i = busiLiveBroadcastMapper.deleteBusiLiveBroadcastById(id);
            if (i > 0) {
                LiveBroadcastCache.getInstance().remove(id);
            }
        } else {
            throw new SystemException("不能删除此直播！");
        }
        return i;
    }

    private String createStreamingUrl(Long deptId) {
        String streamUrl = null;
        try {
            BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(deptId);
            if (busiLiveDept != null) {
                if (busiLiveDept.getLiveType() == 1) {
                    BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(busiLiveDept.getLiveId());
                    if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
                        streamUrl = busiLive.getProtocolType() + "://" + busiLive.getDomainName() + "/" + busiLive.getUriPath() + "/";
                    } else {
                        streamUrl = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/";
                    }
                } else {
                    BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
                    busiLiveClusterMap.setClusterId(busiLiveDept.getLiveId());
                    List<BusiLiveClusterMap> busiLiveClusterMaps = BeanFactory.getBean(BusiLiveClusterMapMapper.class).selectBusiLiveClusterMapList(busiLiveClusterMap);
                    if (busiLiveClusterMaps != null && busiLiveClusterMaps.size() > 0) {
                        for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
                            if (liveClusterMap.getLiveType() == 1) {
                                BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(liveClusterMap.getLiveId());
                                if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
                                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getDomainName() + "/" + busiLive.getUriPath() + "/";
                                } else {
                                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/";
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return streamUrl;
    }

    public List<String> createStreamUrlList(long deptId, String str) {
        {
            List<String> stringList = new ArrayList<>();
            try {
                BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(deptId);
                if (busiLiveDept != null) {
                    if (busiLiveDept.getLiveType() == 100) {
                        BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
                        busiLiveClusterMap.setClusterId(busiLiveDept.getLiveId());
                        List<BusiLiveClusterMap> busiLiveClusterMaps = BeanFactory.getBean(BusiLiveClusterMapMapper.class).selectBusiLiveClusterMapList(busiLiveClusterMap);
                        if (busiLiveClusterMaps != null && busiLiveClusterMaps.size() > 0) {
                            for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
                                BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(liveClusterMap.getLiveId());
                                if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
                                    busiLive.setIp(busiLive.getDomainName());
                                }
                                String url = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + str;
                                stringList.add(url);
                            }
                        }
                    } else {
                        BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(busiLiveDept.getLiveId());
                        if (busiLive != null) {

                            if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
                                busiLive.setIp(busiLive.getDomainName());
                            }
                            String url = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + str;
                            stringList.add(url);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringList;
        }
    }

    private BusiConferenceAppointment createConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment, BusiLiveBroadcast busiLiveBroadcast) {
        Map<String, Object> params = busiConferenceAppointment.getParams();

        if (!params.containsKey("businessFieldType")) {
            params.put("businessFieldType", 100);
        }
        if (!params.containsKey("conferenceName")) {
            params.put("conferenceName", busiLiveBroadcast.getName());
        }
        if (!params.containsKey("deptId")) {
            params.put("deptId", busiLiveBroadcast.getDeptId());
        }
        if (!params.containsKey("duration")) {
            params.put("duration", 24);
        }
        if (!params.containsKey("endTime")) {
            params.put("endTime", busiConferenceAppointment.getEndTime());
        }
        if (!params.containsKey("isAutoCreateTemplate")) {
            params.put("isAutoCreateTemplate", 1);
        }
//        if (!params.containsKey("muteType")) {
//            params.put("muteType", );
//        }
        if (!params.containsKey("repeatValue")) {
            params.put("repeatValue", 1);
        }
        if (!params.containsKey("startTime")) {
            params.put("startTime", busiConferenceAppointment.getStartTime());
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
        if (!params.containsKey("conferenceName")) {
            params.put("conferenceName", busiLiveBroadcast.getName());
        }
        if (!params.containsKey("defaultViewLayout")) {
            params.put("defaultViewLayout", "speakerOnly");
        }
        if (!params.containsKey("defaultViewIsDisplaySelf")) {
            params.put("defaultViewIsDisplaySelf", 1);
        }
        if (!params.containsKey("recordingEnabled")) {
            params.put("recordingEnabled", 2);
        }
        if (!params.containsKey("streamingEnabled")) {
            params.put("streamingEnabled", 2);
        }
        if (!params.containsKey("defaultViewIsBroadcast")) {
            params.put("defaultViewIsBroadcast", 1);
        }
        if (!params.containsKey("defaultViewIsFill")) {
            params.put("defaultViewIsFill", 1);
        }
        if (!params.containsKey("recordingEnabled")) {
            params.put("recordingEnabled", busiLiveBroadcast.getPlaybackEnabled());
        }
        if (!params.containsKey("masterTerminalId")) {
            params.put("masterTerminalId", busiLiveBroadcast.getTerminalId());
        }
        if (!params.containsKey("streamUrl")) {
            params.put("streamUrl", busiLiveBroadcast.getStreamUrl());
        }
        if (!params.containsKey("streamingEnabled")) {
            params.put("streamingEnabled", 2);
        }
        if (!params.containsKey("templateParticipants") && busiLiveBroadcast.getTerminalId() != null) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            BusiTerminal busiTerminal = TerminalCache.getInstance().get(Long.valueOf(busiLiveBroadcast.getTerminalId()));
            if (busiTerminal != null) {
                Long deptId = busiTerminal.getDeptId();
                if (deptId != busiLiveBroadcast.getDeptId()) {
                    if (!params.containsKey("templateDepts")) {
                        List<Map<String, Object>> deptList = new ArrayList<>();
                        Map<String, Object> map = new HashMap<>();
                        map.put("deptId", deptId);
                        map.put("weight", 1);
                        deptList.add(map);
                        params.put("templateDepts", deptList);
                    }
                }
                Map<String, Object> map = new HashMap<>();
                map.put("id", busiTerminal.getId());
                map.put("terminalId", busiTerminal.getId());
                map.put("attendType", 1);
                map.put("weight", 1);
                map.put("businessProperties", "");
                map.put("deptId", deptId);
                mapList.add(map);
            }
            params.put("templateParticipants", mapList);
        }
        return busiConferenceAppointment;
    }

    /**
     * 获取部门直播计数
     *
     * @return
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts() {
        return busiLiveBroadcastMapper.getDeptRecordCounts();
    }

    /**
     * 结束直播
     * @param id
     * @return
     */
    @Override
    public Boolean endLive(Long id, int endType) {
        BusiLiveBroadcast busiLiveBroadcast = busiLiveBroadcastMapper.selectBusiLiveBroadcastById(id);
        boolean isEnd = false;
        if (busiLiveBroadcast != null) {
            isEnd = true;
            Integer type = busiLiveBroadcast.getType();
            if (type == 1) {
                BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap = LiveBroadcastCache.getInstance().getMapById(id);
                String mcuType = busiLiveBroadcastAppointmentMap.getMcuType();
                Long appointmentId = busiLiveBroadcastAppointmentMap.getAppointmentId();
                String generateKey = EncryptIdUtil.generateKey(appointmentId, mcuType);
                BusiConferenceAppointment busiConferenceAppointment = AppointmentCache.getInstance().get(generateKey);
                if (busiConferenceAppointment != null) {
                    Long templateId = busiConferenceAppointment.getTemplateId();
                    String contextKey = EncryptIdUtil.generateContextKey(templateId, mcuType);
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        String encryptId = EncryptIdUtil.generateEncryptId(contextKey);
                        busiAllConferenceAppointmentService.endConference(encryptId, EndReasonsType.ADMINISTRATOR_HANGS_UP);
                    }
                }
            }
        }

        if (isEnd) {
            busiLiveBroadcast.setStatus(3);
            Date startTime = DateUtils.convertToDate(busiLiveBroadcast.getStartTime());
            Date date = new Date();
            Long timeDiff = date.getTime() - startTime.getTime();
            Integer min = Math.toIntExact(timeDiff / (60 * 1000));
            busiLiveBroadcast.setDuration(min);
            busiLiveBroadcast.setEndReasonsType(endType);
            int i = busiLiveBroadcastMapper.updateBusiLiveBroadcast(busiLiveBroadcast);
            if (i > 0) {
                LiveBroadcastCache.getInstance().add(busiLiveBroadcast);
            }

            LiveRecordsTask liveRecordsTask = new LiveRecordsTask(id + "", 120 * 1000, id);
            taskService.addTask(liveRecordsTask);
        }
        return isEnd;
    }

}
