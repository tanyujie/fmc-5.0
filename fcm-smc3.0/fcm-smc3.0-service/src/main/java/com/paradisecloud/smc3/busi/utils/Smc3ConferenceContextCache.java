/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceCache.java
 * Package     : com.paradisecloud.fcm.fme.cache
 * @author lilinhai
 * @since 2021-02-02 13:57
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.utils;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.BaseConferenceContextCache;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.cascade.Cascade;
import com.sinhy.model.GenericValue;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>正在进行中的会议上下文缓存, key=coSpaceId</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-02 13:57
 */
public class Smc3ConferenceContextCache extends BaseConferenceContextCache<Smc3ConferenceContext> {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-02 17:35
     */
    private static final long serialVersionUID = 1L;
    private static final Smc3ConferenceContextCache INSTANCE = new Smc3ConferenceContextCache();

    /**
     * 用于缓存级联会议，方便根据级联ID快速查找所有级联在一起的会议
     */
    private Map<String, Map<String, Smc3ConferenceContext>> cascadeConferenceContextsMap = new ConcurrentHashMap<>();

    private Map<String, String> smc3ConferenceIdMap = new ConcurrentHashMap<>();

    private Smc3ConferenceContextCache() {

    }

    public static Smc3ConferenceContextCache getInstance() {
        return INSTANCE;
    }

    public void addCascadeConferenceContext(String upConferenceNumber, Smc3ConferenceContext conferenceContext) {
        Map<String, Smc3ConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(upConferenceNumber);
        if (cascadeConferenceContexts == null) {
            cascadeConferenceContexts = new ConcurrentHashMap<>();
            cascadeConferenceContextsMap.put(upConferenceNumber, cascadeConferenceContexts);
        }
        cascadeConferenceContexts.put(conferenceContext.getConferenceNumber(), conferenceContext);
    }

    public void add(Smc3ConferenceContext conferenceContext) {
        String contextKey = EncryptIdUtil.generateKey(conferenceContext.getTemplateConferenceId(), McuType.SMC3);
        super.put(contextKey, conferenceContext);
        smc3ConferenceIdMap.put(conferenceContext.getSmc3conferenceId(), contextKey);
    }

    public Smc3ConferenceContext get(String key) {
        Smc3ConferenceContext smc3ConferenceContext;
        smc3ConferenceContext = super.get(key);
        if (smc3ConferenceContext == null) {
            String contextKey = smc3ConferenceIdMap.get(key);
            if(Strings.isNotBlank(contextKey)){
                smc3ConferenceContext = super.get(contextKey);
            }
        }
        return smc3ConferenceContext;
    }

    public Smc3ConferenceContext remove(String key) {
        for (Entry<String, String> stringStringEntry : smc3ConferenceIdMap.entrySet()) {
            String value = stringStringEntry.getValue();
            if (Objects.equals(value, key)) {
                smc3ConferenceIdMap.remove(stringStringEntry.getKey());
            }
        }
        return super.remove(key);
    }

    /**
     * <pre>根据加密的会议号从缓存中获取会议上下文对象</pre>
     *
     * @param encryptedConferenceNumber
     * @return ConferenceContext
     * @author lilinhai
     * @since 2021-02-05 15:30
     */
    public Smc3ConferenceContext getByEncryptedConferenceNumber(String encryptedConferenceNumber) {
        return get(AesEnsUtils.getAesEncryptor().decryptHexToString(encryptedConferenceNumber));
    }

    /**
     * <pre>解除下级级联会议缓存（结束上级会议的时候会调用该方法释放资源）</pre>
     *
     * @return List<ConferenceContext>
     * @author lilinhai
     * @since 2021-02-04 13:53
     */
    public Map<String, Smc3ConferenceContext> deleteCascadeConferenceContexts(String upConferenceNumber) {
        return cascadeConferenceContextsMap.remove(upConferenceNumber);
    }

    /**
     * <pre>解除下级级联会议缓存（结束上级会议的时候会调用该方法释放资源）</pre>
     *
     * @return List<ConferenceContext>
     * @author lilinhai
     * @since 2021-02-04 13:53
     */
    public Map<String, Smc3ConferenceContext> deleteCascadeConferenceContextsByEncryptedConferenceNumber(String encryptedConferenceNumber) {
        return cascadeConferenceContextsMap.remove(AesEnsUtils.getAesEncryptor().decryptHexToString(encryptedConferenceNumber));
    }

    /**
     * 销毁所有向下级联的会议室
     *
     * @param conferenceContextProcessor void
     * @author lilinhai
     * @since 2021-03-04 11:30
     */
    public void destroyAllCascadeConferenceContextsByEncryptedConferenceNumber(String encryptedConferenceNumber, ConferenceContextProcessor conferenceContextProcessor) {
        destroyAllCascadeConferenceContexts(AesEnsUtils.getAesEncryptor().decryptHexToString(encryptedConferenceNumber), conferenceContextProcessor);
    }

