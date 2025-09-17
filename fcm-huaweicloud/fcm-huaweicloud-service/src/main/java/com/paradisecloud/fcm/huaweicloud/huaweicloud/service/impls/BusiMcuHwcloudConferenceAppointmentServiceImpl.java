package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls;

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
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.TerminalAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiConferenceNumberHwcloudService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudConferenceAppointmentService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudTemplateConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.StartTemplateConference;
import com.paradisecloud.fcm.service.interfaces.IBusiAllMcuTemplateService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateDept;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudTemplateParticipant;
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
public class BusiMcuHwcloudConferenceAppointmentServiceImpl implements IBusiMcuHwcloudConferenceAppointmentService
{
    @Resource
    private BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper;

    @Resource
    private BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper;

    @Resource
    private IBusiMcuHwcloudTemplateConferenceService busiMcuHwcloudTemplateConferenceService;

    @Resource
    private IBusiConferenceNumberHwcloudService busiConferenceNumberForMcuHwcloudService;

    @Resource
    private BusiMcuHwcloudTemplateParticipantMapper busiMcuHwcloudTemplateParticipantMapper;

    @Resource
    private BusiMcuHwcloudTemplateDeptMapper busiMcuHwcloudTemplateDeptMapper;

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
        return busiMcuHwcloudConferenceAppointmentMapper.getDeptRecordCounts(businessFieldType);
    }

    /**
     * 查询会议预约记录
     *
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    @Override
    public BusiMcuHwcloudConferenceAppointment selectBusiMcuHwcloudConferenceAppointmentById(Long id)
    {
        return busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentById(id);
    }

    /**
     * 查询会议预约记录列表
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录
     */
    @Override
    public List<BusiMcuHwcloudConferenceAppointment> selectBusiMcuHwcloudConferenceAppointmentList(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment)
    {
        BusinessFieldType.convert((Integer) busiConferenceAppointment.getParams().get("businessFieldType"));
        return selectBusiMcuHwcloudConferenceAppointmentListWithOutBusinessFieldType(busiConferenceAppointment);
    }

    @Override
    public List<BusiMcuHwcloudConferenceAppointment> selectBusiMcuHwcloudConferenceAppointmentListWithOutBusinessFieldType(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment)
    {
        List<BusiMcuHwcloudConferenceAppointment> cas = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentList(busiConferenceAppointment);
        return cas;
    }

    /**
     * 新增会议预约记录
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    @Override
    public Map<String, Object> insertBusiMcuHwcloudConferenceAppointment(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment)
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

            createTemplate(busiConferenceAppointment, null, null, null, null);
            //创建预约会议
            BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(busiConferenceAppointment.getTemplateId());

            String conferenceNumber1 = new StartTemplateConference().createConferenceNumber(busiConferenceAppointment.getTemplateId(), busiConferenceAppointment.getStartTime());
            conferenceNumber = Long.parseLong(conferenceNumber1);
            tc.setConferenceNumber(conferenceNumber);
            busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);
        }
        else
        {
            BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            if (tc.getConferenceNumber() == null && ConferenceNumberCreateType.convert(tc.getIsAutoCreateConferenceNumber()) == ConferenceNumberCreateType.AUTO)
            {
                String conferenceNumber1 = new StartTemplateConference().createConferenceNumber(busiConferenceAppointment.getTemplateId(), busiConferenceAppointment.getStartTime());
                conferenceNumber=Long.valueOf(conferenceNumber1);
                // 获取模板会议实体对象
                tc.setConferenceNumber(conferenceNumber);
                busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);
            }
            conferenceNumber = tc.getConferenceNumber();
            HwcloudConferenceContext conferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(tc.getId());

            Object modelBean = busiMcuHwcloudTemplateConferenceService.selectBusiTemplateConferenceById(tc.getId());

            Map<String, Object> params = (Map<String, Object>) modelBean;
            busiConferenceAppointment.setParams(params);
            Collection<TerminalAttendeeHwcloud> values = conferenceContext.getTerminalAttendeeMap().values();
            if (values != null && values.size() >0) {
                for (TerminalAttendeeHwcloud value : values) {
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

        int i = busiMcuHwcloudConferenceAppointmentMapper.insertBusiMcuHwcloudConferenceAppointment(busiConferenceAppointment);
        if (i > 0) {
            mqttService.putAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_HWCLOUD.getCode()), busiConferenceAppointment);
            if (snList != null && snList.size() > 0) {
                mqttService.conferenceList(snList);
            }
        }
        resultMap.put("rows", i);
        resultMap.put("templateId", busiConferenceAppointment.getTemplateId());
        resultMap.put("conferenceNumber", conferenceNumber);
        resultMap.put("appointmentId",busiConferenceAppointment.getId());
        return resultMap;
    }
    /**
     * 创建会议模板
     * @author sinhy
     * @since 2021-08-12 14:57
     * @param busiConferenceAppointment
     * @param busiConferenceNumber
     * @param masterTerminalId
     * @param busiMcuHwcloudTemplateParticipants
     * @param templateDepts void
     */
    @SuppressWarnings("unchecked")
    private void createTemplate(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment, BusiConferenceNumber busiConferenceNumber, Long masterTerminalId, List<BusiMcuHwcloudTemplateParticipant> busiMcuHwcloudTemplateParticipants, List<BusiMcuHwcloudTemplateDept> templateDepts)
    {

        Assert.isTrue(busiConferenceAppointment.getParams().get("conferenceName") != null, "会议名不能为空");
        Assert.isTrue(busiConferenceAppointment.getParams().get("businessFieldType") != null, "业务领域类型businessFieldType不能为空");

        BusiMcuHwcloudTemplateConference busiMcuHwcloudTemplateConference = new BusiMcuHwcloudTemplateConference();
        busiMcuHwcloudTemplateConference.setName((String) busiConferenceAppointment.getParams().get("conferenceName"));
        busiMcuHwcloudTemplateConference.setDeptId(busiConferenceAppointment.getDeptId());
        busiMcuHwcloudTemplateConference.setRemarks((String) busiConferenceAppointment.getParams().get("remarks"));
        busiMcuHwcloudTemplateConference.setBusinessFieldType((Integer)busiConferenceAppointment.getParams().get("businessFieldType"));
        busiMcuHwcloudTemplateConference.setViewType((Integer)busiConferenceAppointment.getParams().get("viewType"));
        // 默认自动分屏
        busiMcuHwcloudTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
        busiMcuHwcloudTemplateConference.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        busiMcuHwcloudTemplateConference.setDefaultViewIsFill(YesOrNo.YES.getValue());
        busiMcuHwcloudTemplateConference.setPollingInterval(10);
        busiMcuHwcloudTemplateConference.setDefaultViewIsDisplaySelf(-1);
        Integer muteType = null;
        try {
            muteType = TypeUtils.castToInt(busiConferenceAppointment.getParams().get("muteType"));
        } catch (Exception e) {
        }
        busiMcuHwcloudTemplateConference.setMuteType(muteType);


        if (busiConferenceAppointment.getParams().get("businessProperties") != null)
        {
            busiMcuHwcloudTemplateConference.setBusinessProperties((Map<String, Object>) busiConferenceAppointment.getParams().get("businessProperties"));
        }

        // 模板会议是否允许被级联：1允许，2不允许
        busiMcuHwcloudTemplateConference.setType(ConferenceType.SINGLE.getValue());

        // 模板创建类型
        busiMcuHwcloudTemplateConference.setCreateType(ConferenceTemplateCreateType.AUTO.getValue());

        // 会议号是否自动创建
        busiMcuHwcloudTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.AUTO.getValue());

        // 直播地址
        YesOrNo streamingEnabled = YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("supportLive"));

        busiMcuHwcloudTemplateConference.setStreamingEnabled(streamingEnabled.getValue());
        busiMcuHwcloudTemplateConference.setStreamUrl((String) busiConferenceAppointment.getParams().get("streamUrl"));

        // 是否录制
        busiMcuHwcloudTemplateConference.setRecordingEnabled(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("supportRecord")).getValue());
        // 自动呼入与会者
        busiMcuHwcloudTemplateConference.setIsAutoCall(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("isAutoCall")).getValue());
        busiMcuHwcloudTemplateConference.setIsAutoMonitor(ConferenceAutoMonitor.NO.getValue());
        if(busiConferenceAppointment.getParams().get("duration")!=null){
            busiMcuHwcloudTemplateConference.setDurationTime((Integer) busiConferenceAppointment.getParams().get("duration")*60);
        }else {
            busiMcuHwcloudTemplateConference.setDurationTime(120);
        }





        if(busiConferenceAppointment.getParams().get("conferencePassword")!=null){
            String passwordStr = (String) busiConferenceAppointment.getParams().get("conferencePassword");
            if(Strings.isNotBlank(passwordStr)){
                Pattern pattern = Pattern.compile(regex);
                // 创建匹配器
                Matcher matcher = pattern.matcher(passwordStr);
                if (!matcher.matches()) {
                    throw new CustomException("密码格式不正确,密码是4到6位的数字");
                }
                busiMcuHwcloudTemplateConference.setGuestPassword(passwordStr);
                busiMcuHwcloudTemplateConference.setConferencePassword(passwordStr);
            }
        }

        String defaultViewLayout = (String) busiConferenceAppointment.getParams().get("defaultViewLayout");
        if (StringUtils.isNotEmpty(defaultViewLayout)) {
            busiMcuHwcloudTemplateConference.setDefaultViewLayout(defaultViewLayout);
        }
        busiMcuHwcloudTemplateConference.setDefaultViewIsBroadcast(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsBroadcast")).getValue());
        busiMcuHwcloudTemplateConference.setDefaultViewIsFill(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsFill")).getValue());

        // 布局模式

        // 默认带宽2MB
        Integer bandwidth = (Integer)busiConferenceAppointment.getParams().get("bandwidth");
        busiMcuHwcloudTemplateConference.setBandwidth(bandwidth == null ? 1 : bandwidth);
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
            busiMcuHwcloudTemplateConference.setCreateUserName(createUserName);
            busiMcuHwcloudTemplateConference.setCreateUserId(createUserId);
        }

        // 创建一个无主会场，终端的空会议
        int c = busiMcuHwcloudTemplateConferenceService.insertBusiTemplateConference(busiMcuHwcloudTemplateConference, null, null, null);
        if (c <= 0)
        {
            busiConferenceNumberForMcuHwcloudService.deleteBusiConferenceNumberById(busiConferenceNumber.getId());
        }
        else
        {
            if (!ObjectUtils.isEmpty(busiMcuHwcloudTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuHwcloudTemplateParticipant busiMcuHwcloudTemplateParticipant : busiMcuHwcloudTemplateParticipants)
                {
                    busiMcuHwcloudTemplateParticipant.setTemplateConferenceId(busiMcuHwcloudTemplateConference.getId());
                    busiMcuHwcloudTemplateParticipant.setCreateTime(new Date());
                    busiMcuHwcloudTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuHwcloudTemplateParticipantMapper.insertBusiMcuHwcloudTemplateParticipant(busiMcuHwcloudTemplateParticipant);
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuHwcloudTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiMcuHwcloudTemplateConference.setMasterParticipantId(busiMcuHwcloudTemplateParticipant.getId());
                        busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiMcuHwcloudTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuHwcloudTemplateDept busiMcuHwcloudTemplateDept : templateDepts)
                {
                    busiMcuHwcloudTemplateDept.setTemplateConferenceId(busiMcuHwcloudTemplateConference.getId());
                    busiMcuHwcloudTemplateDept.setCreateTime(new Date());
                    busiMcuHwcloudTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuHwcloudTemplateDeptMapper.insertBusiMcuHwcloudTemplateDept(busiMcuHwcloudTemplateDept);
                }
            }
        }
        Assert.isTrue(c > 0, "创建预约会议的默认模板失败！");
        busiConferenceAppointment.setTemplateId(busiMcuHwcloudTemplateConference.getId());
    }

    private void valid(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment)
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
            BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(busiConferenceAppointment.getTemplateId());
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

    private void validTimeSegment(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment, BusiMcuHwcloudTemplateConference tc)
    {
        BusiMcuHwcloudConferenceAppointment con = new BusiMcuHwcloudConferenceAppointment();
        con.setTemplateId(tc.getId());
        List<BusiMcuHwcloudConferenceAppointment> cas = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas))
        {
            AppointDatetimeRange appointDatetimeRange = getDateStrRange(busiConferenceAppointment);
            for (BusiMcuHwcloudConferenceAppointment busiConferenceAppointment2 : cas)
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

    private AppointDatetimeRange getDateStrRange(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment)
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
    public int updateBusiMcuHwcloudConferenceAppointment(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment, boolean checkApproval)
    {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuHwcloudConferenceAppointment old = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentById(busiConferenceAppointment.getId());
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
                        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        if (tc != null) {
                            conferenceName = tc.getName();
                            busiConferenceAppointment.getParams().put("conferenceName", conferenceName);
                        }
                    }
                }
            }
        }

        int i1 = busiMcuHwcloudConferenceAppointmentMapper.updateBusiMcuHwcloudConferenceAppointment(busiConferenceAppointment);

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_HWCLOUD.getCode()));
        if(busiConferenceAppointment1!=null){
            busiConferenceAppointment.setParams(busiConferenceAppointment1.getParams());
        }
        if (i1 > 0) {
            if (isApprovalEnabled) {
                String jsonString = JSON.toJSONString(busiConferenceAppointment);
                JSONObject jsonObject = JSON.parseObject(jsonString);;
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
                    busiConferenceApproval.setMcuType(McuType.MCU_HWCLOUD.getCode());
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
            BusiConferenceAppointment appointmentCache = bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_HWCLOUD.getCode()));
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
                        bean.updateAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.MCU_HWCLOUD.getCode()), busiConferenceAppointment);
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
    public int deleteBusiMcuHwcloudConferenceAppointmentByIds(Long[] ids)
    {
        return busiMcuHwcloudConferenceAppointmentMapper.deleteBusiMcuHwcloudConferenceAppointmentByIds(ids);
    }

    /**
     * 删除会议预约记录信息
     *
     * @param id 会议预约记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuHwcloudConferenceAppointmentById(Long id)
    {
        ArrayList<String> snList = new ArrayList<>();
        BusiMcuHwcloudConferenceAppointment busiConferenceAppointment = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentById(id);
        if (busiConferenceAppointment == null)
        {
            return 0;
        }
        if (busiConferenceAppointment.getIsStart() != null && YesOrNo.convert(busiConferenceAppointment.getIsStart()) == YesOrNo.YES)
        {
            throw new SystemException(1004244, "会议进行中，无法删除预约会议！");
        }
        int c = busiMcuHwcloudConferenceAppointmentMapper.deleteBusiMcuHwcloudConferenceAppointmentById(id);

        if (busiConferenceAppointment.getTemplateId() != null)
        {
            if (ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO)
            {
                new StartTemplateConference().deleteConference(busiConferenceAppointment.getTemplateId());
                busiMcuHwcloudTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());

            }
        }

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiMcuHwcloudConferenceAppointment busiConferenceAppointment2 = new BusiMcuHwcloudConferenceAppointment();
        busiConferenceAppointment2.setId(id);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(id, McuType.MCU_HWCLOUD.getCode()));
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
                bean.removeAppointmentCache(EncryptIdUtil.generateKey(id, McuType.MCU_HWCLOUD.getCode()), busiConferenceAppointment);
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

        busiAllMcuTemplateService.deleteAllMcuTemplate(busiConferenceAppointment.getTemplateId(), McuType.MCU_HWCLOUD.getCode());
        return c;
    }

    @Override
    public Page<BusiMcuHwcloudConferenceAppointment> selectBusiMcuHwcloudConferenceAppointmentListByKey(String searchKey, Long deptId, Integer pageIndex, Integer pageSize) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        PageHelper.startPage(pageIndex,pageSize);
        Page<BusiMcuHwcloudConferenceAppointment> cas = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentListByKey(searchKey, deptId);
        return cas;
    }

    @Override
    public Map<String, Object> insertBusiMcuHwcloudConferenceAppointmentIsMute(BusiMcuHwcloudConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId) {
        if (isMute) {
            busiConferenceAppointment.getParams().put("muteType", 1);
        } else {
            busiConferenceAppointment.getParams().put("muteType", 0);
        }
        busiConferenceAppointment.getParams().put("presenter", userId);
        Map<String, Object> resultMap = insertBusiMcuHwcloudConferenceAppointment(busiConferenceAppointment);
        return resultMap;
    }

    @Override
    public List<BusiMcuHwcloudConferenceAppointment> selectBusiMcuHwcloudConferenceAppointmentByTemplateId(Long id) {
        return busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentByTemplateId(id);
    }
}
