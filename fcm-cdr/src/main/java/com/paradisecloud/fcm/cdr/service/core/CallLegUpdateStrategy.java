package com.paradisecloud.fcm.cdr.service.core;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegUpdateService;
import com.paradisecloud.fcm.cdr.service.model.CallLegElement;
import com.paradisecloud.fcm.cdr.service.model.RecordElement;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.model.CdrCallLegUpdate;

/**
 * @author johnson liu
 * @date 2021/5/14 10:11
 */
@Component
public class CallLegUpdateStrategy implements XmlReadStrategy<CdrCallLegUpdate>
{
    private static final Logger logger = LoggerFactory.getLogger(CallLegUpdateStrategy.class);
    
    @Autowired
    private ICdrCallLegUpdateService cdrCallLegUpdateService;
    
    @Override
    public CdrCallLegUpdate readToBean(String session, RecordElement recordElement)
    {
        CdrCallLegUpdate cdrCallLegUpdate = new CdrCallLegUpdate();
        
        String time = recordElement.getTime();
        Integer recordIndex = recordElement.getRecordIndex();
        Integer correlatorIndex = recordElement.getCorrelatorIndex();
        CallLegElement callLeg = recordElement.getCallLeg();
        
        Date date = DateUtil.convertDateByString(time);
        
        cdrCallLegUpdate.setRecordIndex(recordIndex);
        cdrCallLegUpdate.setCorrelatorIndex(correlatorIndex);
        cdrCallLegUpdate.setTime(date);
        cdrCallLegUpdate.setSession(session);
        
        BeanUtils.copyProperties(callLeg, cdrCallLegUpdate);
        cdrCallLegUpdate.setCdrId(callLeg.getId());
        cdrCallLegUpdate.setCreateTime(new Date());
        return cdrCallLegUpdate;
    }
    
    @Override
    @Transactional
    public synchronized void executeAdd(CdrCallLegUpdate cdrCallLegUpdate)
    {
        cdrCallLegUpdateService.insertCdrCallLegUpdate(cdrCallLegUpdate);
    }
    
    @Override
    @Transactional
    public synchronized void executeAdd(String session, RecordElement recordElement, String fmeIp)
    {
        CdrCallLegUpdate cdrCallLegUpdate = readToBean(session, recordElement);
        logger.info("转化JavaBean后:{}", cdrCallLegUpdate);
        cdrCallLegUpdateService.insertCdrCallLegUpdate(cdrCallLegUpdate);
    }
    
}
