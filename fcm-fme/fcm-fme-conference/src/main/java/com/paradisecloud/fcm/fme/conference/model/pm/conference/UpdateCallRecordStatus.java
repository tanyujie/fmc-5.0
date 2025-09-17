/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UpdateCallRecordStatus.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:08
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.fme.model.parambuilder.CallParamBuilder;

public class UpdateCallRecordStatus extends UpdateCall
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:08 
     * @param method 
     */
    protected UpdateCallRecordStatus(Method method)
    {
        super(method);
        // TODO Auto-generated constructor stub
    }

    public void updateCallRecordStatus(String contextKey, Boolean record) {
        updateExistParticipantCall(contextKey, new CallParamBuilder().recording(record).build());
    }
}
