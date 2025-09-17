/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WelcomeServiceImpl.java
 * Package     : com.paradisecloud.fcm.service.impls
 * @author lilinhai 
 * @since 2021-06-02 11:34
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.web.service.impls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.pagehelper.PageHelper;
import com.paradisecloud.fcm.common.enumer.FcmType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberSectionService;
import com.paradisecloud.fcm.terminal.fs.cache.DeptFcmMappingCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.FreeSwitchClusterCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridgeCluster;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.DeptFmeMappingCache;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.service.interfaces.IBusiFmeDeptService;
import com.paradisecloud.fcm.web.service.interfaces.IWelcomeService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.DeptFsbcMappingCache;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.utils.SecurityUtils;

import javax.annotation.Resource;

@Component
public class WelcomeServiceImpl implements IWelcomeService
{

    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    
    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;
    
    @Resource
    private IBusiFmeDeptService busiFmeDeptService;
    
    @Resource
    private ISysDeptService sysDeptService;
    
    @Resource
    private IBusiConferenceNumberSectionService busiConferenceNumberSectionService;

    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;

    
    @Override
    public JSONObject conferenceStat()
    {
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        
        JSONObject json = new JSONObject();
        if (deptId != null)
        {
            BusiTemplateConference con = new BusiTemplateConference();
            con.setDeptId(deptId);
            json.put("deptTemplateCount", busiTemplateConferenceService.selectBusiTemplateConferenceListWithoutBusinessFieldType(con).size());
            
            SysDept con3 = new SysDept();
            con3.setDeptId(deptId);
            List<SysDept> sds = sysDeptService.selectDeptList(con3);
            
            int c = 0;
            Collection<ConferenceContext> cc = ConferenceContextCache.getInstance().values();
            for (Iterator<ConferenceContext> iterator = cc.iterator(); iterator.hasNext();)
            {
                ConferenceContext conferenceContext = iterator.next();
                for (SysDept sysDept : sds)
                {
                    if (conferenceContext.getDeptId().longValue() == sysDept.getDeptId().longValue())
                    {
                        c++;
                        break;
                    }
                }
            }
            
            // 活跃会议室的数量
            json.put("activeConferenceCount", c);
            
            BusiConferenceAppointment con1 = new BusiConferenceAppointment();
            con1.setDeptId(deptId);
            
            // 预约会议数
            json.put("appointConferenceCount", busiConferenceAppointmentService.selectBusiConferenceAppointmentListWithOutBusinessFieldType(con1).size());
        }
        else
        {
            // 活跃会议室的数量
            json.put("activeConferenceCount", ConferenceContextCache.getInstance().size());
            json.put("deptTemplateCount", busiTemplateConferenceService.selectBusiTemplateConferenceListWithoutBusinessFieldType(new BusiTemplateConference()).size());
            
            // 预约会议数
            json.put("appointConferenceCount", busiConferenceAppointmentService.selectBusiConferenceAppointmentListWithOutBusinessFieldType(new BusiConferenceAppointment()).size());
        }
        return json;
    }
    
    public JSONObject terminalStat()
    {
        JSONObject json = new JSONObject();
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        if (deptId != null)
        {
            Set<Long> deptIds = SysDeptCache.getInstance().getSubordinateDeptIds(deptId);
            int total = 0;
            int onlineCount = 0;
            int meetingCount = 0;
            Collection<BusiTerminal> bts = TerminalCache.getInstance().values();
            for (Iterator<BusiTerminal> iterator = bts.iterator(); iterator.hasNext();)
            {
                BusiTerminal busiTerminal = iterator.next();
                if (deptIds.contains(busiTerminal.getDeptId()))
                {
                    total++;
                    if (TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus()) == TerminalOnlineStatus.ONLINE)
                    {
                        onlineCount++;
                    }
                }
            }
            
            Collection<ConferenceContext> cc = ConferenceContextCache.getInstance().values();
            for (Iterator<ConferenceContext> iterator0 = cc.iterator(); iterator0.hasNext();)
            {
                ConferenceContext conferenceContext = iterator0.next();
                AtomicInteger ai = new AtomicInteger();
                ConferenceContextUtils.eachNonFmeAttendeeInConference(conferenceContext, (a)->{
                    if (a instanceof TerminalAttendee && deptIds.contains(a.getDeptId()))
                    {
                        ai.incrementAndGet();
                    }
                });
                meetingCount += ai.get();
            }
            
            json.put("total", total);
            json.put("onlineCount", onlineCount);
            json.put("meetingCount", meetingCount);
        }
        else
        {
            int onlineCount = 0;
            int meetingCount = 0;
            Collection<BusiTerminal> bts = TerminalCache.getInstance().values();
            for (Iterator<BusiTerminal> iterator = bts.iterator(); iterator.hasNext();)
            {
                BusiTerminal busiTerminal = iterator.next();
                if (TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus()) == TerminalOnlineStatus.ONLINE)
                {
                    onlineCount++;
                }
            }
            
