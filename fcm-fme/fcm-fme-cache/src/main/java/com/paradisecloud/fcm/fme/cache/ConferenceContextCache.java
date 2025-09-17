/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceCache.java
 * Package     : com.paradisecloud.fcm.fme.cache
 * @author lilinhai 
 * @since 2021-02-02 13:57
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.cascade.Cascade;
import com.paradisecloud.fcm.service.conference.BaseConferenceContextCache;
import com.sinhy.model.GenericValue;

/**  
 * <pre>正在进行中的会议上下文缓存, key=coSpaceId</pre>
 * @author lilinhai
 * @since 2021-02-02 13:57
 * @version V1.0  
 */
public class ConferenceContextCache extends BaseConferenceContextCache<ConferenceContext>
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-02 17:35 
     */
    private static final long serialVersionUID = 1L;
    private static final ConferenceContextCache INSTANCE = new ConferenceContextCache();
    
    /**
     * 用于缓存级联会议，方便根据级联ID快速查找所有级联在一起的会议
     */
    private Map<String, Map<String, ConferenceContext>> cascadeConferenceContextsMap = new ConcurrentHashMap<>();
    
    private ConferenceContextCache()
    {
        
    }
    
    public void addCascadeConferenceContext(String upContextKey, ConferenceContext conferenceContext)
    {
        Map<String, ConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(upContextKey);
        if (cascadeConferenceContexts == null)
        {
            cascadeConferenceContexts = new ConcurrentHashMap<>();
            cascadeConferenceContextsMap.put(upContextKey, cascadeConferenceContexts);
        }
        cascadeConferenceContexts.put(conferenceContext.getContextKey(), conferenceContext);
    }
    
    public void add(ConferenceContext conferenceContext)
    {
        super.put(conferenceContext.getContextKey(), conferenceContext);
    }
    
    /**
     * <pre>根据加密的会议号从缓存中获取会议上下文对象</pre>
     * @author lilinhai
     * @since 2021-02-05 15:30 
     * @param conferenceId
     * @return ConferenceContext
     */
    public ConferenceContext getByConferenceId(String conferenceId)
    {
        return get(EncryptIdUtil.parasToContextKey(conferenceId));
    }
    
    /**
     * <pre>解除下级级联会议缓存（结束上级会议的时候会调用该方法释放资源）</pre>
     * @author lilinhai
     * @since 2021-02-04 13:53 
     * @param upContextKey
     * @return List<ConferenceContext>
     */
    public Map<String, ConferenceContext> deleteCascadeConferenceContexts(String upContextKey)
    {
        return cascadeConferenceContextsMap.remove(upContextKey);
    }
    
    /**
     * <pre>解除下级级联会议缓存（结束上级会议的时候会调用该方法释放资源）</pre>
     * @author lilinhai
     * @since 2021-02-04 13:53 
     * @param conferenceId
     * @return List<ConferenceContext>
     */
    public Map<String, ConferenceContext> deleteCascadeConferenceContextsByEncryptedConferenceNumber(String conferenceId)
    {
        return cascadeConferenceContextsMap.remove(EncryptIdUtil.parasToContextKey(conferenceId));
    }
    
    /**
     * 销毁所有向下级联的会议室
     * @author lilinhai
     * @since 2021-03-04 11:30 
     * @param conferenceId
     * @param conferenceContextProcessor void
     */
    public void destroyAllCascadeConferenceContextsByEncryptedConferenceNumber(String conferenceId, ConferenceContextProcessor conferenceContextProcessor)
    {
        destroyAllCascadeConferenceContexts(EncryptIdUtil.parasToContextKey(conferenceId), conferenceContextProcessor);
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
        ConferenceContext cc = get(upContextKey);
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            Map<String, ConferenceContext> cascadeConferenceContexts = deleteCascadeConferenceContexts(upContextKey);
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
        ConferenceContext cc = get(upContextKey);
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
    public ConferenceContext getUpConferenceContext(ConferenceContext cc)
    {
        for (Entry<String, Map<String, ConferenceContext>> e : cascadeConferenceContextsMap.entrySet())
        {
            String upCn = e.getKey();
            Map<String, ConferenceContext> ccMap = e.getValue();
            if (ccMap.containsKey(cc.getContextKey()))
            {
                return get(upCn);
            }
        }
        return null;
    }
    
    public void upwardProcessingConferenceContext(ConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        conferenceContextProcessor.process(cc);
        if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
        {
            return;
        }
        for (Entry<String, Map<String, ConferenceContext>> e : cascadeConferenceContextsMap.entrySet())
        {
            String upCn = e.getKey();
            Map<String, ConferenceContext> ccMap = e.getValue();
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
        ConferenceContext cc = get(upContextKey);
        if (cc != null)
        {
            downwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }
     
    public void downwardProcessingConferenceContext(ConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
            {
                return;
            }
            
            Map<String, ConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(String.valueOf(cc.getConferenceNumber()));
            if (cascadeConferenceContexts != null)
            {
                for (Entry<String, ConferenceContext> e : cascadeConferenceContexts.entrySet())
                {
                    downwardProcessingConferenceContext(String.valueOf(e.getValue().getConferenceNumber()), conferenceContextProcessor);
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
    public void downwardProcessingCascadeConferenceContext(ConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
            {
                return;
            }
            
            Cascade cascade = cc.getCascade();
            if (cascade != null)
            {
                cascade.eachFmeAttendee((fa)->{
                    downwardProcessingCascadeConferenceContext(get(fa.getCascadeConferenceNumber()), conferenceContextProcessor);
                });
            }
        }
    }
    
    public ConferenceContext getMainConferenceContext(String contextKey)
    {
        return getMainConferenceContext(get(contextKey));
    }
    
    public ConferenceContext getMainConferenceContext(ConferenceContext conferenceContext)
    {
        if (conferenceContext == null)
        {
            return null;
        }
        if (conferenceContext.isMain())
        {
            return conferenceContext;
        }
        
        GenericValue<ConferenceContext> v = new GenericValue<ConferenceContext>();
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
        void process(ConferenceContext conferenceContext);
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
    
    public static ConferenceContextCache getInstance()
    {
        return INSTANCE;
    }
}
