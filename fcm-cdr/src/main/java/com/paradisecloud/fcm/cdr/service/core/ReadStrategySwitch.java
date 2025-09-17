package com.paradisecloud.fcm.cdr.service.core;

import com.paradisecloud.fcm.dao.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author johnson liu
 * @date 2021/5/14 0:26
 */
@Component
public class ReadStrategySwitch
{
    
    private Map<String, XmlReadStrategy<?>> providers = new HashMap<>();
    
    @Autowired
    private XmlReadStrategy<CdrCall> callReadStrategy;
    
    @Autowired
    private XmlReadStrategy<CdrCallLegEnd> callLegEndStrategy;
    
    @Autowired
    private XmlReadStrategy<CdrCallLegStart> callLegStartStrategy;
    
    @Autowired
    private XmlReadStrategy<CdrCallLegUpdate> callLegUpdateStrategy;
    
    @Autowired
    private XmlReadStrategy<CdrRecording> recordingStrategy;
    
    @Autowired
    private XmlReadStrategy<CdrStreaming> streamingStrategy;
    
    @PostConstruct
    public void inits()
    {
        providers.put("callStart", callReadStrategy);
        providers.put("callEnd", callReadStrategy);
        providers.put("callLegStart", callLegStartStrategy);
        providers.put("callLegEnd", callLegEndStrategy);
        providers.put("callLegUpdate", callLegUpdateStrategy);
        providers.put("recordingStart", recordingStrategy);
        providers.put("recordingEnd", recordingStrategy);
        providers.put("streamingStart", streamingStrategy);
        providers.put("streamingEnd", streamingStrategy);
    }
    
    public XmlReadStrategy<?> getClassByType(String type)
    {
        return providers.get(type);
    }
}
