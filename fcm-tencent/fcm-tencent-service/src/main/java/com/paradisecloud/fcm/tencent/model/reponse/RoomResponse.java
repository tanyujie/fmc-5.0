package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.paradisecloud.fcm.tencent.model.MeetingRoom;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/8/10 16:51
 */
@NoArgsConstructor
@Data
public class RoomResponse extends BaseResponse {

    @Expose
    @SerializedName("meeting_room_list")
    private List<MeetingRoom> meetingRoomList;
    @Expose
    @SerializedName("current_size")
    private Integer currentSize;
    @Expose
    @SerializedName("total_count")
    private Integer totalCount;
    @Expose
    @SerializedName("current_page")
    private Integer currentPage;
    @Expose
    @SerializedName("total_page")
    private Integer totalPage;


}
