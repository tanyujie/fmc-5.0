package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AllEqualSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OnePlusNSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.StackedSplitScreen;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeCountingStatistics;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudTemplateConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.StartTemplateConference;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会议模板Service业务层处理
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@Transactional
@Service
public class BusiMcuHwcloudTemplateConferenceServiceImpl implements IBusiMcuHwcloudTemplateConferenceService
{
    @Resource
    private BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper;

    @Resource
    private BusiMcuHwcloudTemplateParticipantMapper busiMcuHwcloudTemplateParticipantMapper;

    @Resource
    private BusiMcuHwcloudTemplateDeptMapper busiMcuHwcloudTemplateDeptMapper;

    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;

    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;


    @Resource
    private BusiMcuHwcloudTemplateConferenceDefaultViewCellScreenMapper busiMcuHwcloudTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuHwcloudTemplateConferenceDefaultViewDeptMapper busiMcuHwcloudTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuHwcloudTemplateConferenceDefaultViewPaticipantMapper busiMcuHwcloudTemplateConferenceDefaultViewPaticipantMapper;



    /**
     * 更新默认视图配置信息
     * @author lilinhai
     * @since 2021-04-08 15:20
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
        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(id);
        tc.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        tc.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        tc.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        tc.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setPollingInterval(jsonObj.getInteger("pollingInterval"));
        int c = busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1002422, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");

        // 根据模板ID删除分频信息
        busiMcuHwcloudTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuHwcloudTemplateConferenceDefaultViewCellScreenById(id);
        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuHwcloudTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);
        // 默认视图的参会者信息保存
        Assert.isTrue(jsonObj.containsKey("defaultViewPaticipants"), "defaultViewpaticipants默认视图的参会者信息不能为空");
        ja = jsonObj.getJSONArray("defaultViewPaticipants");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant = new BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant();
            busiTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiMcuHwcloudTemplateConferenceDefaultViewPaticipantMapper.insertBusiMcuHwcloudTemplateConferenceDefaultViewPaticipant(busiTemplateConferenceDefaultViewPaticipant);
        }
        ja = jsonObj.getJSONArray("defaultViewCellScreens");

        if (tc.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)
                || tc.getDefaultViewLayout().equals(AllEqualSplitScreen.LAYOUT)
                || tc.getDefaultViewLayout().equals(OnePlusNSplitScreen.LAYOUT)
                || tc.getDefaultViewLayout().equals(StackedSplitScreen.LAYOUT))
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
                BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen = new BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen();
                busiTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant con = new BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant();
                    List<BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant> ps = busiMcuHwcloudTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }

                busiMcuHwcloudTemplateConferenceDefaultViewCellScreenMapper.insertBusiMcuHwcloudTemplateConferenceDefaultViewCellScreen(busiTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        busiMcuHwcloudTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuHwcloudTemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuHwcloudTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept = new BusiMcuHwcloudTemplateConferenceDefaultViewDept();
            busiTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiMcuHwcloudTemplateConferenceDefaultViewDeptMapper.insertBusiMcuHwcloudTemplateConferenceDefaultViewDept(busiTemplateConferenceDefaultViewDept);
        }


    }


    public String selectBusiMcuHwcloudTemplateConferenceCoverById(Long id)
    {
        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(id);
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
    public ModelBean selectBusiTemplateConferenceById(Long id) {
        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return getTemplateConferenceDetails(tc);
    }

    @Override
    public ModelBean getTemplateConferenceDetails(BusiMcuHwcloudTemplateConference tc)
    {
        BusiMcuHwcloudTemplateParticipant busiTemplateParticipant = new BusiMcuHwcloudTemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(tc.getId());
        List<ModelBean> pMbs = new ArrayList<>();
        List<BusiMcuHwcloudTemplateParticipant> ps = busiMcuHwcloudTemplateParticipantMapper.selectBusiMcuHwcloudTemplateParticipantList(busiTemplateParticipant);

        int onlineCount = 0;
        int weight = 0;
        String mainName = "";
        Map<Long, ModelBean> bpm = new HashMap<>();
        for (BusiMcuHwcloudTemplateParticipant busiTemplateParticipant2 : ps)
        {
            ModelBean pmb = new ModelBean(busiTemplateParticipant2);
            BusiTerminal bt = TerminalCache.getInstance().get(busiTemplateParticipant2.getTerminalId());
            if (bt == null) {
                continue;
            }
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
            if (!ObjectUtils.isEmpty(busiTemplateParticipant2.getBusinessProperties()))
            {
                tmb.remove("businessProperties");
            }

            if (!ObjectUtils.isEmpty(busiTemplateParticipant2.getAttendType()))
            {
                tmb.remove("attendType");
            }
            pmb.putAll(tmb);
            pMbs.add(pmb);

//            if (bt.getDeptId().longValue() == tc.getDeptId().longValue() && busiTemplateParticipant2.getWeight().intValue() >= weight)
//            {
//                weight = busiTemplateParticipant2.getWeight().intValue();
//                mainName = bt.getName();
//            }

            ModelBean mb0 = new ModelBean();
            mb0.putAll(pmb);
            mb0.remove("weight");
            bpm.put(busiTemplateParticipant2.getId(), mb0);
        }

        if (tc.getMasterParticipantId() != null)
        {
            mainName = bpm.get(tc.getMasterParticipantId()).get("name").toString();
        }

        BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen con = new BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(tc.getId());
        List<BusiMcuHwcloudTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuHwcloudTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuHwcloudTemplateConferenceDefaultViewCellScreenList(con);

        BusiMcuHwcloudTemplateConferenceDefaultViewDept con1 = new BusiMcuHwcloudTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(tc.getId());
        List<BusiMcuHwcloudTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuHwcloudTemplateConferenceDefaultViewDeptMapper.selectBusiMcuHwcloudTemplateConferenceDefaultViewDeptList(con1);

        BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(tc.getId());
        List<BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuHwcloudTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantList(con2);
        List<ModelBean> defaultViewPaticipantMbs = new ArrayList<>();
        for (BusiMcuHwcloudTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants) {
            ModelBean mb = bpm.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                mb.put("weight", busiTemplateConferenceDefaultViewPaticipant.getWeight());
                mb.put("cellSequenceNumber", busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbs.add(mb);
            }
        }

        BusiMcuHwcloudTemplateDept tdCon = new BusiMcuHwcloudTemplateDept();
        tdCon.setTemplateConferenceId(tc.getId());
        List<BusiMcuHwcloudTemplateDept> tds = busiMcuHwcloudTemplateDeptMapper.selectBusiMcuHwcloudTemplateDeptList(tdCon);

        ModelBean tcmb = new ModelBean(tc);
        tcmb.put("password",tc.getConferencePassword());
        tcmb.put("supportRecord",tc.getRecordingEnabled());
        tcmb.put("supportLive",tc.getStreamingEnabled());
        if (tc.getMasterParticipantId() != null)
        {
            tcmb.put("masterTerminalId", bpm.get(tc.getMasterParticipantId()).get("terminalId"));
        }
        ModelBean mb = new ModelBean();
        mb.put("templateConference", tcmb);
        mb.put("templateParticipants", pMbs);
        mb.put("templateDepts", tds);
        mb.put("defaultViewCellScreens", defaultViewCellScreens);
        mb.put("defaultViewDepts", defaultViewDepts);
        mb.put("defaultViewPaticipants", defaultViewPaticipantMbs);
        mb.put("onlineCount", onlineCount);
        if (HwcloudConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_HWCLOUD)))
        {
            HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_HWCLOUD));
            tcmb.put("conferenceNumber",conferenceContext.getConferenceNumber());
            tcmb.put("tenantId",conferenceContext.getTenantId());
            AttendeeCountingStatistics attendeeCountingStatistics = HwcloudConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_HWCLOUD)).getAttendeeCountingStatistics();
            mb.put("meetingJoinedCount", attendeeCountingStatistics.getMeetingJoinedCount());
            mb.put("onlineCount", attendeeCountingStatistics.getOnlineCount());
        }
        mb.put("mainVenue", mainName);
        mb.put("isStart",  HwcloudConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_HWCLOUD)));
        int size=0;
        if(!CollectionUtils.isEmpty(tds)){
            List<SysDept> sysDepts = new ArrayList<>();
            tds.forEach(busiTemplateDept -> {
                SysDept sysDept = SysDeptCache.getInstance().get(busiTemplateDept.getDeptId());
                sysDepts.add(sysDept);
            });
            size = sysDepts.stream().collect(Collectors.groupingBy(SysDept::getAncestors)).size();
        }
        mb.put("templateDeptGroupingSize", size);
        return mb;
    }

    @Override
    public String selectBusiTemplateConferenceCoverById(Long id) {
        return null;
    }

    /**
     * 查询会议模板列表
     *
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuHwcloudTemplateConference> selectBusiTemplateConferenceList(BusiMcuHwcloudTemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    @Override
    public Page<BusiMcuHwcloudTemplateConference> selectBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                deptId=loginUser.getUser().getDeptId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Page<BusiMcuHwcloudTemplateConference> tcs = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceListByKey(searchKey,deptId);
        return tcs;
    }

    @Override
    public List<BusiMcuHwcloudTemplateConference> selectAllBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        return  busiMcuHwcloudTemplateConferenceMapper.selectAllBusiMcuHwcloudTemplateConferenceListByKey(searchKey,deptId);
    }

    /**
     * 查询会议模板列表
     *
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuHwcloudTemplateConference> selectAllBusiTemplateConferenceList(BusiMcuHwcloudTemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectAllBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    @Override
    public List<BusiMcuHwcloudTemplateConference> selectBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuHwcloudTemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuHwcloudTemplateConference> tcs = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceList(busiTemplateConference);
        return tcs;
    }

    @Override
    public List<BusiMcuHwcloudTemplateConference> selectAllBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuHwcloudTemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuHwcloudTemplateConference> tcs = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceList(busiTemplateConference);
        return tcs;
    }

    /**
     * <pre>模板集合转换成modelBean</pre>
     * @author lilinhai
     * @since 2021-01-20 14:04
     * @param tcs
     * @return List<ModelBean>
     */
    @Override
    public List<ModelBean> toModelBean(List<BusiMcuHwcloudTemplateConference> tcs)
    {
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuHwcloudTemplateConference tc : tcs)
        {
            mbs.add(getTemplateConferenceDetails(tc));
        }

