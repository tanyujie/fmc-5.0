package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmAddRoomRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmSearchRoomsRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmModRoomRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmAddRoomResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmModRoomResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmSearchRoomsResponse;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplateConferenceService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplatePollingSchemeService;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.McuZjDeleteRoomTask;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 会议模板Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@Transactional
@Service
public class BusiMcuZjTemplateConferenceServiceImpl implements IBusiMcuZjTemplateConferenceService 
{
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
    
    @Resource
    private BusiMcuZjTemplateParticipantMapper busiMcuZjTemplateParticipantMapper;
    
    @Resource
    private BusiMcuZjTemplateDeptMapper busiMcuZjTemplateDeptMapper;
    
    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;
    
    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;

    @Resource
    private IBusiMcuZjTemplatePollingSchemeService busiMcuZjTemplatePollingSchemeService;

    @Resource
    private BusiMcuZjTemplateConferenceDefaultViewCellScreenMapper busiMcuZjTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuZjTemplateConferenceDefaultViewDeptMapper busiMcuZjTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuZjTemplateConferenceDefaultViewPaticipantMapper busiMcuZjTemplateConferenceDefaultViewPaticipantMapper;

    @Resource
    DelayTaskService delayTaskService;

    /**
     * 更新默认视图配置信息
     * @author lilinhai
     * @since 2021-04-08 15:30 
     * @param jsonObj
     * @param id
     */
    @Override
    public void updateDefaultViewConfigInfo(JSONObject jsonObj, Long id)
    {
        // 参数校验
        Assert.isTrue(jsonObj.containsKey("defaultViewLayout"), "defaultViewLayout不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsBroadcast"), "defaultViewIsBroadcast不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsDisplaySelf"), "defaultViewIsDisplaySelf不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsFill"), "defaultViewIsFill不能为空");
        Assert.isTrue(jsonObj.containsKey("pollingInterval"), "pollingInterval默认视图的轮询时间间隔不能为空");
        
        // 模板信息更新
        BusiMcuZjTemplateConference tc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(id);
        tc.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        if (tc.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)) {
            tc.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        } else {
            tc.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        }
        tc.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        tc.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setPollingInterval(jsonObj.getInteger("pollingInterval"));
        int c = busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1003432, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");

        // 根据模板ID删除分频信息
        BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreenCon = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
        busiMcuZjTemplateConferenceDefaultViewCellScreenCon.setType(1);
        busiMcuZjTemplateConferenceDefaultViewCellScreenCon.setTemplateConferenceId(id);
        List<BusiMcuZjTemplateConferenceDefaultViewCellScreen> busiMcuZjTemplateConferenceDefaultViewCellScreenList = busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuZjTemplateConferenceDefaultViewCellScreenList(busiMcuZjTemplateConferenceDefaultViewCellScreenCon);
        if (busiMcuZjTemplateConferenceDefaultViewCellScreenList.size() > 0) {
            for (BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreen : busiMcuZjTemplateConferenceDefaultViewCellScreenList) {
                busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuZjTemplateConferenceDefaultViewCellScreenById(busiMcuZjTemplateConferenceDefaultViewCellScreen.getId());
            }
        }
