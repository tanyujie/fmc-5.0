package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author nj
 * @date 2023/7/25 15:14
 */
@NoArgsConstructor
@Data
public class QueryRoomsDeviceResponse extends BaseResponse implements Serializable {

    @Expose
    @SerializedName("total_count")
    private Integer totalCount;
    @Expose
    @SerializedName("total_page")
    private Integer totalPage;
    @Expose
    @SerializedName("current_page")
    private Integer currentPage;
    @Expose
    @SerializedName("current_size")
    private Integer currentSize;
    @Expose
    @SerializedName("device_info_list")
    private List<DeviceInfoListDTO> deviceInfoList;

    @NoArgsConstructor
    @Data
    public static class DeviceInfoListDTO {
        @Expose
        @SerializedName("meeting_room_id")
        private String meetingRoomId;
        @Expose
        @SerializedName("rooms_id")
        private String roomsId;
        @Expose
        @SerializedName("meeting_room_name")
        private String meetingRoomName;
        @Expose
        @SerializedName("meeting_room_location")
        private String meetingRoomLocation;
        @Expose
        @SerializedName("device_model")
        private String deviceModel;
        @Expose
        @SerializedName("app_version")
        private String appVersion;
        /**
         * 会议室状态：
         * 0：未激活
         * 1：未绑定
         * 2：空闲
         * 3：使用中
         * 4：离线
         */
        @Expose
        @SerializedName("meeting_room_status")
        private Integer meetingRoomStatus;
        @Expose
        @SerializedName("device_monitor_info")
        private DeviceMonitorInfoDTO deviceMonitorInfo;

        @NoArgsConstructor
        @Data
        public static class DeviceMonitorInfoDTO {
            @Expose
            @SerializedName("camera_status")
            private Boolean cameraStatus;
            @Expose
            @SerializedName("microphone_status")
            private Boolean microphoneStatus;
            @Expose
            @SerializedName("speaker_status")
            private Boolean speakerStatus;
        }
    }
}
