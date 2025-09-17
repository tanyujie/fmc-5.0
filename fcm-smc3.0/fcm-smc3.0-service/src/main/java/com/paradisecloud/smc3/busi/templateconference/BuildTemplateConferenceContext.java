/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BuildTemplateConferenceContext.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy
 * @since 2021-09-22 21:17
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.templateconference;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.smc3.busi.ConferenceNode;
import com.paradisecloud.smc3.busi.DefaultAttendeeOperation;
import com.paradisecloud.smc3.busi.DeptWeight;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.TerminalAttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.utils.AttendeeUtils;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantRspDto;
import com.paradisecloud.smc3.model.SmcConferenceTemplate;
import com.paradisecloud.smc3.model.request.TemplateNode;
import com.paradisecloud.smc3.model.request.TemplateNodeTemp;
import com.paradisecloud.smc3.model.response.SmcErrorResponse;
import com.paradisecloud.smc3.service.interfaces.IDefaultAttendeeSmc3OperationPackageService;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static com.paradisecloud.smc3.model.ConstAPI.SVC_VIDEO_RESOLUTION;
import static com.paradisecloud.smc3.model.ConstAPI.VIDEO_RESOLUTION;

public class BuildTemplateConferenceContext
{


    /**
     * <pre>根据模板ID构建会议上下文对象</pre>
     * @author lilinhai
     * @since 2021-02-03 13:14
     * @param templateConferenceId
     * @return ConferenceContext
     */
    public Smc3ConferenceContext buildTemplateConferenceContext(Long templateConferenceId)
    {
        // 获取模板会议实体对象
        BusiMcuSmc3TemplateConference tc = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).selectBusiMcuSmc3TemplateConferenceById(templateConferenceId);
        BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
        if (tc == null)
        {
            throw new CustomException("模版不存在");
        }

