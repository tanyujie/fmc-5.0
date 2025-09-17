package com.paradisecloud.fcm.mcu.kdc.service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.conference.model.AppointDatetimeRange;
import com.paradisecloud.fcm.mcu.kdc.conference.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.TerminalAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceAppointmentService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplateConferenceService;
import com.paradisecloud.fcm.service.interfaces.IBusiAllMcuTemplateService;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysConfigService;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 会议预约记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-24
 */
@Service
@Transactional
public class BusiMcuKdcConferenceAppointmentServiceImpl implements IBusiMcuKdcConferenceAppointmentService
{
    @Resource
    private BusiMcuKdcConferenceAppointmentMapper busiMcuKdcConferenceAppointmentMapper;

    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;

    @Resource
    private IBusiMcuKdcTemplateConferenceService busiMcuKdcTemplateConferenceService;

    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;

    @Resource
    private BusiMcuKdcTemplateParticipantMapper busiMcuKdcTemplateParticipantMapper;

    @Resource
    private BusiMcuKdcTemplateDeptMapper busiMcuKdcTemplateDeptMapper;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    @Resource
    private IBusiAllMcuTemplateService busiAllMcuTemplateService;

    @Resource
    private BusiConferenceApprovalMapper busiConferenceApprovalMapper;

    @Resource
    private ISysConfigService sysConfigService;

    @Resource
    private ISysUserService sysUserService;

