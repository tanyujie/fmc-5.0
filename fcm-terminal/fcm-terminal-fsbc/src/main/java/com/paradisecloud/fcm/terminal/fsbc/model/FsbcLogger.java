/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeLogger.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-02-05 10:21
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fsbc.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * <pre>FSBC日志记录器</pre>
 * @author lilinhai
 * @since 2021-02-05 10:21
 * @version V1.0  
 */
public class FsbcLogger
{
    
    private Logger logger;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-05 10:22 
     * @param fmeBridge 
     */
    FsbcLogger(FsbcBridge fsbcBridge)
    {
        this.logger = LoggerFactory.getLogger("FsbcLogger[" + fsbcBridge.getBusiFsbcRegistrationServer().getDataSyncIp().replace('.', '`') + "]");
    }
    
    /**
     * <pre>webSocket日志记录</pre>
     * @author lilinhai
     * @since 2021-02-05 10:29 
     * @param log
     * @param isLog
     * @param e void
     */
    public void logInfo(String log, boolean isLog, Throwable e)
    {
        if (e != null)
        {
            logger.error(log, e);
        }
        else
        {
            logger.info("--[ {}]", log);
        }
    }
    
    /**
     * <pre>webSocket日志记录</pre>
     * @author lilinhai
     * @since 2021-02-05 10:29 
     * @param log
     * @param isLog
     * @param isError void
     */
    public void logInfo(String log, boolean isLog, boolean isError)
    {
        if (isError)
        {
            logger.error("--[ {}]", log);
        }
        else
        {
            logger.info("--[ {}]", log);
        }
    }
}