        Smc3ConferenceContext conferenceContext = null;
        // 缓存中还有会议上下文，则证明会议正在进行，直接返回给前端
        conferenceContext = Smc3ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.SMC3));
        if (conferenceContext != null)
        {
            // 简单级联
            bindCascadeConference(conferenceContext, tc);
            return conferenceContext;
        }
        Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(tc.getDeptId());
        if(bridgesByDept==null){
            throw new CustomException("未找到MCU");
        }
        tc.setTenantId(bridgesByDept.getTenantId());
        // 会议上下文对象
        conferenceContext = new Smc3ConferenceContext(bridgesByDept);


        // 拷贝模板参数到会议上下文
        copyTemplateAttrs(conferenceContext, tc);

        if (tc.getConferenceNumber() != null)
        {
            // 设置FME参会的uri
            conferenceContext.setFmeAttendeeRemoteParty(bridgesByDept.getRootUrl());
        }
        // 会议室的id
        conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));

        if(conferenceContext.getConferenceNumber()!=null){
            String conferenceRemoteParty = conferenceContext.getTenantId()==null?"":conferenceContext.getTenantId() + conferenceContext.getConferenceNumber() + "@" + conferenceContext.getMcuCallIp();
            if (conferenceContext.getMcuCallPort() != null && conferenceContext.getMcuCallPort() != 5060) {
                conferenceRemoteParty += ":" + conferenceContext.getMcuCallPort();
            }
            conferenceContext.setConferenceRemoteParty(conferenceRemoteParty);
        }

        BusiMcuSmc3TemplateParticipant condition = new BusiMcuSmc3TemplateParticipant();
        condition.setTemplateConferenceId(templateConferenceId);
        List<BusiMcuSmc3TemplateParticipant> allParticipants = BeanFactory.getBean(BusiMcuSmc3TemplateParticipantMapper.class).selectBusiMcuSmc3TemplateParticipantList(condition);

        // 创建部门-与会者列表映射
        Map<Long, List<BusiMcuSmc3TemplateParticipant>> deptPsMap = new HashMap<>();
        Map<Long, BusiMcuSmc3TemplateParticipant> busiTemplateParticipantMap = new HashMap<>();
        BusiMcuSmc3TemplateParticipant templateMasterParticipant = null;
        for (BusiMcuSmc3TemplateParticipant busiTemplateParticipant : allParticipants)
        {
            BusiTerminal terminal = TerminalCache.getInstance().get(busiTemplateParticipant.getTerminalId());
            if(terminal==null){
                 terminal = BeanFactory.getBean(IBusiTerminalService.class).selectBusiTerminalById(busiTemplateParticipant.getTerminalId());
            }

            if (AttendType.convert(busiTemplateParticipant.getAttendType()).isJoin())
            {
                if (tc.getMasterParticipantId() != null && busiTemplateParticipant.getId().longValue() == tc.getMasterParticipantId().longValue())
                {
                    templateMasterParticipant = busiTemplateParticipant;
                }
                List<BusiMcuSmc3TemplateParticipant> deptPs = deptPsMap.get(terminal.getDeptId());
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
                TerminalAttendeeSmc3 ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addLiveTerminal(ta);
            }
        }

        // 先拉会议发起方的与会者
        List<BusiMcuSmc3TemplateParticipant> ps = deptPsMap.get(tc.getDeptId());
        if (ps != null)
        {
            for (BusiMcuSmc3TemplateParticipant busiTemplateParticipant : ps)
            {
                TerminalAttendeeSmc3 ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }
                conferenceContext.addAttendee(ta);
            }

            // 根据显示顺序排序
            Collections.sort(conferenceContext.getAttendees());
        }

        BusiMcuSmc3TemplateDept busiTemplateDeptCondition = new BusiMcuSmc3TemplateDept();
        busiTemplateDeptCondition.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc3TemplateDept> busiTemplateDepts = BeanFactory.getBean(BusiMcuSmc3TemplateDeptMapper.class).selectBusiMcuSmc3TemplateDeptList(busiTemplateDeptCondition);
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
            AttendeeSmc3 masterAttendee = conferenceContext.getAttendeeById(templateMasterParticipant.getUuid());
            conferenceContext.setMasterAttendee(masterAttendee);
        }


        {

            // 会议室的id，这里指向加密后的ConferenceNumber
            conferenceContext.setId(EncryptIdUtil.generateConferenceId(conferenceContext.getContextKey()));

            // 设置MCU参会的uri
            conferenceContext.setMcuAttendeeRemoteParty(bridgesByDept.getBridgeIp());
        }

        // 默认参会者操作解析设置
        DefaultAttendeeOperation defaultAttendeeOperation = BeanFactory.getBean(IDefaultAttendeeSmc3OperationPackageService.class).packing(conferenceContext, tc, busiTemplateParticipantMap);
        conferenceContext.setDefaultViewOperation(defaultAttendeeOperation);

        BusiMcuSmc3ConferenceAppointment con = new BusiMcuSmc3ConferenceAppointment();
        con.setTemplateId(templateConferenceId);
        con.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
        List<BusiMcuSmc3ConferenceAppointment> cas = BeanFactory.getBean(BusiMcuSmc3ConferenceAppointmentMapper.class).selectBusiMcuSmc3ConferenceAppointmentList(con);
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
        //多级会议TREE
        cascadeTree(conferenceContext, tc);
        return conferenceContext;
    }

    private void cascadeTree(Smc3ConferenceContext conferenceContext, BusiMcuSmc3TemplateConference tc) {

        List<ConferenceNode> cascadeConferenceTree = new ArrayList<>();
        if(Strings.isNotBlank(tc.getCascadeId()) || Objects.equals(ConstAPI.CASCADE, tc.getCategory())){
            String cascadeNodesTemp = tc.getCascadeNodesTemp();
            if(Strings.isBlank(cascadeNodesTemp) || Objects.equals("null",cascadeNodesTemp)){
                BusiMcuSmc3TemplateConferenceMapper bean = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference=new BusiMcuSmc3TemplateConference();
                busiMcuSmc3TemplateConference.setId(Long.valueOf(tc.getCascadeId()));
                busiMcuSmc3TemplateConference.setCategory(ConstAPI.CASCADE);
                List<BusiMcuSmc3TemplateConference> busiMcuSmc3TemplateConferences = bean.selectBusiMcuSmc3TemplateConferenceList(busiMcuSmc3TemplateConference);
                if(CollectionUtils.isEmpty(busiMcuSmc3TemplateConferences)){
                    return;
                }
                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConferenceMainCascade = busiMcuSmc3TemplateConferences.get(0);
                cascadeNodesTemp = busiMcuSmc3TemplateConferenceMainCascade.getCascadeNodesTemp();

                List<TemplateNodeTemp> templateNodesTemp = JSONArray.parseArray(cascadeNodesTemp, TemplateNodeTemp.class);
                for (TemplateNodeTemp templateNodeTemp : templateNodesTemp) {
                    if(Objects.equals(templateNodeTemp.getTemplateId(),conferenceContext.getTemplateConferenceId())){
                        ConferenceNode conferenceNode = new ConferenceNode();
                        conferenceNode.setConferenceId(conferenceContext.getId());
                        conferenceNode.setName(busiMcuSmc3TemplateConferenceMainCascade.getName()+"-"+conferenceContext.getName());
                        cascadeConferenceTree.add(conferenceNode);
                    }

                    if(Objects.equals(templateNodeTemp.getParentTemplateId(),conferenceContext.getTemplateConferenceId())){
                        ConferenceNode conferenceNode = new ConferenceNode();
                        BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference2 = bean.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                        Long id = busiMcuSmc3TemplateConference2.getId();
                        conferenceNode.setConferenceId(EncryptIdUtil.generateConferenceId(id, McuType.SMC3.getCode()));
                        conferenceNode.setName(busiMcuSmc3TemplateConferenceMainCascade.getName()+"-"+busiMcuSmc3TemplateConference2.getName());
                        cascadeConferenceTree.add(conferenceNode);
                    }
                }
            } else {
                List<TemplateNodeTemp> templateNodesTemp = JSONArray.parseArray(cascadeNodesTemp, TemplateNodeTemp.class);
                for (TemplateNodeTemp templateNodeTemp : templateNodesTemp) {
                    BusiMcuSmc3TemplateConferenceMapper bean = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
                    ConferenceNode conferenceNode = new ConferenceNode();
                    BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = bean.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                    Long id = busiMcuSmc3TemplateConference.getId();
                    conferenceNode.setConferenceId(EncryptIdUtil.generateConferenceId(id, McuType.SMC3.getCode()));
                    conferenceNode.setName(conferenceContext.getName() + "-"+templateNodeTemp.getSubject());
                    cascadeConferenceTree.add(conferenceNode);
                }
            }

            conferenceContext.setCascadeConferenceTree(cascadeConferenceTree);
        }


    }

    /**
     * <pre>绑定级联的MCU参会信息</pre>
     * @author lilinhai
     * @since 2021-03-23 18:05
     * @param conferenceContext
     */
    private void bindCascadeConference(Smc3ConferenceContext conferenceContext, BusiMcuSmc3TemplateConference tc) {
        Map<String, ViewTemplateConference> newConferenceIdMap = new HashMap<>();
        Map<String, McuAttendeeSmc3> oldConferenceIdMap = new HashMap<>();

        List<McuAttendeeSmc3> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeSmc3 attendee : mcuAttendees) {
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
    private void bindCascadeConference(Smc3ConferenceContext conferenceContext, ViewTemplateConference viewTemplateConference)
    {
        McuAttendeeSmc3 mcuAttendee = new McuAttendeeSmc3();
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



    public static void createSmc3Template(BusiMcuSmc3TemplateConference tc, Smc3ConferenceContext conferenceContext) {
        SmcConferenceTemplate smcConferenceTemplate = getSmcConferenceTemplate(tc);
        List<ParticipantRspDto> templateParticipants = new ArrayList<>();
        List<AttendeeSmc3> attendees = conferenceContext.getAttendees();
        for (AttendeeSmc3 attendee : attendees) {
            ParticipantRspDto participantRspDto = new ParticipantRspDto();
            participantRspDto.setName(attendee.getName());
            participantRspDto.setUri(attendee.getRemoteParty());
            participantRspDto.setIpProtocolType(2);
            participantRspDto.setDialMode("OUT");
            participantRspDto.setVoice(false);
            participantRspDto.setRate(0);
            if(Strings.isNotBlank(tc.getConferencePassword())){
                participantRspDto.setDtmfInfo(tc.getConferencePassword());
            }
            participantRspDto.setMainParticipant(Objects.equals(conferenceContext.getMasterAttendee()!=null? conferenceContext.getMasterAttendee().getId():null, attendee.getId()));
            templateParticipants.add(participantRspDto);
        }
        smcConferenceTemplate.setTemplateParticipants(templateParticipants);
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(tc.getDeptId());
        String jsonString = JSONObject.toJSONString(smcConferenceTemplate);
        String result = bridge.getSmcConferencesTemplateInvoker().creatConferencesTemplate(jsonString, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        if(result!=null&&result.contains(ConstAPI.TOKEN_NOT_EXIST)){
            bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders();
        }
        if (result != null&&result.contains(ConstAPI.ERRORNO)) {
            SmcErrorResponse smcErrorResponse = JSON.parseObject(result, SmcErrorResponse.class);
            throw new CustomException("新增模板失败:" + smcErrorResponse.getErrorDesc());
        }
        SmcConferenceTemplate smcConferenceTemplate1 = JSON.parseObject(result, SmcConferenceTemplate.class);
        if (Objects.isNull(smcConferenceTemplate1)) {
            throw new CustomException("创建模板会议失败");
        }
        tc.setSmcTemplateId(smcConferenceTemplate1.getId());
        tc.setUpdateTime(new Date());
        conferenceContext.setSmcTemplateId(smcConferenceTemplate1.getId());
        BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class).updateBusiMcuSmc3TemplateConference(tc);
    }


    /**
     * <pre>添加所有子会议启动器</pre>
     * @author lilinhai
     * @since 2021-02-01 15:03
     * @param deptPsMap
     * @param busiTemplateDepts
     */
    private void buildMainConferenceContext(Map<Long, List<BusiMcuSmc3TemplateParticipant>> deptPsMap, List<BusiMcuSmc3TemplateDept> busiTemplateDepts, Smc3ConferenceContext conferenceContext)
    {
        List<BusiMcuSmc3TemplateParticipant> ps;

        // 倒叙排列，按此顺序进行先后子会议的呼叫发起
        Collections.sort(busiTemplateDepts, new Comparator<BusiMcuSmc3TemplateDept>()
        {
            @Override
            public int compare(BusiMcuSmc3TemplateDept o1, BusiMcuSmc3TemplateDept o2) {
                return o2.getWeight().compareTo(o1.getWeight());
            }

        });

        for (BusiMcuSmc3TemplateDept busiTemplateDept : busiTemplateDepts)
        {
            // 先拉会议发起方的与会者
            ps = deptPsMap.get(busiTemplateDept.getDeptId());
            if (ps == null)
            {
                continue;
            }


            BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
            DeptWeight dw = new DeptWeight();
            dw.setDeptId(busiTemplateDept.getDeptId());
            dw.setWeight(busiTemplateDept.getWeight());
            conferenceContext.addDeptWeight(dw);
            for (BusiMcuSmc3TemplateParticipant busiTemplateParticipant : ps)
            {
                TerminalAttendeeSmc3 ta = AttendeeUtils.packTerminalAttendee(busiTemplateParticipant);
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(ta.getTerminalId());
                if (busiUserTerminal != null) {
                    ta.setUserId(busiUserTerminal.getUserId());
                }

                // 封装级联与会者属性并添加到集合
                conferenceContext.addCascadeAttendee(ta);

                ta.setConferenceNumber(conferenceContext.getConferenceNumber());
                conferenceContext.addAttendeeToIdMap(ta);
                conferenceContext.addAttendeeToRemotePartyMap(ta);
            }

            // 排序级联子会议终端
            Collections.sort(conferenceContext.getCascadeAttendeesMap().get(busiTemplateDept.getDeptId()));

            AttendeeSmc3 m = conferenceContext.getCascadeAttendeesMap().get(busiTemplateDept.getDeptId()).remove(0);

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
    private void bindCascadeFmeAttendee(Smc3ConferenceContext conferenceContext, BusiMcuSmc3TemplateDept busiTemplateDept)
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
        Smc3Bridge fmeBridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiTemplateDept.getDeptId());
        McuAttendeeSmc3 fmeAttendee = new McuAttendeeSmc3();
        fmeAttendee.setName(SysDeptCache.getInstance().get(busiTemplateDept.getDeptId()).getDeptName());
        fmeAttendee.setWeight(busiTemplateDept.getWeight());
        fmeAttendee.setId(busiTemplateDept.getUuid());
        if (fmeBridge != null)
        {
            fmeAttendee.setIp(fmeBridge.getBusiSMC().getIp());
            fmeAttendee.setCascadeMcuId(fmeBridge.getBusiSMC().getId());
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
     * <pre>拷贝模板参数到会议上下文</pre>
     * @author lilinhai
     * @since 2021-02-02 15:42
     * @param conferenceContext
     * @param tc void
     */
    private void copyTemplateAttrs(Smc3ConferenceContext conferenceContext, BusiMcuSmc3TemplateConference tc)
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
        conferenceContext.setTenantId(tc.getTenantId());
        conferenceContext.setMaxParticipantNum(tc.getMaxParticipantNum()==null?500:tc.getMaxParticipantNum());
        conferenceContext.setDurationEnabled(tc.getDurationEnabled());
        conferenceContext.setDurationTime(tc.getDurationTime());
        conferenceContext.setCreateUserId(tc.getCreateUserId());
        conferenceContext.setChairmanPassword(tc.getChairmanPassword());
        conferenceContext.setGuestPassword(tc.getGuestPassword());
        conferenceContext.setMuteType(tc.getMuteType());
        String upCascadeConferenceId = EncryptIdUtil.generateConferenceId(tc.getUpCascadeId(), tc.getUpCascadeMcuType());
        conferenceContext.setUpCascadeConferenceId(upCascadeConferenceId);
        conferenceContext.setUpCascadeIndex(tc.getUpCascadeIndex());
        conferenceContext.setCategory(tc.getCategory());
    }


    public static SmcConferenceTemplate getSmcConferenceTemplate(BusiMcuSmc3TemplateConference smcTemplateConferenceRequest) {
        SmcConferenceTemplate smcConferenceTemplate = buildTemplateConference();

        smcConferenceTemplate.setGuestPassword(smcTemplateConferenceRequest.getGuestPassword());
        smcConferenceTemplate.setChairmanPassword(smcTemplateConferenceRequest.getChairmanPassword());
        SmcConferenceTemplate.ConferenceCapabilitySettingDTO conferenceCapabilitySetting = smcConferenceTemplate.getConferenceCapabilitySetting();
        conferenceCapabilitySetting.setSvcVideoResolution(SVC_VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setVideoResolution(VIDEO_RESOLUTION);
        Integer bandwidth = smcTemplateConferenceRequest.getBandwidth();
        conferenceCapabilitySetting.setRate(bandwidth);
        String videoResolution = smcTemplateConferenceRequest.getVideoResolution();
        String videoProtocol = smcTemplateConferenceRequest.getVideoProtocol();
//        if (StringUtils.isNotBlank(videoResolution)) {
            conferenceCapabilitySetting.setVideoResolution(videoResolution);
            conferenceCapabilitySetting.setSvcVideoResolution(videoResolution);
//            conferenceCapabilitySetting.setVideoProtocol("H264_HP");
//        }
        conferenceCapabilitySetting.setVideoProtocol(videoProtocol);
        conferenceCapabilitySetting.setEnableRecord(smcTemplateConferenceRequest.getRecordingEnabled()==1);
        conferenceCapabilitySetting.setEnableLiveBroadcast(smcTemplateConferenceRequest.getStreamingEnabled() == 1);
        conferenceCapabilitySetting.setEnableDataConf(false);
        smcConferenceTemplate.setConferenceCapabilitySetting(conferenceCapabilitySetting);

        SmcConferenceTemplate.StreamServiceDTO streamService = smcConferenceTemplate.getStreamService();
        streamService.setSupportRecord(smcTemplateConferenceRequest.getRecordingEnabled() == 1);
        streamService.setAmcRecord(false);
        streamService.setSupportLive(smcTemplateConferenceRequest.getStreamingEnabled() == 1);
        smcConferenceTemplate.setStreamService(streamService);

        smcConferenceTemplate.setSubject(smcTemplateConferenceRequest.getName());
        smcConferenceTemplate.setVmrNumber("");
        smcConferenceTemplate.setDuration(smcTemplateConferenceRequest.getDurationTime());
        SmcConferenceTemplate.ConferencePolicySettingDTO conferencePolicySetting = smcConferenceTemplate.getConferencePolicySetting();
        conferencePolicySetting.setVoiceActive(false);
        conferencePolicySetting.setAutoMute(smcTemplateConferenceRequest.getMuteType() == 1);
        conferencePolicySetting.setMaxParticipantNum(smcTemplateConferenceRequest.getMaxParticipantNum());
        return smcConferenceTemplate;
    }
    public static SmcConferenceTemplate buildTemplateConference() {

        SmcConferenceTemplate smcConferenceTemplateRquest = new SmcConferenceTemplate();
        smcConferenceTemplateRquest.setGuestPassword(null);
        smcConferenceTemplateRquest.setChairmanPassword(null);
        smcConferenceTemplateRquest.setMainMcuId("");
        smcConferenceTemplateRquest.setMainMcuName("");
        smcConferenceTemplateRquest.setMainServiceZoneId("");
        smcConferenceTemplateRquest.setTemplateType("COMMON_CONF");
        smcConferenceTemplateRquest.setVmrNumber("");
        SmcConferenceTemplate.ConferenceCapabilitySettingDTO conferenceCapabilitySetting = new SmcConferenceTemplate.ConferenceCapabilitySettingDTO();
        conferenceCapabilitySetting.setAmcRecord(false);
        conferenceCapabilitySetting.setAudioProtocol("AAC_LD_S");
        conferenceCapabilitySetting.setAudioRecord(false);
        conferenceCapabilitySetting.setAutoRecord(false);
        conferenceCapabilitySetting.setCheckInDuration(10);
        conferenceCapabilitySetting.setDataConfProtocol("DATA_RESOLUTION_STANDARD");
        conferenceCapabilitySetting.setEnableCheckIn(false);
        conferenceCapabilitySetting.setEnableDataConf(false);
        conferenceCapabilitySetting.setEnableFec(false);
        conferenceCapabilitySetting.setEnableLiveBroadcast(false);
        conferenceCapabilitySetting.setEnableRecord(false);
        conferenceCapabilitySetting.setMediaEncrypt("NOT_ENCRYPT_MODE");//AUTO_ENCRYPT_MODE NOT_ENCRYPT_MODE
        conferenceCapabilitySetting.setReserveResource(0);
        conferenceCapabilitySetting.setSvcRate(3840);
        conferenceCapabilitySetting.setSvcVideoResolution(SVC_VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setRate(1920);
        conferenceCapabilitySetting.setVideoProtocol("H264_BP");//H264_HP
        conferenceCapabilitySetting.setVideoResolution(VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setType("AVC");
        smcConferenceTemplateRquest.setConferenceCapabilitySetting(conferenceCapabilitySetting);
        SmcConferenceTemplate.ConferencePolicySettingDTO policySettingDTO = new SmcConferenceTemplate.ConferencePolicySettingDTO();
        policySettingDTO.setAutoEnd(true);
        policySettingDTO.setAutoExtend(true);
        policySettingDTO.setAutoMute(false);
        policySettingDTO.setChairmanPassword("");
        policySettingDTO.setGuestPassword("");
        policySettingDTO.setLanguage(1);
        policySettingDTO.setVoiceActive(false);

        smcConferenceTemplateRquest.setConferencePolicySetting(policySettingDTO);

        SmcConferenceTemplate.StreamServiceDTO streamServiceDTO = new SmcConferenceTemplate.StreamServiceDTO();
        streamServiceDTO.setSupportMinutes(false);
        smcConferenceTemplateRquest.setStreamService(streamServiceDTO);

        SmcConferenceTemplate.SubtitleServiceDTO subtitleServiceDTO = new SmcConferenceTemplate.SubtitleServiceDTO();
        subtitleServiceDTO.setEnableSubtitle(false);
        subtitleServiceDTO.setSrcLang("CHINESE");
        smcConferenceTemplateRquest.setSubtitleService(subtitleServiceDTO);

        return smcConferenceTemplateRquest;

    }



}
