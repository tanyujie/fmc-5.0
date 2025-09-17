/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeLogger.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai 
 * @since 2021-02-05 10:21
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * <pre>FME日志记录器</pre>
 * @author lilinhai
 * @since 2021-02-05 10:21
 * @version V1.0  
 */
public class FmeLogger
{
    
    private Logger logger;
    
    /**
     * FME桥对象
     */
    private FmeBridge fmeBridge;
    
    /**
     * 是否记录websocket日志
     */
    private boolean isRecordWebsocketLog;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-05 10:22 
     * @param fmeBridge 
     * @param isRecordWebsocketLog
     */
    FmeLogger(FmeBridge fmeBridge, boolean isRecordWebsocketLog)
    {
        this.fmeBridge = fmeBridge;
        this.isRecordWebsocketLog = isRecordWebsocketLog;
        this.logger = LoggerFactory.getLogger("FmeLogger[" + fmeBridge.getBridgeAddress().replace('.', '`') + "]");
    }
    
    /**
     * <pre>webSocket info日志记录</pre>
     * @author lilinhai
     * @since 2021-02-05 09:52 
     * @param logInfo void
     */
    public void logWebsocketInfo(String log, boolean isLog)
    {
        logWebsocketInfo(log, isLog, null);
    }
    
    /**
     * <pre>webSocket日志记录</pre>
     * @author lilinhai
     * @since 2021-02-05 10:29 
     * @param log
     * @param isLog
     * @param e void
     */
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
                logger.info("--Websocket-[{}]-[ {}]", fmeBridge.isAvailable(), log);
            }
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
    public void logWebsocketInfo(String log, boolean isLog, boolean isError)
    {
        if (isRecordWebsocketLog && isLog)
        {
            if (isError)
            {
                logger.error("--Websocket-[ {}]", log);
            }
            else
            {
                logger.info("--Websocket-[{}]-[ {}]", fmeBridge.isAvailable(), log);
            }
        }
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
