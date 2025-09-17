/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeIDBuilder.java
 * Package     : com.paradisecloud.fcm.fme.model.core
 * @author lilinhai 
 * @since 2021-01-21 11:13
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.core;

import com.paradisecloud.fcm.dao.model.BusiFme;

/**  
 * <pre>获取FMEID</pre>
 * @author lilinhai
 * @since 2021-01-21 11:13
 * @version V1.0  
 */
public class FmeIDBuilder
{
    
    public static String build(BusiFme busiFme)
    {
        return busiFme.getIp() + ":" + busiFme.getPort();
    }
}
