/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultAttendeeOperationPackageServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-04-12 18:48
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.setvice2.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.CellScreenAttendeeOperation;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AllEqualSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OnePlusNSplitScreen;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2TemplateConferenceDefaultViewDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.DefaultViewOperation;
import com.paradisecloud.fcm.smc2.model.DeptWeight;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.layout.CellScreen;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IDefaultAttendeeSmc2OperationPackageService;
import com.sinhy.exception.SystemException;
import org.apache.commons.collections.CollectionUtils;
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
public class DefaultAttendeeSmc2OperationPackageServiceImpl implements IDefaultAttendeeSmc2OperationPackageService
{

    @Resource
    private BusiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper busiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuSmc2TemplateConferenceDefaultViewDeptMapper busiMcuSmc2TemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper busiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper;

    @Override
    public DefaultAttendeeOperation packing(Smc2ConferenceContext conferenceContext, BusiMcuSmc2TemplateConference tc, Map<Long, BusiMcuSmc2TemplateParticipant> busiTemplateParticipantMap)
    {
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(tc.getDefaultViewLayout());
        defaultAttendeeOperation.setDefaultViewIsBroadcast(tc.getDefaultViewIsBroadcast());
        defaultAttendeeOperation.setDefaultViewIsFill(tc.getDefaultViewIsFill());
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(tc.getDefaultViewIsDisplaySelf());
        defaultAttendeeOperation.setDefaultViewPollingInterval(tc.getPollingInterval());
        defaultAttendeeOperation.initSplitScreen();
        BusiMcuSmc2TemplateConferenceDefaultViewCellScreen con = new BusiMcuSmc2TemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        List<BusiMcuSmc2TemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuSmc2TemplateConferenceDefaultViewCellScreenList(con);
        for (BusiMcuSmc2TemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen : defaultViewCellScreens)
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
        BusiMcuSmc2TemplateConferenceDefaultViewDept con1 = new BusiMcuSmc2TemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        List<BusiMcuSmc2TemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuSmc2TemplateConferenceDefaultViewDeptMapper.selectBusiMcuSmc2TemplateConferenceDefaultViewDeptList(con1);
        Collections.sort(defaultViewDepts, new Comparator<BusiMcuSmc2TemplateConferenceDefaultViewDept>()
        {
            public int compare(BusiMcuSmc2TemplateConferenceDefaultViewDept o1, BusiMcuSmc2TemplateConferenceDefaultViewDept o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        BusiMcuSmc2TemplateConferenceDefaultViewPaticipant con2 = new BusiMcuSmc2TemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        List<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuSmc2TemplateConferenceDefaultViewPaticipantList(con2);
        Map<Integer, List<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant>> busiTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
        Map<Long, List<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant>> deptDefaultViewPaticipantsMap = new HashMap<>();
        for (BusiMcuSmc2TemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants)
        {
            if (busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber() != null)
            {
                List<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant> busiTemplateConferenceDefaultViewPaticipants = busiTemplateConferenceDefaultViewPaticipantsMap.get(busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                if (busiTemplateConferenceDefaultViewPaticipants == null)
                {
                    busiTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                    busiTemplateConferenceDefaultViewPaticipantsMap.put(busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber(), busiTemplateConferenceDefaultViewPaticipants);
                }
                busiTemplateConferenceDefaultViewPaticipants.add(busiTemplateConferenceDefaultViewPaticipant);
            }
            else {
                BusiMcuSmc2TemplateParticipant busiTemplateParticipant = busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiTemplateParticipant)) {

                    long deptId = conferenceContext.getAttendeeById(busiTemplateParticipant.getUuid()).getDeptId();
                    List<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant> allBusiTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiTemplateConferenceDefaultViewPaticipants == null) {
                        allBusiTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiTemplateConferenceDefaultViewPaticipants.add(busiTemplateConferenceDefaultViewPaticipant);
                }

            }
        }

        Comparator<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant> c = new Comparator<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant>()
        {
            @Override
            public int compare(BusiMcuSmc2TemplateConferenceDefaultViewPaticipant o1, BusiMcuSmc2TemplateConferenceDefaultViewPaticipant o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        };

        busiTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
            Collections.sort(v, c);
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(screenNumber - 1);
            for (BusiMcuSmc2TemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant : v)
            {
                BusiMcuSmc2TemplateParticipant busiTemplateParticipant = busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiTemplateParticipant)) {
                    cellScreen.addAttendee(conferenceContext.getAttendeeById(busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                }
            }
        });

        deptDefaultViewPaticipantsMap.forEach((deptId, v) -> {
            Collections.sort(v, c);
        });

        List<AttendeeSmc2> attendees = new ArrayList<>();
        if (deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()) != null) {
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiTemplateConferenceDefaultViewPaticipant) -> {
                BusiMcuSmc2TemplateParticipant busiTemplateParticipant = busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
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
                    BusiMcuSmc2TemplateParticipant busiTemplateParticipant = busiTemplateParticipantMap.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
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
        final String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        // 会议室上下文实例对象
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);

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

        List<AttendeeSmc2> attendees = new ArrayList<>();
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");
        if (defaultAttendeeOperation.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)
                || defaultAttendeeOperation.getDefaultViewLayout().equals(AllEqualSplitScreen.LAYOUT)
                || defaultAttendeeOperation.getDefaultViewLayout().equals(OnePlusNSplitScreen.LAYOUT))
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
            @Override
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

