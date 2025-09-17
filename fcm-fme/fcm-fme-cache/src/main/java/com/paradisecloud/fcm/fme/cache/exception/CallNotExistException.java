package com.paradisecloud.fcm.fme.cache.exception;

import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

/**
 * call不存在异常
 *
 * @author zt1994 2019/7/12 15:44
 */
public class CallNotExistException extends RuntimeException
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2020-12-16 17:13 
     */
    private static final long serialVersionUID = 1L;

    private Participant participant;
    
    public CallNotExistException(String message, Participant participant)
    {
        super(message);
        this.participant = participant;
    }

    /**
     * <p>Get Method   :   participant Participant</p>
     * @return participant
     */
    public Participant getParticipant()
    {
        return participant;
    }
    
}
