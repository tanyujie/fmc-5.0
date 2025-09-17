package com.paradisecloud.fcm.huaweicloud.huaweicloud.event;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nj
 * @date 2024/2/28 11:37
 */
public enum SubscriptionTypeEnum {

    ConfBasicInfoNotify("会议基本信息订阅", "ConfBasicInfoNotify"),
    ConfDynamicInfoNotify("会议状态信息订阅", "ConfDynamicInfoNotify"),
    ParticipantsNotify("在线与会者信息订阅", "ParticipantsNotify"),
    AttendeesNotify("预约与会者信息订阅", "AttendeesNotify"),
    SpeakerChangeNotify("发言方状态信息订阅", "SpeakerChangeNotify"),
    NetConditionNotify("会议媒体质量状态订阅", "NetConditionNotify"),
    InviteResultNotify("邀请结果信息订阅", "InviteResultNotify"),
    RealTimeSubtitleNotify("实时字幕信息订阅", "RealTimeSubtitleNotify"),
    CustomMultiPicNotify("多画面信息","CustomMultiPicNotify"),
    WaitingListNotify("等候室成员列表订阅", "WaitingListNotify");

    private final String description;
    private final String code;


    SubscriptionTypeEnum(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public static List<String> getCodeList() {
        return Arrays.stream(values())
                .map(SubscriptionTypeEnum::getCode)
                .collect(Collectors.toUnmodifiableList());

    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
