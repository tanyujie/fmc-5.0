package com.paradisecloud.fcm.web.model.mobile.vo;

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
public class MobileAttendeeListVo implements Serializable {
    /**
     * 会议ID
     */
    @NotBlank
    private String conferenceId;
    /**
     * 与会者
     */
    @NotEmpty
    private List<String> attendeeIds;

}
