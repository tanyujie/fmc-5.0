/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultAttendeeOperationPackageServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-04-12 18:48
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.kdc.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.CellScreenAttendeeOperation;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuKdcTemplateConferenceDefaultViewCellScreenMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuKdcTemplateConferenceDefaultViewDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuKdcTemplateConferenceDefaultViewPaticipantMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.DeptWeight;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.kdc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IDefaultAttendeeOperationPackageForMcuKdcService;
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
public class DefaultAttendeeOperationPackageServiceForMcuKdcImpl implements IDefaultAttendeeOperationPackageForMcuKdcService
{

    @Resource
    private BusiMcuKdcTemplateConferenceDefaultViewCellScreenMapper busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuKdcTemplateConferenceDefaultViewDeptMapper busiMcuKdcTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuKdcTemplateConferenceDefaultViewPaticipantMapper busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper;

    @Override
    public DefaultAttendeeOperation packing(McuKdcConferenceContext conferenceContext, BusiMcuKdcTemplateConference tc, Map<Long, BusiMcuKdcTemplateParticipant> busiMcuKdcTemplateParticipantMap)
    {
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(tc.getDefaultViewLayout());
        defaultAttendeeOperation.setDefaultViewIsBroadcast(tc.getDefaultViewIsBroadcast());
        defaultAttendeeOperation.setDefaultViewIsFill(tc.getDefaultViewIsFill());
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(tc.getDefaultViewIsDisplaySelf());
        defaultAttendeeOperation.setDefaultViewPollingInterval(tc.getPollingInterval());
        defaultAttendeeOperation.initSplitScreen();
        BusiMcuKdcTemplateConferenceDefaultViewCellScreen con = new BusiMcuKdcTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con.setType(1);
        List<BusiMcuKdcTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuKdcTemplateConferenceDefaultViewCellScreenList(con);
        for (BusiMcuKdcTemplateConferenceDefaultViewCellScreen busiMcuKdcTemplateConferenceDefaultViewCellScreen : defaultViewCellScreens)
        {
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuKdcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
            if (cellScreen == null)
            {
                throw new SystemException(1008767, "找不到分屏信息：" + busiMcuKdcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
            }
            cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuKdcTemplateConferenceDefaultViewCellScreen.getOperation()));
            cellScreen.setFixed(YesOrNo.convert(busiMcuKdcTemplateConferenceDefaultViewCellScreen.getIsFixed()));
            cellScreen.setSerialNumber(busiMcuKdcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
        }

        // 部门信息
        BusiMcuKdcTemplateConferenceDefaultViewDept con1 = new BusiMcuKdcTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con1.setType(1);
        List<BusiMcuKdcTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuKdcTemplateConferenceDefaultViewDeptMapper.selectBusiMcuKdcTemplateConferenceDefaultViewDeptList(con1);
        Collections.sort(defaultViewDepts, new Comparator<BusiMcuKdcTemplateConferenceDefaultViewDept>()
        {
            public int compare(BusiMcuKdcTemplateConferenceDefaultViewDept o1, BusiMcuKdcTemplateConferenceDefaultViewDept o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        BusiMcuKdcTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuKdcTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con2.setType(1);
        List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuKdcTemplateConferenceDefaultViewPaticipantList(con2);
        Map<Integer, List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant>> busiMcuKdcTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
        Map<Long, List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant>> deptDefaultViewPaticipantsMap = new HashMap<>();
        for (BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiMcuKdcTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants)
        {
            if (busiMcuKdcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber() != null)
            {
                List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant> busiMcuKdcTemplateConferenceDefaultViewPaticipants = busiMcuKdcTemplateConferenceDefaultViewPaticipantsMap.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                if (busiMcuKdcTemplateConferenceDefaultViewPaticipants == null)
                {
                    busiMcuKdcTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                    busiMcuKdcTemplateConferenceDefaultViewPaticipantsMap.put(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber(), busiMcuKdcTemplateConferenceDefaultViewPaticipants);
                }
                busiMcuKdcTemplateConferenceDefaultViewPaticipants.add(busiMcuKdcTemplateConferenceDefaultViewPaticipant);
            }
            else {
                BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant = busiMcuKdcTemplateParticipantMap.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuKdcTemplateParticipant)) {

                    long deptId = conferenceContext.getAttendeeById(busiMcuKdcTemplateParticipant.getUuid()).getDeptId();
                    List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant> allBusiMcuKdcTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuKdcTemplateConferenceDefaultViewPaticipants == null) {
                        allBusiMcuKdcTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuKdcTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuKdcTemplateConferenceDefaultViewPaticipants.add(busiMcuKdcTemplateConferenceDefaultViewPaticipant);
                }

            }
        }

        Comparator<BusiMcuKdcTemplateConferenceDefaultViewPaticipant> c = new Comparator<BusiMcuKdcTemplateConferenceDefaultViewPaticipant>()
        {
            @Override
            public int compare(BusiMcuKdcTemplateConferenceDefaultViewPaticipant o1, BusiMcuKdcTemplateConferenceDefaultViewPaticipant o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        };

        busiMcuKdcTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
            Collections.sort(v, c);
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(screenNumber - 1);
            for (BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiMcuKdcTemplateConferenceDefaultViewPaticipant : v)
            {
                BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant = busiMcuKdcTemplateParticipantMap.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuKdcTemplateParticipant)) {
                    cellScreen.addAttendee(conferenceContext.getAttendeeById(busiMcuKdcTemplateParticipantMap.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                }
            }
        });

        deptDefaultViewPaticipantsMap.forEach((deptId, v) -> {
            Collections.sort(v, c);
        });

        List<AttendeeForMcuKdc> attendees = new ArrayList<>();
        if (deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()) != null) {
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuKdcTemplateConferenceDefaultViewPaticipant) -> {
                BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant = busiMcuKdcTemplateParticipantMap.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuKdcTemplateParticipant)) {
                    attendees.add(conferenceContext.getAttendeeById(busiMcuKdcTemplateParticipantMap.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
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
                deptDefaultViewPaticipantsMap.get(d.getDeptId()).forEach((busiMcuKdcTemplateConferenceDefaultViewPaticipant)->{
                    BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant = busiMcuKdcTemplateParticipantMap.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                    if (!Objects.isNull(busiMcuKdcTemplateParticipant)) {
                        attendees.add(conferenceContext.getAttendeeById(busiMcuKdcTemplateParticipantMap.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
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
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);

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

        List<AttendeeForMcuKdc> attendees = new ArrayList<>();
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

                BusiMcuKdcTemplateConferenceDefaultViewCellScreen busiMcuKdcTemplateConferenceDefaultViewCellScreen = new BusiMcuKdcTemplateConferenceDefaultViewCellScreen();
                busiMcuKdcTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
                busiMcuKdcTemplateConferenceDefaultViewCellScreen.setOperation(CellScreenAttendeeOperation.convert(jo.getInteger("operation")).getValue());
                busiMcuKdcTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuKdcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
                if (cellScreen == null)
                {
                    throw new SystemException(1008767, "找不到分屏信息：" + busiMcuKdcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
                }

                // 设置分屏操作
                cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuKdcTemplateConferenceDefaultViewCellScreen.getOperation()));
                cellScreen.setFixed(YesOrNo.convert(busiMcuKdcTemplateConferenceDefaultViewCellScreen.getIsFixed()));
                cellScreen.setSerialNumber(busiMcuKdcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
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

        Map<Integer, List<JSONObject>> busiMcuKdcTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
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
                    List<JSONObject> busiMcuKdcTemplateConferenceDefaultViewPaticipants = busiMcuKdcTemplateConferenceDefaultViewPaticipantsMap.get(jo.getInteger("cellSequenceNumber"));
                    if (busiMcuKdcTemplateConferenceDefaultViewPaticipants == null)
                    {
                        busiMcuKdcTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        busiMcuKdcTemplateConferenceDefaultViewPaticipantsMap.put(jo.getInteger("cellSequenceNumber"), busiMcuKdcTemplateConferenceDefaultViewPaticipants);
                    }
                    busiMcuKdcTemplateConferenceDefaultViewPaticipants.add(jo);
                }
                else
                {
                    long deptId = conferenceContext.getAttendeeById(jo.getString("participantUuId")).getDeptId();
                    List<JSONObject> allBusiMcuKdcTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuKdcTemplateConferenceDefaultViewPaticipants == null)
                    {
                        allBusiMcuKdcTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuKdcTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuKdcTemplateConferenceDefaultViewPaticipants.add(jo);
                }
            }
        }

        busiMcuKdcTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
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
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuKdcTemplateConferenceDefaultViewPaticipant) -> {
                attendees.add(conferenceContext.getAttendeeById(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
            });
        }

        defaultViewDeptJSONObjects.forEach((d) -> {
            defaultAttendeeOperation.addDefaultViewDept(d);
            if (deptDefaultViewPaticipantsMap.get(d.getLong("deptId")) != null)
            {
                deptDefaultViewPaticipantsMap.get(d.getLong("deptId")).forEach((busiMcuKdcTemplateConferenceDefaultViewPaticipant)->{
                    attendees.add(conferenceContext.getAttendeeById(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
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
        McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
        Assert.notNull(conferenceContext, "会议没开始，无法查看显示布局！");
        JSONObject json = new JSONObject();
        json.put("upDeptId", conferenceContext.getDeptId());
        List<AttendeeForMcuKdc> as = new ArrayList<>(conferenceContext.getAttendees());
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
            AttendeeForMcuKdc a = conferenceContext.getAttendeeById(attendeeId);
            Set<AttendeeForMcuKdc> as0 = new HashSet<>();
            as0.add(a);
            List<AttendeeForMcuKdc> as00 = conferenceContext.getCascadeAttendeesMap().get(a.getDeptId());
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

    private void checkRepeatAttendee(DefaultAttendeeOperation defaultAttendeeOperation, List<AttendeeForMcuKdc> attendees)
    {
        Set<String> ids = new HashSet<>();
        if (defaultAttendeeOperation.getSplitScreen() != null) {
            for (CellScreen cellScreen : defaultAttendeeOperation.getSplitScreen().getCellScreens()) {
                if (!ObjectUtils.isEmpty(cellScreen.getAttendees())) {
                    for (AttendeeForMcuKdc a : cellScreen.getAttendees()) {
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

        for (AttendeeForMcuKdc a : attendees)
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
