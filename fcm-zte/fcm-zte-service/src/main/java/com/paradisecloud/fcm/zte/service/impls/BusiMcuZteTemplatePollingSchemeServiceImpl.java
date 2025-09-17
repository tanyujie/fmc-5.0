package com.paradisecloud.fcm.zte.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.zte.attendee.model.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.zte.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.zte.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteTemplatePollingSchemeService;
import com.paradisecloud.system.model.SysDeptCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 轮询方案Service业务层处理
 *
 * @author lilinhai
 * @date 2021-02-25
 */
@Transactional
@Service
public class BusiMcuZteTemplatePollingSchemeServiceImpl implements IBusiMcuZteTemplatePollingSchemeService
{
    private Logger logger = LoggerFactory.getLogger(BusiMcuZteTemplatePollingSchemeServiceImpl.class);

    @Resource
    private BusiMcuZteTemplatePollingSchemeMapper busiMcuZteTemplatePollingSchemeMapper;

    @Resource
    private BusiMcuZteTemplatePollingDeptMapper busiMcuZteTemplatePollingDeptMapper;

    @Resource
    private BusiMcuZteTemplatePollingPaticipantMapper busiMcuZteTemplatePollingPaticipantMapper;

    @Resource
    private BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper;

    @Resource
    private BusiMcuZteTemplateParticipantMapper busiMcuZteTemplateParticipantMapper;

    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    /**
     * 查询轮询方案
     *
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    @Override
    public ModelBean selectBusiMcuZteTemplatePollingSchemeById(Long id)
    {
        return buildBusiMcuZteTemplatePollingScheme(busiMcuZteTemplatePollingSchemeMapper.selectBusiMcuZteTemplatePollingSchemeById(id));
    }

    private ModelBean buildBusiMcuZteTemplatePollingScheme(BusiMcuZteTemplatePollingScheme pollingScheme)
    {
        BusiMcuZteTemplateConference busiMcuZteTemplateConference = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(pollingScheme.getTemplateConferenceId());
        BusiMcuZteTemplatePollingPaticipant busiMcuZteTemplateParticipant = new BusiMcuZteTemplatePollingPaticipant();
        busiMcuZteTemplateParticipant.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuZteTemplatePollingPaticipant> ps = busiMcuZteTemplatePollingPaticipantMapper.selectBusiMcuZteTemplatePollingPaticipantList(busiMcuZteTemplateParticipant);

        BusiMcuZteTemplateParticipant con = new BusiMcuZteTemplateParticipant();
        con.setTemplateConferenceId(pollingScheme.getTemplateConferenceId());
        List<BusiMcuZteTemplateParticipant> tps = busiMcuZteTemplateParticipantMapper.selectBusiMcuZteTemplateParticipantList(con);
        Map<String, BusiMcuZteTemplateParticipant> tpMap = new HashMap<>();
        for (BusiMcuZteTemplateParticipant tp : tps)
        {
            tpMap.put(tp.getUuid(), tp);
        }

        McuZteConferenceContext cc = null;
        if (busiMcuZteTemplateConference.getConferenceNumber() != null)
        {
            cc = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuZteTemplateConference.getId(), McuType.MCU_ZTE.getCode()));
        }
        List<ModelBean> pMbs = new ArrayList<>();
        BusiMcuZteTemplateParticipant tp = null;
        AttendeeForMcuZte a = null;
        Set<String> invalidPollingAttendeeIds = new HashSet<>();
        for (BusiMcuZteTemplatePollingPaticipant pp : ps)
        {
            boolean invalid = true;
            ModelBean pmb = new ModelBean(pp);
            if ((tp = tpMap.get(pp.getAttendeeId())) != null)
            {
                BusiTerminal bt = TerminalCache.getInstance().get(tp.getTerminalId());
                Integer onlineStatus = bt.getOnlineStatus();
                if (onlineStatus == null)
                {
                    onlineStatus = TerminalOnlineStatus.OFFLINE.getValue();
                }
                ModelBean tmb = new ModelBean(bt);
                tmb.remove("id");
                tmb.remove("createTime");
                tmb.remove("updateTime");
                pmb.putAll(tmb);
                invalid = false;
            }
            else
            {
                if (cc != null && (a = cc.getAttendeeById(pp.getAttendeeId())) != null)
                {
                    pmb.put("name", a.getName());
                    pmb.put("deptId", a.getDeptId());
                    invalid = false;
                }
            }
            if (cc != null && pp.getDownCascadeTemplateId() != null) {
                String downCascadeContextKey = EncryptIdUtil.generateContextKey(pp.getDownCascadeTemplateId(), pp.getDownCascadeMcuType());
                String downCascadeConferenceId = EncryptIdUtil.generateConferenceId(downCascadeContextKey);
                AttendeeForMcuZte mcuAttendee = cc.getAttendeeById(downCascadeConferenceId);
                if (mcuAttendee != null && mcuAttendee.isMeetingJoined()) {
                    pmb.put("downCascadeConferenceId", downCascadeConferenceId);
                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeContextKey);
                    if (downCascadeConferenceContext != null) {
                        BaseAttendee attendee = downCascadeConferenceContext.getAttendeeById(pp.getAttendeeId());
                        if (attendee != null) {
                            ViewTemplateConference downCascadeViewTemplateConference = viewTemplateConferenceMapper.selectViewTemplateConferenceById(downCascadeConferenceContext.getMcuType(), downCascadeConferenceContext.getTemplateConferenceId());
                            if (downCascadeViewTemplateConference != null) {
                                pmb.put("name", attendee.getName());
                                pmb.put("deptId", -downCascadeConferenceContext.getUpCascadeIndex());
                                invalid = false;
                            }
                        }
                    }
                }
            }
            if (invalid) {
                invalidPollingAttendeeIds.add(pp.getAttendeeId());
            } else {
                pMbs.add(pmb);
            }
        }

        if (!invalidPollingAttendeeIds.isEmpty())
        {
            int c = busiMcuZteTemplatePollingPaticipantMapper.deleteBusiMcuZteTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
            if (c > 0)
            {
                logger.info("删除无效的轮询会场条数：" + c);
            }
        }

        BusiMcuZteTemplatePollingDept tdCon = new BusiMcuZteTemplatePollingDept();
        tdCon.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuZteTemplatePollingDept> tds = busiMcuZteTemplatePollingDeptMapper.selectBusiMcuZteTemplatePollingDeptList(tdCon);
        List<ModelBean> pds = new ArrayList<>();
        for (BusiMcuZteTemplatePollingDept busiMcuZteTemplatePollingDept : tds)
        {
            ModelBean m = new ModelBean(busiMcuZteTemplatePollingDept);
            if (busiMcuZteTemplatePollingDept.getDeptId() > 0) {
                m.put("deptName", SysDeptCache.getInstance().get(busiMcuZteTemplatePollingDept.getDeptId()).getDeptName());
                pds.add(m);
            } else {
                ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                viewTemplateConferenceCon.setUpCascadeId(busiMcuZteTemplateConference.getId());
                viewTemplateConferenceCon.setUpCascadeMcuType(McuType.MCU_ZTE.getCode());
                viewTemplateConferenceCon.setUpCascadeIndex(-busiMcuZteTemplatePollingDept.getDeptId().intValue());
                List<ViewTemplateConference> viewTemplateConferences = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceCon);
                if (viewTemplateConferences != null && viewTemplateConferences.size() > 0) {
                    ViewTemplateConference viewTemplateConference = viewTemplateConferences.get(0);
                    m.put("deptName", viewTemplateConference.getName());
                    pds.add(m);
                }
            }
        }

        ModelBean mb = new ModelBean();
        mb.put("pollingScheme", pollingScheme);
        mb.put("pollingParticipants", pMbs);
        mb.put("pollingDepts", pds);
        return mb;
    }

    /**
     * 查询轮询方案列表
     *
     * @param busiMcuZteTemplatePollingScheme 轮询方案
     * @return 轮询方案
     */
    @Override
    public List<BusiMcuZteTemplatePollingScheme> selectBusiMcuZteTemplatePollingSchemeList(BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme)
    {
        return busiMcuZteTemplatePollingSchemeMapper.selectBusiMcuZteTemplatePollingSchemeList(busiMcuZteTemplatePollingScheme);
    }

