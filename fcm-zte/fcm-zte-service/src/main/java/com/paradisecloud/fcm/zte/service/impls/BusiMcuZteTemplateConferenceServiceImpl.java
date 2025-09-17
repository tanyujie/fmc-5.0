package com.paradisecloud.fcm.zte.service.impls;

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
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.zte.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.zte.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.zte.model.enumer.McuZteLayoutTemplates;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteTemplateConferenceService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteTemplatePollingSchemeService;
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
public class BusiMcuZteTemplateConferenceServiceImpl implements IBusiMcuZteTemplateConferenceService
{
    @Resource
    private BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper;

    @Resource
    private BusiMcuZteTemplateParticipantMapper busiMcuZteTemplateParticipantMapper;

    @Resource
    private BusiMcuZteTemplateDeptMapper busiMcuZteTemplateDeptMapper;

    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;

    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;

    @Resource
    private IBusiMcuZteTemplatePollingSchemeService busiMcuZteTemplatePollingSchemeService;

    @Resource
    private BusiMcuZteTemplateConferenceDefaultViewCellScreenMapper busiMcuZteTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuZteTemplateConferenceDefaultViewDeptMapper busiMcuZteTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuZteTemplateConferenceDefaultViewPaticipantMapper busiMcuZteTemplateConferenceDefaultViewPaticipantMapper;

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
       // Assert.isTrue(jsonObj.containsKey("defaultViewIsDisplaySelf"), "defaultViewIsDisplaySelf不能为空");
     //   Assert.isTrue(jsonObj.containsKey("defaultViewIsFill"), "defaultViewIsFill不能为空");
        Assert.isTrue(jsonObj.containsKey("pollingInterval"), "pollingInterval默认视图的轮询时间间隔不能为空");

        // 模板信息更新
        BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(id);
        tc.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        if (tc.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)) {
            tc.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        } else {
            tc.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        }
        tc.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.SELF.getValue());
        //tc.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setDefaultViewIsFill(2);
        tc.setPollingInterval(jsonObj.getInteger("pollingInterval"));
        int c = busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1003432, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");

        // 根据模板ID删除分频信息
        BusiMcuZteTemplateConferenceDefaultViewCellScreen busiMcuZteTemplateConferenceDefaultViewCellScreenCon = new BusiMcuZteTemplateConferenceDefaultViewCellScreen();
        busiMcuZteTemplateConferenceDefaultViewCellScreenCon.setType(1);
        busiMcuZteTemplateConferenceDefaultViewCellScreenCon.setTemplateConferenceId(id);
        List<BusiMcuZteTemplateConferenceDefaultViewCellScreen> busiMcuZteTemplateConferenceDefaultViewCellScreenList = busiMcuZteTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuZteTemplateConferenceDefaultViewCellScreenList(busiMcuZteTemplateConferenceDefaultViewCellScreenCon);
        if (busiMcuZteTemplateConferenceDefaultViewCellScreenList.size() > 0) {
            for (BusiMcuZteTemplateConferenceDefaultViewCellScreen busiMcuZteTemplateConferenceDefaultViewCellScreen : busiMcuZteTemplateConferenceDefaultViewCellScreenList) {
                busiMcuZteTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuZteTemplateConferenceDefaultViewCellScreenById(busiMcuZteTemplateConferenceDefaultViewCellScreen.getId());
            }
        }
