package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client;

import com.huaweicloud.sdk.core.ClientBuilder;
import com.huaweicloud.sdk.core.HcClient;
import com.huaweicloud.sdk.core.exception.SdkException;
import com.huaweicloud.sdk.meeting.v1.MeetingClient;
import com.huaweicloud.sdk.meeting.v1.model.SetCustomMultiPictureResponse;
import com.huaweicloud.sdk.meeting.v1.model.SetParticipantViewRequest;
import com.huaweicloud.sdk.meeting.v1.model.SetParticipantViewResponse;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.request.SetCustomMultiPictureRequest2;

/**
 * @author nj
 * @date 2024/3/6 16:46
 */
public class MyMeetingClient extends MeetingClient {


    public MyMeetingClient(Object object) {
        super((HcClient)object);
    }

    public static ClientBuilder<MyMeetingClient> builder() {
        ClientBuilder<MyMeetingClient> clientBuilder = new ClientBuilder(MyMeetingClient::new, "MeetingCredentials");
        return clientBuilder;
    }

    public SetCustomMultiPictureResponse setCustomMultiPicture2(SetCustomMultiPictureRequest2 request) {
        return (SetCustomMultiPictureResponse)this.hcClient.syncInvokeHttp(request, MeetingMeta2.setCustomMultiPicture2);
    }

    public SetParticipantViewResponse setParticipantView2(SetParticipantViewRequest request) {
        return (SetParticipantViewResponse)this.hcClient.syncInvokeHttp(request, MeetingMeta2.setParticipantView);
    }


}
