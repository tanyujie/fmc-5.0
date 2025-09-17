/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ParticipantInfo.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-03-01 17:52
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model;

import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

/**  
 * <pre>FME侧参会者信息</pre>
 * @author lilinhai
 * @since 2021-03-01 17:52
 * @version V1.0  
 */
public class ParticipantInfo
{
    
    private Participant participant;
    
    private FmeBridge fmeBridge;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-03-01 17:53 
     * @param participant
     * @param fmeBridge 
     */
    public ParticipantInfo(Participant participant, FmeBridge fmeBridge)
    {
        super();
        this.participant = participant;
        this.fmeBridge = fmeBridge;
    }

    /**
     * <p>Get Method   :   participant Participant</p>
     * @return participant
     */
    public Participant getParticipant()
    {
        return participant;
    }

    /**
     * <p>Get Method   :   fmeBridge FmeBridge</p>
     * @return fmeBridge
     */
    public FmeBridge getFmeBridge()
    {
        return fmeBridge;
    }
}
