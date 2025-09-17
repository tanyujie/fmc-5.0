package com.paradisecloud.fcm.cdr.service.core;

import com.paradisecloud.fcm.cdr.service.core.cache.CdrCache;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegStartService;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IHistoryAllService;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IHistoryService;
import com.paradisecloud.fcm.cdr.service.model.CallLegElement;
import com.paradisecloud.fcm.cdr.service.model.RecordElement;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryAllParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllParticipant;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.CdrCallLegStart;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author johnson liu
 * @date 2021/5/14 10:11
 */
@Component
public class CallLegStartStrategy implements XmlReadStrategy<CdrCallLegStart>
{
    private static final Logger logger = LoggerFactory.getLogger(CallLegStartStrategy.class);
    
    @Autowired
    private ICdrCallLegStartService iCdrCallLegStartService;
    
    @Autowired
    private IHistoryService iHistoryService;
    
    @Autowired
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;

    @Resource
    private IHistoryAllService iHistoryAllService;

    @Resource
    private BusiHistoryAllParticipantMapper busiHistoryAllParticipantMapper;

    @Resource
    private IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService;
    
    @Override
    public CdrCallLegStart readToBean(String session, RecordElement recordElement)
    {
        CdrCallLegStart cdrCallLegStart = new CdrCallLegStart();
        
        String time = recordElement.getTime();
        Integer recordIndex = recordElement.getRecordIndex();
        Integer correlatorIndex = recordElement.getCorrelatorIndex();
        CallLegElement callLeg = recordElement.getCallLeg();
        
        Date date = DateUtil.convertDateByString(time);
        
        cdrCallLegStart.setRecordIndex(recordIndex);
        cdrCallLegStart.setCorrelatorIndex(correlatorIndex);
        cdrCallLegStart.setTime(date);
        cdrCallLegStart.setSession(session);
        
        BeanUtils.copyProperties(callLeg, cdrCallLegStart);
        cdrCallLegStart.setCdrId(callLeg.getId());
        cdrCallLegStart.setCreateTime(new Date());
        
        return cdrCallLegStart;
    }
    
    @Override
    @Transactional
    public synchronized void executeAdd(CdrCallLegStart cdrCallLegStart)
    {
        iCdrCallLegStartService.insertCdrCallLegStart(cdrCallLegStart);
    }
    
    @Override
    @Transactional
    public synchronized void executeAdd(String session, RecordElement recordElement, String fmeIp)
    {
        CdrCallLegStart cdrCallLegStart = readToBean(session, recordElement);
        logger.info("转化JavaBean后:{}", cdrCallLegStart);
        
        // 对新的CDR记录入库
        iCdrCallLegStartService.insertCdrCallLegStart(cdrCallLegStart);
        if (cdrCallLegStart.getCall() != null)
        {
            // 全会议与会者记录 start 2022.05.11
            // 添加全会议与会者历史记录表信息
            if (ExternalConfigCache.getInstance().isEnableCdrAll()) {
                BusiHistoryAllParticipant busiHistoryAllParticipant = busiHistoryAllParticipantMapper.selectBusiHistoryAllParticipantByCallLegId(cdrCallLegStart.getCdrId());
                if (busiHistoryAllParticipant == null) {
                    try {
                        busiHistoryAllParticipant = iHistoryAllService.buildHistoryParticipant(cdrCallLegStart);
                        iHistoryAllService.insertHistoryAllParticipant(busiHistoryAllParticipant);
                    } catch (Throwable e) {
                        logger.error("save history_all_participant error", e);
                    }
                }
            }
            // 全会议与会者记录 end 2022.05.11

            // 添加与会者历史记录表信息
            BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(cdrCallLegStart.getCdrId());
            if (busiHistoryParticipant == null)
            {
                try
                {
                    busiHistoryParticipant = iHistoryService.buildHistoryParticipant(cdrCallLegStart);
                    iHistoryService.insertHistoryParticipant(busiHistoryParticipant);
                    // 更新参会者终端信息
                    updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
                }
                catch (Throwable e)
                {
                    logger.error("save cdrCallLegStart error", e);
                }
            }
        }
        CdrCache.getInstance().putCallLegSaveMap(cdrCallLegStart.getCdrId(), true);
    }

    /**
     * 更新参会者终端信息
     *
     * @param busiHistoryParticipant
     */
    private void updateBusiHistoryParticipantTerminal(BusiHistoryParticipant busiHistoryParticipant) {
        if (busiHistoryParticipant != null) {
            if (busiHistoryParticipant.getTerminalId() != null) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiHistoryParticipant.getTerminalId());
                if (busiTerminal != null) {
                    busiHistoryParticipant.setName(busiTerminal.getName());
                }
            }
            try {
                busiHistoryParticipantTerminalService.updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(busiHistoryParticipant);
            } catch (Exception e) {
            }
        }
    }
}
