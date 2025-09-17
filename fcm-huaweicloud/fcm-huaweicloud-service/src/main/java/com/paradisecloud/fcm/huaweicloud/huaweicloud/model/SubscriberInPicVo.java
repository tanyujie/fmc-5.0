package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2024/3/18 16:17
 */
@Data
@NoArgsConstructor
public class SubscriberInPicVo {
    private Integer share;
    private Integer index;
    private List<String> id = new ArrayList<>();
}