            Collection<ConferenceContext> cc = ConferenceContextCache.getInstance().values();
            for (Iterator<ConferenceContext> iterator0 = cc.iterator(); iterator0.hasNext();)
            {
                ConferenceContext conferenceContext = iterator0.next();
                AtomicInteger ai = new AtomicInteger();
                ConferenceContextUtils.eachNonFmeAttendeeInConference(conferenceContext, (a)->{
                    if (a instanceof TerminalAttendee)
                    {
                        ai.incrementAndGet();
                    }
                });
                meetingCount += ai.get();
            }
            
            json.put("total", TerminalCache.getInstance().size());
            json.put("onlineCount", onlineCount);
            json.put("meetingCount", meetingCount);
        }
        return json;
    }

    @Override
    public JSONObject tenantResource()
    {
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        if (deptId == null)
        {
            return null;
        }
        
        JSONObject json = new JSONObject();
        
        BusiFmeDept busiFmeDept = DeptFmeMappingCache.getInstance().getBindFme(deptId);
        if (busiFmeDept != null)
        {
            json.put("bindFmeInfo", busiFmeDeptService.toModelBean(busiFmeDept));
        }
        else
        {
            json.put("bindFmeInfo", null);
        }
        
        BusiFsbcServerDept busiFsbcServerDept = DeptFsbcMappingCache.getInstance().get(deptId);
        if (busiFsbcServerDept != null)
        {
            BusiFsbcRegistrationServer s = FsbcBridgeCache.getInstance().getById(busiFsbcServerDept.getFsbcServerId()).getBusiFsbcRegistrationServer();
            ModelBean modelBean = new ModelBean();
            modelBean.put("dataSyncIp",s.getDataSyncIp());
            modelBean.put("callIp",s.getCallIp());
            json.put("bindFsbcInfo", modelBean);
        }
        else
        {
            json.put("bindFsbcInfo", null);
        }

        BusiFreeSwitchDept busiFreeSwitchDept = DeptFcmMappingCache.getInstance().get(deptId);
        if (busiFreeSwitchDept != null) {
            json.put("bindFcmInfo", toModelBean(busiFreeSwitchDept));
        } else {
            json.put("bindFcmInfo", null);
        }

        List<ModelBean> cnss = getBindBusiConferenceNumberSection(deptId);
        if (ObjectUtils.isEmpty(cnss))
        {
            json.put("bindConferenceNumberSectionInfo", null);
        }
        else
        {
            json.put("bindConferenceNumberSectionInfo", cnss);
        }

        return json;
    }

    public List<ModelBean> getBindBusiConferenceNumberSection(Long deptId)
    {
        BusiConferenceNumberSection con = new BusiConferenceNumberSection();
        con.setDeptId(deptId);
        List<ModelBean> cnss = busiConferenceNumberSectionService.selectBusiConferenceNumberSectionList(con);
        if (ObjectUtils.isEmpty(cnss))
        {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return getBindBusiConferenceNumberSection(sysDept.getParentId());
            }
            else
            {
                return null;
            }
        }
        else
        {
            return cnss;
        }
    }

    public ModelBean toModelBean(BusiFreeSwitchDept busiFreeSwitchDept) {
        ModelBean mb = new ModelBean(busiFreeSwitchDept);
        mb.put("deptName", SysDeptCache.getInstance().get(busiFreeSwitchDept.getDeptId()).getDeptName());
        mb.put("fcmTypeName", FcmType.convert(busiFreeSwitchDept.getFcmType()).getName());
        StringBuilder fmeInfoBuilder = new StringBuilder();
        List<BusiFreeSwitch> busiFreeSwitchList = new ArrayList<>();
        if (FcmType.convert(busiFreeSwitchDept.getFcmType()) == FcmType.CLUSTER) {
            BusiFreeSwitchCluster busiFreeSwitchCluster = FreeSwitchClusterCache.getInstance().get(busiFreeSwitchDept.getServerId());
            fmeInfoBuilder.append("【").append(busiFreeSwitchCluster.getName()).append("】");
            FcmBridgeCluster fcmBridgeCluster = FcmBridgeCache.getInstance().getByFcmClusterId(busiFreeSwitchDept.getServerId());
            for (FcmBridge fcmBridge : fcmBridgeCluster.getFcmBridges()) {
                busiFreeSwitchList.add(fcmBridge.getBusiFreeSwitch());
            }
        } else {
            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiFreeSwitchDept.getServerId());
            fmeInfoBuilder.append("【").append(fcmBridge.getBusiFreeSwitch().getName()).append("】");
            busiFreeSwitchList.add(fcmBridge.getBusiFreeSwitch());
        }

        if (busiFreeSwitchDept.getFcmType() == FcmType.SINGLE_NODE.getValue()) {
        } else if (busiFreeSwitchDept.getFcmType() == FcmType.CLUSTER.getValue()) {

        }
        mb.put("existAvailableFcmBridge", busiFreeSwitchList != null);
        if (busiFreeSwitchList == null) {
            fmeInfoBuilder.append("-").append("当前无可用的FME信息");
            mb.put("fcms", new ArrayList<>());
        } else {
            fmeInfoBuilder.append("FCM[");

            List<String> fmes = new ArrayList<String>();
            StringBuilder fmeIpInfoBuilder = new StringBuilder();
            busiFreeSwitchList.forEach((fcmBridge) -> {
                if (!ObjectUtils.isEmpty(fmeIpInfoBuilder)) {
                    fmeIpInfoBuilder.append(", ");
                }
                fmeIpInfoBuilder.append(fcmBridge.getIp());

                fmes.add(fcmBridge.getIp());
            });
            fmeInfoBuilder.append(fmeIpInfoBuilder);
            fmeInfoBuilder.append("]");
            mb.put("fcms", fmes);
        }
        mb.put("fcmInfo", fmeInfoBuilder.toString());
        return mb;
    }

    @Override
    public List<JSONObject> activeConferencesPages(String searchKey,int pageIndex,int pageSize){
        List<JSONObject> jsons = new ArrayList<>();
        PageHelper.startPage(pageIndex,pageSize);
        List<BusiTemplateConference> templateConferences = busiTemplateConferenceService.selectAllBusiTemplateConferenceList(searchKey, null);
        if(!CollectionUtils.isEmpty(templateConferences)){
            for (BusiTemplateConference templateConference : templateConferences) {
                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(EncryptIdUtil.generateConferenceId(templateConference.getConferenceNumber(), McuType.FME.getCode()));
                if(conferenceContext!=null&&templateConference!=null){
                    JSONObject json = toJson(conferenceContext,templateConference);
                    jsons.add(json);
                }
            }
        }
        return jsons;
    }


    @Override
    public List<JSONObject> activeConferences()
    {
        List<JSONObject> jsons = new ArrayList<>();

        Collection<ConferenceContext> cc = ConferenceContextCache.getInstance().values();
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        if (deptId != null)
        {
            SysDept con = new SysDept();
            con.setDeptId(deptId);
            List<SysDept> sds = sysDeptService.selectDeptList(con);
            for (Iterator<ConferenceContext> iterator = cc.iterator(); iterator.hasNext();)
            {
                ConferenceContext conferenceContext = iterator.next();
                if (!conferenceContext.isEnd() && conferenceContext.isStart()) {
                    BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                    if (busiTemplateConference != null) {
                        for (SysDept sysDept : sds) {
                            if (conferenceContext.getDeptId().longValue() == sysDept.getDeptId().longValue()) {
                                JSONObject json = toJson(conferenceContext,busiTemplateConference);
                                jsons.add(json);
                                break;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            for (Iterator<ConferenceContext> iterator = cc.iterator(); iterator.hasNext();)
            {
                ConferenceContext conferenceContext = iterator.next();
                if (!conferenceContext.isEnd() && conferenceContext.isStart()) {
                    BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                    if (busiTemplateConference != null) {
                        JSONObject json = toJson(conferenceContext,busiTemplateConference);
                        jsons.add(json);
                    }
                }
            }
        }
        
        return jsons;
    }

    private JSONObject toJson(ConferenceContext conferenceContext,BusiTemplateConference templateConference)
    {
        JSONObject json = new JSONObject();
        json.put("conferenceName", conferenceContext.getName());
        json.put("conferenceNumber", conferenceContext.getConferenceNumber());
        json.put("templateId", conferenceContext.getTemplateConferenceId());
        json.put("bindwidth", conferenceContext.getBandwidth());
        json.put("isAppointment",conferenceContext.isAppointment());
        if(conferenceContext.isAppointment()){
            json.put("type",conferenceContext.getAppointmentType());
        }
        json.put("conferenceId",conferenceContext.getId());
        json.put("deptId",conferenceContext.getDeptId());
        json.put("deptName",SysDeptCache.getInstance().get(conferenceContext.getDeptId()).getDeptName());
        json.put("createUserName",templateConference.getCreateUserName());
        json.put("createUserId",templateConference.getCreateUserId());
        AtomicInteger as = new AtomicInteger();
        AtomicInteger inMeetings = new AtomicInteger();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(conferenceContext, (a)->{
            as.incrementAndGet();
            if (a.isMeetingJoined())
            {
                inMeetings.incrementAndGet();
            }
        });
        
        json.put("terminalCount", as.get());
        json.put("inMeetingTerminalCount", inMeetings.get());
        json.put("conferenceStartTime", conferenceContext.getStartTime());
        json.put("conferenceName", conferenceContext.getName());
        json.put("masterName", conferenceContext.getMasterAttendee() != null ? conferenceContext.getMasterAttendee().getName() : null);
        json.put("type", "fme");
        json.put("tenantId", "");
        return json;
    }
    
}
