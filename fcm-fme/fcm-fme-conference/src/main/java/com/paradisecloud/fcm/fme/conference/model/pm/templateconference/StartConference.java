/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : StartConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.templateconference
 * @author sinhy 
 * @since 2021-09-22 21:12
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.templateconference;

import java.lang.reflect.Method;

public class StartConference extends StartTemplateConference
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-22 21:13 
     * @param method 
     */
    public StartConference(Method method)
    {
        super(method);
    }
    
    public synchronized void startConference(long templateConferenceId)
    {
        String contextKey = startTemplateConference(templateConferenceId);
        if(contextKey != null){
            startTemplateConference(templateConferenceId);
        }

    }
}
