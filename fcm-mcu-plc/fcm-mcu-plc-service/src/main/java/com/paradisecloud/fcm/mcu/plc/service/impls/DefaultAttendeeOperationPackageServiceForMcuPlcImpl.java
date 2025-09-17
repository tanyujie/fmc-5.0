/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultAttendeeOperationPackageServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-04-12 18:48
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.plc.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.CellScreenAttendeeOperation;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcTemplateConferenceDefaultViewCellScreenMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcTemplateConferenceDefaultViewDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcTemplateConferenceDefaultViewPaticipantMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.plc.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.DeptWeight;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.plc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IDefaultAttendeeOperationPackageForMcuPlcService;
import com.sinhy.exception.SystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <pre>默认参会者操作对象封装器实现类</pre>
 * @author lilinhai
 * @since 2021-04-12 18:48
 * @version V1.0
 */
@Transactional
@Service
public class DefaultAttendeeOperationPackageServiceForMcuPlcImpl implements IDefaultAttendeeOperationPackageForMcuPlcService
{

    @Resource
    private BusiMcuPlcTemplateConferenceDefaultViewCellScreenMapper busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuPlcTemplateConferenceDefaultViewDeptMapper busiMcuPlcTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuPlcTemplateConferenceDefaultViewPaticipantMapper busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper;

