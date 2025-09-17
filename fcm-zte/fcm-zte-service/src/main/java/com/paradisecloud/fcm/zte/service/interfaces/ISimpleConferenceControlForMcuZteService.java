package com.paradisecloud.fcm.zte.service.interfaces;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.RaiseHandStatus;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;

public interface ISimpleConferenceControlForMcuZteService {

    /**
     * 点名
     *
     * @param conferenceContext 会议号
     * @param attendeeId 参会者ID
     */
    RestResponse rollCall(McuZteConferenceContext conferenceContext, String attendeeId);

    /**
     * 更新显示布局
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum 分屏
     * @param broadcast 是否广播
     */
    RestResponse updateDefaultViewConfigInfo(McuZteConferenceContext conferenceContext, int splitScreenNum, boolean broadcast);

    /**
     * 更新显示布局
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum 分屏
     * @param broadcast 是否广播
     * @param showSelfView 是否显示本端画面
     */
    RestResponse updateDefaultViewConfigInfo(McuZteConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView);

    /**
     * 轮询
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum 分屏
     * @param broadcast 是否广播
     */
    RestResponse polling(McuZteConferenceContext conferenceContext, int splitScreenNum, boolean broadcast);

    /**
     * 轮询
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum 分屏
     * @param broadcast 是否广播
     * @param showSelfView 是否显示本端画面
     */
    RestResponse polling(McuZteConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView);

    /**
     * 讨论
     *
     * @param conferenceContext 会议号
     */
    RestResponse discuss(McuZteConferenceContext conferenceContext);

    /**
     * 恢复之前默认显示布局
     *
     * @param conferenceContext 会议号
     */
    RestResponse recoveryLastDefaultView(McuZteConferenceContext conferenceContext);

    /**
     * 举手
     *
     * @param conferenceContext 会议号
     * @param attendeeId 参会者ID
     * @param raiseHandStatus 举手状态
     * @return
     */
    RestResponse raiseHand(McuZteConferenceContext conferenceContext, String attendeeId, RaiseHandStatus raiseHandStatus);

    /**
     * 设置横幅
     *
     * @param conferenceContext 会议号
     * @param text 文字内容
     * @return
     */
    RestResponse setBanner(McuZteConferenceContext conferenceContext, String text);

    /**
     * 对话
     *
     * @param conferenceContext 会议号
     * @param attendeeId 参会者ID
     */
    RestResponse talk(McuZteConferenceContext conferenceContext, String attendeeId);
}
