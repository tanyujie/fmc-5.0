package com.paradisecloud.fcm.smc2.conference.templateconference;

import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.smc2.cache.*;
import com.paradisecloud.fcm.smc2.model.DeptWeight;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.McuAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IDefaultAttendeeSmc2OperationPackageService;
import com.paradisecloud.fcm.smc2.utils.AttendeeSmc2Utils;
import com.paradisecloud.fcm.smc2.utils.AttendeeUtils;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author nj
 * @date 2022/4/20 11:04
 */
public class BuildTemplateConferenceContext {


    public Smc2ConferenceContext buildTemplateConferenceContext(long templateConferenceId){

        // 获取模板会议实体对象
        BusiMcuSmc2TemplateConference tc = BeanFactory.getBean(BusiMcuSmc2TemplateConferenceMapper.class).selectBusiMcuSmc2TemplateConferenceById(templateConferenceId);
        BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
        if (tc == null)
        {
            throw new CustomException("模版不存在");
        }

        Smc2ConferenceContext conferenceContext = null;
        if (tc.getConferenceNumber() != null)
        {
            // 缓存中还有会议上下文，则证明会议正在进行，直接返回给前端
            conferenceContext = Smc2ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.SMC2));
            if (conferenceContext != null)
            {
                // 简单级联
                bindCascadeConference(conferenceContext, tc);
                return conferenceContext;
            }
        }
        Smc2Bridge smc2Bridge = Smc2BridgeCache.getInstance().getBridgesByDept(tc.getDeptId());
        if(smc2Bridge==null){
            throw new CustomException("未找到MCU,或正在初始化中...,稍后再试");
        }
        // 会议上下文对象
        conferenceContext = new Smc2ConferenceContext(smc2Bridge);


        // 拷贝模板参数到会议上下文
        copyTemplateAttrs(conferenceContext, tc);
        // 会议室的id
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));
        if (tc.getConferenceNumber() != null)
        {
            // 会议室的id，这里指向加密后的ConferenceNumber
            conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));
            // 设置FME参会的uri
            conferenceContext.setFmeAttendeeRemoteParty(smc2Bridge.getRootUrl());
        }
        if(conferenceContext.getConferenceNumber()!=null){
            String conferenceRemoteParty = conferenceContext.getTenantId()==null?"":conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }

        BusiMcuSmc2TemplateParticipant condition = new BusiMcuSmc2TemplateParticipant();
        condition.setTemplateConferenceId(templateConferenceId);
        List<BusiMcuSmc2TemplateParticipant> allParticipants = BeanFactory.getBean(BusiMcuSmc2TemplateParticipantMapper.class).selectBusiMcuSmc2TemplateParticipantList(condition);

        // 创建部门-与会者列表映射
        Map<Long, List<BusiMcuSmc2TemplateParticipant>> deptPsMap = new HashMap<>();
        Map<Long, BusiMcuSmc2TemplateParticipant> busiTemplateParticipantMap = new HashMap<>();
        BusiMcuSmc2TemplateParticipant templateMasterParticipant = null;
        for (BusiMcuSmc2TemplateParticipant busiTemplateParticipant : allParticipants)
        {
            BusiTerminal terminal = TerminalCache.getInstance().get(busiTemplateParticipant.getTerminalId());
            if (AttendType.convert(busiTemplateParticipant.getAttendType()).isJoin())
            {
                if (tc.getMasterParticipantId() != null && busiTemplateParticipant.getId().longValue() == tc.getMasterParticipantId().longValue())
                {
                    templateMasterParticipant = busiTemplateParticipant;
                }
                List<BusiMcuSmc2TemplateParticipant> deptPs = deptPsMap.get(terminal.getDeptId());
                if (deptPs == null)
                {
                    deptPs = new ArrayList<>();
                    deptPsMap.put(terminal.getDeptId(), deptPs);
                }
                deptPs.add(busiTemplateParticipant);
                busiTemplateParticipantMap.put(busiTemplateParticipant.getId(), busiTemplateParticipant);
            }
            else
            {
                // 添加直播终端
                TerminalAttendeeSmc2 ta = AttendeeSmc2Utils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addLiveTerminal(ta);
            }
        }

        // 先拉会议发起方的与会者
        List<BusiMcuSmc2TemplateParticipant> ps = deptPsMap.get(tc.getDeptId());
        if (ps != null)
        {
            for (BusiMcuSmc2TemplateParticipant busiTemplateParticipant : ps)
            {
                TerminalAttendeeSmc2 ta = AttendeeSmc2Utils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addAttendee(ta);
            }

            // 根据显示顺序排序
            Collections.sort(conferenceContext.getAttendees());
        }

        BusiMcuSmc2TemplateDept busiTemplateDeptCondition = new BusiMcuSmc2TemplateDept();
        busiTemplateDeptCondition.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc2TemplateDept> busiTemplateDepts = BeanFactory.getBean(BusiMcuSmc2TemplateDeptMapper.class).selectBusiMcuSmc2TemplateDeptList(busiTemplateDeptCondition);
        if (!ObjectUtils.isEmpty(busiTemplateDepts))
        {
            // 添加所有子会议启动器
            buildMainConferenceContext(deptPsMap, busiTemplateDepts, conferenceContext);

            // 排序部门
            Collections.sort(conferenceContext.getDeptWeights());

            // 排序FME参会者
            Collections.sort(conferenceContext.getFmeAttendees());

            // 对每个子会议的参会者进行排序
            conferenceContext.getCascadeAttendeesMap().forEach((k, v) -> {
                Collections.sort(v);
            });
        }

        // 设置主会场
        if (templateMasterParticipant != null)
        {
            AttendeeSmc2 masterAttendee = conferenceContext.getAttendeeById(templateMasterParticipant.getUuid());
            conferenceContext.setMasterAttendee(masterAttendee);
        }



        // 默认参会者操作解析设置
        DefaultAttendeeOperation defaultAttendeeOperation = BeanFactory.getBean(IDefaultAttendeeSmc2OperationPackageService.class).packing(conferenceContext, tc, busiTemplateParticipantMap);
        conferenceContext.setDefaultViewOperation(defaultAttendeeOperation);

        BusiMcuSmc2ConferenceAppointment con = new BusiMcuSmc2ConferenceAppointment();
        con.setTemplateId(templateConferenceId);
        con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
        List<BusiMcuSmc2ConferenceAppointment> cas = BeanFactory.getBean(BusiMcuSmc2ConferenceAppointmentMapper.class).selectBusiMcuSmc2ConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas)) {
            conferenceContext.setAppointment(true);
            conferenceContext.setAppointmentType(cas.get(0).getType());

            BusiConferenceAppointment busiConferenceAppointment = cas.get(0);
            if (busiConferenceAppointment.getApprovalId() != null) {
                BusiConferenceApproval busiConferenceApproval = BeanFactory.getBean(BusiConferenceApprovalMapper.class).selectBusiConferenceApprovalById(busiConferenceAppointment.getApprovalId());
                if (busiConferenceApproval != null) {
                    conferenceContext.setApprovedConference(true);
                }
            }
        }
        // 简单级联
        bindCascadeConference(conferenceContext, tc);
        return conferenceContext;
    }
    private void copyTemplateAttrs(Smc2ConferenceContext conferenceContext, BusiSmc2DeptTemplate tc)
    {

        SmcConference smcConference = new SmcConference();
        smcConference.setDuration(tc.getDuration());
        smcConference.setUsername(tc.getCreatUser());
        smcConference.setSubject(tc.getTemplateName());
        conferenceContext.setAccessCode(tc.getAccessCode());
        conferenceContext.setConference(smcConference);
        conferenceContext.setRate(tc.getRate());
        conferenceContext.setTemplateConferenceId(Long.valueOf(tc.getId().longValue()));
        conferenceContext.setDeptId(tc.getDeptId().longValue());
        conferenceContext.setSmc2TemplateId(tc.getSmc2TemplateId());
        conferenceContext.setTemplateCreateTime(tc.getCreateTime());
        ConferenceState conferenceState = conferenceContext.getDetailConference().getConferenceState();
        ConferenceUiParam conferenceUiParam = conferenceContext.getDetailConference().getConferenceUiParam();
        conferenceState.setMultiPicPollStatus("CANCEL");
        conferenceState.setBroadcastPollStatus("CANCEL");
        conferenceState.setChairmanPollStatus("CANCEL");
        conferenceState.setTemplateId(tc.getId().longValue());
        conferenceState.setType(tc.getType());
        conferenceUiParam.setSubject(tc.getTemplateName());
        conferenceUiParam.setAccessCode(tc.getAccessCode());
        conferenceUiParam.setDuration(tc.getDuration());
        ConfTextTip confTextTip = new ConfTextTip();
        conferenceUiParam.setConfTextTip(confTextTip);
        smcConference.setRate(tc.getRate());
        conferenceContext.setCreateUser(tc.getCreatUser());

    }

    /**
     * <pre>拷贝模板参数到会议上下文</pre>
     * @author lilinhai
     * @since 2021-02-02 15:42
     * @param conferenceContext
     * @param tc void
     */
    private void copyTemplateAttrs(Smc2ConferenceContext conferenceContext, BusiMcuSmc2TemplateConference tc)
    {
        conferenceContext.setName(tc.getName());
        conferenceContext.setRemarks(tc.getRemarks());
        conferenceContext.setDeptId(tc.getDeptId());
        conferenceContext.setBandwidth(tc.getBandwidth());
        if (tc.getConferenceNumber() != null)
        {
            conferenceContext.setConferenceNumber(String.valueOf(tc.getConferenceNumber()));
        }
        conferenceContext.setType(tc.getType());
        conferenceContext.setTemplateConferenceId(tc.getId());
        conferenceContext.setBusinessFieldType(tc.getBusinessFieldType());
        conferenceContext.setConferencePassword(tc.getConferencePassword());
        conferenceContext.setTemplateCreateTime(new Date());

        // 呼叫设置
        conferenceContext.setAutoCallTerminal(tc.getIsAutoCall() != null && YesOrNo.convert(tc.getIsAutoCall()) == YesOrNo.YES);

        // 录制开关
        if (tc.getRecordingEnabled() != null && YesOrNo.convert(tc.getRecordingEnabled()) == YesOrNo.YES) {
            conferenceContext.setRecordingEnabled(YesOrNo.YES.getValue());
            conferenceContext.setRecorded(true);
        } else {
            conferenceContext.setRecordingEnabled(YesOrNo.NO.getValue());
            conferenceContext.setRecorded(false);
        }

        // 直播开关
        if (tc.getStreamingEnabled() != null && YesOrNo.convert(tc.getStreamingEnabled()) == YesOrNo.YES) {
            conferenceContext.setStreamingEnabled(YesOrNo.YES.getValue());
            conferenceContext.setStreaming(true);
        } else {
            conferenceContext.setStreamingEnabled(YesOrNo.NO.getValue());
            conferenceContext.setStreaming(false);
        }
        conferenceContext.setStreamingUrl(tc.getStreamUrl());


        conferenceContext.setDurationEnabled(tc.getDurationEnabled());
        conferenceContext.setDurationTime(tc.getDurationTime());
        conferenceContext.setCreateUserId(tc.getCreateUserId());
    }


    /**
     * <pre>添加所有子会议启动器</pre>
     * @author lilinhai
     * @since 2021-02-01 15:02
     * @param deptPsMap
     * @param busiTemplateDepts
     */
    private void buildMainConferenceContext(Map<Long, List<BusiMcuSmc2TemplateParticipant>> deptPsMap, List<BusiMcuSmc2TemplateDept> busiTemplateDepts, Smc2ConferenceContext conferenceContext)
    {
        List<BusiMcuSmc2TemplateParticipant> ps;

        // 倒叙排列，按此顺序进行先后子会议的呼叫发起
        Collections.sort(busiTemplateDepts, new Comparator<BusiMcuSmc2TemplateDept>()
        {
            @Override
            public int compare(BusiMcuSmc2TemplateDept o1, BusiMcuSmc2TemplateDept o2) {
                return o2.getWeight().compareTo(o1.getWeight());
            }

        });

        for (BusiMcuSmc2TemplateDept busiTemplateDept : busiTemplateDepts)
        {
            // 先拉会议发起方的与会者
            ps = deptPsMap.get(busiTemplateDept.getDeptId());
            if (ps == null)
            {
                continue;
            }

            boolean isBindSameFme = DeptSmc2MappingCache.getInstance().isBindSameFme(conferenceContext.getDeptId(), busiTemplateDept.getDeptId());

            // 如果相等，则说明是两个部门绑定的是同一个FME或FME集群
            if (!isBindSameFme)
            {
                // 是否存在绑定FME集群的部门
                if (DeptSmc2MappingCache.getInstance().isExsitFmeClusterBind(conferenceContext.getDeptId(), busiTemplateDept.getDeptId()))
                {
                    throw new SystemException(1006756, "级联会议不支持在集群的FME中使用！");
                }

                // 绑定级联的FME参会信息
                bindCascadeFmeAttendee(conferenceContext, busiTemplateDept);
            }

            BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
            DeptWeight dw = new DeptWeight();
            dw.setDeptId(busiTemplateDept.getDeptId());
            dw.setWeight(busiTemplateDept.getWeight());
            conferenceContext.addDeptWeight(dw);
            for (BusiMcuSmc2TemplateParticipant busiTemplateParticipant : ps)
            {
                TerminalAttendeeSmc2 ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }

                // 封装级联与会者属性并添加到集合
                conferenceContext.addCascadeAttendee(ta);

                // 如果绑定的FME是同一个，则会议号用跟主会议同一个号
                if (isBindSameFme)
                {
                    ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                    conferenceContext.addAttendeeToIdMap(ta);
                    conferenceContext.addAttendeeToRemotePartyMap(ta);
                }
            }

            // 排序级联子会议终端
            Collections.sort(conferenceContext.getCascadeAttendeesMap().get(busiTemplateDept.getDeptId()));

            AttendeeSmc2 m = conferenceContext.getCascadeAttendeesMap().get(busiTemplateDept.getDeptId()).remove(0);

            // 添加地州主会场终端到主导方集合中，同时删除级联子会议的主会场
            conferenceContext.addMasterAttendee(m);
        }
    }



    /**
     * <pre>绑定级联的FME参会信息</pre>
     * @author lilinhai
     * @since 2021-03-23 18:05
     * @param conferenceContext
     * @param busiTemplateDept void
     */
    private void bindCascadeFmeAttendee(Smc2ConferenceContext conferenceContext, BusiMcuSmc2TemplateDept busiTemplateDept)
    {
        BusiConferenceNumber busiConferenceNumberCondition = new BusiConferenceNumber();
        busiConferenceNumberCondition.setDeptId(busiTemplateDept.getDeptId());
        busiConferenceNumberCondition.setType(ConferenceType.CASCADE.getValue());
        List<BusiConferenceNumber> cns = BeanFactory.getBean(BusiConferenceNumberMapper.class).selectBusiConferenceNumberList(busiConferenceNumberCondition);
        if (ObjectUtils.isEmpty(cns))
        {
            throw new SystemException(1002334, "会议发起失败，未配置【" + SysDeptCache.getInstance().get(busiTemplateDept.getDeptId()).getDeptName() + "】的集群会议号！");
        }

        Collections.sort(cns, new Comparator<BusiConferenceNumber>()
        {
            @Override
            public int compare(BusiConferenceNumber o1, BusiConferenceNumber o2)
            {
                return o2.getId().compareTo(o1.getId());
            }
        });

        BusiConferenceNumber conferenceNumber = cns.get(0);

        // 获取会议桥工具对象
        Smc2Bridge fmeBridge = Smc2BridgeCache.getInstance().getBridgesByDept(busiTemplateDept.getDeptId());
        McuAttendeeSmc2 fmeAttendee = new McuAttendeeSmc2();
        fmeAttendee.setName(SysDeptCache.getInstance().get(busiTemplateDept.getDeptId()).getDeptName());
        fmeAttendee.setWeight(busiTemplateDept.getWeight());
        fmeAttendee.setId(busiTemplateDept.getUuid());
        if (fmeBridge != null)
        {
            fmeAttendee.setIp(fmeBridge.getBusiSmc2().getIp());
            fmeAttendee.setMcuId(fmeBridge.getBusiSmc2().getId());
            fmeAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        }
        else
        {
            fmeAttendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        }

        fmeAttendee.setCascadeDeptId(busiTemplateDept.getDeptId());
        fmeAttendee.setCascadeConferenceNumber(conferenceNumber.getId().toString());
        fmeAttendee.setConferenceNumber(conferenceNumber.getId().toString());
        conferenceContext.addFmeAttendee(fmeAttendee);
    }


    /**
     * <pre>绑定级联的MCU参会信息</pre>
     * @author lilinhai
     * @since 2021-03-23 18:05
     * @param conferenceContext
     */
    private void bindCascadeConference(Smc2ConferenceContext conferenceContext, BusiMcuSmc2TemplateConference tc) {
        Map<String, ViewTemplateConference> newConferenceIdMap = new HashMap<>();
        Map<String, McuAttendeeSmc2> oldConferenceIdMap = new HashMap<>();

        List<McuAttendeeSmc2> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeSmc2 attendee : mcuAttendees) {
            if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(attendee.getId())) {
                String contextKey = EncryptIdUtil.parasToContextKey(attendee.getId());
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                if (baseConferenceContext != null) {
                    String remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                    if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                        remoteParty += ":" + baseConferenceContext.getMcuCallPort();
                    }
                    attendee.setRemoteParty(remoteParty);
                    attendee.setIp(baseConferenceContext.getMcuCallIp());
                    attendee.setName(baseConferenceContext.getName());
                    attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                } else {
                    attendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                }
                oldConferenceIdMap.put(attendee.getId(), attendee);
            }
        }
        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
        ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
        viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
        viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
        for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceList) {
            newConferenceIdMap.put(viewTemplateConference.getConferenceId(), viewTemplateConference);
        }
        for (String conferenceIdTemp : oldConferenceIdMap.keySet()) {
            if (!newConferenceIdMap.containsKey(conferenceIdTemp)) {
                conferenceContext.removeMcuAttendee(oldConferenceIdMap.get(conferenceIdTemp));
            }
        }
        for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceList) {
            if (!oldConferenceIdMap.containsKey(viewTemplateConference.getConferenceId())) {
                bindCascadeConference(conferenceContext, viewTemplateConference);
            }
        }
        // 上级级联
        String upCascadeConferenceId = EncryptIdUtil.generateConferenceId(tc.getUpCascadeId(), tc.getUpCascadeMcuType());
        conferenceContext.setUpCascadeConferenceId(upCascadeConferenceId);
        conferenceContext.setUpCascadeIndex(tc.getUpCascadeIndex());
    }

    /**
     * <pre>绑定级联的MCU参会信息</pre>
     * @author lilinhai
     * @since 2021-03-23 18:05
     * @param conferenceContext
     * @param viewTemplateConference void
     */
    private void bindCascadeConference(Smc2ConferenceContext conferenceContext, ViewTemplateConference viewTemplateConference)
    {
        McuAttendeeSmc2 mcuAttendee = new McuAttendeeSmc2();
        mcuAttendee.setDeptId(conferenceContext.getDeptId());
        mcuAttendee.setDeptName(SysDeptCache.getInstance().get(conferenceContext.getDeptId()).getDeptName());
        mcuAttendee.setName(viewTemplateConference.getName());
        mcuAttendee.setWeight(0);
        mcuAttendee.setId(viewTemplateConference.getConferenceId());
        mcuAttendee.setUpCascadeConferenceId(viewTemplateConference.getUpCascadeConferenceId());
        mcuAttendee.setUpCascadeIndex(viewTemplateConference.getUpCascadeIndex());
        mcuAttendee.setCascadeConferenceId(viewTemplateConference.getConferenceId());
        mcuAttendee.setCascadeMcuType(viewTemplateConference.getMcuType());
        mcuAttendee.setCascadeTemplateId(viewTemplateConference.getId());
        String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendee.getId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        if (baseConferenceContext != null) {
            String remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
            if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                remoteParty += ":" + baseConferenceContext.getMcuCallPort();
            }
            mcuAttendee.setRemoteParty(remoteParty);
            mcuAttendee.setIp(baseConferenceContext.getMcuCallIp());
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        } else {
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        }
        mcuAttendee.setCascadeTemplateId(viewTemplateConference.getId());

        mcuAttendee.setCascadeDeptId(viewTemplateConference.getDeptId());
        mcuAttendee.setCascadeDeptName(SysDeptCache.getInstance().get(viewTemplateConference.getDeptId()).getDeptName());
        if (viewTemplateConference.getConferenceNumber() != null) {
            mcuAttendee.setCascadeConferenceNumber(viewTemplateConference.getConferenceNumber().toString());
        }
        conferenceContext.addMcuAttendee(mcuAttendee);
    }

}
