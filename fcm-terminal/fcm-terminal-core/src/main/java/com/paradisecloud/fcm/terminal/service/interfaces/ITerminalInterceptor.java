/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ITerminalInterceptor.java
 * Package     : com.paradisecloud.fcm.terminal.service.interfaces
 * @author sinhy 
 * @since 2021-12-18 20:34
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiTerminal;

/**  
 * 终端拦截器
 * @author sinhy
 * @since 2021-12-18 20:34
 * @version V1.0  
 */
public interface ITerminalInterceptor
{
    
    /**
     * 终端添加后拦截处理
     * @author sinhy
     * @since 2021-12-18 20:36 
     * @param busiTerminal void
     */
    void terminalInserted(BusiTerminal busiTerminal);
    
    /**
     * 终端更新后拦截处理
     * @author sinhy
     * @since 2021-12-18 20:36 
     * @param busiTerminal void
     */
    void terminalUpdated(BusiTerminal busiTerminal);
    
    /**
     * 终端移除后拦截处理
     * @author sinhy
     * @since 2021-12-18 20:36 
     * @param busiTerminal void
     */
    void terminalRemoved(BusiTerminal busiTerminal);
}
