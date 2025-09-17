package com.paradisecloud.fcm.tencent.service2.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.exception.CustomException;
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
import com.paradisecloud.fcm.tencent.busi.attende.TerminalAttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateDept;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentTemplateParticipant;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiConferenceNumberTencentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentConferenceAppointmentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentTemplateConferenceService;
import com.paradisecloud.fcm.tencent.templateConference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.tencent.templateConference.StartTemplateConference;
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
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 会议预约记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-24
 */
@Service
@Transactional
public class BusiMcuTencentConferenceAppointmentServiceImpl implements IBusiMcuTencentConferenceAppointmentService
{
    @Resource
    private BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper;

    @Resource
    private BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper;

    @Resource
    private IBusiMcuTencentTemplateConferenceService busiMcuTencentTemplateConferenceService;

    @Resource
    private IBusiConferenceNumberTencentService busiConferenceNumberForMcuTencentService;

    @Resource
    private BusiMcuTencentTemplateParticipantMapper busiMcuTencentTemplateParticipantMapper;

    @Resource
    private BusiMcuTencentTemplateDeptMapper busiMcuTencentTemplateDeptMapper;

    @Resource
    private BusiConferenceApprovalMapper busiConferenceApprovalMapper;

    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    @Resource
    private ISysConfigService sysConfigService;

    @Resource
    private ISysUserService sysUserService;
    @Resource
    private IBusiAllMcuTemplateService busiAllMcuTemplateService;

    private Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");

