/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BuildTemplateConferenceContext.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy
 * @since 2021-09-22 21:17
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.zj.conference.model.templateconference;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperationForGuest;
import com.paradisecloud.fcm.mcu.zj.attendee.utils.AttendeeUtils;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.cache.model.DeptWeight;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.InvitedAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.McuAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.TerminalAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IDefaultAttendeeOperationPackageForMcuZjService;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class BuildTemplateConferenceContext
{

    public static final String A = "自动直播";

    /**
     * <pre>根据模板ID构建会议上下文对象</pre>
     * @author lilinhai
     * @since 2021-02-03 13:14
     * @param templateConferenceId
     * @return ConferenceContext
     */
    public McuZjConferenceContext buildTemplateConferenceContext(long templateConferenceId)
    {
        // 获取模板会议实体对象
        BusiMcuZjTemplateConference tc = BeanFactory.getBean(BusiMcuZjTemplateConferenceMapper.class).selectBusiMcuZjTemplateConferenceById(templateConferenceId);
        BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
        if (tc == null)
        {
            return null;
        }

        McuZjConferenceContext conferenceContext = null;
        if (tc.getConferenceNumber() != null)
        {
            // 缓存中还有会议上下文，则证明会议正在进行，直接返回给前端
            conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_ZJ));
            if (conferenceContext != null)
            {
                // 简单级联
                bindCascadeConference(conferenceContext, tc);
                return conferenceContext;
            }
        }

        // 会议主导方的会议桥对象，这一步是进行验证，若是验证不成功，则抛异常
        McuZjBridge conferenceMainMcuBridge = null;
        try {
            conferenceMainMcuBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(tc.getDeptId()).getMasterMcuZjBridge();
        } catch (Exception e) {
        }

        if (conferenceMainMcuBridge == null) {
            return null;
        }

        // 会议上下文对象
        conferenceContext = new McuZjConferenceContext(conferenceMainMcuBridge);

        // 拷贝模板参数到会议上下文
        copyTemplateAttrs(conferenceContext, tc);

        if (tc.getResourceTemplateId() != null) {
            SourceTemplate sourceTemplate = conferenceMainMcuBridge.getSourceTemplateById(tc.getResourceTemplateId());
            if (sourceTemplate != null) {
                conferenceContext.setSupportRollCall(sourceTemplate.isSupportRollCall());
                conferenceContext.setSupportSplitScreen(sourceTemplate.isSupportSplitScreen());
                conferenceContext.setSupportPolling(sourceTemplate.isSupportPolling());
                conferenceContext.setSupportChooseSee(sourceTemplate.isSupportChooseSee());
                conferenceContext.setSupportTalk(sourceTemplate.isSupportTalk());
                conferenceContext.setSupportBroadcast(sourceTemplate.isSupportBroadcast());
                boolean singleView = false;
                if (sourceTemplate.getSingle_view() != null && sourceTemplate.getSingle_view() == 1) {
                    singleView = true;
                }
                conferenceContext.setSingleView(singleView);
                conferenceContext.setSpeakerSplitScreenList(sourceTemplate.getSpeakerSplitScreenList());
                conferenceContext.setGuestSplitScreenList(sourceTemplate.getGuestSplitScreenList());
            }
        }

        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));

        if (tc.getConferenceNumber() != null)
        {
            // 设置MCU参会的uri
            String conferenceRemoteParty = conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }

        BusiMcuZjTemplateParticipant condition = new BusiMcuZjTemplateParticipant();
        condition.setTemplateConferenceId(templateConferenceId);
        List<BusiMcuZjTemplateParticipant> allParticipants = BeanFactory.getBean(BusiMcuZjTemplateParticipantMapper.class).selectBusiMcuZjTemplateParticipantList(condition);

        // 创建部门-与会者列表映射
        Map<Long, List<BusiMcuZjTemplateParticipant>> deptPsMap = new HashMap<>();
        Map<Long, BusiMcuZjTemplateParticipant> busiTemplateParticipantMap = new HashMap<>();
        BusiMcuZjTemplateParticipant templateMasterParticipant = null;
        for (BusiMcuZjTemplateParticipant busiTemplateParticipant : allParticipants)
        {
            BusiTerminal terminal = TerminalCache.getInstance().get(busiTemplateParticipant.getTerminalId());
            if (AttendType.convert(busiTemplateParticipant.getAttendType()).isJoin())
            {
                if (tc.getMasterParticipantId() != null && busiTemplateParticipant.getId().longValue() == tc.getMasterParticipantId().longValue())
                {
                    templateMasterParticipant = busiTemplateParticipant;
                }
                List<BusiMcuZjTemplateParticipant> deptPs = deptPsMap.get(terminal.getDeptId());
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
                TerminalAttendeeForMcuZj ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addLiveTerminal(ta);;
            }
        }

        // 先拉会议发起方的与会者
        List<BusiMcuZjTemplateParticipant> ps = deptPsMap.get(tc.getDeptId());
        if (ps != null)
        {
            for (BusiMcuZjTemplateParticipant busiTemplateParticipant : ps)
            {
                TerminalAttendeeForMcuZj ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addAttendee(ta);
            }

            // 根据显示顺序排序
            Collections.sort(conferenceContext.getAttendees());
        }

        BusiMcuZjTemplateDept busiTemplateDeptCondition = new BusiMcuZjTemplateDept();
        busiTemplateDeptCondition.setTemplateConferenceId(tc.getId());
        List<BusiMcuZjTemplateDept> busiTemplateDepts = BeanFactory.getBean(BusiMcuZjTemplateDeptMapper.class).selectBusiMcuZjTemplateDeptList(busiTemplateDeptCondition);
        if (!ObjectUtils.isEmpty(busiTemplateDepts))
        {
            // 添加所有子会议启动器
            buildMainConferenceContext(deptPsMap, busiTemplateDepts, conferenceContext);

            // 排序部门
            Collections.sort(conferenceContext.getDeptWeights());

            // 排序MCU参会者
            Collections.sort(conferenceContext.getMcuAttendees());

            // 对每个子会议的参会者进行排序
            conferenceContext.getCascadeAttendeesMap().forEach((k, v) -> {
                Collections.sort(v);
            });
        }

        // 设置主会场
        if (templateMasterParticipant != null)
        {
            AttendeeForMcuZj masterAttendee = conferenceContext.getAttendeeById(templateMasterParticipant.getUuid());
            conferenceContext.setMasterAttendee(masterAttendee);
        }

        // 默认参会者操作解析设置
        DefaultAttendeeOperation defaultAttendeeOperation = BeanFactory.getBean(IDefaultAttendeeOperationPackageForMcuZjService.class).packing(conferenceContext, tc, busiTemplateParticipantMap);
        DefaultAttendeeOperationForGuest defaultAttendeeOperationForGuest = BeanFactory.getBean(IDefaultAttendeeOperationPackageForMcuZjService.class).packingForGuest(conferenceContext, tc, busiTemplateParticipantMap);
        conferenceContext.setDefaultViewOperation(defaultAttendeeOperation);
        conferenceContext.setDefaultViewOperationForGuest(defaultAttendeeOperationForGuest);

        BusiMcuZjConferenceAppointment con = new BusiMcuZjConferenceAppointment();
        con.setTemplateId(templateConferenceId);
        con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
        List<BusiMcuZjConferenceAppointment> cas = BeanFactory.getBean(BusiMcuZjConferenceAppointmentMapper.class).selectBusiMcuZjConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas)) {
            conferenceContext.setAppointment(true);
            conferenceContext.setAppointmentType(cas.get(0).getType());
            conferenceContext.setConferenceAppointment(cas.get(0));

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


    /**
     * <pre>添加所有子会议启动器</pre>
     * @author lilinhai
     * @since 2021-02-01 15:03
     */
    private void buildMainConferenceContext(Map<Long, List<BusiMcuZjTemplateParticipant>> deptPsMap, List<BusiMcuZjTemplateDept> busiTemplateDepts, McuZjConferenceContext conferenceContext)
    {
        List<BusiMcuZjTemplateParticipant> ps;

        // 倒叙排列，按此顺序进行先后子会议的呼叫发起
        Collections.sort(busiTemplateDepts, new Comparator<BusiMcuZjTemplateDept>()
        {
            public int compare(BusiMcuZjTemplateDept o1, BusiMcuZjTemplateDept o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });

        for (BusiMcuZjTemplateDept busiTemplateDept : busiTemplateDepts)
        {
            // 先拉会议发起方的与会者
            ps = deptPsMap.get(busiTemplateDept.getDeptId());
            if (ps == null)
            {
                continue;
            }

//            boolean isBindSameMcu = DeptMcuZjMappingCache.getInstance().isBindSameMcu(conferenceContext.getDeptId(), busiTemplateDept.getDeptId());
//
//            // 如果相等，则说明是两个部门绑定的是同一个MCU或MCU集群
//            if (!isBindSameMcu)
//            {
//                // 是否存在绑定MCU集群的部门
//                if (DeptMcuZjMappingCache.getInstance().isExsitMcuClusterBind(conferenceContext.getDeptId(), busiTemplateDept.getDeptId()))
//                {
//                    throw new SystemException(1006756, "级联会议不支持在集群的MCU中使用！");
//                }
//
//                // 绑定级联的MCU参会信息
//                bindCascadeMcuAttendee(conferenceContext, busiTemplateDept);
//            }

            BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
            DeptWeight dw = new DeptWeight();
            dw.setDeptId(busiTemplateDept.getDeptId());
            dw.setWeight(busiTemplateDept.getWeight());
            conferenceContext.addDeptWeight(dw);
            for (BusiMcuZjTemplateParticipant busiTemplateParticipant : ps)
            {
                TerminalAttendeeForMcuZj ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }

                // 封装级联与会者属性并添加到集合
                conferenceContext.addCascadeAttendee(ta);

                // 如果绑定的MCU是同一个，则会议号用跟主会议同一个号
//                if (isBindSameMcu)
                {
                    ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                    conferenceContext.addAttendeeToIdMap(ta);
                    conferenceContext.addAttendeeToRemotePartyMap(ta);
                }
            }

            // 排序级联子会议终端
            Collections.sort(conferenceContext.getCascadeAttendeesMap().get(busiTemplateDept.getDeptId()));
            if (StringUtils.isNotEmpty(conferenceContext.getStreamingRemoteParty())) {
                InvitedAttendeeForMcuZj ia = new InvitedAttendeeForMcuZj();
                ia.setConferenceNumber(conferenceContext.getConferenceNumber());
                ia.setId(java.util.UUID.randomUUID().toString());
                ia.setName(conferenceContext.getStreamingName());
                ia.setRemoteParty(conferenceContext.getStreamingRemoteParty());
                ia.setWeight(1);
                ia.setDeptId(conferenceContext.getDeptId());
                if (ia.getRemoteParty().contains("@"))
                {
                    ia.setIp(ia.getRemoteParty().split("@")[1]);
                }
                else
                {
                    ia.setIp(ia.getRemoteParty());
                }
                ia.setCallType(2);
                conferenceContext.setStreamingAttendee(ia);
            }

            AttendeeForMcuZj m = conferenceContext.getCascadeAttendeesMap().get(busiTemplateDept.getDeptId()).remove(0);

            // 添加地州主会场终端到主导方集合中，同时删除级联子会议的主会场
            conferenceContext.addMasterAttendee(m);
        }
    }

    /**
     * <pre>绑定级联的MCU参会信息</pre>
     * @author lilinhai
     * @since 2021-03-23 18:05
     * @param conferenceContext
     * @param busiMcuZjTemplateDept void
     */
    private void bindCascadeMcuAttendee(McuZjConferenceContext conferenceContext, BusiMcuZjTemplateDept busiMcuZjTemplateDept)
    {
        BusiConferenceNumber busiConferenceNumberCondition = new BusiConferenceNumber();
        busiConferenceNumberCondition.setDeptId(busiMcuZjTemplateDept.getDeptId());
        busiConferenceNumberCondition.setType(ConferenceType.CASCADE.getValue());
        List<BusiConferenceNumber> cns = BeanFactory.getBean(BusiConferenceNumberMapper.class).selectBusiConferenceNumberList(busiConferenceNumberCondition);
        if (ObjectUtils.isEmpty(cns))
        {
            throw new SystemException(1002334, "会议发起失败，未配置【" + SysDeptCache.getInstance().get(busiMcuZjTemplateDept.getDeptId()).getDeptName() + "】的集群会议号！");
        }

        Collections.sort(cns, new Comparator<BusiConferenceNumber>()
        {
            public int compare(BusiConferenceNumber o1, BusiConferenceNumber o2)
            {
                return o2.getId().compareTo(o1.getId());
            }
        });

        BusiConferenceNumber conferenceNumber = cns.get(0);

        // 获取会议桥工具对象
        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjTemplateDept.getDeptId()).getMcuZjBridges().get(0);
        McuAttendeeForMcuZj mcuAttendee = new McuAttendeeForMcuZj();
        mcuAttendee.setName(SysDeptCache.getInstance().get(busiMcuZjTemplateDept.getDeptId()).getDeptName());
        mcuAttendee.setWeight(busiMcuZjTemplateDept.getWeight());
        mcuAttendee.setId(busiMcuZjTemplateDept.getUuid());
        if (mcuZjBridge != null)
        {
            mcuAttendee.setIp(mcuZjBridge.getAttendeeIp());
            mcuAttendee.setCascadeMcuId(mcuZjBridge.getBusiMcuZj().getId());
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        }
        else
        {
            mcuAttendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        }

        mcuAttendee.setCascadeDeptId(busiMcuZjTemplateDept.getDeptId());
        mcuAttendee.setCascadeConferenceNumber(conferenceNumber.getId().toString());
        conferenceContext.addMcuAttendee(mcuAttendee);
    }

    /**
     * <pre>绑定级联的MCU参会信息</pre>
     * @author lilinhai
     * @since 2021-03-23 18:05
     * @param conferenceContext
     */
    private void bindCascadeConference(McuZjConferenceContext conferenceContext, BusiMcuZjTemplateConference tc) {
        Map<String, ViewTemplateConference> newConferenceIdMap = new HashMap<>();
        Map<String, McuAttendeeForMcuZj> oldConferenceIdMap = new HashMap<>();
        List<McuAttendeeForMcuZj> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeForMcuZj attendee : mcuAttendees) {
            if (StringUtils.isNotEmpty(attendee.getId())) {
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
    private void bindCascadeConference(McuZjConferenceContext conferenceContext, ViewTemplateConference viewTemplateConference)
    {
        McuAttendeeForMcuZj mcuAttendee = new McuAttendeeForMcuZj();
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
        mcuAttendee.setTerminalId(viewTemplateConference.getId());

        mcuAttendee.setCascadeDeptId(viewTemplateConference.getDeptId());
        mcuAttendee.setCascadeDeptName(SysDeptCache.getInstance().get(viewTemplateConference.getDeptId()).getDeptName());
        if (viewTemplateConference.getConferenceNumber() != null) {
            mcuAttendee.setCascadeConferenceNumber(viewTemplateConference.getConferenceNumber().toString());
        }
        conferenceContext.addMcuAttendee(mcuAttendee);
    }

    /**
     * <pre>拷贝模板参数到会议上下文</pre>
     * @author lilinhai
     * @since 2021-02-02 15:42
     * @param conferenceContext
     * @param tc void
     */
    private void copyTemplateAttrs(McuZjConferenceContext conferenceContext, BusiMcuZjTemplateConference tc)
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
        if (!ObjectUtils.isEmpty(tc.getBusinessProperties()))
        {
            conferenceContext.setBusinessProperties(tc.getBusinessProperties());
        }

        // 默认视图设置
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
        if (StringUtils.isNotEmpty(tc.getStreamUrl())) {
            BusiLiveSettingMapper busiLiveSettingMapper = BeanFactory.getBean(BusiLiveSettingMapper.class);
            BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
            busiLiveSettingCon.setUrl(tc.getStreamUrl());
            List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
            if (busiLiveSettingList.size() > 0) {
                BusiLiveSetting busiLiveSetting = busiLiveSettingList.get(0);
                if (StringUtils.isNotEmpty(busiLiveSetting.getRemoteParty())) {
                    conferenceContext.setStreamingRemoteParty(busiLiveSetting.getRemoteParty());
                }
                conferenceContext.setStreamingName(busiLiveSetting.getName());
            }
        }

        conferenceContext.setDurationEnabled(tc.getDurationEnabled());
        conferenceContext.setDurationTime(tc.getDurationTime());
        conferenceContext.setConferenceCtrlPassword(tc.getConferenceCtrlPassword());
        String supervisorPassword = generateSupervisorPassword(tc.getConferenceCtrlPassword());
        conferenceContext.setSupervisorPassword(supervisorPassword);
        conferenceContext.setTenantId(tc.getTenantId());
        conferenceContext.setResourceTemplateId(tc.getResourceTemplateId());
        conferenceContext.setTemplateCreateTime(tc.getCreateTime());
        conferenceContext.setIsAutoCreateStreamUrl(tc.getIsAutoCreateStreamUrl());
        conferenceContext.setCreateUserId(tc.getCreateUserId());
        conferenceContext.setPresenter(tc.getPresenter());
        if (conferenceContext.getPresenter() == null) {
            conferenceContext.setPresenter(0l);
        }
        conferenceContext.setMuteType(tc.getMuteType());
        String upCascadeConferenceId = EncryptIdUtil.generateConferenceId(tc.getUpCascadeId(), tc.getUpCascadeMcuType());
        conferenceContext.setUpCascadeConferenceId(upCascadeConferenceId);
        conferenceContext.setUpCascadeIndex(tc.getUpCascadeIndex());
    }

    private String generateSupervisorPassword(String controlPassword) {
        String password = "";
        if (StringUtils.isNotEmpty(controlPassword)) {
            password += "6";
            String passwordA = "";
            for (int i = 0; i < controlPassword.length(); i++) {
                String pwdS = controlPassword.substring(i, i + 1);
                try {
                    passwordA += (9 - Integer.valueOf(pwdS));
                } catch (Exception e) {
                    break;
                }
            }
            if (passwordA.length() < controlPassword.length()) {
                passwordA = "6666";
            }
            password += passwordA;
        }
        return password;
    }

}
