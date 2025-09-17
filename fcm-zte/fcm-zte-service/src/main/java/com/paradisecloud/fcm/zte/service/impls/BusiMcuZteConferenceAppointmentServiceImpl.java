package com.paradisecloud.fcm.zte.service.impls;

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
import com.paradisecloud.fcm.service.interfaces.IBusiAllMcuTemplateService;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.AppointDatetimeRange;
import com.paradisecloud.fcm.zte.model.busi.attendee.TerminalAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.zte.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.zte.model.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceAppointmentService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteTemplateConferenceService;
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
public class BusiMcuZteConferenceAppointmentServiceImpl implements IBusiMcuZteConferenceAppointmentService
{
    @Resource
    private BusiMcuZteConferenceAppointmentMapper busiMcuZteConferenceAppointmentMapper;

    @Resource
    private BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper;

    @Resource
    private IBusiMcuZteTemplateConferenceService busiMcuZteTemplateConferenceService;

    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;

    @Resource
    private BusiMcuZteTemplateParticipantMapper busiMcuZteTemplateParticipantMapper;

    @Resource
    private BusiMcuZteTemplateDeptMapper busiMcuZteTemplateDeptMapper;

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
        return busiMcuZteConferenceAppointmentMapper.getDeptRecordCounts(businessFieldType);
    }

    /**
     * 查询会议预约记录
     *
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    @Override
    public BusiMcuZteConferenceAppointment selectBusiMcuZteConferenceAppointmentById(Long id)
    {
        return busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentById(id);
    }

    /**
     * 查询会议预约记录列表
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录
     */
    @Override
    public List<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentList(BusiMcuZteConferenceAppointment busiConferenceAppointment)
    {
        BusinessFieldType.convert((Integer) busiConferenceAppointment.getParams().get("businessFieldType"));
        return selectBusiMcuZteConferenceAppointmentListWithOutBusinessFieldType(busiConferenceAppointment);
    }

    public List<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentListWithOutBusinessFieldType(BusiMcuZteConferenceAppointment busiConferenceAppointment)
    {
        List<BusiMcuZteConferenceAppointment> cas = busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentList(busiConferenceAppointment);
        return cas;
    }

    /**
     * 新增会议预约记录
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    @Override
    public Map<String, Object> insertBusiMcuZteConferenceAppointment(BusiMcuZteConferenceAppointment busiConferenceAppointment)
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
            JSONArray busiMcuZteTemplateParticipantArr = (JSONArray) JSON.toJSON(busiConferenceAppointment.getParams().get("templateParticipants"));
            List<BusiMcuZteTemplateParticipant> busiMcuZteTemplateParticipants = new ArrayList<>();
            if (busiMcuZteTemplateParticipantArr != null)
            {
                for (int i = 0; i < busiMcuZteTemplateParticipantArr.size(); i++)
                {
                    BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant = busiMcuZteTemplateParticipantArr.getObject(i, BusiMcuZteTemplateParticipant.class);
                    Assert.notNull(busiMcuZteTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                    busiMcuZteTemplateParticipants.add(busiMcuZteTemplateParticipant);
                    if (busiMcuZteTemplateParticipantArr.getJSONObject(i).containsKey("sn")) {
                        String sn = busiMcuZteTemplateParticipantArr.getJSONObject(i).getString("sn");
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
            List<BusiMcuZteTemplateDept> templateDepts = new ArrayList<>();
            if (templateDeptArr != null)
            {
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuZteTemplateDept.class));
                }
            }

            Object cnObj = busiConferenceAppointment.getParams().get("conferenceNumber");
            if (cnObj != null)
            {
                conferenceNumber = TypeUtils.castToLong(cnObj);
            }
            if (conferenceNumber != null)
            {
                BusiMcuZteTemplateConference con = new BusiMcuZteTemplateConference();
                con.setConferenceNumber(conferenceNumber);
                con.setDeptId(busiConferenceAppointment.getDeptId());
                List<BusiMcuZteTemplateConference> tcs = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceList(con);
                Assert.isTrue(ObjectUtils.isEmpty(tcs), "该会议号已被其它模板绑定，请重新选择别的会议号！");
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(conferenceNumber);
                if (busiConferenceNumber == null)
                {
                    // 根据输入的号码初始化号码，再自动创建模板
                    busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), McuType.MCU_ZTE.getCode(), conferenceNumber);
                }

                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiMcuZteTemplateParticipants, templateDepts);
            }
            else
            {
                // 自动生成号码，自动创建模板
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), McuType.MCU_ZTE.getCode());
                conferenceNumber = busiConferenceNumber.getId();
                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiMcuZteTemplateParticipants, templateDepts);
            }
        }
        else
        {
            BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            if (tc.getConferenceNumber() == null && ConferenceNumberCreateType.convert(tc.getIsAutoCreateConferenceNumber()) == ConferenceNumberCreateType.AUTO)
            {
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), McuType.MCU_ZTE.getCode());

                // 获取模板会议实体对象
                tc.setConferenceNumber(busiConferenceNumber.getId());
                busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(tc);
            }
            conferenceNumber = tc.getConferenceNumber();
            McuZteConferenceContext conferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(tc.getId());

            Object modelBean = busiMcuZteTemplateConferenceService.selectBusiMcuZteTemplateConferenceById(tc.getId());

            Map<String, Object> params = (Map<String, Object>) modelBean;
            busiConferenceAppointment.setParams(params);
            Collection<TerminalAttendeeForMcuZte> values = conferenceContext.getTerminalAttendeeMap().values();
            if (values != null && values.size() >0) {
                for (TerminalAttendeeForMcuZte value : values) {
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
            BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiConferenceAppointment.getTemplateId());
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

        int i = busiMcuZteConferenceAppointmentMapper.insertBusiMcuZteConferenceAppointment(busiConferenceAppointment);
        if (i > 0) {
            if (isApprovalEnabled) {
                String jsonString = JSON.toJSONString(busiConferenceAppointment);
                JSONObject jsonObject = JSON.parseObject(jsonString);
                BusiConferenceApproval busiConferenceApproval = new BusiConferenceApproval();
                busiConferenceApproval.setAppointmentConferenceId(busiConferenceAppointment.getId());
                busiConferenceApproval.setMcuType(McuType.MCU_ZTE.getCode());
                busiConferenceApproval.setConferenceName(conferenceName);
                busiConferenceApproval.setDeptId(busiConferenceAppointment.getDeptId());
                busiConferenceApproval.setApprovalStatus(0);
                busiConferenceApproval.setCreateUserId(createUserId.toString());
                busiConferenceApproval.setCreateBy(createBy);
                busiConferenceApproval.setCreateTime(new Date());
                busiConferenceApproval.setConferenceDetail(jsonObject);
                busiConferenceApprovalMapper.insertBusiConferenceApproval(busiConferenceApproval);
            }
            mqttService.putAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_ZTE.getCode()), busiConferenceAppointment);
            if (snList != null && snList.size() > 0) {
                mqttService.conferenceList(snList);
            }
        }
        resultMap.put("rows", i);
        resultMap.put("templateId", busiConferenceAppointment.getTemplateId());
        resultMap.put("conferenceNumber", conferenceNumber);
        String tenantId = "";
        resultMap.put("tenantId", tenantId);
        resultMap.put("appointmentId", busiConferenceAppointment.getId());
//        if(busiConferenceAppointment.getType()==1){
//            new StartTemplateConference().createConferenceNumber(busiConferenceAppointment.getTemplateId(), busiConferenceAppointment.getStartTime());
//        }

        return resultMap;
    }
    /**
     * 创建会议模板
     * @author sinhy
     * @since 2021-08-12 14:57
     * @param busiConferenceAppointment
     * @param busiConferenceNumber
     * @param masterTerminalId
     * @param busiMcuZteTemplateParticipants
     * @param templateDepts void
     */
    @SuppressWarnings("unchecked")
    private void createTemplate(BusiMcuZteConferenceAppointment busiConferenceAppointment, BusiConferenceNumber busiConferenceNumber, Long masterTerminalId, List<BusiMcuZteTemplateParticipant> busiMcuZteTemplateParticipants, List<BusiMcuZteTemplateDept> templateDepts)
    {
        Assert.isTrue(busiConferenceAppointment.getParams().get("isAutoCall") != null, "自动呼入与会者不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("conferenceName") != null, "会议名不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("defaultViewLayout") != null, "会议默认布局不能为空");
//        Assert.isTrue(busiConferenceAppointment.getParams().get("defaultViewIsDisplaySelf") != null, "布局模式不能为空");
//        Assert.isTrue(busiConferenceAppointment.getParams().get("recordingEnabled") != null, "是否录制不能为空");
//        Assert.isTrue(busiConferenceAppointment.getParams().get("streamingEnabled") != null, "是否直播不能为空");
       // Assert.isTrue(busiConferenceAppointment.getParams().get("defaultViewIsBroadcast") != null, "是否默认广播不能为空");
    //    Assert.isTrue(busiConferenceAppointment.getParams().get("defaultViewIsFill") != null, "是否默认补位不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("businessFieldType") != null, "业务领域类型businessFieldType不能为空");

        BusiMcuZteTemplateConference busiMcuZteTemplateConference = new BusiMcuZteTemplateConference();
        busiMcuZteTemplateConference.setName((String) busiConferenceAppointment.getParams().get("conferenceName"));
        busiMcuZteTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
        busiMcuZteTemplateConference.setDeptId(busiConferenceAppointment.getDeptId());
        busiMcuZteTemplateConference.setRemarks((String) busiConferenceAppointment.getParams().get("remarks"));
        busiMcuZteTemplateConference.setBusinessFieldType((Integer)busiConferenceAppointment.getParams().get("businessFieldType"));
        busiMcuZteTemplateConference.setViewType((Integer)busiConferenceAppointment.getParams().get("viewType"));
        // 默认自动分屏
        busiMcuZteTemplateConference.setDefaultViewLayout(com.paradisecloud.fcm.zte.model.busi.layout.splitscreen.AutomaticSplitScreen.LAYOUT);
        busiMcuZteTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        busiMcuZteTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
        busiMcuZteTemplateConference.setPollingInterval(10);
        busiMcuZteTemplateConference.setDefaultViewIsDisplaySelf(1);
        Integer muteType = null;
        try {
            muteType = TypeUtils.castToInt(busiConferenceAppointment.getParams().get("muteType"));
        } catch (Exception e) {
        }
        if (muteType == null || muteType != 0) {
            muteType = 1;// 0 不静音 1 静音
        }
        busiMcuZteTemplateConference.setMuteType(muteType);
        Long presenter = null;
        try {
            presenter = (Long) busiConferenceAppointment.getParams().get("presenter");
        } catch (Exception e) {
        }
        busiMcuZteTemplateConference.setPresenter(presenter);
        if (busiConferenceAppointment.getParams().get("businessProperties") != null)
        {
            busiMcuZteTemplateConference.setBusinessProperties((Map<String, Object>) busiConferenceAppointment.getParams().get("businessProperties"));
        }

        // 模板会议是否允许被级联：1允许，2不允许
        busiMcuZteTemplateConference.setType(ConferenceType.SINGLE.getValue());

        // 模板创建类型
        busiMcuZteTemplateConference.setCreateType(ConferenceTemplateCreateType.AUTO.getValue());

        // 会议号是否自动创建
        busiMcuZteTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.convert(busiConferenceNumber.getCreateType()).getValue());

        // 直播地址
