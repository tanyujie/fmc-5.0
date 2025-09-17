package com.paradisecloud.fcm.fme.conference.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IBusiTemplatePollingSchemeService;
import com.paradisecloud.fcm.fme.attendee.model.enumer.PollingStrategy;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.DeptTenantCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
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
public class BusiTemplateConferenceServiceImpl implements IBusiTemplateConferenceService
{
    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    @Resource
    private BusiTemplateParticipantMapper busiTemplateParticipantMapper;
    @Resource
    private BusiTemplateDeptMapper busiTemplateDeptMapper;
    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;
    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;
    @Resource
    private IBusiTemplatePollingSchemeService busiTemplatePollingSchemeService;
    @Resource
    private BusiTemplateConferenceDefaultViewCellScreenMapper busiTemplateConferenceDefaultViewCellScreenMapper;
    @Resource
    private BusiTemplateConferenceDefaultViewDeptMapper busiTemplateConferenceDefaultViewDeptMapper;
    @Resource
    private BusiTemplateConferenceDefaultViewPaticipantMapper busiTemplateConferenceDefaultViewPaticipantMapper;
    @Resource
    private BusiLiveSettingMapper busiLiveSettingMapper;
    @Resource
    private ICoSpaceService coSpaceService;

    /**
     * 更新默认视图配置信息
     * @author lilinhai
     * @since 2021-04-08 15:30
     * @param jsonObj
     * @param id
     * @see com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService#updateDefaultViewConfigInfo(com.alibaba.fastjson.JSONObject, java.lang.Long)
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
        BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(id);
        tc.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        tc.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        tc.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        tc.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setPollingInterval(jsonObj.getInteger("pollingInterval"));
        int c = busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1003432, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");

        // 根据模板ID删除分频信息
        busiTemplateConferenceDefaultViewCellScreenMapper.deleteBusiTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);
        // 根据模板ID批量删除默认视图的参会者信息
        busiTemplateConferenceDefaultViewPaticipantMapper.deleteBusiTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);
        // 默认视图的参会者信息保存
        Assert.isTrue(jsonObj.containsKey("defaultViewPaticipants"), "defaultViewpaticipants默认视图的参会者信息不能为空");
        ja = jsonObj.getJSONArray("defaultViewPaticipants");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            if (!jo.containsKey("templateParticipantId")) {
                if (jo.containsKey("terminalId")) {
                    try {
                        BusiTemplateParticipant busiTemplateParticipantCon = new BusiTemplateParticipant();
                        busiTemplateParticipantCon.setTemplateConferenceId(id);
                        busiTemplateParticipantCon.setTerminalId(jo.getLong("terminalId"));
                        List<BusiTemplateParticipant> busiTemplateParticipants = busiTemplateParticipantMapper.selectBusiTemplateParticipantList(busiTemplateParticipantCon);
                        if (busiTemplateParticipants.size() > 0) {
                            BusiTemplateParticipant busiTemplateParticipantTemp = busiTemplateParticipants.get(0);
                            jo.put("templateParticipantId", busiTemplateParticipantTemp.getId());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant = new BusiTemplateConferenceDefaultViewPaticipant();
            busiTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiTemplateConferenceDefaultViewPaticipantMapper.insertBusiTemplateConferenceDefaultViewPaticipant(busiTemplateConferenceDefaultViewPaticipant);
        }
        ja = jsonObj.getJSONArray("defaultViewCellScreens");

        if (tc.getDefaultViewLayout().equals(AutomaticSplitScreen.LAYOUT)
                || tc.getDefaultViewLayout().equals(AllEqualSplitScreen.LAYOUT)
                || tc.getDefaultViewLayout().equals(OnePlusNSplitScreen.LAYOUT)
                || tc.getDefaultViewLayout().equals(StackedSplitScreen.LAYOUT)
                || tc.getDefaultViewLayout().equals(TelepresenceSplitScreen.LAYOUT))
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
                BusiTemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen = new BusiTemplateConferenceDefaultViewCellScreen();
                busiTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiTemplateConferenceDefaultViewPaticipant con = new BusiTemplateConferenceDefaultViewPaticipant();
                    List<BusiTemplateConferenceDefaultViewPaticipant> ps = busiTemplateConferenceDefaultViewPaticipantMapper.selectBusiTemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }

                busiTemplateConferenceDefaultViewCellScreenMapper.insertBusiTemplateConferenceDefaultViewCellScreen(busiTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        busiTemplateConferenceDefaultViewDeptMapper.deleteBusiTemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiTemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept = new BusiTemplateConferenceDefaultViewDept();
            busiTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiTemplateConferenceDefaultViewDeptMapper.insertBusiTemplateConferenceDefaultViewDept(busiTemplateConferenceDefaultViewDept);
        }


    }

    @Override
    public String selectBusiTemplateConferenceCoverById(Long id)
    {
        BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(id);
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
    public ModelBean selectBusiTemplateConferenceById(Long id)
    {
        BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return getTemplateConferenceDetails(tc);
    }

    public ModelBean getTemplateConferenceDetails(BusiTemplateConference tc)
    {
        BusiTemplateParticipant busiTemplateParticipant = new BusiTemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(tc.getId());
        List<ModelBean> pMbs = new ArrayList<>();
        List<BusiTemplateParticipant> ps = busiTemplateParticipantMapper.selectBusiTemplateParticipantList(busiTemplateParticipant);

        int onlineCount = 0;
        int weight = 0;
        String mainName = "";
        Map<Long, ModelBean> bpm = new HashMap<>();
        for (BusiTemplateParticipant busiTemplateParticipant2 : ps)
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

        BusiTemplateConferenceDefaultViewCellScreen con = new BusiTemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(tc.getId());
        List<BusiTemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiTemplateConferenceDefaultViewCellScreenMapper.selectBusiTemplateConferenceDefaultViewCellScreenList(con);

        BusiTemplateConferenceDefaultViewDept con1 = new BusiTemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(tc.getId());
        List<BusiTemplateConferenceDefaultViewDept> defaultViewDepts = busiTemplateConferenceDefaultViewDeptMapper.selectBusiTemplateConferenceDefaultViewDeptList(con1);

        BusiTemplateConferenceDefaultViewPaticipant con2 = new BusiTemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(tc.getId());
        List<BusiTemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiTemplateConferenceDefaultViewPaticipantMapper.selectBusiTemplateConferenceDefaultViewPaticipantList(con2);
        List<ModelBean> defaultViewPaticipantMbs = new ArrayList<>();
        for (BusiTemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants) {
            ModelBean mb = bpm.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                mb.put("weight", busiTemplateConferenceDefaultViewPaticipant.getWeight());
                mb.put("cellSequenceNumber", busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbs.add(mb);
            }
        }

        BusiTemplateDept tdCon = new BusiTemplateDept();
        tdCon.setTemplateConferenceId(tc.getId());
        List<BusiTemplateDept> tds = busiTemplateDeptMapper.selectBusiTemplateDeptList(tdCon);

        ModelBean tcmb = new ModelBean(tc);
        if (tc.getMasterParticipantId() != null)
        {
            tcmb.put("masterTerminalId", bpm.get(tc.getMasterParticipantId()).get("terminalId"));
        }
        tcmb.put("tenantId", "");
        ModelBean mb = new ModelBean();
        mb.put("templateConference", tcmb);
        mb.put("templateParticipants", pMbs);
        mb.put("templateDepts", tds);
        mb.put("defaultViewCellScreens", defaultViewCellScreens);
        mb.put("defaultViewDepts", defaultViewDepts);
        mb.put("defaultViewPaticipants", defaultViewPaticipantMbs);
        mb.put("onlineCount", onlineCount);
        if (tc.getConferenceNumber() != null && ConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.FME)))
        {
            mb.put("meetingJoinedCount", ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.FME)).getAttendeeCountingStatistics().getMeetingJoinedCount());
        }
        mb.put("mainVenue", mainName);
        mb.put("isStart", tc.getConferenceNumber() != null && ConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.FME)));
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
        mb.put("mcuType", McuType.FME.getCode());
        return mb;
    }

    /**
     * 查询会议模板列表
     *
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiTemplateConference> selectBusiTemplateConferenceList(BusiTemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    @Override
    public Page<BusiTemplateConference> selectBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        Page<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectBusiTemplateConferenceListByKey(searchKey,deptId);
        return tcs;
    }

    @Override
    public List<BusiTemplateConference> selectAllBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        return  busiTemplateConferenceMapper.selectAllBusiTemplateConferenceListByKey(searchKey,deptId);
    }

    /**
     * 查询会议模板列表
     *
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiTemplateConference> selectAllBusiTemplateConferenceList(BusiTemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectAllBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    public List<BusiTemplateConference> selectBusiTemplateConferenceListWithoutBusinessFieldType(BusiTemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(busiTemplateConference);
        return tcs;
    }

    public List<BusiTemplateConference> selectAllBusiTemplateConferenceListWithoutBusinessFieldType(BusiTemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(busiTemplateConference);
        return tcs;
    }

    /**
     * <pre>模板集合转换成modelBean</pre>
     * @author lilinhai
     * @since 2021-01-30 14:04
     * @param tcs
     * @return List<ModelBean>
     */
    public List<ModelBean> toModelBean(List<BusiTemplateConference> tcs)
    {
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiTemplateConference tc : tcs)
        {
            mbs.add(getTemplateConferenceDetails(tc));
        }

