package com.paradisecloud.fcm.smc2.setvice2.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.com.fcm.smc.modle.ConstAPI;
import com.paradisecloud.common.exception.CustomException;
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
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeCountingStatistics;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2TemplateConferenceService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.apache.logging.log4j.util.Strings;
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
public class BusiMcuSmc2TemplateConferenceServiceImpl implements IBusiMcuSmc2TemplateConferenceService
{
    @Resource
    private BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper;

    @Resource
    private BusiMcuSmc2TemplateParticipantMapper busiMcuSmc2TemplateParticipantMapper;

    @Resource
    private BusiMcuSmc2TemplateDeptMapper busiMcuSmc2TemplateDeptMapper;

    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;

    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;


    @Resource
    private BusiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper busiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuSmc2TemplateConferenceDefaultViewDeptMapper busiMcuSmc2TemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper busiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper;



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
        BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(id);
        tc.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        tc.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        tc.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        tc.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setPollingInterval(jsonObj.getInteger("pollingInterval"));
        int c = busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1002422, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");

        // 根据模板ID删除分频信息
        busiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuSmc2TemplateConferenceDefaultViewCellScreenById(id);
        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuSmc2TemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);
        // 默认视图的参会者信息保存
        Assert.isTrue(jsonObj.containsKey("defaultViewPaticipants"), "defaultViewpaticipants默认视图的参会者信息不能为空");
        ja = jsonObj.getJSONArray("defaultViewPaticipants");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuSmc2TemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant = new BusiMcuSmc2TemplateConferenceDefaultViewPaticipant();
            busiTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper.insertBusiMcuSmc2TemplateConferenceDefaultViewPaticipant(busiTemplateConferenceDefaultViewPaticipant);
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
                BusiMcuSmc2TemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen = new BusiMcuSmc2TemplateConferenceDefaultViewCellScreen();
                busiTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiMcuSmc2TemplateConferenceDefaultViewPaticipant con = new BusiMcuSmc2TemplateConferenceDefaultViewPaticipant();
                    List<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant> ps = busiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuSmc2TemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }

                busiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper.insertBusiMcuSmc2TemplateConferenceDefaultViewCellScreen(busiTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        busiMcuSmc2TemplateConferenceDefaultViewDeptMapper.deleteBusiMcuSmc2TemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuSmc2TemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept = new BusiMcuSmc2TemplateConferenceDefaultViewDept();
            busiTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiMcuSmc2TemplateConferenceDefaultViewDeptMapper.insertBusiMcuSmc2TemplateConferenceDefaultViewDept(busiTemplateConferenceDefaultViewDept);
        }


    }


    public String selectBusiMcuSmc2TemplateConferenceCoverById(Long id)
    {
        BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(id);
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
        BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return getTemplateConferenceDetails(tc);
    }

    @Override
    public ModelBean getTemplateConferenceDetails(BusiMcuSmc2TemplateConference tc)
    {
        BusiMcuSmc2TemplateParticipant busiTemplateParticipant = new BusiMcuSmc2TemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(tc.getId());
        List<ModelBean> pMbs = new ArrayList<>();
        List<BusiMcuSmc2TemplateParticipant> ps = busiMcuSmc2TemplateParticipantMapper.selectBusiMcuSmc2TemplateParticipantList(busiTemplateParticipant);

        int onlineCount = 0;
        int weight = 0;
        String mainName = "";
        Map<Long, ModelBean> bpm = new HashMap<>();
        for (BusiMcuSmc2TemplateParticipant busiTemplateParticipant2 : ps)
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

        BusiMcuSmc2TemplateConferenceDefaultViewCellScreen con = new BusiMcuSmc2TemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc2TemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuSmc2TemplateConferenceDefaultViewCellScreenList(con);

        BusiMcuSmc2TemplateConferenceDefaultViewDept con1 = new BusiMcuSmc2TemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc2TemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuSmc2TemplateConferenceDefaultViewDeptMapper.selectBusiMcuSmc2TemplateConferenceDefaultViewDeptList(con1);

        BusiMcuSmc2TemplateConferenceDefaultViewPaticipant con2 = new BusiMcuSmc2TemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc2TemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuSmc2TemplateConferenceDefaultViewPaticipantList(con2);
        List<ModelBean> defaultViewPaticipantMbs = new ArrayList<>();
        for (BusiMcuSmc2TemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants) {
            ModelBean mb = bpm.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                mb.put("weight", busiTemplateConferenceDefaultViewPaticipant.getWeight());
                mb.put("cellSequenceNumber", busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbs.add(mb);
            }
        }

        BusiMcuSmc2TemplateDept tdCon = new BusiMcuSmc2TemplateDept();
        tdCon.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc2TemplateDept> tds = busiMcuSmc2TemplateDeptMapper.selectBusiMcuSmc2TemplateDeptList(tdCon);

        ModelBean tcmb = new ModelBean(tc);
        tcmb.put("password",tcmb.get("guestPassword"));
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
        if (tc.getConferenceNumber() != null && Smc2ConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.SMC2)))
        {
            Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.SMC2));
            AttendeeCountingStatistics attendeeCountingStatistics = smc2ConferenceContext.getAttendeeCountingStatistics();
            mb.put("meetingJoinedCount", attendeeCountingStatistics.getMeetingJoinedCount());
            mb.put("onlineCount", attendeeCountingStatistics.getOnlineCount());
        }
        mb.put("mainVenue", mainName);
        mb.put("isStart",  Smc2ConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.SMC2)));
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
        mb.put("mcuType", McuType.SMC2.getCode());
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
    public List<BusiMcuSmc2TemplateConference> selectBusiTemplateConferenceList(BusiMcuSmc2TemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    @Override
    public Page<BusiMcuSmc2TemplateConference> selectBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                deptId=loginUser.getUser().getDeptId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Page<BusiMcuSmc2TemplateConference> tcs = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceListByKey(searchKey,deptId);
        return tcs;
    }

    @Override
    public List<BusiMcuSmc2TemplateConference> selectAllBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        return  busiMcuSmc2TemplateConferenceMapper.selectAllBusiMcuSmc2TemplateConferenceListByKey(searchKey,deptId);
    }

    /**
     * 查询会议模板列表
     *
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuSmc2TemplateConference> selectAllBusiTemplateConferenceList(BusiMcuSmc2TemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectAllBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    @Override
    public List<BusiMcuSmc2TemplateConference> selectBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuSmc2TemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuSmc2TemplateConference> tcs = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceList(busiTemplateConference);
        return tcs;
    }

    @Override
    public List<BusiMcuSmc2TemplateConference> selectAllBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuSmc2TemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = null;
            try {
                loginUser = SecurityUtils.getLoginUser();
            } catch (Exception e) {

            }
            if(loginUser!=null) {
                busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
            }
        }

        List<BusiMcuSmc2TemplateConference> tcs = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceList(busiTemplateConference);
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
    public List<ModelBean> toModelBean(List<BusiMcuSmc2TemplateConference> tcs)
    {
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuSmc2TemplateConference tc : tcs)
        {
            mbs.add(getTemplateConferenceDetails(tc));
        }

        return mbs;
    }

    @Override
    public List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType)
    {
        BusinessFieldType.convert(businessFieldType);
        return busiMcuSmc2TemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
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
    public int insertBusiTemplateConference(BusiMcuSmc2TemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuSmc2TemplateParticipant> busiTemplateParticipants, List<BusiMcuSmc2TemplateDept> templateDepts)
    {
        busiTemplateConference.setCreateTime(new Date());

        Assert.notNull(busiTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
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

        // 校验会议类型
        //ConferenceType.convert(busiTemplateConference.getType());

        // 新直播方式：事先配置的直播地址时模板会议自动开启直播


        int c = 0;
        try {
            c = busiMcuSmc2TemplateConferenceMapper.insertBusiMcuSmc2TemplateConference(busiTemplateConference);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.isTrue(false, "添加会议模板失败！请刷新页面后重试！");
        }
        if (c > 0)
        {
            if (!ObjectUtils.isEmpty(busiTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuSmc2TemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
                {
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    try
                    {
                        busiMcuSmc2TemplateParticipantMapper.insertBusiMcuSmc2TemplateParticipant(busiTemplateParticipant);
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
                        busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuSmc2TemplateDept busiTemplateDept : templateDepts)
                {
                    busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuSmc2TemplateDeptMapper.insertBusiMcuSmc2TemplateDept(busiTemplateDept);
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

    private void updatePassword(BusiMcuSmc2TemplateConference busiTemplateConference) {

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
    public int updateBusiTemplateConference(BusiMcuSmc2TemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuSmc2TemplateParticipant> busiTemplateParticipants, List<BusiMcuSmc2TemplateDept> templateDepts)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        busiTemplateConference.setUpdateTime(new Date());
        if (YesOrNo.convert(busiTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
        {
            Assert.notNull(busiTemplateConference.getConferenceNumber(), "会议号不能为空！");
        }

        // 校验会议类型
        //ConferenceType.convert(busiTemplateConference.getType());
        // 新直播方式：事先配置的直播地址时模板会议自动开启直播


        // 先置空主会场配置
        busiTemplateConference.setMasterParticipantId(null);
        busiTemplateConference.setCreateUserId(null);
        busiTemplateConference.setCreateUserName(null);
        int c = busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConference);
        if (c > 0)
        {
            doParticipantUpdate(busiTemplateConference, busiTemplateParticipants, masterTerminalId);
            doDeptUpdate(busiTemplateConference, templateDepts);

            // 修改号码状态为已绑定
//            if (busiTemplateConference.getConferenceNumber() != null)
//            {
//                BusiConferenceNumber cn = new BusiConferenceNumber();
//                cn.setId(busiTemplateConference.getConferenceNumber());
//                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
//                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
//                updatePassword(busiTemplateConference);
//            }
//
//            BusiMcuSmc2TemplateConference old = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiTemplateConference.getId());
//            if (old.getConferenceNumber() != null && !old.getConferenceNumber().equals(busiTemplateConference.getConferenceNumber()))
//            {
//                BusiMcuSmc2TemplateConference con = new BusiMcuSmc2TemplateConference();
//                con.setConferenceNumber(old.getConferenceNumber());
//                List<BusiMcuSmc2TemplateConference> bcs = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceList(con);
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

    private void doDeptUpdate(BusiMcuSmc2TemplateConference busiTemplateConference, List<BusiMcuSmc2TemplateDept> templateDepts)
    {
        BusiMcuSmc2TemplateDept deptCon = new BusiMcuSmc2TemplateDept();
        deptCon.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiMcuSmc2TemplateDept> ds = busiMcuSmc2TemplateDeptMapper.selectBusiMcuSmc2TemplateDeptList(deptCon);
        Map<Long, BusiMcuSmc2TemplateDept> oldMap = new HashMap<>();
        for (BusiMcuSmc2TemplateDept busiTemplateDept : ds)
        {
            oldMap.put(busiTemplateDept.getDeptId(), busiTemplateDept);
        }

        // 添加模板部门顺序信息
        if (!ObjectUtils.isEmpty(templateDepts))
        {
            for (BusiMcuSmc2TemplateDept busiTemplateDept : templateDepts)
            {
                busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                BusiMcuSmc2TemplateDept oldTd = oldMap.remove(busiTemplateDept.getDeptId());
                if (oldTd != null)
                {
                    oldTd.setUpdateTime(new Date());
                    oldTd.setWeight(busiTemplateDept.getWeight());
                    busiTemplateDept = oldTd;
                    busiMcuSmc2TemplateDeptMapper.updateBusiMcuSmc2TemplateDept(busiTemplateDept);
                }
                else
                {
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuSmc2TemplateDeptMapper.insertBusiMcuSmc2TemplateDept(busiTemplateDept);
                }
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((deptId, td) -> {
                busiMcuSmc2TemplateConferenceDefaultViewDeptMapper.deleteBusiMcuSmc2TemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(td.getTemplateConferenceId(), td.getDeptId());
                busiMcuSmc2TemplateDeptMapper.deleteBusiMcuSmc2TemplateDeptById(td.getId());
            });
        }
    }

    private void doParticipantUpdate(BusiMcuSmc2TemplateConference busiTemplateConference, List<BusiMcuSmc2TemplateParticipant> busiTemplateParticipants, Long masterTerminalId)
    {
        BusiMcuSmc2TemplateParticipant query = new BusiMcuSmc2TemplateParticipant();
        query.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiMcuSmc2TemplateParticipant> ps = busiMcuSmc2TemplateParticipantMapper.selectBusiMcuSmc2TemplateParticipantList(query);
        Map<Long, BusiMcuSmc2TemplateParticipant> oldMap = new HashMap<>();
        for (BusiMcuSmc2TemplateParticipant tp : ps)
        {
            oldMap.put(tp.getTerminalId(), tp);
        }

        if (!ObjectUtils.isEmpty(busiTemplateParticipants))
        {
            for (BusiMcuSmc2TemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
            {
                busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                BusiMcuSmc2TemplateParticipant oldTp = oldMap.remove(busiTemplateParticipant.getTerminalId());
                if (oldTp != null)
                {
                    oldTp.setWeight(busiTemplateParticipant.getWeight());
                    oldTp.setUpdateTime(new Date());
                    oldTp.setAttendType(busiTemplateParticipant.getAttendType());
                    oldTp.setBusinessProperties(busiTemplateParticipant.getBusinessProperties());
                    busiMcuSmc2TemplateParticipantMapper.updateBusiMcuSmc2TemplateParticipant(oldTp);
                    busiTemplateParticipant = oldTp;
                }
                else
                {
                    // 新增
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiMcuSmc2TemplateParticipantMapper.insertBusiMcuSmc2TemplateParticipant(busiTemplateParticipant);
                }

                if (masterTerminalId != null && masterTerminalId.longValue() == busiTemplateParticipant.getTerminalId().longValue())
                {
                    // 设置模板会议中配置的主会场参会终端
                    busiTemplateConference.setMasterParticipantId(busiTemplateParticipant.getId());
                    busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConference);
                }
            }

            if (masterTerminalId == null)
            {
                // 设置模板会议中配置的主会场参会终端
                busiTemplateConference.setMasterParticipantId(null);
                busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConference);
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((terminalId, tp) -> {
                busiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuSmc2TemplateConferenceDefaultViewPaticipantById(tp.getId());
                busiMcuSmc2TemplateParticipantMapper.deleteBusiMcuSmc2TemplateParticipantById(tp.getId());
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




        BusiMcuSmc2TemplateConference busiTemplateConference = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(id);
        if(busiTemplateConference==null){
            throw new CustomException("模板不存在");
        }
        busiTemplateConference.setMasterParticipantId(null);
        busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConference);
        deleteParticipants(id);
        int c = busiMcuSmc2TemplateConferenceMapper.deleteBusiMcuSmc2TemplateConferenceById(id);
        if (c > 0 && busiTemplateConference.getConferenceNumber() != null)
        {
            BusiMcuSmc2TemplateConference con = new BusiMcuSmc2TemplateConference();
            con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
            List<BusiMcuSmc2TemplateConference> cs = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceList(busiTemplateConference);
            if (ObjectUtils.isEmpty(cs))
            {
                // 修改号码状态为闲置
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
            }

            // 若是自动创建的会议号，则删除模板的时候同步进行删除
            BusiConferenceNumber bcn = busiConferenceNumberService.selectBusiConferenceNumberById(busiTemplateConference.getConferenceNumber());
            if(bcn!=null){
                if (ConferenceNumberCreateType.convert(bcn.getCreateType()) == ConferenceNumberCreateType.AUTO) {
                    busiConferenceNumberService.deleteBusiConferenceNumberById(busiTemplateConference.getConferenceNumber());
                }
            }

        }
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

        BusiMcuSmc2TemplateConference busiTemplateConference = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(id);
        busiTemplateConference.setMasterParticipantId(null);
        busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiTemplateConference);
        deleteParticipants(id);
        int c = busiMcuSmc2TemplateConferenceMapper.deleteBusiMcuSmc2TemplateConferenceById(id);
        if (c > 0 && busiTemplateConference.getConferenceNumber() != null) {
            BusiMcuSmc2TemplateConference con = new BusiMcuSmc2TemplateConference();
            con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
            List<BusiMcuSmc2TemplateConference> cs = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceList(busiTemplateConference);
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

        BusiMcuSmc2TemplateConference tc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(id);
        if (tc == null || Objects.equals(tc.getName(), name)) {
            return 0;
        }
        tc.setName(name);
        return   busiMcuSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(tc);
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
        busiMcuSmc2TemplateConferenceDefaultViewDeptMapper.deleteBusiMcuSmc2TemplateConferenceDefaultViewDeptById(id);

        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuSmc2TemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuSmc2TemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);

        // 删除分屏信息
        busiMcuSmc2TemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuSmc2TemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);

        // 先删除与会者模板
        BusiMcuSmc2TemplateParticipant busiTemplateParticipant = new BusiMcuSmc2TemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(id);

        List<Long> pIds = new ArrayList<>();
        List<BusiMcuSmc2TemplateParticipant> ps = busiMcuSmc2TemplateParticipantMapper.selectBusiMcuSmc2TemplateParticipantList(busiTemplateParticipant);
        for (BusiMcuSmc2TemplateParticipant busiTemplateParticipant2 : ps)
        {
            pIds.add(busiTemplateParticipant2.getId());
        }

        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuSmc2TemplateParticipantMapper.deleteBusiMcuSmc2TemplateParticipantByIds(pIds.toArray(new Long[pIds.size()]));
        }

        // 删除部门顺序信息
        BusiMcuSmc2TemplateDept busiTemplateDept = new BusiMcuSmc2TemplateDept();
        busiTemplateDept.setTemplateConferenceId(id);
        pIds = new ArrayList<>();
        List<BusiMcuSmc2TemplateDept> ds = busiMcuSmc2TemplateDeptMapper.selectBusiMcuSmc2TemplateDeptList(busiTemplateDept);
        for (BusiMcuSmc2TemplateDept busiTemplateDept2 : ds)
        {
            pIds.add(busiTemplateDept2.getId());
        }
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuSmc2TemplateDeptMapper.deleteBusiMcuSmc2TemplateDeptByIds(pIds.toArray(new Long[pIds.size()]));
        }
    }
}
