/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BuildTemplateConferenceContext.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy
 * @since 2021-09-22 21:17
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.conference.model.pm.templateconference;

import java.lang.reflect.Method;
import java.util.*;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.model.MinutesParam;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.model.busi.attendee.McuAttendee;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.live.LiveService;
import com.paradisecloud.fcm.service.live.LiveValue;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.fme.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.fme.attendee.utils.AttendeeUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IDefaultAttendeeOperationPackageService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.model.busi.core.DeptWeight;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;

public class BuildTemplateConferenceContext extends ProxyMethod
{

    public static final String A = "自动直播";

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-09-22 21:17
     * @param method
     */
    public BuildTemplateConferenceContext(Method method)
    {
        super(method);
    }

    /**
     * <pre>根据模板ID构建会议上下文对象</pre>
     * @author lilinhai
     * @since 2021-02-03 13:14
     * @param templateConferenceId
     * @return ConferenceContext
     */
    public ConferenceContext buildTemplateConferenceContext(long templateConferenceId)
    {
        // 获取模板会议实体对象
        BusiTemplateConference tc = BeanFactory.getBean(BusiTemplateConferenceMapper.class).selectBusiTemplateConferenceById(templateConferenceId);
        BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
        if (tc == null)
        {
            return null;
        }

        FmeBridgeCache.getInstance().getFmeBridgesByDept(tc.getDeptId());
        ConferenceContext conferenceContext = null;
        if (tc.getConferenceNumber() != null)
        {
            // 缓存中还有会议上下文，则证明会议正在进行，直接返回给前端
            conferenceContext = ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.FME));
            if (conferenceContext != null)
            {
                // 简单级联
                bindCascadeConference(conferenceContext, tc);
                return conferenceContext;
            }
        }

        // 会议上下文对象
        conferenceContext = new ConferenceContext();

        // 拷贝模板参数到会议上下文
        copyTemplateAttrs(conferenceContext, tc);

        // 会议室的id
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));

        if (tc.getConferenceNumber() != null)
        {
            // 会议主导方的会议桥对象，这一步是进行验证，若是验证不成功，则抛异常
            FmeBridge conferenceMainFmeBridge = BridgeUtils.getFmeBridgeByDeptIdAndConferenceNumber(tc.getDeptId(), tc.getConferenceNumber().toString(), true);

            // 设置FME参会的uri
            conferenceContext.setFmeAttendeeRemoteParty(conferenceMainFmeBridge.getAttendeeIp());

            conferenceContext.setMcuBridge(conferenceMainFmeBridge);
            String conferenceRemoteParty = conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }

        BusiTemplateParticipant condition = new BusiTemplateParticipant();
        condition.setTemplateConferenceId(templateConferenceId);
        List<BusiTemplateParticipant> allParticipants = BeanFactory.getBean(BusiTemplateParticipantMapper.class).selectBusiTemplateParticipantList(condition);

        // 创建部门-与会者列表映射
        Map<Long, List<BusiTemplateParticipant>> deptPsMap = new HashMap<>();
        Map<Long, BusiTemplateParticipant> busiTemplateParticipantMap = new HashMap<>();
        BusiTemplateParticipant templateMasterParticipant = null;
        for (BusiTemplateParticipant busiTemplateParticipant : allParticipants)
        {
            BusiTerminal terminal = TerminalCache.getInstance().get(busiTemplateParticipant.getTerminalId());
            if (AttendType.convert(busiTemplateParticipant.getAttendType()).isJoin())
            {
                if (tc.getMasterParticipantId() != null && busiTemplateParticipant.getId().longValue() == tc.getMasterParticipantId().longValue())
                {
                    templateMasterParticipant = busiTemplateParticipant;
                }
                List<BusiTemplateParticipant> deptPs = deptPsMap.get(terminal.getDeptId());
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
                TerminalAttendee ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addLiveTerminal(ta);
            }
        }

        // 先拉会议发起方的与会者
        List<BusiTemplateParticipant> ps = deptPsMap.get(tc.getDeptId());
        if (ps != null)
        {
            for (BusiTemplateParticipant busiTemplateParticipant : ps)
            {
                TerminalAttendee ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addAttendee(ta);
            }

            // 根据显示顺序排序
            Collections.sort(conferenceContext.getAttendees());
        }

        BusiTemplateDept busiTemplateDeptCondition = new BusiTemplateDept();
        busiTemplateDeptCondition.setTemplateConferenceId(tc.getId());
        List<BusiTemplateDept> busiTemplateDepts = BeanFactory.getBean(BusiTemplateDeptMapper.class).selectBusiTemplateDeptList(busiTemplateDeptCondition);
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
            Attendee masterAttendee = conferenceContext.getAttendeeById(templateMasterParticipant.getUuid());
            conferenceContext.setMasterAttendee(masterAttendee);
        }

        // 默认参会者操作解析设置
        DefaultAttendeeOperation defaultAttendeeOperation = BeanFactory.getBean(IDefaultAttendeeOperationPackageService.class).packing(conferenceContext, tc, busiTemplateParticipantMap);
        conferenceContext.setDefaultViewOperation(defaultAttendeeOperation);

        BusiConferenceAppointment con = new BusiConferenceAppointment();
        con.setTemplateId(templateConferenceId);
        con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
        List<BusiConferenceAppointment> cas = BeanFactory.getBean(BusiConferenceAppointmentMapper.class).selectBusiConferenceAppointmentList(con);
        if (!ObjectUtils.isEmpty(cas)) {
            conferenceContext.setAppointment(true);
            conferenceContext.setAppointmentType(cas.get(0).getType());
            BusiConferenceAppointment busiConferenceAppointment = cas.get(0);
            conferenceContext.setConferenceAppointment(busiConferenceAppointment);
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
     * @param deptPsMap
     * @param busiTemplateDepts
     */
    private void buildMainConferenceContext(Map<Long, List<BusiTemplateParticipant>> deptPsMap, List<BusiTemplateDept> busiTemplateDepts, ConferenceContext conferenceContext)
    {
        List<BusiTemplateParticipant> ps;

        // 倒叙排列，按此顺序进行先后子会议的呼叫发起
        Collections.sort(busiTemplateDepts, new Comparator<BusiTemplateDept>()
        {
            public int compare(BusiTemplateDept o1, BusiTemplateDept o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });

        for (BusiTemplateDept busiTemplateDept : busiTemplateDepts)
        {
            // 先拉会议发起方的与会者
            ps = deptPsMap.get(busiTemplateDept.getDeptId());
            if (ps == null)
            {
                continue;
            }

//            boolean isBindSameFme = DeptFmeMappingCache.getInstance().isBindSameFme(conferenceContext.getDeptId(), busiTemplateDept.getDeptId());
//
//            // 如果相等，则说明是两个部门绑定的是同一个FME或FME集群
//            if (!isBindSameFme)
//            {
//                // 是否存在绑定FME集群的部门
//                if (DeptFmeMappingCache.getInstance().isExsitFmeClusterBind(conferenceContext.getDeptId(), busiTemplateDept.getDeptId()))
//                {
//                    throw new SystemException(1006756, "级联会议不支持在集群的FME中使用！");
//                }
//
//                // 绑定级联的FME参会信息
//                bindCascadeFmeAttendee(conferenceContext, busiTemplateDept);
//            }

            BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
            DeptWeight dw = new DeptWeight();
            dw.setDeptId(busiTemplateDept.getDeptId());
            dw.setWeight(busiTemplateDept.getWeight());
            conferenceContext.addDeptWeight(dw);
            for (BusiTemplateParticipant busiTemplateParticipant : ps)
            {
                TerminalAttendee ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }

                // 封装级联与会者属性并添加到集合
                conferenceContext.addCascadeAttendee(ta);

                // 如果绑定的FME是同一个，则会议号用跟主会议同一个号
//                if (isBindSameFme)
                {
                    ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                    conferenceContext.addAttendeeToIdMap(ta);
                    conferenceContext.addAttendeeToRemotePartyMap(ta);
                }
            }

            // 排序级联子会议终端
            Collections.sort(conferenceContext.getCascadeAttendeesMap().get(busiTemplateDept.getDeptId()));

            Attendee m = conferenceContext.getCascadeAttendeesMap().get(busiTemplateDept.getDeptId()).remove(0);

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
    private void bindCascadeFmeAttendee(ConferenceContext conferenceContext, BusiTemplateDept busiTemplateDept)
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
            public int compare(BusiConferenceNumber o1, BusiConferenceNumber o2)
            {
                return o2.getId().compareTo(o1.getId());
            }
        });

        BusiConferenceNumber conferenceNumber = cns.get(0);

        // 获取会议桥工具对象
        FmeBridge fmeBridge = BridgeUtils.getFmeBridgeByDeptIdAndConferenceNumber(busiTemplateDept.getDeptId(), conferenceNumber.getId().toString(), false);
        FmeAttendee fmeAttendee = new FmeAttendee();
        fmeAttendee.setName(SysDeptCache.getInstance().get(busiTemplateDept.getDeptId()).getDeptName());
        fmeAttendee.setWeight(busiTemplateDept.getWeight());
        fmeAttendee.setId(busiTemplateDept.getUuid());
        if (fmeBridge != null)
        {
            fmeAttendee.setIp(fmeBridge.getAttendeeIp());
            fmeAttendee.setFmeId(fmeBridge.getBusiFme().getId());
            fmeAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        }
        else
        {
            fmeAttendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
        }

        fmeAttendee.setCascadeDeptId(busiTemplateDept.getDeptId());
        fmeAttendee.setCascadeConferenceNumber(conferenceNumber.getId().toString());
        conferenceContext.addFmeAttendee(fmeAttendee);
    }

    /**
     * <pre>绑定级联的MCU参会信息</pre>
     * @author lilinhai
     * @since 2021-03-23 18:05
     * @param conferenceContext
     */
    private void bindCascadeConference(ConferenceContext conferenceContext, BusiTemplateConference tc) {
        Map<String, ViewTemplateConference> newConferenceIdMap = new HashMap<>();
        Map<String, McuAttendee> oldConferenceIdMap = new HashMap<>();
        List<McuAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendee attendee : mcuAttendees) {
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
        BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuTencentConferenceAppointmentMapper.class);
        BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper = BeanFactory.getBean(BusiMcuHwcloudConferenceAppointmentMapper.class);
        for (ViewTemplateConference viewTemplateConference : viewTemplateConferenceList) {
            boolean needAdd = true;
            if (McuType.MCU_TENCENT.getCode().equals(viewTemplateConference.getMcuType())) {
                List<BusiMcuTencentConferenceAppointment> busiMcuTencentConferenceAppointmentList = busiMcuTencentConferenceAppointmentMapper.selectBusiMcuTencentConferenceAppointmentByTemplateId(viewTemplateConference.getId());
                if (busiMcuTencentConferenceAppointmentList.size() > 0) {
                    BusiMcuTencentConferenceAppointment busiMcuTencentConferenceAppointment = busiMcuTencentConferenceAppointmentList.get(0);
                    if (busiMcuTencentConferenceAppointment.getIsCloudConference() != null && busiMcuTencentConferenceAppointment.getIsCloudConference() == 1) {
                        needAdd = false;
                    }
                }
            } else if (McuType.MCU_HWCLOUD.getCode().equals(viewTemplateConference.getMcuType())) {
                List<BusiMcuHwcloudConferenceAppointment> busiMcuHwcloudConferenceAppointmentList = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentByTemplateId(viewTemplateConference.getId());
                if (busiMcuHwcloudConferenceAppointmentList.size() > 0) {
                    BusiMcuHwcloudConferenceAppointment busiMcuHwcloudConferenceAppointment = busiMcuHwcloudConferenceAppointmentList.get(0);
                    if (busiMcuHwcloudConferenceAppointment.getIsCloudConference() != null && busiMcuHwcloudConferenceAppointment.getIsCloudConference() == 1) {
                        needAdd = false;
                    }
                }
            }
            if (needAdd) {
                newConferenceIdMap.put(viewTemplateConference.getConferenceId(), viewTemplateConference);
            }
        }
        for (String conferenceIdTemp : oldConferenceIdMap.keySet()) {
            if (!newConferenceIdMap.containsKey(conferenceIdTemp)) {
                conferenceContext.removeMcuAttendee(oldConferenceIdMap.get(conferenceIdTemp));
            }
        }
        for (ViewTemplateConference viewTemplateConference : newConferenceIdMap.values()) {
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
    private void bindCascadeConference(ConferenceContext conferenceContext, ViewTemplateConference viewTemplateConference)
    {
        McuAttendee mcuAttendee = new McuAttendee();
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

    /**
     * <pre>拷贝模板参数到会议上下文</pre>
     * @author lilinhai
     * @since 2021-02-02 15:42
     * @param conferenceContext
     * @param tc void
     */
    private void copyTemplateAttrs(ConferenceContext conferenceContext, BusiTemplateConference tc)
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
        } else {
            conferenceContext.setRecordingEnabled(YesOrNo.NO.getValue());
        }

        // 直播开关
        StreamingEnabledType streamingEnabledType = StreamingEnabledType.convert(tc.getStreamingEnabled());
        conferenceContext.setStreamingEnabled(streamingEnabledType.getValue());
        if (streamingEnabledType == StreamingEnabledType.CLOUDS) {
            if (StringUtils.isNotEmpty(tc.getStreamUrl())) {
                LiveService liveService = BeanFactory.getBean(LiveService.class);
                String streamingUrl = tc.getStreamUrl();
                LiveValue liveValue = liveService.generateUrlFromPushUrl(streamingUrl);
                conferenceContext.setStreamingUrl(liveValue.getPullUrl());
                conferenceContext.setCloudsStreamingUrl(streamingUrl);
                conferenceContext.addStreamUrlList(liveValue.getPullUrlList());
            }
        } else {
            conferenceContext.setStreamingUrl(tc.getStreamUrl());
        }
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
        conferenceContext.setIsAutoCreateStreamUrl(tc.getIsAutoCreateStreamUrl());
        conferenceContext.setPresenter(tc.getPresenter());
        conferenceContext.setCreateUserId(tc.getCreateUserId());
        conferenceContext.setCreateUserName(tc.getCreateUserName());
        String upCascadeConferenceId = EncryptIdUtil.generateConferenceId(tc.getUpCascadeId(), tc.getUpCascadeMcuType());
        conferenceContext.setUpCascadeConferenceId(upCascadeConferenceId);
        conferenceContext.setUpCascadeIndex(tc.getUpCascadeIndex());
        conferenceContext.setConferenceMode(tc.getConferenceMode());
        conferenceContext.setMinutesEnabled(tc.getMinutesEnabled());
        MinutesParam minutesParam = new MinutesParam();
        conferenceContext.setMinutesParam(minutesParam);
    }
}
