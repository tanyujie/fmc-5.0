package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.*;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IAttendeeForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.ISimpleConferenceControlForMcuZjService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 简单会议控制服务类
 */
@Service
public class SimpleConferenceControlForMcuZjServiceImpl implements ISimpleConferenceControlForMcuZjService {

    @Resource
    IAttendeeForMcuZjService attendeeForMcuZjService;

    /**
     * 点名
     *
     * @param conferenceContext 会议号
     * @param attendeeId       参会者ID
     */
    @Override
    public RestResponse rollCall(McuZjConferenceContext conferenceContext, String attendeeId) {
        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().isMeetingJoined())
        {
            return RestResponse.fail("主会场未设置，无法进行点名操作！");
        }

        if (conferenceContext != null) {
            SourceTemplate sourceTemplate = conferenceContext.getMcuZjBridge().getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate == null || !sourceTemplate.isSupportRollCall()) {
                return RestResponse.fail("该会议不支持进行点名操作！");
            }
            AttendeeForMcuZj rollCallAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (rollCallAttendee != null) {
                AttendeeOperation old = conferenceContext.getAttendeeOperation();
                AttendeeOperation attendeeOperation = new RollCallAttendeeOperation(conferenceContext, rollCallAttendee);
                conferenceContext.setAttendeeOperation(attendeeOperation);
                old.cancel();

                // 观众
                if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                    conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
                }
                AttendeeOperation oldForGuest = conferenceContext.getAttendeeOperationForGuest();
                oldForGuest.cancel();
                conferenceContext.setAttendeeOperationForGuest(attendeeOperation);
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
    public RestResponse updateDefaultViewConfigInfo(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast) {
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
    public RestResponse updateDefaultViewConfigInfo(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView) {
        if (conferenceContext != null) {
            if (conferenceContext.isSingleView()) {
                return updateDefaultViewConfigInfoForGuest(conferenceContext, splitScreenNum, broadcast, showSelfView);
            }
            McuZjBridge mcuZjBridge = conferenceContext.getMcuZjBridge();
            SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate != null) {
                if (sourceTemplate.getMax_spk_mosic() < splitScreenNum) {
                    return RestResponse.fail("该会议（入会参数）不支持该视图布局");
                }
            }
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
            // 观众
            if (defaultAttendeeOperation.getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
                conferenceContext.setAttendeeOperationForGuest(defaultAttendeeOperation);
            } else {
                if (!(conferenceContext.getDefaultViewOperationForGuest() instanceof DefaultAttendeeOperationForGuest)) {
                    conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                }
            }
        }
        return RestResponse.success();
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
    public RestResponse updateDefaultViewConfigInfoForGuest(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView) {
        if (conferenceContext != null) {
            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                if (defaultAttendeeOperation.getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
                    return RestResponse.fail("广播中不能单独编辑观众默认视图");
                }
            }
            McuZjBridge mcuZjBridge = conferenceContext.getMcuZjBridge();
            SourceTemplate sourceTemplate = mcuZjBridge.getSourceTemplateById(conferenceContext.getResourceTemplateId());
            if (sourceTemplate != null) {
                if (sourceTemplate.getMax_guest_mosic() < splitScreenNum) {
                    return RestResponse.fail("该会议（入会参数）不支持该视图布局");
                }
            }
            SplitScreen splitScreen = convertSplitScreen(splitScreenNum);
            DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
            defaultAttendeeOperation.setDefaultViewLayout(splitScreen.getLayout());
            defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.NO.getValue());
            defaultAttendeeOperation.setDefaultViewIsDisplaySelf(PanePlacementSelfPaneMode.SELF.getValue());
            defaultAttendeeOperation.setDefaultViewIsFill(YesOrNo.YES.getValue());
            defaultAttendeeOperation.setDefaultViewPollingInterval(10);
            defaultAttendeeOperation.initSplitScreen();

            conferenceContext.setDefaultViewOperationForGuest(defaultAttendeeOperation);
            if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
            }
            AttendeeOperation old = conferenceContext.getAttendeeOperationForGuest();
            old.cancel();
            conferenceContext.setAttendeeOperationForGuest(defaultAttendeeOperation);
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
    public RestResponse polling(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast) {
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
    public RestResponse polling(McuZjConferenceContext conferenceContext, int splitScreenNum, boolean broadcast, boolean showSelfView) {
        if (conferenceContext != null) {
            SplitScreen splitScreen = convertSplitScreen(splitScreenNum);
            McuZjConferenceContext mainConferenceContext = McuZjConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
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

            // 观众
            if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
            }
            if (pollingScheme.getIsBroadcast() == YesOrNo.YES || conferenceContext.isSingleView()) {
                AttendeeOperation oldForGuest = conferenceContext.getAttendeeOperationForGuest();
                oldForGuest.cancel();
                conferenceContext.setAttendeeOperationForGuest(newAttendeeOperation);
            } else {
                if (!(conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest)) {
                    conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                }
            }
        }
        return RestResponse.success();
    }

    /**
     * 讨论
     *
     * @param conferenceContext 会议号
     */
    @Override
    public RestResponse discuss(McuZjConferenceContext conferenceContext) {
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
    public RestResponse recoveryLastDefaultView(McuZjConferenceContext conferenceContext) {
        if (conferenceContext != null) {
            AttendeeOperation old = conferenceContext.getAttendeeOperation();
            conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
            old.cancel();

            // 观众
            if (conferenceContext.getDefaultViewOperation().getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
                conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperation());
            } else {
                if (!(conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest)) {
                    conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                }
            }
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
    public RestResponse raiseHand(McuZjConferenceContext conferenceContext, String attendeeId, RaiseHandStatus raiseHandStatus) {
        if (conferenceContext != null) {
            attendeeForMcuZjService.raiseHand(conferenceContext.getId(), attendeeId, raiseHandStatus);
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
    public RestResponse setBanner(McuZjConferenceContext conferenceContext, String text) {
        if (conferenceContext != null) {
            attendeeForMcuZjService.setMessageBannerText(conferenceContext.getId(), text);
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
    public RestResponse talk(McuZjConferenceContext conferenceContext, String attendeeId)
    {
        if (conferenceContext.getMasterAttendee() == null || conferenceContext.getMasterAttendee().getMeetingStatus() != AttendeeMeetingStatus.IN.getValue())
        {
            return RestResponse.fail("主会场未设置，无法进行对话操作！");
        }
        if (conferenceContext != null) {
            AttendeeForMcuZj talkAttendee = conferenceContext.getAttendeeById(attendeeId);
            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            }
            AttendeeOperation old = conferenceContext.getAttendeeOperation();
            if (old instanceof TalkAttendeeOperation) {
                TalkAttendeeOperation talkAttendeeOperation = (TalkAttendeeOperation) old;
                if (talkAttendeeOperation.getTalkUserId().equals(talkAttendee.getEpUserId())) {
                    return RestResponse.success();
                }
            }
            old.cancel();
            AttendeeOperation attendeeOperation = new TalkAttendeeOperation(conferenceContext, talkAttendee);
            conferenceContext.setAttendeeOperation(attendeeOperation);

            // 观众
            if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperationForGuest) {
                conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getAttendeeOperationForGuest());
            }
            AttendeeOperation oldForGuest = conferenceContext.getAttendeeOperationForGuest();
            oldForGuest.cancel();
            conferenceContext.setAttendeeOperationForGuest(attendeeOperation);
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
