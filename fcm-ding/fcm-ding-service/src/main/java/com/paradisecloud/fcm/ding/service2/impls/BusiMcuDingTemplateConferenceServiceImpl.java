package com.paradisecloud.fcm.ding.service2.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.fcm.ding.busi.AttendeeCountingStatistics;
import com.paradisecloud.fcm.ding.cache.DingConferenceContextCache;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingTemplateConferenceService;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AllEqualSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OnePlusNSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.StackedSplitScreen;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;

import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.slf4j.LoggerFactory;
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
public class BusiMcuDingTemplateConferenceServiceImpl implements IBusiMcuDingTemplateConferenceService
{
    @Resource
    private BusiMcuDingTemplateConferenceMapper busiMcuDingTemplateConferenceMapper;

    @Resource
    private BusiMcuDingTemplateParticipantMapper busiMcuDingTemplateParticipantMapper;

    @Resource
    private BusiMcuDingTemplateDeptMapper busiMcuDingTemplateDeptMapper;

    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;

    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;


    @Resource
    private BusiMcuDingTemplateConferenceDefaultViewCellScreenMapper busiMcuDingTemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuDingTemplateConferenceDefaultViewDeptMapper busiMcuDingTemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuDingTemplateConferenceDefaultViewPaticipantMapper busiMcuDingTemplateConferenceDefaultViewPaticipantMapper;



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
        BusiMcuDingTemplateConference tc = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(id);
        tc.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        tc.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        tc.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        tc.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setPollingInterval(jsonObj.getInteger("pollingInterval"));
        int c = busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1002422, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");