        return mbs;
    }

    @Override
    public List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType)
    {
        BusinessFieldType.convert(businessFieldType);
        return busiTemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
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
    public int insertBusiTemplateConference(BusiTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiTemplateParticipant> busiTemplateParticipants, List<BusiTemplateDept> templateDepts)
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
            throw new SystemException(1004543, "添加会议模板，部门ID不能为空！");
        }

        if (busiTemplateConference.getCreateUserId() == null || busiTemplateConference.getCreateUserName() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setCreateUserId(loginUser.getUser().getUserId());
            busiTemplateConference.setCreateUserName(loginUser.getUser().getUserName());
        }
        if (busiTemplateConference.getMinutesEnabled() == null) {
            busiTemplateConference.setMinutesEnabled(YesOrNo.NO.getValue());
        }

        // 校验会议类型
        //ConferenceType.convert(busiTemplateConference.getType());

        // 新直播方式：事先配置的直播地址时模板会议自动开启直播
        try {
            if (StringUtils.isNotEmpty(busiTemplateConference.getStreamUrl())) {
                BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
                busiLiveSettingCon.setUrl(busiTemplateConference.getStreamUrl());
                List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
                if (busiLiveSettingList.size() > 0) {
                    BusiLiveSetting busiLiveSetting = busiLiveSettingList.get(0);
                    if (StringUtils.isNotEmpty(busiLiveSetting.getRemoteParty())) {
                        // 已经被配置到会议的地址不能再被配置
                        BusiTemplateConference busiTemplateConferenceCon = new BusiTemplateConference();
                        busiTemplateConferenceCon.setStreamUrl(busiTemplateConference.getStreamUrl());
                        List<BusiTemplateConference> busiTemplateConferenceList = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(busiTemplateConferenceCon);
                        if (busiTemplateConferenceList.size() > 0) {
                            BusiTemplateConference busiTemplateConferenceExist = busiTemplateConferenceList.get(0);
                            SysDept sysDept = SysDeptCache.getInstance().get(busiTemplateConferenceExist.getDeptId());
                            String deptName = sysDept.getDeptName();
                            String conferenceName = busiTemplateConferenceExist.getName();
                            throw new SystemException(1004543, "该直播地址已被[" + deptName + ":" + conferenceName + "]使用！请选择其它直播地址！");
                        }
                        busiTemplateConference.setStreamingEnabled(YesOrNo.YES.getValue());
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof SystemException) {
                throw e;
            }
        }

        int c = 0;
        try {
            c = busiTemplateConferenceMapper.insertBusiTemplateConference(busiTemplateConference);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.isTrue(false, "添加会议模板失败！请刷新页面后重试！");
        }
        if (c > 0)
        {
            if (!ObjectUtils.isEmpty(busiTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiTemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
                {
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    try
                    {
                        busiTemplateParticipantMapper.insertBusiTemplateParticipant(busiTemplateParticipant);
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
                        busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiTemplateDept busiTemplateDept : templateDepts)
                {
                    busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiTemplateDeptMapper.insertBusiTemplateDept(busiTemplateDept);
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

            // 添加默认轮询模板
            BusiTemplatePollingScheme busiTemplatePollingScheme = new BusiTemplatePollingScheme();
            busiTemplatePollingScheme.setEnableStatus(YesOrNo.YES.getValue());
            busiTemplatePollingScheme.setIsBroadcast(YesOrNo.YES.getValue());
            busiTemplatePollingScheme.setIsDisplaySelf(PanePlacementSelfPaneMode.OFF.getValue());
            busiTemplatePollingScheme.setSchemeName("全局轮询");
            busiTemplatePollingScheme.setLayout(OneSplitScreen.LAYOUT);
            busiTemplatePollingScheme.setIsFill(YesOrNo.YES.getValue());
            busiTemplatePollingScheme.setPollingInterval(10);
            busiTemplatePollingScheme.setPollingStrategy(PollingStrategy.GLOBAL.getValue());
            busiTemplatePollingScheme.setTemplateConferenceId(busiTemplateConference.getId());
            busiTemplatePollingSchemeService.insertBusiTemplatePollingScheme(busiTemplatePollingScheme, null, null);
        }
        return c;
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
    public int insertBusiTemplateConferenceOps(BusiTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiTemplateParticipant> busiTemplateParticipants, List<BusiTemplateDept> templateDepts)
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
            throw new SystemException(1004543, "添加会议模板，部门ID不能为空！");
        }

        if (busiTemplateConference.getCreateUserId() == null || busiTemplateConference.getCreateUserName() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setCreateUserId(loginUser.getUser().getUserId());
            busiTemplateConference.setCreateUserName(loginUser.getUser().getUserName());
        }
        if (busiTemplateConference.getMinutesEnabled() == null) {
            busiTemplateConference.setMinutesEnabled(YesOrNo.NO.getValue());
        }

        // 校验会议类型
        //ConferenceType.convert(busiTemplateConference.getType());

        // 新直播方式：事先配置的直播地址时模板会议自动开启直播
        try {
            if (StringUtils.isNotEmpty(busiTemplateConference.getStreamUrl())) {
                BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
                busiLiveSettingCon.setUrl(busiTemplateConference.getStreamUrl());
                List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
                if (busiLiveSettingList.size() > 0) {
                    BusiLiveSetting busiLiveSetting = busiLiveSettingList.get(0);
                    if (StringUtils.isNotEmpty(busiLiveSetting.getRemoteParty())) {
                        // 已经被配置到会议的地址不能再被配置
                        BusiTemplateConference busiTemplateConferenceCon = new BusiTemplateConference();
                        busiTemplateConferenceCon.setStreamUrl(busiTemplateConference.getStreamUrl());
                        List<BusiTemplateConference> busiTemplateConferenceList = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(busiTemplateConferenceCon);
                        if (busiTemplateConferenceList.size() > 0) {
                            BusiTemplateConference busiTemplateConferenceExist = busiTemplateConferenceList.get(0);
                            SysDept sysDept = SysDeptCache.getInstance().get(busiTemplateConferenceExist.getDeptId());
                            String deptName = sysDept.getDeptName();
                            String conferenceName = busiTemplateConferenceExist.getName();
                            throw new SystemException(1004543, "该直播地址已被[" + deptName + ":" + conferenceName + "]使用！请选择其它直播地址！");
                        }
                        busiTemplateConference.setStreamingEnabled(YesOrNo.YES.getValue());
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof SystemException) {
                throw e;
            }
        }

        int c = 0;
        try {
            c = busiTemplateConferenceMapper.insertBusiTemplateConference(busiTemplateConference);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.isTrue(false, "添加会议模板失败！请刷新页面后重试！");
        }
        if (c > 0)
        {
            if (!ObjectUtils.isEmpty(busiTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiTemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
                {
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    try
                    {
                        busiTemplateParticipantMapper.insertBusiTemplateParticipant(busiTemplateParticipant);
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
                        busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConference);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiTemplateDept busiTemplateDept : templateDepts)
                {
                    busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiTemplateDeptMapper.insertBusiTemplateDept(busiTemplateDept);
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

            // 添加默认轮询模板
            BusiTemplatePollingScheme busiTemplatePollingScheme = new BusiTemplatePollingScheme();
            busiTemplatePollingScheme.setEnableStatus(YesOrNo.YES.getValue());
            busiTemplatePollingScheme.setIsBroadcast(YesOrNo.YES.getValue());
            busiTemplatePollingScheme.setIsDisplaySelf(PanePlacementSelfPaneMode.SELF.getValue());
            busiTemplatePollingScheme.setSchemeName("全局轮询");
            busiTemplatePollingScheme.setLayout(OneSplitScreen.LAYOUT);
            busiTemplatePollingScheme.setIsFill(YesOrNo.YES.getValue());
            busiTemplatePollingScheme.setPollingInterval(10);
            busiTemplatePollingScheme.setPollingStrategy(PollingStrategy.GLOBAL.getValue());
            busiTemplatePollingScheme.setTemplateConferenceId(busiTemplateConference.getId());
            busiTemplatePollingSchemeService.insertBusiTemplatePollingScheme(busiTemplatePollingScheme, null, null);
        }
        return c;
    }

    private void updatePassword(BusiTemplateConference busiTemplateConference) {
        FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(busiTemplateConference.getDeptId());
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(busiTemplateConference.getConferenceNumber());
        if (coSpace != null)
        {
            CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
            coSpaceParamBuilder.passcode(busiTemplateConference.getConferencePassword());
            RestResponse rr = fmeBridge.getCoSpaceInvoker().updateCoSpace(coSpace.getId(), coSpaceParamBuilder.build());
            if (!rr.isSuccess())
            {
                throw new SystemException(1008989, rr.getMessage());
            }
        }
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
    public int updateBusiTemplateConference(BusiTemplateConference busiTemplateConference, Long masterTerminalId, List<BusiTemplateParticipant> busiTemplateParticipants, List<BusiTemplateDept> templateDepts)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        Assert.notNull(busiTemplateConference.getIsAutoCreateConferenceNumber(), "会议号创建类型不能为空！");
        busiTemplateConference.setUpdateTime(new Date());
        if (YesOrNo.convert(busiTemplateConference.getIsAutoCreateConferenceNumber()) == YesOrNo.NO)
        {
            Assert.notNull(busiTemplateConference.getConferenceNumber(), "会议号不能为空！");
        }

        // 校验会议类型
       // ConferenceType.convert(busiTemplateConference.getType());
        // 新直播方式：事先配置的直播地址时模板会议自动开启直播
        try {
            if (StringUtils.isNotEmpty(busiTemplateConference.getStreamUrl())) {
                BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
                busiLiveSettingCon.setUrl(busiTemplateConference.getStreamUrl());
                List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
                if (busiLiveSettingList.size() > 0) {
                    BusiLiveSetting busiLiveSetting = busiLiveSettingList.get(0);
                    if (StringUtils.isNotEmpty(busiLiveSetting.getRemoteParty())) {
                        // 已经被配置到会议的地址不能再被配置
                        BusiTemplateConference busiTemplateConferenceCon = new BusiTemplateConference();
                        busiTemplateConferenceCon.setStreamUrl(busiTemplateConference.getStreamUrl());
                        List<BusiTemplateConference> busiTemplateConferenceList = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(busiTemplateConferenceCon);
                        if (busiTemplateConferenceList.size() > 0) {
                            BusiTemplateConference busiTemplateConferenceExist = busiTemplateConferenceList.get(0);
                            if (busiTemplateConferenceExist.getId().longValue() != busiTemplateConference.getId().longValue()) {
                                SysDept sysDept = SysDeptCache.getInstance().get(busiTemplateConferenceExist.getDeptId());
                                String deptName = sysDept.getDeptName();
                                String conferenceName = busiTemplateConferenceExist.getName();
                                throw new SystemException(1004543, "该直播地址已被[" + deptName + ":" + conferenceName + "]使用！请选择其它直播地址！");
                            }
                        }
                        busiTemplateConference.setStreamingEnabled(YesOrNo.YES.getValue());
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof SystemException) {
                throw e;
            }
        }

        // 先置空主会场配置
        busiTemplateConference.setMasterParticipantId(null);
        busiTemplateConference.setCreateUserId(null);
        busiTemplateConference.setCreateUserName(null);
        int c = busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConference);
        if (c > 0)
        {
            doParticipantUpdate(busiTemplateConference, busiTemplateParticipants, masterTerminalId);
            doDeptUpdate(busiTemplateConference, templateDepts);

            // 修改号码状态为已绑定
            if (busiTemplateConference.getConferenceNumber() != null)
            {
                BusiConferenceNumber cn = new BusiConferenceNumber();
                cn.setId(busiTemplateConference.getConferenceNumber());
                cn.setStatus(ConferenceNumberStatus.BOUND.getValue());
                busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
                updatePassword(busiTemplateConference);
            }

            BusiTemplateConference old = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiTemplateConference.getId());
            if (old.getConferenceNumber() != null && !old.getConferenceNumber().equals(busiTemplateConference.getConferenceNumber()))
            {
                BusiTemplateConference con = new BusiTemplateConference();
                con.setConferenceNumber(old.getConferenceNumber());
                List<BusiTemplateConference> bcs = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(con);
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

    private void doDeptUpdate(BusiTemplateConference busiTemplateConference, List<BusiTemplateDept> templateDepts)
    {
        BusiTemplateDept deptCon = new BusiTemplateDept();
        deptCon.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiTemplateDept> ds = busiTemplateDeptMapper.selectBusiTemplateDeptList(deptCon);
        Map<Long, BusiTemplateDept> oldMap = new HashMap<>();
        for (BusiTemplateDept busiTemplateDept : ds)
        {
            oldMap.put(busiTemplateDept.getDeptId(), busiTemplateDept);
        }

        // 添加模板部门顺序信息
        if (!ObjectUtils.isEmpty(templateDepts))
        {
            for (BusiTemplateDept busiTemplateDept : templateDepts)
            {
                busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                BusiTemplateDept oldTd = oldMap.remove(busiTemplateDept.getDeptId());
                if (oldTd != null)
                {
                    oldTd.setUpdateTime(new Date());
                    oldTd.setWeight(busiTemplateDept.getWeight());
                    busiTemplateDept = oldTd;
                    busiTemplateDeptMapper.updateBusiTemplateDept(busiTemplateDept);
                }
                else
                {
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiTemplateDeptMapper.insertBusiTemplateDept(busiTemplateDept);
                }
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((deptId, td) -> {
                busiTemplateConferenceDefaultViewDeptMapper.deleteBusiTemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(td.getTemplateConferenceId(), td.getDeptId());
                busiTemplateDeptMapper.deleteBusiTemplateDeptById(td.getId());
            });
        }
    }

    private void doParticipantUpdate(BusiTemplateConference busiTemplateConference, List<BusiTemplateParticipant> busiTemplateParticipants, Long masterTerminalId)
    {
        BusiTemplateParticipant query = new BusiTemplateParticipant();
        query.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiTemplateParticipant> ps = busiTemplateParticipantMapper.selectBusiTemplateParticipantList(query);
        Map<Long, BusiTemplateParticipant> oldMap = new HashMap<>();
        for (BusiTemplateParticipant tp : ps)
        {
            oldMap.put(tp.getTerminalId(), tp);
        }

        if (!ObjectUtils.isEmpty(busiTemplateParticipants))
        {
            for (BusiTemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
            {
                busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                BusiTemplateParticipant oldTp = oldMap.remove(busiTemplateParticipant.getTerminalId());
                if (oldTp != null)
                {
                    oldTp.setWeight(busiTemplateParticipant.getWeight());
                    oldTp.setUpdateTime(new Date());
                    oldTp.setAttendType(busiTemplateParticipant.getAttendType());
                    oldTp.setBusinessProperties(busiTemplateParticipant.getBusinessProperties());
                    busiTemplateParticipantMapper.updateBusiTemplateParticipant(oldTp);
                    busiTemplateParticipant = oldTp;
                }
                else
                {
                    // 新增
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());
                    busiTemplateParticipantMapper.insertBusiTemplateParticipant(busiTemplateParticipant);
                }

                if (masterTerminalId != null && masterTerminalId.longValue() == busiTemplateParticipant.getTerminalId().longValue())
                {
                    // 设置模板会议中配置的主会场参会终端
                    busiTemplateConference.setMasterParticipantId(busiTemplateParticipant.getId());
                    busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConference);
                }
            }

            if (masterTerminalId == null)
            {
                // 设置模板会议中配置的主会场参会终端
                busiTemplateConference.setMasterParticipantId(null);
                busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConference);
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((terminalId, tp) -> {
                busiTemplateConferenceDefaultViewPaticipantMapper.deleteBusiTemplateConferenceDefaultViewPaticipantByTemplateParticipantId(tp.getId());
                busiTemplateParticipantMapper.deleteBusiTemplateParticipantById(tp.getId());
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
        BusiTemplatePollingScheme busiTemplatePollingScheme = new BusiTemplatePollingScheme();
        busiTemplatePollingScheme.setTemplateConferenceId(id);
        List<BusiTemplatePollingScheme> pss = busiTemplatePollingSchemeService.selectBusiTemplatePollingSchemeList(busiTemplatePollingScheme);
        if (!ObjectUtils.isEmpty(pss))
        {
            for (BusiTemplatePollingScheme busiTemplatePollingScheme2 : pss)
            {
                busiTemplatePollingSchemeService.deleteBusiTemplatePollingSchemeById(busiTemplatePollingScheme2.getId());
            }
        }

        // 删除轮询方案
        busiTemplatePollingSchemeService.deleteBusiTemplatePollingSchemeById(id);
        BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(id);
        busiTemplateConference.setMasterParticipantId(null);
        busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConference);
        deleteParticipants(id);
        int c = busiTemplateConferenceMapper.deleteBusiTemplateConferenceById(id);
        if (c > 0 && busiTemplateConference.getConferenceNumber() != null)
        {
            BusiTemplateConference con = new BusiTemplateConference();
            con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
            List<BusiTemplateConference> cs = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(busiTemplateConference);
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
            if (ConferenceNumberCreateType.convert(bcn.getCreateType()) == ConferenceNumberCreateType.AUTO) {
                busiConferenceNumberService.deleteBusiConferenceNumberById(busiTemplateConference.getConferenceNumber());
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
        BusiTemplatePollingScheme busiTemplatePollingScheme = new BusiTemplatePollingScheme();
        busiTemplatePollingScheme.setTemplateConferenceId(id);
        List<BusiTemplatePollingScheme> pss = busiTemplatePollingSchemeService.selectBusiTemplatePollingSchemeList(busiTemplatePollingScheme);
        if (!ObjectUtils.isEmpty(pss)) {
            for (BusiTemplatePollingScheme busiTemplatePollingScheme2 : pss) {
                busiTemplatePollingSchemeService.deleteBusiTemplatePollingSchemeById(busiTemplatePollingScheme2.getId());
            }
        }

        // 删除轮询方案
        busiTemplatePollingSchemeService.deleteBusiTemplatePollingSchemeById(id);
        BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(id);
        busiTemplateConference.setMasterParticipantId(null);
        busiTemplateConferenceMapper.updateBusiTemplateConference(busiTemplateConference);
        deleteParticipants(id);
        int c = busiTemplateConferenceMapper.deleteBusiTemplateConferenceById(id);
        if (c > 0 && busiTemplateConference.getConferenceNumber() != null) {
            BusiTemplateConference con = new BusiTemplateConference();
            con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
            List<BusiTemplateConference> cs = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(busiTemplateConference);
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

        BusiTemplateConference tc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(id);
        if (tc == null || Objects.equals(tc.getName(), name)) {
            return 0;
        }
        tc.setName(name);
        return   busiTemplateConferenceMapper.updateBusiTemplateConference(tc);
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
        busiTemplateConferenceDefaultViewDeptMapper.deleteBusiTemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 根据模板ID批量删除默认视图的参会者信息
        busiTemplateConferenceDefaultViewPaticipantMapper.deleteBusiTemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);

        // 删除分屏信息
        busiTemplateConferenceDefaultViewCellScreenMapper.deleteBusiTemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);

        // 先删除与会者模板
        BusiTemplateParticipant busiTemplateParticipant = new BusiTemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(id);

        List<Long> pIds = new ArrayList<>();
        List<BusiTemplateParticipant> ps = busiTemplateParticipantMapper.selectBusiTemplateParticipantList(busiTemplateParticipant);
        for (BusiTemplateParticipant busiTemplateParticipant2 : ps)
        {
            pIds.add(busiTemplateParticipant2.getId());
        }

        if (!ObjectUtils.isEmpty(pIds))
        {
            busiTemplateParticipantMapper.deleteBusiTemplateParticipantByIds(pIds.toArray(new Long[pIds.size()]));
        }

        // 删除部门顺序信息
        BusiTemplateDept busiTemplateDept = new BusiTemplateDept();
        busiTemplateDept.setTemplateConferenceId(id);
        pIds = new ArrayList<>();
        List<BusiTemplateDept> ds = busiTemplateDeptMapper.selectBusiTemplateDeptList(busiTemplateDept);
        for (BusiTemplateDept busiTemplateDept2 : ds)
        {
            pIds.add(busiTemplateDept2.getId());
        }
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiTemplateDeptMapper.deleteBusiTemplateDeptByIds(pIds.toArray(new Long[pIds.size()]));
        }
    }

    /**
     * <pre>获取coSpaceId</pre>
     * @author lilinhai
     * @since 2021-02-01 13:51
     * @param busiConferenceNumber
     * @param deptId
     * @return String
     */
    public void createCoSpaceId(BusiConferenceNumber busiConferenceNumber, Long deptId)
    {
        CoSpace coSpace = coSpaceService.getCoSpaceByConferenceNumber(deptId, busiConferenceNumber.getId().toString());
        if (busiConferenceNumber.getParams() != null)
        {
            CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
            Object profileIdObj = busiConferenceNumber.getParams().get("callLegProfileId");
            if (profileIdObj != null)
            {
                String profileId = profileIdObj.toString();
                coSpaceParamBuilder.callLegProfile(profileId);
            }
            else
            {
                coSpaceParamBuilder.callLegProfile("");
            }

            profileIdObj = busiConferenceNumber.getParams().get("callProfileId");
            if (profileIdObj != null)
            {
                String profileId = profileIdObj.toString();
                coSpaceParamBuilder.callProfile(profileId);
            }
            else
            {
                coSpaceParamBuilder.callProfile("");
            }

            profileIdObj = busiConferenceNumber.getParams().get("callBrandingProfileId");
            if (profileIdObj != null)
            {
                String profileId = profileIdObj.toString();
                coSpaceParamBuilder.callBrandingProfile(profileId);
            }
            else
            {
                coSpaceParamBuilder.callBrandingProfile("");
            }

            profileIdObj = busiConferenceNumber.getParams().get("dialInSecurityProfileId");
            if (profileIdObj != null)
            {
                String profileId = profileIdObj.toString();
                coSpaceParamBuilder.dialInSecurityProfile(profileId);
            }
            else
            {
                coSpaceParamBuilder.dialInSecurityProfile("");
            }

            profileIdObj = busiConferenceNumber.getParams().get("secondaryUri");
            if (profileIdObj != null)
            {
                String profileId = profileIdObj.toString();
                coSpaceParamBuilder.secondaryUri(profileId);
            }
            else
            {
                coSpaceParamBuilder.secondaryUri("");
            }

            profileIdObj = busiConferenceNumber.getParams().get("passcode");
            if (profileIdObj != null)
            {
                String profileId = profileIdObj.toString();
                coSpaceParamBuilder.passcode(profileId);
            }

            profileIdObj = busiConferenceNumber.getParams().get("defaultLayout");
            if (profileIdObj != null)
            {
                String profileId = profileIdObj.toString();
                coSpaceParamBuilder.defaultLayout(profileId);
            }
            else
            {
                coSpaceParamBuilder.defaultLayout("");
            }

            profileIdObj = busiConferenceNumber.getParams().get("streamUrl");
            if (profileIdObj != null)
            {
                String profileId = profileIdObj.toString();
                coSpaceParamBuilder.streamUrl(profileId);
            }
            else
            {
                coSpaceParamBuilder.streamUrl("");
            }

            // 如果设置了FME的租户，则coSpace需要绑定租户
            if (DeptTenantCache.getInstance().get(deptId) != null)
            {
                coSpaceParamBuilder.tenant(DeptTenantCache.getInstance().get(deptId).getFmeTenantUuid());
            }

            FmeBridge fmeBridge = BridgeUtils.getFmeBridgeByDeptIdAndConferenceNumber(deptId, busiConferenceNumber.getId().toString(), true);
            coSpaceService.updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
            CoSpace coSpace1 =  fmeBridge.getDataCache().getCoSpaceByConferenceNumber(busiConferenceNumber.getId().toString());
            FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    fmeBridge.getDataCache().update(coSpace1);
                    fmeBridge.getFmeLogger().logWebsocketInfo("CoSpace data sync: " + coSpace1, true);
                }
            });
        }

        FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(deptId, new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                coSpaceService.updateCoSpaceCache(fmeBridge, coSpace.getId());
            }
        });
    }
}
