package com.paradisecloud.smc3.model.response;

import com.paradisecloud.smc3.model.SmcConference;
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
