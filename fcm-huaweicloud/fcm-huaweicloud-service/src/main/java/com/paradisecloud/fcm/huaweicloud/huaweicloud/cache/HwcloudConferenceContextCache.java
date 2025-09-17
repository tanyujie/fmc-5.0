package com.paradisecloud.fcm.huaweicloud.huaweicloud.cache;

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
public class HwcloudConferenceContextCache extends BaseConferenceContextCache<HwcloudConferenceContext> {
    private HwcloudConferenceContextCache()
    {

    }
    private static final long serialVersionUID = 1L;
    private static final HwcloudConferenceContextCache INSTANCE = new HwcloudConferenceContextCache();

    private Map<String, Map<String, HwcloudConferenceContext>> cascadeConferenceContextsMap = new ConcurrentHashMap<>();

    private Map<String, Object> monitorParticipantMap = new ConcurrentHashMap<>();

    private Map<String, String> confIdConferenceMap = new ConcurrentHashMap<>();

    public HwcloudConferenceContext getMainConferenceContext(String conferenceNumber)
    {
        return getMainConferenceContext(get(conferenceNumber));
    }

    public HwcloudConferenceContext getMainConferenceContext(HwcloudConferenceContext conferenceContext)
    {
        if (conferenceContext == null)
        {
            return null;
        }

        GenericValue<HwcloudConferenceContext> v = new GenericValue<>();
        upwardProcessingConferenceContext(conferenceContext, (cc)->{
            if (cc!=null)
            {
                v.setValue(conferenceContext);
            }
        });
        return v.getValue();
    }
    public void upwardProcessingConferenceContext(HwcloudConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        conferenceContextProcessor.process(cc);
        for (Entry<String, Map<String, HwcloudConferenceContext>> e : cascadeConferenceContextsMap.entrySet())
        {
            String upCn = e.getKey();
            Map<String, HwcloudConferenceContext> ccMap = e.getValue();
            if (ccMap.containsKey(cc.getAccessCode()))
            {
                // 向上递归
                upwardProcessingConferenceContext(upCn, conferenceContextProcessor);
            }
        }
    }
    public void upwardProcessingConferenceContext(String upConferenceNumber, ConferenceContextProcessor conferenceContextProcessor)
    {
        HwcloudConferenceContext cc = get(upConferenceNumber);
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
        HwcloudConferenceContext cc = get(upConferenceNumber);
        if (cc != null)
        {
            downwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }

    public void downwardProcessingConferenceContext(HwcloudConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor)
    {
        if (cc != null)
        {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor)conferenceContextProcessor).stopRecursion())
            {
                return;
            }

            Map<String, HwcloudConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(String.valueOf(cc.getAccessCode()));
            if (cascadeConferenceContexts != null)
            {
                for (Entry<String, HwcloudConferenceContext> e : cascadeConferenceContexts.entrySet())
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
    public Map<String, HwcloudConferenceContext> deleteCascadeConferenceContexts(String upConferenceNumber)
    {
        return cascadeConferenceContextsMap.remove(upConferenceNumber);
    }

    public void add(HwcloudConferenceContext conferenceContext)
    {
        String contextKey = EncryptIdUtil.generateKey(conferenceContext.getTemplateConferenceId(), McuType.MCU_HWCLOUD);
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
        void process(HwcloudConferenceContext conferenceContext);
    }
    public static HwcloudConferenceContextCache getInstance()
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