        // 设置默认操作的参会者
        defaultAttendeeOperation.setAttendees(attendees);
        conferenceContext.setDefaultViewOperation(defaultAttendeeOperation);
        conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
        AttendeeOperation old = conferenceContext.getAttendeeOperation();
        old.cancel(defaultAttendeeOperation);
        conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
    }


    public void updateDefaultViewConfigInfo_back(String conferenceId, JSONObject jsonObj)
    {


        // 会议号
        final String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        // 会议室上下文实例对象
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);


        Assert.notNull(conferenceContext, "会议没开始不能在此编辑默认视图");

        // 参数校验
        Assert.isTrue(jsonObj.containsKey("defaultViewLayout"), "默认视图布局不能为空");
//        Assert.isTrue(jsonObj.containsKey("defaultViewIsBroadcast"), "默认视图是否广播不能为空");
//        Assert.isTrue(jsonObj.containsKey("defaultViewIsDisplaySelf"), "默认视图是否显示自己不能为空");
//        Assert.isTrue(jsonObj.containsKey("defaultViewIsFill"), "默认视图是否补位不能为空");
//        Assert.isTrue(jsonObj.containsKey("pollingInterval"), "默认视图的轮询时间间隔不能为空");
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
//        defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
//        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
//        defaultAttendeeOperation.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
//        defaultAttendeeOperation.setDefaultViewPollingInterval(jsonObj.getInteger("pollingInterval"));
        defaultAttendeeOperation.initSplitScreen();

