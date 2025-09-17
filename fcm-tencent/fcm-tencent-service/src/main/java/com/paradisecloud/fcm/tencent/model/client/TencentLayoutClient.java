package com.paradisecloud.fcm.tencent.model.client;

import com.google.gson.reflect.TypeToken;
import com.paradisecloud.fcm.tencent.model.reponse.*;
import com.paradisecloud.fcm.tencent.model.request.layout.*;
import com.tencentcloudapi.wemeet.common.RequestSender;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.BaseResponse;

/**
 * @author nj
 * @date 2023/7/14 15:00
 */
public class TencentLayoutClient {
    private final RequestSender sender;

    public TencentLayoutClient(RequestSender sender) {
        this.sender = sender;
    }

    public LayoutTemplatesResponse queryLayoutTemplates(QueryLayoutTemplatesAllRequest request) throws WemeetSdkException {
        return (LayoutTemplatesResponse)this.sender.request(request, new TypeToken<LayoutTemplatesResponse>() {
        });
    }

    public MeetingAdvancedLayoutResponse queryMeetingLayoutsAdvanced(QueryMeetingLayoutListAdvancedRequest request) throws WemeetSdkException {
        return (MeetingAdvancedLayoutResponse)this.sender.request(request, new TypeToken<MeetingAdvancedLayoutResponse>() {
        });
    }
    public MeetingAdvancedLayoutResponse queryMeetingLayouts(QueryMeetingLayoutListRequest request) throws WemeetSdkException {
        return (MeetingAdvancedLayoutResponse)this.sender.request(request, new TypeToken<MeetingAdvancedLayoutResponse>() {
        });
    }


    public UserApplyingLayoutResponse queryUserApplyingLayout(QueryUserApplyingLayoutRequest request) throws WemeetSdkException {
        return (UserApplyingLayoutResponse)this.sender.request(request, new TypeToken<UserApplyingLayoutResponse>() {
        });
    }

    public AddMeetingLayoutReponse addMeetingLayout(AddMeetingLayoutRequest request) throws WemeetSdkException {
        return (AddMeetingLayoutReponse)this.sender.request(request, new TypeToken<AddMeetingLayoutReponse>() {
        });
    }


    public BaseResponse changeMeetingLayout(ChangeMeetingLayoutRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse changeAdMeetingLayout(ChangeAdancedMeetingLayoutRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse deleteMeetingLayout(DeleteMeetingLayoutRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }
    public BaseResponse deleteMeetingLayoutBatch(DeleteMeetingLayoutBatchRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse deleteMeetingLayoutADBatch(DeleteMeetingLayoutADBatchRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public MeetingBackgroundResponse addMeetingBackground(AddMeetingBackgrouds request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<MeetingBackgroundResponse>() {
        });
    }

    public BaseResponse addDefaultGrounds(SetingDefaultBackgrounds request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }


    public BaseResponse deleteBackgrounds(DeleteBackgrounds request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public BaseResponse deleteBackgroundBatch(DeleteBackgroundsBatch request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }


    public MeetingBackgroundResponse queryBackgroundsList(QueryBackGroundsList request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<MeetingBackgroundResponse>() {
        });
    }

    public BaseResponse applyingLayout(ApplyingLayoutRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<BaseResponse>() {
        });
    }

    public MeetingAdvancedLayoutResponse addAdvancedLayout(AddMeetingAdancedLayoutRequest request) throws WemeetSdkException {
        return this.sender.request(request, new TypeToken<MeetingAdvancedLayoutResponse>() {
        });
    }
}
