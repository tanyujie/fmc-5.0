package com.paradisecloud.fcm.tencent.cache;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.BaseConferenceContextCache;
import com.sinhy.model.GenericValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/4/20 11:11
 */
public class TencentConferenceContextCache extends BaseConferenceContextCache<TencentConferenceContext> {
    private TencentConferenceContextCache()
    {

    }
    private static final long serialVersionUID = 1L;
    private static final TencentConferenceContextCache INSTANCE = new TencentConferenceContextCache();

    private Map<String, Map<String, TencentConferenceContext>> cascadeConferenceContextsMap = new ConcurrentHashMap<>();

    private Map<String, Object> monitorParticipantMap = new ConcurrentHashMap<>();

    private Map<String, String> confIdConferenceMap = new ConcurrentHashMap<>();

    public TencentConferenceContext getMainConferenceContext(String conferenceNumber)
    {
        return getMainConferenceContext(get(conferenceNumber));
    }

    public TencentConferenceContext getMainConferenceContext(TencentConferenceContext conferenceContext)
    {
        if (conferenceContext == null)
        {
            return null;
        }

        GenericValue<TencentConferenceContext> v = new GenericValue<>();
        upwardProcessingConferenceContext(conferenceContext, (cc)->{
            if (cc!=null)
            {
                v.setValue(conferenceContext);
            }
        });
        return v.getValue();
    }
    public void upwardProcessingConferenceContext(TencentConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        conferenceContextProcessor.process(cc);
        for (Entry<String, Map<String, TencentConferenceContext>> e : cascadeConferenceContextsMap.entrySet())
        {
            String upCn = e.getKey();
            Map<String, TencentConferenceContext> ccMap = e.getValue();
            if (ccMap.containsKey(cc.getAccessCode()))
            {
                // 向上递归
                upwardProcessingConferenceContext(upCn, conferenceContextProcessor);
            }
        }
    }
    public void upwardProcessingConferenceContext(String upConferenceNumber, ConferenceContextProcessor conferenceContextProcessor)
    {
        TencentConferenceContext cc = get(upConferenceNumber);
        if (cc != null)
        {
            upwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }


    /**
     * 向下处理会议室
     * @author lilinhai
     * @since 2021-03-04 11:58
     * @param upConferenceNumber
     * @param conferenceContextProcessor void
     */
    public void downwardProcessingConferenceContext(String upConferenceNumber, ConferenceContextProcessor conferenceContextProcessor)
    {
        TencentConferenceContext cc = get(upConferenceNumber);
        if (cc != null)
        {
            downwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }

    public void downwardProcessingConferenceContext(TencentConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
            {
                return;
            }

            Map<String, TencentConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(String.valueOf(cc.getAccessCode()));
            if (cascadeConferenceContexts != null)
            {
                for (Entry<String, TencentConferenceContext> e : cascadeConferenceContexts.entrySet())
                {
                    downwardProcessingConferenceContext(String.valueOf(e.getValue().getAccessCode()), conferenceContextProcessor);
                }
            }
        }
    }


    /**
     * <pre>解除下级级联会议缓存（结束上级会议的时候会调用该方法释放资源）</pre>
     * @author lilinhai
     * @since 2021-02-04 13:53
     * @return List<ConferenceContext>
     */
    public Map<String, TencentConferenceContext> deleteCascadeConferenceContexts(String upConferenceNumber)
    {
        return cascadeConferenceContextsMap.remove(upConferenceNumber);
    }

    public void add(TencentConferenceContext conferenceContext)
    {
        String contextKey = EncryptIdUtil.generateKey(conferenceContext.getTemplateConferenceId(), McuType.MCU_TENCENT);
        super.put(contextKey, conferenceContext);
        super.put(conferenceContext.getMeetingId(), conferenceContext);
    }


    /**
     * 会议室上下文处理器
     * @author lilinhai
     * @since 2021-03-04 11:30
     * @version V1.0
     */
    public static interface ConferenceContextProcessor
    {
        void process(TencentConferenceContext conferenceContext);
    }
    public static TencentConferenceContextCache getInstance()
    {
        return INSTANCE;
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

    public Map<String, Object> getMonitorParticipantMap() {
        return monitorParticipantMap;
    }

    public void setMonitorParticipantMap(Map<String, Object> monitorParticipantMap) {
        this.monitorParticipantMap = monitorParticipantMap;
    }

    public Map<String, String> getConfIdConferenceMap() {
        return confIdConferenceMap;
    }

    public void setConfIdConferenceMap(Map<String, String> confIdConferenceMap) {
        this.confIdConferenceMap = confIdConferenceMap;
    }


}
