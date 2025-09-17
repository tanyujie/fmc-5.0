package com.paradisecloud.fcm.mcu.plc.service.impls;

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
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcTemplatePollingSchemeService;
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
public class BusiMcuPlcTemplateConferenceServiceImpl implements IBusiMcuPlcTemplateConferenceService 
{
    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    
    @Resource
    private BusiMcuPlcTemplateParticipantMapper busiMcuPlcTemplateParticipantMapper;
    
    @Resource
    private BusiMcuPlcTemplateDeptMapper busiMcuPlcTemplateDeptMapper;
    
    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;
    
    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;

    @Resource
    private IBusiMcuPlcTemplatePollingSchemeService busiMcuPlcTemplatePollingSchemeService;

    @Resource
    private BusiMcuPlcTemplateConferenceDefaultViewCellScreenMapper busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuPlcTemplateConferenceDefaultViewDeptMapper busiMcuPlcTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuPlcTemplateConferenceDefaultViewPaticipantMapper busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper;

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
        BusiMcuPlcTemplateConference tc = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(id);
        tc.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        if (tc.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)) {
            tc.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        } else {
            tc.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        }
        tc.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        tc.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setPollingInterval(jsonObj.getInteger("pollingInterval"));
        int c = busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1003432, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");

        // 根据模板ID删除分频信息
        BusiMcuPlcTemplateConferenceDefaultViewCellScreen busiMcuPlcTemplateConferenceDefaultViewCellScreenCon = new BusiMcuPlcTemplateConferenceDefaultViewCellScreen();
        busiMcuPlcTemplateConferenceDefaultViewCellScreenCon.setType(1);
        busiMcuPlcTemplateConferenceDefaultViewCellScreenCon.setTemplateConferenceId(id);
        List<BusiMcuPlcTemplateConferenceDefaultViewCellScreen> busiMcuPlcTemplateConferenceDefaultViewCellScreenList = busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuPlcTemplateConferenceDefaultViewCellScreenList(busiMcuPlcTemplateConferenceDefaultViewCellScreenCon);
        if (busiMcuPlcTemplateConferenceDefaultViewCellScreenList.size() > 0) {
            for (BusiMcuPlcTemplateConferenceDefaultViewCellScreen busiMcuPlcTemplateConferenceDefaultViewCellScreen : busiMcuPlcTemplateConferenceDefaultViewCellScreenList) {
                busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuPlcTemplateConferenceDefaultViewCellScreenById(busiMcuPlcTemplateConferenceDefaultViewCellScreen.getId());
            }
        }
