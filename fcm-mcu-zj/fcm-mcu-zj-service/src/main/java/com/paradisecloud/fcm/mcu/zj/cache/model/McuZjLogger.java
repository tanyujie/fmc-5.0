/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeLogger.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai
 * @since 2021-02-05 10:21
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.zj.cache.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>FME日志记录器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-05 10:21
 */
public class McuZjLogger {

    private Logger logger;

    /**
     * FME桥对象
     */
    private McuZjBridge mcuXdBridge;

    /**
     * 是否记录websocket日志
     */
    private boolean isRecordLog;

    /**
     * <pre>构造方法</pre>
     *
     * @param mcuXdBridge
     * @param isRecordLog
     * @author lilinhai
     * @since 2021-02-05 10:22
     */
    McuZjLogger(McuZjBridge mcuXdBridge, boolean isRecordLog) {
        this.mcuXdBridge = mcuXdBridge;
        this.isRecordLog = isRecordLog;
        this.logger = LoggerFactory.getLogger("McuZjLogger[" + mcuXdBridge + "]");
    }

    /**
     * <pre>webSocket日志记录</pre>
     *
     * @param log
     * @author lilinhai
     * @since 2021-02-05 10:29
     */
    public void logInfo(String log) {
        logger.info("--[ {}]", log);
    }

    /**
     * <pre>webSocket日志记录</pre>
     *
     * @param log
     * @param isLog
     * @param e     void
     * @author lilinhai
     * @since 2021-02-05 10:29
     */
    public void logInfo(String log, boolean isLog, Throwable e) {
        if (e != null) {
            logger.error(log, e);
        } else {
            logger.info("--[ {}]", log);
        }
    }

    /**
     * <pre>webSocket日志记录</pre>
     *
     * @param log
     * @param isLog
     * @param isError void
     * @author lilinhai
     * @since 2021-02-05 10:29
     */
    public void logInfo(String log, boolean isLog, boolean isError) {
        if (isError) {
            logger.error("--[ {}]", log);
        } else {
            logger.info("--[ {}]", log);
        }
    }
}
