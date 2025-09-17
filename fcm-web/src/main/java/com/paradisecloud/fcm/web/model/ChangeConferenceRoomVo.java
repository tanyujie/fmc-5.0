package com.paradisecloud.fcm.web.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ChangeConferenceRoomVo {

    private  String conferenceIdOld;

    private  String conferenceIdNew;

    private List<String> attendeeIds;
}
