package com.paradisecloud.fcm.fme.conference.impls;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.interfaces.IBusiAllMcuTemplateService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.conference.interfaces.ITemplateConferenceStartService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysConfigService;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.spring.BeanFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.util.TypeUtils;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.conference.model.AppointDatetimeRange;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.DateUtils;

import javax.annotation.Resource;

/**
 * 会议预约记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-24
 */
@Service
@Transactional
public class BusiConferenceAppointmentServiceImpl implements IBusiConferenceAppointmentService
{
    @Resource
    private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;
    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;
    @Resource
    private BusiTemplateParticipantMapper busiTemplateParticipantMapper;
    @Resource
    private BusiTemplateDeptMapper busiTemplateDeptMapper;
    @Resource
    private ITemplateConferenceStartService templateConferenceStartService;
    @Resource
    private BusiLiveSettingMapper busiLiveSettingMapper;
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
    @Resource
    private BusiConferenceApprovalExcludeMapper busiConferenceApprovalExcludeMapper;

    private Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 10:59
     * @param businessFieldType
     * @return
     * @see com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService#getDeptRecordCounts(java.lang.Integer)
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType)
    {
        return busiConferenceAppointmentMapper.getDeptRecordCounts(businessFieldType);
    }

    @Override
    public Page<BusiConferenceAppointment> selectBusiConferenceAppointmentListBykey(String searchKey, Long deptId,Integer pageIndex,Integer pageSize) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        PageHelper.startPage(pageIndex,pageSize);
        Page<BusiConferenceAppointment> cas = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentListBykey(searchKey,deptId);
        return cas;
    }

    @Override
    public int insertBusiConferenceAppointmentIsMute(BusiConferenceAppointment busiConferenceAppointment, boolean isMute, Long userId) {
        if (busiConferenceAppointment.getRepeatRate() != null) {
            if (busiConferenceAppointment.getRepeatRate() == 3) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(DateUtils.convertToDate(busiConferenceAppointment.getStartTime()));
                boolean isFirstSunday = (cal.getFirstDayOfWeek() == Calendar.SUNDAY);
                // 指示一个星期中的某天。
                int w = isFirstSunday ? cal.get(Calendar.DAY_OF_WEEK) - 1 : cal.get(Calendar.DAY_OF_WEEK);
                busiConferenceAppointment.setRepeatDate(w);
            } else if (busiConferenceAppointment.getRepeatRate() == 4) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(DateUtils.convertToDate(busiConferenceAppointment.getStartTime()));
                // 指示一个月的某天。
                int w = cal.get(Calendar.DAY_OF_MONTH);
                busiConferenceAppointment.setRepeatDate(w);
            }
        }
        ArrayList<String> snList = new ArrayList<>();
        busiConferenceAppointment.setCreateTime(new Date());
        valid(busiConferenceAppointment);

        ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate());

        // 自动生成模板
        if (busiConferenceAppointment.getTemplateId() == null)
        {
            Long masterTerminalId = TypeUtils.castToLong(busiConferenceAppointment.getParams().get("masterTerminalId"));
            JSONArray busiTemplateParticipantArr = (JSONArray) JSON.toJSON(busiConferenceAppointment.getParams().get("templateParticipants"));
            List<BusiTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
            if (busiTemplateParticipantArr != null)
            {
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiTemplateParticipant.class);
                    Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                    busiTemplateParticipants.add(busiTemplateParticipant);
                    if (busiTemplateParticipantArr.getJSONObject(i).containsKey("sn")) {
                        String sn = busiTemplateParticipantArr.getJSONObject(i).getString("sn");
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
            List<BusiTemplateDept> templateDepts = new ArrayList<>();
            if (templateDeptArr != null)
            {
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templateDepts.add(templateDeptArr.getObject(i, BusiTemplateDept.class));
                }
            }

            Object cnObj = busiConferenceAppointment.getParams().get("conferenceNumber");
            Long conferenceNumber = null;
            if (cnObj != null)
            {
                conferenceNumber = TypeUtils.castToLong(cnObj);
            }
            if (conferenceNumber != null)
            {
                BusiTemplateConference con = new BusiTemplateConference();
                con.setConferenceNumber(conferenceNumber);
                con.setDeptId(busiConferenceAppointment.getDeptId());
                List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(con);
                Assert.isTrue(ObjectUtils.isEmpty(tcs), "该会议号已被其它会议使用，请重新选择别的会议号！");
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(conferenceNumber);
                if (busiConferenceNumber == null)
                {
                    // 根据输入的号码初始化号码，再自动创建模板
                    busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumberIsMute(busiConferenceAppointment.getDeptId(), conferenceNumber,isMute);
                }

                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiTemplateParticipants, templateDepts);
            }
            else
            {
                // 自动生成号码，自动创建模板
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumberIsMute(busiConferenceAppointment.getDeptId(), McuType.FME.getCode(),isMute);

                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiTemplateParticipants, templateDepts);
            }
            BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            if (tc != null) {
                tc.setPresenter(userId);
                busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
            }
        }
        else
        {
            BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            Object cnObj = busiConferenceAppointment.getParams().get("conferenceNumber");
            Long conferenceNumber = null;
            if (cnObj != null)
            {
                conferenceNumber = TypeUtils.castToLong(cnObj);
            }
            if (conferenceNumber != null) {
                BusiTemplateConference con = new BusiTemplateConference();
                con.setConferenceNumber(conferenceNumber);
                con.setDeptId(busiConferenceAppointment.getDeptId());
                List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(con);
                Assert.isTrue(ObjectUtils.isEmpty(tcs), "该会议号已被其它会议使用，请重新选择别的会议号！");
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(conferenceNumber);
                if (busiConferenceNumber == null)
                {
                    // 根据输入的号码初始化号码，再自动创建模板
                    busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), McuType.FME.getCode(), conferenceNumber);
                }
                tc.setConferenceNumber(conferenceNumber);
                busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
            }
            if (tc.getConferenceNumber() == null && ConferenceNumberCreateType.convert(tc.getIsAutoCreateConferenceNumber()) == ConferenceNumberCreateType.AUTO)
            {
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), McuType.FME.getCode());

                // 获取模板会议实体对象
                tc.setConferenceNumber(busiConferenceNumber.getId());
                tc.setCallLegProfileId((String) busiConferenceNumber.getParams().get("callLegProfileId"));
                tc.setPresenter(userId);
                busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
            }

            ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(tc.getId());

            Object modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(tc.getId());

            Map<String, Object> params = (Map<String, Object>) modelBean;
            busiConferenceAppointment.setParams(params);
            Collection<TerminalAttendee> values = conferenceContext.getTerminalAttendeeMap().values();
            if (values != null && values.size() >0) {
                for (TerminalAttendee value : values) {
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
        LoginUser loginUser = SecurityUtils.getLoginUser();
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
            if (loginUser != null) {
                createUserId = loginUser.getUser().getUserId();
                createBy = loginUser.getUser().getNickName();
            }
        }
        boolean isApprovalEnabled = false;
        String conferenceApprovalEnable = sysConfigService.selectConfigByKey(ConfigConstant.CONFIG_KEY_CONFERENCE_APPROVAL_ENABLE);
        if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
            if (loginUser != null) {
                Long deptId = loginUser.getUser().getDeptId();
                if (deptId == null) {
                    deptId = 1l;
                }
                if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                    BusiConferenceApprovalExclude busiConferenceApprovalExcludeCon = new BusiConferenceApprovalExclude();
                    busiConferenceApprovalExcludeCon.setType(0);
                    busiConferenceApprovalExcludeCon.setExcludeId(deptId);
                    List<BusiConferenceApprovalExclude> busiConferenceApprovalExcludeList = busiConferenceApprovalExcludeMapper.selectBusiConferenceApprovalExcludeList(busiConferenceApprovalExcludeCon);
                    if (busiConferenceApprovalExcludeList.size() > 0) {
                        conferenceApprovalEnable = "";
                    }
                }
                if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                    BusiConferenceApprovalExclude busiConferenceApprovalExcludeCon = new BusiConferenceApprovalExclude();
                    busiConferenceApprovalExcludeCon.setType(1);
                    busiConferenceApprovalExcludeCon.setExcludeId(loginUser.getUser().getUserId());
                    List<BusiConferenceApprovalExclude> busiConferenceApprovalExcludeList = busiConferenceApprovalExcludeMapper.selectBusiConferenceApprovalExcludeList(busiConferenceApprovalExcludeCon);
                    if (busiConferenceApprovalExcludeList.size() > 0) {
                        conferenceApprovalEnable = "";
                    }
                }
            }
        }
        if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
            isApprovalEnabled = true;
            busiConferenceAppointment.setStatus(AppointmentConferenceStatus.DISABLED.getValue());
        }

        int i = busiConferenceAppointmentMapper.insertBusiConferenceAppointment(busiConferenceAppointment);
        if (i > 0) {
            if (isApprovalEnabled) {
                String jsonString = JSON.toJSONString(busiConferenceAppointment);
                JSONObject jsonObject = JSON.parseObject(jsonString);
                BusiConferenceApproval busiConferenceApproval = new BusiConferenceApproval();
                busiConferenceApproval.setAppointmentConferenceId(busiConferenceAppointment.getId());
                busiConferenceApproval.setConferenceName(conferenceName);
                busiConferenceApproval.setDeptId(busiConferenceAppointment.getDeptId());
                busiConferenceApproval.setApprovalStatus(0);
                busiConferenceApproval.setCreateUserId(createUserId.toString());
                busiConferenceApproval.setCreateBy(createBy);
                busiConferenceApproval.setCreateTime(new Date());
                busiConferenceApproval.setConferenceDetail(jsonObject);
                busiConferenceApprovalMapper.insertBusiConferenceApproval(busiConferenceApproval);
            }
            mqttService.putAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.FME), busiConferenceAppointment);
            if (snList != null && snList.size() > 0) {
                mqttService.conferenceList(snList);
            }
        }
        return i;
    }

    /**
     * 查询会议预约记录
     *
     * @param id 会议预约记录ID
     * @return 会议预约记录
     */
    @Override
    public BusiConferenceAppointment selectBusiConferenceAppointmentById(Long id)
    {
        return busiConferenceAppointmentMapper.selectBusiConferenceAppointmentById(id);
    }

    @Override
    public List<BusiConferenceAppointment> selectBusiConferenceAppointmentByTemplateId(Long id) {
        return busiConferenceAppointmentMapper.selectBusiConferenceAppointmentByTemplateId(id);
    }

    /**
     * 查询会议预约记录列表
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 会议预约记录
     */
    @Override
    public List<BusiConferenceAppointment> selectBusiConferenceAppointmentList(BusiConferenceAppointment busiConferenceAppointment)
    {
        BusinessFieldType.convert((Integer) busiConferenceAppointment.getParams().get("businessFieldType"));
        return selectBusiConferenceAppointmentListWithOutBusinessFieldType(busiConferenceAppointment);
    }

    public List<BusiConferenceAppointment> selectBusiConferenceAppointmentListWithOutBusinessFieldType(BusiConferenceAppointment busiConferenceAppointment)
    {
        List<BusiConferenceAppointment> cas = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentList(busiConferenceAppointment);
        return cas;
    }

    /**
     * 新增会议预约记录
     *
     * @param busiConferenceAppointment 会议预约记录
     * @return 结果
     */
    @Override
    public Map<String, Object> insertBusiConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment)
    {
        Object isAutoCreateStreamUrlObj = busiConferenceAppointment.getParams().get("isAutoCreateStreamUrl");
        if (isAutoCreateStreamUrlObj != null) {
            try {
                Integer isAutoCreateStreamUrl = (Integer) isAutoCreateStreamUrlObj;
                if (isAutoCreateStreamUrl == 1) {
                    busiConferenceAppointment.getParams().put("streamUrl", null);
                }
            } catch (Exception e) {
            }
        }
        if (busiConferenceAppointment.getRepeatRate() != null) {
            if (busiConferenceAppointment.getRepeatRate() == 3) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(DateUtils.convertToDate(busiConferenceAppointment.getStartTime()));
                boolean isFirstSunday = (cal.getFirstDayOfWeek() == Calendar.SUNDAY);
                // 指示一个星期中的某天。
                int w = isFirstSunday ? cal.get(Calendar.DAY_OF_WEEK) - 1 : cal.get(Calendar.DAY_OF_WEEK);
                busiConferenceAppointment.setRepeatDate(w);
            } else if (busiConferenceAppointment.getRepeatRate() == 4) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(DateUtils.convertToDate(busiConferenceAppointment.getStartTime()));
                // 指示一个月的某天。
                int w = cal.get(Calendar.DAY_OF_MONTH);
                busiConferenceAppointment.setRepeatDate(w);
            }
        }
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
            JSONArray busiTemplateParticipantArr = (JSONArray) JSON.toJSON(busiConferenceAppointment.getParams().get("templateParticipants"));
            List<BusiTemplateParticipant> busiTemplateParticipants = new ArrayList<>();
            if (busiTemplateParticipantArr != null)
            {
                for (int i = 0; i < busiTemplateParticipantArr.size(); i++)
                {
                    BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantArr.getObject(i, BusiTemplateParticipant.class);
                    Assert.notNull(busiTemplateParticipant.getTerminalId(), "参会者终端ID不能为空");
                    busiTemplateParticipants.add(busiTemplateParticipant);
                    if (busiTemplateParticipantArr.getJSONObject(i).containsKey("sn")) {
                        String sn = busiTemplateParticipantArr.getJSONObject(i).getString("sn");
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
            List<BusiTemplateDept> templateDepts = new ArrayList<>();
            if (templateDeptArr != null)
            {
                for (int i = 0; i < templateDeptArr.size(); i++)
                {
                    templateDepts.add(templateDeptArr.getObject(i, BusiTemplateDept.class));
                }
            }

            Object cnObj = busiConferenceAppointment.getParams().get("conferenceNumber");
            if (cnObj != null)
            {
                conferenceNumber = TypeUtils.castToLong(cnObj);
            }
            if (conferenceNumber != null)
            {
                BusiTemplateConference con = new BusiTemplateConference();
                con.setConferenceNumber(conferenceNumber);
                con.setDeptId(busiConferenceAppointment.getDeptId());
                List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(con);
                Assert.isTrue(ObjectUtils.isEmpty(tcs), "该会议号已被其它会议使用，请重新选择别的会议号！");
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(conferenceNumber);
                if (busiConferenceNumber == null)
                {
                    // 根据输入的号码初始化号码，再自动创建模板
                    busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), McuType.FME.getCode(), conferenceNumber);
                }

                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiTemplateParticipants, templateDepts);
            }
            else
            {
                // 自动生成号码，自动创建模板
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), McuType.FME.getCode());
                conferenceNumber = busiConferenceNumber.getId();

                // 基于存在的号码，自动创建模板
                createTemplate(busiConferenceAppointment, busiConferenceNumber, masterTerminalId, busiTemplateParticipants, templateDepts);
            }
        }
        else
        {
            BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            Object cnObj = busiConferenceAppointment.getParams().get("conferenceNumber");
            if (cnObj != null)
            {
                conferenceNumber = TypeUtils.castToLong(cnObj);
            }
            if (tc.getStreamingEnabled() == StreamingEnabledType.CLOUDS.getValue()) {
                tc.setIsAutoCreateStreamUrl(YesOrNo.YES.getValue());
            }
            if (conferenceNumber != null) {
                BusiTemplateConference con = new BusiTemplateConference();
                con.setConferenceNumber(conferenceNumber);
                con.setDeptId(busiConferenceAppointment.getDeptId());
                List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(con);
                Assert.isTrue(ObjectUtils.isEmpty(tcs), "该会议号已被其它会议使用，请重新选择别的会议号！");
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(conferenceNumber);
                if (busiConferenceNumber == null)
                {
                    // 根据输入的号码初始化号码，再自动创建模板
                    busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), McuType.FME.getCode(), conferenceNumber);
                }
                tc.setConferenceNumber(conferenceNumber);
                if (tc.getIsAutoCreateStreamUrl() == YesOrNo.YES.getValue()) {
                    tc.setStreamUrl(null);
                }
                busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
            }
            if (tc.getConferenceNumber() == null && ConferenceNumberCreateType.convert(tc.getIsAutoCreateConferenceNumber()) == ConferenceNumberCreateType.AUTO)
            {
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiConferenceAppointment.getDeptId(), McuType.FME.getCode());
                conferenceNumber = busiConferenceNumber.getId();

                // 获取模板会议实体对象
                tc.setConferenceNumber(busiConferenceNumber.getId());
                tc.setCallLegProfileId((String) busiConferenceNumber.getParams().get("callLegProfileId"));
                if (tc.getIsAutoCreateStreamUrl() == YesOrNo.YES.getValue()) {
                    tc.setStreamUrl(null);
                }
                busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
            }

            ConferenceContext conferenceContext = templateConferenceStartService.buildTemplateConferenceContext(tc.getId());

            Object modelBean = busiTemplateConferenceService.selectBusiTemplateConferenceById(tc.getId());

            Map<String, Object> params = (Map<String, Object>) modelBean;
            busiConferenceAppointment.setParams(params);
            Collection<TerminalAttendee> values = conferenceContext.getTerminalAttendeeMap().values();
            if (values != null && values.size() >0) {
                for (TerminalAttendee value : values) {
                    if (StringUtils.isNotEmpty(value.getSn())) {
                        snList.add(value.getSn());
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
            BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            if (tc != null) {
                conferenceName = tc.getName();
                busiConferenceAppointment.getParams().put("conferenceName", conferenceName);
            }
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String createBy = "";
        if (createUserId != null) {
            SysUser sysUser = sysUserService.selectUserById(createUserId);
            if (sysUser != null) {
                createBy = sysUser.getNickName();
            }
        } else {
            if (loginUser != null) {
                createUserId = loginUser.getUser().getUserId();
                createBy = loginUser.getUser().getNickName();
            }
        }
        boolean isApprovalEnabled = false;
        String conferenceApprovalEnable = sysConfigService.selectConfigByKey(ConfigConstant.CONFIG_KEY_CONFERENCE_APPROVAL_ENABLE);
        if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
            if (loginUser != null) {
                Long deptId = loginUser.getUser().getDeptId();
                if (deptId == null) {
                    deptId = 1l;
                }
                if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                    BusiConferenceApprovalExclude busiConferenceApprovalExcludeCon = new BusiConferenceApprovalExclude();
                    busiConferenceApprovalExcludeCon.setType(0);
                    busiConferenceApprovalExcludeCon.setExcludeId(deptId);
                    List<BusiConferenceApprovalExclude> busiConferenceApprovalExcludeList = busiConferenceApprovalExcludeMapper.selectBusiConferenceApprovalExcludeList(busiConferenceApprovalExcludeCon);
                    if (busiConferenceApprovalExcludeList.size() > 0) {
                        conferenceApprovalEnable = "";
                    }
                }
                if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                    BusiConferenceApprovalExclude busiConferenceApprovalExcludeCon = new BusiConferenceApprovalExclude();
                    busiConferenceApprovalExcludeCon.setType(1);
                    busiConferenceApprovalExcludeCon.setExcludeId(loginUser.getUser().getUserId());
                    List<BusiConferenceApprovalExclude> busiConferenceApprovalExcludeList = busiConferenceApprovalExcludeMapper.selectBusiConferenceApprovalExcludeList(busiConferenceApprovalExcludeCon);
                    if (busiConferenceApprovalExcludeList.size() > 0) {
                        conferenceApprovalEnable = "";
                    }
                }
            }
        }
        if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
            isApprovalEnabled = true;
            busiConferenceAppointment.setStatus(AppointmentConferenceStatus.DISABLED.getValue());
        }

        int i = busiConferenceAppointmentMapper.insertBusiConferenceAppointment(busiConferenceAppointment);
        if (i > 0) {
            if (isApprovalEnabled) {
                String jsonString = JSON.toJSONString(busiConferenceAppointment);
                JSONObject jsonObject = JSON.parseObject(jsonString);
                BusiConferenceApproval busiConferenceApproval = new BusiConferenceApproval();
                busiConferenceApproval.setAppointmentConferenceId(busiConferenceAppointment.getId());
                busiConferenceApproval.setMcuType(McuType.FME.getCode());
                busiConferenceApproval.setConferenceName(conferenceName);
                busiConferenceApproval.setDeptId(busiConferenceAppointment.getDeptId());
                busiConferenceApproval.setApprovalStatus(0);
                busiConferenceApproval.setCreateUserId(createUserId.toString());
                busiConferenceApproval.setCreateBy(createBy);
                busiConferenceApproval.setCreateTime(new Date());
                busiConferenceApproval.setConferenceDetail(jsonObject);
                busiConferenceApprovalMapper.insertBusiConferenceApproval(busiConferenceApproval);
            }
            mqttService.putAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.FME.getCode()), busiConferenceAppointment);
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
     * @param busiTemplateParticipants
     * @param templateDepts void
     */
    @SuppressWarnings("unchecked")
    private void createTemplate(BusiConferenceAppointment busiConferenceAppointment, BusiConferenceNumber busiConferenceNumber, Long masterTerminalId, List<BusiTemplateParticipant> busiTemplateParticipants, List<BusiTemplateDept> templateDepts)
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
//        Assert.isTrue(busiConferenceAppointment.getParams().get("isAutoCreateStreamUrl") != null, "是否自动创建直播地址不能为空");

        BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
        busiTemplateConference.setName((String) busiConferenceAppointment.getParams().get("conferenceName"));
        busiTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
        busiTemplateConference.setDeptId(busiConferenceAppointment.getDeptId());
        busiTemplateConference.setRemarks((String) busiConferenceAppointment.getParams().get("remarks"));
        busiTemplateConference.setBusinessFieldType((Integer)busiConferenceAppointment.getParams().get("businessFieldType"));
        busiTemplateConference.setViewType((Integer)busiConferenceAppointment.getParams().get("viewType"));
        busiTemplateConference.setIsAutoCreateStreamUrl((Integer) busiConferenceAppointment.getParams().get("isAutoCreateStreamUrl"));
        if (busiConferenceAppointment.getParams().get("businessProperties") != null)
        {
            busiTemplateConference.setBusinessProperties((Map<String, Object>) busiConferenceAppointment.getParams().get("businessProperties"));
        }
        Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
        if (businessProperties != null) {
            if (businessProperties.containsKey("conferenceMode")) {
                String conferenceMode = businessProperties.get("conferenceMode").toString();
                busiTemplateConference.setConferenceMode(conferenceMode);
                if (ConferenceOpsModeEnum.CHAIRMAN_POLLING.name().equals(conferenceMode)) {
                    Assert.notNull(masterTerminalId, "演讲模式须选择主席！");
                }
            }
        }

        // 模板会议是否允许被级联：1允许，2不允许
        busiTemplateConference.setType(ConferenceType.SINGLE.getValue());

        // 模板创建类型
        busiTemplateConference.setCreateType(ConferenceTemplateCreateType.AUTO.getValue());

        // 会议号是否自动创建
        busiTemplateConference.setIsAutoCreateConferenceNumber(ConferenceNumberCreateType.convert(busiConferenceNumber.getCreateType()).getValue());

        // 会议模板参数设置
        busiTemplateConference.setCallLegProfileId((String) busiConferenceAppointment.getParams().get("callLegProfileId"));
        busiTemplateConference.setCallProfileId((String) busiConferenceAppointment.getParams().get("callProfileId"));
        busiTemplateConference.setCallBrandingProfileId((String) busiConferenceAppointment.getParams().get("callBrandingProfileId"));

        // 直播地址
        StreamingEnabledType streamingEnabled = StreamingEnabledType.convert((Integer)busiConferenceAppointment.getParams().get("streamingEnabled"));
        Integer isAutoCreateStreamUrl = 2;
        Object isAutoCreateStreamUrlObj =  busiConferenceAppointment.getParams().get("isAutoCreateStreamUrl");
        if (isAutoCreateStreamUrlObj != null) {
            try {
                isAutoCreateStreamUrl = (Integer) isAutoCreateStreamUrlObj;
            } catch (Exception e) {
            }
        }
        String streamUrl = null;
        Object streamUrlObj = busiConferenceAppointment.getParams().get("streamUrl");
        if (streamUrlObj != null) {
            streamUrl = (String) streamUrlObj;
        }
        if (streamingEnabled == StreamingEnabledType.CLOUDS) {
            isAutoCreateStreamUrl = 1;
            streamUrl = null;
        }
        if (streamingEnabled == StreamingEnabledType.THIRD_PARTY) {
            isAutoCreateStreamUrl = 2;
            Assert.isTrue(!ObjectUtils.isEmpty(streamUrlObj), "开启直播后，直播地址不能为空！");
        }
        if (isAutoCreateStreamUrl != 1) {
            if (streamingEnabled == StreamingEnabledType.YES) {
                Assert.isTrue(!ObjectUtils.isEmpty(streamUrlObj), "开启直播后，直播地址不能为空！");
            }
        }
        busiTemplateConference.setStreamingEnabled(streamingEnabled.getValue());
        busiTemplateConference.setIsAutoCreateStreamUrl(isAutoCreateStreamUrl);
        busiTemplateConference.setStreamUrl(streamUrl);

        // 新直播方式：事先配置的直播地址时模板会议自动开启直播
        try {
            if (StringUtils.isNotEmpty(busiTemplateConference.getStreamUrl())) {
                BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
                busiLiveSettingCon.setUrl(busiTemplateConference.getStreamUrl());
                List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
                if (busiLiveSettingList.size() > 0) {
                    BusiLiveSetting busiLiveSetting = busiLiveSettingList.get(0);
                    if (StringUtils.isNotEmpty(busiLiveSetting.getRemoteParty())) {
                        // 已经被配置到会议的地址不能再被配置
                        BusiTemplateConference busiTemplateConferenceCon = new BusiTemplateConference();
                        busiTemplateConferenceCon.setStreamUrl(busiTemplateConference.getStreamUrl());
                        List<BusiTemplateConference> busiTemplateConferenceList = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(busiTemplateConferenceCon);
                        if (busiTemplateConferenceList.size() > 0) {
                            BusiTemplateConference busiTemplateConferenceExist = busiTemplateConferenceList.get(0);
                            SysDept sysDept = SysDeptCache.getInstance().get(busiTemplateConferenceExist.getDeptId());
                            String deptName = sysDept.getDeptName();
                            String conferenceName = busiTemplateConferenceExist.getName();
                            throw new SystemException(1004543, "该直播地址已被[" + deptName + ":" + conferenceName + "]使用！请选择其它直播地址！");
                        }
                        busiTemplateConference.setStreamingEnabled(YesOrNo.YES.getValue());
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof SystemException) {
                throw e;
            }
        }

        // 是否录制
        busiTemplateConference.setRecordingEnabled(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("recordingEnabled")).getValue());
        // 是否启用会议纪要
        if (busiConferenceAppointment.getParams().get("minutesEnabled") != null) {
            busiTemplateConference.setMinutesEnabled(YesOrNo.convert((Integer) busiConferenceAppointment.getParams().get("minutesEnabled")).getValue());
        } else {
            busiTemplateConference.setMinutesEnabled(YesOrNo.NO.getValue());
        }

        // 自动呼入与会者
        busiTemplateConference.setIsAutoCall(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("isAutoCall")).getValue());
        busiTemplateConference.setIsAutoMonitor(ConferenceAutoMonitor.NO.getValue());

        // 设置会议密码
        busiTemplateConference.setConferencePassword((String) busiConferenceAppointment.getParams().get("conferencePassword"));
        busiTemplateConference.setDefaultViewLayout((String) busiConferenceAppointment.getParams().get("defaultViewLayout"));
        busiTemplateConference.setDefaultViewIsBroadcast(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsBroadcast")).getValue());
        busiTemplateConference.setDefaultViewIsFill(YesOrNo.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsFill")).getValue());

        // 布局模式
        busiTemplateConference.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert((Integer)busiConferenceAppointment.getParams().get("defaultViewIsDisplaySelf")).getValue());
        busiTemplateConference.setPollingInterval(3);

        // 默认带宽2MB
        Integer bandwidth = (Integer)busiConferenceAppointment.getParams().get("bandwidth");
        busiTemplateConference.setBandwidth(bandwidth == null ? 2 : bandwidth);
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
            busiTemplateConference.setCreateUserName(createUserName);
            busiTemplateConference.setCreateUserId(createUserId);
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            busiTemplateConference.setCreateUserName(loginUser.getUser().getNickName());
            busiTemplateConference.setCreateUserId(loginUser.getUser().getUserId());
        }
        busiTemplateConference.setPresenter((Long)busiConferenceAppointment.getParams().get("presenter"));
        // 时长
        Object durationObj = busiConferenceAppointment.getParams().get("duration");
        if (durationObj != null) {
            Integer duration = (Integer) durationObj;
            busiTemplateConference.setDurationTime(duration * 60);
        } else {
            Long durationTime = (Timestamp.valueOf(busiConferenceAppointment.getEndTime()).getTime() - Timestamp.valueOf(busiConferenceAppointment.getStartTime()).getTime()) / (60 * 1000);
            busiTemplateConference.setDurationTime(durationTime.intValue());
        }
        // 创建一个无主会场，终端的空会议
        int c = busiTemplateConferenceService.insertBusiTemplateConference(busiTemplateConference, null, null, null);
        if (c <= 0)
        {
            busiConferenceNumberService.deleteBusiConferenceNumberById(busiConferenceNumber.getId());
        }
        else
        {
            if (!ObjectUtils.isEmpty(busiTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiTemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
                {
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiTemplateParticipantMapper.insertBusiTemplateParticipant(busiTemplateParticipant);
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiTemplateConference.setMasterParticipantId(busiTemplateParticipant.getId());
                        busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiTemplateDept busiTemplateDept : templateDepts)
                {
                    busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiTemplateDeptMapper.insertBusiTemplateDept(busiTemplateDept);
                }
            }
        }
        Assert.isTrue(c > 0, "创建预约会议的默认模板失败！");
        busiConferenceAppointment.setTemplateId(busiTemplateConference.getId());
    }

    private void valid(BusiConferenceAppointment busiConferenceAppointment)
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
            BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
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

    private void validTimeSegment(BusiConferenceAppointment busiConferenceAppointment, BusiTemplateConference tc)
    {
        BusiConferenceAppointment con = new BusiConferenceAppointment();
        con.setTemplateId(tc.getId());
        List<BusiConferenceAppointment> cas = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas))
        {
            AppointDatetimeRange appointDatetimeRange = getDateStrRange(busiConferenceAppointment);
            for (BusiConferenceAppointment busiConferenceAppointment2 : cas)
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

    private AppointDatetimeRange getDateStrRange(BusiConferenceAppointment busiConferenceAppointment)
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
    public int updateBusiConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment, boolean checkApproval)
    {
        if (busiConferenceAppointment.getRepeatRate() != null) {
            if (busiConferenceAppointment.getRepeatRate() == 3) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(DateUtils.convertToDate(busiConferenceAppointment.getStartTime()));
                boolean isFirstSunday = (cal.getFirstDayOfWeek() == Calendar.SUNDAY);
                // 指示一个星期中的某天。
                int w = isFirstSunday ? cal.get(Calendar.DAY_OF_WEEK) - 1 : cal.get(Calendar.DAY_OF_WEEK);
                busiConferenceAppointment.setRepeatDate(w);
            } else if (busiConferenceAppointment.getRepeatRate() == 4) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(DateUtils.convertToDate(busiConferenceAppointment.getStartTime()));
                // 指示一个月的某天。
                int w = cal.get(Calendar.DAY_OF_MONTH);
                busiConferenceAppointment.setRepeatDate(w);
            }
        }
        ArrayList<String> snList = new ArrayList<>();
        BusiConferenceAppointment old = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentById(busiConferenceAppointment.getId());
        Assert.isTrue(old.getTemplateId().longValue() == busiConferenceAppointment.getTemplateId().longValue(), "会议模板创建方式，以及会议号不能修改！");
        Assert.isTrue(old.getIsStart() == null || old.getIsStart() != YesOrNo.YES.getValue(), "会议正在进行中，不能修改！");
        busiConferenceAppointment.setUpdateTime(new Date());

        valid(busiConferenceAppointment);
        busiConferenceAppointment.setIsHangUp(null);

        String conferenceName = "";
        LoginUser loginUser = SecurityUtils.getLoginUser();
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
            if (loginUser != null) {
                createUserId = loginUser.getUser().getUserId();
                createBy = loginUser.getUser().getNickName();
            }
        }
        boolean isApprovalEnabled = false;
        if (checkApproval) {
            String conferenceApprovalEnable = sysConfigService.selectConfigByKey(ConfigConstant.CONFIG_KEY_CONFERENCE_APPROVAL_ENABLE);
            if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                if (loginUser != null) {
                    Long deptId = loginUser.getUser().getDeptId();
                    if (deptId == null) {
                        deptId = 1l;
                    }
                    if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                        BusiConferenceApprovalExclude busiConferenceApprovalExcludeCon = new BusiConferenceApprovalExclude();
                        busiConferenceApprovalExcludeCon.setType(0);
                        busiConferenceApprovalExcludeCon.setExcludeId(deptId);
                        List<BusiConferenceApprovalExclude> busiConferenceApprovalExcludeList = busiConferenceApprovalExcludeMapper.selectBusiConferenceApprovalExcludeList(busiConferenceApprovalExcludeCon);
                        if (busiConferenceApprovalExcludeList.size() > 0) {
                            conferenceApprovalEnable = "";
                        }
                    }
                    if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                        BusiConferenceApprovalExclude busiConferenceApprovalExcludeCon = new BusiConferenceApprovalExclude();
                        busiConferenceApprovalExcludeCon.setType(1);
                        busiConferenceApprovalExcludeCon.setExcludeId(loginUser.getUser().getUserId());
                        List<BusiConferenceApprovalExclude> busiConferenceApprovalExcludeList = busiConferenceApprovalExcludeMapper.selectBusiConferenceApprovalExcludeList(busiConferenceApprovalExcludeCon);
                        if (busiConferenceApprovalExcludeList.size() > 0) {
                            conferenceApprovalEnable = "";
                        }
                    }
                }
            }
            if (ConfigConstant.CONFERENCE_APPROVAL_ENABLED.equals(conferenceApprovalEnable)) {
                isApprovalEnabled = true;
                busiConferenceAppointment.setStatus(AppointmentConferenceStatus.DISABLED.getValue());
                if (StringUtils.isEmpty(conferenceName)) {
                    if (busiConferenceAppointment.getTemplateId() != null) {
                        BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                        if (tc != null) {
                            conferenceName = tc.getName();
                            busiConferenceAppointment.getParams().put("conferenceName", conferenceName);
                        }
                    }
                }
            }
        }

        int i1 = busiConferenceAppointmentMapper.updateBusiConferenceAppointment(busiConferenceAppointment);

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.FME.getCode()));

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
                    busiConferenceApproval.setMcuType(McuType.FME.getCode());
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
            JSONArray oldJson = (JSONArray) JSON.toJSON(bean.getAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.FME.getCode())).getParams().get("templateParticipants"));
            if (oldJson != null && oldJson.size() > 0) {
                for (int i = 0; i < oldJson.size(); i++) {
                    boolean isSn = oldJson.getJSONObject(i).containsKey("sn");
                    if (isSn) {
                        snList.add((String) oldJson.getJSONObject(i).get("sn"));
                    }
                }

                if (i1 > 0) {
                    bean.updateAppointmentCache(EncryptIdUtil.generateKey(busiConferenceAppointment.getId(), McuType.FME.getCode()), busiConferenceAppointment);
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
    public int deleteBusiConferenceAppointmentByIds(Long[] ids)
    {
        return busiConferenceAppointmentMapper.deleteBusiConferenceAppointmentByIds(ids);
    }

    /**
     * 删除会议预约记录信息
     *
     * @param id 会议预约记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceAppointmentById(Long id)
    {
        ArrayList<String> snList = new ArrayList<>();
        BusiConferenceAppointment busiConferenceAppointment = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentById(id);
        if (busiConferenceAppointment == null)
        {
            return 0;
        }
        if (busiConferenceAppointment.getIsStart() != null && YesOrNo.convert(busiConferenceAppointment.getIsStart()) == YesOrNo.YES)
        {
            throw new SystemException(1004344, "会议进行中，无法删除预约会议！");
        }
        int c = busiConferenceAppointmentMapper.deleteBusiConferenceAppointmentById(id);

        if (busiConferenceAppointment.getTemplateId() != null)
        {
            BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
            if (ConferenceTemplateCreateType.convert(busiConferenceAppointment.getIsAutoCreateTemplate()) == ConferenceTemplateCreateType.AUTO)
            {
                if (tc.getConferenceMode() != null) {
                    BusiTemplateConference busiTemplateConferenceUpdate = new BusiTemplateConference();
                    busiTemplateConferenceUpdate.setId(tc.getId());
                    busiTemplateConferenceUpdate.setConferenceNumber(null);
                    busiTemplateConferenceMapper.updateConferenceNumber(busiTemplateConferenceUpdate);
                } else {
                    busiTemplateConferenceService.deleteBusiTemplateConferenceById(busiConferenceAppointment.getTemplateId());
                }
            }
            else
            {
                tc.setConferenceNumber(null);
                tc.setCallLegProfileId(null);
                busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
            }

            if (tc.getConferenceNumber() != null)
            {
                BusiConferenceNumber bcn = busiConferenceNumberService.selectBusiConferenceNumberById(tc.getConferenceNumber());
                if (bcn != null && ConferenceNumberCreateType.convert(bcn.getCreateType()) == ConferenceNumberCreateType.AUTO)
                {
                    busiConferenceNumberService.deleteBusiConferenceNumberById(tc.getConferenceNumber());
                }
            }
        }

        //发送会议列表到终端
        IMqttService bean = BeanFactory.getBean(IMqttService.class);
        BusiConferenceAppointment busiConferenceAppointment2 = new BusiConferenceAppointment();
        busiConferenceAppointment2.setId(id);
        BusiConferenceAppointment busiConferenceAppointment1 = bean.getAppointmentCache(EncryptIdUtil.generateKey(id, McuType.FME.getCode()));
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
                        String sn = oldJson.getJSONObject(i).getString("sn");
                        if (StringUtils.isNotEmpty(sn)) {
                            snList.add(sn);
                        }
                    }
                }
                bean.removeAppointmentCache(EncryptIdUtil.generateKey(id, McuType.FME.getCode()), busiConferenceAppointment);
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

        busiAllMcuTemplateService.deleteAllMcuTemplate(busiConferenceAppointment.getTemplateId(), McuType.FME.getCode());

        return c;
    }
}
