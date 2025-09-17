package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjTemplatePollingSchemeService;
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
public class BusiMcuZjTemplatePollingSchemeServiceImpl implements IBusiMcuZjTemplatePollingSchemeService 
{
    private Logger logger = LoggerFactory.getLogger(BusiMcuZjTemplatePollingSchemeServiceImpl.class);
    
    @Resource
    private BusiMcuZjTemplatePollingSchemeMapper busiMcuZjTemplatePollingSchemeMapper;
    
    @Resource
    private BusiMcuZjTemplatePollingDeptMapper busiMcuZjTemplatePollingDeptMapper;
    
    @Resource
    private BusiMcuZjTemplatePollingPaticipantMapper busiMcuZjTemplatePollingPaticipantMapper;
    
    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
    
    @Resource
    private BusiMcuZjTemplateParticipantMapper busiMcuZjTemplateParticipantMapper;

    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    @Override
    public ModelBean selectBusiMcuZjTemplatePollingSchemeById(Long id)
    {
        return buildBusiMcuZjTemplatePollingScheme(busiMcuZjTemplatePollingSchemeMapper.selectBusiMcuZjTemplatePollingSchemeById(id));
    }
    
    private ModelBean buildBusiMcuZjTemplatePollingScheme(BusiMcuZjTemplatePollingScheme pollingScheme)
    {
        BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(pollingScheme.getTemplateConferenceId());
        BusiMcuZjTemplatePollingPaticipant busiMcuZjTemplateParticipant = new BusiMcuZjTemplatePollingPaticipant();
        busiMcuZjTemplateParticipant.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuZjTemplatePollingPaticipant> ps = busiMcuZjTemplatePollingPaticipantMapper.selectBusiMcuZjTemplatePollingPaticipantList(busiMcuZjTemplateParticipant);
        
        BusiMcuZjTemplateParticipant con = new BusiMcuZjTemplateParticipant();
        con.setTemplateConferenceId(pollingScheme.getTemplateConferenceId());
        List<BusiMcuZjTemplateParticipant> tps = busiMcuZjTemplateParticipantMapper.selectBusiMcuZjTemplateParticipantList(con);
        Map<String, BusiMcuZjTemplateParticipant> tpMap = new HashMap<>();
        for (BusiMcuZjTemplateParticipant tp : tps)
        {
            tpMap.put(tp.getUuid(), tp);
        }
        
        McuZjConferenceContext cc = null;
        if (busiMcuZjTemplateConference.getConferenceNumber() != null)
        {
            cc = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuZjTemplateConference.getId(), McuType.MCU_ZJ.getCode()));
        }
        List<ModelBean> pMbs = new ArrayList<>();
        BusiMcuZjTemplateParticipant tp = null;
        AttendeeForMcuZj a = null;
        Set<String> invalidPollingAttendeeIds = new HashSet<>();
        for (BusiMcuZjTemplatePollingPaticipant pp : ps)
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
                AttendeeForMcuZj mcuAttendee = cc.getAttendeeById(downCascadeConferenceId);
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
            int c = busiMcuZjTemplatePollingPaticipantMapper.deleteBusiMcuZjTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
            if (c > 0)
            {
                logger.info("删除无效的轮询会场条数：" + c);
            }
        }
        
        BusiMcuZjTemplatePollingDept tdCon = new BusiMcuZjTemplatePollingDept();
        tdCon.setPollingSchemeId(pollingScheme.getId());
        List<BusiMcuZjTemplatePollingDept> tds = busiMcuZjTemplatePollingDeptMapper.selectBusiMcuZjTemplatePollingDeptList(tdCon);
        List<ModelBean> pds = new ArrayList<>();
        for (BusiMcuZjTemplatePollingDept busiMcuZjTemplatePollingDept : tds)
        {
            ModelBean m = new ModelBean(busiMcuZjTemplatePollingDept);
            if (busiMcuZjTemplatePollingDept.getDeptId() > 0) {
                m.put("deptName", SysDeptCache.getInstance().get(busiMcuZjTemplatePollingDept.getDeptId()).getDeptName());
                pds.add(m);
            } else {
                ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                viewTemplateConferenceCon.setUpCascadeId(busiMcuZjTemplateConference.getId());
                viewTemplateConferenceCon.setUpCascadeMcuType(McuType.MCU_ZJ.getCode());
                viewTemplateConferenceCon.setUpCascadeIndex(-busiMcuZjTemplatePollingDept.getDeptId().intValue());
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
     * @param busiMcuZjTemplatePollingScheme 轮询方案
     * @return 轮询方案
     */
    @Override
    public List<BusiMcuZjTemplatePollingScheme> selectBusiMcuZjTemplatePollingSchemeList(BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme)
    {
        return busiMcuZjTemplatePollingSchemeMapper.selectBusiMcuZjTemplatePollingSchemeList(busiMcuZjTemplatePollingScheme);
    }

    /**
     * 新增轮询方案
     * 
     * @param busiMcuZjTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int insertBusiMcuZjTemplatePollingScheme(BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme
            , List<BusiMcuZjTemplatePollingDept> templatePollingDepts
            , List<BusiMcuZjTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiMcuZjTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getLayout(), "布局不能为空");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiMcuZjTemplatePollingScheme.getPollingStrategy());
        busiMcuZjTemplatePollingScheme.setCreateTime(new Date());
        int c = busiMcuZjTemplatePollingSchemeMapper.insertBusiMcuZjTemplatePollingScheme(busiMcuZjTemplatePollingScheme);
        if (c > 0)
        {
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiMcuZjTemplatePollingDept busiMcuZjTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiMcuZjTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiMcuZjTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiMcuZjTemplateDept.setTemplateConferenceId(busiMcuZjTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuZjTemplateDept.setCreateTime(new Date());
                    busiMcuZjTemplateDept.setPollingSchemeId(busiMcuZjTemplatePollingScheme.getId());
                    busiMcuZjTemplatePollingDeptMapper.insertBusiMcuZjTemplatePollingDept(busiMcuZjTemplateDept);
                }
                
                Set<String> attendeeIds = new HashSet<>();
                
                // 添加模板与会者顺序信息
                for (BusiMcuZjTemplatePollingPaticipant busiMcuZjTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiMcuZjTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiMcuZjTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    Assert.isTrue(attendeeIds.add(busiMcuZjTemplateParticipant.getAttendeeId()), "轮询方案参会者“ID”不能重复！");
                    busiMcuZjTemplateParticipant.setTemplateConferenceId(busiMcuZjTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuZjTemplateParticipant.setPollingSchemeId(busiMcuZjTemplatePollingScheme.getId());
                    busiMcuZjTemplateParticipant.setCreateTime(new Date());
                    busiMcuZjTemplatePollingPaticipantMapper.insertBusiMcuZjTemplatePollingPaticipant(busiMcuZjTemplateParticipant);
                }
            }
        }
        return c;
    }

    /**
     * 修改轮询方案
     * 
     * @param busiMcuZjTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int updateBusiMcuZjTemplatePollingScheme(BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme
            , List<BusiMcuZjTemplatePollingDept> templatePollingDepts
            , List<BusiMcuZjTemplatePollingPaticipant> templatePollingPaticipants)
    {
        Assert.notNull(busiMcuZjTemplatePollingScheme.getSchemeName(), "轮询方案名不能为空！");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getIsBroadcast(), "是否广播不能为空！");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getIsDisplaySelf(), "是否显示自己不能为空！");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getIsFill(), "是否补位不能为空！");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getLayout(), "分频不能为空");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getEnableStatus(), "启用状态不能为空");
        Assert.notNull(busiMcuZjTemplatePollingScheme.getPollingStrategy(), "轮询策略不能为空");
        PollingStrategy pollingStrategy = PollingStrategy.convert(busiMcuZjTemplatePollingScheme.getPollingStrategy());
        busiMcuZjTemplatePollingScheme.setUpdateTime(new Date());
        int c = busiMcuZjTemplatePollingSchemeMapper.updateBusiMcuZjTemplatePollingScheme(busiMcuZjTemplatePollingScheme);
        if (c > 0)
        {
            // 删除轮询方案对应的子表信息
            busiMcuZjTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(busiMcuZjTemplatePollingScheme.getId());
            busiMcuZjTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(busiMcuZjTemplatePollingScheme.getId());
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiMcuZjTemplatePollingDept busiMcuZjTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiMcuZjTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiMcuZjTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiMcuZjTemplateDept.setTemplateConferenceId(busiMcuZjTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuZjTemplateDept.setCreateTime(new Date());
                    busiMcuZjTemplateDept.setPollingSchemeId(busiMcuZjTemplatePollingScheme.getId());
                    busiMcuZjTemplatePollingDeptMapper.insertBusiMcuZjTemplatePollingDept(busiMcuZjTemplateDept);
                }
                
                // 添加模板与会者顺序信息
                for (BusiMcuZjTemplatePollingPaticipant busiMcuZjTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiMcuZjTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiMcuZjTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    busiMcuZjTemplateParticipant.setTemplateConferenceId(busiMcuZjTemplatePollingScheme.getTemplateConferenceId());
                    busiMcuZjTemplateParticipant.setCreateTime(new Date());
                    busiMcuZjTemplateParticipant.setPollingSchemeId(busiMcuZjTemplatePollingScheme.getId());
                    busiMcuZjTemplatePollingPaticipantMapper.insertBusiMcuZjTemplatePollingPaticipant(busiMcuZjTemplateParticipant);
                }
            }
            
            updateMemery(busiMcuZjTemplatePollingScheme);
        }
        
        return c;
    }

    private void updateMemery(BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme)
    {
        busiMcuZjTemplatePollingScheme = busiMcuZjTemplatePollingSchemeMapper.selectBusiMcuZjTemplatePollingSchemeById(busiMcuZjTemplatePollingScheme.getId());
        if (YesOrNo.convert(busiMcuZjTemplatePollingScheme.getEnableStatus()).getBoolean())
        {
            BusiMcuZjTemplateConference bc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiMcuZjTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateConferenceId(bc.getId(), McuType.MCU_ZJ.getCode()));
                if (conferenceContext != null)
                {
                    // TODO
                }
            }
        }
    }
    
    public int updateBusiMcuZjTemplatePollingSchemes(List<BusiMcuZjTemplatePollingScheme> templatePollingSchemes)
    {
        if (ObjectUtils.isEmpty(templatePollingSchemes))
        {
            return 0;
        }
        BusiMcuZjTemplatePollingScheme open = null; 
        for (BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme : templatePollingSchemes)
        {
            busiMcuZjTemplatePollingScheme.setUpdateTime(new Date());
            busiMcuZjTemplatePollingSchemeMapper.updateBusiMcuZjTemplatePollingScheme(busiMcuZjTemplatePollingScheme);
            if (YesOrNo.convert(busiMcuZjTemplatePollingScheme.getEnableStatus()).getBoolean())
            {
                open = busiMcuZjTemplatePollingScheme;
            }
        }
        
        if (open != null)
        {
            updateMemery(open);
        }
        else
        {
            BusiMcuZjTemplatePollingScheme busiMcuZjTemplatePollingScheme = busiMcuZjTemplatePollingSchemeMapper.selectBusiMcuZjTemplatePollingSchemeById(templatePollingSchemes.get(0).getId());
            BusiMcuZjTemplateConference bc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(busiMcuZjTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateConferenceId(bc.getId(), McuType.MCU_ZJ.getCode()));
                
                if (conferenceContext != null)
                {
                    // 停止轮询
                    // TODO
                }
            }
        }
        return 1;
    }

    public PollingScheme convert(BusiMcuZjTemplatePollingScheme ps, McuZjConferenceContext conferenceContext)
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
        
        BusiMcuZjTemplatePollingDept con = new BusiMcuZjTemplatePollingDept();
        con.setPollingSchemeId(ps.getId());
        List<BusiMcuZjTemplatePollingDept> pollingDepts = busiMcuZjTemplatePollingDeptMapper.selectBusiMcuZjTemplatePollingDeptList(con);
        for (BusiMcuZjTemplatePollingDept busiMcuZjTemplatePollingDept : pollingDepts)
        {
            DeptPollingAttendees deptPollingAttendees = new DeptPollingAttendees();
            deptPollingAttendees.setDeptId(busiMcuZjTemplatePollingDept.getDeptId());
            deptPollingAttendees.setWeight(busiMcuZjTemplatePollingDept.getWeight());
            pollingScheme.addDeptPollingAttendees(deptPollingAttendees);
        }
        
        if (pollingScheme.getPollingStrategy() != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingScheme.getPollingStrategy())
        {
            BusiMcuZjTemplatePollingPaticipant con1 = new BusiMcuZjTemplatePollingPaticipant();
            con1.setPollingSchemeId(ps.getId());
            List<BusiMcuZjTemplatePollingPaticipant> pps = busiMcuZjTemplatePollingPaticipantMapper.selectBusiMcuZjTemplatePollingPaticipantList(con1);
            Set<String> invalidPollingAttendeeIds = new HashSet<>();
            Set<String> repeatPollingAttendeeIds = new HashSet<>();
            List<PollingAttendee> pollingAttendeeList = new ArrayList<>();
            for (BusiMcuZjTemplatePollingPaticipant busiMcuZjTemplatePollingPaticipant : pps)
            {
                if (busiMcuZjTemplatePollingPaticipant.getDownCascadeTemplateId() != null) {
                    String downCascadeContextKey = EncryptIdUtil.generateContextKey(busiMcuZjTemplatePollingPaticipant.getDownCascadeTemplateId(), busiMcuZjTemplatePollingPaticipant.getDownCascadeMcuType());
                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeContextKey);
                    if (downCascadeConferenceContext != null) {
                        BaseAttendee downCascadeAttendeeTemp = downCascadeConferenceContext.getAttendeeById(busiMcuZjTemplatePollingPaticipant.getAttendeeId());
                        if (downCascadeAttendeeTemp != null) {
                            Assert.isTrue(repeatPollingAttendeeIds.add(busiMcuZjTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                            BaseAttendee downCascadeAttendee = new BaseAttendee();
                            BeanUtils.copyProperties(downCascadeAttendeeTemp, downCascadeAttendee);
                            downCascadeAttendee.setDeptId(-downCascadeConferenceContext.getUpCascadeIndex());
                            downCascadeAttendee.setDeptName(downCascadeConferenceContext.getName());
                            PollingAttendee pollingAttendee = new PollingAttendee();
                            pollingAttendee.setDownCascadeAttendee(downCascadeAttendee);
                            pollingAttendee.setInterval(busiMcuZjTemplatePollingPaticipant.getPollingInterval());
                            pollingAttendee.setWeight(busiMcuZjTemplatePollingPaticipant.getWeight());
                            pollingAttendeeList.add(pollingAttendee);
                            invalidPollingAttendeeIds.add(downCascadeConferenceContext.getId());
                        }
                    }
                    continue;
                }
                // 根据remoteParty获取参会者
                AttendeeForMcuZj attendee = conferenceContext.getAttendeeById(busiMcuZjTemplatePollingPaticipant.getAttendeeId());
                if (attendee != null)
                {
                    Assert.isTrue(repeatPollingAttendeeIds.add(busiMcuZjTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                    PollingAttendee pollingAttendee = new PollingAttendee();
                    pollingAttendee.setAttendee(attendee);
                    pollingAttendee.setInterval(busiMcuZjTemplatePollingPaticipant.getPollingInterval());
                    pollingAttendee.setWeight(busiMcuZjTemplatePollingPaticipant.getWeight());
                    pollingAttendeeList.add(pollingAttendee);
                }
                else
                {
                    invalidPollingAttendeeIds.add(busiMcuZjTemplatePollingPaticipant.getAttendeeId());
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
                int c = busiMcuZjTemplatePollingPaticipantMapper.deleteBusiMcuZjTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
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
    public int deleteBusiMcuZjTemplatePollingSchemeById(Long id)
    {
        // 删除轮询方案对应的子表信息
        busiMcuZjTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(id);
        busiMcuZjTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(id);
        return busiMcuZjTemplatePollingSchemeMapper.deleteBusiMcuZjTemplatePollingSchemeById(id);
    }
}
