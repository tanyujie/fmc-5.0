/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FmeLogger.java
 * Package     : com.paradisecloud.fcm.fme.cache.model
 * @author lilinhai
 * @since 2021-02-05 10:21
 * @version  V1.0
 */
package com.paradisecloud.fcm.zte.cache.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>FME日志记录器</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-05 10:21
 */
public class McuZteLogger {

    private Logger logger;

    /**
     * FME桥对象
     */
    private McuZteBridge mcuZteBridge;

    /**
     * 是否记录websocket日志
     */
    private boolean isRecordLog;

    /**
     * <pre>构造方法</pre>
     *
     * @param mcuZteBridge
     * @param isRecordLog
     * @author lilinhai
     * @since 2021-02-05 10:22
     */
    McuZteLogger(McuZteBridge mcuZteBridge, boolean isRecordLog) {
        this.mcuZteBridge = mcuZteBridge;
        this.isRecordLog = isRecordLog;
        this.logger = LoggerFactory.getLogger("McuPlcLogger[" + this.mcuZteBridge + "]");
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
