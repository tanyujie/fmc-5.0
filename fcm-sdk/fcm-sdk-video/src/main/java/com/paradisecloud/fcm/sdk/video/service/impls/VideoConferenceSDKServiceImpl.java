/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : VideoConferenceSDKServiceImpl.java
 * Package     : com.paradiscloud.fcm.videosdk.service.impls
 * @author sinhy 
 * @since 2021-10-28 10:58
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.sdk.video.service.impls;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceAppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.RaiseHandStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplatePollingSchemeMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.dao.model.BusiTemplatePollingScheme;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.fme.attendee.model.operation.DiscussAttendeeOperation;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.sdk.video.service.interfaces.IVideoConferenceSDKService;
import com.sinhy.exception.SystemException;
import com.sinhy.model.GenericValue;

import javax.annotation.Resource;

@Service
public class VideoConferenceSDKServiceImpl implements IVideoConferenceSDKService
{
    
    @Resource
    private BusiTerminalMapper busiTerminalMapper;
    
    @Resource
    private BusiConferenceAppointmentMapper busiConferenceAppointmentMapper;
    
    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;
    
    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;
    
    @Resource
    private IAttendeeService attendeeService;
    
    @Resource
    private IBusiConferenceService busiConferenceService;
    
    @Resource
    private BusiTemplatePollingSchemeMapper busiTemplatePollingSchemeMapper;

    @Resource
    private IBusiConferenceAppointmentService busiConferenceAppointmentService;
    
    @Override
    public RestResponse callTheRoll(ConferenceContext conferenceContext, int number)
    {
        Assert.isTrue(conferenceContext != null, "非法会议号：" + conferenceContext);
        
        AtomicInteger ai = new AtomicInteger();
        GenericValue<Attendee> val = new GenericValue<>();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(conferenceContext, (a) -> {
            if (a.isMeetingJoined() && ai.incrementAndGet() == number)
            {
                val.setValue(a);
            }
        });
        
        if (val.getValue() == null)
        {
            return RestResponse.fail("找不到编号为“"+number+"”的在线参会！");
        }
        attendeeService.callTheRoll(conferenceContext.getId(), val.getValue().getId());
        return RestResponse.success();
    }
    
    @Override
    public RestResponse talk(ConferenceContext conferenceContext, int number)
    {
        Assert.isTrue(conferenceContext != null, "非法会议号：" + conferenceContext);
        
        AtomicInteger ai = new AtomicInteger();
        GenericValue<Attendee> val = new GenericValue<>();
        ConferenceContextUtils.eachNonFmeAttendeeInConference(conferenceContext, (a) -> {
            if (a.isMeetingJoined() && ai.incrementAndGet() == number)
            {
                val.setValue(a);
            }
        });
        
        if (val.getValue() == null)
        {
            return RestResponse.fail("找不到编号为“"+number+"”的在线参会！");
        }
        attendeeService.talk(conferenceContext.getId(), val.getValue().getId());
        return RestResponse.success();
    }