        // 根据模板ID删除分频信息
        busiMcuDingTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuDingTemplateConferenceDefaultViewCellScreenById(id);
        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuDingTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuDingTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);
        // 默认视图的参会者信息保存
        Assert.isTrue(jsonObj.containsKey("defaultViewPaticipants"), "defaultViewpaticipants默认视图的参会者信息不能为空");
        ja = jsonObj.getJSONArray("defaultViewPaticipants");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuDingTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant = new BusiMcuDingTemplateConferenceDefaultViewPaticipant();
            busiTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiMcuDingTemplateConferenceDefaultViewPaticipantMapper.insertBusiMcuDingTemplateConferenceDefaultViewPaticipant(busiTemplateConferenceDefaultViewPaticipant);
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
                BusiMcuDingTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen = new BusiMcuDingTemplateConferenceDefaultViewCellScreen();
                busiTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiMcuDingTemplateConferenceDefaultViewPaticipant con = new BusiMcuDingTemplateConferenceDefaultViewPaticipant();
                    List<BusiMcuDingTemplateConferenceDefaultViewPaticipant> ps = busiMcuDingTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuDingTemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }

                busiMcuDingTemplateConferenceDefaultViewCellScreenMapper.insertBusiMcuDingTemplateConferenceDefaultViewCellScreen(busiTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        busiMcuDingTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuDingTemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuDingTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept = new BusiMcuDingTemplateConferenceDefaultViewDept();
            busiTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiMcuDingTemplateConferenceDefaultViewDeptMapper.insertBusiMcuDingTemplateConferenceDefaultViewDept(busiTemplateConferenceDefaultViewDept);
        }


    }


    public String selectBusiMcuDingTemplateConferenceCoverById(Long id)
    {
        BusiMcuDingTemplateConference tc = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(id);
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
        BusiMcuDingTemplateConference tc = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return getTemplateConferenceDetails(tc);
    }

    @Override
    public ModelBean getTemplateConferenceDetails(BusiMcuDingTemplateConference tc)
    {
        BusiMcuDingTemplateParticipant busiTemplateParticipant = new BusiMcuDingTemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(tc.getId());
        List<ModelBean> pMbs = new ArrayList<>();
        List<BusiMcuDingTemplateParticipant> ps = busiMcuDingTemplateParticipantMapper.selectBusiMcuDingTemplateParticipantList(busiTemplateParticipant);

        int onlineCount = 0;
        int weight = 0;
        String mainName = "";
        Map<Long, ModelBean> bpm = new HashMap<>();
        for (BusiMcuDingTemplateParticipant busiTemplateParticipant2 : ps)
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

        BusiMcuDingTemplateConferenceDefaultViewCellScreen con = new BusiMcuDingTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(tc.getId());
        List<BusiMcuDingTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuDingTemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuDingTemplateConferenceDefaultViewCellScreenList(con);

        BusiMcuDingTemplateConferenceDefaultViewDept con1 = new BusiMcuDingTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(tc.getId());
        List<BusiMcuDingTemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuDingTemplateConferenceDefaultViewDeptMapper.selectBusiMcuDingTemplateConferenceDefaultViewDeptList(con1);

        BusiMcuDingTemplateConferenceDefaultViewPaticipant con2 = new BusiMcuDingTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(tc.getId());
        List<BusiMcuDingTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuDingTemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuDingTemplateConferenceDefaultViewPaticipantList(con2);
        List<ModelBean> defaultViewPaticipantMbs = new ArrayList<>();
        for (BusiMcuDingTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants) {
            ModelBean mb = bpm.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                mb.put("weight", busiTemplateConferenceDefaultViewPaticipant.getWeight());
                mb.put("cellSequenceNumber", busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbs.add(mb);
            }
        }

        BusiMcuDingTemplateDept tdCon = new BusiMcuDingTemplateDept();
        tdCon.setTemplateConferenceId(tc.getId());
        List<BusiMcuDingTemplateDept> tds = busiMcuDingTemplateDeptMapper.selectBusiMcuDingTemplateDeptList(tdCon);

        ModelBean tcmb = new ModelBean(tc);
        tcmb.put("password",tc.getConferencePassword());
        tcmb.put("supportRecord",tc.getRecordingEnabled());
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
        if (tc.getConferenceNumber() != null && DingConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_DING)))
        {
            AttendeeCountingStatistics attendeeCountingStatistics = DingConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_DING)).getAttendeeCountingStatistics();
            mb.put("meetingJoinedCount", attendeeCountingStatistics.getMeetingJoinedCount());
            mb.put("onlineCount", attendeeCountingStatistics.getOnlineCount());
        }
        mb.put("mainVenue", mainName);
        mb.put("isStart",  DingConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.MCU_DING)));
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
    public List<BusiMcuDingTemplateConference> selectBusiTemplateConferenceList(BusiMcuDingTemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    @Override
    public Page<BusiMcuDingTemplateConference> selectBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                deptId=loginUser.getUser().getDeptId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Page<BusiMcuDingTemplateConference> tcs = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceListByKey(searchKey,deptId);
        return tcs;
    }

    @Override
    public List<BusiMcuDingTemplateConference> selectAllBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        return  busiMcuDingTemplateConferenceMapper.selectAllBusiMcuDingTemplateConferenceListByKey(searchKey,deptId);
    }

    /**
     * 查询会议模板列表
     *
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuDingTemplateConference> selectAllBusiTemplateConferenceList(BusiMcuDingTemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectAllBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    @Override
    public List<BusiMcuDingTemplateConference> selectBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuDingTemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuDingTemplateConference> tcs = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceList(busiTemplateConference);
        return tcs;
    }

    @Override
    public List<BusiMcuDingTemplateConference> selectAllBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuDingTemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuDingTemplateConference> tcs = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceList(busiTemplateConference);
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
    public List<ModelBean> toModelBean(List<BusiMcuDingTemplateConference> tcs)
    {
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuDingTemplateConference tc : tcs)
        {
            mbs.add(getTemplateConferenceDetails(tc));
        }

        return mbs;
    }

    @Override
    public List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType)
    {
        BusinessFieldType.convert(businessFieldType);
        return busiMcuDingTemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
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
    public int insertBusiTemplateConference(BusiMcuDingTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuDingTemplateParticipant> busiTemplateParticipants, List<BusiMcuDingTemplateDept> templateDepts)
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
            c = busiMcuDingTemplateConferenceMapper.insertBusiMcuDingTemplateConference(busiTemplateConference);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.isTrue(false, "添加会议模板失败！请刷新页面后重试！");
        }
        if (c > 0)
        {
            if (!ObjectUtils.isEmpty(busiTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuDingTemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
                {
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    try
                    {
                        busiMcuDingTemplateParticipantMapper.insertBusiMcuDingTemplateParticipant(busiTemplateParticipant);
                    }
                    catch (Exception e)
                    {
                        LoggerFactory.getLogger(getClass()).error("终端ID已不存在：" + busiTemplateParticipant.getTerminalId(), e);
                        throw new RuntimeException("终端ID已不存在：" + busiTemplateParticipant.getTerminalId());
                    }
                    if (masterTerminalId != null && masterTerminalId.longValue() == busiTemplateParticipant.getTerminalId().longValue())
                    {
                        // 设置模板会议中配置的主会场参会终端
                        busiTemplateConference.setMasterParticipantId(busiTemplateParticipant.getId());
                        busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuDingTemplateDept busiTemplateDept : templateDepts)
                {
                    busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuDingTemplateDeptMapper.insertBusiMcuDingTemplateDept(busiTemplateDept);
                }
            }

            if (busiTemplateConference.getConferenceNumber() != null)
            {
                // 修改号码状态为已绑定
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
                updatePassword(busiTemplateConference);

            }


        }
        return c;
    }

    private void updatePassword(BusiMcuDingTemplateConference busiTemplateConference) {

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
    public int updateBusiTemplateConference(BusiMcuDingTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuDingTemplateParticipant> busiTemplateParticipants, List<BusiMcuDingTemplateDept> templateDepts)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        busiTemplateConference.setUpdateTime(new Date());
        if (YesOrNo.convert(busiTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
        {
            Assert.notNull(busiTemplateConference.getConferenceNumber(), "会议号不能为空！");
        }
        int c = busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConference);
        return c;
    }

    private void doDeptUpdate(BusiMcuDingTemplateConference busiTemplateConference, List<BusiMcuDingTemplateDept> templateDepts)
    {
        BusiMcuDingTemplateDept deptCon = new BusiMcuDingTemplateDept();
        deptCon.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiMcuDingTemplateDept> ds = busiMcuDingTemplateDeptMapper.selectBusiMcuDingTemplateDeptList(deptCon);
        Map<Long, BusiMcuDingTemplateDept> oldMap = new HashMap<>();
        for (BusiMcuDingTemplateDept busiTemplateDept : ds)
        {
            oldMap.put(busiTemplateDept.getDeptId(), busiTemplateDept);
        }

        // 添加模板部门顺序信息
        if (!ObjectUtils.isEmpty(templateDepts))
        {
            for (BusiMcuDingTemplateDept busiTemplateDept : templateDepts)
            {
                busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                BusiMcuDingTemplateDept oldTd = oldMap.remove(busiTemplateDept.getDeptId());
                if (oldTd != null)
                {
                    oldTd.setUpdateTime(new Date());
                    oldTd.setWeight(busiTemplateDept.getWeight());
                    busiTemplateDept = oldTd;
                    busiMcuDingTemplateDeptMapper.updateBusiMcuDingTemplateDept(busiTemplateDept);
                }
                else
                {
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuDingTemplateDeptMapper.insertBusiMcuDingTemplateDept(busiTemplateDept);
                }
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((deptId, td) -> {
                busiMcuDingTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuDingTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(td.getTemplateConferenceId(), td.getDeptId());
                busiMcuDingTemplateDeptMapper.deleteBusiMcuDingTemplateDeptById(td.getId());
            });
        }
    }

    private void doParticipantUpdate(BusiMcuDingTemplateConference busiTemplateConference, List<BusiMcuDingTemplateParticipant> busiTemplateParticipants, Long masterTerminalId)
    {
        BusiMcuDingTemplateParticipant query = new BusiMcuDingTemplateParticipant();
        query.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiMcuDingTemplateParticipant> ps = busiMcuDingTemplateParticipantMapper.selectBusiMcuDingTemplateParticipantList(query);
        Map<Long, BusiMcuDingTemplateParticipant> oldMap = new HashMap<>();
        for (BusiMcuDingTemplateParticipant tp : ps)
        {
            oldMap.put(tp.getTerminalId(), tp);
        }

        if (!ObjectUtils.isEmpty(busiTemplateParticipants))
        {
            for (BusiMcuDingTemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
            {
                busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                BusiMcuDingTemplateParticipant oldTp = oldMap.remove(busiTemplateParticipant.getTerminalId());
                if (oldTp != null)
                {
                    oldTp.setWeight(busiTemplateParticipant.getWeight());
                    oldTp.setUpdateTime(new Date());
                    oldTp.setAttendType(busiTemplateParticipant.getAttendType());
                    oldTp.setBusinessProperties(busiTemplateParticipant.getBusinessProperties());
                    busiMcuDingTemplateParticipantMapper.updateBusiMcuDingTemplateParticipant(oldTp);
                    busiTemplateParticipant = oldTp;
                }
                else
                {
                    // 新增
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuDingTemplateParticipantMapper.insertBusiMcuDingTemplateParticipant(busiTemplateParticipant);
                }

                if (masterTerminalId != null && masterTerminalId.longValue() == busiTemplateParticipant.getTerminalId().longValue())
                {
                    // 设置模板会议中配置的主会场参会终端
                    busiTemplateConference.setMasterParticipantId(busiTemplateParticipant.getId());
                    busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConference);
                }
            }

            if (masterTerminalId == null)
            {
                // 设置模板会议中配置的主会场参会终端
                busiTemplateConference.setMasterParticipantId(null);
                busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConference);
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((terminalId, tp) -> {
                busiMcuDingTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuDingTemplateConferenceDefaultViewPaticipantById(tp.getId());
                busiMcuDingTemplateParticipantMapper.deleteBusiMcuDingTemplateParticipantById(tp.getId());
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

        BusiMcuDingTemplateConference busiTemplateConference = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(id);
        busiTemplateConference.setMasterParticipantId(null);
        busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConference);
        deleteParticipants(id);
        int c = busiMcuDingTemplateConferenceMapper.deleteBusiMcuDingTemplateConferenceById(id);
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

        BusiMcuDingTemplateConference busiTemplateConference = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(id);
        busiTemplateConference.setMasterParticipantId(null);
        busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiTemplateConference);
        deleteParticipants(id);
        int c = busiMcuDingTemplateConferenceMapper.deleteBusiMcuDingTemplateConferenceById(id);
        if (c > 0 && busiTemplateConference.getConferenceNumber() != null) {
            BusiMcuDingTemplateConference con = new BusiMcuDingTemplateConference();
            con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
            List<BusiMcuDingTemplateConference> cs = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceList(busiTemplateConference);
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

        BusiMcuDingTemplateConference tc = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(id);
        if (tc == null || Objects.equals(tc.getName(), name)) {
            return 0;
        }
        tc.setName(name);
        return   busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(tc);
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
        busiMcuDingTemplateConferenceDefaultViewDeptMapper.deleteBusiMcuDingTemplateConferenceDefaultViewDeptById(id);

        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuDingTemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuDingTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);

        // 删除分屏信息
        busiMcuDingTemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuDingTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);

        // 先删除与会者模板
        BusiMcuDingTemplateParticipant busiTemplateParticipant = new BusiMcuDingTemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(id);

        List<Long> pIds = new ArrayList<>();
        List<BusiMcuDingTemplateParticipant> ps = busiMcuDingTemplateParticipantMapper.selectBusiMcuDingTemplateParticipantList(busiTemplateParticipant);
        for (BusiMcuDingTemplateParticipant busiTemplateParticipant2 : ps)
        {
            pIds.add(busiTemplateParticipant2.getId());
        }

        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuDingTemplateParticipantMapper.deleteBusiMcuDingTemplateParticipantByIds(pIds.toArray(new Long[pIds.size()]));
        }

        // 删除部门顺序信息
        BusiMcuDingTemplateDept busiTemplateDept = new BusiMcuDingTemplateDept();
        busiTemplateDept.setTemplateConferenceId(id);
        pIds = new ArrayList<>();
        List<BusiMcuDingTemplateDept> ds = busiMcuDingTemplateDeptMapper.selectBusiMcuDingTemplateDeptList(busiTemplateDept);
        for (BusiMcuDingTemplateDept busiTemplateDept2 : ds)
        {
            pIds.add(busiTemplateDept2.getId());
        }
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuDingTemplateDeptMapper.deleteBusiMcuDingTemplateDeptByIds(pIds.toArray(new Long[pIds.size()]));
        }
    }
}
