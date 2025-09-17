package com.paradisecloud.smc3.service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.paradiscloud.fcm.business.model.enumer.BusinessFieldType;
import com.paradisecloud.com.fcm.smc.modle.ParticipantRspDto;
import com.paradisecloud.com.fcm.smc.modle.PresetMultiPicReqDto;
import com.paradisecloud.com.fcm.smc.modle.SmcConferenceTemplate;
import com.paradisecloud.com.fcm.smc.modle.response.SmcErrorResponse;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
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
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.smc3.busi.AttendeeCountingStatistics;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.DialMode;
import com.paradisecloud.smc3.model.request.CasecadeTemplateRequest;
import com.paradisecloud.smc3.model.request.TemplateNode;
import com.paradisecloud.smc3.model.request.TemplateNodeTemp;
import com.paradisecloud.smc3.model.response.GetVmrResponse;
import com.paradisecloud.smc3.model.response.SmcCreateTemplateRep;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3TemplateConferenceService;
import com.paradisecloud.smc3.utils.BusiTerminalUtils;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
public class BusiMcuSmc3TemplateConferenceServiceImpl implements IBusiMcuSmc3TemplateConferenceService
{
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper;

    @Resource
    private BusiMcuSmc3TemplateParticipantMapper busiMcuSmc3TemplateParticipantMapper;

    @Resource
    private BusiMcuSmc3TemplateDeptMapper busiMcuSmc3TemplateDeptMapper;

    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;

    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;


    @Resource
    private BusiMcuSmc3TemplateConferenceDefaultViewCellScreenMapper busiMcuSmc3TemplateConferenceDefaultViewCellScreenMapper;

    @Resource
    private BusiMcuSmc3TemplateConferenceDefaultViewDeptMapper busiMcuSmc3TemplateConferenceDefaultViewDeptMapper;

