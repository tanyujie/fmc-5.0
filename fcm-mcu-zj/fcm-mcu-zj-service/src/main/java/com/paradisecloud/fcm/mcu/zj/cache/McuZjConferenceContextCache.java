/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceCache.java
 * Package     : com.paradisecloud.fcm.fme.cache
 * @author lilinhai 
 * @since 2021-02-02 13:57
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.cache;

import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContextCache;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.sinhy.model.GenericValue;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>正在进行中的会议上下文缓存, key=coSpaceId</pre>
 * @author lilinhai
 * @since 2021-02-02 13:57
 * @version V1.0  
 */
public class McuZjConferenceContextCache extends BaseConferenceContextCache<McuZjConferenceContext>
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 17:35 
     */
    private static final long serialVersionUID = 1L;
    private static final McuZjConferenceContextCache INSTANCE = new McuZjConferenceContextCache();
    
    /**
     * 用于缓存级联会议，方便根据级联ID快速查找所有级联在一起的会议
     */
    private Map<String, Map<String, McuZjConferenceContext>> cascadeConferenceContextsMap = new ConcurrentHashMap<>();
    
    private McuZjConferenceContextCache()
    {
        
    }
    
    public void addCascadeConferenceContext(String upContextKey, McuZjConferenceContext conferenceContext)
    {
        Map<String, McuZjConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(upContextKey);
        if (cascadeConferenceContexts == null)
        {
            cascadeConferenceContexts = new ConcurrentHashMap<>();
            cascadeConferenceContextsMap.put(upContextKey, cascadeConferenceContexts);
        }
        cascadeConferenceContexts.put(conferenceContext.getContextKey(), conferenceContext);
    }
    
    public void add(McuZjConferenceContext conferenceContext)
    {
        super.put(conferenceContext.getContextKey(), conferenceContext);
    }

    public void remove(String contextKey) {
        super.remove(contextKey);
    }

    /**
     *
     * @param fullConferenceNum 租户号（tenantId：通常为""） + 会议号
     * @param busiTerminal
     * @return
     */
    public McuZjConferenceContext getContext(String fullConferenceNum, BusiTerminal busiTerminal) {
        if (busiTerminal != null) {
            Collection<McuZjConferenceContext> conferenceContexts = getConferenceContextListByFullConferenceNum(fullConferenceNum);
            if (conferenceContexts != null) {
                for (McuZjConferenceContext conferenceContext : conferenceContexts) {
                    BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(busiTerminal.getId());
                    if (attendee != null && attendee.isMeetingJoined()) {
                        return conferenceContext;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * <pre>解除下级级联会议缓存（结束上级会议的时候会调用该方法释放资源）</pre>
     * @author lilinhai
     * @since 2021-02-04 13:53
     * @return List<ConferenceContext>
     */
    public Map<String, McuZjConferenceContext> deleteCascadeConferenceContexts(String upContextKey)
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
        McuZjConferenceContext cc = get(upContextKey);
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            Map<String, McuZjConferenceContext> cascadeConferenceContexts = deleteCascadeConferenceContexts(upContextKey);
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
        McuZjConferenceContext cc = get(upContextKey);
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
    public McuZjConferenceContext getUpConferenceContext(McuZjConferenceContext cc)
    {
        for (Entry<String, Map<String, McuZjConferenceContext>> e : cascadeConferenceContextsMap.entrySet())
        {
            String upCn = e.getKey();
            Map<String, McuZjConferenceContext> ccMap = e.getValue();
            if (ccMap.containsKey(cc.getContextKey()))
            {
                return get(upCn);
            }
        }
        return null;
    }
    
    public void upwardProcessingConferenceContext(McuZjConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        conferenceContextProcessor.process(cc);
        if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
        {
            return;
        }
        for (Entry<String, Map<String, McuZjConferenceContext>> e : cascadeConferenceContextsMap.entrySet())
        {
            String upCn = e.getKey();
            Map<String, McuZjConferenceContext> ccMap = e.getValue();
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
        McuZjConferenceContext cc = get(upContextKey);
        if (cc != null)
        {
            downwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }
     
    public void downwardProcessingConferenceContext(McuZjConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
            {
                return;
            }
            
            Map<String, McuZjConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(cc.getContextKey());
            if (cascadeConferenceContexts != null)
            {
                for (Entry<String, McuZjConferenceContext> e : cascadeConferenceContexts.entrySet())
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
    public void downwardProcessingCascadeConferenceContext(McuZjConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
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
//                    downwardProcessingCascadeConferenceContext(get(fa.getContextKey()), conferenceContextProcessor);
//                });
//            }
        }
    }
    
    public McuZjConferenceContext getMainConferenceContext(String contextKey)
    {
        return getMainConferenceContext(get(contextKey));
    }
    
    public McuZjConferenceContext getMainConferenceContext(McuZjConferenceContext conferenceContext)
    {
        if (conferenceContext == null)
        {
            return null;
        }
        if (conferenceContext.isMain())
        {
            return conferenceContext;
        }
        
        GenericValue<McuZjConferenceContext> v = new GenericValue<>();
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
        void process(McuZjConferenceContext conferenceContext);
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
    
    public static McuZjConferenceContextCache getInstance()
    {
        return INSTANCE;
    }
}
