/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeLogger.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-02-05 10:21
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc.cache.modle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * <pre>FME日志记录器</pre>
 * @author lilinhai
 * @since 2021-02-05 10:21
 * @version V1.0  
 */
public class SmcLogger
{
    
    private Logger logger;
    

    private SmcBridge smcBridge;
    
    /**
     * 是否记录websocket日志
     */
    private boolean isRecordWebsocketLog;


    SmcLogger(SmcBridge smcBridge, boolean isRecordWebsocketLog)
    {
        this.smcBridge = smcBridge;
        this.isRecordWebsocketLog = isRecordWebsocketLog;
        this.logger = LoggerFactory.getLogger("SmcLogger[" + smcBridge.getIp() + "]");
    }

    public void logWebsocketInfo(String log, boolean isLog)
    {
        logWebsocketInfo(log, isLog, null);
    }
    

    public void logWebsocketInfo(String log, boolean isLog, Throwable e)
    {
        if (isRecordWebsocketLog && isLog)
        {
            if (e != null)
            {
                logger.error(log, e);
            }
            else
            {
                logger.info("--SMCWebsocket-[{}]-[ {}]", smcBridge.getWebsocketAvailable(), log);
            }
        }
    }
    

    public void logWebsocketInfo(String log, boolean isLog, boolean isError)
    {
        if (isRecordWebsocketLog && isLog)
        {
            if (isError)
            {
                logger.error("--SMCWebsocket-[ {}]", log);
            }
            else
            {
                logger.info("--SMCWebsocket-[{}]-[ {}]", smcBridge.getWebsocketAvailable(), log);
            }
        }
    }
    

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
