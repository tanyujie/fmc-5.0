package com.paradisecloud.fcm.fme.attendee.impls;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IBusiTemplatePollingSchemeService;
import com.paradisecloud.fcm.fme.attendee.model.enumer.PollingStrategy;
import com.paradisecloud.fcm.fme.attendee.model.operation.PollingAttendeeOpreationImpl;
import com.paradisecloud.fcm.fme.attendee.model.polling.DeptPollingAttendees;
import com.paradisecloud.fcm.fme.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.fme.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;

import javax.annotation.Resource;

/**
 * 轮询方案Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
@Transactional
@Service
public class BusiTemplatePollingSchemeServiceImpl implements IBusiTemplatePollingSchemeService 
{
    private Logger logger = LoggerFactory.getLogger(BusiTemplatePollingSchemeServiceImpl.class);
    
    @Autowired
    private BusiTemplatePollingSchemeMapper busiTemplatePollingSchemeMapper;
    
    @Autowired
    private BusiTemplatePollingDeptMapper busiTemplatePollingDeptMapper;
    
    @Autowired
    private BusiTemplatePollingPaticipantMapper busiTemplatePollingPaticipantMapper;
    
    @Autowired
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    
    @Autowired
    private BusiTemplateParticipantMapper busiTemplateParticipantMapper;

    @Resource
    private ViewTemplateConferenceMapper viewTemplateConferenceMapper;

    /**
     * 查询轮询方案
     * 
     * @param id 轮询方案ID
     * @return 轮询方案
     */
    @Override
    public ModelBean selectBusiTemplatePollingSchemeById(Long id)
    {
        return buildBusiTemplatePollingScheme(busiTemplatePollingSchemeMapper.selectBusiTemplatePollingSchemeById(id));
    }
    
    private ModelBean buildBusiTemplatePollingScheme(BusiTemplatePollingScheme pollingScheme)
    {
        BusiTemplateConference busiTemplateConference = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(pollingScheme.getTemplateConferenceId());
        BusiTemplatePollingPaticipant busiTemplateParticipant = new BusiTemplatePollingPaticipant();
        busiTemplateParticipant.setPollingSchemeId(pollingScheme.getId());
        List<BusiTemplatePollingPaticipant> ps = busiTemplatePollingPaticipantMapper.selectBusiTemplatePollingPaticipantList(busiTemplateParticipant);
        
        BusiTemplateParticipant con = new BusiTemplateParticipant();
        con.setTemplateConferenceId(pollingScheme.getTemplateConferenceId());
        List<BusiTemplateParticipant> tps = busiTemplateParticipantMapper.selectBusiTemplateParticipantList(con);
        Map<String, BusiTemplateParticipant> tpMap = new HashMap<>();
        for (BusiTemplateParticipant tp : tps)
        {
            tpMap.put(tp.getUuid(), tp);
        }
        
        ConferenceContext cc = null;
        if (busiTemplateConference.getConferenceNumber() != null)
        {
            cc = ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(busiTemplateConference.getId(), McuType.FME.getCode()));
        }
        List<ModelBean> pMbs = new ArrayList<>();
        BusiTemplateParticipant tp = null;
        Attendee a = null;
        Set<String> invalidPollingAttendeeIds = new HashSet<>();
        for (BusiTemplatePollingPaticipant pp : ps)
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
                Attendee mcuAttendee = cc.getAttendeeById(downCascadeConferenceId);
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
            int c = busiTemplatePollingPaticipantMapper.deleteBusiTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
            if (c > 0)
            {
                logger.info("删除无效的轮询会场条数：" + c);
            }
        }
        
        BusiTemplatePollingDept tdCon = new BusiTemplatePollingDept();
        tdCon.setPollingSchemeId(pollingScheme.getId());
        List<BusiTemplatePollingDept> tds = busiTemplatePollingDeptMapper.selectBusiTemplatePollingDeptList(tdCon);
        List<ModelBean> pds = new ArrayList<>();
        for (BusiTemplatePollingDept busiTemplatePollingDept : tds)
        {
            ModelBean m = new ModelBean(busiTemplatePollingDept);
            if (busiTemplatePollingDept.getDeptId() > 0) {
                m.put("deptName", SysDeptCache.getInstance().get(busiTemplatePollingDept.getDeptId()).getDeptName());
                pds.add(m);
            } else {
                ViewTemplateConference viewTemplateConferenceCon = new ViewTemplateConference();
                viewTemplateConferenceCon.setUpCascadeId(busiTemplateConference.getId());
                viewTemplateConferenceCon.setUpCascadeMcuType(McuType.FME.getCode());
                viewTemplateConferenceCon.setUpCascadeIndex(-busiTemplatePollingDept.getDeptId().intValue());
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
     * @param busiTemplatePollingScheme 轮询方案
     * @return 轮询方案
     */
    @Override
    public List<BusiTemplatePollingScheme> selectBusiTemplatePollingSchemeList(BusiTemplatePollingScheme busiTemplatePollingScheme)
    {
        return busiTemplatePollingSchemeMapper.selectBusiTemplatePollingSchemeList(busiTemplatePollingScheme);
    }

    /**
     * 新增轮询方案
     * 
     * @param busiTemplatePollingScheme 轮询方案
     * @return 结果
     */
    @Override
    public int insertBusiTemplatePollingScheme(BusiTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiTemplatePollingDept> templatePollingDepts
            , List<BusiTemplatePollingPaticipant> templatePollingPaticipants)
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
        int c = busiTemplatePollingSchemeMapper.insertBusiTemplatePollingScheme(busiTemplatePollingScheme);
        if (c > 0)
        {
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiTemplatePollingDept busiTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiTemplateDept.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiTemplatePollingDeptMapper.insertBusiTemplatePollingDept(busiTemplateDept);
                }
                
                Set<String> attendeeIds = new HashSet<>();
                
                // 添加模板与会者顺序信息
                for (BusiTemplatePollingPaticipant busiTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    Assert.isTrue(attendeeIds.add(busiTemplateParticipant.getAttendeeId()), "轮询方案参会者“ID”不能重复！");
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateParticipant.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplatePollingPaticipantMapper.insertBusiTemplatePollingPaticipant(busiTemplateParticipant);
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
    public int updateBusiTemplatePollingScheme(BusiTemplatePollingScheme busiTemplatePollingScheme
            , List<BusiTemplatePollingDept> templatePollingDepts
            , List<BusiTemplatePollingPaticipant> templatePollingPaticipants)
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
        int c = busiTemplatePollingSchemeMapper.updateBusiTemplatePollingScheme(busiTemplatePollingScheme);
        if (c > 0)
        {
            // 删除轮询方案对应的子表信息
            busiTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(busiTemplatePollingScheme.getId());
            busiTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(busiTemplatePollingScheme.getId());
            if (pollingStrategy != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingStrategy)
            {
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingDepts), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定组织架构至少有一个！");
                Assert.isTrue(!ObjectUtils.isEmpty(templatePollingPaticipants), "当轮询策略为非“全局”和“全局+组织架构优先”时，轮询方案需选定的参会至少有一个！");
                
                // 添加模板部门顺序信息
                for (BusiTemplatePollingDept busiTemplateDept : templatePollingDepts)
                {
                    Assert.notNull(busiTemplateDept.getWeight(), "轮询方案部门权重不能为空！");
                    Assert.notNull(busiTemplateDept.getDeptId(), "轮询方案部门ID不能为空！");
                    busiTemplateDept.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateDept.setCreateTime(new Date());
                    busiTemplateDept.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiTemplatePollingDeptMapper.insertBusiTemplatePollingDept(busiTemplateDept);
                }
                
                // 添加模板与会者顺序信息
                for (BusiTemplatePollingPaticipant busiTemplateParticipant : templatePollingPaticipants)
                {
                    Assert.notNull(busiTemplateParticipant.getWeight(), "轮询方案参会者权重不能为空！");
                    Assert.notNull(busiTemplateParticipant.getAttendeeId(), "轮询方案参会者“ID”不能为空！");
                    busiTemplateParticipant.setTemplateConferenceId(busiTemplatePollingScheme.getTemplateConferenceId());
                    busiTemplateParticipant.setCreateTime(new Date());
                    busiTemplateParticipant.setPollingSchemeId(busiTemplatePollingScheme.getId());
                    busiTemplatePollingPaticipantMapper.insertBusiTemplatePollingPaticipant(busiTemplateParticipant);
                }
            }
            
            updateMemery(busiTemplatePollingScheme);
        }
        
        return c;
    }

    private void updateMemery(BusiTemplatePollingScheme busiTemplatePollingScheme)
    {
        busiTemplatePollingScheme = busiTemplatePollingSchemeMapper.selectBusiTemplatePollingSchemeById(busiTemplatePollingScheme.getId());
        if (YesOrNo.convert(busiTemplatePollingScheme.getEnableStatus()).getBoolean())
        {
            BusiTemplateConference bc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(bc.getId(), McuType.FME.getCode()));
                if (conferenceContext != null && conferenceContext.getAttendeeOperation() instanceof PollingAttendeeOpreationImpl)
                {
                    PollingAttendeeOpreationImpl pollingAttendeeOpreationImpl = (PollingAttendeeOpreationImpl) conferenceContext.getAttendeeOperation();
                    if (pollingAttendeeOpreationImpl.getPollingScheme().getId() == busiTemplatePollingScheme.getId().longValue())
                    {
                        PollingScheme pc = convert(busiTemplatePollingScheme, conferenceContext);
                        pollingAttendeeOpreationImpl.update(pc);
                    }
                }
            }
        }
    }
    
    public int updateBusiTemplatePollingSchemes(List<BusiTemplatePollingScheme> templatePollingSchemes)
    {
        if (ObjectUtils.isEmpty(templatePollingSchemes))
        {
            return 0;
        }
        BusiTemplatePollingScheme open = null; 
        for (BusiTemplatePollingScheme busiTemplatePollingScheme : templatePollingSchemes)
        {
            busiTemplatePollingScheme.setUpdateTime(new Date());
            busiTemplatePollingSchemeMapper.updateBusiTemplatePollingScheme(busiTemplatePollingScheme);
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
            BusiTemplatePollingScheme busiTemplatePollingScheme = busiTemplatePollingSchemeMapper.selectBusiTemplatePollingSchemeById(templatePollingSchemes.get(0).getId());
            BusiTemplateConference bc = busiTemplateConferenceMapper.selectBusiTemplateConferenceById(busiTemplatePollingScheme.getTemplateConferenceId());
            if (bc.getConferenceNumber() != null)
            {
                ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(EncryptIdUtil.generateKey(bc.getId(), McuType.FME.getCode()));
                
                if (conferenceContext != null)
                {
                    // 停止轮询
                    BeanFactory.getBean(IAttendeeService.class).cancelCurrentOperation(conferenceContext);
                }
            }
        }
        return 1;
    }

    public PollingScheme convert(BusiTemplatePollingScheme ps, ConferenceContext conferenceContext)
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
        
        BusiTemplatePollingDept con = new BusiTemplatePollingDept();
        con.setPollingSchemeId(ps.getId());
        List<BusiTemplatePollingDept> pollingDepts = busiTemplatePollingDeptMapper.selectBusiTemplatePollingDeptList(con);
        for (BusiTemplatePollingDept busiTemplatePollingDept : pollingDepts)
        {
            DeptPollingAttendees deptPollingAttendees = new DeptPollingAttendees();
            deptPollingAttendees.setDeptId(busiTemplatePollingDept.getDeptId());
            deptPollingAttendees.setWeight(busiTemplatePollingDept.getWeight());
            pollingScheme.addDeptPollingAttendees(deptPollingAttendees);
        }
        
        if (pollingScheme.getPollingStrategy() != PollingStrategy.GLOBAL && PollingStrategy.GLOBAL_AND_DEPT_FIRST != pollingScheme.getPollingStrategy())
        {
            BusiTemplatePollingPaticipant con1 = new BusiTemplatePollingPaticipant();
            con1.setPollingSchemeId(ps.getId());
            List<BusiTemplatePollingPaticipant> pps = busiTemplatePollingPaticipantMapper.selectBusiTemplatePollingPaticipantList(con1);
            Set<String> invalidPollingAttendeeIds = new HashSet<>();
            Set<String> repeatPollingAttendeeIds = new HashSet<>();
            List<PollingAttendee> pollingAttendeeList = new ArrayList<>();
            for (BusiTemplatePollingPaticipant busiTemplatePollingPaticipant : pps)
            {
                if (busiTemplatePollingPaticipant.getDownCascadeTemplateId() != null) {
                    String downCascadeContextKey = EncryptIdUtil.generateContextKey(busiTemplatePollingPaticipant.getDownCascadeTemplateId(), busiTemplatePollingPaticipant.getDownCascadeMcuType());
                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeContextKey);
                    if (downCascadeConferenceContext != null) {
                        BaseAttendee downCascadeAttendeeTemp = downCascadeConferenceContext.getAttendeeById(busiTemplatePollingPaticipant.getAttendeeId());
                        if (downCascadeAttendeeTemp != null) {
                            Assert.isTrue(repeatPollingAttendeeIds.add(busiTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                            BaseAttendee downCascadeAttendee = new BaseAttendee();
                            BeanUtils.copyProperties(downCascadeAttendeeTemp, downCascadeAttendee);
                            downCascadeAttendee.setDeptId(-downCascadeConferenceContext.getUpCascadeIndex());
                            downCascadeAttendee.setDeptName(downCascadeConferenceContext.getName());
                            PollingAttendee pollingAttendee = new PollingAttendee();
                            pollingAttendee.setDownCascadeAttendee(downCascadeAttendee);
                            pollingAttendee.setInterval(busiTemplatePollingPaticipant.getPollingInterval());
                            pollingAttendee.setWeight(busiTemplatePollingPaticipant.getWeight());
                            pollingAttendeeList.add(pollingAttendee);
                            invalidPollingAttendeeIds.add(downCascadeConferenceContext.getId());
                        }
                    }
                    continue;
                }
                // 根据remoteParty获取参会者
                Attendee attendee = conferenceContext.getAttendeeById(busiTemplatePollingPaticipant.getAttendeeId());
                if (attendee != null)
                {
                    Assert.isTrue(repeatPollingAttendeeIds.add(busiTemplatePollingPaticipant.getAttendeeId()), "轮询方案中，轮询参会存在重复，请重新编辑！");
                    PollingAttendee pollingAttendee = new PollingAttendee();
                    pollingAttendee.setAttendee(attendee);
                    pollingAttendee.setInterval(busiTemplatePollingPaticipant.getPollingInterval());
                    pollingAttendee.setWeight(busiTemplatePollingPaticipant.getWeight());
                    pollingAttendeeList.add(pollingAttendee);
                }
                else
                {
                    invalidPollingAttendeeIds.add(busiTemplatePollingPaticipant.getAttendeeId());
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
                int c = busiTemplatePollingPaticipantMapper.deleteBusiTemplatePollingPaticipantByAttendeeIds(invalidPollingAttendeeIds.toArray(new String[invalidPollingAttendeeIds.size()]));
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
        busiTemplatePollingDeptMapper.deletePollingDeptByPollingSchemeId(id);
        busiTemplatePollingPaticipantMapper.deletePollingPaticipantByPollingSchemeId(id);
        return busiTemplatePollingSchemeMapper.deleteBusiTemplatePollingSchemeById(id);
    }
}