        return mbs;
    }

    @Override
    public List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType)
    {
        BusinessFieldType.convert(businessFieldType);
        return busiMcuHwcloudTemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
    }

    /**
     * 新增会议模板
     *
     * @param busiTemplateConference 会议模板
     * @param busiTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int insertBusiTemplateConference(BusiMcuHwcloudTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuHwcloudTemplateParticipant> busiTemplateParticipants, List<BusiMcuHwcloudTemplateDept> templateDepts)
    {
        busiTemplateConference.setCreateTime(new Date());
        Assert.notNull(busiTemplateConference.getName(), "会议模板名称不能为空！");
        Assert.notNull(busiTemplateConference.getCreateType(), "会议模板创建类型不能为空！");
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        if (YesOrNo.convert(busiTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
        {
            Assert.notNull(busiTemplateConference.getConferenceNumber(), "会议号不能为空！");
        }

        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        if (busiTemplateConference.getDeptId() == null)
        {
            throw new SystemException(1004542, "添加会议模板，部门ID不能为空！");
        }

        if (busiTemplateConference.getCreateUserId() == null || busiTemplateConference.getCreateUserName() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setCreateUserId(loginUser.getUser().getUserId());
            busiTemplateConference.setCreateUserName(loginUser.getUser().getUserName());
        }


        int c = 0;
        try {
            c = busiMcuHwcloudTemplateConferenceMapper.insertBusiMcuHwcloudTemplateConference(busiTemplateConference);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.isTrue(false, "添加会议模板失败！请刷新页面后重试！");
        }

        return c;
    }

    private void updatePassword(BusiMcuHwcloudTemplateConference busiTemplateConference) {

    }

    /**
     * 修改会议模板
     *
     * @param busiTemplateConference 会议模板
     * @param busiTemplateParticipants 参会者列表
     * @param templateDepts
     * @return 结果
     */
    @Override
    public int updateBusiTemplateConference(BusiMcuHwcloudTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuHwcloudTemplateParticipant> busiTemplateParticipants, List<BusiMcuHwcloudTemplateDept> templateDepts)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        busiTemplateConference.setUpdateTime(new Date());
        int c = busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiTemplateConference);
        return c;
    }

    private void doDeptUpdate(BusiMcuHwcloudTemplateConference busiTemplateConference, List<BusiMcuHwcloudTemplateDept> templateDepts)
    {
        BusiMcuHwcloudTemplateDept deptCon = new BusiMcuHwcloudTemplateDept();
        deptCon.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiMcuHwcloudTemplateDept> ds = busiMcuHwcloudTemplateDeptMapper.selectBusiMcuHwcloudTemplateDeptList(deptCon);
        Map<Long, BusiMcuHwcloudTemplateDept> oldMap = new HashMap<>();
        for (BusiMcuHwcloudTemplateDept busiTemplateDept : ds)
        {
            oldMap.put(busiTemplateDept.getDeptId(), busiTemplateDept);
        }

        // 添加模板部门顺序信息
        if (!ObjectUtils.isEmpty(templateDepts))
        {
            for (BusiMcuHwcloudTemplateDept busiTemplateDept : templateDepts)
            {
                busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                BusiMcuHwcloudTemplateDept oldTd = oldMap.remove(busiTemplateDept.getDeptId());
                if (oldTd != null)
                {
                    oldTd.setUpdateTime(new Date());
                    oldTd.setWeight(busiTemplateDept.getWeight());
                    busiTemplateDept = oldTd;
                    busiMcuHwcloudTemplateDeptMapper.updateBusiMcuHwcloudTemplateDept(busiTemplateDept);
                }
                else
                {
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuHwcloudTemplateDeptMapper.insertBusiMcuHwcloudTemplateDept(busiTemplateDept);
                }
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((deptId, td) -> {
                busiMcuHwcloudTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuHwcloudTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(td.getTemplateConferenceId(), td.getDeptId());
                busiMcuHwcloudTemplateDeptMapper.deleteBusiMcuHwcloudTemplateDeptById(td.getId());
            });
        }
    }

    private void doParticipantUpdate(BusiMcuHwcloudTemplateConference busiTemplateConference, List<BusiMcuHwcloudTemplateParticipant> busiTemplateParticipants, Long masterTerminalId)
    {
        BusiMcuHwcloudTemplateParticipant query = new BusiMcuHwcloudTemplateParticipant();
        query.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiMcuHwcloudTemplateParticipant> ps = busiMcuHwcloudTemplateParticipantMapper.selectBusiMcuHwcloudTemplateParticipantList(query);
        Map<Long, BusiMcuHwcloudTemplateParticipant> oldMap = new HashMap<>();
        for (BusiMcuHwcloudTemplateParticipant tp : ps)
        {
            oldMap.put(tp.getTerminalId(), tp);
        }

        if (!ObjectUtils.isEmpty(busiTemplateParticipants))
        {
            for (BusiMcuHwcloudTemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
            {
                busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                BusiMcuHwcloudTemplateParticipant oldTp = oldMap.remove(busiTemplateParticipant.getTerminalId());
                if (oldTp != null)
                {
                    oldTp.setWeight(busiTemplateParticipant.getWeight());
                    oldTp.setUpdateTime(new Date());
                    oldTp.setAttendType(busiTemplateParticipant.getAttendType());
                    oldTp.setBusinessProperties(busiTemplateParticipant.getBusinessProperties());
                    busiMcuHwcloudTemplateParticipantMapper.updateBusiMcuHwcloudTemplateParticipant(oldTp);
                    busiTemplateParticipant = oldTp;
                }
                else
                {
                    // 新增
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuHwcloudTemplateParticipantMapper.insertBusiMcuHwcloudTemplateParticipant(busiTemplateParticipant);
                }

                if (masterTerminalId != null && masterTerminalId.longValue() == busiTemplateParticipant.getTerminalId().longValue())
                {
                    // 设置模板会议中配置的主会场参会终端
                    busiTemplateConference.setMasterParticipantId(busiTemplateParticipant.getId());
                    busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiTemplateConference);
                }
            }

            if (masterTerminalId == null)
            {
                // 设置模板会议中配置的主会场参会终端
                busiTemplateConference.setMasterParticipantId(null);
                busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiTemplateConference);
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((terminalId, tp) -> {
                busiMcuHwcloudTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantById(tp.getId());
                busiMcuHwcloudTemplateParticipantMapper.deleteBusiMcuHwcloudTemplateParticipantById(tp.getId());
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
    public int deleteBusiTemplateConferenceById(Long id)
    {

        BusiMcuHwcloudTemplateConference busiTemplateConference = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(id);
        busiTemplateConference.setMasterParticipantId(null);
        busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiTemplateConference);
        deleteParticipants(id);
        int c = busiMcuHwcloudTemplateConferenceMapper.deleteBusiMcuHwcloudTemplateConferenceById(id);
        return c;
    }

    /**
     * 删除会议模板信息
     *
     * @param id 会议模板ID
     * @return 结果
     */
    @Override
    public int deleteMobileBusiTemplateConferenceById(Long id) {

        BusiMcuHwcloudTemplateConference busiTemplateConference = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(id);
        busiTemplateConference.setMasterParticipantId(null);
        busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiTemplateConference);
        deleteParticipants(id);
        int c = busiMcuHwcloudTemplateConferenceMapper.deleteBusiMcuHwcloudTemplateConferenceById(id);
        if (c > 0 && busiTemplateConference.getConferenceNumber() != null) {
            BusiMcuHwcloudTemplateConference con = new BusiMcuHwcloudTemplateConference();
            con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
            List<BusiMcuHwcloudTemplateConference> cs = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceList(busiTemplateConference);
            if (ObjectUtils.isEmpty(cs)) {
                // 修改号码状态为闲置
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }
            busiConferenceNumberService.deleteBusiConferenceNumberById(busiTemplateConference.getConferenceNumber());

        }
        return c;

    }


    @Override
    public int updateBusiTemplateConferenceName(Long id, String name) {

        BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(id);
        if (tc == null || Objects.equals(tc.getName(), name)) {
            return 0;
        }
        tc.setName(name);
        return   busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);
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
        busiMcuHwcloudTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuHwcloudTemplateConferenceDefaultViewDeptById(id);

        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuHwcloudTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuHwcloudTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);

        // 删除分屏信息
        busiMcuHwcloudTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuHwcloudTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);

        // 先删除与会者模板
        BusiMcuHwcloudTemplateParticipant busiTemplateParticipant = new BusiMcuHwcloudTemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(id);

        List<Long> pIds = new ArrayList<>();
        List<BusiMcuHwcloudTemplateParticipant> ps = busiMcuHwcloudTemplateParticipantMapper.selectBusiMcuHwcloudTemplateParticipantList(busiTemplateParticipant);
        for (BusiMcuHwcloudTemplateParticipant busiTemplateParticipant2 : ps)
        {
            pIds.add(busiTemplateParticipant2.getId());
        }

        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuHwcloudTemplateParticipantMapper.deleteBusiMcuHwcloudTemplateParticipantByIds(pIds.toArray(new Long[pIds.size()]));
        }

        // 删除部门顺序信息
        BusiMcuHwcloudTemplateDept busiTemplateDept = new BusiMcuHwcloudTemplateDept();
        busiTemplateDept.setTemplateConferenceId(id);
        pIds = new ArrayList<>();
        List<BusiMcuHwcloudTemplateDept> ds = busiMcuHwcloudTemplateDeptMapper.selectBusiMcuHwcloudTemplateDeptList(busiTemplateDept);
        for (BusiMcuHwcloudTemplateDept busiTemplateDept2 : ds)
        {
            pIds.add(busiTemplateDept2.getId());
        }
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuHwcloudTemplateDeptMapper.deleteBusiMcuHwcloudTemplateDeptByIds(pIds.toArray(new Long[pIds.size()]));
        }
    }
}
