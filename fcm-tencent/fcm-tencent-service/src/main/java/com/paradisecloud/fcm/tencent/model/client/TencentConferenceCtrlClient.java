package com.paradisecloud.fcm.tencent.model.client;

import com.google.gson.reflect.TypeToken;
import com.paradisecloud.fcm.tencent.model.reponse.MeetingStatusResponse;
import com.paradisecloud.fcm.tencent.model.reponse.RoomCallReponse;
import com.paradisecloud.fcm.tencent.model.reponse.RoomResponse;
import com.paradisecloud.fcm.tencent.model.request.*;
import com.tencentcloudapi.wemeet.common.RequestSender;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.BaseResponse;

/**
 * @author nj
 * @date 2023/7/13 10:11
 */
public class TencentConferenceCtrlClient {

    private final RequestSender sender;

    public TencentConferenceCtrlClient(RequestSender sender) {
        this.sender = sender;
    }

    public MeetingStatusResponse modifyConferenceStatus(ModifyConferenceRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<MeetingStatusResponse>() {
        });
    }

    public MeetingStatusResponse queryMeetingStatus(QueryMeetingStatusRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<MeetingStatusResponse>() {
        });
    }

    public BaseResponse kickout(RemoveParticipantRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }


    public BaseResponse muteParticpant(MuteParticipantRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse videoParticpant(VideoParticipantRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse screenSharedClose(ScreenSharedParticipantRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse nameChange(NameParticipantRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse hostsAction(HostsParticipantRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public RoomResponse queryRooms(QueryRoomsRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<RoomResponse>() {
        });
    }

    public RoomCallReponse roomsInvite(RoomCallRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<RoomCallReponse>() {
        });
    }

}
