package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2024/3/18 16:35
 */

@Data
@NoArgsConstructor
public class PicB {

    List<MultiPicDisplayVo> picLayouts=new ArrayList();
    List<MultiPicDisplayVo> picDisplay=new ArrayList();
}
