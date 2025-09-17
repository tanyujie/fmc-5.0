package com.paradisecloud.smc3.model.response;

import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
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