    @Override
    public DefaultAttendeeOperation packing(McuPlcConferenceContext conferenceContext, BusiMcuPlcTemplateConference tc, Map<Long, BusiMcuPlcTemplateParticipant> busiMcuPlcTemplateParticipantMap)
    {
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(tc.getDefaultViewLayout());
        defaultAttendeeOperation.setDefaultViewIsBroadcast(tc.getDefaultViewIsBroadcast());
        defaultAttendeeOperation.setDefaultViewIsFill(tc.getDefaultViewIsFill());
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(tc.getDefaultViewIsDisplaySelf());
        defaultAttendeeOperation.setDefaultViewPollingInterval(tc.getPollingInterval());
        defaultAttendeeOperation.initSplitScreen();
        BusiMcuPlcTemplateConferenceDefaultViewCellScreen con = new BusiMcuPlcTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con.setType(1);
        List<BusiMcuPlcTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuPlcTemplateConferenceDefaultViewCellScreenList(con);
        for (BusiMcuPlcTemplateConferenceDefaultViewCellScreen busiMcuPlcTemplateConferenceDefaultViewCellScreen : defaultViewCellScreens)
        {
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuPlcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
            if (cellScreen == null)
            {
                throw new SystemException(1008767, "找不到分屏信息：" + busiMcuPlcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
            }
            cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuPlcTemplateConferenceDefaultViewCellScreen.getOperation()));
            cellScreen.setFixed(YesOrNo.convert(busiMcuPlcTemplateConferenceDefaultViewCellScreen.getIsFixed()));
            cellScreen.setSerialNumber(busiMcuPlcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
        }

        // 部门信息
        BusiMcuPlcTemplateConferenceDefaultViewDept con1 = new BusiMcuPlcTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con1.setType(1);
        List<BusiMcuPlcTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuPlcTemplateConferenceDefaultViewDeptMapper.selectBusiMcuPlcTemplateConferenceDefaultViewDeptList(con1);
        Collections.sort(defaultViewDepts, new Comparator<BusiMcuPlcTemplateConferenceDefaultViewDept>()
        {
            public int compare(BusiMcuPlcTemplateConferenceDefaultViewDept o1, BusiMcuPlcTemplateConferenceDefaultViewDept o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        BusiMcuPlcTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuPlcTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con2.setType(1);
        List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuPlcTemplateConferenceDefaultViewPaticipantList(con2);
        Map<Integer, List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant>> busiMcuPlcTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
        Map<Long, List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant>> deptDefaultViewPaticipantsMap = new HashMap<>();
        for (BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiMcuPlcTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants)
        {
            if (busiMcuPlcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber() != null)
            {
                List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant> busiMcuPlcTemplateConferenceDefaultViewPaticipants = busiMcuPlcTemplateConferenceDefaultViewPaticipantsMap.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                if (busiMcuPlcTemplateConferenceDefaultViewPaticipants == null)
                {
                    busiMcuPlcTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                    busiMcuPlcTemplateConferenceDefaultViewPaticipantsMap.put(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber(), busiMcuPlcTemplateConferenceDefaultViewPaticipants);
                }
                busiMcuPlcTemplateConferenceDefaultViewPaticipants.add(busiMcuPlcTemplateConferenceDefaultViewPaticipant);
            }
            else {
                BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant = busiMcuPlcTemplateParticipantMap.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuPlcTemplateParticipant)) {

                    long deptId = conferenceContext.getAttendeeById(busiMcuPlcTemplateParticipant.getUuid()).getDeptId();
                    List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant> allBusiMcuPlcTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuPlcTemplateConferenceDefaultViewPaticipants == null) {
                        allBusiMcuPlcTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuPlcTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuPlcTemplateConferenceDefaultViewPaticipants.add(busiMcuPlcTemplateConferenceDefaultViewPaticipant);
                }

            }
        }

        Comparator<BusiMcuPlcTemplateConferenceDefaultViewPaticipant> c = new Comparator<BusiMcuPlcTemplateConferenceDefaultViewPaticipant>()
        {
            @Override
            public int compare(BusiMcuPlcTemplateConferenceDefaultViewPaticipant o1, BusiMcuPlcTemplateConferenceDefaultViewPaticipant o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        };

        busiMcuPlcTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
            Collections.sort(v, c);
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(screenNumber - 1);
            for (BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiMcuPlcTemplateConferenceDefaultViewPaticipant : v)
            {
                BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant = busiMcuPlcTemplateParticipantMap.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuPlcTemplateParticipant)) {
                    cellScreen.addAttendee(conferenceContext.getAttendeeById(busiMcuPlcTemplateParticipantMap.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                }
            }
        });

        deptDefaultViewPaticipantsMap.forEach((deptId, v) -> {
            Collections.sort(v, c);
        });

        List<AttendeeForMcuPlc> attendees = new ArrayList<>();
        if (deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()) != null) {
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuPlcTemplateConferenceDefaultViewPaticipant) -> {
                BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant = busiMcuPlcTemplateParticipantMap.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuPlcTemplateParticipant)) {
                    attendees.add(conferenceContext.getAttendeeById(busiMcuPlcTemplateParticipantMap.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
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
                deptDefaultViewPaticipantsMap.get(d.getDeptId()).forEach((busiMcuPlcTemplateConferenceDefaultViewPaticipant)->{
                    BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant = busiMcuPlcTemplateParticipantMap.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                    if (!Objects.isNull(busiMcuPlcTemplateParticipant)) {
                        attendees.add(conferenceContext.getAttendeeById(busiMcuPlcTemplateParticipantMap.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
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
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);

        Assert.notNull(conferenceContext, "会议没开始不能在此编辑默认视图");

        // 参数校验
        Assert.isTrue(jsonObj.containsKey("defaultViewLayout"), "默认视图布局不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsBroadcast"), "默认视图是否广播不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsDisplaySelf"), "默认视图是否显示自己不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsFill"), "默认视图是否补位不能为空");
        Assert.isTrue(jsonObj.containsKey("pollingInterval"), "默认视图的轮询时间间隔不能为空");
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        if (defaultAttendeeOperation.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)) {
            defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        } else {
            defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        }
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        defaultAttendeeOperation.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        defaultAttendeeOperation.setDefaultViewPollingInterval(jsonObj.getInteger("pollingInterval"));
        if (!conferenceContext.isSupportBroadcast()) {
            defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        }
        defaultAttendeeOperation.initSplitScreen();

        List<AttendeeForMcuPlc> attendees = new ArrayList<>();
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

                BusiMcuPlcTemplateConferenceDefaultViewCellScreen busiMcuPlcTemplateConferenceDefaultViewCellScreen = new BusiMcuPlcTemplateConferenceDefaultViewCellScreen();
                busiMcuPlcTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
                busiMcuPlcTemplateConferenceDefaultViewCellScreen.setOperation(CellScreenAttendeeOperation.convert(jo.getInteger("operation")).getValue());
                busiMcuPlcTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuPlcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
                if (cellScreen == null)
                {
                    throw new SystemException(1008767, "找不到分屏信息：" + busiMcuPlcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
                }

                // 设置分屏操作
                cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuPlcTemplateConferenceDefaultViewCellScreen.getOperation()));
                cellScreen.setFixed(YesOrNo.convert(busiMcuPlcTemplateConferenceDefaultViewCellScreen.getIsFixed()));
                cellScreen.setSerialNumber(busiMcuPlcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
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

        Map<Integer, List<JSONObject>> busiMcuPlcTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
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
                    List<JSONObject> busiMcuPlcTemplateConferenceDefaultViewPaticipants = busiMcuPlcTemplateConferenceDefaultViewPaticipantsMap.get(jo.getInteger("cellSequenceNumber"));
                    if (busiMcuPlcTemplateConferenceDefaultViewPaticipants == null)
                    {
                        busiMcuPlcTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        busiMcuPlcTemplateConferenceDefaultViewPaticipantsMap.put(jo.getInteger("cellSequenceNumber"), busiMcuPlcTemplateConferenceDefaultViewPaticipants);
                    }
                    busiMcuPlcTemplateConferenceDefaultViewPaticipants.add(jo);
                }
                else
                {
                    long deptId = conferenceContext.getAttendeeById(jo.getString("participantUuId")).getDeptId();
                    List<JSONObject> allBusiMcuPlcTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuPlcTemplateConferenceDefaultViewPaticipants == null)
                    {
                        allBusiMcuPlcTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuPlcTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuPlcTemplateConferenceDefaultViewPaticipants.add(jo);
                }
            }
        }

        busiMcuPlcTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
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
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuPlcTemplateConferenceDefaultViewPaticipant) -> {
                attendees.add(conferenceContext.getAttendeeById(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
            });
        }

        defaultViewDeptJSONObjects.forEach((d) -> {
            defaultAttendeeOperation.addDefaultViewDept(d);
            if (deptDefaultViewPaticipantsMap.get(d.getLong("deptId")) != null)
            {
                deptDefaultViewPaticipantsMap.get(d.getLong("deptId")).forEach((busiMcuPlcTemplateConferenceDefaultViewPaticipant)->{
                    attendees.add(conferenceContext.getAttendeeById(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
                });
            }
        });

        checkRepeatAttendee(defaultAttendeeOperation, attendees);

        // 设置默认操作的参会者
        defaultAttendeeOperation.setAttendees(attendees);
        conferenceContext.setDefaultViewOperation(defaultAttendeeOperation);
        if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
        }
        AttendeeOperation old = conferenceContext.getAttendeeOperation();
        old.cancel();
        conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
    }

    @Override
    public JSONObject defaultViewData(String conferenceId)
    {
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        Assert.notNull(conferenceContext, "会议没开始，无法查看显示布局！");
        JSONObject json = new JSONObject();
        json.put("upDeptId", conferenceContext.getDeptId());
        List<AttendeeForMcuPlc> as = new ArrayList<>(conferenceContext.getAttendees());
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
            AttendeeForMcuPlc a = conferenceContext.getAttendeeById(attendeeId);
            Set<AttendeeForMcuPlc> as0 = new HashSet<>();
            as0.add(a);
            List<AttendeeForMcuPlc> as00 = conferenceContext.getCascadeAttendeesMap().get(a.getDeptId());
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
        json.put("defaultAttendeeOperation", conferenceContext.getDefaultViewOperation());
        return json;
    }

    private void checkRepeatAttendee(DefaultAttendeeOperation defaultAttendeeOperation, List<AttendeeForMcuPlc> attendees)
    {
        Set<String> ids = new HashSet<>();
        if (defaultAttendeeOperation.getSplitScreen() != null) {
            for (CellScreen cellScreen : defaultAttendeeOperation.getSplitScreen().getCellScreens()) {
                if (!ObjectUtils.isEmpty(cellScreen.getAttendees())) {
                    for (AttendeeForMcuPlc a : cellScreen.getAttendees()) {
                        if (a == null) {
                            throw new SystemException(1005347, "显示布局所选参会者已离会");
                        }
                        if (!ids.add(a.getId())) {
                            throw new SystemException(1005347, "显示布局所选参会者存在重复：" + a.getName());
                        }
                    }
                }
            }
        }

        for (AttendeeForMcuPlc a : attendees)
        {
            if (a == null) {
                throw new SystemException(1005347, "显示布局所选参会者已离会");
            }
            if (!ids.add(a.getId()))
            {
                throw new SystemException(1005347, "显示布局所选参会者存在重复：" + a.getName());
            }
        }
    }

}
