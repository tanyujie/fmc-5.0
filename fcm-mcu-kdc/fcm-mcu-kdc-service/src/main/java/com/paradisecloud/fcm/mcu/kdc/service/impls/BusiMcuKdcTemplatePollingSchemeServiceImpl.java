package com.paradisecloud.fcm.mcu.kdc.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcTemplatePollingSchemeService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
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
public class BusiMcuKdcTemplatePollingSchemeServiceImpl implements IBusiMcuKdcTemplatePollingSchemeService 
{
    private Logger logger = LoggerFactory.getLogger(BusiMcuKdcTemplatePollingSchemeServiceImpl.class);
    
    @Resource
    private BusiMcuKdcTemplatePollingSchemeMapper busiMcuKdcTemplatePollingSchemeMapper;
    
    @Resource
    private BusiMcuKdcTemplatePollingDeptMapper busiMcuKdcTemplatePollingDeptMapper;
    
    @Resource
    private BusiMcuKdcTemplatePollingPaticipantMapper busiMcuKdcTemplatePollingPaticipantMapper;
    
    @Resource
    private BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper;
    
    @Resource
    private BusiMcuKdcTemplateParticipantMapper busiMcuKdcTemplateParticipantMapper;

    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    @Override
    public ModelBean selectBusiMcuKdcTemplatePollingSchemeById(Long id)
    {
        return buildBusiMcuKdcTemplatePollingScheme(busiMcuKdcTemplatePollingSchemeMapper.selectBusiMcuKdcTemplatePollingSchemeById(id));
    }
    
