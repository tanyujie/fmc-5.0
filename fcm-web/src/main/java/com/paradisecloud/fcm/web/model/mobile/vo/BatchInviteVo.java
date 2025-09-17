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
 * @date 2022/6/24 15:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchInviteVo implements Serializable {
    /**
     * 会议ID
     */
    @NotBlank
    private String conferenceId;
    /**
     * 终端ID
     */
    @NotEmpty
    private List<Long> terminalIds;
}
