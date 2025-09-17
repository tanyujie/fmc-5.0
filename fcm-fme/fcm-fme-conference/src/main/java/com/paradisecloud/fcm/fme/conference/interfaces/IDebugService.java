/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IConferenceDebugService.java
 * Package     : com.paradisecloud.fcm.fme.conference.interfaces
 * @author sinhy 
 * @since 2021-09-18 08:21
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.interfaces;

import com.alibaba.fastjson.JSONObject;

/**  
 * <pre>请加上该类的描述</pre>
 * @author sinhy
 * @since 2021-09-18 08:21
 * @version V1.0  
 */
public interface IDebugService
{
    
    JSONObject allConference();
    JSONObject logJson(String conferenceNumber);
}