    /**
     * 销毁所有向下级联的会议室
     *
     * @param upConferenceNumber
     * @param conferenceContextProcessor void
     * @author lilinhai
     * @since 2021-03-04 11:30
     */
    public void destroyAllCascadeConferenceContexts(String upConferenceNumber, ConferenceContextProcessor conferenceContextProcessor) {
        Smc3ConferenceContext cc = get(upConferenceNumber);
        if (cc != null) {
            conferenceContextProcessor.process(cc);
            Map<String, Smc3ConferenceContext> cascadeConferenceContexts = deleteCascadeConferenceContexts(upConferenceNumber);
            if (cascadeConferenceContexts != null) {
                cascadeConferenceContexts.forEach((cn, cc0) -> {
                    destroyAllCascadeConferenceContexts(cc0.getConferenceNumber(), conferenceContextProcessor);
                });
            }
        }
    }

    /**
     * 向上处理会议室
     *
     * @param upConferenceNumber
     * @param conferenceContextProcessor void
     * @author lilinhai
     * @since 2021-03-04 11:39
     */
    public void upwardProcessingConferenceContext(String upConferenceNumber, ConferenceContextProcessor conferenceContextProcessor) {
        Smc3ConferenceContext cc = get(upConferenceNumber);
        if (cc != null) {
            upwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }

    /**
     * 获取上级会议
     *
     * @param cc
     * @return ConferenceContext
     * @author lilinhai
     * @since 2021-03-08 14:51
     */
    public Smc3ConferenceContext getUpConferenceContext(Smc3ConferenceContext cc) {
        for (Entry<String, Map<String, Smc3ConferenceContext>> e : cascadeConferenceContextsMap.entrySet()) {
            String upCn = e.getKey();
            Map<String, Smc3ConferenceContext> ccMap = e.getValue();
            if (ccMap.containsKey(cc.getConferenceNumber())) {
                return get(upCn);
            }
        }
        return null;
    }

    public void upwardProcessingConferenceContext(Smc3ConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor) {
        conferenceContextProcessor.process(cc);
        if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor) conferenceContextProcessor).stopRecursion()) {
            return;
        }
        for (Entry<String, Map<String, Smc3ConferenceContext>> e : cascadeConferenceContextsMap.entrySet()) {
            String upCn = e.getKey();
            Map<String, Smc3ConferenceContext> ccMap = e.getValue();
            if (ccMap.containsKey(cc.getConferenceNumber())) {
                // 向上递归
                upwardProcessingConferenceContext(upCn, conferenceContextProcessor);
            }
        }
    }

    /**
     * 向下处理会议室
     *
     * @param upConferenceNumber
     * @param conferenceContextProcessor void
     * @author lilinhai
     * @since 2021-03-04 11:58
     */
    public void downwardProcessingConferenceContext(String upConferenceNumber, ConferenceContextProcessor conferenceContextProcessor) {
        Smc3ConferenceContext cc = get(upConferenceNumber);
        if (cc != null) {
            downwardProcessingConferenceContext(cc, conferenceContextProcessor);
        }
    }

    public void downwardProcessingConferenceContext(Smc3ConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor) {
        if (cc != null) {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor) conferenceContextProcessor).stopRecursion()) {
                return;
            }

            Map<String, Smc3ConferenceContext> cascadeConferenceContexts = cascadeConferenceContextsMap.get(String.valueOf(cc.getConferenceNumber()));
            if (cascadeConferenceContexts != null) {
                for (Entry<String, Smc3ConferenceContext> e : cascadeConferenceContexts.entrySet()) {
                    downwardProcessingConferenceContext(String.valueOf(e.getValue().getConferenceNumber()), conferenceContextProcessor);
                }
            }
        }
    }

    /**
     * 向下处理级联会议
     *
     * @param cc
     * @param conferenceContextProcessor void
     * @author lilinhai
     * @since 2021-03-08 10:07
     */
    public void downwardProcessingCascadeConferenceContext(Smc3ConferenceContext cc, ConferenceContextProcessor conferenceContextProcessor) {
        if (cc != null) {
            conferenceContextProcessor.process(cc);
            if (conferenceContextProcessor instanceof ConferenceContextBreakProcessor && ((ConferenceContextBreakProcessor) conferenceContextProcessor).stopRecursion()) {
                return;
            }

            Cascade cascade = cc.getCascade();
            if (cascade != null) {
                cascade.eachFmeAttendee((fa) -> {
                    downwardProcessingCascadeConferenceContext(get(fa.getCascadeConferenceNumber()), conferenceContextProcessor);
                });
            }
        }
    }

    public Smc3ConferenceContext getMainConferenceContext(String conferenceNumber) {
        return getMainConferenceContext(get(conferenceNumber));
    }

    public Smc3ConferenceContext getMainConferenceContext(Smc3ConferenceContext conferenceContext) {
        if (conferenceContext == null) {
            return null;
        }
        if (conferenceContext.isMain()) {
            return conferenceContext;
        }

        GenericValue<Smc3ConferenceContext> v = new GenericValue<Smc3ConferenceContext>();
        upwardProcessingConferenceContext(conferenceContext, (cc) -> {
            if (cc.isMain()) {
                v.setValue(conferenceContext);
            }
        });
        return v.getValue();
    }

    /**
     * 会议室上下文处理器
     *
     * @author lilinhai
     * @version V1.0
     * @since 2021-03-04 11:30
     */
    public static interface ConferenceContextProcessor {
        void process(Smc3ConferenceContext conferenceContext);
    }

    /**
     * 会议室上下文处理器
     *
     * @author lilinhai
     * @version V1.0
     * @since 2021-03-04 11:30
     */
    public static interface ConferenceContextBreakProcessor extends ConferenceContextProcessor {
        boolean stopRecursion();
    }
}
