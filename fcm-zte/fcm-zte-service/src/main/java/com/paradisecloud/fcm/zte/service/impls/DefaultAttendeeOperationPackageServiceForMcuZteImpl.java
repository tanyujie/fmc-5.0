/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultAttendeeOperationPackageServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-04-12 18:48
 * @version  V1.0
 */
package com.paradisecloud.fcm.zte.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.CellScreenAttendeeOperation;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteTemplateConferenceDefaultViewCellScreenMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteTemplateConferenceDefaultViewDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteTemplateConferenceDefaultViewPaticipantMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.zte.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.model.DeptWeight;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.zte.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.zte.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.zte.service.interfaces.IDefaultAttendeeOperationPackageForMcuZteService;
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
public class DefaultAttendeeOperationPackageServiceForMcuZteImpl implements IDefaultAttendeeOperationPackageForMcuZteService
{

    @Resource
    private BusiMcuZteTemplateConferenceDefaultViewCellScreenMapper busiMcuZteTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuZteTemplateConferenceDefaultViewDeptMapper busiMcuZteTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuZteTemplateConferenceDefaultViewPaticipantMapper busiMcuZteTemplateConferenceDefaultViewPaticipantMapper;

    @Override
    public DefaultAttendeeOperation packing(McuZteConferenceContext conferenceContext, BusiMcuZteTemplateConference tc, Map<Long, BusiMcuZteTemplateParticipant> busiMcuZteTemplateParticipantMap)
    {
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(tc.getDefaultViewLayout());
        defaultAttendeeOperation.setDefaultViewIsBroadcast(tc.getDefaultViewIsBroadcast());
        defaultAttendeeOperation.setDefaultViewIsFill(tc.getDefaultViewIsFill());
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(tc.getDefaultViewIsDisplaySelf());
        defaultAttendeeOperation.setDefaultViewPollingInterval(tc.getPollingInterval());
        defaultAttendeeOperation.initSplitScreen();
        BusiMcuZteTemplateConferenceDefaultViewCellScreen con = new BusiMcuZteTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con.setType(1);
        List<BusiMcuZteTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuZteTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuZteTemplateConferenceDefaultViewCellScreenList(con);
        for (BusiMcuZteTemplateConferenceDefaultViewCellScreen busiMcuZteTemplateConferenceDefaultViewCellScreen : defaultViewCellScreens)
        {
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuZteTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
            if (cellScreen == null)
            {
                throw new SystemException(1008767, "找不到分屏信息：" + busiMcuZteTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
            }
            cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuZteTemplateConferenceDefaultViewCellScreen.getOperation()));
            cellScreen.setFixed(YesOrNo.convert(busiMcuZteTemplateConferenceDefaultViewCellScreen.getIsFixed()));
            cellScreen.setSerialNumber(busiMcuZteTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
        }

        // 部门信息
        BusiMcuZteTemplateConferenceDefaultViewDept con1 = new BusiMcuZteTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con1.setType(1);
        List<BusiMcuZteTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuZteTemplateConferenceDefaultViewDeptMapper.selectBusiMcuZteTemplateConferenceDefaultViewDeptList(con1);
        Collections.sort(defaultViewDepts, new Comparator<BusiMcuZteTemplateConferenceDefaultViewDept>()
        {
            public int compare(BusiMcuZteTemplateConferenceDefaultViewDept o1, BusiMcuZteTemplateConferenceDefaultViewDept o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        BusiMcuZteTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuZteTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con2.setType(1);
        List<BusiMcuZteTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuZteTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZteTemplateConferenceDefaultViewPaticipantList(con2);
        Map<Integer, List<BusiMcuZteTemplateConferenceDefaultViewPaticipant>> busiMcuZteTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
        Map<Long, List<BusiMcuZteTemplateConferenceDefaultViewPaticipant>> deptDefaultViewPaticipantsMap = new HashMap<>();
        for (BusiMcuZteTemplateConferenceDefaultViewPaticipant busiMcuZteTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants)
        {
            if (busiMcuZteTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber() != null)
            {
                List<BusiMcuZteTemplateConferenceDefaultViewPaticipant> busiMcuZteTemplateConferenceDefaultViewPaticipants = busiMcuZteTemplateConferenceDefaultViewPaticipantsMap.get(busiMcuZteTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                if (busiMcuZteTemplateConferenceDefaultViewPaticipants == null)
                {
                    busiMcuZteTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                    busiMcuZteTemplateConferenceDefaultViewPaticipantsMap.put(busiMcuZteTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber(), busiMcuZteTemplateConferenceDefaultViewPaticipants);
                }
                busiMcuZteTemplateConferenceDefaultViewPaticipants.add(busiMcuZteTemplateConferenceDefaultViewPaticipant);
            }
            else {
                BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant = busiMcuZteTemplateParticipantMap.get(busiMcuZteTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuZteTemplateParticipant)) {

                    long deptId = conferenceContext.getAttendeeById(busiMcuZteTemplateParticipant.getUuid()).getDeptId();
                    List<BusiMcuZteTemplateConferenceDefaultViewPaticipant> allBusiMcuZteTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuZteTemplateConferenceDefaultViewPaticipants == null) {
                        allBusiMcuZteTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuZteTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuZteTemplateConferenceDefaultViewPaticipants.add(busiMcuZteTemplateConferenceDefaultViewPaticipant);
                }

            }
        }

        Comparator<BusiMcuZteTemplateConferenceDefaultViewPaticipant> c = new Comparator<BusiMcuZteTemplateConferenceDefaultViewPaticipant>()
        {
            @Override
            public int compare(BusiMcuZteTemplateConferenceDefaultViewPaticipant o1, BusiMcuZteTemplateConferenceDefaultViewPaticipant o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        };

        busiMcuZteTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
            Collections.sort(v, c);
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(screenNumber - 1);
            for (BusiMcuZteTemplateConferenceDefaultViewPaticipant busiMcuZteTemplateConferenceDefaultViewPaticipant : v)
            {
                BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant = busiMcuZteTemplateParticipantMap.get(busiMcuZteTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuZteTemplateParticipant)) {
                    cellScreen.addAttendee(conferenceContext.getAttendeeById(busiMcuZteTemplateParticipantMap.get(busiMcuZteTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                }
            }
        });

        deptDefaultViewPaticipantsMap.forEach((deptId, v) -> {
            Collections.sort(v, c);
        });

        List<AttendeeForMcuZte> attendees = new ArrayList<>();
        if (deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()) != null) {
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuZteTemplateConferenceDefaultViewPaticipant) -> {
                BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant = busiMcuZteTemplateParticipantMap.get(busiMcuZteTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuZteTemplateParticipant)) {
                    attendees.add(conferenceContext.getAttendeeById(busiMcuZteTemplateParticipantMap.get(busiMcuZteTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
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
                deptDefaultViewPaticipantsMap.get(d.getDeptId()).forEach((busiMcuZteTemplateConferenceDefaultViewPaticipant)->{
                    BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant = busiMcuZteTemplateParticipantMap.get(busiMcuZteTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                    if (!Objects.isNull(busiMcuZteTemplateParticipant)) {
                        attendees.add(conferenceContext.getAttendeeById(busiMcuZteTemplateParticipantMap.get(busiMcuZteTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);

        Assert.notNull(conferenceContext, "会议没开始不能在此编辑默认视图");

        // 参数校验
        Assert.isTrue(jsonObj.containsKey("defaultViewLayout"), "默认视图布局不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsBroadcast"), "默认视图是否广播不能为空");
//        Assert.isTrue(jsonObj.containsKey("defaultViewIsDisplaySelf"), "默认视图是否显示自己不能为空");
//        Assert.isTrue(jsonObj.containsKey("defaultViewIsFill"), "默认视图是否补位不能为空");
        Assert.isTrue(jsonObj.containsKey("pollingInterval"), "默认视图的轮询时间间隔不能为空");
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        if (defaultAttendeeOperation.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)) {
            defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        } else {
            defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        }
        defaultAttendeeOperation.setDefaultViewIsBroadcast(jsonObj.getInteger("defaultViewIsBroadcast"));
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(1);
        defaultAttendeeOperation.setDefaultViewIsFill(1);
        defaultAttendeeOperation.setDefaultViewPollingInterval(jsonObj.getInteger("pollingInterval"));
        if (!conferenceContext.isSupportBroadcast()) {
            defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        }
        defaultAttendeeOperation.initSplitScreen();

        List<AttendeeForMcuZte> attendees = new ArrayList<>();
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


                BusiMcuZteTemplateConferenceDefaultViewCellScreen busiMcuZteTemplateConferenceDefaultViewCellScreen = new BusiMcuZteTemplateConferenceDefaultViewCellScreen();
                busiMcuZteTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
                busiMcuZteTemplateConferenceDefaultViewCellScreen.setOperation(CellScreenAttendeeOperation.convert(jo.getInteger("operation")).getValue());
                busiMcuZteTemplateConferenceDefaultViewCellScreen.setIsFixed(1);

                CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuZteTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
                if (cellScreen == null)
                {
                    throw new SystemException(1008767, "找不到分屏信息：" + busiMcuZteTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
                }

                // 设置分屏操作
                cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuZteTemplateConferenceDefaultViewCellScreen.getOperation()));
                cellScreen.setFixed(YesOrNo.YES);
                cellScreen.setSerialNumber(busiMcuZteTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
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

        Map<Integer, List<JSONObject>> busiMcuZteTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
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
                    List<JSONObject> busiMcuZteTemplateConferenceDefaultViewPaticipants = busiMcuZteTemplateConferenceDefaultViewPaticipantsMap.get(jo.getInteger("cellSequenceNumber"));
                    if (busiMcuZteTemplateConferenceDefaultViewPaticipants == null)
                    {
                        busiMcuZteTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        busiMcuZteTemplateConferenceDefaultViewPaticipantsMap.put(jo.getInteger("cellSequenceNumber"), busiMcuZteTemplateConferenceDefaultViewPaticipants);
                    }
                    busiMcuZteTemplateConferenceDefaultViewPaticipants.add(jo);
                }
                else
                {
                    long deptId = conferenceContext.getAttendeeById(jo.getString("participantUuId")).getDeptId();
                    List<JSONObject> allBusiMcuZteTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuZteTemplateConferenceDefaultViewPaticipants == null)
                    {
                        allBusiMcuZteTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuZteTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuZteTemplateConferenceDefaultViewPaticipants.add(jo);
                }
            }
        }

        busiMcuZteTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
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
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuZteTemplateConferenceDefaultViewPaticipant) -> {
                attendees.add(conferenceContext.getAttendeeById(busiMcuZteTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
            });
        }

        defaultViewDeptJSONObjects.forEach((d) -> {
            defaultAttendeeOperation.addDefaultViewDept(d);
            if (deptDefaultViewPaticipantsMap.get(d.getLong("deptId")) != null)
            {
                deptDefaultViewPaticipantsMap.get(d.getLong("deptId")).forEach((busiMcuZteTemplateConferenceDefaultViewPaticipant)->{
                    attendees.add(conferenceContext.getAttendeeById(busiMcuZteTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        Assert.notNull(conferenceContext, "会议没开始，无法查看显示布局！");
        JSONObject json = new JSONObject();
        json.put("upDeptId", conferenceContext.getDeptId());
        List<AttendeeForMcuZte> as = new ArrayList<>(conferenceContext.getAttendees());
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
            AttendeeForMcuZte a = conferenceContext.getAttendeeById(attendeeId);
            Set<AttendeeForMcuZte> as0 = new HashSet<>();
            as0.add(a);
            List<AttendeeForMcuZte> as00 = conferenceContext.getCascadeAttendeesMap().get(a.getDeptId());
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

    private void checkRepeatAttendee(DefaultAttendeeOperation defaultAttendeeOperation, List<AttendeeForMcuZte> attendees)
    {
        Set<String> ids = new HashSet<>();
        if (defaultAttendeeOperation.getSplitScreen() != null) {
            for (CellScreen cellScreen : defaultAttendeeOperation.getSplitScreen().getCellScreens()) {
                if (!ObjectUtils.isEmpty(cellScreen.getAttendees())) {
                    for (AttendeeForMcuZte a : cellScreen.getAttendees()) {
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

        for (AttendeeForMcuZte a : attendees)
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