//        busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuPlcTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);
        // 根据模板ID批量删除默认视图的参会者信息
        BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiMcuPlcTemplateConferenceDefaultViewPaticipantCon = new BusiMcuPlcTemplateConferenceDefaultViewPaticipant();
        busiMcuPlcTemplateConferenceDefaultViewPaticipantCon.setType(1);
        busiMcuPlcTemplateConferenceDefaultViewPaticipantCon.setTemplateConferenceId(id);
        List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant> busiMcuPlcTemplateConferenceDefaultViewPaticipantList = busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuPlcTemplateConferenceDefaultViewPaticipantList(busiMcuPlcTemplateConferenceDefaultViewPaticipantCon);
        if (busiMcuPlcTemplateConferenceDefaultViewPaticipantList.size() > 0) {
            for (BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiMcuPlcTemplateConferenceDefaultViewPaticipant : busiMcuPlcTemplateConferenceDefaultViewPaticipantList) {
                busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuPlcTemplateConferenceDefaultViewPaticipantById(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getId());
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
                        BusiMcuPlcTemplateParticipant busiTemplateParticipantCon = new BusiMcuPlcTemplateParticipant();
                        busiTemplateParticipantCon.setTemplateConferenceId(id);
                        busiTemplateParticipantCon.setTerminalId(jo.getLong("terminalId"));
                        List<BusiMcuPlcTemplateParticipant> busiTemplateParticipants = busiMcuPlcTemplateParticipantMapper.selectBusiMcuPlcTemplateParticipantList(busiTemplateParticipantCon);
                        if (busiTemplateParticipants.size() > 0) {
                            BusiMcuPlcTemplateParticipant busiTemplateParticipantTemp = busiTemplateParticipants.get(0);
                            jo.put("templateParticipantId", busiTemplateParticipantTemp.getId());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiMcuPlcTemplateConferenceDefaultViewPaticipant = new BusiMcuPlcTemplateConferenceDefaultViewPaticipant();
            busiMcuPlcTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiMcuPlcTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiMcuPlcTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiMcuPlcTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper.insertBusiMcuPlcTemplateConferenceDefaultViewPaticipant(busiMcuPlcTemplateConferenceDefaultViewPaticipant);
        }
        ja = jsonObj.getJSONArray("defaultViewCellScreens");

        if (tc.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT))
        {
            Assert.isTrue(ja == null || ja.size() == 0, "自动分屏下不能添加分屏信息");
        }
        else
        {
            Assert.isTrue(jsonObj.containsKey("defaultViewCellScreens"), "cellScreens分频信息不能为空");
            for (int i = 0; i < ja.size(); i++)
            {
                JSONObject jo = ja.getJSONObject(i);
                Assert.isTrue(jo.containsKey("cellSequenceNumber"), "cellSequenceNumber分频序号不能为空");
                Assert.isTrue(jo.containsKey("operation"), "operation分频操作类型不能为空");
                Assert.isTrue(jo.containsKey("isFixed"), "isFixed分频固定类型不能为空");
                BusiMcuPlcTemplateConferenceDefaultViewCellScreen busiMcuPlcTemplateConferenceDefaultViewCellScreen = new BusiMcuPlcTemplateConferenceDefaultViewCellScreen();
                busiMcuPlcTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiMcuPlcTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiMcuPlcTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiMcuPlcTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiMcuPlcTemplateConferenceDefaultViewPaticipant con = new BusiMcuPlcTemplateConferenceDefaultViewPaticipant();
                    List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant> ps = busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuPlcTemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiMcuPlcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }

                busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper.insertBusiMcuPlcTemplateConferenceDefaultViewCellScreen(busiMcuPlcTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        BusiMcuPlcTemplateConferenceDefaultViewDept busiMcuPlcTemplateConferenceDefaultViewDeptCon = new BusiMcuPlcTemplateConferenceDefaultViewDept();
        busiMcuPlcTemplateConferenceDefaultViewDeptCon.setType(1);
        busiMcuPlcTemplateConferenceDefaultViewDeptCon.setTemplateConferenceId(id);
        List<BusiMcuPlcTemplateConferenceDefaultViewDept> busiMcuPlcTemplateConferenceDefaultViewDeptList = busiMcuPlcTemplateConferenceDefaultViewDeptMapper.selectBusiMcuPlcTemplateConferenceDefaultViewDeptList(busiMcuPlcTemplateConferenceDefaultViewDeptCon);
        if (busiMcuPlcTemplateConferenceDefaultViewDeptList.size() > 0) {
            for (BusiMcuPlcTemplateConferenceDefaultViewDept busiMcuPlcTemplateConferenceDefaultViewDept : busiMcuPlcTemplateConferenceDefaultViewDeptList) {
                busiMcuPlcTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuPlcTemplateConferenceDefaultViewDeptById(busiMcuPlcTemplateConferenceDefaultViewDept.getId());
            }
        }

        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuPlcTemplateConferenceDefaultViewDept busiMcuPlcTemplateConferenceDefaultViewDept = new BusiMcuPlcTemplateConferenceDefaultViewDept();
            busiMcuPlcTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiMcuPlcTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiMcuPlcTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiMcuPlcTemplateConferenceDefaultViewDeptMapper.insertBusiMcuPlcTemplateConferenceDefaultViewDept(busiMcuPlcTemplateConferenceDefaultViewDept);
        }
    }

    @Override
    public String selectBusiMcuPlcTemplateConferenceCoverById(Long id)
    {
        BusiMcuPlcTemplateConference tc = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(id);
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
    public ModelBean selectBusiMcuPlcTemplateConferenceById(Long id)
    {
        BusiMcuPlcTemplateConference tc = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return getTemplateConferenceDetails(tc);
    }

    public ModelBean getTemplateConferenceDetails(BusiMcuPlcTemplateConference tc)
    {
        BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant = new BusiMcuPlcTemplateParticipant();
        busiMcuPlcTemplateParticipant.setTemplateConferenceId(tc.getId());
        List<ModelBean> pMbs = new ArrayList<>();
        List<BusiMcuPlcTemplateParticipant> ps = busiMcuPlcTemplateParticipantMapper.selectBusiMcuPlcTemplateParticipantList(busiMcuPlcTemplateParticipant);

        int onlineCount = 0;
        int weight = 0;
        String mainName = "";
        Map<Long, ModelBean> bpm = new HashMap<>();
        for (BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant2 : ps)
        {
            ModelBean pmb = new ModelBean(busiMcuPlcTemplateParticipant2);
            BusiTerminal bt = TerminalCache.getInstance().get(busiMcuPlcTemplateParticipant2.getTerminalId());
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
            if (!ObjectUtils.isEmpty(busiMcuPlcTemplateParticipant2.getBusinessProperties()))
            {
                tmb.remove("businessProperties");
            }

            if (!ObjectUtils.isEmpty(busiMcuPlcTemplateParticipant2.getAttendType()))
            {
                tmb.remove("attendType");
            }
            pmb.putAll(tmb);
            pMbs.add(pmb);

//            if (bt.getDeptId().longValue() == tc.getDeptId().longValue() && busiMcuPlcTemplateParticipant2.getWeight().intValue() >= weight)
//            {
//                weight = busiMcuPlcTemplateParticipant2.getWeight().intValue();
//                mainName = bt.getName();
//            }

            ModelBean mb0 = new ModelBean();
            mb0.putAll(pmb);
            mb0.remove("weight");
            bpm.put(busiMcuPlcTemplateParticipant2.getId(), mb0);
        }

        if (tc.getMasterParticipantId() != null)
        {
            mainName = bpm.get(tc.getMasterParticipantId()).get("name").toString();
        }

        BusiMcuPlcTemplateConferenceDefaultViewCellScreen con = new BusiMcuPlcTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(tc.getId());
        con.setType(1);
        List<BusiMcuPlcTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuPlcTemplateConferenceDefaultViewCellScreenList(con);

        BusiMcuPlcTemplateConferenceDefaultViewDept con1 = new BusiMcuPlcTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(tc.getId());
        con1.setType(1);
        List<BusiMcuPlcTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuPlcTemplateConferenceDefaultViewDeptMapper.selectBusiMcuPlcTemplateConferenceDefaultViewDeptList(con1);

        BusiMcuPlcTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuPlcTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(tc.getId());
        con2.setType(1);
        List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuPlcTemplateConferenceDefaultViewPaticipantList(con2);
        List<ModelBean> defaultViewPaticipantMbs = new ArrayList<>();
        for (BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiMcuPlcTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants) {
            ModelBean mb = bpm.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                ModelBean mbNew = new ModelBean();
                mbNew.putAll(mb);
                mbNew.put("weight", busiMcuPlcTemplateConferenceDefaultViewPaticipant.getWeight());
                mbNew.put("cellSequenceNumber", busiMcuPlcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbs.add(mbNew);
            }
        }
        //观众
        BusiMcuPlcTemplateConferenceDefaultViewCellScreen conGuest = new BusiMcuPlcTemplateConferenceDefaultViewCellScreen();
        conGuest.setTemplateConferenceId(tc.getId());
        conGuest.setType(2);
        List<BusiMcuPlcTemplateConferenceDefaultViewCellScreen> defaultViewCellScreensGuest = busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuPlcTemplateConferenceDefaultViewCellScreenList(conGuest);

        BusiMcuPlcTemplateConferenceDefaultViewDept con1Guest = new BusiMcuPlcTemplateConferenceDefaultViewDept();
        con1Guest.setTemplateConferenceId(tc.getId());
        con1Guest.setType(2);
        List<BusiMcuPlcTemplateConferenceDefaultViewDept> defaultViewDeptsGuest = busiMcuPlcTemplateConferenceDefaultViewDeptMapper.selectBusiMcuPlcTemplateConferenceDefaultViewDeptList(con1Guest);

        BusiMcuPlcTemplateConferenceDefaultViewPaticipant con2Guest = new BusiMcuPlcTemplateConferenceDefaultViewPaticipant();
        con2Guest.setTemplateConferenceId(tc.getId());
        con2Guest.setType(2);
        List<BusiMcuPlcTemplateConferenceDefaultViewPaticipant> defaultViewPaticipantsGuest = busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuPlcTemplateConferenceDefaultViewPaticipantList(con2Guest);
        List<ModelBean> defaultViewPaticipantMbsGuest = new ArrayList<>();
        for (BusiMcuPlcTemplateConferenceDefaultViewPaticipant busiMcuPlcTemplateConferenceDefaultViewPaticipant : defaultViewPaticipantsGuest) {
            ModelBean mb = bpm.get(busiMcuPlcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                ModelBean mbNew = new ModelBean();
                mbNew.putAll(mb);
                mbNew.put("weight", busiMcuPlcTemplateConferenceDefaultViewPaticipant.getWeight());
                mbNew.put("cellSequenceNumber", busiMcuPlcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbsGuest.add(mbNew);
            }
        }

        BusiMcuPlcTemplateDept tdCon = new BusiMcuPlcTemplateDept();
        tdCon.setTemplateConferenceId(tc.getId());
        List<BusiMcuPlcTemplateDept> tds = busiMcuPlcTemplateDeptMapper.selectBusiMcuPlcTemplateDeptList(tdCon);

        ModelBean tcmb = new ModelBean(tc);
        if (tc.getMasterParticipantId() != null)
        {
            tcmb.put("masterTerminalId", bpm.get(tc.getMasterParticipantId()).get("terminalId"));
        }
        tcmb.put("tenantId", "");
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
        if (tc.getConferenceNumber() != null && McuPlcConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_PLC)))
        {
            mb.put("meetingJoinedCount", McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_PLC)).getAttendeeCountingStatistics().getMeetingJoinedCount());
        }
        mb.put("mainVenue", mainName);
        mb.put("isStart", tc.getConferenceNumber() != null && McuPlcConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_PLC)));
        mb.put("mcuType", McuType.MCU_PLC.getCode());
        return mb;
    }

    /**
     * 查询会议模板列表
     *
     * @param busiMcuPlcTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuPlcTemplateConference> selectBusiMcuPlcTemplateConferenceList(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference)
    {
        Assert.notNull(busiMcuPlcTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectBusiMcuPlcTemplateConferenceListWithoutBusinessFieldType(busiMcuPlcTemplateConference);
    }

    /**
     * 查询会议模板列表
     *
     * @param busiMcuPlcTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuPlcTemplateConference> selectAllBusiMcuPlcTemplateConferenceList(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference)
    {
        Assert.notNull(busiMcuPlcTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectAllBusiMcuPlcTemplateConferenceListWithoutBusinessFieldType(busiMcuPlcTemplateConference);
    }

    public List<BusiMcuPlcTemplateConference> selectBusiMcuPlcTemplateConferenceListWithoutBusinessFieldType(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference)
    {
        // 绑定终端归属部门
        if (busiMcuPlcTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuPlcTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuPlcTemplateConference> tcs = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceList(busiMcuPlcTemplateConference);
        return tcs;
    }

    public List<BusiMcuPlcTemplateConference> selectAllBusiMcuPlcTemplateConferenceListWithoutBusinessFieldType(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference)
    {
        // 绑定终端归属部门
        if (busiMcuPlcTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuPlcTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuPlcTemplateConference> tcs = busiMcuPlcTemplateConferenceMapper.selectAllBusiMcuPlcTemplateConferenceList(busiMcuPlcTemplateConference);
        return tcs;
    }

    /**
     * <pre>模板集合转换成modelBean</pre>
     * @author lilinhai
     * @since 2021-01-30 14:04
     * @param tcs
     * @return List<ModelBean>
     */
    public List<ModelBean> toModelBean(List<BusiMcuPlcTemplateConference> tcs)
    {
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuPlcTemplateConference tc : tcs)
        {
            mbs.add(getTemplateConferenceDetails(tc));
        }

        return mbs;
    }

    @Override
    public List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType)
    {
        BusinessFieldType.convert(businessFieldType);
        return busiMcuPlcTemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
    }

    /**
     * 新增会议模板
     *
     * @param busiMcuPlcTemplateConference 会议模板
     * @param busiMcuPlcTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int insertBusiMcuPlcTemplateConference(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference, Long masterTerminalId, List<BusiMcuPlcTemplateParticipant> busiMcuPlcTemplateParticipants, List<BusiMcuPlcTemplateDept> templateDepts)
    {
        busiMcuPlcTemplateConference.setCreateTime(new Date());

        Assert.notNull(busiMcuPlcTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        Assert.notNull(busiMcuPlcTemplateConference.getCreateType(), "会议模板创建类型不能为空！");
        Assert.notNull(busiMcuPlcTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        if (YesOrNo.convert(busiMcuPlcTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
        {
            Assert.notNull(busiMcuPlcTemplateConference.getConferenceNumber(), "会议号不能为空！");
        }

        // 绑定终端归属部门
        if (busiMcuPlcTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuPlcTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        if (busiMcuPlcTemplateConference.getDeptId() == null)
        {
            throw new SystemException(1004543, "添加会议模板，部门ID不能为空！");
        }

        if (busiMcuPlcTemplateConference.getCreateUserId() == null || busiMcuPlcTemplateConference.getCreateUserName() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiMcuPlcTemplateConference.setCreateUserId(loginUser.getUser().getUserId());
                busiMcuPlcTemplateConference.setCreateUserName(loginUser.getUser().getUserName());
            }
        }

        // 校验会议类型
        //ConferenceType.convert(busiMcuPlcTemplateConference.getType());

        int c = busiMcuPlcTemplateConferenceMapper.insertBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
        if (c > 0)
        {
            if (!ObjectUtils.isEmpty(busiMcuPlcTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant : busiMcuPlcTemplateParticipants)
                {
                    busiMcuPlcTemplateParticipant.setTemplateConferenceId(busiMcuPlcTemplateConference.getId());
                    busiMcuPlcTemplateParticipant.setCreateTime(new Date());
                    busiMcuPlcTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    try
                    {
                        busiMcuPlcTemplateParticipantMapper.insertBusiMcuPlcTemplateParticipant(busiMcuPlcTemplateParticipant);
                    }
                    catch (Exception e)
                    {
                        LoggerFactory.getLogger(getClass()).error("终端ID已不存在：" + busiMcuPlcTemplateParticipant.getTerminalId(), e);
                        throw new RuntimeException("终端ID已不存在：" + busiMcuPlcTemplateParticipant.getTerminalId());
                    }
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuPlcTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiMcuPlcTemplateConference.setMasterParticipantId(busiMcuPlcTemplateParticipant.getId());
                        busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuPlcTemplateDept busiMcuPlcTemplateDept : templateDepts)
                {
                    busiMcuPlcTemplateDept.setTemplateConferenceId(busiMcuPlcTemplateConference.getId());
                    busiMcuPlcTemplateDept.setCreateTime(new Date());
                    busiMcuPlcTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuPlcTemplateDeptMapper.insertBusiMcuPlcTemplateDept(busiMcuPlcTemplateDept);
                }
            }

            if (busiMcuPlcTemplateConference.getConferenceNumber() == null) {
                // 生成会议号
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiMcuPlcTemplateConference.getDeptId(), McuType.MCU_PLC.getCode());
                busiMcuPlcTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
                busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
            }

            if (busiMcuPlcTemplateConference.getConferenceNumber() != null)
            {
                // 修改号码状态为已绑定
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiMcuPlcTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            // 添加默认轮询模板
            BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme = new BusiMcuPlcTemplatePollingScheme();
            busiMcuPlcTemplatePollingScheme.setEnableStatus(YesOrNo.YES.getValue());
            busiMcuPlcTemplatePollingScheme.setIsBroadcast(YesOrNo.NO.getValue());
            busiMcuPlcTemplatePollingScheme.setIsDisplaySelf(PanePlacementSelfPaneMode.OFF.getValue());
            busiMcuPlcTemplatePollingScheme.setSchemeName("全局轮询");
            busiMcuPlcTemplatePollingScheme.setLayout(OneSplitScreen.LAYOUT);
            busiMcuPlcTemplatePollingScheme.setIsFill(YesOrNo.YES.getValue());
            busiMcuPlcTemplatePollingScheme.setPollingInterval(10);
            busiMcuPlcTemplatePollingScheme.setPollingStrategy(PollingStrategy.GLOBAL.getValue());
            busiMcuPlcTemplatePollingScheme.setTemplateConferenceId(busiMcuPlcTemplateConference.getId());
            busiMcuPlcTemplatePollingSchemeService.insertBusiMcuPlcTemplatePollingScheme(busiMcuPlcTemplatePollingScheme, null, null);
        }
        return c;
    }

    /**
     * 修改会议模板
     *
     * @param busiMcuPlcTemplateConference 会议模板
     * @param busiMcuPlcTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int updateBusiMcuPlcTemplateConference(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference, Long masterTerminalId, List<BusiMcuPlcTemplateParticipant> busiMcuPlcTemplateParticipants, List<BusiMcuPlcTemplateDept> templateDepts)
    {
        Assert.notNull(busiMcuPlcTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiMcuPlcTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        busiMcuPlcTemplateConference.setUpdateTime(new Date());
        if (YesOrNo.convert(busiMcuPlcTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
        {
            Assert.notNull(busiMcuPlcTemplateConference.getConferenceNumber(), "会议号不能为空！");
        }

        // 校验会议类型
        //ConferenceType.convert(busiMcuPlcTemplateConference.getType());

        // 先置空主会场配置
        busiMcuPlcTemplateConference.setMasterParticipantId(null);
        busiMcuPlcTemplateConference.setCreateUserId(null);
        busiMcuPlcTemplateConference.setCreateUserName(null);
        int c = busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
        if (c > 0)
        {
            doParticipantUpdate(busiMcuPlcTemplateConference, busiMcuPlcTemplateParticipants, masterTerminalId);
            doDeptUpdate(busiMcuPlcTemplateConference, templateDepts);

            // 修改号码状态为已绑定
            if (busiMcuPlcTemplateConference.getConferenceNumber() != null)
            {
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiMcuPlcTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            BusiMcuPlcTemplateConference old = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiMcuPlcTemplateConference.getId());
            if (old.getConferenceNumber() != null && !old.getConferenceNumber().equals(busiMcuPlcTemplateConference.getConferenceNumber()))
            {
                BusiMcuPlcTemplateConference con = new BusiMcuPlcTemplateConference();
                con.setConferenceNumber(old.getConferenceNumber());
                List<BusiMcuPlcTemplateConference> bcs = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceList(con);
                if (ObjectUtils.isEmpty(bcs))
                {
                    BusiConferenceNumber cn = new BusiConferenceNumber();
                    cn.setId(old.getConferenceNumber());
                    cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
                    busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
                }
            }
        }
        return c;
    }

    private void doDeptUpdate(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference, List<BusiMcuPlcTemplateDept> templateDepts)
    {
        BusiMcuPlcTemplateDept deptCon = new BusiMcuPlcTemplateDept();
        deptCon.setTemplateConferenceId(busiMcuPlcTemplateConference.getId());
        List<BusiMcuPlcTemplateDept> ds = busiMcuPlcTemplateDeptMapper.selectBusiMcuPlcTemplateDeptList(deptCon);
        Map<Long, BusiMcuPlcTemplateDept> oldMap = new HashMap<>();
        for (BusiMcuPlcTemplateDept busiMcuPlcTemplateDept : ds)
        {
            oldMap.put(busiMcuPlcTemplateDept.getDeptId(), busiMcuPlcTemplateDept);
        }

        // 添加模板部门顺序信息
        if (!ObjectUtils.isEmpty(templateDepts))
        {
            for (BusiMcuPlcTemplateDept busiMcuPlcTemplateDept : templateDepts)
            {
                busiMcuPlcTemplateDept.setTemplateConferenceId(busiMcuPlcTemplateConference.getId());
                BusiMcuPlcTemplateDept oldTd = oldMap.remove(busiMcuPlcTemplateDept.getDeptId());
                if (oldTd != null)
                {
                    oldTd.setUpdateTime(new Date());
                    oldTd.setWeight(busiMcuPlcTemplateDept.getWeight());
                    busiMcuPlcTemplateDept = oldTd;
                    busiMcuPlcTemplateDeptMapper.updateBusiMcuPlcTemplateDept(busiMcuPlcTemplateDept);
                }
                else
                {
                    busiMcuPlcTemplateDept.setCreateTime(new Date());
                    busiMcuPlcTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuPlcTemplateDeptMapper.insertBusiMcuPlcTemplateDept(busiMcuPlcTemplateDept);
                }
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((deptId, td) -> {
                busiMcuPlcTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuPlcTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(td.getTemplateConferenceId(), td.getDeptId());
                busiMcuPlcTemplateDeptMapper.deleteBusiMcuPlcTemplateDeptById(td.getId());
            });
        }
    }

    private void doParticipantUpdate(BusiMcuPlcTemplateConference busiMcuPlcTemplateConference, List<BusiMcuPlcTemplateParticipant> busiMcuPlcTemplateParticipants, Long masterTerminalId)
    {
        BusiMcuPlcTemplateParticipant query = new BusiMcuPlcTemplateParticipant();
        query.setTemplateConferenceId(busiMcuPlcTemplateConference.getId());
        List<BusiMcuPlcTemplateParticipant> ps = busiMcuPlcTemplateParticipantMapper.selectBusiMcuPlcTemplateParticipantList(query);
        Map<Long, BusiMcuPlcTemplateParticipant> oldMap = new HashMap<>();
        for (BusiMcuPlcTemplateParticipant tp : ps)
        {
            oldMap.put(tp.getTerminalId(), tp);
        }

        if (!ObjectUtils.isEmpty(busiMcuPlcTemplateParticipants))
        {
            for (BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant : busiMcuPlcTemplateParticipants)
            {
                busiMcuPlcTemplateParticipant.setTemplateConferenceId(busiMcuPlcTemplateConference.getId());
                BusiMcuPlcTemplateParticipant oldTp = oldMap.remove(busiMcuPlcTemplateParticipant.getTerminalId());
                if (oldTp != null)
                {
                    oldTp.setWeight(busiMcuPlcTemplateParticipant.getWeight());
                    oldTp.setUpdateTime(new Date());
                    oldTp.setAttendType(busiMcuPlcTemplateParticipant.getAttendType());
                    oldTp.setBusinessProperties(busiMcuPlcTemplateParticipant.getBusinessProperties());
                    busiMcuPlcTemplateParticipantMapper.updateBusiMcuPlcTemplateParticipant(oldTp);
                    busiMcuPlcTemplateParticipant = oldTp;
                }
                else
                {
                    // 新增
                    busiMcuPlcTemplateParticipant.setCreateTime(new Date());
                    busiMcuPlcTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuPlcTemplateParticipantMapper.insertBusiMcuPlcTemplateParticipant(busiMcuPlcTemplateParticipant);
                }

                if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuPlcTemplateParticipant.getTerminalId().longValue())
                {
                    // 设置模板会议中配置的主会场参会终端
                    busiMcuPlcTemplateConference.setMasterParticipantId(busiMcuPlcTemplateParticipant.getId());
                    busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
                }
            }

            if (masterTerminalId == null)
            {
                // 设置模板会议中配置的主会场参会终端
                busiMcuPlcTemplateConference.setMasterParticipantId(null);
                busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((terminalId, tp) -> {
                busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuPlcTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(tp.getId());
                busiMcuPlcTemplateParticipantMapper.deleteBusiMcuPlcTemplateParticipantById(tp.getId());
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
    public int deleteBusiMcuPlcTemplateConferenceById(Long id)
    {
        BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme = new BusiMcuPlcTemplatePollingScheme();
        busiMcuPlcTemplatePollingScheme.setTemplateConferenceId(id);
        List<BusiMcuPlcTemplatePollingScheme> pss = busiMcuPlcTemplatePollingSchemeService.selectBusiMcuPlcTemplatePollingSchemeList(busiMcuPlcTemplatePollingScheme);
        if (!ObjectUtils.isEmpty(pss))
        {
            for (BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme2 : pss)
            {
                busiMcuPlcTemplatePollingSchemeService.deleteBusiMcuPlcTemplatePollingSchemeById(busiMcuPlcTemplatePollingScheme2.getId());
            }
        }

        // 删除轮询方案
        busiMcuPlcTemplatePollingSchemeService.deleteBusiMcuPlcTemplatePollingSchemeById(id);
        BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(id);
        busiMcuPlcTemplateConference.setMasterParticipantId(null);
        busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
        deleteParticipants(id);
        int c = busiMcuPlcTemplateConferenceMapper.deleteBusiMcuPlcTemplateConferenceById(id);
        if (c > 0 && busiMcuPlcTemplateConference.getConferenceNumber() != null)
        {
            BusiMcuPlcTemplateConference con = new BusiMcuPlcTemplateConference();
            con.setConferenceNumber(busiMcuPlcTemplateConference.getConferenceNumber());
            List<BusiMcuPlcTemplateConference> cs = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceList(busiMcuPlcTemplateConference);
            if (ObjectUtils.isEmpty(cs))
            {
                // 修改号码状态为闲置
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiMcuPlcTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            // 若是自动创建的会议号，则删除模板的时候同步进行删除
            BusiConferenceNumber bcn = busiConferenceNumberService.selectBusiConferenceNumberById(busiMcuPlcTemplateConference.getConferenceNumber());
            if (ConferenceNumberCreateType.convert(bcn.getCreateType()) == ConferenceNumberCreateType.AUTO)
            {
                busiConferenceNumberService.deleteBusiConferenceNumberById(busiMcuPlcTemplateConference.getConferenceNumber());
            }
        }
        return c;
    }

    @Override
    public Page<BusiMcuPlcTemplateConference> selectBusiMcuPlcTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        Page<BusiMcuPlcTemplateConference> tcs = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceListByKey(searchKey,deptId);
        return tcs;
    }

    @Override
    public List<BusiMcuPlcTemplateConference> selectAllBusiMcuPlcTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        return busiMcuPlcTemplateConferenceMapper.selectAllBusiMcuPlcTemplateConferenceListByKey(searchKey,deptId);
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
        busiMcuPlcTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuPlcTemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuPlcTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuPlcTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);

        // 删除分屏信息
        busiMcuPlcTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuPlcTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);
        
        // 先删除与会者模板
        BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant = new BusiMcuPlcTemplateParticipant();
        busiMcuPlcTemplateParticipant.setTemplateConferenceId(id);
        
        List<Long> pIds = new ArrayList<>();
        List<BusiMcuPlcTemplateParticipant> ps = busiMcuPlcTemplateParticipantMapper.selectBusiMcuPlcTemplateParticipantList(busiMcuPlcTemplateParticipant);
        for (BusiMcuPlcTemplateParticipant busiMcuPlcTemplateParticipant2 : ps)
        {
            pIds.add(busiMcuPlcTemplateParticipant2.getId());
        }
        
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuPlcTemplateParticipantMapper.deleteBusiMcuPlcTemplateParticipantByIds(pIds.toArray(new Long[pIds.size()]));
        }
        
        // 删除部门顺序信息
        BusiMcuPlcTemplateDept busiMcuPlcTemplateDept = new BusiMcuPlcTemplateDept();
        busiMcuPlcTemplateDept.setTemplateConferenceId(id);
        pIds = new ArrayList<>();
        List<BusiMcuPlcTemplateDept> ds = busiMcuPlcTemplateDeptMapper.selectBusiMcuPlcTemplateDeptList(busiMcuPlcTemplateDept);
        for (BusiMcuPlcTemplateDept busiMcuPlcTemplateDept2 : ds)
        {
            pIds.add(busiMcuPlcTemplateDept2.getId());
        }
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuPlcTemplateDeptMapper.deleteBusiMcuPlcTemplateDeptByIds(pIds.toArray(new Long[pIds.size()]));
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
