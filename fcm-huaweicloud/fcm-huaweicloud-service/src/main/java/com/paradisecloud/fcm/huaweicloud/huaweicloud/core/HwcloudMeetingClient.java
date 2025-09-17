package com.paradisecloud.fcm.huaweicloud.huaweicloud.core;

import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.meeting.v1.MeetingClient;
import com.huaweicloud.sdk.meeting.v1.MeetingCredentials;
import com.huaweicloud.sdk.meeting.v1.model.AuthTypeEnum;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MyMeetingClient;

/**
 * @author nj
 * @date 2024/2/27 17:11
 */
public class HwcloudMeetingClient {

    private String hwcloudAppId;
    private String hwcloudAppKey;
    private MeetingClient managerClient;
    private MyMeetingClient myMeetingClient;
    private MeetingClient userClient;
    private String hwcloudEndPoint = "https://api.meeting.huaweicloud.com";


    public HwcloudMeetingClient(String hwcloudAppId, String hwcloudAppKey) {
        this.hwcloudAppId = hwcloudAppId;
        this.hwcloudAppKey = hwcloudAppKey;
    }

    public void createCorpManagerClient() {
        ICredential auth = new MeetingCredentials()
                .withAuthType(AuthTypeEnum.APP_ID)
                .withAppId(hwcloudAppId)
                .withAppKey(hwcloudAppKey);

        managerClient = MeetingClient.newBuilder()
                .withCredential(auth)
                .withEndpoint(hwcloudEndPoint)
                .build();
    }

    public void createCorpMyManagerClient() {
        ICredential auth = new MeetingCredentials()
                .withAuthType(AuthTypeEnum.APP_ID)
                .withAppId(hwcloudAppId)
                .withAppKey(hwcloudAppKey);

        myMeetingClient = MyMeetingClient.builder()
                .withCredential(auth)
                .withEndpoint(hwcloudEndPoint)
                .build();
    }

    public void createCorpUserClient(String userId) {
        ICredential auth = new MeetingCredentials()
                .withAuthType(AuthTypeEnum.APP_ID)
                .withAppId(hwcloudAppId)
                .withAppKey(hwcloudAppKey)
                .withUserId(userId);

        userClient = MeetingClient.newBuilder()
                .withCredential(auth)
                .withEndpoint(hwcloudEndPoint)
                .build();
    }

    public MeetingClient getManagerClient() {
        return managerClient;
    }

    public MeetingClient getUserClient() {
        return userClient;
    }

    public MyMeetingClient getMyMeetingClient() {
        return myMeetingClient;
    }
}
