package com.paradisecloud.fcm.web.model.mobile.vo;

import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author nj
 * @date 2022/6/24 16:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileAttendeeFixedParamVo implements Serializable {
    /**
     * 会议ID
     */
    @NotBlank
    private String conferenceId;
    /**
     * 布局参数
     */
    @NotEmpty
    private List<BaseFixedParamValue> params;
    /**
     * 与会者
     */
    @NotBlank
    private String attendeeId;

    private String Layout;

}
