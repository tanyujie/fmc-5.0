package com.paradisecloud.fcm.cdr.service.core;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.paradisecloud.fcm.cdr.service.core.listener.AllRecordEvent;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IHistoryAllService;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.response.cospace.CoSpaceInfoResponse;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.paradisecloud.fcm.cdr.service.core.cache.CdrCache;
import com.paradisecloud.fcm.cdr.service.core.listener.RecordEvent;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallService;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IHistoryService;
import com.paradisecloud.fcm.cdr.service.model.CallElement;
import com.paradisecloud.fcm.cdr.service.model.RecordElement;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;

/**
 * @author johnson liu
 * @date 2021/5/13 23:13
 */
@Component
public class CallReadStrategy implements XmlReadStrategy<CdrCall>
{
    private static final String MESSAGE_TYPE = "callStart";
    
    private static final Logger logger = LoggerFactory.getLogger(CallReadStrategy.class);
    
    @Resource
    private ICdrCallService cdrCallService;
    @Resource
    private IHistoryService iHistoryService;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private BusiConferenceNumberMapper busiConferenceNumberMapper;
    @Resource
    private IBusiHistoryConferenceService busiHistoryConferenceService;
    @Resource
    private IHistoryAllService iHistoryAllService;
    @Resource
    private IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService;

    @Override
    public CdrCall readToBean(String session, RecordElement recordElement)
    {
        CdrCall cdrCall = new CdrCall();
        // 读取节点属性
        String typeAttribute = recordElement.getType();
        String time = recordElement.getTime();
        Integer recordIndex = recordElement.getRecordIndex();
        Integer correlatorIndex = recordElement.getCorrelatorIndex();
        CallElement call = recordElement.getCall();
        
        Date date = DateUtil.convertDateByString(time);
        cdrCall.setRecordType((MESSAGE_TYPE.equals(typeAttribute)) ? 1 : 0);
        cdrCall.setRecordIndex(recordIndex);
        cdrCall.setCorrelatorIndex(correlatorIndex);
        cdrCall.setTime(date);
        cdrCall.setSession(session);
        
        BeanUtils.copyProperties(call, cdrCall);
        cdrCall.setCdrId(call.getId());
        cdrCall.setCreateTime(new Date());
        return cdrCall;
    }
    
    @Override
    public synchronized void executeAdd(CdrCall cdrCall)
    {
        cdrCallService.insertCdrCall(cdrCall);
    }
    
