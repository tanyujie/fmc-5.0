package com.paradisecloud.fcm.cdr.service.core;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrStreamingService;
import com.paradisecloud.fcm.cdr.service.model.RecordElement;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.model.CdrStreaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author johnson liu
 * @date 2021/5/13 23:13
 */
@Component
public class StreamingStrategy implements XmlReadStrategy<CdrStreaming>
{
    private static final String MESSAGE_TYPE = "streamingStart";
    
    private static final Logger logger = LoggerFactory.getLogger(StreamingStrategy.class);
    
    @Autowired
    private ICdrStreamingService cdrStreamingService;
    
    @Override
    public CdrStreaming readToBean(String session, RecordElement recordElement)
    {
        CdrStreaming cdrStreaming = new CdrStreaming();
        // 读取节点属性
        String typeAttribute = recordElement.getType();
        String time = recordElement.getTime();
        Integer recordIndex = recordElement.getRecordIndex();
        Integer correlatorIndex = recordElement.getCorrelatorIndex();
        
        Date date = DateUtil.convertDateByString(time);
        
        cdrStreaming.setRecordType((MESSAGE_TYPE.equals(typeAttribute)) ? 1 : 0);
        cdrStreaming.setRecordIndex(recordIndex);
        cdrStreaming.setCorrelatorIndex(correlatorIndex);
        cdrStreaming.setTime(date);
        cdrStreaming.setSession(session);
        
        cdrStreaming.setCreateTime(new Date());
        return cdrStreaming;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void executeAdd(CdrStreaming cdrStreaming)
    {
        cdrStreamingService.insertCdrStreaming(cdrStreaming);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void executeAdd(String session, RecordElement recordElement, String fmeIp)
    {
        CdrStreaming cdrStreaming = readToBean(session, recordElement);
        logger.info("转化JavaBean后:{}", cdrStreaming);
        cdrStreamingService.insertCdrStreaming(cdrStreaming);
    }
    
}