    /**
     * 新增轮询方案
     *
     * @param busiMcuZteTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int insertBusiMcuZteTemplatePollingScheme(BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme
            , List<BusiMcuZteTemplatePollingDept> templatePollingDepts
            , List<BusiMcuZteTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiMcuZteTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getLayout(), "布局不能为空");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiMcuZteTemplatePollingScheme.getPollingStrategy());
        busiMcuZteTemplatePollingScheme.setCreateTime(new Date());
        int c = busiMcuZteTemplatePollingSchemeMapper.insertBusiMcuZteTemplatePollingScheme(busiMcuZteTemplatePollingScheme);
        if (c > 0)
        {
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");

                // 添加模板部门顺序信息
                for (BusiMcuZteTemplatePollingDept busiMcuZteTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiMcuZteTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiMcuZteTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiMcuZteTemplateDept.setTemplateConferenceId(busiMcuZteTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuZteTemplateDept.setCreateTime(new Date());
                    busiMcuZteTemplateDept.setPollingSchemeId(busiMcuZteTemplatePollingScheme.getId());
                    busiMcuZteTemplatePollingDeptMapper.insertBusiMcuZteTemplatePollingDept(busiMcuZteTemplateDept);
                }

                Set<String> attendeeIds = new HashSet<>();

                // 添加模板与会者顺序信息
                for (BusiMcuZteTemplatePollingPaticipant busiMcuZteTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiMcuZteTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiMcuZteTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    Assert.isTrue(attendeeIds.add(busiMcuZteTemplateParticipant.getAttendeeId()), "轮询方案参会者“ID”不能重复！");
                    busiMcuZteTemplateParticipant.setTemplateConferenceId(busiMcuZteTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuZteTemplateParticipant.setPollingSchemeId(busiMcuZteTemplatePollingScheme.getId());
                    busiMcuZteTemplateParticipant.setCreateTime(new Date());
                    busiMcuZteTemplatePollingPaticipantMapper.insertBusiMcuZteTemplatePollingPaticipant(busiMcuZteTemplateParticipant);
                }
            }
        }
        return c;
    }

    /**
     * 修改轮询方案
     *
     * @param busiMcuZteTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int updateBusiMcuZteTemplatePollingScheme(BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme
            , List<BusiMcuZteTemplatePollingDept> templatePollingDepts
            , List<BusiMcuZteTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiMcuZteTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getLayout(), "分频不能为空");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiMcuZteTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiMcuZteTemplatePollingScheme.getPollingStrategy());
        busiMcuZteTemplatePollingScheme.setUpdateTime(new Date());
        int c = busiMcuZteTemplatePollingSchemeMapper.updateBusiMcuZteTemplatePollingScheme(busiMcuZteTemplatePollingScheme);
        if (c > 0)
        {
            // 删除轮询方案对应的子表信息
            busiMcuZteTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(busiMcuZteTemplatePollingScheme.getId());
            busiMcuZteTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(busiMcuZteTemplatePollingScheme.getId());
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");

                // 添加模板部门顺序信息
                for (BusiMcuZteTemplatePollingDept busiMcuZteTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiMcuZteTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiMcuZteTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiMcuZteTemplateDept.setTemplateConferenceId(busiMcuZteTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuZteTemplateDept.setCreateTime(new Date());
                    busiMcuZteTemplateDept.setPollingSchemeId(busiMcuZteTemplatePollingScheme.getId());
                    busiMcuZteTemplatePollingDeptMapper.insertBusiMcuZteTemplatePollingDept(busiMcuZteTemplateDept);
                }

                // 添加模板与会者顺序信息
                for (BusiMcuZteTemplatePollingPaticipant busiMcuZteTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiMcuZteTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiMcuZteTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    busiMcuZteTemplateParticipant.setTemplateConferenceId(busiMcuZteTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuZteTemplateParticipant.setCreateTime(new Date());
                    busiMcuZteTemplateParticipant.setPollingSchemeId(busiMcuZteTemplatePollingScheme.getId());
                    busiMcuZteTemplatePollingPaticipantMapper.insertBusiMcuZteTemplatePollingPaticipant(busiMcuZteTemplateParticipant);
                }
            }

            updateMemery(busiMcuZteTemplatePollingScheme);
        }

        return c;
    }

    private void updateMemery(BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme)
    {
        busiMcuZteTemplatePollingScheme = busiMcuZteTemplatePollingSchemeMapper.selectBusiMcuZteTemplatePollingSchemeById(busiMcuZteTemplatePollingScheme.getId());
        if (YesOrNo.convert(busiMcuZteTemplatePollingScheme.getEnableStatus()).getBoolean())
        {
            BusiMcuZteTemplateConference bc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiMcuZteTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(bc.getId(), McuType.MCU_ZTE.getCode()));
                if (conferenceContext != null)
                {
                    // TODO
                }
            }
        }
    }

    @Override
    public int updateBusiMcuZteTemplatePollingSchemes(List<BusiMcuZteTemplatePollingScheme> templatePollingSchemes)
    {
        if (ObjectUtils.isEmpty(templatePollingSchemes))
        {
            return 0;
        }
        BusiMcuZteTemplatePollingScheme open = null;
        for (BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme : templatePollingSchemes)
        {
            busiMcuZteTemplatePollingScheme.setUpdateTime(new Date());
            busiMcuZteTemplatePollingSchemeMapper.updateBusiMcuZteTemplatePollingScheme(busiMcuZteTemplatePollingScheme);
            if (YesOrNo.convert(busiMcuZteTemplatePollingScheme.getEnableStatus()).getBoolean())
            {
                open = busiMcuZteTemplatePollingScheme;
            }
        }

        if (open != null)
        {
            updateMemery(open);
        }
        else
        {
            BusiMcuZteTemplatePollingScheme busiMcuZteTemplatePollingScheme = busiMcuZteTemplatePollingSchemeMapper.selectBusiMcuZteTemplatePollingSchemeById(templatePollingSchemes.get(0).getId());
            BusiMcuZteTemplateConference bc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(busiMcuZteTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(bc.getId(), McuType.MCU_ZTE.getCode()));

                if (conferenceContext != null)
                {
                    // 停止轮询
                    // TODO
                }
            }
        }
        return 1;
    }

    @Override
    public PollingScheme convert(BusiMcuZteTemplatePollingScheme ps, McuZteConferenceContext conferenceContext)
    {
        PollingScheme pollingScheme = new PollingScheme();
        pollingScheme.setId(ps.getId());
        pollingScheme.setInterval(ps.getPollingInterval());
        pollingScheme.setLayout(ps.getLayout());
        pollingScheme.setIsBroadcast(YesOrNo.convert(ps.getIsBroadcast()));
        pollingScheme.setPanePlacementSelfPaneMode(PanePlacementSelfPaneMode.convert(ps.getIsDisplaySelf()));
        pollingScheme.setIsFill(YesOrNo.convert(ps.getIsFill()));
        pollingScheme.setIsFixSelf(YesOrNo.convert(ps.getIsFixSelf()));
        pollingScheme.setPollingStrategy(PollingStrategy.convert(ps.getPollingStrategy()));

        BusiMcuZteTemplatePollingDept con = new BusiMcuZteTemplatePollingDept();
        con.setPollingSchemeId(ps.getId());
        List<BusiMcuZteTemplatePollingDept> pollingDepts = busiMcuZteTemplatePollingDeptMapper.selectBusiMcuZteTemplatePollingDeptList(con);
        for (BusiMcuZteTemplatePollingDept busiMcuZteTemplatePollingDept : pollingDepts)
        {
            DeptPollingAttendees deptPollingAttendees = new DeptPollingAttendees();
            deptPollingAttendees.setDeptId(busiMcuZteTemplatePollingDept.getDeptId());
            deptPollingAttendees.setWeight(busiMcuZteTemplatePollingDept.getWeight());
            pollingScheme.addDeptPollingAttendees(deptPollingAttendees);
        }

        if (pollingScheme.getPollingStrategy() != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingScheme.getPollingStrategy())
        {
            BusiMcuZteTemplatePollingPaticipant con1 = new BusiMcuZteTemplatePollingPaticipant();
            con1.setPollingSchemeId(ps.getId());
            List<BusiMcuZteTemplatePollingPaticipant> pps = busiMcuZteTemplatePollingPaticipantMapper.selectBusiMcuZteTemplatePollingPaticipantList(con1);
            Set<String> invalidPollingAttendeeIds = new HashSet<>();
            Set<String> repeatPollingAttendeeIds = new HashSet<>();
            List<PollingAttendee> pollingAttendeeList = new ArrayList<>();
            for (BusiMcuZteTemplatePollingPaticipant busiMcuZteTemplatePollingPaticipant : pps)
            {
                if (busiMcuZteTemplatePollingPaticipant.getDownCascadeTemplateId() != null) {
                    String downCascadeContextKey = EncryptIdUtil.generateContextKey(busiMcuZteTemplatePollingPaticipant.getDownCascadeTemplateId(), busiMcuZteTemplatePollingPaticipant.getDownCascadeMcuType());
                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeContextKey);
                    if (downCascadeConferenceContext != null) {
                        BaseAttendee downCascadeAttendeeTemp = downCascadeConferenceContext.getAttendeeById(busiMcuZteTemplatePollingPaticipant.getAttendeeId());
                        if (downCascadeAttendeeTemp != null) {
                            Assert.isTrue(repeatPollingAttendeeIds.add(busiMcuZteTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                            BaseAttendee downCascadeAttendee = new BaseAttendee();
                            BeanUtils.copyProperties(downCascadeAttendeeTemp, downCascadeAttendee);
                            downCascadeAttendee.setDeptId(-downCascadeConferenceContext.getUpCascadeIndex());
                            downCascadeAttendee.setDeptName(downCascadeConferenceContext.getName());
                            PollingAttendee pollingAttendee = new PollingAttendee();
                            pollingAttendee.setDownCascadeAttendee(downCascadeAttendee);
                            pollingAttendee.setInterval(busiMcuZteTemplatePollingPaticipant.getPollingInterval());
                            pollingAttendee.setWeight(busiMcuZteTemplatePollingPaticipant.getWeight());
                            pollingAttendeeList.add(pollingAttendee);
                            invalidPollingAttendeeIds.add(downCascadeConferenceContext.getId());
                        }
                    }
                    continue;
                }
                // 根据remoteParty获取参会者
                AttendeeForMcuZte attendee = conferenceContext.getAttendeeById(busiMcuZteTemplatePollingPaticipant.getAttendeeId());
                if (attendee != null)
                {
                    Assert.isTrue(repeatPollingAttendeeIds.add(busiMcuZteTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                    PollingAttendee pollingAttendee = new PollingAttendee();
                    pollingAttendee.setAttendee(attendee);
                    pollingAttendee.setInterval(busiMcuZteTemplatePollingPaticipant.getPollingInterval());
                    pollingAttendee.setWeight(busiMcuZteTemplatePollingPaticipant.getWeight());
                    pollingAttendeeList.add(pollingAttendee);
                }
                else
                {
                    invalidPollingAttendeeIds.add(busiMcuZteTemplatePollingPaticipant.getAttendeeId());
                }
            }

            for (PollingAttendee pollingAttendee : pollingAttendeeList) {
                BaseAttendee baseAttendee = pollingAttendee.getAttendee() != null ? pollingAttendee.getAttendee() : pollingAttendee.getDownCascadeAttendee();
                if (baseAttendee != null) {
                    if (!invalidPollingAttendeeIds.contains(baseAttendee.getId())) {
                        pollingScheme.addPollingAttendee(pollingAttendee);
                    }
                }
            }
            if (!invalidPollingAttendeeIds.isEmpty())
            {
                int c = busiMcuZteTemplatePollingPaticipantMapper.deleteBusiMcuZteTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
                if (c > 0)
                {
                    logger.info("PollingScheme convert删除无效的轮询会场条数：" + c);
                }
            }

            // 排序轮询参会者
            pollingScheme.sort();
        }

        return pollingScheme;
    }

    /**
     * 删除轮询方案信息
     *
     * @param id 轮询方案ID
     * @return 结果
     */
    @Override
    public int deleteBusiMcuZteTemplatePollingSchemeById(Long id)
    {
        // 删除轮询方案对应的子表信息
        busiMcuZteTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(id);
        busiMcuZteTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(id);
        return busiMcuZteTemplatePollingSchemeMapper.deleteBusiMcuZteTemplatePollingSchemeById(id);
    }
}
