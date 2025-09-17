package com.paradisecloud.fcm.mcu.plc.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.plc.attendee.model.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.mcu.plc.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.mcu.plc.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcTemplatePollingSchemeService;
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
public class BusiMcuPlcTemplatePollingSchemeServiceImpl implements IBusiMcuPlcTemplatePollingSchemeService 
{
    private Logger logger = LoggerFactory.getLogger(BusiMcuPlcTemplatePollingSchemeServiceImpl.class);
    
    @Resource
    private BusiMcuPlcTemplatePollingSchemeMapper busiMcuPlcTemplatePollingSchemeMapper;
    
    @Resource
    private BusiMcuPlcTemplatePollingDeptMapper busiMcuPlcTemplatePollingDeptMapper;
    
    @Resource
    private BusiMcuPlcTemplatePollingPaticipantMapper busiMcuPlcTemplatePollingPaticipantMapper;
    
    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    
    @Resource
    private BusiMcuPlcTemplateParticipantMapper busiMcuPlcTemplateParticipantMapper;

    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    @Override
    public ModelBean selectBusiMcuPlcTemplatePollingSchemeById(Long id)
    {
        return buildBusiMcuPlcTemplatePollingScheme(busiMcuPlcTemplatePollingSchemeMapper.selectBusiMcuPlcTemplatePollingSchemeById(id));
    }
    
