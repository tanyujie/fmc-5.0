package com.paradisecloud.fcm.telep.model.busi;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/10/12 11:11
 */
@NoArgsConstructor
@Data
public class ConferencesResponse {


    private List<TeleConference> conferences;
    private Integer currentRevision;


}
