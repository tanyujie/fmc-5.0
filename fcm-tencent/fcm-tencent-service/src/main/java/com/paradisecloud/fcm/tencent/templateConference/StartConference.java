/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : StartConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy
 * @since 2021-09-22 21:12
 * @version  V1.0
 */
package com.paradisecloud.fcm.tencent.templateConference;


import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;

public class StartConference extends StartTemplateConference
{

    public synchronized void startConference(long templateConferenceId)
    {
        TencentConferenceContext cn = startTemplateConference(templateConferenceId);
        while (cn != null)
        {
            cn = startTemplateConference(templateConferenceId);
        }
    }
}
