package com.paradisecloud.smc3.model;

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
