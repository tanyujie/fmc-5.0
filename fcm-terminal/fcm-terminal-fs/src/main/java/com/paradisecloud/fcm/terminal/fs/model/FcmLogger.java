/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FcmLogger.java
 * Package     : com.paradisecloud.fcm.terminal.fs.model
 * @author sinhy 
 * @since 2021-12-15 22:00
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fs.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * @author sinhy
 * @since 2021-12-15 22:00
 * @version V1.0  
 */
public class FcmLogger
{
    
    private Logger logger;
    
    public FcmLogger(FcmBridge fcmBridge)
    {
        this.logger = LoggerFactory.getLogger("FcmLogger[" + fcmBridge.getBusiFreeSwitch().getIp().replace('.', '`') + "]");
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
