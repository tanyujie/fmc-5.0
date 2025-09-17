package com.paradisecloud.fcm.cdr.service.impls;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.cdr.service.core.ReadStrategySwitch;
import com.paradisecloud.fcm.cdr.service.core.cache.CdrCache;
import com.paradisecloud.fcm.cdr.service.interfaces.ICallDetailRecordHandler;
import com.paradisecloud.fcm.cdr.service.model.CdrInfoRequest;
import com.paradisecloud.fcm.cdr.service.model.RecordElement;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.IOUtils;

/**
 * @author johnson liu
 * @date 2021/5/12 14:06
 */
@Service
public class CallDetailRecordHandler implements ICallDetailRecordHandler
{
    
    private static Logger logger = LoggerFactory.getLogger(CallDetailRecordHandler.class);
    
    @Autowired
    private ReadStrategySwitch readStrategySwitch;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveCallDetailRecords(HttpServletRequest request, HttpServletResponse response)
    {
        String type = "";
        try
        {
            String fmeIp = getRemoteIp(request);
            // 读取xml文档
            String xmlBody = IOUtils.copyToString(request.getInputStream());
            logger.info("CDR信息:\n{}", xmlBody);
            String json = XML.toJSONObject(xmlBody).toString();
            JSONObject jsonObject = JSONObject.parseObject(json);
            
            CdrInfoRequest cdrInfoRequest = jsonObject.toJavaObject(CdrInfoRequest.class);
            
            List<RecordElement> recordElements = cdrInfoRequest.getRecords().getRecord();
            String session = cdrInfoRequest.getRecords().getSession();
            
            int maxCorrelatedIndex = readMaxCorrelatedIndex(recordElements);
            
            Integer cacheIndex = CdrCache.getInstance().getCorrelationIndex(session);
            if (cacheIndex != null && maxCorrelatedIndex <= cacheIndex)
            {
                // 已经处理过的记录不再处理
                return 0;
            }
            for (RecordElement recordElement : recordElements)
            {
                type = recordElement.getType();
                readStrategySwitch.getClassByType(type).executeAdd(session, recordElement, fmeIp);
            }
            CdrCache.getInstance().putCorrelationIndex(session, maxCorrelatedIndex);
        }
        catch (IOException e)
        {
            logger.error("解析CDR记录异常" + type + ":", e);
            throw new SystemException("解析CDR记录异常");
        }
        catch (Exception e)
        {
            logger.error("保存CDR记录异常" + type + ":", e);
            throw new SystemException("保存CDR记录异常");
        }
        return 0;
    }
    
    /**
     * 从cdrInfoRequest读取最大的CorrelatedIndex
     *
     * @param recordElements
     * @return
     */
    private int readMaxCorrelatedIndex(List<RecordElement> recordElements)
    {
        int maxIndex = 0;
        try
        {
            for (RecordElement recordElement : recordElements)
            {
                Integer correlatorIndex = recordElement.getCorrelatorIndex();
                maxIndex = (maxIndex >= correlatorIndex) ? maxIndex : correlatorIndex;
            }
        }
        catch (Exception e)
        {
            logger.error("获取文档中correlatorIndex属性值失败", e.getMessage());
        }
        return maxIndex;
    }

    private String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
}
