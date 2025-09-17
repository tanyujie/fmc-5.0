package com.paradisecloud.com.fcm.smc.modle.response;

import com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/10/19 10:11
 */
@Data
@NoArgsConstructor
public class VideoSourceRep {

    private String participantId;

    private MultiPicInfoReq.MultiPicInfoDTO multiPicInfo;

}
