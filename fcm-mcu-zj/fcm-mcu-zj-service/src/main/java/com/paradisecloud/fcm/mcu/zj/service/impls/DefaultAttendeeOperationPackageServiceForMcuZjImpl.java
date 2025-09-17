/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultAttendeeOperationPackageServiceImpl.java
 * Package     : com.paradisecloud.fcm.fme.service.impls
 * @author lilinhai
 * @since 2021-04-12 18:48
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.CellScreenAttendeeOperation;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceDefaultViewCellScreenMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceDefaultViewDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceDefaultViewPaticipantMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperationForGuest;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.PollingAttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.model.DeptWeight;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IDefaultAttendeeOperationPackageForMcuZjService;
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
public class DefaultAttendeeOperationPackageServiceForMcuZjImpl implements IDefaultAttendeeOperationPackageForMcuZjService
{

    @Resource
    private BusiMcuZjTemplateConferenceDefaultViewCellScreenMapper busiMcuZjTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuZjTemplateConferenceDefaultViewDeptMapper busiMcuZjTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuZjTemplateConferenceDefaultViewPaticipantMapper busiMcuZjTemplateConferenceDefaultViewPaticipantMapper;

    @Override
    public DefaultAttendeeOperation packing(McuZjConferenceContext conferenceContext, BusiMcuZjTemplateConference tc, Map<Long, BusiMcuZjTemplateParticipant> busiMcuZjTemplateParticipantMap)
    {
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(tc.getDefaultViewLayout());
        defaultAttendeeOperation.setDefaultViewIsBroadcast(tc.getDefaultViewIsBroadcast());
        defaultAttendeeOperation.setDefaultViewIsFill(tc.getDefaultViewIsFill());
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(tc.getDefaultViewIsDisplaySelf());
        defaultAttendeeOperation.setDefaultViewPollingInterval(tc.getPollingInterval());
        defaultAttendeeOperation.initSplitScreen();
        BusiMcuZjTemplateConferenceDefaultViewCellScreen con = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con.setType(1);
        List<BusiMcuZjTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuZjTemplateConferenceDefaultViewCellScreenList(con);
        for (BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreen : defaultViewCellScreens)
        {
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
            if (cellScreen == null)
            {
                throw new SystemException(1008767, "找不到分屏信息：" + busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
            }
            cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuZjTemplateConferenceDefaultViewCellScreen.getOperation()));
            cellScreen.setFixed(YesOrNo.convert(busiMcuZjTemplateConferenceDefaultViewCellScreen.getIsFixed()));
            cellScreen.setSerialNumber(busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
        }

        // 部门信息
        BusiMcuZjTemplateConferenceDefaultViewDept con1 = new BusiMcuZjTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con1.setType(1);
        List<BusiMcuZjTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuZjTemplateConferenceDefaultViewDeptMapper.selectBusiMcuZjTemplateConferenceDefaultViewDeptList(con1);
        Collections.sort(defaultViewDepts, new Comparator<BusiMcuZjTemplateConferenceDefaultViewDept>()
        {
            public int compare(BusiMcuZjTemplateConferenceDefaultViewDept o1, BusiMcuZjTemplateConferenceDefaultViewDept o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        BusiMcuZjTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con2.setType(1);
        List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZjTemplateConferenceDefaultViewPaticipantList(con2);
        Map<Integer, List<BusiMcuZjTemplateConferenceDefaultViewPaticipant>> busiMcuZjTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
        Map<Long, List<BusiMcuZjTemplateConferenceDefaultViewPaticipant>> deptDefaultViewPaticipantsMap = new HashMap<>();
        for (BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants)
        {
            if (busiMcuZjTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber() != null)
            {
                List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> busiMcuZjTemplateConferenceDefaultViewPaticipants = busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                if (busiMcuZjTemplateConferenceDefaultViewPaticipants == null)
                {
                    busiMcuZjTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                    busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.put(busiMcuZjTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber(), busiMcuZjTemplateConferenceDefaultViewPaticipants);
                }
                busiMcuZjTemplateConferenceDefaultViewPaticipants.add(busiMcuZjTemplateConferenceDefaultViewPaticipant);
            }
            else {
                BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuZjTemplateParticipant)) {

                    long deptId = conferenceContext.getAttendeeById(busiMcuZjTemplateParticipant.getUuid()).getDeptId();
                    List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> allBusiMcuZjTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuZjTemplateConferenceDefaultViewPaticipants == null) {
                        allBusiMcuZjTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuZjTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuZjTemplateConferenceDefaultViewPaticipants.add(busiMcuZjTemplateConferenceDefaultViewPaticipant);
                }

            }
        }

        Comparator<BusiMcuZjTemplateConferenceDefaultViewPaticipant> c = new Comparator<BusiMcuZjTemplateConferenceDefaultViewPaticipant>()
        {
            @Override
            public int compare(BusiMcuZjTemplateConferenceDefaultViewPaticipant o1, BusiMcuZjTemplateConferenceDefaultViewPaticipant o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        };

        busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
            Collections.sort(v, c);
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(screenNumber - 1);
            for (BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant : v)
            {
                BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuZjTemplateParticipant)) {
                    cellScreen.addAttendee(conferenceContext.getAttendeeById(busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                }
            }
        });

        deptDefaultViewPaticipantsMap.forEach((deptId, v) -> {
            Collections.sort(v, c);
        });

        List<AttendeeForMcuZj> attendees = new ArrayList<>();
        if (deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()) != null) {
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuZjTemplateConferenceDefaultViewPaticipant) -> {
                BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuZjTemplateParticipant)) {
                    attendees.add(conferenceContext.getAttendeeById(busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
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
                deptDefaultViewPaticipantsMap.get(d.getDeptId()).forEach((busiMcuZjTemplateConferenceDefaultViewPaticipant)->{
                    BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                    if (!Objects.isNull(busiMcuZjTemplateParticipant)) {
                        attendees.add(conferenceContext.getAttendeeById(busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                    }
                });
            }
        });

        checkRepeatAttendee(defaultAttendeeOperation, attendees);
        defaultAttendeeOperation.setAttendees(attendees);
        return defaultAttendeeOperation;
    }

    @Override
    public DefaultAttendeeOperationForGuest packingForGuest(McuZjConferenceContext conferenceContext, BusiMcuZjTemplateConference tc, Map<Long, BusiMcuZjTemplateParticipant> busiMcuZjTemplateParticipantMap)
    {
        DefaultAttendeeOperationForGuest defaultAttendeeOperation = new DefaultAttendeeOperationForGuest(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(tc.getDefaultViewLayoutGuest());
        defaultAttendeeOperation.setDefaultViewIsBroadcast(tc.getDefaultViewIsBroadcast());
        defaultAttendeeOperation.setDefaultViewIsFill(tc.getDefaultViewIsFillGuest());
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(tc.getDefaultViewIsDisplaySelf());
        defaultAttendeeOperation.setDefaultViewPollingInterval(tc.getPollingIntervalGuest());
        defaultAttendeeOperation.initSplitScreen();
        BusiMcuZjTemplateConferenceDefaultViewCellScreen con = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con.setType(2);
        List<BusiMcuZjTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuZjTemplateConferenceDefaultViewCellScreenList(con);
        for (BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreen : defaultViewCellScreens)
        {
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
            if (cellScreen == null)
            {
                throw new SystemException(1008767, "找不到分屏信息：" + busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
            }
            cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuZjTemplateConferenceDefaultViewCellScreen.getOperation()));
            cellScreen.setFixed(YesOrNo.convert(busiMcuZjTemplateConferenceDefaultViewCellScreen.getIsFixed()));
            cellScreen.setSerialNumber(busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
        }

        // 部门信息
        BusiMcuZjTemplateConferenceDefaultViewDept con1 = new BusiMcuZjTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con1.setType(2);
        List<BusiMcuZjTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuZjTemplateConferenceDefaultViewDeptMapper.selectBusiMcuZjTemplateConferenceDefaultViewDeptList(con1);
        Collections.sort(defaultViewDepts, new Comparator<BusiMcuZjTemplateConferenceDefaultViewDept>()
        {
            public int compare(BusiMcuZjTemplateConferenceDefaultViewDept o1, BusiMcuZjTemplateConferenceDefaultViewDept o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        BusiMcuZjTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con2.setType(2);
        List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZjTemplateConferenceDefaultViewPaticipantList(con2);
        Map<Integer, List<BusiMcuZjTemplateConferenceDefaultViewPaticipant>> busiMcuZjTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
        Map<Long, List<BusiMcuZjTemplateConferenceDefaultViewPaticipant>> deptDefaultViewPaticipantsMap = new HashMap<>();
        for (BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants)
        {
            if (busiMcuZjTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber() != null)
            {
                List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> busiMcuZjTemplateConferenceDefaultViewPaticipants = busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                if (busiMcuZjTemplateConferenceDefaultViewPaticipants == null)
                {
                    busiMcuZjTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                    busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.put(busiMcuZjTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber(), busiMcuZjTemplateConferenceDefaultViewPaticipants);
                }
                busiMcuZjTemplateConferenceDefaultViewPaticipants.add(busiMcuZjTemplateConferenceDefaultViewPaticipant);
            }
            else {
                BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuZjTemplateParticipant)) {

                    long deptId = conferenceContext.getAttendeeById(busiMcuZjTemplateParticipant.getUuid()).getDeptId();
                    List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> allBusiMcuZjTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuZjTemplateConferenceDefaultViewPaticipants == null) {
                        allBusiMcuZjTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuZjTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuZjTemplateConferenceDefaultViewPaticipants.add(busiMcuZjTemplateConferenceDefaultViewPaticipant);
                }

            }
        }

        Comparator<BusiMcuZjTemplateConferenceDefaultViewPaticipant> c = new Comparator<BusiMcuZjTemplateConferenceDefaultViewPaticipant>()
        {
            @Override
            public int compare(BusiMcuZjTemplateConferenceDefaultViewPaticipant o1, BusiMcuZjTemplateConferenceDefaultViewPaticipant o2)
            {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        };

        busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
            Collections.sort(v, c);
            CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(screenNumber - 1);
            for (BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant : v)
            {
                BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuZjTemplateParticipant)) {
                    cellScreen.addAttendee(conferenceContext.getAttendeeById(busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                }
            }
        });

        deptDefaultViewPaticipantsMap.forEach((deptId, v) -> {
            Collections.sort(v, c);
        });

        List<AttendeeForMcuZj> attendees = new ArrayList<>();
        if (deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()) != null) {
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuZjTemplateConferenceDefaultViewPaticipant) -> {
                BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                if (!Objects.isNull(busiMcuZjTemplateParticipant)) {
                    attendees.add(conferenceContext.getAttendeeById(busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
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
                deptDefaultViewPaticipantsMap.get(d.getDeptId()).forEach((busiMcuZjTemplateConferenceDefaultViewPaticipant)->{
                    BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
                    if (!Objects.isNull(busiMcuZjTemplateParticipant)) {
                        attendees.add(conferenceContext.getAttendeeById(busiMcuZjTemplateParticipantMap.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId()).getUuid()));
                    }
                });
            }
        });

        checkRepeatAttendeeForGeust(defaultAttendeeOperation, attendees);
        defaultAttendeeOperation.setAttendees(attendees);
        return defaultAttendeeOperation;
    }


    @Override
    public void updateDefaultViewConfigInfo(String conferenceId, JSONObject jsonObj)
    {
        // 会议号
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);

        // 会议室上下文实例对象
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.isSingleView()) {
            updateDefaultViewConfigInfoForGuest(conferenceId, jsonObj);
            return;
        }

        Assert.notNull(conferenceContext, "会议没开始不能在此编辑默认视图");

        SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
        if (sourceTemplate == null || !sourceTemplate.isSupportSplitScreen()) {
            throw new SystemException(1005454, "该会议（入会参数）只支持自动设置视图布局！");
        }
        if (sourceTemplate.getSingle_view() == null || sourceTemplate.getSingle_view() == 1) {
            throw new SystemException(1005454, "该会议（入会参数）只不支持设置该视图布局！");
        }

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

        List<AttendeeForMcuZj> attendees = new ArrayList<>();
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
            if (sourceTemplate.getMax_spk_mosic() < ja.size()) {
                throw new SystemException(1005454, "该会议（入会参数）不支持该视图布局！");
            }
            Assert.isTrue(jsonObj.containsKey("defaultViewCellScreens"), "cellScreens分屏信息不能为空");
            for (int i = 0; i < ja.size(); i++)
            {
                JSONObject jo = ja.getJSONObject(i);
                Assert.isTrue(jo.containsKey("cellSequenceNumber"), "cellSequenceNumber分频序号不能为空");
                Assert.isTrue(jo.containsKey("operation"), "operation分频操作类型不能为空");
                Assert.isTrue(jo.containsKey("isFixed"), "isFixed分频固定类型不能为空");

                BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreen = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setOperation(CellScreenAttendeeOperation.convert(jo.getInteger("operation")).getValue());
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
                if (cellScreen == null)
                {
                    throw new SystemException(1008767, "找不到分屏信息：" + busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
                }

                // 设置分屏操作
                cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuZjTemplateConferenceDefaultViewCellScreen.getOperation()));
                cellScreen.setFixed(YesOrNo.convert(busiMcuZjTemplateConferenceDefaultViewCellScreen.getIsFixed()));
                cellScreen.setSerialNumber(busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
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

        Map<Integer, List<JSONObject>> busiMcuZjTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
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
                    List<JSONObject> busiMcuZjTemplateConferenceDefaultViewPaticipants = busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.get(jo.getInteger("cellSequenceNumber"));
                    if (busiMcuZjTemplateConferenceDefaultViewPaticipants == null)
                    {
                        busiMcuZjTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.put(jo.getInteger("cellSequenceNumber"), busiMcuZjTemplateConferenceDefaultViewPaticipants);
                    }
                    busiMcuZjTemplateConferenceDefaultViewPaticipants.add(jo);
                }
                else
                {
                    long deptId = conferenceContext.getAttendeeById(jo.getString("participantUuId")).getDeptId();
                    List<JSONObject> allBusiMcuZjTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuZjTemplateConferenceDefaultViewPaticipants == null)
                    {
                        allBusiMcuZjTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuZjTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuZjTemplateConferenceDefaultViewPaticipants.add(jo);
                }
            }
        }

        busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
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
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuZjTemplateConferenceDefaultViewPaticipant) -> {
                attendees.add(conferenceContext.getAttendeeById(busiMcuZjTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
            });
        }

        defaultViewDeptJSONObjects.forEach((d) -> {
            defaultAttendeeOperation.addDefaultViewDept(d);
            if (deptDefaultViewPaticipantsMap.get(d.getLong("deptId")) != null)
            {
                deptDefaultViewPaticipantsMap.get(d.getLong("deptId")).forEach((busiMcuZjTemplateConferenceDefaultViewPaticipant)->{
                    attendees.add(conferenceContext.getAttendeeById(busiMcuZjTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
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
        // 观众
        if (defaultAttendeeOperation.getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
            if (conferenceContext.getAttendeeOperationForGuest() instanceof  DefaultAttendeeOperationForGuest) {
                conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
            }
            conferenceContext.setAttendeeOperationForGuest(defaultAttendeeOperation);
        }
    }

    @Override
    public void updateDefaultViewConfigInfoForGuest(String conferenceId, JSONObject jsonObj)
    {
        // 会议号
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);

        // 会议室上下文实例对象
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
            DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
            if (defaultAttendeeOperation.getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
                Assert.isTrue(false, "广播中不能单独编辑观众默认视图");
            }
        }

        Assert.notNull(conferenceContext, "会议没开始不能在此编辑默认视图");

        SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
        if (sourceTemplate == null || !sourceTemplate.isSupportSplitScreen()) {
            throw new SystemException(1005454, "该会议（入会参数）只支持自动设置视图布局！");
        }

        // 参数校验
        Assert.isTrue(jsonObj.containsKey("defaultViewLayout"), "默认视图布局不能为空");
//        Assert.isTrue(jsonObj.containsKey("defaultViewIsBroadcast"), "默认视图是否广播不能为空");
//        Assert.isTrue(jsonObj.containsKey("defaultViewIsDisplaySelf"), "默认视图是否显示自己不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsFill"), "默认视图是否补位不能为空");
        Assert.isTrue(jsonObj.containsKey("pollingInterval"), "默认视图的轮询时间间隔不能为空");
        DefaultAttendeeOperationForGuest defaultAttendeeOperation = new DefaultAttendeeOperationForGuest(conferenceContext);
        defaultAttendeeOperation.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        defaultAttendeeOperation.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.OFF.getValue());
        defaultAttendeeOperation.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        defaultAttendeeOperation.setDefaultViewPollingInterval(jsonObj.getInteger("pollingInterval"));
        defaultAttendeeOperation.initSplitScreen();

        List<AttendeeForMcuZj> attendees = new ArrayList<>();
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
            if (sourceTemplate.getMax_guest_mosic() < ja.size()) {
                throw new SystemException(1005454, "该会议（入会参数）不支持该视图布局！");
            }
            Assert.isTrue(jsonObj.containsKey("defaultViewCellScreens"), "cellScreens分屏信息不能为空");
            for (int i = 0; i < ja.size(); i++)
            {
                JSONObject jo = ja.getJSONObject(i);
                Assert.isTrue(jo.containsKey("cellSequenceNumber"), "cellSequenceNumber分频序号不能为空");
                Assert.isTrue(jo.containsKey("operation"), "operation分频操作类型不能为空");
                Assert.isTrue(jo.containsKey("isFixed"), "isFixed分频固定类型不能为空");

                BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreen = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setOperation(CellScreenAttendeeOperation.convert(jo.getInteger("operation")).getValue());
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                CellScreen cellScreen = defaultAttendeeOperation.getSplitScreen().getCellScreens().get(busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber() - 1);
                if (cellScreen == null)
                {
                    throw new SystemException(1008767, "找不到分屏信息：" + busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
                }

                // 设置分屏操作
                cellScreen.setCellScreenAttendeeOperation(CellScreenAttendeeOperation.convert(busiMcuZjTemplateConferenceDefaultViewCellScreen.getOperation()));
                cellScreen.setFixed(YesOrNo.convert(busiMcuZjTemplateConferenceDefaultViewCellScreen.getIsFixed()));
                cellScreen.setSerialNumber(busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber());
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

        Map<Integer, List<JSONObject>> busiMcuZjTemplateConferenceDefaultViewPaticipantsMap = new HashMap<>();
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
                    List<JSONObject> busiMcuZjTemplateConferenceDefaultViewPaticipants = busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.get(jo.getInteger("cellSequenceNumber"));
                    if (busiMcuZjTemplateConferenceDefaultViewPaticipants == null)
                    {
                        busiMcuZjTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.put(jo.getInteger("cellSequenceNumber"), busiMcuZjTemplateConferenceDefaultViewPaticipants);
                    }
                    busiMcuZjTemplateConferenceDefaultViewPaticipants.add(jo);
                }
                else
                {
                    long deptId = conferenceContext.getAttendeeById(jo.getString("participantUuId")).getDeptId();
                    List<JSONObject> allBusiMcuZjTemplateConferenceDefaultViewPaticipants = deptDefaultViewPaticipantsMap.get(deptId);
                    if (allBusiMcuZjTemplateConferenceDefaultViewPaticipants == null)
                    {
                        allBusiMcuZjTemplateConferenceDefaultViewPaticipants = new ArrayList<>();
                        deptDefaultViewPaticipantsMap.put(deptId, allBusiMcuZjTemplateConferenceDefaultViewPaticipants);
                    }
                    allBusiMcuZjTemplateConferenceDefaultViewPaticipants.add(jo);
                }
            }
        }

        busiMcuZjTemplateConferenceDefaultViewPaticipantsMap.forEach((screenNumber, v) -> {
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
            deptDefaultViewPaticipantsMap.get(conferenceContext.getDeptId()).forEach((busiMcuZjTemplateConferenceDefaultViewPaticipant) -> {
                attendees.add(conferenceContext.getAttendeeById(busiMcuZjTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
            });
        }

        defaultViewDeptJSONObjects.forEach((d) -> {
            defaultAttendeeOperation.addDefaultViewDept(d);
            if (deptDefaultViewPaticipantsMap.get(d.getLong("deptId")) != null)
            {
                deptDefaultViewPaticipantsMap.get(d.getLong("deptId")).forEach((busiMcuZjTemplateConferenceDefaultViewPaticipant)->{
                    attendees.add(conferenceContext.getAttendeeById(busiMcuZjTemplateConferenceDefaultViewPaticipant.getString("participantUuId")));
                });
            }
        });

        checkRepeatAttendeeForGeust(defaultAttendeeOperation, attendees);

        // 设置默认操作的参会者
        defaultAttendeeOperation.setAttendees(attendees);
        conferenceContext.setDefaultViewOperationForGuest(defaultAttendeeOperation);
        if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
            conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
        }
        AttendeeOperation old = conferenceContext.getAttendeeOperationForGuest();
        old.cancel();
        conferenceContext.setAttendeeOperationForGuest(defaultAttendeeOperation);
    }

    @Override
    public JSONObject defaultViewData(String conferenceId)
    {
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        Assert.notNull(conferenceContext, "会议没开始，无法查看显示布局！");
        JSONObject json = new JSONObject();
        json.put("upDeptId", conferenceContext.getDeptId());
        List<AttendeeForMcuZj> as = new ArrayList<>(conferenceContext.getAttendees());
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
            AttendeeForMcuZj a = conferenceContext.getAttendeeById(attendeeId);
            Set<AttendeeForMcuZj> as0 = new HashSet<>();
            as0.add(a);
            List<AttendeeForMcuZj> as00 = conferenceContext.getCascadeAttendeesMap().get(a.getDeptId());
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
        json.put("defaultAttendeeOperationForGuest", conferenceContext.getDefaultViewOperationForGuest());
        return json;
    }

    private void checkRepeatAttendee(DefaultAttendeeOperation defaultAttendeeOperation, List<AttendeeForMcuZj> attendees)
    {
        Set<String> ids = new HashSet<>();
        if (defaultAttendeeOperation.getSplitScreen() != null) {
            for (CellScreen cellScreen : defaultAttendeeOperation.getSplitScreen().getCellScreens()) {
                if (!ObjectUtils.isEmpty(cellScreen.getAttendees())) {
                    for (AttendeeForMcuZj a : cellScreen.getAttendees()) {
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

        for (AttendeeForMcuZj a : attendees)
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

    private void checkRepeatAttendeeForGeust(DefaultAttendeeOperationForGuest defaultAttendeeOperation, List<AttendeeForMcuZj> attendees)
    {
        Set<String> ids = new HashSet<>();
        if (defaultAttendeeOperation.getSplitScreen() != null) {
            for (CellScreen cellScreen : defaultAttendeeOperation.getSplitScreen().getCellScreens()) {
                if (!ObjectUtils.isEmpty(cellScreen.getAttendees())) {
                    for (AttendeeForMcuZj a : cellScreen.getAttendees()) {
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

        for (AttendeeForMcuZj a : attendees)
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