//        List<AttendeeTele> attendees = new ArrayList<>();
//        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");
//        if (defaultAttendeeOperation.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)
//                || defaultAttendeeOperation.getDefaultViewLayout().equals(AllEqualSplitScreen.LAYOUT))
//        {
//            Assert.isTrue(ja == null || ja.size() == 0, "[" + defaultAttendeeOperation.getDefaultViewLayout() + "]布局下不能添加分屏信息");
//        }
//        else
//        {
//            Assert.isTrue(jsonObj.containsKey("defaultViewCellScreens"), "cellScreens分屏信息不能为空");
//            for (int i = 0; i < ja.size(); i++)
//            {
//                JSONObject jo = ja.getJSONObject(i);
//                Assert.isTrue(jo.containsKey("cellSequenceNumber"), "cellSequenceNumber分频序号不能为空");
//                Assert.isTrue(jo.containsKey("operation"), "operation分频操作类型不能为空");
//                Assert.isTrue(jo.containsKey("isFixed"), "isFixed分频固定类型不能为空");
//
//                BusiMcuTeleTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen = new BusiMcuTeleTemplateConferenceDefaultViewCellScreen();
//                busiTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
//                busiTemplateConferenceDefaultViewCellScreen.setOperation(CellScreenAttendeeOperation.convert(jo.getInteger("operation")).getValue());
//                busiTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());
//
//                CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
//                if (cellScreen == null)
//                {
//                    throw new SystemException(1008767, "找不到分屏信息：" + busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
//                }
//
//                // 设置分屏操作
//                cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiTemplateConferenceDefaultViewCellScreen.getOperation()));
//                cellScreen.setFixed(YesOrNo.convert(busiTemplateConferenceDefaultViewCellScreen.getIsFixed()));
//                cellScreen.setSerialNumber(busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
//            }
//        }
//
//        Comparator<JSONObject> c = new Comparator<JSONObject>()
//        {
//            public int compare(JSONObject o1, JSONObject o2)
//            {
//                return o2.getInteger("weight").compareTo(o1.getInteger("weight"));
//            }
//        };
//
//        List<JSONObject> defaultViewDeptJSONObjects = new ArrayList<>();
//        JSONArray defaultViewDepts = jsonObj.getJSONArray("defaultViewDepts");
//        if (defaultViewDepts != null)
//        {
//            for (int i = 0; i < defaultViewDepts.size(); i++)
//            {
//                JSONObject jo = defaultViewDepts.getJSONObject(i);
//                Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
//                Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
//                defaultViewDeptJSONObjects.add(jo);
//            }
//            defaultViewDeptJSONObjects.sort(c);
//        }
//
//        Map<Integer, List<JSONObject>> busiTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
//        Map<Long, List<JSONObject>> deptDefaultViewPaticipantsMap = new HashMap<>();
//        ja = jsonObj.getJSONArray("defaultViewPaticipants");
//        if (ja != null)
//        {
//            for (int i = 0; i < ja.size(); i++)
//            {
//                JSONObject jo = ja.getJSONObject(i);
//                Assert.isTrue(jo.containsKey("participantUuId"), "participantUuId不能为空");
//                Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
//
//                if (jo.getInteger("cellSequenceNumber") != null)
//                {
//                    List<JSONObject> busiTemplateConferenceDefaultViewPaticipants = busiTemplateConferenceDefaultViewPaticipantsMap.get(jo.getInteger("cellSequenceNumber"));
//                    if (busiTemplateConferenceDefaultViewPaticipants == null)
//                    {
//                        busiTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
//                        busiTemplateConferenceDefaultViewPaticipantsMap.put(jo.getInteger("cellSequenceNumber"), busiTemplateConferenceDefaultViewPaticipants);
//                    }
//                    busiTemplateConferenceDefaultViewPaticipants.add(jo);
//                }
//                else
//                {
//                    long deptId = conferenceContext.getAttendeeById(jo.getString("participantUuId")).getDeptId();
//                    List<JSONObject> allBusiTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
//                    if (allBusiTemplateConferenceDefaultViewPaticipants == null)
//                    {
//                        allBusiTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
//                        deptDefaultViewPaticipantsMap.put(deptId, allBusiTemplateConferenceDefaultViewPaticipants);
//                    }
//                    allBusiTemplateConferenceDefaultViewPaticipants.add(jo);
//                }
//            }
//        }
//
//        busiTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
//            v.sort(c);
//            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(screenNumber - 1);
//            for (JSONObject jo : v)
//            {
//                cellScreen.addAttendee(conferenceContext.getAttendeeById(jo.getString("participantUuId")));
//            }
//        });
//
//        deptDefaultViewPaticipantsMap.forEach((deptId, v) -> {
//            Collections.sort(v, c);
//        });
//
//        if (deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()) != null)
//        {
//            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiTemplateConferenceDefaultViewPaticipant) -> {
//                attendees.add(conferenceContext.getAttendeeById(busiTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
//            });
//        }
//
//        defaultViewDeptJSONObjects.forEach((d) -> {
//            defaultAttendeeOperation.addDefaultViewDept(d);
//            if (deptDefaultViewPaticipantsMap.get(d.getLong("deptId")) != null)
//            {
//                deptDefaultViewPaticipantsMap.get(d.getLong("deptId")).forEach((busiTemplateConferenceDefaultViewPaticipant)->{
//                    attendees.add(conferenceContext.getAttendeeById(busiTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
//                });
//            }
//        });
//
//        checkRepeatAttendee(defaultAttendeeOperation, attendees);

        // 设置默认操作的参会者
        //     defaultAttendeeOperation.setAttendees(attendees);
        conferenceContext.setDefaultViewOperation(defaultAttendeeOperation);
        conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
        AttendeeOperation old = conferenceContext.getAttendeeOperation();
        old.cancel(defaultAttendeeOperation);
        conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
    }

    @Override
    public JSONObject defaultViewData(String conferenceId)
    {
        final String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
        Assert.notNull(conferenceContext, "会议没开始，无法查看显示布局！");
        JSONObject json = new JSONObject();
        json.put("upDeptId", conferenceContext.getDeptId());
        List<AttendeeSmc2> as = new ArrayList<>(conferenceContext.getAttendees());
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
            AttendeeSmc2 a = conferenceContext.getAttendeeById(attendeeId);
            Set<AttendeeSmc2> as0 = new HashSet<>();
            as0.add(a);
            List<AttendeeSmc2> as00 = conferenceContext.getCascadeAttendeesMap().get(a.getDeptId());
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
        List<AttendeeSmc2> attendees = defaultViewOperation.getAttendees();
        if (CollectionUtils.isNotEmpty(attendees)) {
            for (int i = attendees.size() - 1; i >= 0; i--) {
                attendees.get(i).setWeight(i + 1);
            }
        }
        json.put("defaultAttendeeOperation", conferenceContext.getDefaultViewOperation());
        return json;
    }

    private void checkRepeatAttendee(DefaultAttendeeOperation defaultAttendeeOperation, List<AttendeeSmc2> attendees)
    {
        Set<String> ids = new HashSet<>();
        if (defaultAttendeeOperation.getSplitScreen() != null) {
            for (CellScreen cellScreen : defaultAttendeeOperation.getSplitScreen().getCellScreens()) {
                if (!ObjectUtils.isEmpty(cellScreen.getAttendees())) {
                    for (AttendeeSmc2 a : cellScreen.getAttendees()) {
                        if (!ids.add(a.getId())) {
                            throw new SystemException(1005247, "显示布局所选参会者存在重复：" + a.getName());
                        }
                    }
                }
            }
        }

        for (AttendeeSmc2 a : attendees)
        {
            if (!ids.add(a.getId()))
            {
                throw new SystemException(1005247, "显示布局所选参会者存在重复：" + a.getName());
            }
        }
    }
}
