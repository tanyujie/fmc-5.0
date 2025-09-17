/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IBusinessPropertiesService.java
 * Package     : com.paradiscloud.fcm.business.interfaces
 * @author sinhy 
 * @since 2021-10-23 16:30
 * @version  V1.0
 */ 
package com.paradiscloud.fcm.business.interfaces;

import java.util.Map;

/**  
 * <pre>业务领域服务</pre>
 * @author sinhy
 * @since 2021-10-23 16:30
 * @version V1.0  
 */
public interface IBusinessFieldService
{
    
    /**
     * <pre>解析终端业务属性</pre>
     * @author sinhy
     * @since 2021-10-23 17:01 
     * @param businessProperties
     * @return Map<String,Object>
     */
    default Map<String, Object> parseTerminalBusinessProperties(Map<String, Object> businessProperties)
    {
        return null;
    }
}
