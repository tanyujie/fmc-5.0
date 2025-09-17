package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;

import com.paradisecloud.fcm.fme.attendee.model.enumer.PollingStrategy;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.polling.PollingAttendee;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.polling.PollingScheme;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudTemplatePollingSchemeService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.model.SysDeptCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BusiMcuHwcloudTemplatePollingSchemeServiceImpl implements IBusiHwcloudTemplatePollingSchemeService
{
    private Logger logger = LoggerFactory.getLogger(BusiMcuHwcloudTemplatePollingSchemeServiceImpl.class);

    @Resource
    private BusiMcuHwcloudTemplatePollingSchemeMapper busiMcuHwcloudTemplatePollingSchemeMapper;

    @Resource
    private BusiMcuHwcloudTemplatePollingDeptMapper busiMcuHwcloudTemplatePollingDeptMapper;

    @Resource
    private BusiMcuHwcloudTemplatePollingPaticipantMapper busiMcuHwcloudTemplatePollingPaticipantMapper;

    @Resource
    private BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper;

    @Resource
    private BusiMcuHwcloudTemplateParticipantMapper busiMcuHwcloudTemplateParticipantMapper;

    /**
     * 查询轮询方案
     *
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    @Override
    public ModelBean selectBusiTemplatePollingSchemeById(Long id)
    {
        return buildBusiTemplatePollingScheme(busiMcuHwcloudTemplatePollingSchemeMapper.selectBusiMcuHwcloudTemplatePollingSchemeById(id));
    }

    private ModelBean buildBusiTemplatePollingScheme(BusiMcuHwcloudTemplatePollingScheme pollingScheme)
    {
        BusiMcuHwcloudTemplateConference busiTemplateConference = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(pollingScheme.getTemplateConferenceId());
        BusiMcuHwcloudTemplatePollingPaticipant busiTemplateParticipant = new BusiMcuHwcloudTemplatePollingPaticipant();
        busiTemplateParticipant.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuHwcloudTemplatePollingPaticipant> ps = busiMcuHwcloudTemplatePollingPaticipantMapper.selectBusiMcuHwcloudTemplatePollingPaticipantList(busiTemplateParticipant);

        BusiMcuHwcloudTemplateParticipant con = new BusiMcuHwcloudTemplateParticipant();
        con.setTemplateConferenceId(pollingScheme.getTemplateConferenceId());
        List<BusiMcuHwcloudTemplateParticipant> tps = busiMcuHwcloudTemplateParticipantMapper.selectBusiMcuHwcloudTemplateParticipantList(con);
        Map<String, BusiMcuHwcloudTemplateParticipant> tpMap = new HashMap<>();
        for (BusiMcuHwcloudTemplateParticipant tp : tps)
        {
            tpMap.put(tp.getUuid(), tp);
        }

        HwcloudConferenceContext cc = null;
        if (busiTemplateConference.getConferenceNumber() != null)
        {
            cc = HwcloudConferenceContextCache.getInstance().get(busiTemplateConference.getConferenceNumber().toString());
        }
        List<ModelBean> pMbs = new ArrayList<>();
        BusiMcuHwcloudTemplateParticipant tp = null;
        AttendeeHwcloud a = null;
        Set<String> invalidPollingAttendeeIds = new HashSet<>();
        for (BusiMcuHwcloudTemplatePollingPaticipant pp : ps)
        {
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
            }
            else
            {
                if (cc != null && (a = cc.getAttendeeById(pp.getAttendeeId())) != null)
                {
                    pmb.put("name", a.getName());
                    pmb.put("deptId", a.getDeptId());
                }
                else
                {
                    invalidPollingAttendeeIds.add(pp.getAttendeeId());
                }
            }
            pMbs.add(pmb);
        }

        if (!invalidPollingAttendeeIds.isEmpty())
        {
            int c = busiMcuHwcloudTemplatePollingPaticipantMapper.deleteBusiMcuHwcloudTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
            if (c > 0)
            {
                logger.info("删除无效的轮询会场条数：" + c);
            }
        }

        BusiMcuHwcloudTemplatePollingDept tdCon = new BusiMcuHwcloudTemplatePollingDept();
        tdCon.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuHwcloudTemplatePollingDept> tds = busiMcuHwcloudTemplatePollingDeptMapper.selectBusiMcuHwcloudTemplatePollingDeptList(tdCon);
        List<ModelBean> pds = new ArrayList<>();
        for (BusiMcuHwcloudTemplatePollingDept busiTemplatePollingDept : tds)
        {
            ModelBean m = new ModelBean(busiTemplatePollingDept);
            m.put("deptName", SysDeptCache.getInstance().get(busiTemplatePollingDept.getDeptId()).getDeptName());
            pds.add(m);
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
     * @param busiTemplatePollingScheme 轮询方案
     * @return 轮询方案
     */
    @Override
    public List<BusiMcuHwcloudTemplatePollingScheme> selectBusiTemplatePollingSchemeList(BusiMcuHwcloudTemplatePollingScheme busiTemplatePollingScheme)
    {
        return busiMcuHwcloudTemplatePollingSchemeMapper.selectBusiMcuHwcloudTemplatePollingSchemeList(busiTemplatePollingScheme);
    }

    /**
     * 新增轮询方案
     *
     * @param busiTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int insertBusiTemplatePollingScheme(BusiMcuHwcloudTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuHwcloudTemplatePollingDept> templatePollingDepts
            , List<BusiMcuHwcloudTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiTemplatePollingScheme.getLayout(), "布局不能为空");
        Assert.notNull(busiTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiTemplatePollingScheme.getPollingStrategy());
        busiTemplatePollingScheme.setCreateTime(new Date());
        int c = busiMcuHwcloudTemplatePollingSchemeMapper.insertBusiMcuHwcloudTemplatePollingScheme(busiTemplatePollingScheme);
        if (c > 0)
        {
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");

                // 添加模板部门顺序信息
                for (BusiMcuHwcloudTemplatePollingDept busiTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiTemplateDept.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiMcuHwcloudTemplatePollingDeptMapper.insertBusiMcuHwcloudTemplatePollingDept(busiTemplateDept);
                }

                Set<String> attendeeIds = new HashSet<>();

                // 添加模板与会者顺序信息
                for (BusiMcuHwcloudTemplatePollingPaticipant busiTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    Assert.isTrue(attendeeIds.add(busiTemplateParticipant.getAttendeeId()), "轮询方案参会者“ID”不能重复！");
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateParticipant.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiMcuHwcloudTemplatePollingPaticipantMapper.insertBusiMcuHwcloudTemplatePollingPaticipant(busiTemplateParticipant);
                }
            }
        }
        return c;
    }

    /**
     * 修改轮询方案
     *
     * @param busiTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int updateBusiTemplatePollingScheme(BusiMcuHwcloudTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuHwcloudTemplatePollingDept> templatePollingDepts
            , List<BusiMcuHwcloudTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiTemplatePollingScheme.getLayout(), "分频不能为空");
        Assert.notNull(busiTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiTemplatePollingScheme.getPollingStrategy());
        busiTemplatePollingScheme.setUpdateTime(new Date());
        int c = busiMcuHwcloudTemplatePollingSchemeMapper.updateBusiMcuHwcloudTemplatePollingScheme(busiTemplatePollingScheme);
        if (c > 0)
        {
            // 删除轮询方案对应的子表信息
            busiMcuHwcloudTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(busiTemplatePollingScheme.getId());
            busiMcuHwcloudTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(busiTemplatePollingScheme.getId());
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");

                // 添加模板部门顺序信息
                for (BusiMcuHwcloudTemplatePollingDept busiTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiTemplateDept.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiMcuHwcloudTemplatePollingDeptMapper.insertBusiMcuHwcloudTemplatePollingDept(busiTemplateDept);
                }

                // 添加模板与会者顺序信息
                for (BusiMcuHwcloudTemplatePollingPaticipant busiTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiMcuHwcloudTemplatePollingPaticipantMapper.insertBusiMcuHwcloudTemplatePollingPaticipant(busiTemplateParticipant);
                }
            }

            updateMemery(busiTemplatePollingScheme);
        }

        return c;
    }

    private void updateMemery(BusiMcuHwcloudTemplatePollingScheme busiTemplatePollingScheme)
    {
        busiTemplatePollingScheme = busiMcuHwcloudTemplatePollingSchemeMapper.selectBusiMcuHwcloudTemplatePollingSchemeById(busiTemplatePollingScheme.getId());
        if (YesOrNo.convert(busiTemplatePollingScheme.getEnableStatus()).getBoolean())
        {
            BusiMcuHwcloudTemplateConference bc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(busiTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(bc.getConferenceNumber().toString());

            }
        }
    }

    @Override
    public int updateBusiTemplatePollingSchemes(List<BusiMcuHwcloudTemplatePollingScheme> templatePollingSchemes)
    {
        if (ObjectUtils.isEmpty(templatePollingSchemes))
        {
            return 0;
        }
        BusiMcuHwcloudTemplatePollingScheme open = null;
        for (BusiMcuHwcloudTemplatePollingScheme busiTemplatePollingScheme : templatePollingSchemes)
        {
            busiTemplatePollingScheme.setUpdateTime(new Date());
            busiMcuHwcloudTemplatePollingSchemeMapper.updateBusiMcuHwcloudTemplatePollingScheme(busiTemplatePollingScheme);
            if (YesOrNo.convert(busiTemplatePollingScheme.getEnableStatus()).getBoolean())
            {
                open = busiTemplatePollingScheme;
            }
        }

        if (open != null)
        {
            updateMemery(open);
        }
        else
        {
            BusiMcuHwcloudTemplatePollingScheme busiTemplatePollingScheme = busiMcuHwcloudTemplatePollingSchemeMapper.selectBusiMcuHwcloudTemplatePollingSchemeById(templatePollingSchemes.get(0).getId());
            BusiMcuHwcloudTemplateConference bc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(busiTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(bc.getConferenceNumber().toString());

                if (conferenceContext != null)
                {
                    // 停止轮询

                }
            }
        }
        return 1;
    }

    @Override
    public PollingScheme convert(BusiMcuHwcloudTemplatePollingScheme ps, HwcloudConferenceContext conferenceContext)
    {
        PollingScheme pollingScheme = new PollingScheme();
        pollingScheme.setId(ps.getId());
        pollingScheme.setInterval(ps.getPollingInterval());
        pollingScheme.setLayout(ps.getLayout());
        pollingScheme.setIsBroadcast(YesOrNo.convert(ps.getIsBroadcast()));
        pollingScheme.setPanePlacementSelfPaneMode(PanePlacementSelfPaneMode.convert(ps.getIsDisplaySelf()));
        pollingScheme.setIsFill(YesOrNo.convert(ps.getIsFill()));
        pollingScheme.setPollingStrategy(PollingStrategy.convert(ps.getPollingStrategy()));

        BusiMcuHwcloudTemplatePollingDept con = new BusiMcuHwcloudTemplatePollingDept();
        con.setPollingSchemeId(ps.getId());
        List<BusiMcuHwcloudTemplatePollingDept> pollingDepts = busiMcuHwcloudTemplatePollingDeptMapper.selectBusiMcuHwcloudTemplatePollingDeptList(con);
        for (BusiMcuHwcloudTemplatePollingDept busiTemplatePollingDept : pollingDepts)
        {
            DeptPollingAttendees deptPollingAttendees = new DeptPollingAttendees();
            deptPollingAttendees.setDeptId(busiTemplatePollingDept.getDeptId());
            deptPollingAttendees.setWeight(busiTemplatePollingDept.getWeight());
            pollingScheme.addDeptPollingAttendees(deptPollingAttendees);
        }

        if (pollingScheme.getPollingStrategy() != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingScheme.getPollingStrategy())
        {
            BusiMcuHwcloudTemplatePollingPaticipant con1 = new BusiMcuHwcloudTemplatePollingPaticipant();
            con1.setPollingSchemeId(ps.getId());
            List<BusiMcuHwcloudTemplatePollingPaticipant> pps = busiMcuHwcloudTemplatePollingPaticipantMapper.selectBusiMcuHwcloudTemplatePollingPaticipantList(con1);
            Set<String> invalidPollingAttendeeIds = new HashSet<>();
            Set<String> repeatPollingAttendeeIds = new HashSet<>();
            for (BusiMcuHwcloudTemplatePollingPaticipant busiTemplatePollingPaticipant : pps)
            {
                // 根据remoteParty获取参会者
                AttendeeHwcloud attendee = conferenceContext.getAttendeeById(busiTemplatePollingPaticipant.getAttendeeId());
                if (attendee != null)
                {
                    Assert.isTrue(repeatPollingAttendeeIds.add(busiTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                    PollingAttendee pollingAttendee = new PollingAttendee();
                    pollingAttendee.setAttendee(attendee);
                    pollingAttendee.setInterval(busiTemplatePollingPaticipant.getPollingInterval());
                    pollingAttendee.setWeight(busiTemplatePollingPaticipant.getWeight());
                    pollingScheme.addPollingAttendee(pollingAttendee);
                }
                else
                {
                    invalidPollingAttendeeIds.add(busiTemplatePollingPaticipant.getAttendeeId());
                }
            }

            if (!invalidPollingAttendeeIds.isEmpty())
            {
                int c = busiMcuHwcloudTemplatePollingPaticipantMapper.deleteBusiMcuHwcloudTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
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
    public int deleteBusiTemplatePollingSchemeById(Long id)
    {
        // 删除轮询方案对应的子表信息
        busiMcuHwcloudTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(id);
        busiMcuHwcloudTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(id);
        return busiMcuHwcloudTemplatePollingSchemeMapper.deleteBusiMcuHwcloudTemplatePollingSchemeById(id);
    }
}
