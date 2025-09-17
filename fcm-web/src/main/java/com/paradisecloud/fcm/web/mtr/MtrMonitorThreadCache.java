/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalCache.java
 * Package     : com.paradisecloud.fcm.terminal
 * @author lilinhai
 * @since 2021-01-22 18:06
 * @version  V1.0
 */
package com.paradisecloud.fcm.web.mtr;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.service.conference.cache.BaseWebSocketMessagePusher;

/**
 * <pre>监控线程缓存</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-01-22 18:06
 */
public class MtrMonitorThreadCache {

    public static final String debugSourceServer = "";

    private static final MtrMonitorThreadCache INSTANCE = new MtrMonitorThreadCache();

    private volatile long mtrChangesTime = 0;
    private MtrThread mtrThread = null;

    private volatile long iperf3ServerChangesTime = 0;
    private Iperf3ServerStatusThread iperf3ServerStatusThread = null;
    private volatile boolean iperf3ServerStarted = false;
    private volatile long iperf3ServerRequestTime = 0;
    private Iperf3ServerStartThread iperf3ServerStartThread = null;
    private StringBuilder iperf3ServerMsg = new StringBuilder();
    private volatile String iperf3ServerPid = null;

    /**
     * <pre>构造方法</pre>
     *
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private MtrMonitorThreadCache() {
    }

    public static MtrMonitorThreadCache getInstance() {
        return INSTANCE;
    }

    public long getMtrChangesTime() {
        return mtrChangesTime;
    }

    public void setMtrChangesTime(long mtrChangesTime) {
        this.mtrChangesTime = mtrChangesTime;
    }

    public MtrThread getMtrThread() {
        return mtrThread;
    }

    public void setMtrThread(MtrThread mtrThread) {
        this.mtrThread = mtrThread;
    }

    public long getIperf3ServerChangesTime() {
        return iperf3ServerChangesTime;
    }

    public void setIperf3ServerChangesTime(long iperf3ServerChangesTime) {
        this.iperf3ServerChangesTime = iperf3ServerChangesTime;
    }

    public Iperf3ServerStatusThread getIperf3ServerStatusThread() {
        return iperf3ServerStatusThread;
    }

    public void setIperf3ServerStatusThread(Iperf3ServerStatusThread iperf3ServerStatusThread) {
        this.iperf3ServerStatusThread = iperf3ServerStatusThread;
    }

    public boolean isIperf3ServerStarted() {
        return iperf3ServerStarted;
    }

    public void setIperf3ServerStarted(boolean iperf3ServerStarted) {
        this.iperf3ServerStarted = iperf3ServerStarted;
    }

    public long getIperf3ServerRequestTime() {
        return iperf3ServerRequestTime;
    }

    public void setIperf3ServerRequestTime(long iperf3ServerRequestTime) {
        this.iperf3ServerRequestTime = iperf3ServerRequestTime;
    }

    public Iperf3ServerStartThread getIperf3ServerStartThread() {
        return iperf3ServerStartThread;
    }

    public void setIperf3ServerStartThread(Iperf3ServerStartThread iperf3ServerStartThread) {
        this.iperf3ServerStartThread = iperf3ServerStartThread;
    }

    public void appendServerMsg(String msg) {
        if (!msg.contains("COMMAND") && !msg.contains("LISTEN")) {
            iperf3ServerMsg.append(msg);
            BaseWebSocketMessagePusher.getInstance().pushNetCheckServerMessage(WebsocketMessageType.NET_CHECK_MSG_TEXT, msg);
        }
    }

    public String getServerMsg() {
        return iperf3ServerMsg.toString();
    }

    public void clearServerMsg() {
        iperf3ServerMsg = new StringBuilder();
    }

    public String getIperf3ServerPid() {
        return iperf3ServerPid;
    }

    public void setIperf3ServerPid(String iperf3ServerPid) {
        this.iperf3ServerPid = iperf3ServerPid;
    }
}
