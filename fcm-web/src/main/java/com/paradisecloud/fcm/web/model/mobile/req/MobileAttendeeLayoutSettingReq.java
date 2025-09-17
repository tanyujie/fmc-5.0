package com.paradisecloud.fcm.web.model.mobile.req;

import com.paradisecloud.fcm.fme.model.busi.attendee.FixedParamValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author nj
 * @date 2023/2/10 10:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileAttendeeLayoutSettingReq implements Serializable {

    /**
     * 会议ID
     */
    @NotBlank
    private String conferenceId;

    /**
     * 与会者
     */
    @NotBlank
    private String attendeeId;

    private String Layout;
}
