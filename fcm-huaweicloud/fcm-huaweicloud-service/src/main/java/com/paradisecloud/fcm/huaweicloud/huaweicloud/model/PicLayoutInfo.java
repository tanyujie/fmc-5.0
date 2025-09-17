package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import com.huaweicloud.sdk.meeting.v1.model.SubPicLayoutInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/3/5 15:59
 */
@Data
@NoArgsConstructor
public class PicLayoutInfo {

    private Integer x;
    private Integer y;
    public  List<SubPicLayoutInfo> subPicLayoutInfoList;
}
