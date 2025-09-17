package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import com.huaweicloud.sdk.meeting.v1.model.PicInfoNotify;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/2/28 15:11
 */
@Data
@NoArgsConstructor
public class MultiPicInfoNotify {

    private Integer picNum;
    private Integer period;
    private List<PicInfoNotify> picInfos;
}
