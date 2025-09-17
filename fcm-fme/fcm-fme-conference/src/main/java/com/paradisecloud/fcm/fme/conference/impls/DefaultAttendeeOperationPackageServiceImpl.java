/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultAttendeeOperationPackageServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-04-12 18:48
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.conference.impls;

import java.util.*;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.fme.model.busi.operation.DefaultViewOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.CellScreenAttendeeOperation;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceDefaultViewCellScreenMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceDefaultViewDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceDefaultViewPaticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewCellScreen;
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewDept;
import com.paradisecloud.fcm.dao.model.BusiTemplateConferenceDefaultViewPaticipant;
import com.paradisecloud.fcm.dao.model.BusiTemplateParticipant;
import com.paradisecloud.fcm.fme.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IDefaultAttendeeOperationPackageService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.core.DeptWeight;
import com.paradisecloud.fcm.fme.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AllEqualSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OnePlusNSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.StackedSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.TelepresenceSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.sinhy.exception.SystemException;

/**
 * <pre>默认参会者操作对象封装器实现类</pre>
 * @author lilinhai
 * @since 2021-04-12 18:48
 * @version V1.0
 */
@Transactional
@Service
public class DefaultAttendeeOperationPackageServiceImpl implements IDefaultAttendeeOperationPackageService
{

    @Autowired
    private BusiTemplateConferenceDefaultViewCellScreenMapper busiTemplateConferenceDefaultViewCellScreenMapper;

    @Autowired
    private BusiTemplateConferenceDefaultViewDeptMapper busiTemplateConferenceDefaultViewDeptMapper;

    @Autowired
    private BusiTemplateConferenceDefaultViewPaticipantMapper busiTemplateConferenceDefaultViewPaticipantMapper;