//        YesOrNo streamingEnabled = YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("streamingEnabled"));
//        if (streamingEnabled == YesOrNo.YES)
//        {
//            Assert.isTrue(!ObjectUtils.isEmpty(busiConferenceAppointment.getParams().get("streamUrl")), "开启直播后，直播地址不能为空！");
//        }
  //      busiMcuZteTemplateConference.setStreamingEnabled(streamingEnabled.getValue());
        busiMcuZteTemplateConference.setStreamingEnabled(0);
        busiMcuZteTemplateConference.setRecordingEnabled(0);
        busiMcuZteTemplateConference.setStreamUrl((String) busiConferenceAppointment.getParams().get("streamUrl"));

        // 是否录制
       // busiMcuZteTemplateConference.setRecordingEnabled(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("recordingEnabled")).getValue());

        // 自动呼入与会者
        busiMcuZteTemplateConference.setIsAutoCall(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("isAutoCall")).getValue());
        busiMcuZteTemplateConference.setIsAutoMonitor(ConferenceAutoMonitor.NO.getValue());

        // 设置会议密码
        busiMcuZteTemplateConference.setConferencePassword((String) busiConferenceAppointment.getParams().get("conferencePassword"));
        String defaultViewLayout = (String) busiConferenceAppointment.getParams().get("defaultViewLayout");
        if (StringUtils.isNotEmpty(defaultViewLayout)) {
            busiMcuZteTemplateConference.setDefaultViewLayout(defaultViewLayout);
        }
        busiMcuZteTemplateConference.setDefaultViewIsBroadcast(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsBroadcast")).getValue());
        busiMcuZteTemplateConference.setDefaultViewIsFill(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsFill")).getValue());

        // 布局模式
        busiMcuZteTemplateConference.setDefaultViewIsDisplaySelf(1);
        busiMcuZteTemplateConference.setPollingInterval(10);

        // 默认带宽2MB
        Integer bandwidth = (Integer)busiConferenceAppointment.getParams().get("bandwidth");
        busiMcuZteTemplateConference.setBandwidth(bandwidth == null ? 1 : bandwidth);
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
            busiMcuZteTemplateConference.setCreateUserName(createUserName);
            busiMcuZteTemplateConference.setCreateUserId(createUserId);
        }
        String hdResolution = null;
        try {
            hdResolution = TypeUtils.castToString(busiConferenceAppointment.getParams().get("hdResolution"));
        } catch (Exception e) {
        }
        if (hdResolution == null || !"1080".equals(hdResolution)) {
            hdResolution = "720";
        }
        busiMcuZteTemplateConference.setHdResolution(hdResolution);

        // 创建一个无主会场，终端的空会议
        int c = busiMcuZteTemplateConferenceService.insertBusiMcuZteTemplateConference(busiMcuZteTemplateConference, null, null, null);
        if (c <= 0)
        {
            busiConferenceNumberService.deleteBusiConferenceNumberById(busiConferenceNumber.getId());
        }
        else
        {
            if (!ObjectUtils.isEmpty(busiMcuZteTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant : busiMcuZteTemplateParticipants)
                {
                    busiMcuZteTemplateParticipant.setTemplateConferenceId(busiMcuZteTemplateConference.getId());
                    busiMcuZteTemplateParticipant.setCreateTime(new Date());
                    busiMcuZteTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuZteTemplateParticipantMapper.insertBusiMcuZteTemplateParticipant(busiMcuZteTemplateParticipant);
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuZteTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiMcuZteTemplateConference.setMasterParticipantId(busiMcuZteTemplateParticipant.getId());
                        busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuZteTemplateDept busiMcuZteTemplateDept : templateDepts)
                {
                    busiMcuZteTemplateDept.setTemplateConferenceId(busiMcuZteTemplateConference.getId());
                    busiMcuZteTemplateDept.setCreateTime(new Date());
                    busiMcuZteTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuZteTemplateDeptMapper.insertBusiMcuZteTemplateDept(busiMcuZteTemplateDept);
                }
            }
        }
        Assert.isTrue(c > 0, "创建预约会议的默认模板失败！");
        busiConferenceAppointment.setTemplateId(busiMcuZteTemplateConference.getId());
    }

    private void valid(BusiMcuZteConferenceAppointment busiConferenceAppointment)
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
            BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiConferenceAppointment.getTemplateId());
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

    private void validTimeSegment(BusiMcuZteConferenceAppointment busiConferenceAppointment, BusiMcuZteTemplateConference tc)
    {
        BusiMcuZteConferenceAppointment con = new BusiMcuZteConferenceAppointment();
        con.setTemplateId(tc.getId());
        List<BusiMcuZteConferenceAppointment> cas = busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas))
        {
            AppointDatetimeRange appointDatetimeRange = getDateStrRange(busiConferenceAppointment);
            for (BusiMcuZteConferenceAppointment busiConferenceAppointment2 : cas)
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

    private AppointDatetimeRange getDateStrRange(BusiMcuZteConferenceAppointment busiConferenceAppointment)
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
    public int updateBusiMcuZteConferenceAppointment(BusiMcuZteConferenceAppointment busiConferenceAppointment, boolean checkApproval)
    {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuZteConferenceAppointment old = busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentById(busiConferenceAppointment.getId());
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
                        BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        if (tc != null) {
                            conferenceName = tc.getName();
                            busiConferenceAppointment.getParams().put("conferenceName", conferenceName);
                        }
                    }
                }
            }
        }

        int i1 = busiMcuZteConferenceAppointmentMapper.updateBusiMcuZteConferenceAppointment(busiConferenceAppointment);

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_ZTE.getCode()));

        busiConferenceAppointment.setParams(busiConferenceAppointment1.getParams());
        if (i1 > 0) {
            if (isApprovalEnabled) {
                String jsonString = JSON.toJSONString(busiConferenceAppointment);
                JSONObject jsonObject = JSON.parseObject(jsonString);
                BusiConferenceApproval busiConferenceApprovalCon = new BusiConferenceApproval();
                busiConferenceApprovalCon.setAppointmentConferenceId(busiConferenceAppointment.getId());
                busiConferenceApprovalCon.setMcuType(McuType.MCU_ZTE.getCode());
                List<BusiConferenceApproval> busiConferenceApprovalList = busiConferenceApprovalMapper.selectBusiConferenceApprovalList(busiConferenceApprovalCon);
                if (busiConferenceApprovalList != null && busiConferenceApprovalList.size() > 0) {
                    BusiConferenceApproval busiConferenceApproval = busiConferenceApprovalList.get(0);
                    busiConferenceApproval.setApprovalStatus(0);
                    busiConferenceApproval.setCreateUserId(createUserId.toString());
                    busiConferenceApproval.setCreateBy(createBy);
                    busiConferenceApproval.setCreateTime(new Date());
                    busiConferenceApproval.setCreateTime(new Date());
                    busiConferenceApproval.setConferenceDetail(jsonObject);
                    busiConferenceApprovalMapper.updateBusiConferenceApproval(busiConferenceApproval);
                } else {
                    BusiConferenceApproval busiConferenceApproval = new BusiConferenceApproval();
                    busiConferenceApproval.setAppointmentConferenceId(busiConferenceAppointment.getId());
                    busiConferenceApproval.setMcuType(McuType.MCU_ZTE.getCode());
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
            JSONArray oldJson = (JSONArray) JSON.toJSON(bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_ZTE.getCode())).getParams().get("templateParticipants"));
            if (oldJson != null && oldJson.size() > 0) {
                for (int i = 0; i < oldJson.size(); i++) {
                    boolean isSn = oldJson.getJSONObject(i).containsKey("sn");
                    if (isSn) {
                        snList.add((String) oldJson.getJSONObject(i).get("sn"));
                    }
                }

                if (i1 > 0) {
                    bean.updateAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_ZTE.getCode()), busiConferenceAppointment);
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
    public int deleteBusiMcuZteConferenceAppointmentByIds(Long[] ids)
    {
        return busiMcuZteConferenceAppointmentMapper.deleteBusiMcuZteConferenceAppointmentByIds(ids);
    }

    /**
     * 删除会议预约记录信息
     *
     * @param id 会议预约记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuZteConferenceAppointmentById(Long id)
    {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuZteConferenceAppointment busiConferenceAppointment = busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentById(id);
        if (busiConferenceAppointment == null)
        {
            return 0;
        }
        if (busiConferenceAppointment.getIsStart() != null && YesOrNo.convert(busiConferenceAppointment.getIsStart()) == YesOrNo.YES)
        {
            throw new SystemException(1004344, "会议进行中，无法删除预约会议！");
        }
        int c = busiMcuZteConferenceAppointmentMapper.deleteBusiMcuZteConferenceAppointmentById(id);

        if (busiConferenceAppointment.getTemplateId() != null)
        {
            if (ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO)
            {
                busiMcuZteTemplateConferenceService.deleteBusiMcuZteTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                new StartTemplateConference().deleteConference(busiConferenceAppointment.getTemplateId());
            }
        }

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiMcuZteConferenceAppointment busiConferenceAppointment2 = new BusiMcuZteConferenceAppointment();
        busiConferenceAppointment2.setId(id);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(id, McuType.MCU_ZTE.getCode()));
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
                bean.removeAppointmentCache(EncryptIdUtil.generateKey(id, McuType.MCU_ZTE.getCode()), busiConferenceAppointment);
                if (snList != null && snList.size() > 0) {
                    bean.conferenceList(snList);
                }
            }
        }

        BusiConferenceApproval busiConferenceApprovalCon = new BusiConferenceApproval();
        busiConferenceApprovalCon.setAppointmentConferenceId(busiConferenceAppointment.getId());
        busiConferenceApprovalCon.setMcuType(McuType.MCU_ZTE.getCode());
        List<BusiConferenceApproval> busiConferenceApprovalList = busiConferenceApprovalMapper.selectBusiConferenceApprovalList(busiConferenceApprovalCon);
        if (busiConferenceApprovalList != null && busiConferenceApprovalList.size() > 0) {
            BusiConferenceApproval busiConferenceApproval = busiConferenceApprovalList.get(0);
            if (busiConferenceApproval.getApprovalStatus() == 0) {
                busiConferenceApproval.setApprovalStatus(3);
                busiConferenceApprovalMapper.updateBusiConferenceApproval(busiConferenceApproval);
            }
        }

        busiAllMcuTemplateService.deleteAllMcuTemplate(busiConferenceAppointment.getTemplateId(), McuType.MCU_ZTE.getCode());

        return c;
    }

    @Override
    public Page<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentListByKey(String searchKey, Long deptId, Integer pageIndex, Integer pageSize) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        PageHelper.startPage(pageIndex,pageSize);
        Page<BusiMcuZteConferenceAppointment> cas = busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentListByKey(searchKey, deptId);
        return cas;
    }

    @Override
    public Map<String, Object> insertBusiMcuZteConferenceAppointmentIsMute(BusiMcuZteConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId) {
        if (isMute) {
            busiConferenceAppointment.getParams().put("muteType", 1);
        } else {
            busiConferenceAppointment.getParams().put("muteType", 0);
        }
        busiConferenceAppointment.getParams().put("presenter", userId);
        Map<String, Object> resultMap = insertBusiMcuZteConferenceAppointment(busiConferenceAppointment);
        return resultMap;
    }

    @Override
    public List<BusiMcuZteConferenceAppointment> selectBusiMcuZteConferenceAppointmentByTemplateId(Long id) {
        return busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentByTemplateId(id);
    }
}
