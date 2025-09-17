package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;


import com.huaweicloud.sdk.meeting.v1.model.PicInfoNotify;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/3/18 16:27
 */
@Data
@NoArgsConstructor
public class MultiPicDisplayVo {

    private Integer manualSet;
    private String imageType;
    private List<PicInfoNotify> subscriberInPics = null;
    private Integer switchTime;

    private Integer skipEmptyPic;
    private  Boolean autoApplyMultiPic;
    private String name;
}