    private ModelBean buildBusiMcuKdcTemplatePollingScheme(BusiMcuKdcTemplatePollingScheme pollingScheme)
    {
        BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(pollingScheme.getTemplateConferenceId());
        BusiMcuKdcTemplatePollingPaticipant busiMcuKdcTemplateParticipant = new BusiMcuKdcTemplatePollingPaticipant();
        busiMcuKdcTemplateParticipant.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuKdcTemplatePollingPaticipant> ps = busiMcuKdcTemplatePollingPaticipantMapper.selectBusiMcuKdcTemplatePollingPaticipantList(busiMcuKdcTemplateParticipant);
        
        BusiMcuKdcTemplateParticipant con = new BusiMcuKdcTemplateParticipant();
        con.setTemplateConferenceId(pollingScheme.getTemplateConferenceId());
        List<BusiMcuKdcTemplateParticipant> tps = busiMcuKdcTemplateParticipantMapper.selectBusiMcuKdcTemplateParticipantList(con);
        Map<String, BusiMcuKdcTemplateParticipant> tpMap = new HashMap<>();
        for (BusiMcuKdcTemplateParticipant tp : tps)
        {
            tpMap.put(tp.getUuid(), tp);
        }
        
        McuKdcConferenceContext cc = null;
        if (busiMcuKdcTemplateConference.getConferenceNumber() != null)
        {
            cc = McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuKdcTemplateConference.getId(), McuType.MCU_KDC.getCode()));
        }
        List<ModelBean> pMbs = new ArrayList<>();
        BusiMcuKdcTemplateParticipant tp = null;
        AttendeeForMcuKdc a = null;
        Set<String> invalidPollingAttendeeIds = new HashSet<>();
        for (BusiMcuKdcTemplatePollingPaticipant pp : ps)
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
                AttendeeForMcuKdc mcuAttendee = cc.getAttendeeById(downCascadeConferenceId);
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
            int c = busiMcuKdcTemplatePollingPaticipantMapper.deleteBusiMcuKdcTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
            if (c > 0)
            {
                logger.info("删除无效的轮询会场条数：" + c);
            }
        }
        
        BusiMcuKdcTemplatePollingDept tdCon = new BusiMcuKdcTemplatePollingDept();
        tdCon.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuKdcTemplatePollingDept> tds = busiMcuKdcTemplatePollingDeptMapper.selectBusiMcuKdcTemplatePollingDeptList(tdCon);
        List<ModelBean> pds = new ArrayList<>();
        for (BusiMcuKdcTemplatePollingDept busiMcuKdcTemplatePollingDept : tds)
        {
            ModelBean m = new ModelBean(busiMcuKdcTemplatePollingDept);
            if (busiMcuKdcTemplatePollingDept.getDeptId() > 0) {
                m.put("deptName", SysDeptCache.getInstance().get(busiMcuKdcTemplatePollingDept.getDeptId()).getDeptName());
                pds.add(m);
            } else {
                ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                viewTemplateConferenceCon.setUpCascadeId(busiMcuKdcTemplateConference.getId());
                viewTemplateConferenceCon.setUpCascadeMcuType(McuType.MCU_KDC.getCode());
                viewTemplateConferenceCon.setUpCascadeIndex(-busiMcuKdcTemplatePollingDept.getDeptId().intValue());
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
     * @param busiMcuKdcTemplatePollingScheme 轮询方案
     * @return 轮询方案
     */
    @Override
    public List<BusiMcuKdcTemplatePollingScheme> selectBusiMcuKdcTemplatePollingSchemeList(BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme)
    {
        return busiMcuKdcTemplatePollingSchemeMapper.selectBusiMcuKdcTemplatePollingSchemeList(busiMcuKdcTemplatePollingScheme);
    }

    /**
     * 新增轮询方案
     * 
     * @param busiMcuKdcTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int insertBusiMcuKdcTemplatePollingScheme(BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme
            , List<BusiMcuKdcTemplatePollingDept> templatePollingDepts
            , List<BusiMcuKdcTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getLayout(), "布局不能为空");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiMcuKdcTemplatePollingScheme.getPollingStrategy());
        busiMcuKdcTemplatePollingScheme.setCreateTime(new Date());
        int c = busiMcuKdcTemplatePollingSchemeMapper.insertBusiMcuKdcTemplatePollingScheme(busiMcuKdcTemplatePollingScheme);
        if (c > 0)
        {
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiMcuKdcTemplatePollingDept busiMcuKdcTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiMcuKdcTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiMcuKdcTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiMcuKdcTemplateDept.setTemplateConferenceId(busiMcuKdcTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuKdcTemplateDept.setCreateTime(new Date());
                    busiMcuKdcTemplateDept.setPollingSchemeId(busiMcuKdcTemplatePollingScheme.getId());
                    busiMcuKdcTemplatePollingDeptMapper.insertBusiMcuKdcTemplatePollingDept(busiMcuKdcTemplateDept);
                }
                
                Set<String> attendeeIds = new HashSet<>();
                
                // 添加模板与会者顺序信息
                for (BusiMcuKdcTemplatePollingPaticipant busiMcuKdcTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiMcuKdcTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiMcuKdcTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    Assert.isTrue(attendeeIds.add(busiMcuKdcTemplateParticipant.getAttendeeId()), "轮询方案参会者“ID”不能重复！");
                    busiMcuKdcTemplateParticipant.setTemplateConferenceId(busiMcuKdcTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuKdcTemplateParticipant.setPollingSchemeId(busiMcuKdcTemplatePollingScheme.getId());
                    busiMcuKdcTemplateParticipant.setCreateTime(new Date());
                    busiMcuKdcTemplatePollingPaticipantMapper.insertBusiMcuKdcTemplatePollingPaticipant(busiMcuKdcTemplateParticipant);
                }
            }
        }
        return c;
    }

    /**
     * 修改轮询方案
     * 
     * @param busiMcuKdcTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int updateBusiMcuKdcTemplatePollingScheme(BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme
            , List<BusiMcuKdcTemplatePollingDept> templatePollingDepts
            , List<BusiMcuKdcTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getLayout(), "分频不能为空");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiMcuKdcTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiMcuKdcTemplatePollingScheme.getPollingStrategy());
        busiMcuKdcTemplatePollingScheme.setUpdateTime(new Date());
        int c = busiMcuKdcTemplatePollingSchemeMapper.updateBusiMcuKdcTemplatePollingScheme(busiMcuKdcTemplatePollingScheme);
        if (c > 0)
        {
            // 删除轮询方案对应的子表信息
            busiMcuKdcTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(busiMcuKdcTemplatePollingScheme.getId());
            busiMcuKdcTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(busiMcuKdcTemplatePollingScheme.getId());
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiMcuKdcTemplatePollingDept busiMcuKdcTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiMcuKdcTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiMcuKdcTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiMcuKdcTemplateDept.setTemplateConferenceId(busiMcuKdcTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuKdcTemplateDept.setCreateTime(new Date());
                    busiMcuKdcTemplateDept.setPollingSchemeId(busiMcuKdcTemplatePollingScheme.getId());
                    busiMcuKdcTemplatePollingDeptMapper.insertBusiMcuKdcTemplatePollingDept(busiMcuKdcTemplateDept);
                }
                
                // 添加模板与会者顺序信息
                for (BusiMcuKdcTemplatePollingPaticipant busiMcuKdcTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiMcuKdcTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiMcuKdcTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    busiMcuKdcTemplateParticipant.setTemplateConferenceId(busiMcuKdcTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuKdcTemplateParticipant.setCreateTime(new Date());
                    busiMcuKdcTemplateParticipant.setPollingSchemeId(busiMcuKdcTemplatePollingScheme.getId());
                    busiMcuKdcTemplatePollingPaticipantMapper.insertBusiMcuKdcTemplatePollingPaticipant(busiMcuKdcTemplateParticipant);
                }
            }
            
            updateMemery(busiMcuKdcTemplatePollingScheme);
        }
        
        return c;
    }

    private void updateMemery(BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme)
    {
        busiMcuKdcTemplatePollingScheme = busiMcuKdcTemplatePollingSchemeMapper.selectBusiMcuKdcTemplatePollingSchemeById(busiMcuKdcTemplatePollingScheme.getId());
        if (YesOrNo.convert(busiMcuKdcTemplatePollingScheme.getEnableStatus()).getBoolean())
        {
            BusiMcuKdcTemplateConference bc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiMcuKdcTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(bc.getId(), McuType.MCU_KDC.getCode()));
                if (conferenceContext != null)
                {
                    // TODO
                }
            }
        }
    }
    
    public int updateBusiMcuKdcTemplatePollingSchemes(List<BusiMcuKdcTemplatePollingScheme> templatePollingSchemes)
    {
        if (ObjectUtils.isEmpty(templatePollingSchemes))
        {
            return 0;
        }
        BusiMcuKdcTemplatePollingScheme open = null; 
        for (BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme : templatePollingSchemes)
        {
            busiMcuKdcTemplatePollingScheme.setUpdateTime(new Date());
            busiMcuKdcTemplatePollingSchemeMapper.updateBusiMcuKdcTemplatePollingScheme(busiMcuKdcTemplatePollingScheme);
            if (YesOrNo.convert(busiMcuKdcTemplatePollingScheme.getEnableStatus()).getBoolean())
            {
                open = busiMcuKdcTemplatePollingScheme;
            }
        }
        
        if (open != null)
        {
            updateMemery(open);
        }
        else
        {
            BusiMcuKdcTemplatePollingScheme busiMcuKdcTemplatePollingScheme = busiMcuKdcTemplatePollingSchemeMapper.selectBusiMcuKdcTemplatePollingSchemeById(templatePollingSchemes.get(0).getId());
            BusiMcuKdcTemplateConference bc = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceById(busiMcuKdcTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(bc.getId(), McuType.MCU_KDC.getCode()));
                
                if (conferenceContext != null)
                {
                    // 停止轮询
                    // TODO
                }
            }
        }
        return 1;
    }

    public PollingScheme convert(BusiMcuKdcTemplatePollingScheme ps, McuKdcConferenceContext conferenceContext)
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
        
        BusiMcuKdcTemplatePollingDept con = new BusiMcuKdcTemplatePollingDept();
        con.setPollingSchemeId(ps.getId());
        List<BusiMcuKdcTemplatePollingDept> pollingDepts = busiMcuKdcTemplatePollingDeptMapper.selectBusiMcuKdcTemplatePollingDeptList(con);
        for (BusiMcuKdcTemplatePollingDept busiMcuKdcTemplatePollingDept : pollingDepts)
        {
            DeptPollingAttendees deptPollingAttendees = new DeptPollingAttendees();
            deptPollingAttendees.setDeptId(busiMcuKdcTemplatePollingDept.getDeptId());
            deptPollingAttendees.setWeight(busiMcuKdcTemplatePollingDept.getWeight());
            pollingScheme.addDeptPollingAttendees(deptPollingAttendees);
        }
        
        if (pollingScheme.getPollingStrategy() != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingScheme.getPollingStrategy())
        {
            BusiMcuKdcTemplatePollingPaticipant con1 = new BusiMcuKdcTemplatePollingPaticipant();
            con1.setPollingSchemeId(ps.getId());
            List<BusiMcuKdcTemplatePollingPaticipant> pps = busiMcuKdcTemplatePollingPaticipantMapper.selectBusiMcuKdcTemplatePollingPaticipantList(con1);
            Set<String> invalidPollingAttendeeIds = new HashSet<>();
            Set<String> repeatPollingAttendeeIds = new HashSet<>();
            List<PollingAttendee> pollingAttendeeList = new ArrayList<>();
            for (BusiMcuKdcTemplatePollingPaticipant busiMcuKdcTemplatePollingPaticipant : pps)
            {
                if (busiMcuKdcTemplatePollingPaticipant.getDownCascadeTemplateId() != null) {
                    String downCascadeContextKey = EncryptIdUtil.generateContextKey(busiMcuKdcTemplatePollingPaticipant.getDownCascadeTemplateId(), busiMcuKdcTemplatePollingPaticipant.getDownCascadeMcuType());
                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeContextKey);
                    if (downCascadeConferenceContext != null) {
                        BaseAttendee downCascadeAttendeeTemp = downCascadeConferenceContext.getAttendeeById(busiMcuKdcTemplatePollingPaticipant.getAttendeeId());
                        if (downCascadeAttendeeTemp != null) {
                            Assert.isTrue(repeatPollingAttendeeIds.add(busiMcuKdcTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                            BaseAttendee downCascadeAttendee = new BaseAttendee();
                            BeanUtils.copyProperties(downCascadeAttendeeTemp, downCascadeAttendee);
                            downCascadeAttendee.setDeptId(-downCascadeConferenceContext.getUpCascadeIndex());
                            downCascadeAttendee.setDeptName(downCascadeConferenceContext.getName());
                            PollingAttendee pollingAttendee = new PollingAttendee();
                            pollingAttendee.setDownCascadeAttendee(downCascadeAttendee);
                            pollingAttendee.setInterval(busiMcuKdcTemplatePollingPaticipant.getPollingInterval());
                            pollingAttendee.setWeight(busiMcuKdcTemplatePollingPaticipant.getWeight());
                            pollingAttendeeList.add(pollingAttendee);
                            invalidPollingAttendeeIds.add(downCascadeConferenceContext.getId());
                        }
                    }
                    continue;
                }
                // 根据remoteParty获取参会者
                AttendeeForMcuKdc attendee = conferenceContext.getAttendeeById(busiMcuKdcTemplatePollingPaticipant.getAttendeeId());
                if (attendee != null)
                {
                    Assert.isTrue(repeatPollingAttendeeIds.add(busiMcuKdcTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                    PollingAttendee pollingAttendee = new PollingAttendee();
                    pollingAttendee.setAttendee(attendee);
                    pollingAttendee.setInterval(busiMcuKdcTemplatePollingPaticipant.getPollingInterval());
                    pollingAttendee.setWeight(busiMcuKdcTemplatePollingPaticipant.getWeight());
                    pollingAttendeeList.add(pollingAttendee);
                }
                else
                {
                    invalidPollingAttendeeIds.add(busiMcuKdcTemplatePollingPaticipant.getAttendeeId());
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
                int c = busiMcuKdcTemplatePollingPaticipantMapper.deleteBusiMcuKdcTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
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
    public int deleteBusiMcuKdcTemplatePollingSchemeById(Long id)
    {
        // 删除轮询方案对应的子表信息
        busiMcuKdcTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(id);
        busiMcuKdcTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(id);
        return busiMcuKdcTemplatePollingSchemeMapper.deleteBusiMcuKdcTemplatePollingSchemeById(id);
    }
}
