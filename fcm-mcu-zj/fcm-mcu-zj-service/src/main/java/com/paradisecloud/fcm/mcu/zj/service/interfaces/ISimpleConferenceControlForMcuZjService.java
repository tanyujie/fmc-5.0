package com.paradisecloud.fcm.mcu.zj.service.interfaces;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.RaiseHandStatus;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;

public interface ISimpleConferenceControlForMcuZjService {

    /**
     * 点名
     *
     * @param conferenceContext 会议号
     * @param attendeeId 参会者ID
     */
    RestResponse rollCall(McuZjConferenceContext conferenceContext, String attendeeId);

    /**
     * 更新显示布局
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum 分屏
     * @param broadcast 是否广播
     */
    RestResponse updateDefaultViewConfigInfo(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast);

    /**
     * 更新显示布局
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum 分屏
     * @param broadcast 是否广播
     * @param showSelfView 是否显示本端画面
     */
    RestResponse updateDefaultViewConfigInfo(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView);

    /**
     * 更新显示布局
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum 分屏
     * @param broadcast 是否广播
     * @param showSelfView 是否显示本端画面
     */
    RestResponse updateDefaultViewConfigInfoForGuest(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView);

    /**
     * 轮询
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum 分屏
     * @param broadcast 是否广播
     */
    RestResponse polling(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast);

    /**
     * 轮询
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum 分屏
     * @param broadcast 是否广播
     * @param showSelfView 是否显示本端画面
     */
    RestResponse polling(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView);

    /**
     * 讨论
     *
     * @param conferenceContext 会议号
     */
    RestResponse discuss(McuZjConferenceContext conferenceContext);

    /**
     * 恢复之前默认显示布局
     *
     * @param conferenceContext 会议号
     */
    RestResponse recoveryLastDefaultView(McuZjConferenceContext conferenceContext);

    /**
     * 举手
     *
     * @param conferenceContext 会议号
     * @param attendeeId 参会者ID
     * @param raiseHandStatus 举手状态
     * @return
     */
    RestResponse raiseHand(McuZjConferenceContext conferenceContext, String attendeeId, RaiseHandStatus raiseHandStatus);

    /**
     * 设置横幅
     *
     * @param conferenceContext 会议号
     * @param text 文字内容
     * @return
     */
    RestResponse setBanner(McuZjConferenceContext conferenceContext, String text);

    /**
     * 对话
     *
     * @param conferenceContext 会议号
     * @param attendeeId 参会者ID
     */
    RestResponse talk(McuZjConferenceContext conferenceContext, String attendeeId);
}
