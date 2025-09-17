package com.paradisecloud.fcm.smc2.setvice2.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;

import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polingstrategy.PollingStrategy;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polling.PollingAttendee;
import com.paradisecloud.fcm.smc2.model.attendee.operation.polling.PollingScheme;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2TemplatePollingSchemeService;
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
public class BusiMcuSmc2TemplatePollingSchemeServiceImpl implements IBusiSmc2TemplatePollingSchemeService
{
    private Logger logger = LoggerFactory.getLogger(BusiMcuSmc2TemplatePollingSchemeServiceImpl.class);
    
    @Resource
    private BusiMcuSmc2TemplatePollingSchemeMapper busiMcuSmc2TemplatePollingSchemeMapper;
    
    @Resource
    private BusiMcuSmc2TemplatePollingDeptMapper busiMcuSmc2TemplatePollingDeptMapper;
    
    @Resource
    private BusiMcuSmc2TemplatePollingPaticipantMapper busiMcuSmc2TemplatePollingPaticipantMapper;
    
    @Resource
    private BusiMcuSmc2TemplateConferenceMapper busiMcuSmc2TemplateConferenceMapper;
    
    @Resource
    private BusiMcuSmc2TemplateParticipantMapper busiMcuSmc2TemplateParticipantMapper;

    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    @Override
    public ModelBean selectBusiTemplatePollingSchemeById(Long id)
    {
        return buildBusiTemplatePollingScheme(busiMcuSmc2TemplatePollingSchemeMapper.selectBusiMcuSmc2TemplatePollingSchemeById(id));
    }
    
    private ModelBean buildBusiTemplatePollingScheme(BusiMcuSmc2TemplatePollingScheme pollingScheme)
    {
        BusiMcuSmc2TemplateConference busiTemplateConference = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(pollingScheme.getTemplateConferenceId());
        BusiMcuSmc2TemplatePollingPaticipant busiTemplateParticipant = new BusiMcuSmc2TemplatePollingPaticipant();
        busiTemplateParticipant.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuSmc2TemplatePollingPaticipant> ps = busiMcuSmc2TemplatePollingPaticipantMapper.selectBusiMcuSmc2TemplatePollingPaticipantList(busiTemplateParticipant);
        
        BusiMcuSmc2TemplateParticipant con = new BusiMcuSmc2TemplateParticipant();
        con.setTemplateConferenceId(pollingScheme.getTemplateConferenceId());
        List<BusiMcuSmc2TemplateParticipant> tps = busiMcuSmc2TemplateParticipantMapper.selectBusiMcuSmc2TemplateParticipantList(con);
        Map<String, BusiMcuSmc2TemplateParticipant> tpMap = new HashMap<>();
        for (BusiMcuSmc2TemplateParticipant tp : tps)
        {
            tpMap.put(tp.getUuid(), tp);
        }
        
        Smc2ConferenceContext cc = null;
        if (busiTemplateConference.getConferenceNumber() != null)
        {
            cc = Smc2ConferenceContextCache.getInstance().get(busiTemplateConference.getConferenceNumber().toString());
        }
        List<ModelBean> pMbs = new ArrayList<>();
        BusiMcuSmc2TemplateParticipant tp = null;
        AttendeeSmc2 a = null;
        Set<String> invalidPollingAttendeeIds = new HashSet<>();
        for (BusiMcuSmc2TemplatePollingPaticipant pp : ps)
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
            int c = busiMcuSmc2TemplatePollingPaticipantMapper.deleteBusiMcuSmc2TemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
            if (c > 0)
            {
                logger.info("删除无效的轮询会场条数：" + c);
            }
        }
        