    @Resource
    private BusiMcuSmc3TemplateConferenceDefaultViewPaticipantMapper busiMcuSmc3TemplateConferenceDefaultViewPaticipantMapper;



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
        BusiMcuSmc3TemplateConference tc = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(id);
        tc.setDefaultViewLayout(jsonObj.getString("defaultViewLayout"));
        tc.setDefaultViewIsBroadcast(YesOrNo.convert(jsonObj.getInteger("defaultViewIsBroadcast")).getValue());
        tc.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(jsonObj.getInteger("defaultViewIsDisplaySelf")).getValue());
        tc.setDefaultViewIsFill(YesOrNo.convert(jsonObj.getInteger("defaultViewIsFill")).getValue());
        tc.setPollingInterval(jsonObj.getInteger("pollingInterval"));
        int c = busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(tc);
        if (c == 0)
        {
            throw new SystemException(1003432, "更新模板信息失败");
        }

        // 分频单元格信息保存
        JSONArray ja = jsonObj.getJSONArray("defaultViewCellScreens");

        // 根据模板ID删除分频信息
        busiMcuSmc3TemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuSmc3TemplateConferenceDefaultViewCellScreenById(id);
        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuSmc3TemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuSmc3TemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);
        // 默认视图的参会者信息保存
        Assert.isTrue(jsonObj.containsKey("defaultViewPaticipants"), "defaultViewpaticipants默认视图的参会者信息不能为空");
        ja = jsonObj.getJSONArray("defaultViewPaticipants");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("templateParticipantId"), "templateParticipantId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuSmc3TemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant = new BusiMcuSmc3TemplateConferenceDefaultViewPaticipant();
            busiTemplateConferenceDefaultViewPaticipant.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewPaticipant.setTemplateParticipantId(jo.getLong("templateParticipantId"));
            busiTemplateConferenceDefaultViewPaticipant.setWeight(jo.getInteger("weight"));
            busiTemplateConferenceDefaultViewPaticipant.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));
            busiMcuSmc3TemplateConferenceDefaultViewPaticipantMapper.insertBusiMcuSmc3TemplateConferenceDefaultViewPaticipant(busiTemplateConferenceDefaultViewPaticipant);
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
                BusiMcuSmc3TemplateConferenceDefaultViewCellScreen busiTemplateConferenceDefaultViewCellScreen = new BusiMcuSmc3TemplateConferenceDefaultViewCellScreen();
                busiTemplateConferenceDefaultViewCellScreen.setTemplateConferenceId(id);
                busiTemplateConferenceDefaultViewCellScreen.setCellSequenceNumber(jo.getInteger("cellSequenceNumber"));

                CellScreenAttendeeOperation cellScreenAttendeeOperation = CellScreenAttendeeOperation.convert(jo.getInteger("operation"));
                busiTemplateConferenceDefaultViewCellScreen.setOperation(cellScreenAttendeeOperation.getValue());
                busiTemplateConferenceDefaultViewCellScreen.setIsFixed(YesOrNo.convert(jo.getInteger("isFixed")).getValue());

                // 如果是轮询，校验参会者数量
                if (cellScreenAttendeeOperation == CellScreenAttendeeOperation.ROUND)
                {
                    BusiMcuSmc3TemplateConferenceDefaultViewPaticipant con = new BusiMcuSmc3TemplateConferenceDefaultViewPaticipant();
                    List<BusiMcuSmc3TemplateConferenceDefaultViewPaticipant> ps = busiMcuSmc3TemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuSmc3TemplateConferenceDefaultViewPaticipantList(con);
                    Assert.isTrue(ps.size() > 1, "分屏单元格【"+busiTemplateConferenceDefaultViewCellScreen.getCellSequenceNumber()+"】是轮询操作，参会终端不能少于2个！");
                }

                busiMcuSmc3TemplateConferenceDefaultViewCellScreenMapper.insertBusiMcuSmc3TemplateConferenceDefaultViewCellScreen(busiTemplateConferenceDefaultViewCellScreen);
            }
        }

        Assert.isTrue(jsonObj.containsKey("defaultViewDepts"), "defaultViewDepts默认视图的部门信息不能为空");


        // 根据模板ID删除默认视图的部门信息
        busiMcuSmc3TemplateConferenceDefaultViewDeptMapper.deleteBusiMcuSmc3TemplateConferenceDefaultViewDeptByTemplateConferenceId(id);

        // 默认视图的部门信息保存
        ja = jsonObj.getJSONArray("defaultViewDepts");
        for (int i = 0; i < ja.size(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            Assert.isTrue(jo.containsKey("deptId"), "deptId部门信息不能为空");
            Assert.isTrue(jo.containsKey("weight"), "weight部门权重不能为空");
            BusiMcuSmc3TemplateConferenceDefaultViewDept busiTemplateConferenceDefaultViewDept = new BusiMcuSmc3TemplateConferenceDefaultViewDept();
            busiTemplateConferenceDefaultViewDept.setTemplateConferenceId(id);
            busiTemplateConferenceDefaultViewDept.setDeptId(jo.getLong("deptId"));
            busiTemplateConferenceDefaultViewDept.setWeight(jo.getInteger("weight"));
            busiMcuSmc3TemplateConferenceDefaultViewDeptMapper.insertBusiMcuSmc3TemplateConferenceDefaultViewDept(busiTemplateConferenceDefaultViewDept);
        }


    }


    public String selectBusiMcuSmc3TemplateConferenceCoverById(Long id)
    {
        BusiMcuSmc3TemplateConference tc = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(id);
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
        BusiMcuSmc3TemplateConference tc = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(id);
        if (tc == null)
        {
            return null;
        }
        return getTemplateConferenceDetails(tc);
    }

    @Override
    public ModelBean getTemplateConferenceDetails(BusiMcuSmc3TemplateConference tc)
    {
        BusiMcuSmc3TemplateParticipant busiTemplateParticipant = new BusiMcuSmc3TemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(tc.getId());
        List<ModelBean> pMbs = new ArrayList<>();
        List<BusiMcuSmc3TemplateParticipant> ps = busiMcuSmc3TemplateParticipantMapper.selectBusiMcuSmc3TemplateParticipantList(busiTemplateParticipant);

        int onlineCount = 0;
        int weight = 0;
        String mainName = "";
        Map<Long, ModelBean> bpm = new HashMap<>();
        for (BusiMcuSmc3TemplateParticipant busiTemplateParticipant2 : ps)
        {
            ModelBean pmb = new ModelBean(busiTemplateParticipant2);
            BusiTerminal bt = TerminalCache.getInstance().get(busiTemplateParticipant2.getTerminalId());
            if (bt == null) {
                continue;
            }
            if (TerminalType.isFSBC(bt.getType())) {
                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getById(bt.getFsbcServerId());
                if (fsbcBridge != null) {
                    BusiFsbcRegistrationServer busiFsbcRegistrationServer = fsbcBridge.getBusiFsbcRegistrationServer();
                    if (busiFsbcRegistrationServer != null) {
                        pmb.put("uri", bt.getCredential() + "@" + busiFsbcRegistrationServer.getCallIp());
                    }

                }
            } else if (TerminalType.isFCMSIP(bt.getType())) {
                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getById(bt.getFsServerId());
                if (fcmBridge != null) {
                    BusiFreeSwitch busiFreeSwitch = fcmBridge.getBusiFreeSwitch();
                    if (busiFreeSwitch != null) {
                        pmb.put("uri", bt.getCredential() + "@" + busiFreeSwitch.getIp());
                    }
                }
            } else if (TerminalType.isZJ(bt.getType())) {

            } else if (TerminalType.isCisco(bt.getType())) {
                if (bt.getNumber() == null) {
                    pmb.put("uri", bt.getIp());
                } else {
                    pmb.put("uri", bt.getNumber() + "@" + bt.getIp());
                }
            } else if (TerminalType.isSMCNUMBER(bt.getType())) {
                pmb.put("uri", bt.getNumber());
            } else if (TerminalType.isWindows(bt.getType())) {
                pmb.put("uri", bt.getIp());
            } else if (TerminalType.isSMCSIP(bt.getType())) {
                pmb.put("uri", bt.getNumber());
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

        BusiMcuSmc3TemplateConferenceDefaultViewCellScreen con = new BusiMcuSmc3TemplateConferenceDefaultViewCellScreen();
        con.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc3TemplateConferenceDefaultViewCellScreen> defaultViewCellScreens = busiMcuSmc3TemplateConferenceDefaultViewCellScreenMapper.selectBusiMcuSmc3TemplateConferenceDefaultViewCellScreenList(con);

        BusiMcuSmc3TemplateConferenceDefaultViewDept con1 = new BusiMcuSmc3TemplateConferenceDefaultViewDept();
        con1.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc3TemplateConferenceDefaultViewDept> defaultViewDepts = busiMcuSmc3TemplateConferenceDefaultViewDeptMapper.selectBusiMcuSmc3TemplateConferenceDefaultViewDeptList(con1);

        BusiMcuSmc3TemplateConferenceDefaultViewPaticipant con2 = new BusiMcuSmc3TemplateConferenceDefaultViewPaticipant();
        con2.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc3TemplateConferenceDefaultViewPaticipant> defaultViewPaticipants = busiMcuSmc3TemplateConferenceDefaultViewPaticipantMapper.selectBusiMcuSmc3TemplateConferenceDefaultViewPaticipantList(con2);
        List<ModelBean> defaultViewPaticipantMbs = new ArrayList<>();
        for (BusiMcuSmc3TemplateConferenceDefaultViewPaticipant busiTemplateConferenceDefaultViewPaticipant : defaultViewPaticipants) {
            ModelBean mb = bpm.get(busiTemplateConferenceDefaultViewPaticipant.getTemplateParticipantId());
            if (!Objects.isNull(mb)) {
                mb.put("weight", busiTemplateConferenceDefaultViewPaticipant.getWeight());
                mb.put("cellSequenceNumber", busiTemplateConferenceDefaultViewPaticipant.getCellSequenceNumber());
                defaultViewPaticipantMbs.add(mb);
            }
        }

        BusiMcuSmc3TemplateDept tdCon = new BusiMcuSmc3TemplateDept();
        tdCon.setTemplateConferenceId(tc.getId());
        List<BusiMcuSmc3TemplateDept> tds = busiMcuSmc3TemplateDeptMapper.selectBusiMcuSmc3TemplateDeptList(tdCon);

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
        if (tc.getConferenceNumber() != null && Smc3ConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.SMC3)))
        {
            AttendeeCountingStatistics attendeeCountingStatistics = Smc3ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(tc.getId(), McuType.SMC3)).getAttendeeCountingStatistics();
            mb.put("meetingJoinedCount", attendeeCountingStatistics.getMeetingJoinedCount());
            mb.put("onlineCount", attendeeCountingStatistics.getOnlineCount());
        }
        mb.put("mainVenue", mainName);
        mb.put("isStart",  Smc3ConferenceContextCache.getInstance().containsKey(EncryptIdUtil.generateKey(tc.getId(), McuType.SMC3)));
        mb.put("smcTemplateId",  tc.getSmcTemplateId());
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
        mb.put("mcuType", McuType.SMC3.getCode());
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
    public List<BusiMcuSmc3TemplateConference> selectBusiTemplateConferenceList(BusiMcuSmc3TemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    @Override
    public Page<BusiMcuSmc3TemplateConference> selectBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                deptId=loginUser.getUser().getDeptId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Page<BusiMcuSmc3TemplateConference> tcs = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceListByKey(searchKey,deptId);
        return tcs;
    }

    @Override
    public List<BusiMcuSmc3TemplateConference> selectAllBusiTemplateConferenceList(String searchKey, Long deptId) {
        if(deptId==null){
            LoginUser loginUser = SecurityUtils.getLoginUser();
            deptId=loginUser.getUser().getDeptId();
        }
        return  busiMcuSmc3TemplateConferenceMapper.selectAllBusiMcuSmc3TemplateConferenceListByKey(searchKey,deptId);
    }

    /**
     * 查询会议模板列表
     *
     * @param busiTemplateConference 会议模板
     * @return 会议模板
     */
    @Override
    public List<BusiMcuSmc3TemplateConference> selectAllBusiTemplateConferenceList(BusiMcuSmc3TemplateConference busiTemplateConference)
    {
        Assert.notNull(busiTemplateConference.getBusinessFieldType(), "会议模板业务领域类型businessFieldType不能为空！");
        return selectAllBusiTemplateConferenceListWithoutBusinessFieldType(busiTemplateConference);
    }

    @Override
    public List<BusiMcuSmc3TemplateConference> selectBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuSmc3TemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuSmc3TemplateConference> tcs = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(busiTemplateConference);
        return tcs;
    }

    @Override
    public List<BusiMcuSmc3TemplateConference> selectAllBusiTemplateConferenceListWithoutBusinessFieldType(BusiMcuSmc3TemplateConference busiTemplateConference)
    {
        // 绑定终端归属部门
        if (busiTemplateConference.getDeptId() == null)
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            busiTemplateConference.setDeptId(loginUser.getUser().getDeptId());
        }

        List<BusiMcuSmc3TemplateConference> tcs = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(busiTemplateConference);
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
    public List<ModelBean> toModelBean(List<BusiMcuSmc3TemplateConference> tcs)
    {
        List<ModelBean> mbs = new ArrayList<>();
        for (BusiMcuSmc3TemplateConference tc : tcs)
        {
            mbs.add(getTemplateConferenceDetails(tc));
        }

        return mbs;
    }

    @Override
    public List<DeptRecordCount> getDeptTemplateCount(Integer businessFieldType)
    {
        BusinessFieldType.convert(businessFieldType);
        return busiMcuSmc3TemplateConferenceMapper.getDeptTemplateCount(businessFieldType);
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
    public int insertBusiTemplateConference(BusiMcuSmc3TemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants, List<BusiMcuSmc3TemplateDept> templateDepts)
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
        {// 设置默认值
            busiTemplateConference.setRecordingEnabled(2);
            busiTemplateConference.setStreamingEnabled(2);
            if (busiTemplateConference.getBandwidth() == null) {
                busiTemplateConference.setBandwidth(1920);
            }
            if (busiTemplateConference.getIsAutoMonitor() == null) {
                busiTemplateConference.setIsAutoMonitor(2);
            }
            if (busiTemplateConference.getType() == null) {
                busiTemplateConference.setType(2);
            }
            if (StringUtils.isEmpty(busiTemplateConference.getDefaultViewLayout())) {
                busiTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
            }
            if (busiTemplateConference.getDefaultViewIsBroadcast() == null) {
                busiTemplateConference.setDefaultViewIsBroadcast(2);
            }
            if (busiTemplateConference.getDefaultViewIsDisplaySelf() == null) {
                busiTemplateConference.setDefaultViewIsDisplaySelf(-1);
            }
            if (busiTemplateConference.getDefaultViewIsFill() == null) {
                busiTemplateConference.setDefaultViewIsFill(1);
            }
            if (busiTemplateConference.getPollingInterval() == null) {
                busiTemplateConference.setPollingInterval(10);
            }
            if (StringUtils.isEmpty(busiTemplateConference.getVideoProtocol())) {
                busiTemplateConference.setVideoProtocol("H264_BP");
            }
            if (StringUtils.isEmpty(busiTemplateConference.getVideoResolution())) {
                busiTemplateConference.setVideoResolution("MPI_1080P");
            }
            Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
            if (businessProperties == null) {
                businessProperties = new HashMap<>();
                busiTemplateConference.setBusinessProperties(businessProperties);
            }
            businessProperties.put("videoProtocol", busiTemplateConference.getVideoProtocol());
            businessProperties.put("videoResolution", busiTemplateConference.getVideoResolution());
            businessProperties.put("audioProtocol", "AAC_LD_S");
            if (busiTemplateConference.getDurationTime() == null) {
                busiTemplateConference.setDurationTime(Integer.MAX_VALUE);
            }
        }

        // 校验会议类型
        // ConferenceType.convert(busiTemplateConference.getType());

        // 新直播方式：事先配置的直播地址时模板会议自动开启直播


        int c = 0;
        try {
            c = busiMcuSmc3TemplateConferenceMapper.insertBusiMcuSmc3TemplateConference(busiTemplateConference);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.isTrue(false, "添加会议模板失败！请刷新页面后重试！");
        }
        if (c > 0)
        {
            List<ParticipantRspDto> templateParticipants = new ArrayList<>();
            if (!ObjectUtils.isEmpty(busiTemplateParticipants))
            {
                // 添加模板与会者顺序信息
                for (BusiMcuSmc3TemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
                {
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiTemplateParticipant.getTerminalId());
                    if(busiTerminal!=null){
                        ParticipantRspDto participantRspDto = new ParticipantRspDto();
                        String number = busiTerminal.getNumber();
                        participantRspDto.setName(busiTerminal.getName());
                        participantRspDto.setUri(number);
                        participantRspDto.setIpProtocolType(2);
                        participantRspDto.setDialMode("OUT");
                        participantRspDto.setVoice(false);
                        participantRspDto.setRate(0);
                        participantRspDto.setMainParticipant(Objects.equals(busiTerminal.getId(), masterTerminalId));
                        participantRspDto.setUri(BusiTerminalUtils.getUri(busiTerminal));
                        if (TerminalType.isFmeTemplate(busiTerminal.getType())) {
                            BusiTemplateConferenceMapper templateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
                            BusiTemplateConference templateConference = new BusiTemplateConference();
                            templateConference.setConferenceNumber(Long.valueOf(busiTerminal.getNumber()));
                            List<BusiTemplateConference> templateConferences = templateConferenceMapper.selectBusiTemplateConferenceList(templateConference);
                            if (!CollectionUtils.isEmpty(templateConferences)) {
                                String conferencePassword = templateConferences.get(0).getConferencePassword();
                                if (io.jsonwebtoken.lang.Strings.hasText(conferencePassword)) {
                                    participantRspDto.setDtmfInfo(conferencePassword);
                                }
                            }
                        }
                        if(TerminalType.isSMCSIP(busiTerminal.getType())){
                            if(busiTemplateParticipant.getBusinessProperties()!=null){
                                Map<String, Object> businessProperties = busiTemplateParticipant.getBusinessProperties();
                                if(businessProperties.get("audioProtocol")!=null){
                                    participantRspDto.setAudioProtocol((Integer) businessProperties.get("audioProtocol"));
                                }
                                if(businessProperties.get("videoProtocol")!=null){
                                    participantRspDto.setVideoProtocol((Integer) businessProperties.get("videoProtocol"));
                                }
                                if(businessProperties.get("videoResolution")!=null){
                                    participantRspDto.setVideoResolution((Integer) businessProperties.get("videoResolution"));
                                }
                                if(businessProperties.get("rate")!=null){
                                    participantRspDto.setRate((Integer) businessProperties.get("rate"));
                                }
                                if(businessProperties.get("dtmfInfo")!=null){
                                    participantRspDto.setDtmfInfo((String) businessProperties.get("dtmfInfo"));
                                }
                                if(businessProperties.get("serviceZoneId")!=null){
                                    participantRspDto.setServiceZoneId((String) businessProperties.get("serviceZoneId"));
                                }

                            }

                        }

                        if (TerminalType.isMcuTemplateCisco(busiTerminal.getType())) {
                            participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
                        }

                        templateParticipants.add(participantRspDto);
                    }

                    try
                    {
                        busiMcuSmc3TemplateParticipantMapper.insertBusiMcuSmc3TemplateParticipant(busiTemplateParticipant);
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
                        busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConference);
                    }
                }

            }

            if (!ObjectUtils.isEmpty(templateDepts))
            {
                // 添加模板部门顺序信息
                for (BusiMcuSmc3TemplateDept busiTemplateDept : templateDepts)
                {
                    busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuSmc3TemplateDeptMapper.insertBusiMcuSmc3TemplateDept(busiTemplateDept);
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
            if (true) {// 多级会议关联子模板
                if (Strings.isNotBlank(busiTemplateConference.getCascadeNodesTemp()) && !Objects.equals("null", busiTemplateConference.getCascadeNodesTemp())) {
                    List<TemplateNodeTemp> templateNodesTemp = JSONArray.parseArray(busiTemplateConference.getCascadeNodesTemp(), TemplateNodeTemp.class);
                    for (TemplateNodeTemp templateNodeTemp : templateNodesTemp) {
                        BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                        busiMcuSmc3TemplateConference.setCascadeId(busiTemplateConference.getId().toString());
                        busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                    }
                }
            }
            if (false) {// 创建和更新模板时候不在SMC上创建模板，在开始会议时创建SMC模板
                Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiTemplateConference.getDeptId());
                SmcConferenceTemplate smcConferenceTemplate = getSmcConferenceTemplate(busiTemplateConference);
                smcConferenceTemplate.setTemplateParticipants(templateParticipants);
                //多级会议模板：
                if (Strings.isNotBlank(busiTemplateConference.getCascadeNodes()) && !Objects.equals("null", busiTemplateConference.getCascadeNodes())) {
                    List<TemplateNode> templateNodes = JSONArray.parseArray(busiTemplateConference.getCascadeNodes(), TemplateNode.class);
                    CasecadeTemplateRequest casecadeTemplateRequest = new CasecadeTemplateRequest();

                    SmcConferenceTemplate.ConferencePolicySettingDTO policySettingDTO = new SmcConferenceTemplate.ConferencePolicySettingDTO();
                    policySettingDTO.setAutoEnd(true);
                    policySettingDTO.setAutoExtend(true);
                    policySettingDTO.setAutoMute(busiTemplateConference.getMuteType() == 1 ? true : false);
                    policySettingDTO.setChairmanPassword(busiTemplateConference.getChairmanPassword());
                    policySettingDTO.setGuestPassword(busiTemplateConference.getGuestPassword());
                    policySettingDTO.setLanguage(1);
                    policySettingDTO.setVoiceActive(false);
                    policySettingDTO.setMaxParticipantNum(busiTemplateConference.getMaxParticipantNum());

                    casecadeTemplateRequest.setSubject(busiTemplateConference.getName());
                    casecadeTemplateRequest.setTemplateNodes(templateNodes);
                    casecadeTemplateRequest.setShowSecurityLevel(false);
                    casecadeTemplateRequest.setConferencePolicySetting(policySettingDTO);
                    casecadeTemplateRequest.setChairmanPassword(busiTemplateConference.getChairmanPassword());
                    casecadeTemplateRequest.setDuration(Integer.MAX_VALUE);
                    casecadeTemplateRequest.setGuestPassword(busiTemplateConference.getGuestPassword());

                    String userInfo = smc3Bridge.getSmcUserInvoker().getUserInfo(smc3Bridge.getSmcportalTokenInvoker().getUserName(), smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                    UserInfoRep userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
                    String id = userInfoRep.getAccount().getOrganization().getId();
                    casecadeTemplateRequest.setOrganizationId(id);

                    String s = smc3Bridge.getSmcConferencesTemplateInvoker().creatConferencesCascadeTemplate(JSON.toJSONString(casecadeTemplateRequest), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    try {
                        CasecadeTemplateRequest cascadeTemplateResponse = JSONObject.parseObject(s, CasecadeTemplateRequest.class);
                        if (cascadeTemplateResponse != null && io.jsonwebtoken.lang.Strings.hasText(cascadeTemplateResponse.getId())) {
                            busiTemplateConference.setCascadeId(cascadeTemplateResponse.getId());
                            busiTemplateConference.setCategory(ConstAPI.CASCADE);
                            busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConference);

                            for (TemplateNode templateNode : templateNodes) {
                                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(templateNode.getTemplateId());
                                busiMcuSmc3TemplateConference.setCascadeId(cascadeTemplateResponse.getId());
                                busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                            }
                        } else {
                            log.error(s);
                            throw new CustomException("多级会议模板创建错误");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new CustomException("多级会议模板创建错误" + e.getMessage());
                    }
                } else {
                    //同步在SMC上创建模板
                    String result = smc3Bridge.getSmcConferencesTemplateInvoker().creatConferencesTemplate(JSONObject.toJSONString(smcConferenceTemplate), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    if (result != null && result.contains(ConstAPI.ERRORNO)) {
                        SmcErrorResponse smcErrorResponse = JSON.parseObject(result, SmcErrorResponse.class);

                        throw new CustomException("新增模板失败:" + smcErrorResponse.getErrorDesc());
                    }
                    SmcConferenceTemplate smcConferenceTemplate1 = JSON.parseObject(result, SmcConferenceTemplate.class);
                    if (Objects.isNull(smcConferenceTemplate1)) {
                        throw new CustomException("创建模板会议失败");
                    }
                    String template1Id = smcConferenceTemplate1.getId();
                    busiTemplateConference.setSmcTemplateId(template1Id);
                    busiTemplateConference.setCategory(ConstAPI.NORMAL);
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConference);
                }
            }

        }
        return c;
    }

    private void updatePassword(BusiMcuSmc3TemplateConference busiTemplateConference) {

    }

    private SmcConferenceTemplate getSmcConferenceTemplate(BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference) {
        SmcConferenceTemplate smcConferenceTemplate = buildTemplateConference();

        Map<String, Object> businessProperties = busiMcuSmc3TemplateConference.getBusinessProperties();
        if(businessProperties!=null){
            smcConferenceTemplate.setMainMcuId((String) businessProperties.get("mainMcuId"));
            smcConferenceTemplate.setMainMcuName((String) businessProperties.get("mainMcuName"));
            smcConferenceTemplate.setMainServiceZoneId((String) businessProperties.get("mainServiceZoneId"));

            Object streamService = businessProperties.get("streamService");
            if(streamService!=null){
                //streamService 转换
                SmcConferenceTemplate.StreamServiceDTO streamServiceDTO =  JSONObject.parseObject(JSONObject.toJSONString(streamService),  SmcConferenceTemplate.StreamServiceDTO.class);
                smcConferenceTemplate.setStreamService(streamServiceDTO);
            }else {
                SmcConferenceTemplate.StreamServiceDTO streamServiceDTO = new SmcConferenceTemplate.StreamServiceDTO();
                smcConferenceTemplate.setStreamService(streamServiceDTO);
            }
        }
        smcConferenceTemplate.setGuestPassword(busiMcuSmc3TemplateConference.getGuestPassword());
        smcConferenceTemplate.setChairmanPassword(busiMcuSmc3TemplateConference.getChairmanPassword());
        SmcConferenceTemplate.ConferenceCapabilitySettingDTO conferenceCapabilitySetting = smcConferenceTemplate.getConferenceCapabilitySetting();
//        conferenceCapabilitySetting.setSvcVideoResolution(busiMcuSmc3TemplateConference.getVideoResolution());
        conferenceCapabilitySetting.setVideoResolution(busiMcuSmc3TemplateConference.getVideoResolution());
        conferenceCapabilitySetting.setRate(busiMcuSmc3TemplateConference.getBandwidth());
        String videoResolution = busiMcuSmc3TemplateConference.getVideoResolution();
        if (StringUtils.isNotBlank(videoResolution)) {
            conferenceCapabilitySetting.setVideoResolution(videoResolution);
//            conferenceCapabilitySetting.setSvcVideoResolution(videoResolution);
            conferenceCapabilitySetting.setVideoProtocol(busiMcuSmc3TemplateConference.getVideoProtocol());
        }
        conferenceCapabilitySetting.setEnableRecord(smcConferenceTemplate.getStreamService().getSupportRecord());
        conferenceCapabilitySetting.setEnableLiveBroadcast(smcConferenceTemplate.getStreamService().getSupportLive());
        conferenceCapabilitySetting.setEnableDataConf(false);
        smcConferenceTemplate.setConferenceCapabilitySetting(conferenceCapabilitySetting);

//        SmcConferenceTemplate.StreamServiceDTO streamService = smcConferenceTemplate.getStreamService();
//        streamService.setSupportRecord(busiMcuSmc3TemplateConference.getRecordingEnabled()==null?false:busiMcuSmc3TemplateConference.getRecordingEnabled()==1?true:false);
//        streamService.setAmcRecord(false);
//        streamService.setSupportLive(busiMcuSmc3TemplateConference.getStreamingEnabled()==null?false:busiMcuSmc3TemplateConference.getStreamingEnabled()==1?true:false);
//        smcConferenceTemplate.setStreamService(streamService);

        smcConferenceTemplate.setSubject(busiMcuSmc3TemplateConference.getName());
        smcConferenceTemplate.setVmrNumber("");
        if (busiMcuSmc3TemplateConference.getConferenceNumber() != null) {
            Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiMcuSmc3TemplateConference.getDeptId());
            BusiConferenceNumber busiConferenceNumber = busiConferenceNumberMapper.selectBusiConferenceNumberById(busiMcuSmc3TemplateConference.getConferenceNumber());
            if (busiConferenceNumber.getCreateType() == ConferenceNumberCreateType.MANUAL.getValue()) {
                smcConferenceTemplate.setVmrNumber(smc3Bridge.getTenantId() + busiMcuSmc3TemplateConference.getConferenceNumber().toString());
                String responseStr = smc3Bridge.getSmcportalTokenInvoker().getVmr(busiMcuSmc3TemplateConference.getConferenceNumber().toString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                GetVmrResponse getVmrResponse = JSON.parseObject(responseStr, GetVmrResponse.class);
                if (StringUtils.isNotEmpty(getVmrResponse.getId())) {
                    String chairmanPassword = "";
                    if (busiMcuSmc3TemplateConference.getChairmanPassword() != null) {
                        chairmanPassword = busiMcuSmc3TemplateConference.getChairmanPassword();
                    }
                    String guestPassword = "";
                    if (busiMcuSmc3TemplateConference.getGuestPassword() != null) {
                        guestPassword = busiMcuSmc3TemplateConference.getGuestPassword();
                    }
                    String response = smc3Bridge.getSmcportalTokenInvoker().changeVmrPwd(getVmrResponse.getId(), chairmanPassword, guestPassword, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
            }
        }
        smcConferenceTemplate.setDuration(Integer.MAX_VALUE);
        SmcConferenceTemplate.ConferencePolicySettingDTO conferencePolicySetting = smcConferenceTemplate.getConferencePolicySetting();
        conferencePolicySetting.setVoiceActive(false);
        conferencePolicySetting.setAutoMute(busiMcuSmc3TemplateConference.getMuteType() == 1);
        conferencePolicySetting.setMaxParticipantNum(busiMcuSmc3TemplateConference.getMaxParticipantNum());
        Map<String, Object> confPresetParam = busiMcuSmc3TemplateConference.getConfPresetParam();
        if(confPresetParam!=null){
            Object presetMultiPics = confPresetParam.get("presetMultiPics");

            List<PresetMultiPicReqDto> presetMultiPicReqDtos = JSONArray.parseArray(JSONObject.toJSONString(presetMultiPics), PresetMultiPicReqDto.class);
            SmcConferenceTemplate.ConfPresetParamDTO confPresetParam_d=new SmcConferenceTemplate.ConfPresetParamDTO();
            confPresetParam_d.setPresetMultiPics(presetMultiPicReqDtos);
            smcConferenceTemplate.setConfPresetParam(confPresetParam_d);
        }

        return smcConferenceTemplate;
    }

    public SmcConferenceTemplate buildTemplateConference() {

        SmcConferenceTemplate smcConferenceTemplateRquest = new SmcConferenceTemplate();
        smcConferenceTemplateRquest.setGuestPassword(null);
        smcConferenceTemplateRquest.setChairmanPassword(null);
        smcConferenceTemplateRquest.setMainMcuId("");
        smcConferenceTemplateRquest.setMainMcuName("");
        smcConferenceTemplateRquest.setMainServiceZoneId("");
        smcConferenceTemplateRquest.setTemplateType("COMMON_CONF");
        smcConferenceTemplateRquest.setVmrNumber("");
        SmcConferenceTemplate.ConferenceCapabilitySettingDTO conferenceCapabilitySetting = new SmcConferenceTemplate.ConferenceCapabilitySettingDTO();
        conferenceCapabilitySetting.setAmcRecord(false);
        conferenceCapabilitySetting.setAudioProtocol("AAC_LD_S");
        conferenceCapabilitySetting.setAudioRecord(false);
        conferenceCapabilitySetting.setAutoRecord(false);
        conferenceCapabilitySetting.setCheckInDuration(10);
        conferenceCapabilitySetting.setDataConfProtocol("DATA_RESOLUTION_STANDARD");
        conferenceCapabilitySetting.setEnableCheckIn(false);
        conferenceCapabilitySetting.setEnableDataConf(false);
        conferenceCapabilitySetting.setEnableFec(false);
        conferenceCapabilitySetting.setEnableLiveBroadcast(false);
        conferenceCapabilitySetting.setEnableRecord(false);
        conferenceCapabilitySetting.setMediaEncrypt("NOT_ENCRYPT_MODE");//AUTO_ENCRYPT_MODE NOT_ENCRYPT_MODE
        conferenceCapabilitySetting.setReserveResource(0);
        conferenceCapabilitySetting.setSvcRate(3840);
        conferenceCapabilitySetting.setSvcVideoProtocol("H265");
        conferenceCapabilitySetting.setSvcVideoResolution(ConstAPI.SVC_VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setRate(1920);
        conferenceCapabilitySetting.setVideoProtocol("H264_BP");//H264_HP
        conferenceCapabilitySetting.setVideoResolution(ConstAPI.VIDEO_RESOLUTION);
        conferenceCapabilitySetting.setType("AVC");
        smcConferenceTemplateRquest.setConferenceCapabilitySetting(conferenceCapabilitySetting);
        SmcConferenceTemplate.ConferencePolicySettingDTO policySettingDTO = new SmcConferenceTemplate.ConferencePolicySettingDTO();
        policySettingDTO.setAutoEnd(true);
        policySettingDTO.setAutoExtend(true);
        policySettingDTO.setAutoMute(false);
        policySettingDTO.setChairmanPassword("");
        policySettingDTO.setGuestPassword("");
        policySettingDTO.setLanguage(1);
        policySettingDTO.setVoiceActive(false);

        smcConferenceTemplateRquest.setConferencePolicySetting(policySettingDTO);

//        SmcConferenceTemplate.StreamServiceDTO streamServiceDTO = new SmcConferenceTemplate.StreamServiceDTO();
//        streamServiceDTO.setSupportMinutes(false);
//        smcConferenceTemplateRquest.setStreamService(streamServiceDTO);

        SmcConferenceTemplate.SubtitleServiceDTO subtitleServiceDTO = new SmcConferenceTemplate.SubtitleServiceDTO();
        subtitleServiceDTO.setEnableSubtitle(false);
        subtitleServiceDTO.setSrcLang("CHINESE");
        smcConferenceTemplateRquest.setSubtitleService(subtitleServiceDTO);

        return smcConferenceTemplateRquest;

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
    public int updateBusiTemplateConference(BusiMcuSmc3TemplateConference busiTemplateConference, Long masterTerminalId, List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants, List<BusiMcuSmc3TemplateDept> templateDepts)
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
        {// 设置默认值
            busiTemplateConference.setRecordingEnabled(2);
            busiTemplateConference.setStreamingEnabled(2);
            if (busiTemplateConference.getBandwidth() == null) {
                busiTemplateConference.setBandwidth(1920);
            }
            if (busiTemplateConference.getIsAutoMonitor() == null) {
                busiTemplateConference.setIsAutoMonitor(2);
            }
            if (busiTemplateConference.getType() == null) {
                busiTemplateConference.setType(2);
            }
            if (StringUtils.isEmpty(busiTemplateConference.getDefaultViewLayout())) {
                busiTemplateConference.setDefaultViewLayout(AutomaticSplitScreen.LAYOUT);
            }
            if (busiTemplateConference.getDefaultViewIsBroadcast() == null) {
                busiTemplateConference.setDefaultViewIsBroadcast(2);
            }
            if (busiTemplateConference.getDefaultViewIsDisplaySelf() == null) {
                busiTemplateConference.setDefaultViewIsDisplaySelf(-1);
            }
            if (busiTemplateConference.getDefaultViewIsFill() == null) {
                busiTemplateConference.setDefaultViewIsFill(1);
            }
            if (busiTemplateConference.getPollingInterval() == null) {
                busiTemplateConference.setPollingInterval(10);
            }
            if (StringUtils.isEmpty(busiTemplateConference.getVideoProtocol())) {
                busiTemplateConference.setVideoProtocol("H264_BP");
            }
            if (StringUtils.isEmpty(busiTemplateConference.getVideoResolution())) {
                busiTemplateConference.setVideoResolution("MPI_1080P");
            }
            Map<String, Object> businessProperties = busiTemplateConference.getBusinessProperties();
            if (businessProperties == null) {
                businessProperties = new HashMap<>();
                busiTemplateConference.setBusinessProperties(businessProperties);
            }
            businessProperties.put("videoProtocol", busiTemplateConference.getVideoProtocol());
            businessProperties.put("videoResolution", busiTemplateConference.getVideoResolution());
            businessProperties.put("audioProtocol", "AAC_LD_S");
            if (busiTemplateConference.getDurationTime() == null) {
                busiTemplateConference.setDurationTime(Integer.MAX_VALUE);
            }
        }
        BusiMcuSmc3TemplateConference old = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(busiTemplateConference.getId());
        int c = busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConference);
        if (c > 0)
        {
            List<ParticipantRspDto> templateParticipants = new ArrayList<>();
            doParticipantUpdate(busiTemplateConference, busiTemplateParticipants, masterTerminalId,templateParticipants);
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
            if (true) {// 多级会议关联子模板
                if (Strings.isNotBlank(old.getCascadeNodesTemp()) && !Objects.equals("null", old.getCascadeNodesTemp())) {
                    List<TemplateNodeTemp> templateNodesTemp = JSONArray.parseArray(old.getCascadeNodesTemp(), TemplateNodeTemp.class);
                    for (TemplateNodeTemp templateNodeTemp : templateNodesTemp) {
                        BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                        busiMcuSmc3TemplateConference.setCascadeId("");
                        busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                    }
                }
                if (Strings.isNotBlank(busiTemplateConference.getCascadeNodesTemp()) && !Objects.equals("null", busiTemplateConference.getCascadeNodesTemp())) {
                    List<TemplateNodeTemp> templateNodesTemp = JSONArray.parseArray(busiTemplateConference.getCascadeNodesTemp(), TemplateNodeTemp.class);
                    for (TemplateNodeTemp templateNodeTemp : templateNodesTemp) {
                        BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                        busiMcuSmc3TemplateConference.setCascadeId(busiTemplateConference.getId().toString());
                        busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                    }
                }
            }
            if (false) {// 创建和更新模板时候不在SMC上创建模板，在开始会议时创建SMC模板
//            if (old.getConferenceNumber() != null && !old.getConferenceNumber().equals(busiTemplateConference.getConferenceNumber()))
//            {
//                BusiMcuSmc3TemplateConference con = new BusiMcuSmc3TemplateConference();
//                con.setConferenceNumber(old.getConferenceNumber());
//                List<BusiMcuSmc3TemplateConference> bcs = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(con);
//                if (ObjectUtils.isEmpty(bcs))
//                {
//                    BusiConferenceNumber cn = new BusiConferenceNumber();
//                    cn.setId(old.getConferenceNumber());
//                    cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
//                    busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
//                }
//            }
                Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiTemplateConference.getDeptId());
                SmcConferenceTemplate smcConferenceTemplate = getSmcConferenceTemplate(busiTemplateConference);
                smcConferenceTemplate.setTemplateParticipants(templateParticipants);

                String smcTemplateId = busiTemplateConference.getSmcTemplateId();
                if (Strings.isNotBlank(smcTemplateId)) {
                    //同步在SMC上更新模板
                    String result = smc3Bridge.getSmcConferencesTemplateInvoker().putConferencesTemplate(smcTemplateId, JSONObject.toJSONString(smcConferenceTemplate), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }

                if (Objects.equals("CASCADE", busiTemplateConference.getCategory())) {

                    //多级会议模板：
                    if (Strings.isNotBlank(busiTemplateConference.getCascadeNodes())) {
                        List<TemplateNode> templateNodes = JSONArray.parseArray(busiTemplateConference.getCascadeNodes(), TemplateNode.class);
                        CasecadeTemplateRequest casecadeTemplateRequest = new CasecadeTemplateRequest();

                        SmcConferenceTemplate.ConferencePolicySettingDTO policySettingDTO = new SmcConferenceTemplate.ConferencePolicySettingDTO();
                        policySettingDTO.setAutoEnd(true);
                        policySettingDTO.setAutoExtend(true);
                        policySettingDTO.setAutoMute(busiTemplateConference.getMuteType() == 1 ? true : false);
                        policySettingDTO.setChairmanPassword(busiTemplateConference.getChairmanPassword());
                        policySettingDTO.setGuestPassword(busiTemplateConference.getGuestPassword());
                        policySettingDTO.setLanguage(1);
                        policySettingDTO.setVoiceActive(false);
                        policySettingDTO.setMaxParticipantNum(busiTemplateConference.getMaxParticipantNum());

                        casecadeTemplateRequest.setSubject(busiTemplateConference.getName());
                        casecadeTemplateRequest.setTemplateNodes(templateNodes);
                        casecadeTemplateRequest.setShowSecurityLevel(false);
                        casecadeTemplateRequest.setConferencePolicySetting(policySettingDTO);
                        casecadeTemplateRequest.setChairmanPassword(busiTemplateConference.getChairmanPassword());
                        casecadeTemplateRequest.setDuration(busiTemplateConference.getDurationTime() == null ? Integer.MAX_VALUE : busiTemplateConference.getDurationTime());
                        casecadeTemplateRequest.setGuestPassword(busiTemplateConference.getGuestPassword());


                        String userInfo = smc3Bridge.getSmcUserInvoker().getUserInfo(smc3Bridge.getSmcportalTokenInvoker().getUserName(), smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                        UserInfoRep userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
                        String id = userInfoRep.getAccount().getOrganization().getId();
                        casecadeTemplateRequest.setOrganizationId(id);
                        casecadeTemplateRequest.setShowSecurityLevel(false);

                        String s = smc3Bridge.getSmcConferencesTemplateInvoker().updateCascadeTemplate(JSON.toJSONString(casecadeTemplateRequest), busiTemplateConference.getCascadeId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                        if (s == null || s.contains(ConstAPI.ERRORNO)) {
                            throw new CustomException("更新失败");
                        }
                        String cascadeNodes = old.getCascadeNodes();
                        if (Strings.isNotBlank(cascadeNodes) && !Objects.equals("null", cascadeNodes)) {
                            List<TemplateNode> templateNodes_Old = JSONArray.parseArray(cascadeNodes, TemplateNode.class);
                            for (TemplateNode templateNode : templateNodes_Old) {
                                BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(templateNode.getTemplateId());
                                busiMcuSmc3TemplateConference.setCascadeId("");
                                busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                            }

                        }
                        for (TemplateNode templateNode : templateNodes) {
                            BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceBySmcTemplateId(templateNode.getTemplateId());
                            busiMcuSmc3TemplateConference.setCascadeId(busiTemplateConference.getCascadeId());
                            busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConference);
                        }

                    }

                }
            }
        }
        return c;
    }

    private void doDeptUpdate(BusiMcuSmc3TemplateConference busiTemplateConference, List<BusiMcuSmc3TemplateDept> templateDepts)
    {
        BusiMcuSmc3TemplateDept deptCon = new BusiMcuSmc3TemplateDept();
        deptCon.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiMcuSmc3TemplateDept> ds = busiMcuSmc3TemplateDeptMapper.selectBusiMcuSmc3TemplateDeptList(deptCon);
        Map<Long, BusiMcuSmc3TemplateDept> oldMap = new HashMap<>();
        for (BusiMcuSmc3TemplateDept busiTemplateDept : ds)
        {
            oldMap.put(busiTemplateDept.getDeptId(), busiTemplateDept);
        }

        // 添加模板部门顺序信息
        if (!ObjectUtils.isEmpty(templateDepts))
        {
            for (BusiMcuSmc3TemplateDept busiTemplateDept : templateDepts)
            {
                busiTemplateDept.setTemplateConferenceId(busiTemplateConference.getId());
                BusiMcuSmc3TemplateDept oldTd = oldMap.remove(busiTemplateDept.getDeptId());
                if (oldTd != null)
                {
                    oldTd.setUpdateTime(new Date());
                    oldTd.setWeight(busiTemplateDept.getWeight());
                    busiTemplateDept = oldTd;
                    busiMcuSmc3TemplateDeptMapper.updateBusiMcuSmc3TemplateDept(busiTemplateDept);
                }
                else
                {
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setUuid(UUID.randomUUID().toString());
                    busiMcuSmc3TemplateDeptMapper.insertBusiMcuSmc3TemplateDept(busiTemplateDept);
                }
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((deptId, td) -> {
                busiMcuSmc3TemplateConferenceDefaultViewDeptMapper.deleteBusiMcuSmc3TemplateConferenceDefaultViewDeptByTemplateConferenceIdAndDeptId(td.getTemplateConferenceId(), td.getDeptId());
                busiMcuSmc3TemplateDeptMapper.deleteBusiMcuSmc3TemplateDeptById(td.getId());
            });
        }
    }

    private void doParticipantUpdate(BusiMcuSmc3TemplateConference busiTemplateConference, List<BusiMcuSmc3TemplateParticipant> busiTemplateParticipants, Long masterTerminalId, List<ParticipantRspDto> templateParticipants)
    {
        BusiMcuSmc3TemplateParticipant query = new BusiMcuSmc3TemplateParticipant();
        query.setTemplateConferenceId(busiTemplateConference.getId());
        List<BusiMcuSmc3TemplateParticipant> ps = busiMcuSmc3TemplateParticipantMapper.selectBusiMcuSmc3TemplateParticipantList(query);
        Map<Long, BusiMcuSmc3TemplateParticipant> oldMap = new HashMap<>();
        for (BusiMcuSmc3TemplateParticipant tp : ps)
        {
            oldMap.put(tp.getTerminalId(), tp);
        }
        if (!ObjectUtils.isEmpty(busiTemplateParticipants))
        {

            for (BusiMcuSmc3TemplateParticipant busiTemplateParticipant : busiTemplateParticipants)
            {
                busiTemplateParticipant.setTemplateConferenceId(busiTemplateConference.getId());
                BusiMcuSmc3TemplateParticipant oldTp = oldMap.remove(busiTemplateParticipant.getTerminalId());
                if (oldTp != null)
                {
                    oldTp.setWeight(busiTemplateParticipant.getWeight());
                    oldTp.setUpdateTime(new Date());
                    oldTp.setAttendType(busiTemplateParticipant.getAttendType());
                    oldTp.setBusinessProperties(busiTemplateParticipant.getBusinessProperties());
                    busiMcuSmc3TemplateParticipantMapper.updateBusiMcuSmc3TemplateParticipant(oldTp);
                    busiTemplateParticipant = oldTp;
                    participantRspDto(masterTerminalId, templateParticipants, busiTemplateParticipant);
                }
                else
                {
                    // 新增
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setUuid(UUID.randomUUID().toString());

                    participantRspDto(masterTerminalId, templateParticipants, busiTemplateParticipant);
                    busiMcuSmc3TemplateParticipantMapper.insertBusiMcuSmc3TemplateParticipant(busiTemplateParticipant);
                }

                if (masterTerminalId != null && masterTerminalId.longValue() == busiTemplateParticipant.getTerminalId().longValue())
                {
                    // 设置模板会议中配置的主会场参会终端
                    busiTemplateConference.setMasterParticipantId(busiTemplateParticipant.getId());
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConference);
                }
            }

            if (masterTerminalId == null)
            {
                // 设置模板会议中配置的主会场参会终端
                busiTemplateConference.setMasterParticipantId(null);
                busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConference);
            }
        }

        if (!oldMap.isEmpty())
        {
            oldMap.forEach((terminalId, tp) -> {
                busiMcuSmc3TemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuSmc3TemplateConferenceDefaultViewPaticipantById(tp.getId());
                busiMcuSmc3TemplateParticipantMapper.deleteBusiMcuSmc3TemplateParticipantById(tp.getId());
            });
        }
    }

    private void participantRspDto(Long masterTerminalId, List<ParticipantRspDto> templateParticipants, BusiMcuSmc3TemplateParticipant busiTemplateParticipant) {
        BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiTemplateParticipant.getTerminalId());
        if(busiTerminal!=null){
            ParticipantRspDto participantRspDto = new ParticipantRspDto();
            String number = busiTerminal.getNumber();
            participantRspDto.setName(busiTerminal.getName());
            participantRspDto.setUri(number);
            participantRspDto.setIpProtocolType(2);
            participantRspDto.setDialMode("OUT");
            participantRspDto.setVoice(false);
            participantRspDto.setRate(0);
            participantRspDto.setMainParticipant(Objects.equals(busiTerminal.getId(), masterTerminalId));
            participantRspDto.setUri(BusiTerminalUtils.getUri(busiTerminal));
            if (TerminalType.isFmeTemplate(busiTerminal.getType())) {
                BusiTemplateConferenceMapper templateConferenceMapper = BeanFactory.getBean(BusiTemplateConferenceMapper.class);
                BusiTemplateConference templateConference = new BusiTemplateConference();
                templateConference.setConferenceNumber(Long.valueOf(busiTerminal.getNumber()));
                List<BusiTemplateConference> templateConferences = templateConferenceMapper.selectBusiTemplateConferenceList(templateConference);
                if (!CollectionUtils.isEmpty(templateConferences)) {
                    String conferencePassword = templateConferences.get(0).getConferencePassword();
                    if (io.jsonwebtoken.lang.Strings.hasText(conferencePassword)) {
                        participantRspDto.setDtmfInfo(conferencePassword);
                    }
                }
            }
            if(TerminalType.isSMCSIP(busiTerminal.getType())){
                if(busiTemplateParticipant.getBusinessProperties()!=null){
                    Map<String, Object> businessProperties = busiTemplateParticipant.getBusinessProperties();

                    participantRspDto.setAudioProtocol((Integer) businessProperties.get("audioProtocol"));
                    participantRspDto.setVideoProtocol((Integer) businessProperties.get("videoProtocol"));
                    participantRspDto.setVideoResolution((Integer) businessProperties.get("videoResolution"));
                    participantRspDto.setDialMode(businessProperties.get("videoResolution")+"");
                    participantRspDto.setRate((Integer) businessProperties.get("rate"));
                    participantRspDto.setDtmfInfo((String) businessProperties.get("dtmfInfo"));
                    participantRspDto.setServiceZoneId((String) businessProperties.get("serviceZoneId"));
                }

            }
            if (TerminalType.isMcuTemplateCisco(busiTerminal.getType())) {
                participantRspDto.setUri(busiTerminal.getNumber() + "@" + busiTerminal.getIp());
            }

            templateParticipants.add(participantRspDto);
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
        BusiMcuSmc3TemplateConference busiTemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(id);
        if(busiTemplateConference==null){
            throw new CustomException("模板不存在");
        }

        if(Objects.equals(ConstAPI.NORMAL,busiTemplateConference.getCategory())){
            if(Strings.isNotBlank(busiTemplateConference.getCascadeId())){
                throw new CustomException("该模板关联在多级模板中，无法被删除");
            }
        }else {
            BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConference=new BusiMcuSmc3TemplateConference();
            busiMcuSmc3TemplateConference.setCascadeId(busiTemplateConference.getId().toString());
            busiMcuSmc3TemplateConference.setCategory(ConstAPI.NORMAL);
            List<BusiMcuSmc3TemplateConference> busiMcuSmc3TemplateConferences = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(busiMcuSmc3TemplateConference);
            if(busiMcuSmc3TemplateConferences!=null&&busiMcuSmc3TemplateConferences.size()>0){
                for (BusiMcuSmc3TemplateConference mcuSmc3TemplateConference : busiMcuSmc3TemplateConferences) {
                    mcuSmc3TemplateConference.setCascadeId("");
                    busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(mcuSmc3TemplateConference);
                }
            }
        }

        deleteParticipants(id);
        int c = busiMcuSmc3TemplateConferenceMapper.deleteBusiMcuSmc3TemplateConferenceById(id);
        if (c > 0 && busiTemplateConference.getConferenceNumber() != null)
        {
            BusiMcuSmc3TemplateConference con = new BusiMcuSmc3TemplateConference();
            con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
            List<BusiMcuSmc3TemplateConference> cs = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(busiTemplateConference);
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

        Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiTemplateConference.getDeptId());
        if (Objects.equals(ConstAPI.CASCADE, busiTemplateConference.getCategory())) {
            if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(busiTemplateConference.getCascadeId())) {
                smc3Bridge.getSmcConferencesTemplateInvoker().deleteCascadeTemplate(busiTemplateConference.getCascadeId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        }
        if (com.paradisecloud.common.utils.StringUtils.isNotEmpty(busiTemplateConference.getSmcTemplateId())) {
            smc3Bridge.getSmcConferencesTemplateInvoker().deleteConferencesTemplate(busiTemplateConference.getSmcTemplateId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
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

        BusiMcuSmc3TemplateConference busiTemplateConference = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(id);
        busiTemplateConference.setMasterParticipantId(null);
        busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiTemplateConference);
        deleteParticipants(id);
        int c = busiMcuSmc3TemplateConferenceMapper.deleteBusiMcuSmc3TemplateConferenceById(id);
        if (c > 0 && busiTemplateConference.getConferenceNumber() != null) {
            BusiMcuSmc3TemplateConference con = new BusiMcuSmc3TemplateConference();
            con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
            List<BusiMcuSmc3TemplateConference> cs = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(busiTemplateConference);
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

        BusiMcuSmc3TemplateConference tc = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(id);
        if (tc == null || Objects.equals(tc.getName(), name)) {
            return 0;
        }
        tc.setName(name);
        return   busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(tc);
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
        busiMcuSmc3TemplateConferenceDefaultViewDeptMapper.deleteBusiMcuSmc3TemplateConferenceDefaultViewDeptById(id);

        // 根据模板ID批量删除默认视图的参会者信息
        busiMcuSmc3TemplateConferenceDefaultViewPaticipantMapper.deleteBusiMcuSmc3TemplateConferenceDefaultViewPaticipantByTemplateConferenceId(id);

        // 删除分屏信息
        busiMcuSmc3TemplateConferenceDefaultViewCellScreenMapper.deleteBusiMcuSmc3TemplateConferenceDefaultViewCellScreenByTemplateConferenceId(id);

        // 先删除与会者模板
        BusiMcuSmc3TemplateParticipant busiTemplateParticipant = new BusiMcuSmc3TemplateParticipant();
        busiTemplateParticipant.setTemplateConferenceId(id);

        List<Long> pIds = new ArrayList<>();
        List<BusiMcuSmc3TemplateParticipant> ps = busiMcuSmc3TemplateParticipantMapper.selectBusiMcuSmc3TemplateParticipantList(busiTemplateParticipant);
        for (BusiMcuSmc3TemplateParticipant busiTemplateParticipant2 : ps)
        {
            pIds.add(busiTemplateParticipant2.getId());
        }

        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuSmc3TemplateParticipantMapper.deleteBusiMcuSmc3TemplateParticipantByIds(pIds.toArray(new Long[pIds.size()]));
        }

        // 删除部门顺序信息
        BusiMcuSmc3TemplateDept busiTemplateDept = new BusiMcuSmc3TemplateDept();
        busiTemplateDept.setTemplateConferenceId(id);
        pIds = new ArrayList<>();
        List<BusiMcuSmc3TemplateDept> ds = busiMcuSmc3TemplateDeptMapper.selectBusiMcuSmc3TemplateDeptList(busiTemplateDept);
        for (BusiMcuSmc3TemplateDept busiTemplateDept2 : ds)
        {
            pIds.add(busiTemplateDept2.getId());
        }
        if (!ObjectUtils.isEmpty(pIds))
        {
            busiMcuSmc3TemplateDeptMapper.deleteBusiMcuSmc3TemplateDeptByIds(pIds.toArray(new Long[pIds.size()]));
        }
    }


    @Override
    public List<BusiMcuSmc3TemplateConference> selectCascadeConferenceList(Long deptId) {

        return  busiMcuSmc3TemplateConferenceMapper.selectCascadeConferenceList(deptId);
    }
}
