package com.paradisecloud.fcm.web.model.mobile.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nj
 * @date 2022/6/24 15:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileAttendeeVo implements Serializable {
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

}
