/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultChooseToSeeAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-02-22 18:16
 * @version  V1.0
 */
package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.sdk.meeting.v1.model.MultiPicDisplayDO;
import com.huaweicloud.sdk.meeting.v1.model.PicInfoNotify;
import com.huaweicloud.sdk.meeting.v1.model.RestSubscriberInPic;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.SmcParitipantsStateRep;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>会议室默认视图</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-22 18:16
 */
public class DefaultAttendeeOperation extends DefaultViewOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 18:16
     */
    private static final long serialVersionUID = 1L;
    private final List<JSONObject> defaultViewDepts = new ArrayList<>();
    private JSONObject jsonObject;
    private Boolean multiPicSaveOnly;


    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-12 17:48
     */
    public DefaultAttendeeOperation(HwcloudConferenceContext conferenceContext) {
        super(conferenceContext);
    }


    public DefaultAttendeeOperation(HwcloudConferenceContext conferenceContext, JSONObject jsonObject, Boolean multiPicSaveOnly) {
        super(conferenceContext);
        this.jsonObject = jsonObject;
        this.multiPicSaveOnly = multiPicSaveOnly;
    }

    public void initSplitScreen() {

    }

    @Override
    public void operate() {


        if (jsonObject != null) {
            MultiPicDisplayDO multiPicDisplayDO = JSONObject.parseObject(JSON.toJSONString(jsonObject), MultiPicDisplayDO.class);

            HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
            List<PicInfoNotify> picInfoNotifies = multiPicDisplayDO.getSubscriberInPics();
            List<RestSubscriberInPic> subscriberInPics = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(picInfoNotifies)) {
                for (PicInfoNotify picInfoNotify : picInfoNotifies) {
                    RestSubscriberInPic restSubscriberInPic = new RestSubscriberInPic();
                    restSubscriberInPic.setIndex(picInfoNotify.getIndex());
                    restSubscriberInPic.setIsAssistStream(picInfoNotify.getShare());
                    restSubscriberInPic.setSubscriber(picInfoNotify.getId());
                    subscriberInPics.add(restSubscriberInPic);
                }

            }

            hwcloudMeetingBridge.getMeetingControl().setCustomMultiPicture(hwcloudMeetingBridge.getTokenInfo().getToken(), conferenceContext.getMeetingId(), multiPicDisplayDO.getSwitchTime(), multiPicDisplayDO.getPicLayoutInfo(), multiPicDisplayDO.getManualSet(), multiPicDisplayDO.getImageType(), subscriberInPics, multiPicSaveOnly);


        }


    }


    @Override
    public void cancel() {
        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        hwcloudMeetingBridge.getMeetingControl().cancelBroadcast(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID());
    }


    public List<JSONObject> getDefaultViewDepts() {
        return defaultViewDepts;
    }


    public void addDefaultViewDept(JSONObject dept) {
        this.defaultViewDepts.add(dept);
    }

    @Override
    public boolean contains(SmcParitipantsStateRep.ContentDTO attendee) {
        for (CellScreen cellScreen : splitScreen.getCellScreens()) {
            if (cellScreen.getLastOperationAttendee() == attendee) {
                return true;
            }
        }
        return false;


    }


}