    private ModelBean buildBusiMcuPlcTemplatePollingScheme(BusiMcuPlcTemplatePollingScheme pollingScheme)
    {
        BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(pollingScheme.getTemplateConferenceId());
        BusiMcuPlcTemplatePollingPaticipant busiMcuPlcTemplateParticipant = new BusiMcuPlcTemplatePollingPaticipant();
        busiMcuPlcTemplateParticipant.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuPlcTemplatePollingPaticipant> ps = busiMcuPlcTemplatePollingPaticipantMapper.selectBusiMcuPlcTemplatePollingPaticipantList(busiMcuPlcTemplateParticipant);
        
        BusiMcuPlcTemplateParticipant con = new BusiMcuPlcTemplateParticipant();
        con.setTemplateConferenceId(pollingScheme.getTemplateConferenceId());
        List<BusiMcuPlcTemplateParticipant> tps = busiMcuPlcTemplateParticipantMapper.selectBusiMcuPlcTemplateParticipantList(con);
        Map<String, BusiMcuPlcTemplateParticipant> tpMap = new HashMap<>();
        for (BusiMcuPlcTemplateParticipant tp : tps)
        {
            tpMap.put(tp.getUuid(), tp);
        }
        
        McuPlcConferenceContext cc = null;
        if (busiMcuPlcTemplateConference.getConferenceNumber() != null)
        {
            cc = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuPlcTemplateConference.getId(), McuType.MCU_PLC.getCode()));
        }
        List<ModelBean> pMbs = new ArrayList<>();
        BusiMcuPlcTemplateParticipant tp = null;
        AttendeeForMcuPlc a = null;
        Set<String> invalidPollingAttendeeIds = new HashSet<>();
        for (BusiMcuPlcTemplatePollingPaticipant pp : ps)
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
                AttendeeForMcuPlc mcuAttendee = cc.getAttendeeById(downCascadeConferenceId);
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
            int c = busiMcuPlcTemplatePollingPaticipantMapper.deleteBusiMcuPlcTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
            if (c > 0)
            {
                logger.info("删除无效的轮询会场条数：" + c);
            }
        }
        
        BusiMcuPlcTemplatePollingDept tdCon = new BusiMcuPlcTemplatePollingDept();
        tdCon.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuPlcTemplatePollingDept> tds = busiMcuPlcTemplatePollingDeptMapper.selectBusiMcuPlcTemplatePollingDeptList(tdCon);
        List<ModelBean> pds = new ArrayList<>();
        for (BusiMcuPlcTemplatePollingDept busiMcuPlcTemplatePollingDept : tds)
        {
            ModelBean m = new ModelBean(busiMcuPlcTemplatePollingDept);
            if (busiMcuPlcTemplatePollingDept.getDeptId() > 0) {
                m.put("deptName", SysDeptCache.getInstance().get(busiMcuPlcTemplatePollingDept.getDeptId()).getDeptName());
                pds.add(m);
            } else {
                ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                viewTemplateConferenceCon.setUpCascadeId(busiMcuPlcTemplateConference.getId());
                viewTemplateConferenceCon.setUpCascadeMcuType(McuType.MCU_PLC.getCode());
                viewTemplateConferenceCon.setUpCascadeIndex(-busiMcuPlcTemplatePollingDept.getDeptId().intValue());
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
     * @param busiMcuPlcTemplatePollingScheme 轮询方案
     * @return 轮询方案
     */
    @Override
    public List<BusiMcuPlcTemplatePollingScheme> selectBusiMcuPlcTemplatePollingSchemeList(BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme)
    {
        return busiMcuPlcTemplatePollingSchemeMapper.selectBusiMcuPlcTemplatePollingSchemeList(busiMcuPlcTemplatePollingScheme);
    }

    /**
     * 新增轮询方案
     * 
     * @param busiMcuPlcTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int insertBusiMcuPlcTemplatePollingScheme(BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme
            , List<BusiMcuPlcTemplatePollingDept> templatePollingDepts
            , List<BusiMcuPlcTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getLayout(), "布局不能为空");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiMcuPlcTemplatePollingScheme.getPollingStrategy());
        busiMcuPlcTemplatePollingScheme.setCreateTime(new Date());
        int c = busiMcuPlcTemplatePollingSchemeMapper.insertBusiMcuPlcTemplatePollingScheme(busiMcuPlcTemplatePollingScheme);
        if (c > 0)
        {
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiMcuPlcTemplatePollingDept busiMcuPlcTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiMcuPlcTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiMcuPlcTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiMcuPlcTemplateDept.setTemplateConferenceId(busiMcuPlcTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuPlcTemplateDept.setCreateTime(new Date());
                    busiMcuPlcTemplateDept.setPollingSchemeId(busiMcuPlcTemplatePollingScheme.getId());
                    busiMcuPlcTemplatePollingDeptMapper.insertBusiMcuPlcTemplatePollingDept(busiMcuPlcTemplateDept);
                }
                
                Set<String> attendeeIds = new HashSet<>();
                
                // 添加模板与会者顺序信息
                for (BusiMcuPlcTemplatePollingPaticipant busiMcuPlcTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiMcuPlcTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiMcuPlcTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    Assert.isTrue(attendeeIds.add(busiMcuPlcTemplateParticipant.getAttendeeId()), "轮询方案参会者“ID”不能重复！");
                    busiMcuPlcTemplateParticipant.setTemplateConferenceId(busiMcuPlcTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuPlcTemplateParticipant.setPollingSchemeId(busiMcuPlcTemplatePollingScheme.getId());
                    busiMcuPlcTemplateParticipant.setCreateTime(new Date());
                    busiMcuPlcTemplatePollingPaticipantMapper.insertBusiMcuPlcTemplatePollingPaticipant(busiMcuPlcTemplateParticipant);
                }
            }
        }
        return c;
    }

    /**
     * 修改轮询方案
     * 
     * @param busiMcuPlcTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int updateBusiMcuPlcTemplatePollingScheme(BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme
            , List<BusiMcuPlcTemplatePollingDept> templatePollingDepts
            , List<BusiMcuPlcTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getLayout(), "分频不能为空");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiMcuPlcTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiMcuPlcTemplatePollingScheme.getPollingStrategy());
        busiMcuPlcTemplatePollingScheme.setUpdateTime(new Date());
        int c = busiMcuPlcTemplatePollingSchemeMapper.updateBusiMcuPlcTemplatePollingScheme(busiMcuPlcTemplatePollingScheme);
        if (c > 0)
        {
            // 删除轮询方案对应的子表信息
            busiMcuPlcTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(busiMcuPlcTemplatePollingScheme.getId());
            busiMcuPlcTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(busiMcuPlcTemplatePollingScheme.getId());
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiMcuPlcTemplatePollingDept busiMcuPlcTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiMcuPlcTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiMcuPlcTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiMcuPlcTemplateDept.setTemplateConferenceId(busiMcuPlcTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuPlcTemplateDept.setCreateTime(new Date());
                    busiMcuPlcTemplateDept.setPollingSchemeId(busiMcuPlcTemplatePollingScheme.getId());
                    busiMcuPlcTemplatePollingDeptMapper.insertBusiMcuPlcTemplatePollingDept(busiMcuPlcTemplateDept);
                }
                
                // 添加模板与会者顺序信息
                for (BusiMcuPlcTemplatePollingPaticipant busiMcuPlcTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiMcuPlcTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiMcuPlcTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    busiMcuPlcTemplateParticipant.setTemplateConferenceId(busiMcuPlcTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuPlcTemplateParticipant.setCreateTime(new Date());
                    busiMcuPlcTemplateParticipant.setPollingSchemeId(busiMcuPlcTemplatePollingScheme.getId());
                    busiMcuPlcTemplatePollingPaticipantMapper.insertBusiMcuPlcTemplatePollingPaticipant(busiMcuPlcTemplateParticipant);
                }
            }
            
            updateMemery(busiMcuPlcTemplatePollingScheme);
        }
        
        return c;
    }

    private void updateMemery(BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme)
    {
        busiMcuPlcTemplatePollingScheme = busiMcuPlcTemplatePollingSchemeMapper.selectBusiMcuPlcTemplatePollingSchemeById(busiMcuPlcTemplatePollingScheme.getId());
        if (YesOrNo.convert(busiMcuPlcTemplatePollingScheme.getEnableStatus()).getBoolean())
        {
            BusiMcuPlcTemplateConference bc = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiMcuPlcTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(bc.getId(), McuType.MCU_PLC.getCode()));
                if (conferenceContext != null)
                {
                    // TODO
                }
            }
        }
    }
    
    public int updateBusiMcuPlcTemplatePollingSchemes(List<BusiMcuPlcTemplatePollingScheme> templatePollingSchemes)
    {
        if (ObjectUtils.isEmpty(templatePollingSchemes))
        {
            return 0;
        }
        BusiMcuPlcTemplatePollingScheme open = null; 
        for (BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme : templatePollingSchemes)
        {
            busiMcuPlcTemplatePollingScheme.setUpdateTime(new Date());
            busiMcuPlcTemplatePollingSchemeMapper.updateBusiMcuPlcTemplatePollingScheme(busiMcuPlcTemplatePollingScheme);
            if (YesOrNo.convert(busiMcuPlcTemplatePollingScheme.getEnableStatus()).getBoolean())
            {
                open = busiMcuPlcTemplatePollingScheme;
            }
        }
        
        if (open != null)
        {
            updateMemery(open);
        }
        else
        {
            BusiMcuPlcTemplatePollingScheme busiMcuPlcTemplatePollingScheme = busiMcuPlcTemplatePollingSchemeMapper.selectBusiMcuPlcTemplatePollingSchemeById(templatePollingSchemes.get(0).getId());
            BusiMcuPlcTemplateConference bc = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(busiMcuPlcTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(bc.getId(), McuType.MCU_PLC.getCode()));
                
                if (conferenceContext != null)
                {
                    // 停止轮询
                    // TODO
                }
            }
        }
        return 1;
    }

    public PollingScheme convert(BusiMcuPlcTemplatePollingScheme ps, McuPlcConferenceContext conferenceContext)
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
        
        BusiMcuPlcTemplatePollingDept con = new BusiMcuPlcTemplatePollingDept();
        con.setPollingSchemeId(ps.getId());
        List<BusiMcuPlcTemplatePollingDept> pollingDepts = busiMcuPlcTemplatePollingDeptMapper.selectBusiMcuPlcTemplatePollingDeptList(con);
        for (BusiMcuPlcTemplatePollingDept busiMcuPlcTemplatePollingDept : pollingDepts)
        {
            DeptPollingAttendees deptPollingAttendees = new DeptPollingAttendees();
            deptPollingAttendees.setDeptId(busiMcuPlcTemplatePollingDept.getDeptId());
            deptPollingAttendees.setWeight(busiMcuPlcTemplatePollingDept.getWeight());
            pollingScheme.addDeptPollingAttendees(deptPollingAttendees);
        }
        
        if (pollingScheme.getPollingStrategy() != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingScheme.getPollingStrategy())
        {
            BusiMcuPlcTemplatePollingPaticipant con1 = new BusiMcuPlcTemplatePollingPaticipant();
            con1.setPollingSchemeId(ps.getId());
            List<BusiMcuPlcTemplatePollingPaticipant> pps = busiMcuPlcTemplatePollingPaticipantMapper.selectBusiMcuPlcTemplatePollingPaticipantList(con1);
            Set<String> invalidPollingAttendeeIds = new HashSet<>();
            Set<String> repeatPollingAttendeeIds = new HashSet<>();
            List<PollingAttendee> pollingAttendeeList = new ArrayList<>();
            for (BusiMcuPlcTemplatePollingPaticipant busiMcuPlcTemplatePollingPaticipant : pps)
            {
                if (busiMcuPlcTemplatePollingPaticipant.getDownCascadeTemplateId() != null) {
                    String downCascadeContextKey = EncryptIdUtil.generateContextKey(busiMcuPlcTemplatePollingPaticipant.getDownCascadeTemplateId(), busiMcuPlcTemplatePollingPaticipant.getDownCascadeMcuType());
                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeContextKey);
                    if (downCascadeConferenceContext != null) {
                        BaseAttendee downCascadeAttendeeTemp = downCascadeConferenceContext.getAttendeeById(busiMcuPlcTemplatePollingPaticipant.getAttendeeId());
                        if (downCascadeAttendeeTemp != null) {
                            Assert.isTrue(repeatPollingAttendeeIds.add(busiMcuPlcTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                            BaseAttendee downCascadeAttendee = new BaseAttendee();
                            BeanUtils.copyProperties(downCascadeAttendeeTemp, downCascadeAttendee);
                            downCascadeAttendee.setDeptId(-downCascadeConferenceContext.getUpCascadeIndex());
                            downCascadeAttendee.setDeptName(downCascadeConferenceContext.getName());
                            PollingAttendee pollingAttendee = new PollingAttendee();
                            pollingAttendee.setDownCascadeAttendee(downCascadeAttendee);
                            pollingAttendee.setInterval(busiMcuPlcTemplatePollingPaticipant.getPollingInterval());
                            pollingAttendee.setWeight(busiMcuPlcTemplatePollingPaticipant.getWeight());
                            pollingAttendeeList.add(pollingAttendee);
                            invalidPollingAttendeeIds.add(downCascadeConferenceContext.getId());
                        }
                    }
                    continue;
                }
                // 根据remoteParty获取参会者
                AttendeeForMcuPlc attendee = conferenceContext.getAttendeeById(busiMcuPlcTemplatePollingPaticipant.getAttendeeId());
                if (attendee != null)
                {
                    Assert.isTrue(repeatPollingAttendeeIds.add(busiMcuPlcTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                    PollingAttendee pollingAttendee = new PollingAttendee();
                    pollingAttendee.setAttendee(attendee);
                    pollingAttendee.setInterval(busiMcuPlcTemplatePollingPaticipant.getPollingInterval());
                    pollingAttendee.setWeight(busiMcuPlcTemplatePollingPaticipant.getWeight());
                    pollingAttendeeList.add(pollingAttendee);
                }
                else
                {
                    invalidPollingAttendeeIds.add(busiMcuPlcTemplatePollingPaticipant.getAttendeeId());
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
                int c = busiMcuPlcTemplatePollingPaticipantMapper.deleteBusiMcuPlcTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
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
    public int deleteBusiMcuPlcTemplatePollingSchemeById(Long id)
    {
        // 删除轮询方案对应的子表信息
        busiMcuPlcTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(id);
        busiMcuPlcTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(id);
        return busiMcuPlcTemplatePollingSchemeMapper.deleteBusiMcuPlcTemplatePollingSchemeById(id);
    }
}