    @Override
    public RestResponse displayLayoutGB(ConferenceContext conferenceContext, String layout, int mode)
    {
        Assert.isTrue(conferenceContext != null, "非法会议号：" + conferenceContext);
        
        DefaultAttendeeOperation defaultViewOperation = (DefaultAttendeeOperation)conferenceContext.getDefaultViewOperation();
        defaultViewOperation.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(mode).getValue());
        defaultViewOperation.setDefaultViewIsBroadcast(YesOrNo.YES.getValue());
        defaultViewOperation.setDefaultViewLayout(layout);
        defaultViewOperation.initSplitScreen();
        return backToDisplayLayout(conferenceContext);
    }

    @Override
    public RestResponse displayLayoutXK(ConferenceContext conferenceContext, String layout, int mode)
    {
        Assert.isTrue(conferenceContext != null, "非法会议号：" + conferenceContext);
        
        DefaultAttendeeOperation defaultViewOperation = (DefaultAttendeeOperation)conferenceContext.getDefaultViewOperation();
        defaultViewOperation.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.convert(mode).getValue());
        defaultViewOperation.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
        defaultViewOperation.setDefaultViewLayout(layout);
        defaultViewOperation.initSplitScreen();
        return backToDisplayLayout(conferenceContext);
    }

    @Override
    public RestResponse pollingGB(ConferenceContext conferenceContext, String layout, int mode)
    {
        Assert.isTrue(conferenceContext != null, "非法会议号：" + conferenceContext);
        BusiTemplatePollingScheme ps = getPollingScheme(conferenceContext);
        PanePlacementSelfPaneMode.convert(mode);
        ps.setIsBroadcast(YesOrNo.YES.getValue());
        ps.setIsDisplaySelf(mode);
        ps.setLayout(layout);
        busiTemplatePollingSchemeMapper.updateBusiTemplatePollingScheme(ps);
        attendeeService.polling(conferenceContext.getId());
        return RestResponse.success();
    }

    /**
     * 获取轮询方案
     * @author sinhy
     * @since 2021-11-24 17:59 
     * @param conferenceContext
     * @return BusiTemplatePollingScheme
     */
    private BusiTemplatePollingScheme getPollingScheme(ConferenceContext conferenceContext)
    {
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined())
        {
            throw new SystemException(1005454, "主会场未设置，无法进行轮询操作！");
        }
        
        BusiTemplatePollingScheme con0 = new BusiTemplatePollingScheme();
        con0.setTemplateConferenceId(conferenceContext.getTemplateConferenceId());
        con0.setEnableStatus(YesOrNo.YES.getValue());
        List<BusiTemplatePollingScheme> pss = busiTemplatePollingSchemeMapper.selectBusiTemplatePollingSchemeList(con0);
        BusiTemplatePollingScheme ps = null;
        for (BusiTemplatePollingScheme busiTemplatePollingScheme : pss)
        {
            if (ps == null || ps.getWeight() < busiTemplatePollingScheme.getWeight())
            {
                ps = busiTemplatePollingScheme;
            }
        }
        if (ps == null)
        {
            throw new SystemException(1008243, "您当前还未配置已启用的轮询方案，请先配置轮询方案，再点开始轮询！");
        }
        return ps;
    }

    @Override
    public RestResponse pollingXK(ConferenceContext conferenceContext, String layout, int mode)
    {
        BusiTemplatePollingScheme ps = getPollingScheme(conferenceContext);
        PanePlacementSelfPaneMode.convert(mode);
        ps.setIsBroadcast(YesOrNo.NO.getValue());
        ps.setIsDisplaySelf(mode);
        ps.setLayout(layout);
        busiTemplatePollingSchemeMapper.updateBusiTemplatePollingScheme(ps);
        attendeeService.polling(conferenceContext.getId());
        return RestResponse.success();
    }

    @Override
    public RestResponse discuss(ConferenceContext conferenceContext)
    {
        Assert.isTrue(conferenceContext != null, "非法会议号：" + conferenceContext);
        if (!(conferenceContext.getAttendeeOperation() instanceof DiscussAttendeeOperation))
        {
            busiConferenceService.discuss(conferenceContext.getId());
        }
        return RestResponse.success();
    }

    @Override
    public RestResponse backToDisplayLayout(ConferenceContext conferenceContext)
    {
        Assert.isTrue(conferenceContext != null, "非法会议号：" + conferenceContext);
        if (conferenceContext.getDefaultViewOperation() != conferenceContext.getAttendeeOperation())
        {
            attendeeService.cancelCurrentOperation(conferenceContext);
        }
        return RestResponse.success();
    }
    
    @Override
    public RestResponse raiseHand(ConferenceContext conferenceContext, String attendeeId, RaiseHandStatus raiseHandStatus)
    {
        attendeeService.raiseHand(conferenceContext.getId(), attendeeId, raiseHandStatus);
        return RestResponse.success();
    }

    /**
     * 根据会议号获取会议详情
     * @author sinhy
     * @since 2021-11-23 16:33 
     * @param conferenceNumber
     * @return String
     */
    public String getConferenceInfo(String conferenceNumber)
    {
        Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(conferenceNumber);
        if (conferenceContextList != null && conferenceContextList.size() > 0) {
            for (ConferenceContext conferenceContext : conferenceContextList) {
                return JSON.toJSONString(conferenceContext);
            }
        }
        return null;
    }
    
    @Override
    public String getVideoConference(String sn, Integer page, Integer size)
    {
        Assert.isTrue(!ObjectUtils.isEmpty(sn), "SN不能为空！");
        Assert.isTrue(page != null, "page不能为空！");
        Assert.isTrue(size != null, "size不能为空！");
        PaginationData<BusiConferenceAppointment> pd = new PaginationData<>();
        pd.setPage(page);
        pd.setSize(size);

        BusiTerminal con = new BusiTerminal();
        con.setSn(sn);
        List<BusiTerminal> ts = busiTerminalMapper.selectBusiTerminalList(con);
        if (ObjectUtils.isEmpty(ts))
        {
            return JSON.toJSONString(pd);
        }

        List<Long> terminalIds = new ArrayList<>();
        for (BusiTerminal busiTerminal : ts)
        {
            terminalIds.add(busiTerminal.getId());
        }

        List<BusiConferenceAppointment> cas = new ArrayList<>();
        BusiTemplateConference con1 = new BusiTemplateConference();
        con1.getParams().put("conditionTerminalIds", terminalIds);
        List<BusiTemplateConference> tcs = busiTemplateConferenceMapper.selectAllBusiTemplateConferenceList(con1);
        for (BusiTemplateConference busiTemplateConference : tcs)
        {
            BusiConferenceAppointment con2 = new BusiConferenceAppointment();
            con2.setTemplateId(busiTemplateConference.getId());
            List<BusiConferenceAppointment> is = busiConferenceAppointmentMapper.selectBusiConferenceAppointmentList(con2);
            if (!ObjectUtils.isEmpty(is) && is.size() > 0)
            {
                for (int i = 0; i < is.size(); i++) {
                    ModelBean mb = busiTemplateConferenceService.getTemplateConferenceDetails(busiTemplateConference);
                    List<BusiConferenceAppointment> busiConferenceAppointments = busiConferenceAppointmentService.selectBusiConferenceAppointmentList(is.get(i));
                    BusiConferenceAppointment ca = busiConferenceAppointments.get(0);
                    if (ca.getIsStart() == null){
                        mb.remove("isStart");
                        mb.put("isStart","false");
                    }

                    ca.getParams().put("templateDetails", mb);
                    cas.add(ca);
                }
            }
        }

        if (ObjectUtils.isEmpty(cas))
        {
            return JSON.toJSONString(pd);
        }

        Collections.sort(cas, new Comparator<BusiConferenceAppointment>()
        {
            @Override
            public int compare(BusiConferenceAppointment o1, BusiConferenceAppointment o2)
            {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });

        int fromIndex = page * size;
        int toIndex = fromIndex + size;
        if (toIndex >= cas.size())
        {
            toIndex = cas.size();
        }

        if (fromIndex >= toIndex)
        {
            return JSON.toJSONString(pd);
        }
        pd.setRecords(cas.subList(fromIndex, toIndex));
        return JSON.toJSONString(pd);
    }
    
}
