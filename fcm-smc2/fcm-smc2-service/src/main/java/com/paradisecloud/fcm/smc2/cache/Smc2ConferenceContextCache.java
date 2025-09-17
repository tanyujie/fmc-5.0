package com.paradisecloud.fcm.smc2.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.BaseConferenceContextCache;
import com.sinhy.model.GenericValue;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/4/20 11:11
 */
public class Smc2ConferenceContextCache extends BaseConferenceContextCache<Smc2ConferenceContext> {
    private Smc2ConferenceContextCache()
    {

    }
    private static final long serialVersionUID = 1L;
    private static final Smc2ConferenceContextCache INSTANCE = new Smc2ConferenceContextCache();

    private Map<String, String> smc2ConferenceIdMap = new ConcurrentHashMap<>();

    private Map<String, Map<String, Smc2ConferenceContext>> cascadeConferenceContextsMap = new ConcurrentHashMap<>();

    private Map<String, Object> monitorParticipantMap = new ConcurrentHashMap<>();

    private Map<String, String> confIdConferenceMap = new ConcurrentHashMap<>();

    public Smc2ConferenceContext getMainConferenceContext(String conferenceNumber)
    {
        return getMainConferenceContext(get(conferenceNumber));
    }

    public Smc2ConferenceContext getMainConferenceContext(Smc2ConferenceContext conferenceContext)
    {
        if (conferenceContext == null)
        {
            return null;
        }

        GenericValue<Smc2ConferenceContext> v = new GenericValue<>();
        upwardProcessingConferenceContext(conferenceContext, (cc)->{
            if (cc!=null)
            {
                v.setValue(conferenceContext);
            }
        });
        return v.getValue();
    }
    public void upwardProcessingConferenceContext(Smc2ConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        conferenceContextProcessor.process(cc);
        for (Entry<String, Map<String, Smc2ConferenceContext>> e : cascadeConferenceContextsMap.entrySet())
        {
            String upCn = e.getKey();
            Map<String, Smc2ConferenceContext> ccMap = e.getValue();
            if (ccMap.containsKey(cc.getAccessCode()))
            {
                // 向上递归
                upwardProcessingConferenceContext(upCn, conferenceContextProcessor);
            }
        }
    }
    public void upwardProcessingConferenceContext(String upConferenceNumber, ConferenceContextProcessor conferenceContextProcessor)
    {
        Smc2ConferenceContext cc = get(upConferenceNumber);
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
        Smc2ConferenceContext cc = get(upConferenceNumber);
        if (cc != null)
        {
            downwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }

    public void downwardProcessingConferenceContext(Smc2ConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
            {
                return;
            }

            Map<String, Smc2ConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(String.valueOf(cc.getAccessCode()));
            if (cascadeConferenceContexts != null)
            {
                for (Entry<String, Smc2ConferenceContext> e : cascadeConferenceContexts.entrySet())
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
    public Map<String, Smc2ConferenceContext> deleteCascadeConferenceContexts(String upConferenceNumber)
    {
        return cascadeConferenceContextsMap.remove(upConferenceNumber);
    }


    public void add(Smc2ConferenceContext conferenceContext) {
        String contextKey = EncryptIdUtil.generateKey(conferenceContext.getTemplateConferenceId(), McuType.SMC2);
        super.put(contextKey, conferenceContext);
        smc2ConferenceIdMap.put(conferenceContext.getSmc2conferenceId(), contextKey);
    }

    public Smc2ConferenceContext get(String key) {
        Smc2ConferenceContext smc3ConferenceContext;
        smc3ConferenceContext = super.get(key);
        if (smc3ConferenceContext == null) {
            String contextKey = smc2ConferenceIdMap.get(key);
            if(Strings.isNotBlank(contextKey)){
                smc3ConferenceContext = super.get(contextKey);
            }
        }
        return smc3ConferenceContext;
    }

    public Smc2ConferenceContext remove(String key) {
        for (Entry<String, String> stringStringEntry : smc2ConferenceIdMap.entrySet()) {
            String value = stringStringEntry.getValue();
            if (Objects.equals(value, key)) {
                smc2ConferenceIdMap.remove(stringStringEntry.getKey());
            }
        }
        return super.remove(key);
    }

    /**
     * 会议室上下文处理器
     * @author lilinhai
     * @since 2021-03-04 11:30
     * @version V1.0
     */
    public static interface ConferenceContextProcessor
    {
        void process(Smc2ConferenceContext conferenceContext);
    }
    public static Smc2ConferenceContextCache getInstance()
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
