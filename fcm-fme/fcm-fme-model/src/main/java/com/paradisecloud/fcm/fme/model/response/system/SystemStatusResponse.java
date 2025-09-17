/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SystemStatusResponse.java
 * Package     : com.paradisecloud.fcm.fme.model.response.system
 * @author sinhy 
 * @since 2021-07-23 12:08
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.response.system;

import com.paradisecloud.fcm.fme.model.cms.system.SystemStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**  
 * <pre>系统状态响应</pre>
 * @author sinhy
 * @since 2021-07-23 12:08
 * @version V1.0  
 */
@Getter
@Setter
@ToString
public class SystemStatusResponse
{
    
    private SystemStatus status;
}
