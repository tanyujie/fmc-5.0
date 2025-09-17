package com.paradisecloud.fcm.common.constant;

public interface EndReasonsType {

    /**
     * 管理员主动挂断
     */
    int ADMINISTRATOR_HANGS_UP = 1;

    /**
     * 自动结束
     */
    int AUTO_END = 2;

    /**
     * 异常挂断
     */
    int ABNORMAL_END = 3;

    /**
     * 会议空闲时间过长自动结束
     */
    int IDLE_TOO_LONG = 4;

    /**
     * 终端挂断
     */
    int PARTICIPANT_HANGS_UP = 5;
}