//        busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuZjTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);
        // 根据模板ID批量删除默认视图的参会者信息
        BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipantCon = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
        busiMcuZjTemplateConferenceDefaultViewPaticipantCon.setType(1);
        busiMcuZjTemplateConferenceDefaultViewPaticipantCon.setTemplateConferenceId(id);
        List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> busiMcuZjTemplateConferenceDefaultViewPaticipantList = busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZjTemplateConferenceDefaultViewPaticipantList(busiMcuZjTemplateConferenceDefaultViewPaticipantCon);
        if (busiMcuZjTemplateConferenceDefaultViewPaticipantList.size() > 0) {
            for (BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant : busiMcuZjTemplateConferenceDefaultViewPaticipantList) {
                busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuZjTemplateConferenceDefaultViewPaticipantById(busiMcuZjTemplateConferenceDefaultViewPaticipant.getId());
            }
        }

        // 默认视图的参会者信息保存
        Assert.isTrue(jsonObj.containsKey("defaultViewPaticipants"), "defaultViewpaticipants默认视图的参会者信息不能为空");
        ja = jsonObj.getJSONArray("defaultViewPaticipants");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            if (!jo.containsKey("templateParticipantId")) {
                if (jo.containsKey("terminalId")) {
                    try {
                        BusiMcuZjTemplateParticipant busiTemplateParticipantCon = new BusiMcuZjTemplateParticipant();
                        busiTemplateParticipantCon.setTemplateConferenceId(id);
                        busiTemplateParticipantCon.setTerminalId(jo.getLong("terminalId"));
                        List<BusiMcuZjTemplateParticipant> busiTemplateParticipants = busiMcuZjTemplateParticipantMapper.selectBusiMcuZjTemplateParticipantList(busiTemplateParticipantCon);
                        if (busiTemplateParticipants.size() > 0) {
                            BusiMcuZjTemplateParticipant busiTemplateParticipantTemp = busiTemplateParticipants.get(0);
                            jo.put("templateParticipantId", busiTemplateParticipantTemp.getId());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
            busiMcuZjTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiMcuZjTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiMcuZjTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiMcuZjTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.insertBusiMcuZjTemplateConferenceDefaultViewPaticipant(busiMcuZjTemplateConferenceDefaultViewPaticipant);
        }
        ja = jsonObj.getJSONArray("defaultViewCellScreens");

        if (tc.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT))
        {
            Assert.isTrue(ja == null || ja.size() == 0, "自动分屏下不能添加分屏信息");
        }
        else
        {
            if (tc.getResourceTemplateId() != null) {
                McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(tc.getDeptId()).getMasterMcuZjBridge();
                SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(tc.getResourceTemplateId());
                if (sourceTemplate == null || !sourceTemplate.isSupportSplitScreen()) {
                    throw new SystemException(1005454, "该会议（入会参数）只支持自动视图布局！");
                }
                if (sourceTemplate.getMax_spk_mosic() < ja.size()) {
                    throw new SystemException(1005454, "该会议（入会参数）不支持该视图布局！");
                }
            }
            Assert.isTrue(jsonObj.containsKey("defaultViewCellScreens"), "cellScreens分频信息不能为空");
            for (int i = 0; i < ja.size(); i++)
            {
                JSONObject jo = ja.getJSONObject(i);
                Assert.isTrue(jo.containsKey("cellSequenceNumber"), "cellSequenceNumber分频序号不能为空");
                Assert.isTrue(jo.containsKey("operation"), "operation分频操作类型不能为空");
                Assert.isTrue(jo.containsKey("isFixed"), "isFixed分频固定类型不能为空");
                BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreen = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiMcuZjTemplateConferenceDefaultViewPaticipant con = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
                    List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> ps = busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZjTemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }

                busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.insertBusiMcuZjTemplateConferenceDefaultViewCellScreen(busiMcuZjTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        BusiMcuZjTemplateConferenceDefaultViewDept busiMcuZjTemplateConferenceDefaultViewDeptCon = new BusiMcuZjTemplateConferenceDefaultViewDept();
        busiMcuZjTemplateConferenceDefaultViewDeptCon.setType(1);
        busiMcuZjTemplateConferenceDefaultViewDeptCon.setTemplateConferenceId(id);
        List<BusiMcuZjTemplateConferenceDefaultViewDept> busiMcuZjTemplateConferenceDefaultViewDeptList = busiMcuZjTemplateConferenceDefaultViewDeptMapper.selectBusiMcuZjTemplateConferenceDefaultViewDeptList(busiMcuZjTemplateConferenceDefaultViewDeptCon);
        if (busiMcuZjTemplateConferenceDefaultViewDeptList.size() > 0) {
            for (BusiMcuZjTemplateConferenceDefaultViewDept busiMcuZjTemplateConferenceDefaultViewDept : busiMcuZjTemplateConferenceDefaultViewDeptList) {
                busiMcuZjTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuZjTemplateConferenceDefaultViewDeptById(busiMcuZjTemplateConferenceDefaultViewDept.getId());
            }
        }

        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuZjTemplateConferenceDefaultViewDept busiMcuZjTemplateConferenceDefaultViewDept = new BusiMcuZjTemplateConferenceDefaultViewDept();
            busiMcuZjTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiMcuZjTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiMcuZjTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiMcuZjTemplateConferenceDefaultViewDeptMapper.insertBusiMcuZjTemplateConferenceDefaultViewDept(busiMcuZjTemplateConferenceDefaultViewDept);
        }
    }

    @Override
    public void updateDefaultViewConfigInfoForGuest(JSONObject jsonObj, Long id)
    {
        // 参数校验
        Assert.isTrue(jsonObj.containsKey("defaultViewLayout"), "defaultViewLayout不能为空");
//        Assert.isTrue(jsonObj.containsKey("defaultViewIsBroadcast"), "defaultViewIsBroadcast不能为空");
//        Assert.isTrue(jsonObj.containsKey("defaultViewIsDisplaySelf"), "defaultViewIsDisplaySelf不能为空");
        Assert.isTrue(jsonObj.containsKey("defaultViewIsFill"), "defaultViewIsFill不能为空");
        Assert.isTrue(jsonObj.containsKey("pollingInterval"), "pollingInterval默认视图的轮询时间间隔不能为空");

        // 模板信息更新
        BusiMcuZjTemplateConference tc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(id);
        tc.setDefaultViewLayoutGuest(jsonObj.getString("defaultViewLayout"));
        tc.setDefaultViewIsFillGuest(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setPollingIntervalGuest(jsonObj.getInteger("pollingInterval"));
        int c = busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1003432, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");
        // 根据模板ID删除分频信息
        BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreenCon = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
        busiMcuZjTemplateConferenceDefaultViewCellScreenCon.setType(2);
        busiMcuZjTemplateConferenceDefaultViewCellScreenCon.setTemplateConferenceId(id);
        List<BusiMcuZjTemplateConferenceDefaultViewCellScreen> busiMcuZjTemplateConferenceDefaultViewCellScreensList = busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuZjTemplateConferenceDefaultViewCellScreenList(busiMcuZjTemplateConferenceDefaultViewCellScreenCon);
        if (busiMcuZjTemplateConferenceDefaultViewCellScreensList.size() > 0) {
            for (BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreen : busiMcuZjTemplateConferenceDefaultViewCellScreensList) {
                busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuZjTemplateConferenceDefaultViewCellScreenById(busiMcuZjTemplateConferenceDefaultViewCellScreen.getId());
            }
        }
        //
//        busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuZjTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);
        // 根据模板ID批量删除默认视图的参会者信息

        // 默认视图的参会者信息保存
        BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipantCon = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
        busiMcuZjTemplateConferenceDefaultViewPaticipantCon.setType(2);
        busiMcuZjTemplateConferenceDefaultViewPaticipantCon.setTemplateConferenceId(id);
        List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> busiMcuZjTemplateConferenceDefaultViewPaticipantList = busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZjTemplateConferenceDefaultViewPaticipantList(busiMcuZjTemplateConferenceDefaultViewPaticipantCon);
        if (busiMcuZjTemplateConferenceDefaultViewPaticipantList.size() > 0) {
            for (BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant : busiMcuZjTemplateConferenceDefaultViewPaticipantList) {
                busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuZjTemplateConferenceDefaultViewPaticipantById(busiMcuZjTemplateConferenceDefaultViewPaticipant.getId());
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewPaticipants"), "defaultViewpaticipants默认视图的参会者信息不能为空");
        ja = jsonObj.getJSONArray("defaultViewPaticipants");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            if (!jo.containsKey("templateParticipantId")) {
                if (jo.containsKey("terminalId")) {
                    try {
                        BusiMcuZjTemplateParticipant busiTemplateParticipantCon = new BusiMcuZjTemplateParticipant();
                        busiTemplateParticipantCon.setTemplateConferenceId(id);
                        busiTemplateParticipantCon.setTerminalId(jo.getLong("terminalId"));
                        List<BusiMcuZjTemplateParticipant> busiTemplateParticipants = busiMcuZjTemplateParticipantMapper.selectBusiMcuZjTemplateParticipantList(busiTemplateParticipantCon);
                        if (busiTemplateParticipants.size() > 0) {
                            BusiMcuZjTemplateParticipant busiTemplateParticipantTemp = busiTemplateParticipants.get(0);
                            jo.put("templateParticipantId", busiTemplateParticipantTemp.getId());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
            busiMcuZjTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiMcuZjTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiMcuZjTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiMcuZjTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiMcuZjTemplateConferenceDefaultViewPaticipant.setType(2);
            busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.insertBusiMcuZjTemplateConferenceDefaultViewPaticipant(busiMcuZjTemplateConferenceDefaultViewPaticipant);
        }
        ja = jsonObj.getJSONArray("defaultViewCellScreens");

        if (tc.getDefaultViewLayoutGuest().equals(AutomaticSplitScreen.LAYOUT))
        {
            Assert.isTrue(ja == null || ja.size() == 0, "自动分屏下不能添加分屏信息");
        }
        else
        {
            if (tc.getResourceTemplateId() != null) {
                McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(tc.getDeptId()).getMasterMcuZjBridge();
                SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(tc.getResourceTemplateId());
                if (sourceTemplate == null || !sourceTemplate.isSupportSplitScreen()) {
                    throw new SystemException(1005454, "该会议（入会参数）只支持自动视图布局！");
                }
                if (sourceTemplate.getMax_guest_mosic() < ja.size()) {
                    throw new SystemException(1005454, "该会议（入会参数）不支持该视图布局！");
                }
            }
            Assert.isTrue(jsonObj.containsKey("defaultViewCellScreens"), "cellScreens分频信息不能为空");
            for (int i = 0; i < ja.size(); i++)
            {
                JSONObject jo = ja.getJSONObject(i);
                Assert.isTrue(jo.containsKey("cellSequenceNumber"), "cellSequenceNumber分频序号不能为空");
                Assert.isTrue(jo.containsKey("operation"), "operation分频操作类型不能为空");
                Assert.isTrue(jo.containsKey("isFixed"), "isFixed分频固定类型不能为空");
                BusiMcuZjTemplateConferenceDefaultViewCellScreen busiMcuZjTemplateConferenceDefaultViewCellScreen = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiMcuZjTemplateConferenceDefaultViewPaticipant con = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
                    List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> ps = busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZjTemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiMcuZjTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }
                busiMcuZjTemplateConferenceDefaultViewCellScreen.setType(2);
                busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.insertBusiMcuZjTemplateConferenceDefaultViewCellScreen(busiMcuZjTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        BusiMcuZjTemplateConferenceDefaultViewDept busiMcuZjTemplateConferenceDefaultViewDeptCon = new BusiMcuZjTemplateConferenceDefaultViewDept();
        busiMcuZjTemplateConferenceDefaultViewDeptCon.setType(2);
        busiMcuZjTemplateConferenceDefaultViewDeptCon.setTemplateConferenceId(id);
        List<BusiMcuZjTemplateConferenceDefaultViewDept> busiMcuZjTemplateConferenceDefaultViewDeptList = busiMcuZjTemplateConferenceDefaultViewDeptMapper.selectBusiMcuZjTemplateConferenceDefaultViewDeptList(busiMcuZjTemplateConferenceDefaultViewDeptCon);
        if (busiMcuZjTemplateConferenceDefaultViewDeptList.size() > 0) {
            for (BusiMcuZjTemplateConferenceDefaultViewDept busiMcuZjTemplateConferenceDefaultViewDept : busiMcuZjTemplateConferenceDefaultViewDeptList) {
                busiMcuZjTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuZjTemplateConferenceDefaultViewDeptById(busiMcuZjTemplateConferenceDefaultViewDept.getId());
            }
        }
        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuZjTemplateConferenceDefaultViewDept busiMcuZjTemplateConferenceDefaultViewDept = new BusiMcuZjTemplateConferenceDefaultViewDept();
            busiMcuZjTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiMcuZjTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiMcuZjTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiMcuZjTemplateConferenceDefaultViewDept.setType(2);
            busiMcuZjTemplateConferenceDefaultViewDeptMapper.insertBusiMcuZjTemplateConferenceDefaultViewDept(busiMcuZjTemplateConferenceDefaultViewDept);
        }
    }

    @Override
    public String selectBusiMcuZjTemplateConferenceCoverById(Long id)
    {
        BusiMcuZjTemplateConference tc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return tc.getCover();
    }



    /**
     * 查询会议模板
     *
     * @param id 会议模板ID
     * @return 会议模板
     */
    @Override
    public ModelBean selectBusiMcuZjTemplateConferenceById(Long id)
    {
        BusiMcuZjTemplateConference tc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return getTemplateConferenceDetails(tc);
    }

    public ModelBean getTemplateConferenceDetails(BusiMcuZjTemplateConference tc)
    {
        BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = new BusiMcuZjTemplateParticipant();
        busiMcuZjTemplateParticipant.setTemplateConferenceId(tc.getId());
        List<ModelBean> pMbs = new ArrayList<>();
        List<BusiMcuZjTemplateParticipant> ps = busiMcuZjTemplateParticipantMapper.selectBusiMcuZjTemplateParticipantList(busiMcuZjTemplateParticipant);

        int onlineCount = 0;
        int weight = 0;
        String mainName = "";
        Map<Long, ModelBean> bpm = new HashMap<>();
        for (BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant2 : ps)
        {
            ModelBean pmb = new ModelBean(busiMcuZjTemplateParticipant2);
            BusiTerminal bt = TerminalCache.getInstance().get(busiMcuZjTemplateParticipant2.getTerminalId());
            Integer onlineStatus = bt.getOnlineStatus();
            if (onlineStatus == null)
            {
                onlineStatus = TerminalOnlineStatus.OFFLINE.getValue();
            }

            if (TerminalOnlineStatus.convert(onlineStatus) == TerminalOnlineStatus.ONLINE)
            {
                onlineCount++;
            }
            bt.setOnlineStatus(onlineStatus);
            ModelBean tmb = new ModelBean(bt);
            tmb.remove("id");
            tmb.remove("createTime");
            tmb.remove("updateTime");
            tmb.remove("businessFieldType");
            if (!ObjectUtils.isEmpty(busiMcuZjTemplateParticipant2.getBusinessProperties()))
            {
                tmb.remove("businessProperties");
            }

            if (!ObjectUtils.isEmpty(busiMcuZjTemplateParticipant2.getAttendType()))
            {
                tmb.remove("attendType");
            }
            pmb.putAll(tmb);
            pMbs.add(pmb);

//            if (bt.getDeptId().longValue() == tc.getDeptId().longValue() && busiMcuZjTemplateParticipant2.getWeight().intValue() >= weight)
//            {
//                weight = busiMcuZjTemplateParticipant2.getWeight().intValue();
//                mainName = bt.getName();
//            }

            ModelBean mb0 = new ModelBean();
            mb0.putAll(pmb);
            mb0.remove("weight");
            bpm.put(busiMcuZjTemplateParticipant2.getId(), mb0);
        }

        if (tc.getMasterParticipantId() != null)
        {
            mainName = bpm.get(tc.getMasterParticipantId()).get("name").toString();
        }

        BusiMcuZjTemplateConferenceDefaultViewCellScreen con = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(tc.getId());
        con.setType(1);
        List<BusiMcuZjTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuZjTemplateConferenceDefaultViewCellScreenList(con);

        BusiMcuZjTemplateConferenceDefaultViewDept con1 = new BusiMcuZjTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(tc.getId());
        con1.setType(1);
        List<BusiMcuZjTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuZjTemplateConferenceDefaultViewDeptMapper.selectBusiMcuZjTemplateConferenceDefaultViewDeptList(con1);

        BusiMcuZjTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(tc.getId());
        con2.setType(1);
        List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZjTemplateConferenceDefaultViewPaticipantList(con2);
        List<ModelBean> defaultViewPaticipantMbs = new ArrayList<>();
        for (BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants) {
            ModelBean mb = bpm.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                ModelBean mbNew = new ModelBean();
                mbNew.putAll(mb);
                mbNew.put("weight", busiMcuZjTemplateConferenceDefaultViewPaticipant.getWeight());
                mbNew.put("cellSequenceNumber", busiMcuZjTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbs.add(mbNew);
            }
        }
        //观众
        BusiMcuZjTemplateConferenceDefaultViewCellScreen conGuest = new BusiMcuZjTemplateConferenceDefaultViewCellScreen();
        conGuest.setTemplateConferenceId(tc.getId());
        conGuest.setType(2);
        List<BusiMcuZjTemplateConferenceDefaultViewCellScreen> defaultViewCellScreensGuest = busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuZjTemplateConferenceDefaultViewCellScreenList(conGuest);

        BusiMcuZjTemplateConferenceDefaultViewDept con1Guest = new BusiMcuZjTemplateConferenceDefaultViewDept();
        con1Guest.setTemplateConferenceId(tc.getId());
        con1Guest.setType(2);
        List<BusiMcuZjTemplateConferenceDefaultViewDept> defaultViewDeptsGuest = busiMcuZjTemplateConferenceDefaultViewDeptMapper.selectBusiMcuZjTemplateConferenceDefaultViewDeptList(con1Guest);

        BusiMcuZjTemplateConferenceDefaultViewPaticipant con2Guest = new BusiMcuZjTemplateConferenceDefaultViewPaticipant();
        con2Guest.setTemplateConferenceId(tc.getId());
        con2Guest.setType(2);
        List<BusiMcuZjTemplateConferenceDefaultViewPaticipant> defaultViewPaticipantsGuest = busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZjTemplateConferenceDefaultViewPaticipantList(con2Guest);
        List<ModelBean> defaultViewPaticipantMbsGuest = new ArrayList<>();
        for (BusiMcuZjTemplateConferenceDefaultViewPaticipant busiMcuZjTemplateConferenceDefaultViewPaticipant : defaultViewPaticipantsGuest) {
            ModelBean mb = bpm.get(busiMcuZjTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                ModelBean mbNew = new ModelBean();
                mbNew.putAll(mb);
                mbNew.put("weight", busiMcuZjTemplateConferenceDefaultViewPaticipant.getWeight());
                mbNew.put("cellSequenceNumber", busiMcuZjTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbsGuest.add(mbNew);
            }
        }

        BusiMcuZjTemplateDept tdCon = new BusiMcuZjTemplateDept();
        tdCon.setTemplateConferenceId(tc.getId());
        List<BusiMcuZjTemplateDept> tds = busiMcuZjTemplateDeptMapper.selectBusiMcuZjTemplateDeptList(tdCon);

        ModelBean tcmb = new ModelBean(tc);
        if (tc.getMasterParticipantId() != null)
        {
            tcmb.put("masterTerminalId", bpm.get(tc.getMasterParticipantId()).get("terminalId"));
        }
        if (StringUtils.isEmpty(tc.getTenantId())) {
            tcmb.put("tenantId", "");
        }
        tcmb.remove("conferenceCtrlPassword");
        ModelBean mb = new ModelBean();
        mb.put("templateConference", tcmb);
        mb.put("templateParticipants", pMbs);
        mb.put("templateDepts", tds);
        mb.put("defaultViewCellScreens", defaultViewCellScreens);
        mb.put("defaultViewDepts", defaultViewDepts);
        mb.put("defaultViewPaticipants", defaultViewPaticipantMbs);

        mb.put("defaultViewCellScreensGuest", defaultViewCellScreensGuest);
        mb.put("defaultViewDeptsGuest", defaultViewDeptsGuest);
        mb.put("defaultViewPaticipantsGuest", defaultViewPaticipantMbsGuest);
        mb.put("onlineCount", onlineCount);
        if (tc.getConferenceNumber() != null && McuZjConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_ZJ)))
        {
            mb.put("meetingJoinedCount", McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_ZJ)).getAttendeeCountingStatistics().getMeetingJoinedCount());
        }
        mb.put("mainVenue", mainName);
        mb.put("isStart", tc.getConferenceNumber() != null && McuZjConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_ZJ)));
        mb.put("mcuType", McuType.MCU_ZJ.getCode());
        return mb;
    }

    /**
     * 查询会议模板列表
     *
     * @param busiMcuZjTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuZjTemplateConference> selectBusiMcuZjTemplateConferenceList(BusiMcuZjTemplateConference busiMcuZjTemplateConference)
    {
        Assert.notNull(busiMcuZjTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectBusiMcuZjTemplateConferenceListWithoutBusinessFieldType(busiMcuZjTemplateConference);
    }

    /**
     * 查询会议模板列表
     *
     * @param busiMcuZjTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuZjTemplateConference> selectAllBusiMcuZjTemplateConferenceList(BusiMcuZjTemplateConference busiMcuZjTemplateConference)
    {
        Assert.notNull(busiMcuZjTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectAllBusiMcuZjTemplateConferenceListWithoutBusinessFieldType(busiMcuZjTemplateConference);
    }

    public List<BusiMcuZjTemplateConference> selectBusiMcuZjTemplateConferenceListWithoutBusinessFieldType(BusiMcuZjTemplateConference busiMcuZjTemplateConference)
    {
        // 绑定终端归属部门
        if (busiMcuZjTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuZjTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuZjTemplateConference> tcs = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceList(busiMcuZjTemplateConference);
        return tcs;
    }

    public List<BusiMcuZjTemplateConference> selectAllBusiMcuZjTemplateConferenceListWithoutBusinessFieldType(BusiMcuZjTemplateConference busiMcuZjTemplateConference)
    {
        // 绑定终端归属部门
        if (busiMcuZjTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuZjTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuZjTemplateConference> tcs = busiMcuZjTemplateConferenceMapper.selectAllBusiMcuZjTemplateConferenceList(busiMcuZjTemplateConference);
        return tcs;
    }

    /**
     * <pre>模板集合转换成modelBean</pre>
     * @author lilinhai
     * @since 2021-01-30 14:04
     * @param tcs
     * @return List<ModelBean>
     */
    public List<ModelBean> toModelBean(List<BusiMcuZjTemplateConference> tcs)
    {
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuZjTemplateConference tc : tcs)
        {
            mbs.add(getTemplateConferenceDetails(tc));
        }

        return mbs;
    }

    @Override
    public List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType)
    {
        BusinessFieldType.convert(businessFieldType);
        return busiMcuZjTemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
    }

    /**
     * 新增会议模板
     *
     * @param busiMcuZjTemplateConference 会议模板
     * @param busiMcuZjTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int insertBusiMcuZjTemplateConference(BusiMcuZjTemplateConference busiMcuZjTemplateConference, Long masterTerminalId, List<BusiMcuZjTemplateParticipant> busiMcuZjTemplateParticipants, List<BusiMcuZjTemplateDept> templateDepts)
    {
        busiMcuZjTemplateConference.setCreateTime(new Date());

        Assert.notNull(busiMcuZjTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        Assert.notNull(busiMcuZjTemplateConference.getCreateType(), "会议模板创建类型不能为空！");
        Assert.notNull(busiMcuZjTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        if (YesOrNo.convert(busiMcuZjTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
        {
            Assert.notNull(busiMcuZjTemplateConference.getConferenceNumber(), "会议号不能为空！");
        }

        // 绑定终端归属部门
        if (busiMcuZjTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuZjTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        if (busiMcuZjTemplateConference.getDeptId() == null)
        {
            throw new SystemException(1004543, "添加会议模板，部门ID不能为空！");
        }

        if (busiMcuZjTemplateConference.getCreateUserId() == null || busiMcuZjTemplateConference.getCreateUserName() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiMcuZjTemplateConference.setCreateUserId(loginUser.getUser().getUserId());
                busiMcuZjTemplateConference.setCreateUserName(loginUser.getUser().getUserName());
            }
        }

        // 校验会议类型
      //  ConferenceType.convert(busiMcuZjTemplateConference.getType());

        int c = busiMcuZjTemplateConferenceMapper.insertBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
        if (c > 0)
        {
            if (!ObjectUtils.isEmpty(busiMcuZjTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant : busiMcuZjTemplateParticipants)
                {
                    busiMcuZjTemplateParticipant.setTemplateConferenceId(busiMcuZjTemplateConference.getId());
                    busiMcuZjTemplateParticipant.setCreateTime(new Date());
                    busiMcuZjTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    try
                    {
                        busiMcuZjTemplateParticipantMapper.insertBusiMcuZjTemplateParticipant(busiMcuZjTemplateParticipant);
                    }
                    catch (Exception e)
                    {
                        LoggerFactory.getLogger(getClass()).error("终端ID已不存在：" + busiMcuZjTemplateParticipant.getTerminalId(), e);
                        throw new RuntimeException("终端ID已不存在：" + busiMcuZjTemplateParticipant.getTerminalId());
                    }
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuZjTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiMcuZjTemplateConference.setMasterParticipantId(busiMcuZjTemplateParticipant.getId());
                        busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuZjTemplateDept busiMcuZjTemplateDept : templateDepts)
                {
                    busiMcuZjTemplateDept.setTemplateConferenceId(busiMcuZjTemplateConference.getId());
                    busiMcuZjTemplateDept.setCreateTime(new Date());
                    busiMcuZjTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuZjTemplateDeptMapper.insertBusiMcuZjTemplateDept(busiMcuZjTemplateDept);
                }
            }

            if (busiMcuZjTemplateConference.getConferenceNumber() == null) {
                // 生成会议号
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiMcuZjTemplateConference.getDeptId(), McuType.MCU_ZJ.getCode());
                busiMcuZjTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
                busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
            }

            if (busiMcuZjTemplateConference.getConferenceNumber() != null)
            {
                // 修改号码状态为已绑定
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiMcuZjTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            // 添加默认轮询模板
            BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme = new BusiMcuZjTemplatePollingScheme();
            busiMcuZjTemplatePollingScheme.setEnableStatus(YesOrNo.YES.getValue());
            busiMcuZjTemplatePollingScheme.setIsBroadcast(YesOrNo.NO.getValue());
            busiMcuZjTemplatePollingScheme.setIsDisplaySelf(PanePlacementSelfPaneMode.OFF.getValue());
            busiMcuZjTemplatePollingScheme.setSchemeName("全局轮询");
            busiMcuZjTemplatePollingScheme.setLayout(OneSplitScreen.LAYOUT);
            busiMcuZjTemplatePollingScheme.setIsFill(YesOrNo.YES.getValue());
            busiMcuZjTemplatePollingScheme.setPollingInterval(10);
            busiMcuZjTemplatePollingScheme.setPollingStrategy(PollingStrategy.GLOBAL.getValue());
            busiMcuZjTemplatePollingScheme.setTemplateConferenceId(busiMcuZjTemplateConference.getId());
            busiMcuZjTemplatePollingSchemeService.insertBusiMcuZjTemplatePollingScheme(busiMcuZjTemplatePollingScheme, null, null);

            // 向MCU添加模板数据
            try {
                Long conferenceNumber = busiMcuZjTemplateConference.getConferenceNumber();
                String conferenceCtrlPassword = generatePassword();
                String supervisorPassword = generateSupervisorPassword(conferenceCtrlPassword);
                McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjTemplateConference.getDeptId()).getMasterMcuZjBridge();

//                if (busiMcuZjTemplateParticipants != null && busiMcuZjTemplateParticipants.size() > 0) {
//                    McuZjEpsRegisterTask mcuZjEpsRegisterTask = new McuZjEpsRegisterTask(mcuZjBridge.getBusiMcuZj().getId() + "_" + conferenceNumber.toString(), 1000, mcuZjBridge, busiMcuZjTemplateParticipants);
//                    delayTaskService.addTask(mcuZjEpsRegisterTask);
//                }

                Integer resourceTemplateId = busiMcuZjTemplateConference.getResourceTemplateId();
                Integer bandwidth = 1;
                if (resourceTemplateId != null) {
                    SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(resourceTemplateId);
                    if (sourceTemplate == null) {
                        sourceTemplate = mcuZjBridge.getDefaultSourceTemplate();
                        if (sourceTemplate == null) {
                            throw new SystemException(1004543, "添加会议模板失败，MCU错误！");
                        }
                    }
                    try {
                        resourceTemplateId = sourceTemplate.getId();
                        String bw = sourceTemplate.getRes_bw();
                        String bwS = bw.split("@")[1].replace("M", "");
                        bandwidth = Integer.valueOf(bwS);
                    } catch (Exception e) {
                    }
                } else {
                    SourceTemplate sourceTemplate = mcuZjBridge.getDefaultSourceTemplate();
                    if (sourceTemplate == null) {
                        throw new SystemException(1004543, "添加会议模板失败，MCU错误！");
                    }
                    resourceTemplateId = sourceTemplate.getId();
                    try {
                        String bw = sourceTemplate.getRes_bw();
                        String bwS = bw.split("@")[1].replace("M", "");
                        bandwidth = Integer.valueOf(bwS);
                    } catch (Exception e) {
                    }
                }
                // 添加会议室
                if (false) {// 开始会议时再添加会议室，避免未开始会议有终端能呼入
                    CmAddRoomRequest cmAddRoomRequest = CmAddRoomRequest.buildDefaultRequest();
                    Integer muteType = busiMcuZjTemplateConference.getMuteType();
                    if (muteType != null && muteType == 0) {
                        cmAddRoomRequest.setAll_guests_mute(0);
                    }
                    cmAddRoomRequest.setRoom_name("会议室" + conferenceNumber);// 会议室81234
                    cmAddRoomRequest.setRoom_mark("" + conferenceNumber);// 81234
                    cmAddRoomRequest.setCtrl_pwd(conferenceCtrlPassword);// 123456
                    cmAddRoomRequest.setJoin_pwd(busiMcuZjTemplateConference.getConferencePassword());
                    cmAddRoomRequest.setSupervisor_pwd(supervisorPassword);
                    cmAddRoomRequest.setResource_template_id(resourceTemplateId);
                    cmAddRoomRequest.setBandwidth(bandwidth * 1024);
                    List<Integer> belong_to_departments = new ArrayList<>();
                    belong_to_departments.add(mcuZjBridge.getTopDepartmentId());
                    cmAddRoomRequest.setBelong_to_departments(belong_to_departments);
                    CmAddRoomResponse cmAddRoomResponse = mcuZjBridge.getConferenceManageApi().addRoom(cmAddRoomRequest);
                    if (cmAddRoomResponse != null) {
                        busiMcuZjTemplateConference.setConferenceCtrlPassword(conferenceCtrlPassword);
                    }
                }
                busiMcuZjTemplateConference.setResourceTemplateId(resourceTemplateId);
                busiMcuZjTemplateConference.setBandwidth(bandwidth);
                busiMcuZjTemplateConference.setTenantId(mcuZjBridge.getTenantId());
                busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
            } catch (Exception e) {
                throw new SystemException(1004543, "添加会议模板失败，MCU错误！");
            }
        }
        return c;
    }

    /**
     * 修改会议模板
     *
     * @param busiMcuZjTemplateConference 会议模板
     * @param busiMcuZjTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int updateBusiMcuZjTemplateConference(BusiMcuZjTemplateConference busiMcuZjTemplateConference, Long masterTerminalId, List<BusiMcuZjTemplateParticipant> busiMcuZjTemplateParticipants, List<BusiMcuZjTemplateDept> templateDepts)
    {
        Assert.notNull(busiMcuZjTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiMcuZjTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        busiMcuZjTemplateConference.setUpdateTime(new Date());
        if (YesOrNo.convert(busiMcuZjTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
        {
            Assert.notNull(busiMcuZjTemplateConference.getConferenceNumber(), "会议号不能为空！");
        }

        // 校验会议类型
        //ConferenceType.convert(busiMcuZjTemplateConference.getType());

        // 先置空主会场配置
        busiMcuZjTemplateConference.setMasterParticipantId(null);
        busiMcuZjTemplateConference.setCreateUserId(null);
        busiMcuZjTemplateConference.setCreateUserName(null);
        int c = busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
        if (c > 0)
        {
            doParticipantUpdate(busiMcuZjTemplateConference, busiMcuZjTemplateParticipants, masterTerminalId);
            doDeptUpdate(busiMcuZjTemplateConference, templateDepts);

            // 修改号码状态为已绑定
            if (busiMcuZjTemplateConference.getConferenceNumber() != null)
            {
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiMcuZjTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            BusiMcuZjTemplateConference old = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiMcuZjTemplateConference.getId());
            if (old.getConferenceNumber() != null && !old.getConferenceNumber().equals(busiMcuZjTemplateConference.getConferenceNumber()))
            {
                BusiMcuZjTemplateConference con = new BusiMcuZjTemplateConference();
                con.setConferenceNumber(old.getConferenceNumber());
                List<BusiMcuZjTemplateConference> bcs = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceList(con);
                if (ObjectUtils.isEmpty(bcs))
                {
                    BusiConferenceNumber cn = new BusiConferenceNumber();
                    cn.setId(old.getConferenceNumber());
                    cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
                    busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
                }
            }
        }

        // 向MCU更新模板数据
        try {
            Long conferenceNumber = busiMcuZjTemplateConference.getConferenceNumber();
            String conferenceCtrlPassword = generatePassword();
            String supervisorPassword = generateSupervisorPassword(conferenceCtrlPassword);
            McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjTemplateConference.getDeptId()).getMasterMcuZjBridge();

//            if (busiMcuZjTemplateParticipants != null && busiMcuZjTemplateParticipants.size() > 0) {
//                McuZjEpsRegisterTask mcuZjEpsRegisterTask = new McuZjEpsRegisterTask(mcuZjBridge.getBusiMcuZj().getId() + "_" + conferenceNumber.toString(), 1000, mcuZjBridge, busiMcuZjTemplateParticipants);
//                delayTaskService.addTask(mcuZjEpsRegisterTask);
//            }

            Integer resourceTemplateId = busiMcuZjTemplateConference.getResourceTemplateId();
            Integer bandwidth = 1;
            if (resourceTemplateId != null) {
                SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(resourceTemplateId);
                if (sourceTemplate == null) {
                    sourceTemplate = mcuZjBridge.getDefaultSourceTemplate();
                    if (sourceTemplate == null) {
                        throw new SystemException(1004543, "修改会议模板失败，MCU错误！");
                    }
                    resourceTemplateId = sourceTemplate.getId();
                }
                try {
                    String bw = sourceTemplate.getRes_bw();
                    String bwS = bw.split("@")[1].replace("M", "");
                    bandwidth = Integer.valueOf(bwS);
                } catch (Exception e) {
                }
            } else {
                SourceTemplate sourceTemplate = mcuZjBridge.getDefaultSourceTemplate();
                if (sourceTemplate == null) {
                    throw new SystemException(1004543, "修改会议模板失败，MCU错误！");
                }
                resourceTemplateId = sourceTemplate.getId();
                try {
                    String bw = sourceTemplate.getRes_bw();
                    String bwS = bw.split("@")[1].replace("M", "");
                    bandwidth = Integer.valueOf(bwS);
                } catch (Exception e) {
                }
            }
            // 修改会议室
            CmSearchRoomsRequest cmSearchRoomsRequest = new CmSearchRoomsRequest();
            String[] filterType = new String[1];
            filterType[0] = "room_mark";
            Object[] filterValue = new Object[1];
            filterValue[0] = conferenceNumber;
            cmSearchRoomsRequest.setFilter_type(filterType);
            cmSearchRoomsRequest.setFilter_value(filterValue);
            CmSearchRoomsResponse cmSearchRoomsResponse = mcuZjBridge.getConferenceManageApi().searchRooms(cmSearchRoomsRequest);
            if (cmSearchRoomsResponse != null && cmSearchRoomsResponse.getRoom_ids() != null && cmSearchRoomsResponse.getRoom_ids().length > 0) {
                CmModRoomRequest cmModRoomRequest = CmModRoomRequest.buildDefaultRequest();
                cmModRoomRequest.setRoom_name("会议室" + conferenceNumber);// 会议室81234
                cmModRoomRequest.setRoom_mark("" + conferenceNumber);// 81234
                cmModRoomRequest.setRoom_id(cmSearchRoomsResponse.getRoom_ids()[0]);
                cmModRoomRequest.setCtrl_pwd(conferenceCtrlPassword);// 123456
                cmModRoomRequest.setJoin_pwd(busiMcuZjTemplateConference.getConferencePassword());
                cmModRoomRequest.setSupervisor_pwd(supervisorPassword);
                cmModRoomRequest.setResource_template_id(resourceTemplateId);
                cmModRoomRequest.setBandwidth(bandwidth * 1024);
                CmModRoomResponse cmModRoomResponse = mcuZjBridge.getConferenceManageApi().modRoom(cmModRoomRequest);
                if (cmModRoomResponse != null && "0/success".equalsIgnoreCase(cmModRoomResponse.getResult())) {
                    busiMcuZjTemplateConference.setConferenceCtrlPassword(conferenceCtrlPassword);
                    busiMcuZjTemplateConference.setResourceTemplateId(resourceTemplateId);
                    busiMcuZjTemplateConference.setBandwidth(bandwidth);
                    busiMcuZjTemplateConference.setTenantId(mcuZjBridge.getTenantId());
                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
                } else {
                    throw new SystemException(1004543, "修改会议模板失败，MCU错误！");
                }
            }
        } catch (Exception e) {
            throw new SystemException(1004543, "修改会议模板失败，MCU错误！");
        }
        return c;
    }

    private void doDeptUpdate(BusiMcuZjTemplateConference busiMcuZjTemplateConference, List<BusiMcuZjTemplateDept> templateDepts)
    {
        BusiMcuZjTemplateDept deptCon = new BusiMcuZjTemplateDept();
        deptCon.setTemplateConferenceId(busiMcuZjTemplateConference.getId());
        List<BusiMcuZjTemplateDept> ds = busiMcuZjTemplateDeptMapper.selectBusiMcuZjTemplateDeptList(deptCon);
        Map<Long, BusiMcuZjTemplateDept> oldMap = new HashMap<>();
        for (BusiMcuZjTemplateDept busiMcuZjTemplateDept : ds)
        {
            oldMap.put(busiMcuZjTemplateDept.getDeptId(), busiMcuZjTemplateDept);
        }

        // 添加模板部门顺序信息
        if (!ObjectUtils.isEmpty(templateDepts))
        {
            for (BusiMcuZjTemplateDept busiMcuZjTemplateDept : templateDepts)
            {
                busiMcuZjTemplateDept.setTemplateConferenceId(busiMcuZjTemplateConference.getId());
                BusiMcuZjTemplateDept oldTd = oldMap.remove(busiMcuZjTemplateDept.getDeptId());
                if (oldTd != null)
                {
                    oldTd.setUpdateTime(new Date());
                    oldTd.setWeight(busiMcuZjTemplateDept.getWeight());
                    busiMcuZjTemplateDept = oldTd;
                    busiMcuZjTemplateDeptMapper.updateBusiMcuZjTemplateDept(busiMcuZjTemplateDept);
                }
                else
                {
                    busiMcuZjTemplateDept.setCreateTime(new Date());
                    busiMcuZjTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuZjTemplateDeptMapper.insertBusiMcuZjTemplateDept(busiMcuZjTemplateDept);
                }
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((deptId, td) -> {
                busiMcuZjTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuZjTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(td.getTemplateConferenceId(), td.getDeptId());
                busiMcuZjTemplateDeptMapper.deleteBusiMcuZjTemplateDeptById(td.getId());
            });
        }
    }

    private void doParticipantUpdate(BusiMcuZjTemplateConference busiMcuZjTemplateConference, List<BusiMcuZjTemplateParticipant> busiMcuZjTemplateParticipants, Long masterTerminalId)
    {
        BusiMcuZjTemplateParticipant query = new BusiMcuZjTemplateParticipant();
        query.setTemplateConferenceId(busiMcuZjTemplateConference.getId());
        List<BusiMcuZjTemplateParticipant> ps = busiMcuZjTemplateParticipantMapper.selectBusiMcuZjTemplateParticipantList(query);
        Map<Long, BusiMcuZjTemplateParticipant> oldMap = new HashMap<>();
        for (BusiMcuZjTemplateParticipant tp : ps)
        {
            oldMap.put(tp.getTerminalId(), tp);
        }

        if (!ObjectUtils.isEmpty(busiMcuZjTemplateParticipants))
        {
            for (BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant : busiMcuZjTemplateParticipants)
            {
                busiMcuZjTemplateParticipant.setTemplateConferenceId(busiMcuZjTemplateConference.getId());
                BusiMcuZjTemplateParticipant oldTp = oldMap.remove(busiMcuZjTemplateParticipant.getTerminalId());
                if (oldTp != null)
                {
                    oldTp.setWeight(busiMcuZjTemplateParticipant.getWeight());
                    oldTp.setUpdateTime(new Date());
                    oldTp.setAttendType(busiMcuZjTemplateParticipant.getAttendType());
                    oldTp.setBusinessProperties(busiMcuZjTemplateParticipant.getBusinessProperties());
                    busiMcuZjTemplateParticipantMapper.updateBusiMcuZjTemplateParticipant(oldTp);
                    busiMcuZjTemplateParticipant = oldTp;
                }
                else
                {
                    // 新增
                    busiMcuZjTemplateParticipant.setCreateTime(new Date());
                    busiMcuZjTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuZjTemplateParticipantMapper.insertBusiMcuZjTemplateParticipant(busiMcuZjTemplateParticipant);
                }

                if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuZjTemplateParticipant.getTerminalId().longValue())
                {
                    // 设置模板会议中配置的主会场参会终端
                    busiMcuZjTemplateConference.setMasterParticipantId(busiMcuZjTemplateParticipant.getId());
                    busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
                }
            }

            if (masterTerminalId == null)
            {
                // 设置模板会议中配置的主会场参会终端
                busiMcuZjTemplateConference.setMasterParticipantId(null);
                busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((terminalId, tp) -> {
                busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuZjTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(tp.getId());
                busiMcuZjTemplateParticipantMapper.deleteBusiMcuZjTemplateParticipantById(tp.getId());
            });
        }
    }

    /**
     * 删除会议模板信息
     *
     * @param id 会议模板ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuZjTemplateConferenceById(Long id)
    {
        BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme = new BusiMcuZjTemplatePollingScheme();
        busiMcuZjTemplatePollingScheme.setTemplateConferenceId(id);
        List<BusiMcuZjTemplatePollingScheme> pss = busiMcuZjTemplatePollingSchemeService.selectBusiMcuZjTemplatePollingSchemeList(busiMcuZjTemplatePollingScheme);
        if (!ObjectUtils.isEmpty(pss))
        {
            for (BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme2 : pss)
            {
                busiMcuZjTemplatePollingSchemeService.deleteBusiMcuZjTemplatePollingSchemeById(busiMcuZjTemplatePollingScheme2.getId());
            }
        }

        // 删除轮询方案
        busiMcuZjTemplatePollingSchemeService.deleteBusiMcuZjTemplatePollingSchemeById(id);
        BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(id);
        busiMcuZjTemplateConference.setMasterParticipantId(null);
        busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
        deleteParticipants(id);
        int c = busiMcuZjTemplateConferenceMapper.deleteBusiMcuZjTemplateConferenceById(id);
        if (c > 0 && busiMcuZjTemplateConference.getConferenceNumber() != null)
        {
            BusiMcuZjTemplateConference con = new BusiMcuZjTemplateConference();
            con.setConferenceNumber(busiMcuZjTemplateConference.getConferenceNumber());
            List<BusiMcuZjTemplateConference> cs = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceList(busiMcuZjTemplateConference);
            if (ObjectUtils.isEmpty(cs))
            {
                // 修改号码状态为闲置
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiMcuZjTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            // 若是自动创建的会议号，则删除模板的时候同步进行删除
            BusiConferenceNumber bcn = busiConferenceNumberService.selectBusiConferenceNumberById(busiMcuZjTemplateConference.getConferenceNumber());
            if (ConferenceNumberCreateType.convert(bcn.getCreateType()) == ConferenceNumberCreateType.AUTO)
            {
                busiConferenceNumberService.deleteBusiConferenceNumberById(busiMcuZjTemplateConference.getConferenceNumber());
            }
        }
        // 删除会议室
        try {
            McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjTemplateConference.getDeptId()).getMasterMcuZjBridge();
            String conferenceNumber = busiMcuZjTemplateConference.getConferenceNumber().toString();
            McuZjDeleteRoomTask mcuZjDeleteRoomTask = new McuZjDeleteRoomTask(mcuZjBridge.getBusiMcuZj().getId() + "_" + conferenceNumber, 20000, mcuZjBridge, conferenceNumber);
            delayTaskService.addTask(mcuZjDeleteRoomTask);
        } catch (Exception e) {
        }
        return c;
    }

    @Override
    public Page<BusiMcuZjTemplateConference> selectBusiMcuZjTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        Page<BusiMcuZjTemplateConference> tcs = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceListByKey(searchKey,deptId);
        return tcs;
    }

    @Override
    public List<BusiMcuZjTemplateConference> selectAllBusiMcuZjTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        return busiMcuZjTemplateConferenceMapper.selectAllBusiMcuZjTemplateConferenceListByKey(searchKey,deptId);
    }

    /**
     * <pre>删除模板与会者</pre>
     * @author lilinhai
     * @since 2021-01-27 12:22
     * @param id void
     */
    private void deleteParticipants(Long id)
    {
        // 根据模板ID删除默认视图的部门信息
        busiMcuZjTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuZjTemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuZjTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuZjTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);

        // 删除分屏信息
        busiMcuZjTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuZjTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);
        
        // 先删除与会者模板
        BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant = new BusiMcuZjTemplateParticipant();
        busiMcuZjTemplateParticipant.setTemplateConferenceId(id);
        
        List<Long> pIds = new ArrayList<>();
        List<BusiMcuZjTemplateParticipant> ps = busiMcuZjTemplateParticipantMapper.selectBusiMcuZjTemplateParticipantList(busiMcuZjTemplateParticipant);
        for (BusiMcuZjTemplateParticipant busiMcuZjTemplateParticipant2 : ps)
        {
            pIds.add(busiMcuZjTemplateParticipant2.getId());
        }
        
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuZjTemplateParticipantMapper.deleteBusiMcuZjTemplateParticipantByIds(pIds.toArray(new Long[pIds.size()]));
        }
        
        // 删除部门顺序信息
        BusiMcuZjTemplateDept busiMcuZjTemplateDept = new BusiMcuZjTemplateDept();
        busiMcuZjTemplateDept.setTemplateConferenceId(id);
        pIds = new ArrayList<>();
        List<BusiMcuZjTemplateDept> ds = busiMcuZjTemplateDeptMapper.selectBusiMcuZjTemplateDeptList(busiMcuZjTemplateDept);
        for (BusiMcuZjTemplateDept busiMcuZjTemplateDept2 : ds)
        {
            pIds.add(busiMcuZjTemplateDept2.getId());
        }
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuZjTemplateDeptMapper.deleteBusiMcuZjTemplateDeptByIds(pIds.toArray(new Long[pIds.size()]));
        }
    }

    private String generatePassword() {
        String password = "";
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            password += "" + random.nextInt(9);
        }
        return password;
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
