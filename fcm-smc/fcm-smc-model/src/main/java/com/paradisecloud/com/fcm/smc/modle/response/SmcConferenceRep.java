package com.paradisecloud.com.fcm.smc.modle.response;

import com.paradisecloud.com.fcm.smc.modle.SmcConference;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SmcConferenceRep {
    private List<SmcConference> content;
    private int number;
    private int size;
    private int totalElements;
    private int totalPages;
    private Boolean empty;
    private Boolean first;
    private Boolean last;
    private int numberOfElements;


}
