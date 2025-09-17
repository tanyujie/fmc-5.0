/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceCache.java
 * Package     : com.paradisecloud.fcm.fme.cache
 * @author lilinhai 
 * @since 2021-02-02 13:57
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.kdc.cache;

import com.paradisecloud.fcm.service.conference.BaseConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.sinhy.model.GenericValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>正在进行中的会议上下文缓存, key=coSpaceId</pre>
 * @author lilinhai
 * @since 2021-02-02 13:57
 * @version V1.0  
 */
public class McuKdcConferenceContextCache extends BaseConferenceContextCache<McuKdcConferenceContext>
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 17:35 
     */
    private static final long serialVersionUID = 1L;
    private static final McuKdcConferenceContextCache INSTANCE = new McuKdcConferenceContextCache();
    /**
     * key 会议名
     */
    private Map<String, McuKdcConferenceContext> conferenceNameContextMap = new ConcurrentHashMap<>();
    
    /**
     * 用于缓存级联会议，方便根据级联ID快速查找所有级联在一起的会议
     */
    private Map<String, Map<String, McuKdcConferenceContext>> cascadeConferenceContextsMap = new ConcurrentHashMap<>();
    
    private McuKdcConferenceContextCache()
    {
        
    }
    
    public void addCascadeConferenceContext(String upContextKey, McuKdcConferenceContext conferenceContext)
    {
        Map<String, McuKdcConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(upContextKey);
        if (cascadeConferenceContexts == null)
        {
            cascadeConferenceContexts = new ConcurrentHashMap<>();
            cascadeConferenceContextsMap.put(upContextKey, cascadeConferenceContexts);
        }
        cascadeConferenceContexts.put(conferenceContext.getContextKey(), conferenceContext);
    }
    
    public void add(McuKdcConferenceContext conferenceContext)
    {
        super.put(conferenceContext.getContextKey(), conferenceContext);
        conferenceNameContextMap.put(conferenceContext.getName(), conferenceContext);
    }

    public void remove(String contextKey) {
        McuKdcConferenceContext conferenceContext = super.remove(contextKey);
        if (conferenceContext != null) {
            conferenceNameContextMap.remove(conferenceContext.getName());
        }
    }

    public McuKdcConferenceContext getByConferenceName(String conferenceName) {
        return conferenceNameContextMap.get(conferenceName);
    }
    
    /**
     * <pre>解除下级级联会议缓存（结束上级会议的时候会调用该方法释放资源）</pre>
     * @author lilinhai
     * @since 2021-02-04 13:53
     * @return List<ConferenceContext>
     */
    public Map<String, McuKdcConferenceContext> deleteCascadeConferenceContexts(String upContextKey)
    {
        return cascadeConferenceContextsMap.remove(upContextKey);
    }
    
    /**
     * 销毁所有向下级联的会议室
     * @author lilinhai
     * @since 2021-03-04 11:30 
     * @param upContextKey
     * @param conferenceContextProcessor void
     */
    public void destroyAllCascadeConferenceContexts(String upContextKey, ConferenceContextProcessor conferenceContextProcessor)
    {
        McuKdcConferenceContext cc = get(upContextKey);
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            Map<String, McuKdcConferenceContext> cascadeConferenceContexts = deleteCascadeConferenceContexts(upContextKey);
            if (cascadeConferenceContexts != null)
            {
                cascadeConferenceContexts.forEach((cn, cc0)->{
                    destroyAllCascadeConferenceContexts(cc0.getContextKey(), conferenceContextProcessor);
                });
            }
        }
    }
    
    /**
     * 向上处理会议室
     * @author lilinhai
     * @since 2021-03-04 11:39 
     * @param upContextKey
     * @param conferenceContextProcessor void
     */
    public void upwardProcessingConferenceContext(String upContextKey, ConferenceContextProcessor conferenceContextProcessor)
    {
        McuKdcConferenceContext cc = get(upContextKey);
        if (cc != null)
        {
            upwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }
    
    /**
     * 获取上级会议
     * @author lilinhai
     * @since 2021-03-08 14:51 
     * @param cc
     * @return ConferenceContext
     */
    public McuKdcConferenceContext getUpConferenceContext(McuKdcConferenceContext cc)
    {
        for (Entry<String, Map<String, McuKdcConferenceContext>> e : cascadeConferenceContextsMap.entrySet())
        {
            String upCn = e.getKey();
            Map<String, McuKdcConferenceContext> ccMap = e.getValue();
            if (ccMap.containsKey(cc.getContextKey()))
            {
                return get(upCn);
            }
        }
        return null;
    }
    
    public void upwardProcessingConferenceContext(McuKdcConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        conferenceContextProcessor.process(cc);
        if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
        {
            return;
        }
        for (Entry<String, Map<String, McuKdcConferenceContext>> e : cascadeConferenceContextsMap.entrySet())
        {
            String upCn = e.getKey();
            Map<String, McuKdcConferenceContext> ccMap = e.getValue();
            if (ccMap.containsKey(cc.getContextKey()))
            {
                // 向上递归
                upwardProcessingConferenceContext(upCn, conferenceContextProcessor);
            }
        }
    }
    
    /**
     * 向下处理会议室
     * @author lilinhai
     * @since 2021-03-04 11:58 
     * @param upContextKey
     * @param conferenceContextProcessor void
     */
    public void downwardProcessingConferenceContext(String upContextKey, ConferenceContextProcessor conferenceContextProcessor)
    {
        McuKdcConferenceContext cc = get(upContextKey);
        if (cc != null)
        {
            downwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }
     
    public void downwardProcessingConferenceContext(McuKdcConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
            {
                return;
            }
            
            Map<String, McuKdcConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(cc.getContextKey());
            if (cascadeConferenceContexts != null)
            {
                for (Entry<String, McuKdcConferenceContext> e : cascadeConferenceContexts.entrySet())
                {
                    downwardProcessingConferenceContext(e.getValue().getContextKey(), conferenceContextProcessor);
                }
            }
        }
    }
    
    /**
     * 向下处理级联会议
     * @author lilinhai
     * @since 2021-03-08 10:07 
     * @param cc
     * @param conferenceContextProcessor void
     */
    public void downwardProcessingCascadeConferenceContext(McuKdcConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
            {
                return;
            }
            
//            Cascade cascade = cc.getCascade();
//            if (cascade != null)
//            {
//                cascade.eachFmeAttendee((fa)->{
//                    downwardProcessingCascadeConferenceContext(get(fa.getUpContextKey()), conferenceContextProcessor);
//                });
//            }
        }
    }
    
    public McuKdcConferenceContext getMainConferenceContext(String contextKey)
    {
        return getMainConferenceContext(get(contextKey));
    }
    
    public McuKdcConferenceContext getMainConferenceContext(McuKdcConferenceContext conferenceContext)
    {
        if (conferenceContext == null)
        {
            return null;
        }
        if (conferenceContext.isMain())
        {
            return conferenceContext;
        }
        
        GenericValue<McuKdcConferenceContext> v = new GenericValue<>();
        upwardProcessingConferenceContext(conferenceContext, (cc)->{
            if (cc.isMain())
            {
                v.setValue(conferenceContext);
            }
        });
        return v.getValue();
    }
    
    /**
     * 会议室上下文处理器
     * @author lilinhai
     * @since 2021-03-04 11:30
     * @version V1.0
     */
    public static interface ConferenceContextProcessor
    {
        void process(McuKdcConferenceContext conferenceContext);
    }
    
    /**
     * 会议室上下文处理器
     * @author lilinhai
     * @since 2021-03-04 11:30
     * @version V1.0
     */
    public static interface ConferenceContextBreakProcessor extends ConferenceContextProcessor
    {
        boolean stopRecursion();
    }
    
    public static McuKdcConferenceContextCache getInstance()
    {
        return INSTANCE;
    }
}
