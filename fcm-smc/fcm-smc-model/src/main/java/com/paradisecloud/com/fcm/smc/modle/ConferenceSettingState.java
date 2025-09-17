package com.paradisecloud.com.fcm.smc.modle;

/**
 * @author nj
 * @date 2023/3/29 10:41
 */
public  abstract class ConferenceSettingState {

    protected  SmcConferenceContextBase smcConferenceContextBase;

    public void setSmcConferenceContextBase(SmcConferenceContextBase smcConferenceContextBase) {
        this.smcConferenceContextBase = smcConferenceContextBase;
    }

    abstract void handler(SmcConferenceContextBase smcConferenceContextBase);
}
