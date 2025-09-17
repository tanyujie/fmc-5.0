package com.paradisecloud.fcm.mcu.kdc.service.impls;

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
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplateConferenceService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplatePollingSchemeService;
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
public class BusiMcuKdcTemplateConferenceServiceImpl implements IBusiMcuKdcTemplateConferenceService 
{
    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;
    
    @Resource
    private BusiMcuKdcTemplateParticipantMapper busiMcuKdcTemplateParticipantMapper;
    
    @Resource
    private BusiMcuKdcTemplateDeptMapper busiMcuKdcTemplateDeptMapper;
    
    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;
    
//    @Resource
//    private BusiConferenceNumberMapper busiConferenceNumberMapper;

    @Resource
    private IBusiMcuKdcTemplatePollingSchemeService busiMcuKdcTemplatePollingSchemeService;

    @Resource
    private BusiMcuKdcTemplateConferenceDefaultViewCellScreenMapper busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuKdcTemplateConferenceDefaultViewDeptMapper busiMcuKdcTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuKdcTemplateConferenceDefaultViewPaticipantMapper busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper;

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
        BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(id);
        tc.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        if (tc.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)) {
            tc.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        } else {
            tc.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        }
        tc.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        tc.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setPollingInterval(jsonObj.getInteger("pollingInterval"));
        int c = busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1003432, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");

        // 根据模板ID删除分频信息
        BusiMcuKdcTemplateConferenceDefaultViewCellScreen busiMcuKdcTemplateConferenceDefaultViewCellScreenCon = new BusiMcuKdcTemplateConferenceDefaultViewCellScreen();
        busiMcuKdcTemplateConferenceDefaultViewCellScreenCon.setType(1);
        busiMcuKdcTemplateConferenceDefaultViewCellScreenCon.setTemplateConferenceId(id);
        List<BusiMcuKdcTemplateConferenceDefaultViewCellScreen> busiMcuKdcTemplateConferenceDefaultViewCellScreenList = busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuKdcTemplateConferenceDefaultViewCellScreenList(busiMcuKdcTemplateConferenceDefaultViewCellScreenCon);
        if (busiMcuKdcTemplateConferenceDefaultViewCellScreenList.size() > 0) {
            for (BusiMcuKdcTemplateConferenceDefaultViewCellScreen busiMcuKdcTemplateConferenceDefaultViewCellScreen : busiMcuKdcTemplateConferenceDefaultViewCellScreenList) {
                busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuKdcTemplateConferenceDefaultViewCellScreenById(busiMcuKdcTemplateConferenceDefaultViewCellScreen.getId());
            }
        }