        BusiMcuSmc2TemplatePollingDept tdCon = new BusiMcuSmc2TemplatePollingDept();
        tdCon.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuSmc2TemplatePollingDept> tds = busiMcuSmc2TemplatePollingDeptMapper.selectBusiMcuSmc2TemplatePollingDeptList(tdCon);
        List<ModelBean> pds = new ArrayList<>();
        for (BusiMcuSmc2TemplatePollingDept busiTemplatePollingDept : tds)
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
    public List<BusiMcuSmc2TemplatePollingScheme> selectBusiTemplatePollingSchemeList(BusiMcuSmc2TemplatePollingScheme busiTemplatePollingScheme)
    {
        return busiMcuSmc2TemplatePollingSchemeMapper.selectBusiMcuSmc2TemplatePollingSchemeList(busiTemplatePollingScheme);
    }

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int insertBusiTemplatePollingScheme(BusiMcuSmc2TemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuSmc2TemplatePollingDept> templatePollingDepts
            , List<BusiMcuSmc2TemplatePollingPaticipant> templatePollingPaticipants)
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
        int c = busiMcuSmc2TemplatePollingSchemeMapper.insertBusiMcuSmc2TemplatePollingScheme(busiTemplatePollingScheme);
        if (c > 0)
        {
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiMcuSmc2TemplatePollingDept busiTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiTemplateDept.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiMcuSmc2TemplatePollingDeptMapper.insertBusiMcuSmc2TemplatePollingDept(busiTemplateDept);
                }
                
                Set<String> attendeeIds = new HashSet<>();
                
                // 添加模板与会者顺序信息
                for (BusiMcuSmc2TemplatePollingPaticipant busiTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    Assert.isTrue(attendeeIds.add(busiTemplateParticipant.getAttendeeId()), "轮询方案参会者“ID”不能重复！");
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateParticipant.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiMcuSmc2TemplatePollingPaticipantMapper.insertBusiMcuSmc2TemplatePollingPaticipant(busiTemplateParticipant);
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
    public int updateBusiTemplatePollingScheme(BusiMcuSmc2TemplatePollingScheme busiTemplatePollingScheme
            , List<BusiMcuSmc2TemplatePollingDept> templatePollingDepts
            , List<BusiMcuSmc2TemplatePollingPaticipant> templatePollingPaticipants)
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
        int c = busiMcuSmc2TemplatePollingSchemeMapper.updateBusiMcuSmc2TemplatePollingScheme(busiTemplatePollingScheme);
        if (c > 0)
        {
            // 删除轮询方案对应的子表信息
            busiMcuSmc2TemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(busiTemplatePollingScheme.getId());
            busiMcuSmc2TemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(busiTemplatePollingScheme.getId());
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiMcuSmc2TemplatePollingDept busiTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiTemplateDept.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiMcuSmc2TemplatePollingDeptMapper.insertBusiMcuSmc2TemplatePollingDept(busiTemplateDept);
                }
                
                // 添加模板与会者顺序信息
                for (BusiMcuSmc2TemplatePollingPaticipant busiTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiMcuSmc2TemplatePollingPaticipantMapper.insertBusiMcuSmc2TemplatePollingPaticipant(busiTemplateParticipant);
                }
            }
            