    public  static String regex = "^\\d{4,6}$";
    public  static String regex_chair = "^\\d{6}$";

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
        return busiMcuTencentConferenceAppointmentMapper.getDeptRecordCounts(businessFieldType);
    }

    /**
     * 查询会议预约记录
     *
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    @Override
    public BusiMcuTencentConferenceAppointment selectBusiMcuTencentConferenceAppointmentById(Long id)
    {
        return busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentById(id);
    }

    /**
     * 查询会议预约记录列表
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录
     */
    @Override
    public List<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentList(BusiMcuTencentConferenceAppointment busiConferenceAppointment)
    {
        BusinessFieldType.convert((Integer) busiConferenceAppointment.getParams().get("businessFieldType"));
        return selectBusiMcuTencentConferenceAppointmentListWithOutBusinessFieldType(busiConferenceAppointment);
    }

    @Override
    public List<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentListWithOutBusinessFieldType(BusiMcuTencentConferenceAppointment busiConferenceAppointment)
    {
        List<BusiMcuTencentConferenceAppointment> cas = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentList(busiConferenceAppointment);
        return cas;
    }

    /**
     * 新增会议预约记录
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    @Override
    public Map<String, Object> insertBusiMcuTencentConferenceAppointment(BusiMcuTencentConferenceAppointment busiConferenceAppointment)
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
            JSONArray busiMcuTencentTemplateParticipantArr = (JSONArray) JSON.toJSON(busiConferenceAppointment.getParams().get("templateParticipants"));
            List<BusiMcuTencentTemplateParticipant> busiMcuTencentTemplateParticipants = new ArrayList<>();

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
            List<BusiMcuTencentTemplateDept> templateDepts = new ArrayList<>();
            if (templateDeptArr != null)
            {
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templateDepts.add(templateDeptArr.getObject(i, BusiMcuTencentTemplateDept.class));
                }
            }

            Object cnObj = busiConferenceAppointment.getParams().get("conferenceNumber");
            if (cnObj != null)
            {
                conferenceNumber = TypeUtils.castToLong(cnObj);
            }
            if (conferenceNumber != null)
            {
                BusiMcuTencentTemplateConference con = new BusiMcuTencentTemplateConference();
                con.setConferenceNumber(conferenceNumber);
                con.setDeptId(busiConferenceAppointment.getDeptId());
                List<BusiMcuTencentTemplateConference> tcs = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceList(con);
                Assert.isTrue(ObjectUtils.isEmpty(tcs), "该会议号已被其它模板绑定，请重新选择别的会议号！");


                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, null, masterTerminalId, busiMcuTencentTemplateParticipants, templateDepts);
            }
            else
            {

                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, null, masterTerminalId, busiMcuTencentTemplateParticipants, templateDepts);
                String conferenceNumber1 = new StartTemplateConference().createConferenceNumber(busiConferenceAppointment.getTemplateId(), busiConferenceAppointment.getStartTime());
                conferenceNumber = Long.parseLong(conferenceNumber1);
                BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                tc.setConferenceNumber(conferenceNumber);
                busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(tc);
            }
        }
        else
        {
            BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            if (tc.getConferenceNumber() == null && ConferenceNumberCreateType.convert(tc.getIsAutoCreateConferenceNumber()) == ConferenceNumberCreateType.AUTO)
            {
                String conferenceNumber1 = new StartTemplateConference().createConferenceNumber(busiConferenceAppointment.getTemplateId(), busiConferenceAppointment.getStartTime());
                conferenceNumber=Long.valueOf(conferenceNumber1);
                // 获取模板会议实体对象
                tc.setConferenceNumber(conferenceNumber);
                busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(tc);
            }
            conferenceNumber = tc.getConferenceNumber();
            TencentConferenceContext conferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(tc.getId());

            Object modelBean = busiMcuTencentTemplateConferenceService.selectBusiTemplateConferenceById(tc.getId());

            Map<String, Object> params = (Map<String, Object>) modelBean;
            busiConferenceAppointment.setParams(params);
            Collection<TerminalAttendeeTencent> values = conferenceContext.getTerminalAttendeeMap().values();
            if (values != null && values.size() >0) {
                for (TerminalAttendeeTencent value : values) {
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

        int i = busiMcuTencentConferenceAppointmentMapper.insertBusiMcuTencentConferenceAppointment(busiConferenceAppointment);
        if (i > 0) {
            mqttService.putAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_TENCENT.getCode()), busiConferenceAppointment);
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
     * @author sinhy
     * @since 2021-08-12 14:57
     * @param busiConferenceAppointment
     * @param busiConferenceNumber
     * @param masterTerminalId
     * @param busiMcuTencentTemplateParticipants
     * @param templateDepts void
     */
    @SuppressWarnings("unchecked")
    private void createTemplate(BusiMcuTencentConferenceAppointment busiConferenceAppointment, BusiConferenceNumber busiConferenceNumber, Long masterTerminalId, List<BusiMcuTencentTemplateParticipant> busiMcuTencentTemplateParticipants, List<BusiMcuTencentTemplateDept> templateDepts)
    {

        Assert.isTrue(busiConferenceAppointment.getParams().get("conferenceName") != null, "会议名不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("businessFieldType") != null, "业务领域类型businessFieldType不能为空");

        BusiMcuTencentTemplateConference busiMcuTencentTemplateConference = new BusiMcuTencentTemplateConference();
        busiMcuTencentTemplateConference.setName((String) busiConferenceAppointment.getParams().get("conferenceName"));
        busiMcuTencentTemplateConference.setDeptId(busiConferenceAppointment.getDeptId());
        busiMcuTencentTemplateConference.setRemarks((String) busiConferenceAppointment.getParams().get("remarks"));
        busiMcuTencentTemplateConference.setBusinessFieldType((Integer)busiConferenceAppointment.getParams().get("businessFieldType"));
        busiMcuTencentTemplateConference.setViewType((Integer)busiConferenceAppointment.getParams().get("viewType"));
        // 默认自动分屏
        busiMcuTencentTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
        busiMcuTencentTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        busiMcuTencentTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
        busiMcuTencentTemplateConference.setPollingInterval(10);
        busiMcuTencentTemplateConference.setDefaultViewIsDisplaySelf(-1);
        Integer muteType = null;
        try {
            muteType = TypeUtils.castToInt(busiConferenceAppointment.getParams().get("muteType"));
        } catch (Exception e) {
        }
        busiMcuTencentTemplateConference.setMuteType(muteType);


        if (busiConferenceAppointment.getParams().get("businessProperties") != null)
        {
            busiMcuTencentTemplateConference.setBusinessProperties((Map<String, Object>) busiConferenceAppointment.getParams().get("businessProperties"));
        }

        // 模板会议是否允许被级联：1允许，2不允许
        busiMcuTencentTemplateConference.setType(ConferenceType.SINGLE.getValue());

        // 模板创建类型
        busiMcuTencentTemplateConference.setCreateType(ConferenceTemplateCreateType.AUTO.getValue());

        // 会议号是否自动创建
        busiMcuTencentTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.AUTO.getValue());

        // 直播地址
        YesOrNo streamingEnabled = YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("supportLive"));

        busiMcuTencentTemplateConference.setStreamingEnabled(streamingEnabled.getValue());
        busiMcuTencentTemplateConference.setStreamUrl((String) busiConferenceAppointment.getParams().get("streamUrl"));

        // 是否录制
        busiMcuTencentTemplateConference.setRecordingEnabled(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("supportRecord")).getValue());
        // 自动呼入与会者
        busiMcuTencentTemplateConference.setIsAutoCall(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("isAutoCall")).getValue());
        busiMcuTencentTemplateConference.setIsAutoMonitor(ConferenceAutoMonitor.NO.getValue());


        // 设置会议密码
        if(busiConferenceAppointment.getParams().get("chairmanPassword")!=null){
            String passwordStr_chair = (String) busiConferenceAppointment.getParams().get("chairmanPassword");
            if(Strings.isNotBlank(passwordStr_chair)){
                Pattern pattern = Pattern.compile(regex_chair);
                // 创建匹配器
                Matcher matcher = pattern.matcher(passwordStr_chair);
                if (!matcher.matches()) {
                    throw new CustomException("密码格式不正确,密码是6位的数字");
                }
                busiMcuTencentTemplateConference.setChairmanPassword(passwordStr_chair);
            }
        }

        if(busiConferenceAppointment.getParams().get("password")!=null){
            String passwordStr = (String) busiConferenceAppointment.getParams().get("password");
            if(Strings.isNotBlank(passwordStr)){
                Pattern pattern = Pattern.compile(regex);
                // 创建匹配器
                Matcher matcher = pattern.matcher(passwordStr);
                if (!matcher.matches()) {
                    throw new CustomException("密码格式不正确,密码是4到6位的数字");
                }
                busiMcuTencentTemplateConference.setGuestPassword(passwordStr);
                busiMcuTencentTemplateConference.setConferencePassword(passwordStr);
            }
        }
        Object duration = busiConferenceAppointment.getParams().get("duration");
        if(duration!=null){
            busiMcuTencentTemplateConference.setDurationTime((Integer)duration*60);
        }

        String defaultViewLayout = (String) busiConferenceAppointment.getParams().get("defaultViewLayout");
        if (StringUtils.isNotEmpty(defaultViewLayout)) {
            busiMcuTencentTemplateConference.setDefaultViewLayout(defaultViewLayout);
        }
        busiMcuTencentTemplateConference.setDefaultViewIsBroadcast(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsBroadcast")).getValue());
        busiMcuTencentTemplateConference.setDefaultViewIsFill(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsFill")).getValue());

        // 布局模式

        // 默认带宽2MB
        Integer bandwidth = (Integer)busiConferenceAppointment.getParams().get("bandwidth");
        busiMcuTencentTemplateConference.setBandwidth(bandwidth == null ? 1 : bandwidth);
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
            busiMcuTencentTemplateConference.setCreateUserName(createUserName);
            busiMcuTencentTemplateConference.setCreateUserId(createUserId);
        }

        // 创建一个无主会场，终端的空会议
        int c = busiMcuTencentTemplateConferenceService.insertBusiTemplateConference(busiMcuTencentTemplateConference, null, null, null);
        if (c <= 0)
        {
            busiConferenceNumberForMcuTencentService.deleteBusiConferenceNumberById(busiConferenceNumber.getId());
        }
        else
        {
            if (!ObjectUtils.isEmpty(busiMcuTencentTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuTencentTemplateParticipant busiMcuTencentTemplateParticipant : busiMcuTencentTemplateParticipants)
                {
                    busiMcuTencentTemplateParticipant.setTemplateConferenceId(busiMcuTencentTemplateConference.getId());
                    busiMcuTencentTemplateParticipant.setCreateTime(new Date());
                    busiMcuTencentTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuTencentTemplateParticipantMapper.insertBusiMcuTencentTemplateParticipant(busiMcuTencentTemplateParticipant);
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuTencentTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiMcuTencentTemplateConference.setMasterParticipantId(busiMcuTencentTemplateParticipant.getId());
                        busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiMcuTencentTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuTencentTemplateDept busiMcuTencentTemplateDept : templateDepts)
                {
                    busiMcuTencentTemplateDept.setTemplateConferenceId(busiMcuTencentTemplateConference.getId());
                    busiMcuTencentTemplateDept.setCreateTime(new Date());
                    busiMcuTencentTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuTencentTemplateDeptMapper.insertBusiMcuTencentTemplateDept(busiMcuTencentTemplateDept);
                }
            }
        }
        Assert.isTrue(c > 0, "创建预约会议的默认模板失败！");
        busiConferenceAppointment.setTemplateId(busiMcuTencentTemplateConference.getId());
    }

    private void valid(BusiMcuTencentConferenceAppointment busiConferenceAppointment)
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

        if (busiConferenceAppointment.getTemplateId() != null)
        {
            BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(busiConferenceAppointment.getTemplateId());
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

    private void validTimeSegment(BusiMcuTencentConferenceAppointment busiConferenceAppointment, BusiMcuTencentTemplateConference tc)
    {
        BusiMcuTencentConferenceAppointment con = new BusiMcuTencentConferenceAppointment();
        con.setTemplateId(tc.getId());
        List<BusiMcuTencentConferenceAppointment> cas = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas))
        {
            AppointDatetimeRange appointDatetimeRange = getDateStrRange(busiConferenceAppointment);
            for (BusiMcuTencentConferenceAppointment busiConferenceAppointment2 : cas)
            {
                if (busiConferenceAppointment.getId() != null && busiConferenceAppointment.getId().longValue() == busiConferenceAppointment2.getId().longValue())
                {
                    continue;
                }

                AppointDatetimeRange appointDatetimeRange2 = getDateStrRange(busiConferenceAppointment2);
                if (appointDatetimeRange.isIntersection(appointDatetimeRange2))
                {
                    throw new SystemException(1008425, "该预约会议所选时间段内已存在相同模板的预约会议：" + tc.getConferenceNumber() + " " + appointDatetimeRange2);
                }
            }
        }
    }

    private AppointDatetimeRange getDateStrRange(BusiMcuTencentConferenceAppointment busiConferenceAppointment)
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
    public int updateBusiMcuTencentConferenceAppointment(BusiMcuTencentConferenceAppointment busiConferenceAppointment, boolean checkApproval)
    {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuTencentConferenceAppointment old = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentById(busiConferenceAppointment.getId());
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
                        BusiMcuTencentTemplateConference tc = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        if (tc != null) {
                            conferenceName = tc.getName();
                            busiConferenceAppointment.getParams().put("conferenceName", conferenceName);
                        }
                    }
                }
            }
        }

        int i1 = busiMcuTencentConferenceAppointmentMapper.updateBusiMcuTencentConferenceAppointment(busiConferenceAppointment);

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_TENCENT.getCode()));
        if(busiConferenceAppointment1!=null){
            busiConferenceAppointment.setParams(busiConferenceAppointment1.getParams());
        }
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
                    busiConferenceApproval.setMcuType(McuType.MCU_TENCENT.getCode());
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

            BusiConferenceAppointment appointmentCache = bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_TENCENT.getCode()));
            if(appointmentCache!=null){
                JSONArray oldJson = (JSONArray) JSON.toJSON(appointmentCache.getParams().get("templateParticipants"));
                if (oldJson != null && oldJson.size() > 0) {
                    for (int i = 0; i < oldJson.size(); i++) {
                        boolean isSn = oldJson.getJSONObject(i).containsKey("sn");
                        if (isSn) {
                            snList.add((String) oldJson.getJSONObject(i).get("sn"));
                        }
                    }

                    if (i1 > 0) {
                        bean.updateAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_TENCENT.getCode()), busiConferenceAppointment);
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
    public int deleteBusiMcuTencentConferenceAppointmentByIds(Long[] ids)
    {
        return busiMcuTencentConferenceAppointmentMapper.deleteBusiMcuTencentConferenceAppointmentByIds(ids);
    }

    /**
     * 删除会议预约记录信息
     *
     * @param id 会议预约记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuTencentConferenceAppointmentById(Long id)
    {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuTencentConferenceAppointment busiConferenceAppointment = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentById(id);
        if (busiConferenceAppointment == null)
        {
            return 0;
        }
        if (busiConferenceAppointment.getIsStart() != null && YesOrNo.convert(busiConferenceAppointment.getIsStart()) == YesOrNo.YES)
        {
            throw new SystemException(1004244, "会议进行中，无法删除预约会议！");
        }
        int c = busiMcuTencentConferenceAppointmentMapper.deleteBusiMcuTencentConferenceAppointmentById(id);

        if (busiConferenceAppointment.getTemplateId() != null)
        {
            if (ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO)
            {
                busiMcuTencentTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                new StartTemplateConference().deleteConference(busiConferenceAppointment.getTemplateId());
            }
        }

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiMcuTencentConferenceAppointment busiConferenceAppointment2 = new BusiMcuTencentConferenceAppointment();
        busiConferenceAppointment2.setId(id);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(id, McuType.MCU_TENCENT.getCode()));
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
                bean.removeAppointmentCache(EncryptIdUtil.generateKey(id, McuType.MCU_TENCENT.getCode()), busiConferenceAppointment);
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

        busiAllMcuTemplateService.deleteAllMcuTemplate(busiConferenceAppointment.getTemplateId(), McuType.MCU_TENCENT.getCode());
        return c;
    }

    @Override
    public Page<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentListByKey(String searchKey, Long deptId, Integer pageIndex, Integer pageSize) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        PageHelper.startPage(pageIndex,pageSize);
        Page<BusiMcuTencentConferenceAppointment> cas = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentListByKey(searchKey, deptId);
        return cas;
    }

    @Override
    public Map<String, Object> insertBusiMcuTencentConferenceAppointmentIsMute(BusiMcuTencentConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId) {
        if (isMute) {
            busiConferenceAppointment.getParams().put("muteType", 1);
        } else {
            busiConferenceAppointment.getParams().put("muteType", 0);
        }
        busiConferenceAppointment.getParams().put("presenter", userId);
        Map<String, Object> resultMap = insertBusiMcuTencentConferenceAppointment(busiConferenceAppointment);
        return resultMap;
    }

    @Override
    public List<BusiMcuTencentConferenceAppointment> selectBusiMcuTencentConferenceAppointmentByTemplateId(Long id) {
        return busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentByTemplateId(id);
    }
}
