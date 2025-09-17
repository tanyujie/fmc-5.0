package com.paradisecloud.fcm.smc2.setvice2.impls;

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
import com.paradisecloud.fcm.fme.conference.model.AppointDatetimeRange;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.service.interfaces.IBusiAllMcuTemplateService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.conference.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiConferenceNumberSmc2Service;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2ConferenceAppointmentService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2TemplateConferenceService;
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
public class BusiMcuSmc2ConferenceAppointmentServiceImpl implements IBusiMcuSmc2ConferenceAppointmentService {
    @Resource
    private BusiMcuSmc2ConferenceAppointmentMapper busiMcuSmc2ConferenceAppointmentMapper;

    @Resource
    private BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper;

    @Resource
    private IBusiMcuSmc2TemplateConferenceService busiMcuSmc2TemplateConferenceService;

    @Resource
    private IBusiConferenceNumberSmc2Service busiConferenceNumberForMcuSmc2Service;

    @Resource
    private BusiMcuSmc2TemplateParticipantMapper busiMcuSmc2TemplateParticipantMapper;

    @Resource
    private BusiMcuSmc2TemplateDeptMapper busiMcuSmc2TemplateDeptMapper;

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

    private final Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     *
     * @param businessFieldType
     * @return
     * @author sinhy
     * @since 2021-10-29 10:59
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType) {
        return busiMcuSmc2ConferenceAppointmentMapper.getDeptRecordCounts(businessFieldType);
    }

    /**
     * 查询会议预约记录
     *
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    @Override
    public BusiMcuSmc2ConferenceAppointment selectBusiMcuSmc2ConferenceAppointmentById(Long id) {
        return busiMcuSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentById(id);
    }

    /**
     * 查询会议预约记录列表
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录
     */
    @Override
    public List<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentList(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment) {
        BusinessFieldType.convert((Integer) busiConferenceAppointment.getParams().get("businessFieldType"));
        return selectBusiMcuSmc2ConferenceAppointmentListWithOutBusinessFieldType(busiConferenceAppointment);
    }

    @Override
    public List<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentListWithOutBusinessFieldType(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment) {
        List<BusiMcuSmc2ConferenceAppointment> cas = busiMcuSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentList(busiConferenceAppointment);
        return cas;
    }

    /**
     * 新增会议预约记录
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    @Override
    public Map<String, Object> insertBusiMcuSmc2ConferenceAppointment(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment) {
        Map<String, Object> resultMap = new HashMap<>();
        Long conferenceNumber = null;
        ArrayList<String> snList = new ArrayList<>();
        busiConferenceAppointment.setCreateTime(new Date());
        valid(busiConferenceAppointment);

        ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate());

        // 自动生成模板
        if (busiConferenceAppointment.getTemplateId() == null) {
            Long masterTerminalId = TypeUtils.castToLong(busiConferenceAppointment.getParams().get("masterTerminalId"));
            JSONArray busiMcuSmc2TemplateParticipantArr = (JSONArray) JSON.toJSON(busiConferenceAppointment.getParams().get("templateParticipants"));
            List<BusiMcuSmc2TemplateParticipant> busiMcuSmc2TemplateParticipants = new ArrayList<>();
            if (busiMcuSmc2TemplateParticipantArr != null) {
                for (int i = 0; i < busiMcuSmc2TemplateParticipantArr.size(); i++) {
                    BusiMcuSmc2TemplateParticipant busiMcuSmc2TemplateParticipant = busiMcuSmc2TemplateParticipantArr.getObject(i, BusiMcuSmc2TemplateParticipant.class);
                    Assert.notNull(busiMcuSmc2TemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                    busiMcuSmc2TemplateParticipants.add(busiMcuSmc2TemplateParticipant);
                    if (busiMcuSmc2TemplateParticipantArr.getJSONObject(i).containsKey("sn")) {
                        String sn = busiMcuSmc2TemplateParticipantArr.getJSONObject(i).getString("sn");
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
            List<BusiMcuSmc2TemplateDept> templateDepts = new ArrayList<>();
            if (templateDeptArr != null) {
                for (int i = 0; i < templateDeptArr.size(); i++) {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuSmc2TemplateDept.class));
                }
            }

            Object cnObj = busiConferenceAppointment.getParams().get("conferenceNumber");
            if (cnObj != null) {
                conferenceNumber = TypeUtils.castToLong(cnObj);
            }
            if (conferenceNumber != null) {
                BusiMcuSmc2TemplateConference con = new BusiMcuSmc2TemplateConference();
                con.setConferenceNumber(conferenceNumber);
                con.setDeptId(busiConferenceAppointment.getDeptId());
                List<BusiMcuSmc2TemplateConference> tcs = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceList(con);
                Assert.isTrue(ObjectUtils.isEmpty(tcs), "该会议号已被其它模板绑定，请重新选择别的会议号！");
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberForMcuSmc2Service.selectBusiConferenceNumberById(conferenceNumber);
                if (busiConferenceNumber == null) {
                    // 根据输入的号码初始化号码，再自动创建模板
                    busiConferenceNumber = busiConferenceNumberForMcuSmc2Service.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), conferenceNumber);
                }

                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiMcuSmc2TemplateParticipants, templateDepts);
            } else {
                // 自动生成号码，自动创建模板
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberForMcuSmc2Service.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId());
                conferenceNumber = busiConferenceNumber.getId();
                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiMcuSmc2TemplateParticipants, templateDepts);
            }
        } else {
            BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiConferenceAppointment.getTemplateId());
            if (tc.getConferenceNumber() == null && ConferenceNumberCreateType.convert(tc.getIsAutoCreateConferenceNumber()) == ConferenceNumberCreateType.AUTO) {
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberForMcuSmc2Service.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId());

                // 获取模板会议实体对象
                tc.setConferenceNumber(busiConferenceNumber.getId());
                busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(tc);
            }
            conferenceNumber = tc.getConferenceNumber();
            Smc2ConferenceContext conferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(tc.getId());

            Object modelBean = busiMcuSmc2TemplateConferenceService.selectBusiTemplateConferenceById(tc.getId());

            Map<String, Object> params = (Map<String, Object>) modelBean;
            busiConferenceAppointment.setParams(params);
            Collection<TerminalAttendeeSmc2> values = conferenceContext.getTerminalAttendeeMap().values();
            if (values != null && values.size() > 0) {
                for (TerminalAttendeeSmc2 value : values) {
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
            BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiConferenceAppointment.getTemplateId());
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

        int i = busiMcuSmc2ConferenceAppointmentMapper.insertBusiMcuSmc2ConferenceAppointment(busiConferenceAppointment);
        if (i > 0) {
            if (isApprovalEnabled) {
                String jsonString = JSON.toJSONString(busiConferenceAppointment);
                JSONObject jsonObject = JSON.parseObject(jsonString);
                BusiConferenceApproval busiConferenceApproval = new BusiConferenceApproval();
                busiConferenceApproval.setAppointmentConferenceId(busiConferenceAppointment.getId());
                busiConferenceApproval.setMcuType(McuType.SMC2.getCode());
                busiConferenceApproval.setConferenceName(conferenceName);
                busiConferenceApproval.setDeptId(busiConferenceAppointment.getDeptId());
                busiConferenceApproval.setApprovalStatus(0);
                busiConferenceApproval.setCreateUserId(createUserId.toString());
                busiConferenceApproval.setCreateBy(createBy);
                busiConferenceApproval.setCreateTime(new Date());
                busiConferenceApproval.setConferenceDetail(jsonObject);
                busiConferenceApprovalMapper.insertBusiConferenceApproval(busiConferenceApproval);
            }
            mqttService.putAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.SMC2.getCode()), busiConferenceAppointment);
            if (snList != null && snList.size() > 0) {
                mqttService.conferenceList(snList);
            }
        }
        resultMap.put("rows", i);
        resultMap.put("templateId", busiConferenceAppointment.getTemplateId());
        resultMap.put("conferenceNumber", conferenceNumber);
        resultMap.put("appointmentId", busiConferenceAppointment.getId());
        return resultMap;
    }

    /**
     * 创建会议模板
     *
     * @param busiConferenceAppointment
     * @param busiConferenceNumber
     * @param masterTerminalId
     * @param busiMcuSmc2TemplateParticipants
     * @param templateDepts                   void
     * @author sinhy
     * @since 2021-08-12 14:57
     */
    @SuppressWarnings("unchecked")
    private void createTemplate(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment, BusiConferenceNumber busiConferenceNumber, Long masterTerminalId, List<BusiMcuSmc2TemplateParticipant> busiMcuSmc2TemplateParticipants, List<BusiMcuSmc2TemplateDept> templateDepts) {
        Assert.isTrue(busiConferenceAppointment.getParams().get("isAutoCall") != null, "自动呼入与会者不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("conferenceName") != null, "会议名不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("businessFieldType") != null, "业务领域类型businessFieldType不能为空");

        BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference = new BusiMcuSmc2TemplateConference();
        busiMcuSmc2TemplateConference.setName((String) busiConferenceAppointment.getParams().get("conferenceName"));
        busiMcuSmc2TemplateConference.setConferenceNumber(busiConferenceNumber.getId());
        busiMcuSmc2TemplateConference.setDeptId(busiConferenceAppointment.getDeptId());
        busiMcuSmc2TemplateConference.setRemarks((String) busiConferenceAppointment.getParams().get("remarks"));
        busiMcuSmc2TemplateConference.setBusinessFieldType((Integer) busiConferenceAppointment.getParams().get("businessFieldType"));
        busiMcuSmc2TemplateConference.setViewType((Integer) busiConferenceAppointment.getParams().get("viewType"));
        // 默认自动分屏
        busiMcuSmc2TemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
        busiMcuSmc2TemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        busiMcuSmc2TemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
        busiMcuSmc2TemplateConference.setPollingInterval(10);
        busiMcuSmc2TemplateConference.setDefaultViewIsDisplaySelf(-1);

        Integer muteType = null;
        try {
            muteType = TypeUtils.castToInt(busiConferenceAppointment.getParams().get("muteType"));
        } catch (Exception e) {
        }

        busiMcuSmc2TemplateConference.setMuteType(muteType);
        Long presenter = null;
        try {
            presenter = (Long) busiConferenceAppointment.getParams().get("presenter");
            busiMcuSmc2TemplateConference.setPresenter(presenter.intValue());
        } catch (Exception e) {
        }

        if (busiConferenceAppointment.getParams().get("businessProperties") != null) {
            busiMcuSmc2TemplateConference.setBusinessProperties((Map<String, Object>) busiConferenceAppointment.getParams().get("businessProperties"));
        }
        if(busiConferenceAppointment.getParams().get("password")!=null){
            busiMcuSmc2TemplateConference.setGuestPassword((String) busiConferenceAppointment.getParams().get("password"));
            busiMcuSmc2TemplateConference.setConferencePassword((String) busiConferenceAppointment.getParams().get("password"));
        }

        // 模板会议是否允许被级联：1允许，2不允许
        busiMcuSmc2TemplateConference.setType(ConferenceType.SINGLE.getValue());

        // 模板创建类型
        busiMcuSmc2TemplateConference.setCreateType(ConferenceTemplateCreateType.AUTO.getValue());

        // 会议号是否自动创建
        busiMcuSmc2TemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.convert(busiConferenceNumber.getCreateType()).getValue());

        // 直播地址
        YesOrNo streamingEnabled = YesOrNo.convert((Integer) busiConferenceAppointment.getParams().get("streamingEnabled"));
        if (streamingEnabled == YesOrNo.YES) {
            Assert.isTrue(!ObjectUtils.isEmpty(busiConferenceAppointment.getParams().get("streamUrl")), "开启直播后，直播地址不能为空！");
        }
        busiMcuSmc2TemplateConference.setStreamingEnabled(streamingEnabled.getValue());
        busiMcuSmc2TemplateConference.setStreamUrl((String) busiConferenceAppointment.getParams().get("streamUrl"));

        // 是否录制
        busiMcuSmc2TemplateConference.setRecordingEnabled(YesOrNo.convert((Integer) busiConferenceAppointment.getParams().get("recordingEnabled")).getValue());

        // 自动呼入与会者
        busiMcuSmc2TemplateConference.setIsAutoCall(YesOrNo.convert((Integer) busiConferenceAppointment.getParams().get("isAutoCall")).getValue());
        busiMcuSmc2TemplateConference.setIsAutoMonitor(ConferenceAutoMonitor.NO.getValue());
        Object duration = busiConferenceAppointment.getParams().get("duration");
        if (duration != null) {
            busiMcuSmc2TemplateConference.setDurationTime((Integer) duration * 60);
        } else {
            busiMcuSmc2TemplateConference.setDurationTime(null);
        }

        if (busiConferenceAppointment.getType() == 3) {
            busiMcuSmc2TemplateConference.setDurationTime(null);
        }
        // 设置会议密码
        busiMcuSmc2TemplateConference.setConferencePassword((String) busiConferenceAppointment.getParams().get("password"));
        busiMcuSmc2TemplateConference.setChairmanPassword((String) busiConferenceAppointment.getParams().get("chairmanPassword"));
        busiMcuSmc2TemplateConference.setMaxParticipantNum((Integer) busiConferenceAppointment.getParams().get("maxParticipantNum"));
        String defaultViewLayout = (String) busiConferenceAppointment.getParams().get("defaultViewLayout");
        if (StringUtils.isNotEmpty(defaultViewLayout)) {
            busiMcuSmc2TemplateConference.setDefaultViewLayout(defaultViewLayout);
        }
        busiMcuSmc2TemplateConference.setDefaultViewIsBroadcast(YesOrNo.convert((Integer) busiConferenceAppointment.getParams().get("defaultViewIsBroadcast")).getValue());
        busiMcuSmc2TemplateConference.setDefaultViewIsFill(YesOrNo.convert((Integer) busiConferenceAppointment.getParams().get("defaultViewIsFill")).getValue());

        // 布局模式
        busiMcuSmc2TemplateConference.setPollingInterval(10);

        // 默认带宽2MB
        Integer bandwidth = (Integer) busiConferenceAppointment.getParams().get("bandwidth");
        busiMcuSmc2TemplateConference.setBandwidth(bandwidth == null ? 1 : bandwidth);
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
            busiMcuSmc2TemplateConference.setCreateUserName(createUserName);
            busiMcuSmc2TemplateConference.setCreateUserId(createUserId);
        }

        // 创建一个无主会场，终端的空会议
        int c = busiMcuSmc2TemplateConferenceService.insertBusiTemplateConference(busiMcuSmc2TemplateConference, null, null, null);
        if (c <= 0) {
            busiConferenceNumberForMcuSmc2Service.deleteBusiConferenceNumberById(busiConferenceNumber.getId());
        } else {
            if (!ObjectUtils.isEmpty(busiMcuSmc2TemplateParticipants)) {
                // 添加模板与会者顺序信息
                for (BusiMcuSmc2TemplateParticipant busiMcuSmc2TemplateParticipant : busiMcuSmc2TemplateParticipants) {
                    busiMcuSmc2TemplateParticipant.setTemplateConferenceId(busiMcuSmc2TemplateConference.getId());
                    busiMcuSmc2TemplateParticipant.setCreateTime(new Date());
                    busiMcuSmc2TemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuSmc2TemplateParticipantMapper.insertBusiMcuSmc2TemplateParticipant(busiMcuSmc2TemplateParticipant);
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuSmc2TemplateParticipant.getTerminalId().longValue()) {
                        // 设置模板会议中配置的主会场参会终端
                        busiMcuSmc2TemplateConference.setMasterParticipantId(busiMcuSmc2TemplateParticipant.getId());
                        busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiMcuSmc2TemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts)) {
                // 添加模板部门顺序信息
                for (BusiMcuSmc2TemplateDept busiMcuSmc2TemplateDept : templateDepts) {
                    busiMcuSmc2TemplateDept.setTemplateConferenceId(busiMcuSmc2TemplateConference.getId());
                    busiMcuSmc2TemplateDept.setCreateTime(new Date());
                    busiMcuSmc2TemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuSmc2TemplateDeptMapper.insertBusiMcuSmc2TemplateDept(busiMcuSmc2TemplateDept);
                }
            }
        }
        Assert.isTrue(c > 0, "创建预约会议的默认模板失败！");
        busiConferenceAppointment.setTemplateId(busiMcuSmc2TemplateConference.getId());
    }

    private void valid(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment) {
        Assert.notNull(busiConferenceAppointment.getIsAutoCreateTemplate(), "是否自动创建模板不能为空！");
//        Assert.notNull(busiConferenceAppointment.getTemplateId(), "预约会议模板ID不能为空！");
        Assert.notNull(busiConferenceAppointment.getDeptId(), "预约会议部门ID不能为空！");
        Assert.notNull(busiConferenceAppointment.getStartTime(), "开始或结束时间不能为空！");
        Assert.notNull(busiConferenceAppointment.getEndTime(), "开始或结束时间不能为空！");
        Assert.notNull(busiConferenceAppointment.getRepeatRate(), "重复频率不能为空！");
        AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
        if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.MONTHLY || appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.WEEKLY) {
            Assert.notNull(busiConferenceAppointment.getRepeatDate(), "重复日期不能为空！");
        }

        Date start = null;
        Date end = null;
        if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
            Assert.isTrue(DateTimeFormatPattern.PATTERN_11.getRegex().matcher(busiConferenceAppointment.getStartTime()).matches()
                    && DateTimeFormatPattern.PATTERN_11.getRegex().matcher(busiConferenceAppointment.getEndTime()).matches(), "开始或结束时间格式错误，须为：yyyy-MM-dd HH:mm:ss");
            start = DateUtils.convertToDate(busiConferenceAppointment.getStartTime());
            end = DateUtils.convertToDate(busiConferenceAppointment.getEndTime());
        } else {
            if (timePattern.matcher(busiConferenceAppointment.getStartTime()).matches()) {
                Assert.isTrue(timePattern.matcher(busiConferenceAppointment.getStartTime()).matches() && timePattern.matcher(busiConferenceAppointment.getEndTime()).matches(), "开始或结束时间格式错误，须为：HH:mm:ss");
                String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_12.getPattern());
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

        if (busiConferenceAppointment.getTemplateId() != null) {
            BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiConferenceAppointment.getTemplateId());
            Assert.notNull(tc, "预约会议模板ID无效！");
            Assert.isTrue(tc.getDeptId().equals(busiConferenceAppointment.getDeptId()), "预约会议所选模板不属于所选部门");

            // 校验时间段是否有交集
            validTimeSegment(busiConferenceAppointment, tc);
        } else {
            Assert.isTrue(busiConferenceAppointment.getParams() != null && busiConferenceAppointment.getParams().containsKey("conferenceName"), "自动创建模板时，会议名不能为空！");
        }
    }

    private void validTimeSegment(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment, BusiMcuSmc2TemplateConference tc) {
        BusiMcuSmc2ConferenceAppointment con = new BusiMcuSmc2ConferenceAppointment();
        con.setTemplateId(tc.getId());
        List<BusiMcuSmc2ConferenceAppointment> cas = busiMcuSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas)) {
            AppointDatetimeRange appointDatetimeRange = getDateStrRange(busiConferenceAppointment);
            for (BusiMcuSmc2ConferenceAppointment busiConferenceAppointment2 : cas) {
                if (busiConferenceAppointment.getId() != null && busiConferenceAppointment.getId().longValue() == busiConferenceAppointment2.getId().longValue()) {
                    continue;
                }

                AppointDatetimeRange appointDatetimeRange2 = getDateStrRange(busiConferenceAppointment2);
                if (appointDatetimeRange.isIntersection(appointDatetimeRange2)) {
                    throw new SystemException(1008425, "该预约会议所选时间段内已存在相同模板的预约会议：" + tc.getConferenceNumber() + " " + appointDatetimeRange2);
                }
            }
        }
    }

    private AppointDatetimeRange getDateStrRange(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment) {
        AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
        AppointDatetimeRange dateRange = new AppointDatetimeRange();
        if (appointmentConferenceRepeatRate != AppointmentConferenceRepeatRate.CUSTOM) {
            if (timePattern.matcher(busiConferenceAppointment.getStartTime()).matches()) {
                String date = appointmentConferenceRepeatRate.getDate(busiConferenceAppointment.getRepeatDate());
                dateRange.setStartTime(date + " " + busiConferenceAppointment.getStartTime());
                dateRange.setEndTime(date + " " + busiConferenceAppointment.getEndTime());
            } else {
                dateRange.setStartTime(busiConferenceAppointment.getStartTime());
                dateRange.setEndTime(busiConferenceAppointment.getEndTime());
            }
        } else {
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
    public int updateBusiMcuSmc2ConferenceAppointment(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment, boolean checkApproval) {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuSmc2ConferenceAppointment old = busiMcuSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentById(busiConferenceAppointment.getId());
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
                        BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        if (tc != null) {
                            conferenceName = tc.getName();
                            busiConferenceAppointment.getParams().put("conferenceName", conferenceName);
                        }
                    }
                }
            }
        }

        int i1 = busiMcuSmc2ConferenceAppointmentMapper.updateBusiMcuSmc2ConferenceAppointment(busiConferenceAppointment);

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.SMC2.getCode()));
        if (busiConferenceAppointment1 != null) {
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
                        busiConferenceApproval.setMcuType(McuType.SMC2.getCode());
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
                JSONArray oldJson = (JSONArray) JSON.toJSON(bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.SMC2.getCode())).getParams().get("templateParticipants"));
                if (oldJson != null && oldJson.size() > 0) {
                    for (int i = 0; i < oldJson.size(); i++) {
                        boolean isSn = oldJson.getJSONObject(i).containsKey("sn");
                        if (isSn) {
                            snList.add((String) oldJson.getJSONObject(i).get("sn"));
                        }
                    }

                    if (i1 > 0) {
                        bean.updateAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.SMC2.getCode()), busiConferenceAppointment);
                        if (snList != null && snList.size() > 0) {
                            bean.conferenceList(snList);
                        }
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
    public int deleteBusiMcuSmc2ConferenceAppointmentByIds(Long[] ids) {
        return busiMcuSmc2ConferenceAppointmentMapper.deleteBusiMcuSmc2ConferenceAppointmentByIds(ids);
    }

    /**
     * 删除会议预约记录信息
     *
     * @param id 会议预约记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuSmc2ConferenceAppointmentById(Long id) {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuSmc2ConferenceAppointment busiConferenceAppointment = busiMcuSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentById(id);
        if (busiConferenceAppointment == null) {
            return 0;
        }
        if (busiConferenceAppointment.getIsStart() != null && YesOrNo.convert(busiConferenceAppointment.getIsStart()) == YesOrNo.YES) {
            throw new SystemException(1004244, "会议进行中，无法删除预约会议！");
        }
        int c = busiMcuSmc2ConferenceAppointmentMapper.deleteBusiMcuSmc2ConferenceAppointmentById(id);

        if (busiConferenceAppointment.getTemplateId() != null) {
            if (ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO) {
                busiMcuSmc2TemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            }
        }

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiMcuSmc2ConferenceAppointment busiConferenceAppointment2 = new BusiMcuSmc2ConferenceAppointment();
        busiConferenceAppointment2.setId(id);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(id, McuType.SMC2.getCode()));
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
                bean.removeAppointmentCache(EncryptIdUtil.generateKey(id, McuType.SMC2.getCode()), busiConferenceAppointment);
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

        busiAllMcuTemplateService.deleteAllMcuTemplate(busiConferenceAppointment.getTemplateId(), McuType.SMC2.getCode());

        return c;
    }

    @Override
    public Page<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentListByKey(String searchKey, Long deptId, Integer pageIndex, Integer pageSize) {
        if (deptId == null) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId = loginUser.getUser().getDeptId();
        }
        PageHelper.startPage(pageIndex, pageSize);
        Page<BusiMcuSmc2ConferenceAppointment> cas = busiMcuSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentListByKey(searchKey, deptId);
        return cas;
    }

    @Override
    public Map<String, Object> insertBusiMcuSmc2ConferenceAppointmentIsMute(BusiMcuSmc2ConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId) {
        if (isMute) {
            busiConferenceAppointment.getParams().put("muteType", 1);
        } else {
            busiConferenceAppointment.getParams().put("muteType", 0);
        }
        busiConferenceAppointment.getParams().put("presenter", userId);
        Map<String, Object> resultMap = insertBusiMcuSmc2ConferenceAppointment(busiConferenceAppointment);
        return resultMap;
    }

    @Override
    public List<BusiMcuSmc2ConferenceAppointment> selectBusiMcuSmc2ConferenceAppointmentByTemplateId(Long id) {
        return busiMcuSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentByTemplateId(id);
    }
}
