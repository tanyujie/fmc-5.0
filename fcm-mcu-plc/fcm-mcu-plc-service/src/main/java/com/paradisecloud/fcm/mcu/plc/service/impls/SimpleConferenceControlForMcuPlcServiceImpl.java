package com.paradisecloud.fcm.mcu.plc.service.impls;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.plc.attendee.model.operation.*;
import com.paradisecloud.fcm.mcu.plc.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.plc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IAttendeeForMcuPlcService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.ISimpleConferenceControlForMcuPlcService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 简单会议控制服务类
 */
@Service
public class SimpleConferenceControlForMcuPlcServiceImpl implements ISimpleConferenceControlForMcuPlcService {

    @Resource
    IAttendeeForMcuPlcService attendeeForMcuPlcService;

    /**
     * 点名
     *
     * @param conferenceContext 会议号
     * @param attendeeId       参会者ID
     */
    @Override
    public RestResponse rollCall(McuPlcConferenceContext conferenceContext, String attendeeId) {
        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            return RestResponse.fail("主会场未设置，无法进行点名操作！");
        }

        if (conferenceContext != null) {
            AttendeeForMcuPlc rollCallAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (rollCallAttendee != null) {
                AttendeeOperation old = conferenceContext.getAttendeeOperation();
                AttendeeOperation attendeeOperation = new RollCallAttendeeOperation(conferenceContext, rollCallAttendee);
                conferenceContext.setAttendeeOperation(attendeeOperation);
                old.cancel();
            }
        } else {
            return RestResponse.fail("该会议无法进行点名操作！");
        }
        return RestResponse.success();
    }

    /**
     * 更新显示布局
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum      分屏
     * @param broadcast        是否广播
     */
    @Override
    public RestResponse updateDefaultViewConfigInfo(McuPlcConferenceContext conferenceContext, int splitScreenNum, boolean broadcast) {
        return updateDefaultViewConfigInfo(conferenceContext, splitScreenNum, broadcast, true);
    }

    /**
     * 更新显示布局
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum      分屏
     * @param broadcast        是否广播
     * @param showSelfView     是否显示本端画面
     */
    @Override
    public RestResponse updateDefaultViewConfigInfo(McuPlcConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView) {
        if (conferenceContext != null) {
            SplitScreen splitScreen = convertSplitScreen(splitScreenNum);
            DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
            defaultAttendeeOperation.setDefaultViewLayout(splitScreen.getLayout());
            if (broadcast) {
                defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.YES.getValue());
            } else {
                defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
            }
            if (!conferenceContext.isSupportBroadcast()) {
                defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
            }
            defaultAttendeeOperation.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.SELF.getValue());
            defaultAttendeeOperation.setDefaultViewIsFill(YesOrNo.YES.getValue());
            defaultAttendeeOperation.setDefaultViewPollingInterval(10);
            defaultAttendeeOperation.initSplitScreen();

            AttendeeOperation old = conferenceContext.getAttendeeOperation();
            conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
            old.cancel();
        }
        return RestResponse.success();
    }

    /**
     * 轮询
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum      分屏
     * @param broadcast        是否广播
     */
    @Override
    public RestResponse polling(McuPlcConferenceContext conferenceContext, int splitScreenNum, boolean broadcast) {
        return polling(conferenceContext, splitScreenNum, broadcast, true);
    }

    /**
     * 轮询
     *
     * @param conferenceContext 会议号
     * @param splitScreenNum      分屏
     * @param broadcast        是否广播
     * @param showSelfView     是否显示本端画面
     */
    @Override
    public RestResponse polling(McuPlcConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView) {
        if (conferenceContext != null) {
            SplitScreen splitScreen = convertSplitScreen(splitScreenNum);
            McuPlcConferenceContext mainConferenceContext = McuPlcConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
            if (mainConferenceContext.getMasterAttendee() == null || !mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
                return RestResponse.fail("主会场未设置或未呼入,无法进行轮询操作！");
            }

            AttendeeOperation oldAttendeeOperation = conferenceContext.getAttendeeOperation();
            PollingScheme pollingScheme = new PollingScheme();
            pollingScheme.setInterval(10);
            if (broadcast) {
                pollingScheme.setIsBroadcast(YesOrNo.YES);
            } else {
                pollingScheme.setIsBroadcast(YesOrNo.NO);
            }
            if (!conferenceContext.isSupportBroadcast()) {
                pollingScheme.setIsBroadcast(YesOrNo.NO);
            }
            pollingScheme.setIsFill(YesOrNo.YES);
            pollingScheme.setLayout(splitScreen.getLayout());
            pollingScheme.setPanePlacementSelfPaneMode(PanePlacementSelfPaneMode.SELF);
            pollingScheme.setPollingStrategy(PollingStrategy.GLOBAL);
            AttendeeOperation newAttendeeOperation = new PollingAttendeeOperation(conferenceContext, pollingScheme);
            conferenceContext.setAttendeeOperation(newAttendeeOperation);
            oldAttendeeOperation.cancel();
        }
        return RestResponse.success();
    }

    /**
     * 讨论
     *
     * @param conferenceContext 会议号
     */
    @Override
    public RestResponse discuss(McuPlcConferenceContext conferenceContext) {
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (!(attendeeOperation instanceof DiscussAttendeeOperation)) {
                DiscussAttendeeOperation discussAttendeeOperation = new DiscussAttendeeOperation(conferenceContext);
                conferenceContext.setAttendeeOperation(discussAttendeeOperation);
                attendeeOperation.cancel();
            }
        }
        return RestResponse.success();
    }

    /**
     * 恢复之前默认显示布局
     *
     * @param conferenceContext 会议号
     */
    @Override
    public RestResponse recoveryLastDefaultView(McuPlcConferenceContext conferenceContext) {
        if (conferenceContext != null) {
            AttendeeOperation old = conferenceContext.getAttendeeOperation();
            conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
            old.cancel();
        }
        return RestResponse.success();
    }

    /**
     * 举手
     *
     * @param conferenceContext 会议号
     * @param attendeeId       参会者ID
     * @param raiseHandStatus  举手状态
     * @return
     */
    @Override
    public RestResponse raiseHand(McuPlcConferenceContext conferenceContext, String attendeeId, RaiseHandStatus raiseHandStatus) {
        if (conferenceContext != null) {
            attendeeForMcuPlcService.raiseHand(conferenceContext.getId(), attendeeId, raiseHandStatus);
        }
        return RestResponse.success();
    }

    /**
     * 设置横幅
     *
     * @param conferenceContext 会议号
     * @param text             文字内容
     * @return
     */
    @Override
    public RestResponse setBanner(McuPlcConferenceContext conferenceContext, String text) {
        if (conferenceContext != null) {
            attendeeForMcuPlcService.setMessageBannerText(conferenceContext.getConferenceNumber(), text);
        }
        return RestResponse.success();
    }

    /**
     * 对话
     * @author sinhy
     * @since 2021-12-02 12:47
     * @param conferenceContext
     * @param attendeeId
     */
    @Override
    public RestResponse talk(McuPlcConferenceContext conferenceContext, String attendeeId)
    {
        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            return RestResponse.fail("主会场未设置，无法进行对话操作！");
        }
        if (conferenceContext != null) {
            AttendeeForMcuPlc talkAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            }
            AttendeeOperation old = conferenceContext.getAttendeeOperation();
            if (old instanceof TalkAttendeeOperation) {
                TalkAttendeeOperation talkAttendeeOperation = (TalkAttendeeOperation) old;
                if (talkAttendeeOperation.getTalkUuid().equals(talkAttendee.getParticipantUuid())) {
                    return RestResponse.success();
                }
            }
            old.cancel();
            AttendeeOperation attendeeOperation = new TalkAttendeeOperation(conferenceContext, talkAttendee);
            conferenceContext.setAttendeeOperation(attendeeOperation);
        }
        return RestResponse.success();
    }

    /**
     *
     * @param num
     * @return
     */
    private SplitScreen convertSplitScreen(int num) {
        if (num == 1) {
            return new OneSplitScreen(num);
        } else if (num == 4) {
            return new FourSplitScreen(num);
        } else if (num == 6) {
            return new OnePlusFiveSplitScreen(num);
        } else if (num == 8) {
            return new OnePlusSevenSplitScreen(num);
        } else if (num == 9) {
            return new NineSplitScreen(num);
        } else if (num == 10) {
            return new OnePlusNineSplitScreen(num);
        } else if (num == 16) {
            return new SixteenSplitScreen(num);
        } else if (num == 25) {
            return new TwentyFiveSplitScreen(num);
        } else {
            return new AutomaticSplitScreen();
        }
    }
}
