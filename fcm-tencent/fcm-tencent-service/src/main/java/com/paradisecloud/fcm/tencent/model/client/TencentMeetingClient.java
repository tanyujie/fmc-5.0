package com.paradisecloud.fcm.tencent.model.client;

import com.google.gson.reflect.TypeToken;
import com.paradisecloud.fcm.tencent.model.reponse.*;
import com.paradisecloud.fcm.tencent.model.request.*;
import com.paradisecloud.fcm.tencent.model.request.layout.DeleteBackgroundsBatch;
import com.tencentcloudapi.wemeet.common.RequestSender;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import com.tencentcloudapi.wemeet.models.meeting.*;

/**
 * @author nj
 * @date 2023/7/11 10:13
 */
public class TencentMeetingClient  {

    private final RequestSender sender;

    public TencentMeetingClient(RequestSender sender) {
        this.sender = sender;
    }

    public RealTimeParticipantsResponse queryRealTimeParticipantsById(QueryRealTimeParticipantsRequest request) throws WemeetSdkException {
        return (RealTimeParticipantsResponse)this.sender.request(request, new TypeToken<RealTimeParticipantsResponse>() {
        });
    }

    public QueryMeetingDetailResponse createMeeting(CreateMeetingRequest request) throws WemeetSdkException {
        return (QueryMeetingDetailResponse)this.sender.request(request, new TypeToken<QueryMeetingDetailResponse>() {
        });
    }

    public QueryMeetingDetailResponse createMeeting(CreateMeetingRequestLocal request) throws WemeetSdkException {
        return (QueryMeetingDetailResponse)this.sender.request(request, new TypeToken<QueryMeetingDetailResponse>() {
        });
    }

    public QueryMeetingDetailResponse queryMeetingById(QueryMeetingByIdRequest request) throws WemeetSdkException {
        return (QueryMeetingDetailResponse)this.sender.request(request, new TypeToken<QueryMeetingDetailResponse>() {
        });
    }


    public QueryMeetingInfoResponse queryMeetingInfoById(QueryMeetingByIdRequest request) throws WemeetSdkException {
        return (QueryMeetingInfoResponse)this.sender.request(request, new TypeToken<QueryMeetingInfoResponse>() {
        });
    }

    public QueryMeetingDetailResponse queryMeetingByCode(QueryMeetingByCodeRequest request) throws WemeetSdkException {
        return (QueryMeetingDetailResponse)this.sender.request(request, new TypeToken<QueryMeetingDetailResponse>() {
        });
    }

    public BaseResponse cancelMeeting(CancelMeetingRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse dismissMeeting(TencentDismissMeetingRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public QueryMeetingDetailResponse modifyMeeting(ModifyMeetingRequest request) throws WemeetSdkException {
        return (QueryMeetingDetailResponse)this.sender.request(request, new TypeToken<QueryMeetingDetailResponse>() {
        });
    }

    public QueryMeetingParticipantsResponse queryParticipants(QueryParticipantsRequest request) throws WemeetSdkException {
        return (QueryMeetingParticipantsResponse)this.sender.request(request, new TypeToken<QueryMeetingParticipantsResponse>() {
        });
    }

    public QueryUserMeetingsResponse queryUserMeetings(QueryUserMeetingsRequest request) throws WemeetSdkException {
        return (QueryUserMeetingsResponse)this.sender.request(request, new TypeToken<QueryUserMeetingsResponse>() {
        });
    }

    public QueryEndedMeetingsResponse queryEndedMeetings(QueryEndedMeetingsRequest request) throws WemeetSdkException {
        return (QueryEndedMeetingsResponse)this.sender.request(request, new TypeToken<QueryEndedMeetingsResponse>() {
        });
    }

    /**
     * MRA 呼叫挂断
     * @param request
     * @return
     * @throws WemeetSdkException
     */
    public BaseResponse hangUp(HangupRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    /**
     * 对 MRA 进行举手和手放下操作
     * @param request
     * @return
     * @throws WemeetSdkException
     */
    public BaseResponse raiseHand(RaiseHandRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    /**
     * 查询 MRA 状态信息
     * @param request
     * @return
     * @throws WemeetSdkException
     */
    public MRAstatusResponse queryMRA_participantStatus(QueryMRAStatusRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<MRAstatusResponse>() {
        });
    }

    /**
     * 切换 MRA 默认布局
     * @param request
     * @return
     * @throws WemeetSdkException
     */
    public BaseResponse mraDefaultLayout(MraDefaultLayoutRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    /**
     * 查询设备列表
     * @param request
     * @return
     * @throws WemeetSdkException
     */
    public QueryRoomsDeviceResponse queryDevices(QueryRoomsDeviceRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<QueryRoomsDeviceResponse>() {
        });
    }

    /**
     * 查询实时等候室成员
     * @param request
     * @return
     * @throws WemeetSdkException
     */
    public WaitingRoomResponse queryWaitingRoomParticipants(QueryWaitingRoomRealRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<WaitingRoomResponse>() {
        });
    }


    /**
     * 用户等候室设置
     * @param request
     * @return
     * @throws WemeetSdkException
     */
    public BaseResponse waitingRoomParticipantSetiing(WaitingRoomParticipantSetiingReq request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

}