    private Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 10:59
     * @param businessFieldType
     * @return
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType)
    {
        return busiMcuKdcConferenceAppointmentMapper.getDeptRecordCounts(businessFieldType);
    }

    /**
     * 查询会议预约记录
     *
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    @Override
    public BusiMcuKdcConferenceAppointment selectBusiMcuKdcConferenceAppointmentById(Long id)
    {
        return busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentById(id);
    }

    /**
     * 查询会议预约记录列表
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录
     */
    @Override
    public List<BusiMcuKdcConferenceAppointment> selectBusiMcuKdcConferenceAppointmentList(BusiMcuKdcConferenceAppointment busiConferenceAppointment)
    {
        BusinessFieldType.convert((Integer) busiConferenceAppointment.getParams().get("businessFieldType"));
        return selectBusiMcuKdcConferenceAppointmentListWithOutBusinessFieldType(busiConferenceAppointment);
    }

    public List<BusiMcuKdcConferenceAppointment> selectBusiMcuKdcConferenceAppointmentListWithOutBusinessFieldType(BusiMcuKdcConferenceAppointment busiConferenceAppointment)
    {
        List<BusiMcuKdcConferenceAppointment> cas = busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentList(busiConferenceAppointment);
        return cas;
    }

    /**
     * 新增会议预约记录
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    @Override
    public Map<String, Object> insertBusiMcuKdcConferenceAppointment(BusiMcuKdcConferenceAppointment busiConferenceAppointment)
    {
        Map<String, Object> resultMap = new HashMap<>();
        Long conferenceNumber = null;
        ArrayList<String> snList = new ArrayList<>();
        busiConferenceAppointment.setCreateTime(new Date());
        valid(busiConferenceAppointment);

        ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate());

        // 自动生成模板
        if (busiConferenceAppointment.getTemplateId() == null)
        {
            Long masterTerminalId = TypeUtils.castToLong(busiConferenceAppointment.getParams().get("masterTerminalId"));
            JSONArray busiMcuKdcTemplateParticipantArr = (JSONArray) JSON.toJSON(busiConferenceAppointment.getParams().get("templateParticipants"));
            List<BusiMcuKdcTemplateParticipant> busiMcuKdcTemplateParticipants = new ArrayList<>();
            if (busiMcuKdcTemplateParticipantArr != null)
            {
                for (int i = 0; i < busiMcuKdcTemplateParticipantArr.size(); i++)
                {
                    BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant = busiMcuKdcTemplateParticipantArr.getObject(i, BusiMcuKdcTemplateParticipant.class);
                    Assert.notNull(busiMcuKdcTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                    busiMcuKdcTemplateParticipants.add(busiMcuKdcTemplateParticipant);
                    if (busiMcuKdcTemplateParticipantArr.getJSONObject(i).containsKey("sn")) {
                        String sn = busiMcuKdcTemplateParticipantArr.getJSONObject(i).getString("sn");
                        if (StringUtils.isNotEmpty(sn)) {
                            snList.add(sn);
                        }
                    }
                }
            }

            Long createUserId = (Long) busiConferenceAppointment.getParams().get("createUserId");
            if (createUserId != null) {
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(createUserId);
                if (busiUserTerminal != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiUserTerminal.getTerminalId());
                    if (busiTerminal != null) {
                        if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
                            if (!snList.contains(busiTerminal.getSn())) {
                                snList.add(busiTerminal.getSn());
                            }
                        }
                    }
                }
            }

            // 部门顺序
            JSONArray templateDeptArr = (JSONArray) JSON.toJSON(busiConferenceAppointment.getParams().get("templateDepts"));
            List<BusiMcuKdcTemplateDept> templateDepts = new ArrayList<>();
            if (templateDeptArr != null)
            {
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuKdcTemplateDept.class));
                }
            }

            Object cnObj = busiConferenceAppointment.getParams().get("conferenceNumber");
            if (cnObj != null)
            {
                conferenceNumber = TypeUtils.castToLong(cnObj);
            }
            if (conferenceNumber != null)
            {
//                BusiMcuKdcTemplateConference con = new BusiMcuKdcTemplateConference();
//                con.setConferenceNumber(conferenceNumber);
//                con.setDeptId(busiConferenceAppointment.getDeptId());
//                List<BusiMcuKdcTemplateConference> tcs = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceList(con);
//                Assert.isTrue(ObjectUtils.isEmpty(tcs), "该会议号已被其它模板绑定，请重新选择别的会议号！");
//                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberForMcuKdcService.selectBusiConferenceNumberById(conferenceNumber);
//                if (busiConferenceNumber == null)
//                {
//                    // 根据输入的号码初始化号码，再自动创建模板
//                    busiConferenceNumber = busiConferenceNumberForMcuKdcService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), conferenceNumber);
//                }

                BusiConferenceNumber busiConferenceNumber = null;

                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiMcuKdcTemplateParticipants, templateDepts);
            }
            else
            {
//                // 自动生成号码，自动创建模板
//                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberForMcuKdcService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId());
//                conferenceNumber = busiConferenceNumber.getId();

                BusiConferenceNumber busiConferenceNumber = null;

                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiMcuKdcTemplateParticipants, templateDepts);
            }
        }
        else
        {
            BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiConferenceAppointment.getTemplateId());
//            if (tc.getConferenceNumber() == null && ConferenceNumberCreateType.convert(tc.getIsAutoCreateConferenceNumber()) == ConferenceNumberCreateType.AUTO)
//            {
//                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberForMcuKdcService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId());
//
//                // 获取模板会议实体对象
//                tc.setConferenceNumber(busiConferenceNumber.getId());
//                busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(tc);
//            }
//            conferenceNumber = tc.getConferenceNumber();
            McuKdcConferenceContext conferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(tc.getId());

            Object modelBean = busiMcuKdcTemplateConferenceService.selectBusiMcuKdcTemplateConferenceById(tc.getId());

            Map<String, Object> params = (Map<String, Object>) modelBean;
            busiConferenceAppointment.setParams(params);
            Collection<TerminalAttendeeForMcuKdc> values = conferenceContext.getTerminalAttendeeMap().values();
            if (values != null && values.size() >0) {
                for (TerminalAttendeeForMcuKdc value : values) {
                    if (StringUtils.isNotEmpty(value.getSn())) {
                        snList.add(value.getSn());
                    }
                }
            }

            Long createUserId = conferenceContext.getCreateUserId();
            if (createUserId != null) {
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(createUserId);
                if (busiUserTerminal != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiUserTerminal.getTerminalId());
                    if (busiTerminal != null) {
                        if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
                            if (!snList.contains(busiTerminal.getSn())) {
                                snList.add(busiTerminal.getSn());
                            }
                        }
                    }
                }
            }
        }

        //发送会议列表到终端
        IMqttService mqttService = BeanFactory.getBean(IMqttService.class);

        String conferenceName = "";
        Long createUserId = null;
        try {
            conferenceName = (String) busiConferenceAppointment.getParams().get("conferenceName");
        } catch (Exception e) {
        }
        try {
            createUserId = (Long) busiConferenceAppointment.getParams().get("createUserId");
        } catch (Exception e) {
        }
        if (busiConferenceAppointment.getTemplateId() != null) {
            BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            if (tc != null) {
                conferenceName = tc.getName();
                busiConferenceAppointment.getParams().put("conferenceName", conferenceName);
            }
        }
        String createBy = "";
        if (createUserId != null) {
            SysUser sysUser = sysUserService.selectUserById(createUserId);
            if (sysUser != null) {
                createBy = sysUser.getNickName();
            }
        } else {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                createUserId = loginUser.getUser().getUserId();
                createBy = loginUser.getUser().getNickName();
            }
        }
        boolean isApprovalEnabled = false;
        String conferenceApprovalEnable = sysConfigService.selectConfigByKey(ConfigConstant.CONFIG_KEY_CONFERENCE_APPROVAL_ENABLE);
        if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
            isApprovalEnabled = true;
            busiConferenceAppointment.setStatus(AppointmentConferenceStatus.DISABLED.getValue());
        }

        int i = busiMcuKdcConferenceAppointmentMapper.insertBusiMcuKdcConferenceAppointment(busiConferenceAppointment);
        if (i > 0) {
            if (isApprovalEnabled) {
                String jsonString = JSON.toJSONString(busiConferenceAppointment);
                JSONObject jsonObject = JSON.parseObject(jsonString);
                BusiConferenceApproval busiConferenceApproval = new BusiConferenceApproval();
                busiConferenceApproval.setAppointmentConferenceId(busiConferenceAppointment.getId());
                busiConferenceApproval.setMcuType(McuType.MCU_KDC.getCode());
                busiConferenceApproval.setConferenceName(conferenceName);
                busiConferenceApproval.setDeptId(busiConferenceAppointment.getDeptId());
                busiConferenceApproval.setApprovalStatus(0);
                busiConferenceApproval.setCreateUserId(createUserId.toString());
                busiConferenceApproval.setCreateBy(createBy);
                busiConferenceApproval.setCreateTime(new Date());
                busiConferenceApproval.setConferenceDetail(jsonObject);
                busiConferenceApprovalMapper.insertBusiConferenceApproval(busiConferenceApproval);
            }
            mqttService.putAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_KDC.getCode()), busiConferenceAppointment);
            if (snList != null && snList.size() > 0) {
                mqttService.conferenceList(snList);
            }
        }
        resultMap.put("rows", i);
        resultMap.put("templateId", busiConferenceAppointment.getTemplateId());
//        resultMap.put("conferenceNumber", conferenceNumber);
        String tenantId = "";
        resultMap.put("tenantId", tenantId);
        resultMap.put("appointmentId", busiConferenceAppointment.getId());
        return resultMap;
    }
    /**
     * 创建会议模板
     * @author sinhy
     * @since 2021-08-12 14:57
     * @param busiConferenceAppointment
     * @param masterTerminalId
     * @param busiMcuKdcTemplateParticipants
     * @param templateDepts void
     */
    @SuppressWarnings("unchecked")
    private void createTemplate(BusiMcuKdcConferenceAppointment busiConferenceAppointment, BusiConferenceNumber busiConferenceNumber2, Long masterTerminalId, List<BusiMcuKdcTemplateParticipant> busiMcuKdcTemplateParticipants, List<BusiMcuKdcTemplateDept> templateDepts)
    {
        Assert.isTrue(busiConferenceAppointment.getParams().get("isAutoCall") != null, "自动呼入与会者不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("conferenceName") != null, "会议名不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("defaultViewLayout") != null, "会议默认布局不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("defaultViewIsDisplaySelf") != null, "布局模式不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("recordingEnabled") != null, "是否录制不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("streamingEnabled") != null, "是否直播不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("defaultViewIsBroadcast") != null, "是否默认广播不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("defaultViewIsFill") != null, "是否默认补位不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("businessFieldType") != null, "业务领域类型businessFieldType不能为空");

        BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = new BusiMcuKdcTemplateConference();
        busiMcuKdcTemplateConference.setName((String) busiConferenceAppointment.getParams().get("conferenceName"));
//        busiMcuKdcTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
        busiMcuKdcTemplateConference.setDeptId(busiConferenceAppointment.getDeptId());
        busiMcuKdcTemplateConference.setRemarks((String) busiConferenceAppointment.getParams().get("remarks"));
        busiMcuKdcTemplateConference.setBusinessFieldType((Integer)busiConferenceAppointment.getParams().get("businessFieldType"));
        busiMcuKdcTemplateConference.setViewType((Integer)busiConferenceAppointment.getParams().get("viewType"));
        // 默认自动分屏
        busiMcuKdcTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
        busiMcuKdcTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        busiMcuKdcTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
        busiMcuKdcTemplateConference.setPollingInterval(10);
        busiMcuKdcTemplateConference.setDefaultViewIsDisplaySelf(-1);
        Integer muteType = null;
        try {
            muteType = TypeUtils.castToInt(busiConferenceAppointment.getParams().get("muteType"));
        } catch (Exception e) {
        }
        if (muteType == null || muteType != 0) {
            muteType = 1;// 0 不静音 1 静音
        }
        busiMcuKdcTemplateConference.setMuteType(muteType);
        Long presenter = null;
        try {
            presenter = (Long) busiConferenceAppointment.getParams().get("presenter");
        } catch (Exception e) {
        }
        busiMcuKdcTemplateConference.setPresenter(presenter);
        if (busiConferenceAppointment.getParams().get("businessProperties") != null)
        {
            busiMcuKdcTemplateConference.setBusinessProperties((Map<String, Object>) busiConferenceAppointment.getParams().get("businessProperties"));
        }

        // 模板会议是否允许被级联：1允许，2不允许
        busiMcuKdcTemplateConference.setType(ConferenceType.SINGLE.getValue());

        // 模板创建类型
        busiMcuKdcTemplateConference.setCreateType(ConferenceTemplateCreateType.AUTO.getValue());

        // 会议号是否自动创建
//        busiMcuKdcTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.convert(busiConferenceNumber.getCreateType()).getValue());
        busiMcuKdcTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.MANUAL.getValue());

        // 直播地址
        YesOrNo streamingEnabled = YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("streamingEnabled"));
        if (streamingEnabled == YesOrNo.YES)
        {
            Assert.isTrue(!ObjectUtils.isEmpty(busiConferenceAppointment.getParams().get("streamUrl")), "开启直播后，直播地址不能为空！");
        }
        busiMcuKdcTemplateConference.setStreamingEnabled(streamingEnabled.getValue());
        busiMcuKdcTemplateConference.setStreamUrl((String) busiConferenceAppointment.getParams().get("streamUrl"));

        // 是否录制
        busiMcuKdcTemplateConference.setRecordingEnabled(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("recordingEnabled")).getValue());

        // 自动呼入与会者
        busiMcuKdcTemplateConference.setIsAutoCall(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("isAutoCall")).getValue());
        busiMcuKdcTemplateConference.setIsAutoMonitor(ConferenceAutoMonitor.NO.getValue());

        // 设置会议密码
        busiMcuKdcTemplateConference.setConferencePassword((String) busiConferenceAppointment.getParams().get("conferencePassword"));
        String defaultViewLayout = (String) busiConferenceAppointment.getParams().get("defaultViewLayout");
        if (StringUtils.isNotEmpty(defaultViewLayout)) {
            busiMcuKdcTemplateConference.setDefaultViewLayout(defaultViewLayout);
        }
        busiMcuKdcTemplateConference.setDefaultViewIsBroadcast(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsBroadcast")).getValue());
        busiMcuKdcTemplateConference.setDefaultViewIsFill(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsFill")).getValue());

        // 布局模式
        busiMcuKdcTemplateConference.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsDisplaySelf")).getValue());
        busiMcuKdcTemplateConference.setPollingInterval(10);

        // 默认带宽2MB
        Integer bandwidth = (Integer)busiConferenceAppointment.getParams().get("bandwidth");
        busiMcuKdcTemplateConference.setBandwidth(bandwidth == null ? 1 : bandwidth);
        Long createUserId = null;
        String createUserName = null;
        try {
            createUserId = (Long) busiConferenceAppointment.getParams().get("createUserId");
        } catch (Exception e) {

        }
        try {
            createUserName = (String) busiConferenceAppointment.getParams().get("createUserName");
        } catch (Exception e) {

        }
        if (createUserId != null) {
            busiMcuKdcTemplateConference.setCreateUserName(createUserName);
            busiMcuKdcTemplateConference.setCreateUserId(createUserId);
        }
        String hdResolution = null;
        try {
            hdResolution = TypeUtils.castToString(busiConferenceAppointment.getParams().get("hdResolution"));
        } catch (Exception e) {
        }
        if (hdResolution == null || !"1080".equals(hdResolution)) {
            hdResolution = "720";
        }
        busiMcuKdcTemplateConference.setHdResolution(hdResolution);

        // 创建一个无主会场，终端的空会议
        int c = busiMcuKdcTemplateConferenceService.insertBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference, null, null, null);
        if (c <= 0)
        {
//            busiConferenceNumberForMcuKdcService.deleteBusiConferenceNumberById(busiConferenceNumber.getId());
        }
        else
        {
            if (!ObjectUtils.isEmpty(busiMcuKdcTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant : busiMcuKdcTemplateParticipants)
                {
                    busiMcuKdcTemplateParticipant.setTemplateConferenceId(busiMcuKdcTemplateConference.getId());
                    busiMcuKdcTemplateParticipant.setCreateTime(new Date());
                    busiMcuKdcTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuKdcTemplateParticipantMapper.insertBusiMcuKdcTemplateParticipant(busiMcuKdcTemplateParticipant);
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuKdcTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiMcuKdcTemplateConference.setMasterParticipantId(busiMcuKdcTemplateParticipant.getId());
                        busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuKdcTemplateDept busiMcuKdcTemplateDept : templateDepts)
                {
                    busiMcuKdcTemplateDept.setTemplateConferenceId(busiMcuKdcTemplateConference.getId());
                    busiMcuKdcTemplateDept.setCreateTime(new Date());
                    busiMcuKdcTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuKdcTemplateDeptMapper.insertBusiMcuKdcTemplateDept(busiMcuKdcTemplateDept);
                }
            }
        }
        Assert.isTrue(c > 0, "创建预约会议的默认模板失败！");
        busiConferenceAppointment.setTemplateId(busiMcuKdcTemplateConference.getId());
    }

    private void valid(BusiMcuKdcConferenceAppointment busiConferenceAppointment)
    {
        Assert.notNull(busiConferenceAppointment.getIsAutoCreateTemplate(), "是否自动创建模板不能为空！");
//        Assert.notNull(busiConferenceAppointment.getTemplateId(), "预约会议模板ID不能为空！");
        Assert.notNull(busiConferenceAppointment.getDeptId(), "预约会议部门ID不能为空！");
        Assert.notNull(busiConferenceAppointment.getStartTime(), "开始或结束时间不能为空！");
        Assert.notNull(busiConferenceAppointment.getEndTime(), "开始或结束时间不能为空！");
        Assert.notNull(busiConferenceAppointment.getRepeatRate(), "重复频率不能为空！");
        AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
        if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.MONTHLY || appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.WEEKLY)
        {
            Assert.notNull(busiConferenceAppointment.getRepeatDate(), "重复日期不能为空！");
        }

        Date start = null;
        Date end = null;
        if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM)
        {
            Assert.isTrue(DateTimeFormatPattern.PATTERN_11.getRegex().matcher(busiConferenceAppointment.getStartTime()).matches()
                    && DateTimeFormatPattern.PATTERN_11.getRegex().matcher(busiConferenceAppointment.getEndTime()).matches(), "开始或结束时间格式错误，须为：yyyy-MM-dd HH:mm:ss");
            start = DateUtils.convertToDate(busiConferenceAppointment.getStartTime());
            end = DateUtils.convertToDate(busiConferenceAppointment.getEndTime());
        }
        else
        {
            if (timePattern.matcher(busiConferenceAppointment.getStartTime()).matches()) {
                Assert.isTrue(timePattern.matcher(busiConferenceAppointment.getStartTime()).matches() && timePattern.matcher(busiConferenceAppointment.getEndTime()).matches(), "开始或结束时间格式错误，须为：HH:mm:ss");
                String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                start = DateUtils.convertToDate(today + " " + busiConferenceAppointment.getStartTime());
                end = DateUtils.convertToDate(today + " " + busiConferenceAppointment.getEndTime());
            } else {
                Assert.isTrue(DateTimeFormatPattern.PATTERN_11.getRegex().matcher(busiConferenceAppointment.getStartTime()).matches()
                        && DateTimeFormatPattern.PATTERN_11.getRegex().matcher(busiConferenceAppointment.getEndTime()).matches(), "开始或结束时间格式错误，须为：yyyy-MM-dd HH:mm:ss");
                start = DateUtils.convertToDate(busiConferenceAppointment.getStartTime());
                end = DateUtils.convertToDate(busiConferenceAppointment.getEndTime());
            }
        }

        Assert.isTrue(((end.getTime() - start.getTime()) / 1000 / 60) >= 5, "预约会议时间段须大于5分钟！");

        if (busiConferenceAppointment.getTemplateId() != null)
        {
            BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            Assert.notNull(tc, "预约会议模板ID无效！");
            Assert.isTrue(tc.getDeptId().equals(busiConferenceAppointment.getDeptId()), "预约会议所选模板不属于所选部门");

            // 校验时间段是否有交集
            validTimeSegment(busiConferenceAppointment, tc);
        }
        else
        {
            Assert.isTrue(busiConferenceAppointment.getParams() != null && busiConferenceAppointment.getParams().containsKey("conferenceName"), "自动创建模板时，会议名不能为空！");
        }
    }

    private void validTimeSegment(BusiMcuKdcConferenceAppointment busiConferenceAppointment, BusiMcuKdcTemplateConference tc)
    {
        BusiMcuKdcConferenceAppointment con = new BusiMcuKdcConferenceAppointment();
        con.setTemplateId(tc.getId());
        List<BusiMcuKdcConferenceAppointment> cas = busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas))
        {
            AppointDatetimeRange appointDatetimeRange = getDateStrRange(busiConferenceAppointment);
            for (BusiMcuKdcConferenceAppointment busiConferenceAppointment2 : cas)
            {
                if (busiConferenceAppointment.getId() != null && busiConferenceAppointment.getId().longValue() == busiConferenceAppointment2.getId().longValue())
                {
                    continue;
                }

                AppointDatetimeRange appointDatetimeRange2 = getDateStrRange(busiConferenceAppointment2);
                if (appointDatetimeRange.isIntersection(appointDatetimeRange2))
                {
                    throw new SystemException(1008435, "该预约会议所选时间段内已存在相同模板的预约会议：" + tc.getConferenceNumber() + " " + appointDatetimeRange2);
                }
            }
        }
    }

    private AppointDatetimeRange getDateStrRange(BusiMcuKdcConferenceAppointment busiConferenceAppointment)
    {
        AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
        AppointDatetimeRange dateRange = new AppointDatetimeRange();
        if (appointmentConferenceRepeatRate != AppointmentConferenceRepeatRate.CUSTOM)
        {
            if (timePattern.matcher(busiConferenceAppointment.getStartTime()).matches()) {
                String date = appointmentConferenceRepeatRate.getDate(busiConferenceAppointment.getRepeatDate());
                dateRange.setStartTime(date + " " + busiConferenceAppointment.getStartTime());
                dateRange.setEndTime(date + " " + busiConferenceAppointment.getEndTime());
            } else {
                dateRange.setStartTime(busiConferenceAppointment.getStartTime());
                dateRange.setEndTime(busiConferenceAppointment.getEndTime());
            }
        }
        else
        {
            dateRange.setStartTime(busiConferenceAppointment.getStartTime());
            dateRange.setEndTime(busiConferenceAppointment.getEndTime());
        }
        return dateRange;
    }

    /**
     * 修改会议预约记录
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    @Override
    public int updateBusiMcuKdcConferenceAppointment(BusiMcuKdcConferenceAppointment busiConferenceAppointment, boolean checkApproval)
    {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuKdcConferenceAppointment old = busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentById(busiConferenceAppointment.getId());
        Assert.isTrue(old.getTemplateId().longValue() == busiConferenceAppointment.getTemplateId().longValue(), "会议模板创建方式，以及会议号不能修改！");
        Assert.isTrue(old.getIsStart() == null || old.getIsStart() != YesOrNo.YES.getValue(), "会议正在进行中，不能修改！");
        busiConferenceAppointment.setUpdateTime(new Date());

        valid(busiConferenceAppointment);
        busiConferenceAppointment.setIsHangUp(null);

        String conferenceName = "";
        Long createUserId = null;
        try {
            conferenceName = (String) busiConferenceAppointment.getParams().get("conferenceName");
            createUserId = (Long) busiConferenceAppointment.getParams().get("createUserId");
        } catch (Exception e) {
        }
        String createBy = "";
        if (createUserId != null) {
            SysUser sysUser = sysUserService.selectUserById(createUserId);
            if (sysUser != null) {
                createBy = sysUser.getNickName();
            }
        } else {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                createUserId = loginUser.getUser().getUserId();
                createBy = loginUser.getUser().getNickName();
            }
        }
        boolean isApprovalEnabled = false;
        if (checkApproval) {
            String conferenceApprovalEnable = sysConfigService.selectConfigByKey(ConfigConstant.CONFIG_KEY_CONFERENCE_APPROVAL_ENABLE);
            if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                isApprovalEnabled = true;
                busiConferenceAppointment.setStatus(AppointmentConferenceStatus.DISABLED.getValue());
                if (StringUtils.isEmpty(conferenceName)) {
                    if (busiConferenceAppointment.getTemplateId() != null) {
                        BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        if (tc != null) {
                            conferenceName = tc.getName();
                            busiConferenceAppointment.getParams().put("conferenceName", conferenceName);
                        }
                    }
                }
            }
        }

        int i1 = busiMcuKdcConferenceAppointmentMapper.updateBusiMcuKdcConferenceAppointment(busiConferenceAppointment);

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_KDC.getCode()));

        busiConferenceAppointment.setParams(busiConferenceAppointment1.getParams());
        if (i1 > 0) {
            if (isApprovalEnabled) {
                String jsonString = JSON.toJSONString(busiConferenceAppointment);
                JSONObject jsonObject = JSON.parseObject(jsonString);
                BusiConferenceApproval busiConferenceApproval = null;
                if (busiConferenceAppointment.getApprovalId() != null) {
                    busiConferenceApproval = busiConferenceApprovalMapper.selectBusiConferenceApprovalById(busiConferenceAppointment.getApprovalId());
                }
                if (busiConferenceApproval != null) {
                    busiConferenceApproval.setApprovalStatus(0);
                    busiConferenceApproval.setCreateUserId(createUserId.toString());
                    busiConferenceApproval.setCreateBy(createBy);
                    busiConferenceApproval.setCreateTime(new Date());
                    busiConferenceApproval.setCreateTime(new Date());
                    busiConferenceApproval.setConferenceDetail(jsonObject);
                    busiConferenceApprovalMapper.updateBusiConferenceApproval(busiConferenceApproval);
                } else {
                    busiConferenceApproval = new BusiConferenceApproval();
                    busiConferenceApproval.setAppointmentConferenceId(busiConferenceAppointment.getId());
                    busiConferenceApproval.setMcuType(McuType.MCU_KDC.getCode());
                    busiConferenceApproval.setConferenceName(conferenceName);
                    busiConferenceApproval.setDeptId(busiConferenceAppointment.getDeptId());
                    busiConferenceApproval.setApprovalStatus(0);
                    busiConferenceApproval.setCreateUserId(createUserId.toString());
                    busiConferenceApproval.setCreateBy(createBy);
                    busiConferenceApproval.setCreateTime(new Date());
                    busiConferenceApproval.setConferenceDetail(jsonObject);
                    busiConferenceApprovalMapper.insertBusiConferenceApproval(busiConferenceApproval);
                }
            }
            JSONArray oldJson = (JSONArray) JSON.toJSON(bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_KDC.getCode())).getParams().get("templateParticipants"));
            if (oldJson != null && oldJson.size() > 0) {
                for (int i = 0; i < oldJson.size(); i++) {
                    boolean isSn = oldJson.getJSONObject(i).containsKey("sn");
                    if (isSn) {
                        snList.add((String) oldJson.getJSONObject(i).get("sn"));
                    }
                }

                if (i1 > 0) {
                    bean.updateAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_KDC.getCode()), busiConferenceAppointment);
                    if (snList != null && snList.size() > 0) {
                        bean.conferenceList(snList);
                    }
                }
            }
        }
        return i1;
    }

    /**
     * 批量删除会议预约记录
     *
     * @param ids 需要删除的会议预约记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuKdcConferenceAppointmentByIds(Long[] ids)
    {
        return busiMcuKdcConferenceAppointmentMapper.deleteBusiMcuKdcConferenceAppointmentByIds(ids);
    }

    /**
     * 删除会议预约记录信息
     *
     * @param id 会议预约记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuKdcConferenceAppointmentById(Long id)
    {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuKdcConferenceAppointment busiConferenceAppointment = busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentById(id);
        if (busiConferenceAppointment == null)
        {
            return 0;
        }
        if (busiConferenceAppointment.getIsStart() != null && YesOrNo.convert(busiConferenceAppointment.getIsStart()) == YesOrNo.YES)
        {
            throw new SystemException(1004344, "会议进行中，无法删除预约会议！");
        }
        int c = busiMcuKdcConferenceAppointmentMapper.deleteBusiMcuKdcConferenceAppointmentById(id);

        if (busiConferenceAppointment.getTemplateId() != null)
        {
            if (ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO)
            {
                busiMcuKdcTemplateConferenceService.deleteBusiMcuKdcTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            }
        }

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiConferenceAppointment busiConferenceAppointment2 = new BusiMcuKdcConferenceAppointment();
        busiConferenceAppointment2.setId(id);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(id, McuType.MCU_KDC.getCode()));
        if (busiConferenceAppointment1 != null && busiConferenceAppointment1.getParams() != null) {
            JSONArray oldJson = (JSONArray) JSON.toJSON(busiConferenceAppointment1.getParams().get("templateParticipants"));

            Long createUserId = (Long) busiConferenceAppointment1.getParams().get("createUserId");
            if (createUserId != null) {
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(createUserId);
                if (busiUserTerminal != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiUserTerminal.getTerminalId());
                    if (busiTerminal != null) {
                        if (!snList.contains(busiTerminal.getSn())) {
                            snList.add(busiTerminal.getSn());
                        }
                    }
                }
            }

            if (oldJson != null) {
                for (int i = 0; i < oldJson.size(); i++) {
                    boolean isSn = oldJson.getJSONObject(i).containsKey("sn");
                    if (isSn) {
                        snList.add((String) oldJson.getJSONObject(i).get("sn"));
                    }
                }
                bean.removeAppointmentCache(McuType.FME.getCode() + id, busiConferenceAppointment);
                if (snList != null && snList.size() > 0) {
                    bean.conferenceList(snList);
                }
            }
        }

        if (busiConferenceAppointment.getApprovalId() != null) {
            BusiConferenceApproval busiConferenceApproval = busiConferenceApprovalMapper.selectBusiConferenceApprovalById(busiConferenceAppointment.getApprovalId());
            if (busiConferenceApproval != null) {
                if (busiConferenceApproval.getApprovalStatus() == 0) {
                    busiConferenceApproval.setApprovalStatus(3);
                    busiConferenceApprovalMapper.updateBusiConferenceApproval(busiConferenceApproval);
                }
            }
        }

        busiAllMcuTemplateService.deleteAllMcuTemplate(busiConferenceAppointment.getTemplateId(), McuType.MCU_KDC.getCode());

        return c;
    }

    @Override
    public Page<BusiMcuKdcConferenceAppointment> selectBusiMcuKdcConferenceAppointmentListByKey(String searchKey, Long deptId, Integer pageIndex, Integer pageSize) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        PageHelper.startPage(pageIndex,pageSize);
        Page<BusiMcuKdcConferenceAppointment> cas = busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentListByKey(searchKey, deptId);
        return cas;
    }

    @Override
    public Map<String, Object> insertBusiMcuKdcConferenceAppointmentIsMute(BusiMcuKdcConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId) {
        if (isMute) {
            busiConferenceAppointment.getParams().put("muteType", 1);
        } else {
            busiConferenceAppointment.getParams().put("muteType", 0);
        }
        busiConferenceAppointment.getParams().put("presenter", userId);
        Map<String, Object> resultMap = insertBusiMcuKdcConferenceAppointment(busiConferenceAppointment);
        return resultMap;
    }

    @Override
    public List<BusiMcuKdcConferenceAppointment> selectBusiMcuKdcConferenceAppointmentByTemplateId(Long id) {
        return busiMcuKdcConferenceAppointmentMapper.selectBusiMcuKdcConferenceAppointmentByTemplateId(id);
    }
}