            updateMemery(busiTemplatePollingScheme);
        }
        
        return c;
    }

    private void updateMemery(BusiMcuSmc2TemplatePollingScheme busiTemplatePollingScheme)
    {
        busiTemplatePollingScheme = busiMcuSmc2TemplatePollingSchemeMapper.selectBusiMcuSmc2TemplatePollingSchemeById(busiTemplatePollingScheme.getId());
        if (YesOrNo.convert(busiTemplatePollingScheme.getEnableStatus()).getBoolean())
        {
            BusiMcuSmc2TemplateConference bc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(bc.getConferenceNumber().toString());

            }
        }
    }
    
    @Override
    public int updateBusiTemplatePollingSchemes(List<BusiMcuSmc2TemplatePollingScheme> templatePollingSchemes)
    {
        if (ObjectUtils.isEmpty(templatePollingSchemes))
        {
            return 0;
        }
        BusiMcuSmc2TemplatePollingScheme open = null;
        for (BusiMcuSmc2TemplatePollingScheme busiTemplatePollingScheme : templatePollingSchemes)
        {
            busiTemplatePollingScheme.setUpdateTime(new Date());
            busiMcuSmc2TemplatePollingSchemeMapper.updateBusiMcuSmc2TemplatePollingScheme(busiTemplatePollingScheme);
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
            BusiMcuSmc2TemplatePollingScheme busiTemplatePollingScheme = busiMcuSmc2TemplatePollingSchemeMapper.selectBusiMcuSmc2TemplatePollingSchemeById(templatePollingSchemes.get(0).getId());
            BusiMcuSmc2TemplateConference bc = busiMcuSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(busiTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(bc.getConferenceNumber().toString());
                
                if (conferenceContext != null)
                {
                    // 停止轮询

                }
            }
        }
        return 1;
    }

    @Override
    public PollingScheme convert(BusiMcuSmc2TemplatePollingScheme ps, Smc2ConferenceContext conferenceContext)
    {
        PollingScheme pollingScheme = new PollingScheme();
        pollingScheme.setId(ps.getId());
        pollingScheme.setInterval(ps.getPollingInterval());
        pollingScheme.setLayout(ps.getLayout());
        pollingScheme.setIsBroadcast(YesOrNo.convert(ps.getIsBroadcast()));
        pollingScheme.setPanePlacementSelfPaneMode(PanePlacementSelfPaneMode.convert(ps.getIsDisplaySelf()));
        pollingScheme.setIsFill(YesOrNo.convert(ps.getIsFill()));
        pollingScheme.setPollingStrategy(PollingStrategy.convert(ps.getPollingStrategy()));
        
        BusiMcuSmc2TemplatePollingDept con = new BusiMcuSmc2TemplatePollingDept();
        con.setPollingSchemeId(ps.getId());
        List<BusiMcuSmc2TemplatePollingDept> pollingDepts = busiMcuSmc2TemplatePollingDeptMapper.selectBusiMcuSmc2TemplatePollingDeptList(con);
        for (BusiMcuSmc2TemplatePollingDept busiTemplatePollingDept : pollingDepts)
        {
            DeptPollingAttendees deptPollingAttendees = new DeptPollingAttendees();
            deptPollingAttendees.setDeptId(busiTemplatePollingDept.getDeptId());
            deptPollingAttendees.setWeight(busiTemplatePollingDept.getWeight());
            pollingScheme.addDeptPollingAttendees(deptPollingAttendees);
        }
        
        if (pollingScheme.getPollingStrategy() != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingScheme.getPollingStrategy())
        {
            BusiMcuSmc2TemplatePollingPaticipant con1 = new BusiMcuSmc2TemplatePollingPaticipant();
            con1.setPollingSchemeId(ps.getId());
            List<BusiMcuSmc2TemplatePollingPaticipant> pps = busiMcuSmc2TemplatePollingPaticipantMapper.selectBusiMcuSmc2TemplatePollingPaticipantList(con1);
            Set<String> invalidPollingAttendeeIds = new HashSet<>();
            Set<String> repeatPollingAttendeeIds = new HashSet<>();
            for (BusiMcuSmc2TemplatePollingPaticipant busiTemplatePollingPaticipant : pps)
            {
                // 根据remoteParty获取参会者
                AttendeeSmc2 attendee = conferenceContext.getAttendeeById(busiTemplatePollingPaticipant.getAttendeeId());
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
                int c = busiMcuSmc2TemplatePollingPaticipantMapper.deleteBusiMcuSmc2TemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
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
        busiMcuSmc2TemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(id);
        busiMcuSmc2TemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(id);
        return busiMcuSmc2TemplatePollingSchemeMapper.deleteBusiMcuSmc2TemplatePollingSchemeById(id);
    }
}