    @Override
    public DefaultAttendeeOperation packing(ConferenceContext conferenceContext, BusiTemplateConference tc, Map<Long, BusiTemplateParticipant> busiTemplateParticipantMap)
    {
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(tc.getDefaultViewLayout());
        defaultAttendeeOperation.setDefaultViewIsBroadcast(tc.getDefaultViewIsBroadcast());
        defaultAttendeeOperation.setDefaultViewIsFill(tc.getDefaultViewIsFill());
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(tc.getDefaultViewIsDisplaySelf());
        defaultAttendeeOperation.setDefaultViewPollingInterval(tc.getPollingInterval());
        defaultAttendeeOperation.initSplitScreen();
        BusiTemplateConferenceDefaultViewCellScreen con = new BusiTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        List<BusiTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiTemplateConferenceDefaultViewCellScreenMapper.selectBusiTemplateConferenceDefaultViewCellScreenList(con);
        for (BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen : defaultViewCellScreens)
        {
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
            if (cellScreen == null)
            {
                throw new SystemException(1008767, "找不到分屏信息：" + busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
            }
            cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiTemplateConferenceDefaultViewCellScreen.getOperation()));
            cellScreen.setFixed(YesOrNo.convert(busiTemplateConferenceDefaultViewCellScreen.getIsFixed()));
            cellScreen.setSerialNumber(busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
        }

        // 部门信息
        BusiTemplateConferenceDefaultViewDept con1 = new BusiTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        List<BusiTemplateConferenceDefaultViewDept> defaultViewDepts = busiTemplateConferenceDefaultViewDeptMapper.selectBusiTemplateConferenceDefaultViewDeptList(con1);
        Collections.sort(defaultViewDepts, new Comparator<BusiTemplateConferenceDefaultViewDept>()
        {
            public int compare(BusiTemplateConferenceDefaultViewDept o1, BusiTemplateConferenceDefaultViewDept o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        BusiTemplateConferenceDefaultViewPaticipant con2 = new BusiTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        List<BusiTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiTemplateConferenceDefaultViewPaticipantMapper.selectBusiTemplateConferenceDefaultViewPaticipantList(con2);
        Map<Integer, List<BusiTemplateConferenceDefaultViewPaticipant>> busiTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
        Map<Long, List<BusiTemplateConferenceDefaultViewPaticipant>> deptDefaultViewPaticipantsMap = new HashMap<>();
        for (BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants)
        {
            if (busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber() != null)
            {
                List<BusiTemplateConferenceDefaultViewPaticipant> busiTemplateConferenceDefaultViewPaticipants = busiTemplateConferenceDefaultViewPaticipantsMap.get(busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                if (busiTemplateConferenceDefaultViewPaticipants == null)
                {
                    busiTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                    busiTemplateConferenceDefaultViewPaticipantsMap.put(busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber(), busiTemplateConferenceDefaultViewPaticipants);
                }
                busiTemplateConferenceDefaultViewPaticipants.add(busiTemplateConferenceDefaultViewPaticipant);
            }
            else {
                BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiTemplateParticipant)) {

                    long deptId = conferenceContext.getAttendeeById(busiTemplateParticipant.getUuid()).getDeptId();
                    List<BusiTemplateConferenceDefaultViewPaticipant> allBusiTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiTemplateConferenceDefaultViewPaticipants == null) {
                        allBusiTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiTemplateConferenceDefaultViewPaticipants.add(busiTemplateConferenceDefaultViewPaticipant);
                }

            }
        }

        Comparator<BusiTemplateConferenceDefaultViewPaticipant> c = new Comparator<BusiTemplateConferenceDefaultViewPaticipant>()
        {
            @Override
            public int compare(BusiTemplateConferenceDefaultViewPaticipant o1, BusiTemplateConferenceDefaultViewPaticipant o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        };

        busiTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
            Collections.sort(v, c);
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(screenNumber - 1);
            for (BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant : v)
            {
                BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiTemplateParticipant)) {
                    cellScreen.addAttendee(conferenceContext.getAttendeeById(busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                }
            }
        });

        deptDefaultViewPaticipantsMap.forEach((deptId, v) -> {
            Collections.sort(v, c);
        });

        List<Attendee> attendees = new ArrayList<>();
        if (deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()) != null) {
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiTemplateConferenceDefaultViewPaticipant) -> {
                BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiTemplateParticipant)) {
                    attendees.add(conferenceContext.getAttendeeById(busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                }
            });
        }

        defaultViewDepts.forEach((d)->{
            JSONObject deptJsonObj = new JSONObject();
            deptJsonObj.put("deptId", d.getDeptId());
            deptJsonObj.put("weight", d.getWeight());
            defaultAttendeeOperation.addDefaultViewDept(deptJsonObj);

            if (deptDefaultViewPaticipantsMap.get(d.getDeptId()) != null)
            {
                deptDefaultViewPaticipantsMap.get(d.getDeptId()).forEach((busiTemplateConferenceDefaultViewPaticipant)->{
                    BusiTemplateParticipant busiTemplateParticipant = busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                    if (!Objects.isNull(busiTemplateParticipant)) {
                        attendees.add(conferenceContext.getAttendeeById(busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                    }
                });
            }
        });

        checkRepeatAttendee(defaultAttendeeOperation, attendees);
        defaultAttendeeOperation.setAttendees(attendees);
        return defaultAttendeeOperation;
    }

    @Override
    public void updateDefaultViewConfigInfo(String conferenceId, JSONObject jsonObj)
    {
        // 会议号
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);

        // 会议室上下文实例对象
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);

        Assert.notNull(conferenceContext, "会议没开始不能在此编辑默认视图");

        // 参数校验
        Assert.isTrue(jsonObj.containsKey("defaultViewLayout"), "默认视图布局不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsBroadcast"), "默认视图是否广播不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsDisplaySelf"), "默认视图是否显示自己不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsFill"), "默认视图是否补位不能为空");
        Assert.isTrue(jsonObj.containsKey("pollingInterval"), "默认视图的轮询时间间隔不能为空");
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        defaultAttendeeOperation.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        defaultAttendeeOperation.setDefaultViewPollingInterval(jsonObj.getInteger("pollingInterval"));
        defaultAttendeeOperation.initSplitScreen();

        List<Attendee> attendees = new ArrayList<>();
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");
        if (defaultAttendeeOperation.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)
                || defaultAttendeeOperation.getDefaultViewLayout().equals(AllEqualSplitScreen.LAYOUT)
                || defaultAttendeeOperation.getDefaultViewLayout().equals(OnePlusNSplitScreen.LAYOUT)
                || defaultAttendeeOperation.getDefaultViewLayout().equals(StackedSplitScreen.LAYOUT)
                || defaultAttendeeOperation.getDefaultViewLayout().equals(TelepresenceSplitScreen.LAYOUT))
        {
            Assert.isTrue(ja == null || ja.size() == 0, "[" + defaultAttendeeOperation.getDefaultViewLayout() + "]布局下不能添加分屏信息");
        }
        else
        {
            Assert.isTrue(jsonObj.containsKey("defaultViewCellScreens"), "cellScreens分屏信息不能为空");
            for (int i = 0; i < ja.size(); i++)
            {
                JSONObject jo = ja.getJSONObject(i);
                Assert.isTrue(jo.containsKey("cellSequenceNumber"), "cellSequenceNumber分频序号不能为空");
                Assert.isTrue(jo.containsKey("operation"), "operation分频操作类型不能为空");
                Assert.isTrue(jo.containsKey("isFixed"), "isFixed分频固定类型不能为空");

                BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen = new BusiTemplateConferenceDefaultViewCellScreen();
                busiTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
                busiTemplateConferenceDefaultViewCellScreen.setOperation(CellScreenAttendeeOperation.convert(jo.getInteger("operation")).getValue());
                busiTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
                if (cellScreen == null)
                {
                    throw new SystemException(1008767, "找不到分屏信息：" + busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
                }

                // 设置分屏操作
                cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiTemplateConferenceDefaultViewCellScreen.getOperation()));
                cellScreen.setFixed(YesOrNo.convert(busiTemplateConferenceDefaultViewCellScreen.getIsFixed()));
                cellScreen.setSerialNumber(busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
            }
        }

        Comparator<JSONObject> c = new Comparator<JSONObject>()
        {
            public int compare(JSONObject o1, JSONObject o2)
            {
                return o2.getInteger("weight").compareTo(o1.getInteger("weight"));
            }
        };

        List<JSONObject> defaultViewDeptJSONObjects = new ArrayList<>();
        JSONArray defaultViewDepts = jsonObj.getJSONArray("defaultViewDepts");
        if (defaultViewDepts != null)
        {
            for (int i = 0; i < defaultViewDepts.size(); i++)
            {
                JSONObject jo = defaultViewDepts.getJSONObject(i);
                Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
                Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
                defaultViewDeptJSONObjects.add(jo);
            }
            defaultViewDeptJSONObjects.sort(c);
        }

        Map<Integer, List<JSONObject>> busiTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
        Map<Long, List<JSONObject>> deptDefaultViewPaticipantsMap = new HashMap<>();
        ja = jsonObj.getJSONArray("defaultViewPaticipants");
        if (ja != null)
        {
            for (int i = 0; i < ja.size(); i++)
            {
                JSONObject jo = ja.getJSONObject(i);
                Assert.isTrue(jo.containsKey("participantUuId"), "participantUuId不能为空");
                Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");

                if (jo.getInteger("cellSequenceNumber") != null)
                {
                    List<JSONObject> busiTemplateConferenceDefaultViewPaticipants = busiTemplateConferenceDefaultViewPaticipantsMap.get(jo.getInteger("cellSequenceNumber"));
                    if (busiTemplateConferenceDefaultViewPaticipants == null)
                    {
                        busiTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        busiTemplateConferenceDefaultViewPaticipantsMap.put(jo.getInteger("cellSequenceNumber"), busiTemplateConferenceDefaultViewPaticipants);
                    }
                    busiTemplateConferenceDefaultViewPaticipants.add(jo);
                }
                else
                {
                    long deptId = conferenceContext.getAttendeeById(jo.getString("participantUuId")).getDeptId();
                    List<JSONObject> allBusiTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiTemplateConferenceDefaultViewPaticipants == null)
                    {
                        allBusiTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiTemplateConferenceDefaultViewPaticipants.add(jo);
                }
            }
        }

        busiTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
            v.sort(c);
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(screenNumber - 1);
            for (JSONObject jo : v)
            {
                cellScreen.addAttendee(conferenceContext.getAttendeeById(jo.getString("participantUuId")));
            }
        });

        deptDefaultViewPaticipantsMap.forEach((deptId, v) -> {
            Collections.sort(v, c);
        });

        if (deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()) != null)
        {
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiTemplateConferenceDefaultViewPaticipant) -> {
                attendees.add(conferenceContext.getAttendeeById(busiTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
            });
        }

        defaultViewDeptJSONObjects.forEach((d) -> {
            defaultAttendeeOperation.addDefaultViewDept(d);
            if (deptDefaultViewPaticipantsMap.get(d.getLong("deptId")) != null)
            {
                deptDefaultViewPaticipantsMap.get(d.getLong("deptId")).forEach((busiTemplateConferenceDefaultViewPaticipant)->{
                    attendees.add(conferenceContext.getAttendeeById(busiTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
                });
            }
        });

        checkRepeatAttendee(defaultAttendeeOperation, attendees);

//        if ("ops".equalsIgnoreCase(ExternalConfigCache.getInstance().getRegion())) {
            List<Attendee> attendeesOps = new ArrayList<>();
            if (ja != null && ja.size() > 0) {
                HashSet<String> attendeeIdSet = new HashSet<>();
                for (Attendee attendeeTemp : attendees) {
                    boolean isMasterAttendee = false;
                    Attendee attendeeExist = conferenceContext.getAttendeeById(attendeeTemp.getId());
                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeTemp.getId())) {
                        attendeeExist = conferenceContext.getMasterAttendee();
                        isMasterAttendee = true;
                    }
                    if (attendeeExist != null) {
                        if (!attendeeIdSet.contains(attendeeExist.getId())) {
                            attendeeTemp.setMasterAttendee(isMasterAttendee);
                            attendeesOps.add(attendeeExist);
                            attendeeIdSet.add(attendeeExist.getId());
                        }
                    }
                }
                if (conferenceContext.getMasterAttendee() != null) {
                    Attendee attendeeTemp = conferenceContext.getMasterAttendee();
                    if (!attendeeIdSet.contains(attendeeTemp.getId())) {
                        attendeeTemp.setMasterAttendee(true);
                        attendeesOps.add(attendeeTemp);
                        attendeeIdSet.add(attendeeTemp.getId());
                    }
                }
                if (conferenceContext.getAttendees() != null) {
                    for (Attendee attendeeTemp : conferenceContext.getAttendees()) {
                        if (!attendeeIdSet.contains(attendeeTemp.getId())) {
                            attendeeTemp.setMasterAttendee(false);
                            attendeesOps.add(attendeeTemp);
                            attendeeIdSet.add(attendeeTemp.getId());
                        }
                    }
                }
                conferenceContext.setAttendeesOps(attendeesOps);
//            }
        }
        Boolean streamingLayoutFollow = jsonObj.getBoolean("streamingLayoutFollow");
        Boolean recordingLayoutFollow = jsonObj.getBoolean("recordingLayoutFollow");
        if (streamingLayoutFollow != null) {
            conferenceContext.setStreamingLayoutFollow(streamingLayoutFollow);
        }
        if (recordingLayoutFollow != null) {
            conferenceContext.setRecordingLayoutFollow(recordingLayoutFollow);
        }
        if (streamingLayoutFollow != null && streamingLayoutFollow) {
            conferenceContext.setStreamingCustomsLayout(false);
        }
        if (recordingLayoutFollow != null && recordingLayoutFollow) {
            conferenceContext.setRecordingCustomsLayout(false);
        }

        // 设置默认操作的参会者
        defaultAttendeeOperation.setAttendees(attendees);
        conferenceContext.setDefaultViewOperation(defaultAttendeeOperation);
        conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
        AttendeeOperation old = conferenceContext.getAttendeeOperation();
        old.cancel(defaultAttendeeOperation);
        conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
    }

    @Override
    public JSONObject defaultViewData(String conferenceId)
    {
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        Assert.notNull(conferenceContext, "会议没开始，无法查看显示布局！");
        JSONObject json = new JSONObject();
        json.put("upDeptId", conferenceContext.getDeptId());
        List<Attendee> as = new ArrayList<>(conferenceContext.getAttendees());
        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getDeptId() == conferenceContext.getDeptId().longValue())
        {
            as.add(conferenceContext.getMasterAttendee());
        }
        Collections.sort(as);
        json.put("upDeptAttendees", as);

        List<JSONObject> nextDeptAttendees = new ArrayList<>();
        Map<Long, JSONObject> deptAttendeesMap = new HashMap<>();
        for (Iterator<String> iterator = conferenceContext.getMasterAttendeeIdSet().iterator(); iterator.hasNext();)
        {
            String attendeeId = iterator.next();
            Attendee a = conferenceContext.getAttendeeById(attendeeId);
            Set<Attendee> as0 = new HashSet<>();
            as0.add(a);
            List<Attendee> as00 = conferenceContext.getCascadeAttendeesMap().get(a.getDeptId());
            if (as00 != null)
            {
                as0.addAll(as00);
            }
            as = new ArrayList<>(as0);

            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getDeptId() == a.getDeptId() && conferenceContext.getMasterAttendee() != a)
            {
                as.add(conferenceContext.getMasterAttendee());
            }

            Collections.sort(as);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("deptId", a.getDeptId());
            jsonObj.put("attendees", as);
            deptAttendeesMap.put(a.getDeptId(), jsonObj);
        }

        for (Iterator<DeptWeight> iterator = conferenceContext.getDeptWeights().iterator(); iterator.hasNext();)
        {
            nextDeptAttendees.add(deptAttendeesMap.get(iterator.next().getDeptId()));
        }

        json.put("nextDeptAttendees", nextDeptAttendees);

        DefaultViewOperation defaultViewOperation = conferenceContext.getDefaultViewOperation();
        List<Attendee> attendees = defaultViewOperation.getAttendees();
        if (CollectionUtils.isNotEmpty(attendees)) {
            for (int i = attendees.size() - 1; i >= 0; i--) {
                attendees.get(i).setWeight(i + 1);
            }
        }
        json.put("defaultAttendeeOperation", conferenceContext.getDefaultViewOperation());
        json.put("streamingLayoutFollow", conferenceContext.isStreamingLayoutFollow());
        json.put("recordingLayoutFollow", conferenceContext.isRecordingLayoutFollow());
        return json;
    }

    private void checkRepeatAttendee(DefaultAttendeeOperation defaultAttendeeOperation, List<Attendee> attendees)
    {
        Set<String> ids = new HashSet<>();
        if (defaultAttendeeOperation.getSplitScreen() != null) {
            for (CellScreen cellScreen : defaultAttendeeOperation.getSplitScreen().getCellScreens()) {
                if (!ObjectUtils.isEmpty(cellScreen.getAttendees())) {
                    for (Attendee a : cellScreen.getAttendees()) {
                        if (!ids.add(a.getId())) {
                            throw new SystemException(1005347, "显示布局所选参会者存在重复：" + a.getName());
                        }
                    }
                }
            }
        }

        for (Attendee a : attendees)
        {
            if (!ids.add(a.getId()))
            {
                throw new SystemException(1005347, "显示布局所选参会者存在重复：" + a.getName());
            }
        }
    }
}