//        busiMcuZteTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuZteTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);
        // 根据模板ID批量删除默认视图的参会者信息
        BusiMcuZteTemplateConferenceDefaultViewPaticipant busiMcuZteTemplateConferenceDefaultViewPaticipantCon = new BusiMcuZteTemplateConferenceDefaultViewPaticipant();
        busiMcuZteTemplateConferenceDefaultViewPaticipantCon.setType(1);
        busiMcuZteTemplateConferenceDefaultViewPaticipantCon.setTemplateConferenceId(id);
        List<BusiMcuZteTemplateConferenceDefaultViewPaticipant> busiMcuZteTemplateConferenceDefaultViewPaticipantList = busiMcuZteTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZteTemplateConferenceDefaultViewPaticipantList(busiMcuZteTemplateConferenceDefaultViewPaticipantCon);
        if (busiMcuZteTemplateConferenceDefaultViewPaticipantList.size() > 0) {
            for (BusiMcuZteTemplateConferenceDefaultViewPaticipant busiMcuZteTemplateConferenceDefaultViewPaticipant : busiMcuZteTemplateConferenceDefaultViewPaticipantList) {
                busiMcuZteTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuZteTemplateConferenceDefaultViewPaticipantById(busiMcuZteTemplateConferenceDefaultViewPaticipant.getId());
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
                        BusiMcuZteTemplateParticipant busiTemplateParticipantCon = new BusiMcuZteTemplateParticipant();
                        busiTemplateParticipantCon.setTemplateConferenceId(id);
                        busiTemplateParticipantCon.setTerminalId(jo.getLong("terminalId"));
                        List<BusiMcuZteTemplateParticipant> busiTemplateParticipants = busiMcuZteTemplateParticipantMapper.selectBusiMcuZteTemplateParticipantList(busiTemplateParticipantCon);
                        if (busiTemplateParticipants.size() > 0) {
                            BusiMcuZteTemplateParticipant busiTemplateParticipantTemp = busiTemplateParticipants.get(0);
                            jo.put("templateParticipantId", busiTemplateParticipantTemp.getId());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuZteTemplateConferenceDefaultViewPaticipant busiMcuZteTemplateConferenceDefaultViewPaticipant = new BusiMcuZteTemplateConferenceDefaultViewPaticipant();
            busiMcuZteTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiMcuZteTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiMcuZteTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiMcuZteTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiMcuZteTemplateConferenceDefaultViewPaticipantMapper.insertBusiMcuZteTemplateConferenceDefaultViewPaticipant(busiMcuZteTemplateConferenceDefaultViewPaticipant);
        }
         ja  = jsonObj.getJSONArray("defaultViewCellScreens");

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
                BusiMcuZteTemplateConferenceDefaultViewCellScreen busiMcuZteTemplateConferenceDefaultViewCellScreen = new BusiMcuZteTemplateConferenceDefaultViewCellScreen();
                busiMcuZteTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiMcuZteTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiMcuZteTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiMcuZteTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.YES.getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiMcuZteTemplateConferenceDefaultViewPaticipant con = new BusiMcuZteTemplateConferenceDefaultViewPaticipant();
                    List<BusiMcuZteTemplateConferenceDefaultViewPaticipant> ps = busiMcuZteTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZteTemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiMcuZteTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }

                busiMcuZteTemplateConferenceDefaultViewCellScreenMapper.insertBusiMcuZteTemplateConferenceDefaultViewCellScreen(busiMcuZteTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        BusiMcuZteTemplateConferenceDefaultViewDept busiMcuZteTemplateConferenceDefaultViewDeptCon = new BusiMcuZteTemplateConferenceDefaultViewDept();
        busiMcuZteTemplateConferenceDefaultViewDeptCon.setType(1);
        busiMcuZteTemplateConferenceDefaultViewDeptCon.setTemplateConferenceId(id);
        List<BusiMcuZteTemplateConferenceDefaultViewDept> busiMcuZteTemplateConferenceDefaultViewDeptList = busiMcuZteTemplateConferenceDefaultViewDeptMapper.selectBusiMcuZteTemplateConferenceDefaultViewDeptList(busiMcuZteTemplateConferenceDefaultViewDeptCon);
        if (busiMcuZteTemplateConferenceDefaultViewDeptList.size() > 0) {
            for (BusiMcuZteTemplateConferenceDefaultViewDept busiMcuZteTemplateConferenceDefaultViewDept : busiMcuZteTemplateConferenceDefaultViewDeptList) {
                busiMcuZteTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuZteTemplateConferenceDefaultViewDeptById(busiMcuZteTemplateConferenceDefaultViewDept.getId());
            }
        }

        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuZteTemplateConferenceDefaultViewDept busiMcuZteTemplateConferenceDefaultViewDept = new BusiMcuZteTemplateConferenceDefaultViewDept();
            busiMcuZteTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiMcuZteTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiMcuZteTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiMcuZteTemplateConferenceDefaultViewDeptMapper.insertBusiMcuZteTemplateConferenceDefaultViewDept(busiMcuZteTemplateConferenceDefaultViewDept);
        }
    }

    @Override
    public String selectBusiMcuZteTemplateConferenceCoverById(Long id)
    {
        BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(id);
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
    public ModelBean selectBusiMcuZteTemplateConferenceById(Long id)
    {
        BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return getTemplateConferenceDetails(tc);
    }

    @Override
    public ModelBean getTemplateConferenceDetails(BusiMcuZteTemplateConference tc)
    {
        BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant = new BusiMcuZteTemplateParticipant();
        busiMcuZteTemplateParticipant.setTemplateConferenceId(tc.getId());
        List<ModelBean> pMbs = new ArrayList<>();
        List<BusiMcuZteTemplateParticipant> ps = busiMcuZteTemplateParticipantMapper.selectBusiMcuZteTemplateParticipantList(busiMcuZteTemplateParticipant);

        int onlineCount = 0;
        int weight = 0;
        String mainName = "";
        Map<Long, ModelBean> bpm = new HashMap<>();
        for (BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant2 : ps)
        {
            ModelBean pmb = new ModelBean(busiMcuZteTemplateParticipant2);
            BusiTerminal bt = TerminalCache.getInstance().get(busiMcuZteTemplateParticipant2.getTerminalId());
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
            if (!ObjectUtils.isEmpty(busiMcuZteTemplateParticipant2.getBusinessProperties()))
            {
                tmb.remove("businessProperties");
            }

            if (!ObjectUtils.isEmpty(busiMcuZteTemplateParticipant2.getAttendType()))
            {
                tmb.remove("attendType");
            }
            pmb.putAll(tmb);
            pMbs.add(pmb);


            ModelBean mb0 = new ModelBean();
            mb0.putAll(pmb);
            mb0.remove("weight");
            bpm.put(busiMcuZteTemplateParticipant2.getId(), mb0);
        }

        if (tc.getMasterParticipantId() != null)
        {
            mainName = bpm.get(tc.getMasterParticipantId()).get("name").toString();
        }

        BusiMcuZteTemplateConferenceDefaultViewCellScreen con = new BusiMcuZteTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(tc.getId());
        con.setType(1);
        List<BusiMcuZteTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuZteTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuZteTemplateConferenceDefaultViewCellScreenList(con);

        BusiMcuZteTemplateConferenceDefaultViewDept con1 = new BusiMcuZteTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(tc.getId());
        con1.setType(1);
        List<BusiMcuZteTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuZteTemplateConferenceDefaultViewDeptMapper.selectBusiMcuZteTemplateConferenceDefaultViewDeptList(con1);

        BusiMcuZteTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuZteTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(tc.getId());
        con2.setType(1);
        List<BusiMcuZteTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuZteTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuZteTemplateConferenceDefaultViewPaticipantList(con2);
        List<ModelBean> defaultViewPaticipantMbs = new ArrayList<>();
        for (BusiMcuZteTemplateConferenceDefaultViewPaticipant busiMcuZteTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants) {
            ModelBean mb = bpm.get(busiMcuZteTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                ModelBean mbNew = new ModelBean();
                mbNew.putAll(mb);
                mbNew.put("weight", busiMcuZteTemplateConferenceDefaultViewPaticipant.getWeight());
                mbNew.put("cellSequenceNumber", busiMcuZteTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbs.add(mbNew);
            }
        }

        BusiMcuZteTemplateDept tdCon = new BusiMcuZteTemplateDept();
        tdCon.setTemplateConferenceId(tc.getId());
        List<BusiMcuZteTemplateDept> tds = busiMcuZteTemplateDeptMapper.selectBusiMcuZteTemplateDeptList(tdCon);

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

        mb.put("onlineCount", onlineCount);
        if (tc.getConferenceNumber() != null && McuZteConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_ZTE)))
        {
            mb.put("meetingJoinedCount", McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_ZTE)).getAttendeeCountingStatistics().getMeetingJoinedCount());
        }
        mb.put("mainVenue", mainName);
        mb.put("isStart", tc.getConferenceNumber() != null && McuZteConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_ZTE)));
        mb.put("mcuType", McuType.MCU_ZTE.getCode());
        return mb;
    }

    /**
     * 查询会议模板列表
     *
     * @param busiMcuZteTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuZteTemplateConference> selectBusiMcuZteTemplateConferenceList(BusiMcuZteTemplateConference busiMcuZteTemplateConference)
    {
        Assert.notNull(busiMcuZteTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectBusiMcuZteTemplateConferenceListWithoutBusinessFieldType(busiMcuZteTemplateConference);
    }

    /**
     * 查询会议模板列表
     *
     * @param busiMcuZteTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuZteTemplateConference> selectAllBusiMcuZteTemplateConferenceList(BusiMcuZteTemplateConference busiMcuZteTemplateConference)
    {
        Assert.notNull(busiMcuZteTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectAllBusiMcuZteTemplateConferenceListWithoutBusinessFieldType(busiMcuZteTemplateConference);
    }

    @Override
    public List<BusiMcuZteTemplateConference> selectBusiMcuZteTemplateConferenceListWithoutBusinessFieldType(BusiMcuZteTemplateConference busiMcuZteTemplateConference)
    {
        // 绑定终端归属部门
        if (busiMcuZteTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuZteTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuZteTemplateConference> tcs = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceList(busiMcuZteTemplateConference);
        return tcs;
    }

    @Override
    public List<BusiMcuZteTemplateConference> selectAllBusiMcuZteTemplateConferenceListWithoutBusinessFieldType(BusiMcuZteTemplateConference busiMcuZteTemplateConference)
    {
        // 绑定终端归属部门
        if (busiMcuZteTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuZteTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuZteTemplateConference> tcs = busiMcuZteTemplateConferenceMapper.selectAllBusiMcuZteTemplateConferenceList(busiMcuZteTemplateConference);
        return tcs;
    }

    /**
     * <pre>模板集合转换成modelBean</pre>
     * @author lilinhai
     * @since 2021-01-30 14:04
     * @param tcs
     * @return List<ModelBean>
     */
    @Override
    public List<ModelBean> toModelBean(List<BusiMcuZteTemplateConference> tcs)
    {
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuZteTemplateConference tc : tcs)
        {
            mbs.add(getTemplateConferenceDetails(tc));
        }

        return mbs;
    }

    @Override
    public List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType)
    {
        BusinessFieldType.convert(businessFieldType);
        return busiMcuZteTemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
    }

    /**
     * 新增会议模板
     *
     * @param busiMcuZteTemplateConference 会议模板
     * @param busiMcuZteTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int insertBusiMcuZteTemplateConference(BusiMcuZteTemplateConference busiMcuZteTemplateConference, Long masterTerminalId, List<BusiMcuZteTemplateParticipant> busiMcuZteTemplateParticipants, List<BusiMcuZteTemplateDept> templateDepts)
    {
        busiMcuZteTemplateConference.setCreateTime(new Date());

        Assert.notNull(busiMcuZteTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        Assert.notNull(busiMcuZteTemplateConference.getCreateType(), "会议模板创建类型不能为空！");
        Assert.notNull(busiMcuZteTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        if (YesOrNo.convert(busiMcuZteTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
        {
            Assert.notNull(busiMcuZteTemplateConference.getConferenceNumber(), "会议号不能为空！");
        }

        // 绑定终端归属部门
        if (busiMcuZteTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiMcuZteTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        if (busiMcuZteTemplateConference.getDeptId() == null)
        {
            throw new SystemException(1004543, "添加会议模板，部门ID不能为空！");
        }

        if (busiMcuZteTemplateConference.getCreateUserId() == null || busiMcuZteTemplateConference.getCreateUserName() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null) {
                busiMcuZteTemplateConference.setCreateUserId(loginUser.getUser().getUserId());
                busiMcuZteTemplateConference.setCreateUserName(loginUser.getUser().getUserName());
            }
        }

        // 校验会议类型
        //ConferenceType.convert(busiMcuZteTemplateConference.getType());

        int c = busiMcuZteTemplateConferenceMapper.insertBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
        if (c > 0)
        {
            if (!ObjectUtils.isEmpty(busiMcuZteTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant : busiMcuZteTemplateParticipants)
                {
                    busiMcuZteTemplateParticipant.setTemplateConferenceId(busiMcuZteTemplateConference.getId());
                    busiMcuZteTemplateParticipant.setCreateTime(new Date());
                    busiMcuZteTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    try
                    {
                        busiMcuZteTemplateParticipantMapper.insertBusiMcuZteTemplateParticipant(busiMcuZteTemplateParticipant);
                    }
                    catch (Exception e)
                    {
                        LoggerFactory.getLogger(getClass()).error("终端ID已不存在：" + busiMcuZteTemplateParticipant.getTerminalId(), e);
                        throw new RuntimeException("终端ID已不存在：" + busiMcuZteTemplateParticipant.getTerminalId());
                    }
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuZteTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiMcuZteTemplateConference.setMasterParticipantId(busiMcuZteTemplateParticipant.getId());
                        busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuZteTemplateDept busiMcuZteTemplateDept : templateDepts)
                {
                    busiMcuZteTemplateDept.setTemplateConferenceId(busiMcuZteTemplateConference.getId());
                    busiMcuZteTemplateDept.setCreateTime(new Date());
                    busiMcuZteTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuZteTemplateDeptMapper.insertBusiMcuZteTemplateDept(busiMcuZteTemplateDept);
                }
            }

            if (busiMcuZteTemplateConference.getConferenceNumber() == null) {
                // 生成会议号
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.autoCreateConferenceNumber(busiMcuZteTemplateConference.getDeptId(), McuType.MCU_ZTE.getCode());
                busiMcuZteTemplateConference.setConferenceNumber(busiConferenceNumber.getId());
                busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
            }

            if (busiMcuZteTemplateConference.getConferenceNumber() != null)
            {
                // 修改号码状态为已绑定
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiMcuZteTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            // 添加默认轮询模板
            BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme = new BusiMcuZteTemplatePollingScheme();
            busiMcuZteTemplatePollingScheme.setEnableStatus(YesOrNo.YES.getValue());
            busiMcuZteTemplatePollingScheme.setIsBroadcast(YesOrNo.NO.getValue());
            busiMcuZteTemplatePollingScheme.setIsDisplaySelf(PanePlacementSelfPaneMode.OFF.getValue());
            busiMcuZteTemplatePollingScheme.setSchemeName("全局轮询");
            busiMcuZteTemplatePollingScheme.setLayout(McuZteLayoutTemplates.SCREEN_1_0.name());
            busiMcuZteTemplatePollingScheme.setIsFill(YesOrNo.YES.getValue());
            busiMcuZteTemplatePollingScheme.setPollingInterval(10);
            busiMcuZteTemplatePollingScheme.setPollingStrategy(PollingStrategy.GLOBAL.getValue());
            busiMcuZteTemplatePollingScheme.setTemplateConferenceId(busiMcuZteTemplateConference.getId());
            busiMcuZteTemplatePollingSchemeService.insertBusiMcuZteTemplatePollingScheme(busiMcuZteTemplatePollingScheme, null, null);
        }
        return c;
    }

    /**
     * 修改会议模板
     *
     * @param busiMcuZteTemplateConference 会议模板
     * @param busiMcuZteTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int updateBusiMcuZteTemplateConference(BusiMcuZteTemplateConference busiMcuZteTemplateConference, Long masterTerminalId, List<BusiMcuZteTemplateParticipant> busiMcuZteTemplateParticipants, List<BusiMcuZteTemplateDept> templateDepts)
    {
        Assert.notNull(busiMcuZteTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
      //  Assert.notNull(busiMcuZteTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        busiMcuZteTemplateConference.setUpdateTime(new Date());
//        if (YesOrNo.convert(busiMcuZteTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
//        {
//            Assert.notNull(busiMcuZteTemplateConference.getConferenceNumber(), "会议号不能为空！");
//        }

        // 校验会议类型
        //ConferenceType.convert(busiMcuZteTemplateConference.getType());

        // 先置空主会场配置
        busiMcuZteTemplateConference.setMasterParticipantId(null);
        busiMcuZteTemplateConference.setCreateUserId(null);
        busiMcuZteTemplateConference.setCreateUserName(null);
        int c = busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
        if (c > 0)
        {
            doParticipantUpdate(busiMcuZteTemplateConference, busiMcuZteTemplateParticipants, masterTerminalId);
            doDeptUpdate(busiMcuZteTemplateConference, templateDepts);

            // 修改号码状态为已绑定
            if (busiMcuZteTemplateConference.getConferenceNumber() != null)
            {
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiMcuZteTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            BusiMcuZteTemplateConference old = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiMcuZteTemplateConference.getId());
            if (old.getConferenceNumber() != null && !old.getConferenceNumber().equals(busiMcuZteTemplateConference.getConferenceNumber()))
            {
                BusiMcuZteTemplateConference con = new BusiMcuZteTemplateConference();
                con.setConferenceNumber(old.getConferenceNumber());
                List<BusiMcuZteTemplateConference> bcs = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceList(con);
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

    private void doDeptUpdate(BusiMcuZteTemplateConference busiMcuZteTemplateConference, List<BusiMcuZteTemplateDept> templateDepts)
    {
        BusiMcuZteTemplateDept deptCon = new BusiMcuZteTemplateDept();
        deptCon.setTemplateConferenceId(busiMcuZteTemplateConference.getId());
        List<BusiMcuZteTemplateDept> ds = busiMcuZteTemplateDeptMapper.selectBusiMcuZteTemplateDeptList(deptCon);
        Map<Long, BusiMcuZteTemplateDept> oldMap = new HashMap<>();
        for (BusiMcuZteTemplateDept busiMcuZteTemplateDept : ds)
        {
            oldMap.put(busiMcuZteTemplateDept.getDeptId(), busiMcuZteTemplateDept);
        }

        // 添加模板部门顺序信息
        if (!ObjectUtils.isEmpty(templateDepts))
        {
            for (BusiMcuZteTemplateDept busiMcuZteTemplateDept : templateDepts)
            {
                busiMcuZteTemplateDept.setTemplateConferenceId(busiMcuZteTemplateConference.getId());
                BusiMcuZteTemplateDept oldTd = oldMap.remove(busiMcuZteTemplateDept.getDeptId());
                if (oldTd != null)
                {
                    oldTd.setUpdateTime(new Date());
                    oldTd.setWeight(busiMcuZteTemplateDept.getWeight());
                    busiMcuZteTemplateDept = oldTd;
                    busiMcuZteTemplateDeptMapper.updateBusiMcuZteTemplateDept(busiMcuZteTemplateDept);
                }
                else
                {
                    busiMcuZteTemplateDept.setCreateTime(new Date());
                    busiMcuZteTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuZteTemplateDeptMapper.insertBusiMcuZteTemplateDept(busiMcuZteTemplateDept);
                }
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((deptId, td) -> {
                busiMcuZteTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuZteTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(td.getTemplateConferenceId(), td.getDeptId());
                busiMcuZteTemplateDeptMapper.deleteBusiMcuZteTemplateDeptById(td.getId());
            });
        }
    }

    private void doParticipantUpdate(BusiMcuZteTemplateConference busiMcuZteTemplateConference, List<BusiMcuZteTemplateParticipant> busiMcuZteTemplateParticipants, Long masterTerminalId)
    {
        BusiMcuZteTemplateParticipant query = new BusiMcuZteTemplateParticipant();
        query.setTemplateConferenceId(busiMcuZteTemplateConference.getId());
        List<BusiMcuZteTemplateParticipant> ps = busiMcuZteTemplateParticipantMapper.selectBusiMcuZteTemplateParticipantList(query);
        Map<Long, BusiMcuZteTemplateParticipant> oldMap = new HashMap<>();
        for (BusiMcuZteTemplateParticipant tp : ps)
        {
            oldMap.put(tp.getTerminalId(), tp);
        }

        if (!ObjectUtils.isEmpty(busiMcuZteTemplateParticipants))
        {
            for (BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant : busiMcuZteTemplateParticipants)
            {
                busiMcuZteTemplateParticipant.setTemplateConferenceId(busiMcuZteTemplateConference.getId());
                BusiMcuZteTemplateParticipant oldTp = oldMap.remove(busiMcuZteTemplateParticipant.getTerminalId());
                if (oldTp != null)
                {
                    oldTp.setWeight(busiMcuZteTemplateParticipant.getWeight());
                    oldTp.setUpdateTime(new Date());
                    oldTp.setAttendType(busiMcuZteTemplateParticipant.getAttendType());
                    oldTp.setBusinessProperties(busiMcuZteTemplateParticipant.getBusinessProperties());
                    busiMcuZteTemplateParticipantMapper.updateBusiMcuZteTemplateParticipant(oldTp);
                    busiMcuZteTemplateParticipant = oldTp;
                }
                else
                {
                    // 新增
                    busiMcuZteTemplateParticipant.setCreateTime(new Date());
                    busiMcuZteTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuZteTemplateParticipantMapper.insertBusiMcuZteTemplateParticipant(busiMcuZteTemplateParticipant);
                }

                if (masterTerminalId != null && masterTerminalId.longValue() == busiMcuZteTemplateParticipant.getTerminalId().longValue())
                {
                    // 设置模板会议中配置的主会场参会终端
                    busiMcuZteTemplateConference.setMasterParticipantId(busiMcuZteTemplateParticipant.getId());
                    busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
                }
            }

            if (masterTerminalId == null)
            {
                // 设置模板会议中配置的主会场参会终端
                busiMcuZteTemplateConference.setMasterParticipantId(null);
                busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((terminalId, tp) -> {
                busiMcuZteTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuZteTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(tp.getId());
                busiMcuZteTemplateParticipantMapper.deleteBusiMcuZteTemplateParticipantById(tp.getId());
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
    public int deleteBusiMcuZteTemplateConferenceById(Long id)
    {
        BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme = new BusiMcuZteTemplatePollingScheme();
        busiMcuZteTemplatePollingScheme.setTemplateConferenceId(id);
        List<BusiMcuZteTemplatePollingScheme> pss = busiMcuZteTemplatePollingSchemeService.selectBusiMcuZteTemplatePollingSchemeList(busiMcuZteTemplatePollingScheme);
        if (!ObjectUtils.isEmpty(pss))
        {
            for (BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme2 : pss)
            {
                busiMcuZteTemplatePollingSchemeService.deleteBusiMcuZteTemplatePollingSchemeById(busiMcuZteTemplatePollingScheme2.getId());
            }
        }

        // 删除轮询方案
        busiMcuZteTemplatePollingSchemeService.deleteBusiMcuZteTemplatePollingSchemeById(id);
        BusiMcuZteTemplateConference busiMcuZteTemplateConference = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(id);
        busiMcuZteTemplateConference.setMasterParticipantId(null);
        busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
        deleteParticipants(id);
        int c = busiMcuZteTemplateConferenceMapper.deleteBusiMcuZteTemplateConferenceById(id);
        if (c > 0 && busiMcuZteTemplateConference.getConferenceNumber() != null)
        {
            BusiMcuZteTemplateConference con = new BusiMcuZteTemplateConference();
            con.setConferenceNumber(busiMcuZteTemplateConference.getConferenceNumber());
            List<BusiMcuZteTemplateConference> cs = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceList(busiMcuZteTemplateConference);
            if (ObjectUtils.isEmpty(cs))
            {
                // 修改号码状态为闲置
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiMcuZteTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            // 若是自动创建的会议号，则删除模板的时候同步进行删除
            BusiConferenceNumber bcn = busiConferenceNumberService.selectBusiConferenceNumberById(busiMcuZteTemplateConference.getConferenceNumber());
            if (ConferenceNumberCreateType.convert(bcn.getCreateType()) == ConferenceNumberCreateType.AUTO)
            {
                busiConferenceNumberService.deleteBusiConferenceNumberById(busiMcuZteTemplateConference.getConferenceNumber());
            }
        }
        return c;
    }

    @Override
    public Page<BusiMcuZteTemplateConference> selectBusiMcuZteTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        Page<BusiMcuZteTemplateConference> tcs = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceListByKey(searchKey,deptId);
        return tcs;
    }

    @Override
    public List<BusiMcuZteTemplateConference> selectAllBusiMcuZteTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        return busiMcuZteTemplateConferenceMapper.selectAllBusiMcuZteTemplateConferenceListByKey(searchKey,deptId);
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
        busiMcuZteTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuZteTemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuZteTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuZteTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);

        // 删除分屏信息
        busiMcuZteTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuZteTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);

        // 先删除与会者模板
        BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant = new BusiMcuZteTemplateParticipant();
        busiMcuZteTemplateParticipant.setTemplateConferenceId(id);

        List<Long> pIds = new ArrayList<>();
        List<BusiMcuZteTemplateParticipant> ps = busiMcuZteTemplateParticipantMapper.selectBusiMcuZteTemplateParticipantList(busiMcuZteTemplateParticipant);
        for (BusiMcuZteTemplateParticipant busiMcuZteTemplateParticipant2 : ps)
        {
            pIds.add(busiMcuZteTemplateParticipant2.getId());
        }

        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuZteTemplateParticipantMapper.deleteBusiMcuZteTemplateParticipantByIds(pIds.toArray(new Long[pIds.size()]));
        }

        // 删除部门顺序信息
        BusiMcuZteTemplateDept busiMcuZteTemplateDept = new BusiMcuZteTemplateDept();
        busiMcuZteTemplateDept.setTemplateConferenceId(id);
        pIds = new ArrayList<>();
        List<BusiMcuZteTemplateDept> ds = busiMcuZteTemplateDeptMapper.selectBusiMcuZteTemplateDeptList(busiMcuZteTemplateDept);
        for (BusiMcuZteTemplateDept busiMcuZteTemplateDept2 : ds)
        {
            pIds.add(busiMcuZteTemplateDept2.getId());
        }
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuZteTemplateDeptMapper.deleteBusiMcuZteTemplateDeptByIds(pIds.toArray(new Long[pIds.size()]));
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
