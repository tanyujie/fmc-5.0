package com.paradisecloud.fcm.tencent.model;

/**
 * @author nj
 * @date 2023/7/24 14:34
 */
public enum TencentMeetingStateEnum {
    /**
     * 非法或未知的会议状态
     */
    MEETING_STATE_INVALID,
    /**
     * 会议待开始。会议预定到预定结束时间前，会议尚无人进会。
     */
    MEETING_STATE_INIT,
    /**
     * 会议已取消。主持人主动取消会议，待开始的会议才能取消，且取消的会议无法再进入
     */
    MEETING_STATE_CANCELLED,
    /**
     * 会议已开始。会议中有人则表示会议进行中。
     */
    MEETING_STATE_STARTED,
    /**
     * 会议已删除。会议已过预定结束时间且尚无人进会时，主持人删除会议，已删除的会议无法再进入
     */
    MEETING_STATE_ENDED,
    /**
     * 会议无状态。会议已过预定结束时间，会议尚无人进会
     */
    MEETING_STATE_NULL,
    /**
     * 会议已回收。会议已过预定开始时间30天，则会议号将被后台回收，无法再进入
     */
    MEETING_STATE_RECYCLED
}
