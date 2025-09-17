package com.paradisecloud.fcm.service.eunm;

public enum NotifyType {
    MEETING_INVITE("meetingInvite"),// 会议邀请通知
    MEETING_CANCEL("meetingCancel"),// 会议取消通知
    ADMIN_MEETING_CANCEL("adminMeetingCancel"),//管理員会议取消通知
    ADMIN_MEETING_BOOK("adminMeetingBook"),// 会议预约通知
    ADMIN_MEETING_START("adminMeetingStart"),// 会议开始通知
    ADMIN_MEETING_END("adminMeetingEnd"),// 会议结束通知

   //会议通知: 会议主题：{1} 会议时间：{2} 云会议号码：{3} 腾讯会议号码：77161895438 手机一键拨号入会 +8675536550000,77161895438 (中国大陆) 根据您的位置拨号 +86 (0)755 36550000 (中国大陆)
    ADMIN_TENCENT_MEETING_START("adminTencentMeetingStart"),// 騰訊会议開始通知
    ;

    private String type;

    NotifyType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
