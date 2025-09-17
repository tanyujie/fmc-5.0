/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : StartConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy
 * @since 2021-09-22 21:12
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi.templateconference;


/**
 * @author Administrator
 */
public class StartConference extends StartTemplateConference
{

    public synchronized String startConference(long templateConferenceId)
    {
        String cn = startTemplateConference(templateConferenceId);

        return cn;

    }
    public synchronized String startConference(long templateConferenceId,String conferenceId)
    {
        String cn = startTemplateConference(templateConferenceId,conferenceId);

        return cn;

    }
    public synchronized String startConference(long templateConferenceId,String conferenceId, boolean downCascade)
    {
        String cn = startTemplateConference(templateConferenceId, conferenceId, downCascade);

        return cn;

    }
}