    @Override
    @Transactional
    public synchronized void executeAdd(String session, RecordElement recordElement, String fmeIp)
    {
        CdrCall cdrCall = readToBean(session, recordElement);
        logger.info("转化JavaBean后:{}", cdrCall);
        if (cdrCall.getRecordType() == 1)
        {
            CdrCache.getInstance().putCdrCallMap(cdrCall.getCdrId(), cdrCall);
        }
        
        // 对新的CDR记录入库
        cdrCallService.insertCdrCall(cdrCall);
        // 全会议记录 start 2022.05.11
        if (ExternalConfigCache.getInstance().isEnableCdrAll()) {
            if (cdrCall.getRecordType() == 1) {
                CoSpace cp = new CoSpace();
                if (cdrCall.getCoSpace() != null) {
                    // 获取会议号
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByBridgeAddressOnly(fmeIp);
                    if (fmeBridge != null) {
                        CoSpaceInfoResponse coSpaceInfoResponse = fmeBridge.getCoSpaceInvoker().getCoSpaceInfo(cdrCall.getCoSpace());
                        if (coSpaceInfoResponse != null && coSpaceInfoResponse.getCoSpace() != null) {
                            String uri = coSpaceInfoResponse.getCoSpace().getUri();
                            cp.setUri(uri);
                        }
                    }
                    // 目前只对应coSpace类型
                    cp.setId(cdrCall.getCoSpace());
                }
            } else if (cdrCall.getRecordType() == 0) {
                BusiHistoryAllCall busiHistoryAllCall = iHistoryAllService.findHistoryCallByCoSpaceAndCall(cdrCall.getCdrId(), cdrCall.getCoSpace());
                if (busiHistoryAllCall != null) {
                    Long historyConferenceId = busiHistoryAllCall.getHistoryConferenceId();
                    List<BusiHistoryAllParticipant> historyParticipantByCoSpaceId = iHistoryAllService.findHistoryParticipantByCoSpaceId(historyConferenceId);
                    int participantNum = CollectionUtils.isEmpty(historyParticipantByCoSpaceId) ? 0 : historyParticipantByCoSpaceId.size();
                    cdrCall.setCoSpace(busiHistoryAllCall.getCoSpace());
                    iHistoryAllService.updateHistoryAllConferenceByCoSpace(historyConferenceId, cdrCall, participantNum);
                    applicationContext.publishEvent(new AllRecordEvent(this, cdrCall, fmeIp));
                }
            } else {
                logger.error("非法call记录类型：" + cdrCall);
            }
        }
        // 全会议记录 end 2022.05.11

        if (cdrCall.getRecordType() == 1)
        {
            CoSpace cp = FmeDataCache.getCoSpaceById(cdrCall.getCoSpace());
            if (cp == null)
            {
                logger.error("CDR-call保存出错，找不到CoSpace对象：" + cdrCall.getCoSpace());
                return;
            }
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().get(cp.getUri());

            Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(cp.getUri());
            if (conferenceContextList != null && conferenceContextList.size() > 0) {
                for (ConferenceContext conferenceContextTemp : conferenceContextList) {
                    if (cp.getId().equals(conferenceContextTemp.getCoSpaceId())) {
                        conferenceContext = conferenceContextTemp;
                        break;
                    }
                }
            }
            if (conferenceContext == null)
            {
                logger.info("CDR-call保存，找不到conferenceContext对象，准备从会议号表查找部门映射：" + cdrCall.getCoSpace());
                BusiConferenceNumber busiConferenceNumber = busiConferenceNumberMapper.selectBusiConferenceNumberById(Long.parseLong(cp.getUri()));
                if (busiConferenceNumber == null)
                {
                    logger.error("CDR-call保存出错，找不到busiConferenceNumber对象，应该属于fme直接拉会，未经过会控：" + cdrCall.getCoSpace());
                    return;
                }

                BusiHistoryConference s = busiHistoryConferenceService.saveHistory(cp, cdrCall, busiConferenceNumber.getDeptId());
                if (s != null) {
                    CdrCache.getInstance().putHistoryConferenceMap(cdrCall.getCoSpace(), s);
                }
            }
            else
            {
                if (conferenceContext.getHistoryConference() != null) {
                    CdrCache.getInstance().putHistoryConferenceMap(cdrCall.getCoSpace(), conferenceContext.getHistoryConference());
                }
            }
        }
        else if (cdrCall.getRecordType() == 0)
        {
            BusiHistoryCall busiHistoryCall = iHistoryService.findHistoryCallByCoSpaceAndCall(cdrCall.getCdrId(), cdrCall.getCoSpace());
            if (busiHistoryCall != null)
            {
                Long historyConferenceId = busiHistoryCall.getHistoryConferenceId();
                BusiHistoryParticipantTerminal busiHistoryParticipantTerminalCon = new BusiHistoryParticipantTerminal();
                busiHistoryParticipantTerminalCon.setHistoryConferenceId(historyConferenceId);
                List<BusiHistoryParticipantTerminal> historyParticipantTerminalList = busiHistoryParticipantTerminalService.selectBusiHistoryParticipantTerminalList(busiHistoryParticipantTerminalCon);
                int participantNum = historyParticipantTerminalList.size();
                if (participantNum == 0) {
                    List<BusiHistoryParticipant> historyParticipantByCoSpaceId = iHistoryService.findHistoryParticipantByCoSpaceId(historyConferenceId);
                    participantNum = CollectionUtils.isEmpty(historyParticipantByCoSpaceId) ? 0 : historyParticipantByCoSpaceId.size();
                }
                cdrCall.setCoSpace(busiHistoryCall.getCoSpace());
                iHistoryService.updateHistoryConferenceByCoSpace(historyConferenceId, cdrCall, participantNum);
                CdrCache.getInstance().remove(busiHistoryCall.getCoSpace());
                CdrCache.getInstance().removeHistoryConferenceMap(busiHistoryCall.getCoSpace());
                applicationContext.publishEvent(new RecordEvent(this, busiHistoryCall.getDeptId(), cdrCall));
            }
        }
        else
        {
            logger.error("非法call记录类型：" + cdrCall);
        }
        // todo 会议结束生成话单
    }
    
}