//        busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuKdcTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);
        // 根据模板ID批量删除默认视图的参会者信息
        BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiMcuKdcTemplateConferenceDefaultViewPaticipantCon = new BusiMcuKdcTemplateConferenceDefaultViewPaticipant();
        busiMcuKdcTemplateConferenceDefaultViewPaticipantCon.setType(1);
        busiMcuKdcTemplateConferenceDefaultViewPaticipantCon.setTemplateConferenceId(id);
        List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant> busiMcuKdcTemplateConferenceDefaultViewPaticipantList = busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuKdcTemplateConferenceDefaultViewPaticipantList(busiMcuKdcTemplateConferenceDefaultViewPaticipantCon);
        if (busiMcuKdcTemplateConferenceDefaultViewPaticipantList.size() > 0) {
            for (BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiMcuKdcTemplateConferenceDefaultViewPaticipant : busiMcuKdcTemplateConferenceDefaultViewPaticipantList) {
                busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuKdcTemplateConferenceDefaultViewPaticipantById(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getId());
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
                        BusiMcuKdcTemplateParticipant busiTemplateParticipantCon = new BusiMcuKdcTemplateParticipant();
                        busiTemplateParticipantCon.setTemplateConferenceId(id);
                        busiTemplateParticipantCon.setTerminalId(jo.getLong("terminalId"));
                        List<BusiMcuKdcTemplateParticipant> busiTemplateParticipants = busiMcuKdcTemplateParticipantMapper.selectBusiMcuKdcTemplateParticipantList(busiTemplateParticipantCon);
                        if (busiTemplateParticipants.size() > 0) {
                            BusiMcuKdcTemplateParticipant busiTemplateParticipantTemp = busiTemplateParticipants.get(0);
                            jo.put("templateParticipantId", busiTemplateParticipantTemp.getId());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiMcuKdcTemplateConferenceDefaultViewPaticipant = new BusiMcuKdcTemplateConferenceDefaultViewPaticipant();
            busiMcuKdcTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiMcuKdcTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiMcuKdcTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiMcuKdcTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper.insertBusiMcuKdcTemplateConferenceDefaultViewPaticipant(busiMcuKdcTemplateConferenceDefaultViewPaticipant);
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
                BusiMcuKdcTemplateConferenceDefaultViewCellScreen busiMcuKdcTemplateConferenceDefaultViewCellScreen = new BusiMcuKdcTemplateConferenceDefaultViewCellScreen();
                busiMcuKdcTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiMcuKdcTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiMcuKdcTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiMcuKdcTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiMcuKdcTemplateConferenceDefaultViewPaticipant con = new BusiMcuKdcTemplateConferenceDefaultViewPaticipant();
                    List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant> ps = busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuKdcTemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiMcuKdcTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }

                busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper.insertBusiMcuKdcTemplateConferenceDefaultViewCellScreen(busiMcuKdcTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        BusiMcuKdcTemplateConferenceDefaultViewDept busiMcuKdcTemplateConferenceDefaultViewDeptCon = new BusiMcuKdcTemplateConferenceDefaultViewDept();
        busiMcuKdcTemplateConferenceDefaultViewDeptCon.setType(1);
        busiMcuKdcTemplateConferenceDefaultViewDeptCon.setTemplateConferenceId(id);
        List<BusiMcuKdcTemplateConferenceDefaultViewDept> busiMcuKdcTemplateConferenceDefaultViewDeptList = busiMcuKdcTemplateConferenceDefaultViewDeptMapper.selectBusiMcuKdcTemplateConferenceDefaultViewDeptList(busiMcuKdcTemplateConferenceDefaultViewDeptCon);
        if (busiMcuKdcTemplateConferenceDefaultViewDeptList.size() > 0) {
            for (BusiMcuKdcTemplateConferenceDefaultViewDept busiMcuKdcTemplateConferenceDefaultViewDept : busiMcuKdcTemplateConferenceDefaultViewDeptList) {
                busiMcuKdcTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuKdcTemplateConferenceDefaultViewDeptById(busiMcuKdcTemplateConferenceDefaultViewDept.getId());
            }
        }

        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuKdcTemplateConferenceDefaultViewDept busiMcuKdcTemplateConferenceDefaultViewDept = new BusiMcuKdcTemplateConferenceDefaultViewDept();
            busiMcuKdcTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiMcuKdcTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiMcuKdcTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiMcuKdcTemplateConferenceDefaultViewDeptMapper.insertBusiMcuKdcTemplateConferenceDefaultViewDept(busiMcuKdcTemplateConferenceDefaultViewDept);
        }
    }

    @Override
    public String selectBusiMcuKdcTemplateConferenceCoverById(Long id)
    {
        BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(id);
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
    public ModelBean selectBusiMcuKdcTemplateConferenceById(Long id)
    {
        BusiMcuKdcTemplateConference tc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return getTemplateConferenceDetails(tc);
    }

    public ModelBean getTemplateConferenceDetails(BusiMcuKdcTemplateConference tc)
    {
        BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant = new BusiMcuKdcTemplateParticipant();
        busiMcuKdcTemplateParticipant.setTemplateConferenceId(tc.getId());
        List<ModelBean> pMbs = new ArrayList<>();
        List<BusiMcuKdcTemplateParticipant> ps = busiMcuKdcTemplateParticipantMapper.selectBusiMcuKdcTemplateParticipantList(busiMcuKdcTemplateParticipant);

        int onlineCount = 0;
        int weight = 0;
        String mainName = "";
        Map<Long, ModelBean> bpm = new HashMap<>();
        for (BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant2 : ps)
        {
            ModelBean pmb = new ModelBean(busiMcuKdcTemplateParticipant2);
            BusiTerminal bt = TerminalCache.getInstance().get(busiMcuKdcTemplateParticipant2.getTerminalId());
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
            if (!ObjectUtils.isEmpty(busiMcuKdcTemplateParticipant2.getBusinessProperties()))
            {
                tmb.remove("businessProperties");
            }

            if (!ObjectUtils.isEmpty(busiMcuKdcTemplateParticipant2.getAttendType()))
            {
                tmb.remove("attendType");
            }
            pmb.putAll(tmb);
            pMbs.add(pmb);

//            if (bt.getDeptId().longValue() == tc.getDeptId().longValue() && busiMcuKdcTemplateParticipant2.getWeight().intValue() >= weight)
//            {
//                weight = busiMcuKdcTemplateParticipant2.getWeight().intValue();
//                mainName = bt.getName();
//            }

            ModelBean mb0 = new ModelBean();
            mb0.putAll(pmb);
            mb0.remove("weight");
            bpm.put(busiMcuKdcTemplateParticipant2.getId(), mb0);
        }

        if (tc.getMasterParticipantId() != null)
        {
            mainName = bpm.get(tc.getMasterParticipantId()).get("name").toString();
        }

        BusiMcuKdcTemplateConferenceDefaultViewCellScreen con = new BusiMcuKdcTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(tc.getId());
        con.setType(1);
        List<BusiMcuKdcTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuKdcTemplateConferenceDefaultViewCellScreenList(con);

        BusiMcuKdcTemplateConferenceDefaultViewDept con1 = new BusiMcuKdcTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(tc.getId());
        con1.setType(1);
        List<BusiMcuKdcTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuKdcTemplateConferenceDefaultViewDeptMapper.selectBusiMcuKdcTemplateConferenceDefaultViewDeptList(con1);

        BusiMcuKdcTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuKdcTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(tc.getId());
        con2.setType(1);
        List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuKdcTemplateConferenceDefaultViewPaticipantList(con2);
        List<ModelBean> defaultViewPaticipantMbs = new ArrayList<>();
        for (BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiMcuKdcTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants) {
            ModelBean mb = bpm.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                ModelBean mbNew = new ModelBean();
                mbNew.putAll(mb);
                mbNew.put("weight", busiMcuKdcTemplateConferenceDefaultViewPaticipant.getWeight());
                mbNew.put("cellSequenceNumber", busiMcuKdcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbs.add(mbNew);
            }
        }
        //观众
        BusiMcuKdcTemplateConferenceDefaultViewCellScreen conGuest = new BusiMcuKdcTemplateConferenceDefaultViewCellScreen();
        conGuest.setTemplateConferenceId(tc.getId());
        conGuest.setType(2);
        List<BusiMcuKdcTemplateConferenceDefaultViewCellScreen> defaultViewCellScreensGuest = busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuKdcTemplateConferenceDefaultViewCellScreenList(conGuest);

        BusiMcuKdcTemplateConferenceDefaultViewDept con1Guest = new BusiMcuKdcTemplateConferenceDefaultViewDept();
        con1Guest.setTemplateConferenceId(tc.getId());
        con1Guest.setType(2);
        List<BusiMcuKdcTemplateConferenceDefaultViewDept> defaultViewDeptsGuest = busiMcuKdcTemplateConferenceDefaultViewDeptMapper.selectBusiMcuKdcTemplateConferenceDefaultViewDeptList(con1Guest);

        BusiMcuKdcTemplateConferenceDefaultViewPaticipant con2Guest = new BusiMcuKdcTemplateConferenceDefaultViewPaticipant();
        con2Guest.setTemplateConferenceId(tc.getId());
        con2Guest.setType(2);
        List<BusiMcuKdcTemplateConferenceDefaultViewPaticipant> defaultViewPaticipantsGuest = busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuKdcTemplateConferenceDefaultViewPaticipantList(con2Guest);
        List<ModelBean> defaultViewPaticipantMbsGuest = new ArrayList<>();
        for (BusiMcuKdcTemplateConferenceDefaultViewPaticipant busiMcuKdcTemplateConferenceDefaultViewPaticipant : defaultViewPaticipantsGuest) {
            ModelBean mb = bpm.get(busiMcuKdcTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                ModelBean mbNew = new ModelBean();
                mbNew.putAll(mb);
                mbNew.put("weight", busiMcuKdcTemplateConferenceDefaultViewPaticipant.getWeight());
                mbNew.put("cellSequenceNumber", busiMcuKdcTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbsGuest.add(mbNew);
            }
        }

        BusiMcuKdcTemplateDept tdCon = new BusiMcuKdcTemplateDept();
        tdCon.setTemplateConferenceId(tc.getId());
        List<BusiMcuKdcTemplateDept> tds = busiMcuKdcTemplateDeptMapper.selectBusiMcuKdcTemplateDeptList(tdCon);

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
        if (tc.getConferenceNumber() != null && McuKdcConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_KDC)))
        {
            mb.put("meetingJoinedCount", McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_KDC)).getAttendeeCountingStatistics().getMeetingJoinedCount());
        }
        mb.put("mainVenue", mainName);
        mb.put("isStart", tc.getConferenceNumber() != null && McuKdcConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_KDC)));
        mb.put("mcuType", McuType.MCU_KDC.getCode());
        return mb;
    }

    /**
     * 查询会议模板列表
     *
     * @param busiMcuKdcTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuKdcTemplateConference> selectBusiMcuKdcTemplateConferenceList(BusiMcuKdcTemplateConference busiMcuKdcTemplateConference)
    {
        Assert.notNull(busiMcuKdcTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectBusiMcuKdcTemplateConferenceListWithoutBusinessFieldType(busiMcuKdcTemplateConference);
    }

    /**
     * 查询会议模板列表
     *
     * @param busiMcuKdcTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuKdcTemplateConference> selectAllBusiMcuKdcTemplateConferenceList(BusiMcuKdcTemplateConference busiMcuKdcTemplateConference)
    {
        Assert.notNull(busiMcuKdcTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectAllBusiMcuKdcTemplateConferenceListWithoutBusinessFieldType(busiMcuKdcTemplateConference);
    }

    public List<BusiMcuKdcTemplateConference> selectBusiMcuKdcTemplateConferenceListWithoutBusinessFieldType(BusiMcuKdcTemplateConference busiMcuKdcTemplateConference)
    {
        // 绑定终端归属部门
        if (busiMcuKdcTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuKdcTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuKdcTemplateConference> tcs = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceList(busiMcuKdcTemplateConference);
        return tcs;
    }

    public List<BusiMcuKdcTemplateConference> selectAllBusiMcuKdcTemplateConferenceListWithoutBusinessFieldType(BusiMcuKdcTemplateConference busiMcuKdcTemplateConference)
    {
        // 绑定终端归属部门
        if (busiMcuKdcTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuKdcTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuKdcTemplateConference> tcs = busiMcuKdcTemplateConferenceMapper.selectAllBusiMcuKdcTemplateConferenceList(busiMcuKdcTemplateConference);
        return tcs;
    }

    /**
     * <pre>模板集合转换成modelBean</pre>
     * @author lilinhai
     * @since 2021-01-30 14:04
     * @param tcs
     * @return List<ModelBean>
     */
    public List<ModelBean> toModelBean(List<BusiMcuKdcTemplateConference> tcs)
    {
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuKdcTemplateConference tc : tcs)
        {
            mbs.add(getTemplateConferenceDetails(tc));
        }

        return mbs;
    }

    @Override
    public List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType)
    {
        BusinessFieldType.convert(businessFieldType);
        return busiMcuKdcTemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
    }

    /**
     * 新增会议模板
     *
     * @param busiMcuKdcTemplateConference 会议模板
     * @param busiMcuKdcTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int insertBusiMcuKdcTemplateConference(BusiMcuKdcTemplateConference busiMcuKdcTemplateConference, Long masterTerminalId, List<BusiMcuKdcTemplateParticipant> busiMcuKdcTemplateParticipants, List<BusiMcuKdcTemplateDept> templateDepts)
    {
        busiMcuKdcTemplateConference.setCreateTime(new Date());

        Assert.notNull(busiMcuKdcTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        Assert.notNull(busiMcuKdcTemplateConference.getCreateType(), "会议模板创建类型不能为空！");
        Assert.notNull(busiMcuKdcTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
//        if (YesOrNo.convert(busiMcuKdcTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
//        {
//            Assert.notNull(busiMcuKdcTemplateConference.getConferenceNumber(), "会议号不能为空！");
//        }

        // 绑定终端归属部门
        if (busiMcuKdcTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuKdcTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        if (busiMcuKdcTemplateConference.getDeptId() == null)
        {
            throw new SystemException(1004543, "添加会议模板，部门ID不能为空！");
        }

        if (busiMcuKdcTemplateConference.getCreateUserId() == null || busiMcuKdcTemplateConference.getCreateUserName() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiMcuKdcTemplateConference.setCreateUserId(loginUser.getUser().getUserId());
                busiMcuKdcTemplateConference.setCreateUserName(loginUser.getUser().getUserName());
            }
        }

        // 校验会议类型
        //ConferenceType.convert(busiMcuKdcTemplateConference.getType());

        int c = busiMcuKdcTemplateConferenceMapper.insertBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
        if (c > 0)
        {
            if (!ObjectUtils.isEmpty(busiMcuKdcTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant : busiMcuKdcTemplateParticipants)
                {
                    busiMcuKdcTemplateParticipant.setTemplateConferenceId(busiMcuKdcTemplateConference.getId());
                    busiMcuKdcTemplateParticipant.setCreateTime(new Date());
                    busiMcuKdcTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    try
                    {
                        busiMcuKdcTemplateParticipantMapper.insertBusiMcuKdcTemplateParticipant(busiMcuKdcTemplateParticipant);
                    }
                    catch (Exception e)
                    {
                        LoggerFactory.getLogger(getClass()).error("终端ID已不存在：" + busiMcuKdcTemplateParticipant.getTerminalId(), e);
                        throw new RuntimeException("终端ID已不存在：" + busiMcuKdcTemplateParticipant.getTerminalId());
                    }
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuKdcTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiMcuKdcTemplateConference.setMasterParticipantId(busiMcuKdcTemplateParticipant.getId());
                        busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuKdcTemplateDept busiMcuKdcTemplateDept : templateDepts)
                {
                    busiMcuKdcTemplateDept.setTemplateConferenceId(busiMcuKdcTemplateConference.getId());
                    busiMcuKdcTemplateDept.setCreateTime(new Date());
                    busiMcuKdcTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuKdcTemplateDeptMapper.insertBusiMcuKdcTemplateDept(busiMcuKdcTemplateDept);
                }
            }

//            if (busiMcuKdcTemplateConference.getConferenceNumber() == null) {
//                // 生成会议号
//                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiMcuKdcTemplateConference.getDeptId());
//                busiMcuKdcTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
//                busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
//            }

//            if (busiMcuKdcTemplateConference.getConferenceNumber() != null)
//            {
//                // 修改号码状态为已绑定
//                BusiConferenceNumber cn = new BusiConferenceNumber();
//                cn.setId(busiMcuKdcTemplateConference.getConferenceNumber());
//                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
//                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
//            }

            // 添加默认轮询模板
            BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme = new BusiMcuKdcTemplatePollingScheme();
            busiMcuKdcTemplatePollingScheme.setEnableStatus(YesOrNo.YES.getValue());
            busiMcuKdcTemplatePollingScheme.setIsBroadcast(YesOrNo.NO.getValue());
            busiMcuKdcTemplatePollingScheme.setIsDisplaySelf(PanePlacementSelfPaneMode.OFF.getValue());
            busiMcuKdcTemplatePollingScheme.setSchemeName("全局轮询");
            busiMcuKdcTemplatePollingScheme.setLayout(OneSplitScreen.LAYOUT);
            busiMcuKdcTemplatePollingScheme.setIsFill(YesOrNo.YES.getValue());
            busiMcuKdcTemplatePollingScheme.setPollingInterval(10);
            busiMcuKdcTemplatePollingScheme.setPollingStrategy(PollingStrategy.GLOBAL.getValue());
            busiMcuKdcTemplatePollingScheme.setTemplateConferenceId(busiMcuKdcTemplateConference.getId());
            busiMcuKdcTemplatePollingSchemeService.insertBusiMcuKdcTemplatePollingScheme(busiMcuKdcTemplatePollingScheme, null, null);
        }
        return c;
    }

    /**
     * 修改会议模板
     *
     * @param busiMcuKdcTemplateConference 会议模板
     * @param busiMcuKdcTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int updateBusiMcuKdcTemplateConference(BusiMcuKdcTemplateConference busiMcuKdcTemplateConference, Long masterTerminalId, List<BusiMcuKdcTemplateParticipant> busiMcuKdcTemplateParticipants, List<BusiMcuKdcTemplateDept> templateDepts)
    {
        Assert.notNull(busiMcuKdcTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiMcuKdcTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        busiMcuKdcTemplateConference.setUpdateTime(new Date());
//        if (YesOrNo.convert(busiMcuKdcTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
//        {
//            Assert.notNull(busiMcuKdcTemplateConference.getConferenceNumber(), "会议号不能为空！");
//        }

        // 校验会议类型
        //ConferenceType.convert(busiMcuKdcTemplateConference.getType());

        // 先置空主会场配置
        busiMcuKdcTemplateConference.setMasterParticipantId(null);
        busiMcuKdcTemplateConference.setCreateUserId(null);
        busiMcuKdcTemplateConference.setCreateUserName(null);
        int c = busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
        if (c > 0)
        {
            doParticipantUpdate(busiMcuKdcTemplateConference, busiMcuKdcTemplateParticipants, masterTerminalId);
            doDeptUpdate(busiMcuKdcTemplateConference, templateDepts);

//            // 修改号码状态为已绑定
//            if (busiMcuKdcTemplateConference.getConferenceNumber() != null)
//            {
//                BusiConferenceNumber cn = new BusiConferenceNumber();
//                cn.setId(busiMcuKdcTemplateConference.getConferenceNumber());
//                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
//                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
//            }

//            BusiMcuKdcTemplateConference old = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiMcuKdcTemplateConference.getId());
//            if (old.getConferenceNumber() != null && !old.getConferenceNumber().equals(busiMcuKdcTemplateConference.getConferenceNumber()))
//            {
//                BusiMcuKdcTemplateConference con = new BusiMcuKdcTemplateConference();
//                con.setConferenceNumber(old.getConferenceNumber());
//                List<BusiMcuKdcTemplateConference> bcs = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceList(con);
//                if (ObjectUtils.isEmpty(bcs))
//                {
//                    BusiConferenceNumber cn = new BusiConferenceNumber();
//                    cn.setId(old.getConferenceNumber());
//                    cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
//                    busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
//                }
//            }
        }
        return c;
    }

    private void doDeptUpdate(BusiMcuKdcTemplateConference busiMcuKdcTemplateConference, List<BusiMcuKdcTemplateDept> templateDepts)
    {
        BusiMcuKdcTemplateDept deptCon = new BusiMcuKdcTemplateDept();
        deptCon.setTemplateConferenceId(busiMcuKdcTemplateConference.getId());
        List<BusiMcuKdcTemplateDept> ds = busiMcuKdcTemplateDeptMapper.selectBusiMcuKdcTemplateDeptList(deptCon);
        Map<Long, BusiMcuKdcTemplateDept> oldMap = new HashMap<>();
        for (BusiMcuKdcTemplateDept busiMcuKdcTemplateDept : ds)
        {
            oldMap.put(busiMcuKdcTemplateDept.getDeptId(), busiMcuKdcTemplateDept);
        }

        // 添加模板部门顺序信息
        if (!ObjectUtils.isEmpty(templateDepts))
        {
            for (BusiMcuKdcTemplateDept busiMcuKdcTemplateDept : templateDepts)
            {
                busiMcuKdcTemplateDept.setTemplateConferenceId(busiMcuKdcTemplateConference.getId());
                BusiMcuKdcTemplateDept oldTd = oldMap.remove(busiMcuKdcTemplateDept.getDeptId());
                if (oldTd != null)
                {
                    oldTd.setUpdateTime(new Date());
                    oldTd.setWeight(busiMcuKdcTemplateDept.getWeight());
                    busiMcuKdcTemplateDept = oldTd;
                    busiMcuKdcTemplateDeptMapper.updateBusiMcuKdcTemplateDept(busiMcuKdcTemplateDept);
                }
                else
                {
                    busiMcuKdcTemplateDept.setCreateTime(new Date());
                    busiMcuKdcTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuKdcTemplateDeptMapper.insertBusiMcuKdcTemplateDept(busiMcuKdcTemplateDept);
                }
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((deptId, td) -> {
                busiMcuKdcTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuKdcTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(td.getTemplateConferenceId(), td.getDeptId());
                busiMcuKdcTemplateDeptMapper.deleteBusiMcuKdcTemplateDeptById(td.getId());
            });
        }
    }

    private void doParticipantUpdate(BusiMcuKdcTemplateConference busiMcuKdcTemplateConference, List<BusiMcuKdcTemplateParticipant> busiMcuKdcTemplateParticipants, Long masterTerminalId)
    {
        BusiMcuKdcTemplateParticipant query = new BusiMcuKdcTemplateParticipant();
        query.setTemplateConferenceId(busiMcuKdcTemplateConference.getId());
        List<BusiMcuKdcTemplateParticipant> ps = busiMcuKdcTemplateParticipantMapper.selectBusiMcuKdcTemplateParticipantList(query);
        Map<Long, BusiMcuKdcTemplateParticipant> oldMap = new HashMap<>();
        for (BusiMcuKdcTemplateParticipant tp : ps)
        {
            oldMap.put(tp.getTerminalId(), tp);
        }

        if (!ObjectUtils.isEmpty(busiMcuKdcTemplateParticipants))
        {
            for (BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant : busiMcuKdcTemplateParticipants)
            {
                busiMcuKdcTemplateParticipant.setTemplateConferenceId(busiMcuKdcTemplateConference.getId());
                BusiMcuKdcTemplateParticipant oldTp = oldMap.remove(busiMcuKdcTemplateParticipant.getTerminalId());
                if (oldTp != null)
                {
                    oldTp.setWeight(busiMcuKdcTemplateParticipant.getWeight());
                    oldTp.setUpdateTime(new Date());
                    oldTp.setAttendType(busiMcuKdcTemplateParticipant.getAttendType());
                    oldTp.setBusinessProperties(busiMcuKdcTemplateParticipant.getBusinessProperties());
                    busiMcuKdcTemplateParticipantMapper.updateBusiMcuKdcTemplateParticipant(oldTp);
                    busiMcuKdcTemplateParticipant = oldTp;
                }
                else
                {
                    // 新增
                    busiMcuKdcTemplateParticipant.setCreateTime(new Date());
                    busiMcuKdcTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuKdcTemplateParticipantMapper.insertBusiMcuKdcTemplateParticipant(busiMcuKdcTemplateParticipant);
                }

                if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuKdcTemplateParticipant.getTerminalId().longValue())
                {
                    // 设置模板会议中配置的主会场参会终端
                    busiMcuKdcTemplateConference.setMasterParticipantId(busiMcuKdcTemplateParticipant.getId());
                    busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
                }
            }

            if (masterTerminalId == null)
            {
                // 设置模板会议中配置的主会场参会终端
                busiMcuKdcTemplateConference.setMasterParticipantId(null);
                busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((terminalId, tp) -> {
                busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuKdcTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(tp.getId());
                busiMcuKdcTemplateParticipantMapper.deleteBusiMcuKdcTemplateParticipantById(tp.getId());
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
    public int deleteBusiMcuKdcTemplateConferenceById(Long id)
    {
        BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme = new BusiMcuKdcTemplatePollingScheme();
        busiMcuKdcTemplatePollingScheme.setTemplateConferenceId(id);
        List<BusiMcuKdcTemplatePollingScheme> pss = busiMcuKdcTemplatePollingSchemeService.selectBusiMcuKdcTemplatePollingSchemeList(busiMcuKdcTemplatePollingScheme);
        if (!ObjectUtils.isEmpty(pss))
        {
            for (BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme2 : pss)
            {
                busiMcuKdcTemplatePollingSchemeService.deleteBusiMcuKdcTemplatePollingSchemeById(busiMcuKdcTemplatePollingScheme2.getId());
            }
        }

        // 删除轮询方案
        busiMcuKdcTemplatePollingSchemeService.deleteBusiMcuKdcTemplatePollingSchemeById(id);
        BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(id);
        busiMcuKdcTemplateConference.setMasterParticipantId(null);
        busiMcuKdcTemplateConferenceMapper.updateBusiMcuKdcTemplateConference(busiMcuKdcTemplateConference);
        deleteParticipants(id);
        int c = busiMcuKdcTemplateConferenceMapper.deleteBusiMcuKdcTemplateConferenceById(id);
        if (c > 0 && busiMcuKdcTemplateConference.getConferenceNumber() != null)
        {
            BusiMcuKdcTemplateConference con = new BusiMcuKdcTemplateConference();
            con.setConferenceNumber(busiMcuKdcTemplateConference.getConferenceNumber());
            List<BusiMcuKdcTemplateConference> cs = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceList(busiMcuKdcTemplateConference);
//            if (ObjectUtils.isEmpty(cs))
//            {
//                // 修改号码状态为闲置
//                BusiConferenceNumber cn = new BusiConferenceNumber();
//                cn.setId(busiMcuKdcTemplateConference.getConferenceNumber());
//                cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
//                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
//            }

//            // 若是自动创建的会议号，则删除模板的时候同步进行删除
//            BusiConferenceNumber bcn = busiConferenceNumberService.selectBusiConferenceNumberById(busiMcuKdcTemplateConference.getConferenceNumber());
//            if (ConferenceNumberCreateType.convert(bcn.getCreateType()) == ConferenceNumberCreateType.AUTO)
//            {
//                busiConferenceNumberService.deleteBusiConferenceNumberById(busiMcuKdcTemplateConference.getConferenceNumber());
//            }
        }
        return c;
    }

    @Override
    public Page<BusiMcuKdcTemplateConference> selectBusiMcuKdcTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        Page<BusiMcuKdcTemplateConference> tcs = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceListByKey(searchKey,deptId);
        return tcs;
    }

    @Override
    public List<BusiMcuKdcTemplateConference> selectAllBusiMcuKdcTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        return busiMcuKdcTemplateConferenceMapper.selectAllBusiMcuKdcTemplateConferenceListByKey(searchKey,deptId);
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
        busiMcuKdcTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuKdcTemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuKdcTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuKdcTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);

        // 删除分屏信息
        busiMcuKdcTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuKdcTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);
        
        // 先删除与会者模板
        BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant = new BusiMcuKdcTemplateParticipant();
        busiMcuKdcTemplateParticipant.setTemplateConferenceId(id);
        
        List<Long> pIds = new ArrayList<>();
        List<BusiMcuKdcTemplateParticipant> ps = busiMcuKdcTemplateParticipantMapper.selectBusiMcuKdcTemplateParticipantList(busiMcuKdcTemplateParticipant);
        for (BusiMcuKdcTemplateParticipant busiMcuKdcTemplateParticipant2 : ps)
        {
            pIds.add(busiMcuKdcTemplateParticipant2.getId());
        }
        
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuKdcTemplateParticipantMapper.deleteBusiMcuKdcTemplateParticipantByIds(pIds.toArray(new Long[pIds.size()]));
        }
        
        // 删除部门顺序信息
        BusiMcuKdcTemplateDept busiMcuKdcTemplateDept = new BusiMcuKdcTemplateDept();
        busiMcuKdcTemplateDept.setTemplateConferenceId(id);
        pIds = new ArrayList<>();
        List<BusiMcuKdcTemplateDept> ds = busiMcuKdcTemplateDeptMapper.selectBusiMcuKdcTemplateDeptList(busiMcuKdcTemplateDept);
        for (BusiMcuKdcTemplateDept busiMcuKdcTemplateDept2 : ds)
        {
            pIds.add(busiMcuKdcTemplateDept2.getId());
        }
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuKdcTemplateDeptMapper.deleteBusiMcuKdcTemplateDeptByIds(pIds.toArray(new Long[pIds.size()]));
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
