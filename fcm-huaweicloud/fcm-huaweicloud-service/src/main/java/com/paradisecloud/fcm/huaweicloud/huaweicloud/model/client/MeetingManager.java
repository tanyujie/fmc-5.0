package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client;

import com.huaweicloud.sdk.core.exception.SdkException;
import com.huaweicloud.sdk.meeting.v1.MeetingClient;
import com.huaweicloud.sdk.meeting.v1.model.*;
import com.paradisecloud.common.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author nj
 * @date 2024/2/27 17:17
 */
public class MeetingManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MeetingClient userClient;

    public MeetingManager(MeetingClient userClient) {
        this.userClient = userClient;
    }

    public ConferenceInfo createMeeting(String meetingSubject, String startTime, RestConfConfigDTO confConfigInfo, List<RestAttendeeDTO> attendees) {
        return createMeeting(meetingSubject, startTime, 1440, "", 500, 0, confConfigInfo, attendees);
    }

    public ConferenceInfo createMeeting(String meetingSubject, String startTime, Integer length, Integer concurrentParticipants, List<RestAttendeeDTO> attendees) {
        RestConfConfigDTO restConfConfigDTO = new RestConfConfigDTO();
        restConfConfigDTO.setIsAutoMute(true);
        restConfConfigDTO.setProlongLength(60);
        return createMeeting(meetingSubject, startTime, length, "", concurrentParticipants, 0, restConfConfigDTO, attendees);
    }

    public ConferenceInfo createMeeting(String meetingSubject, String startTime, List<RestAttendeeDTO> attendees) {
        RestConfConfigDTO restConfConfigDTO = new RestConfConfigDTO();
        restConfConfigDTO.setIsAutoMute(true);
        restConfConfigDTO.setProlongLength(60);
        return createMeeting(meetingSubject, startTime, 1440, "", 500, 0, restConfConfigDTO, attendees);
    }

    public ConferenceInfo createMeeting(String meetingSubject, String startTime, Integer length, String vmrUuid, Integer concurrentParticipants, Integer isAutoRecord, RestConfConfigDTO confConfigInfo, List<RestAttendeeDTO> attendees) {
        logger.info("Start createMeeting...");
        RestScheduleConfDTO createMeetingBody = new RestScheduleConfDTO()
                .withSubject(meetingSubject)
                .withStartTime(startTime)
                .withLength(length)
                .withConcurrentParticipants(concurrentParticipants)
                .withConfConfigInfo(confConfigInfo)
                .withIsAutoRecord(isAutoRecord)
                .withEncryptMode(2)
                .withAttendees(attendees)
                .withTimeZoneID("56")
                .withMediaTypes("HDVideo");



        if (isAutoRecord == 1) {
            createMeetingBody.withRecordType(2);
        }

        // 会议创建在云会议室或者个人会议室上
        if (!vmrUuid.equals("")) {
            createMeetingBody.setVmrFlag(1);
            createMeetingBody.setVmrID(vmrUuid);
        }

        CreateMeetingRequest request = new CreateMeetingRequest()
                .withBody(createMeetingBody);

        String conferenceId = "";
        try {
            CreateMeetingResponse response = userClient.createMeeting(request);
            ConferenceInfo meetingInfo = response.getBody().get(0);
            conferenceId = meetingInfo.getConferenceID();
            if (!vmrUuid.equals("")) {
                // VMR上的会议（云会议室或者个人会议室）
                logger.info("Create Meeting on VMR resource. Conference ID is: " + conferenceId + " VMR Conference ID is: " + meetingInfo.getVmrConferenceID());
            } else {
                logger.info("Create Meeting on concurrent resource. Conference ID is: " + conferenceId);
            }
            return meetingInfo;
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }

        return null;
    }


    public ConferenceInfo updateMeeting(String confId,String meetingSubject, String startTime, Integer length, String vmrUuid, Integer concurrentParticipants, Integer isAutoRecord, RestConfConfigDTO confConfigInfo, List<RestAttendeeDTO> attendees) {
        logger.info("Start updateMeeting...");
        RestScheduleConfDTO createMeetingBody = new RestScheduleConfDTO()
                .withSubject(meetingSubject)
                .withStartTime(startTime)
                .withLength(length)
                .withConcurrentParticipants(concurrentParticipants)
                .withConfConfigInfo(confConfigInfo)
                .withIsAutoRecord(isAutoRecord)
                .withEncryptMode(2)
                .withAttendees(attendees)
                .withTimeZoneID("56")
                .withMediaTypes("HDVideo");



        if (isAutoRecord == 1) {
            createMeetingBody.withRecordType(2);
        }

        // 会议创建在云会议室或者个人会议室上
        if (!vmrUuid.equals("")) {
            createMeetingBody.setVmrFlag(1);
            createMeetingBody.setVmrID(vmrUuid);
        }

        UpdateMeetingRequest request = new UpdateMeetingRequest()
                .withConferenceID(confId)
                .withBody(createMeetingBody);

        try {
            UpdateMeetingResponse response = userClient.updateMeeting(request);
            return  response.getBody().get(0);
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }

        return null;
    }


    public void deleteMeeting(String conferenceId) {
        logger.info("Start DeleteMeeting...");
        CancelMeetingRequest request = new CancelMeetingRequest()
                .withConferenceID(conferenceId)
                // 强制结束正在召开的会议
                .withType(1);

        try {
            CancelMeetingResponse response = userClient.cancelMeeting(request);
            logger.info("Delete Meeting: %s success \r\n", conferenceId);
        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
    }

    public String showMeeting(String conferenceId) {
        logger.info("Start showMeeting...");

        ShowMeetingDetailRequest request = new ShowMeetingDetailRequest()
                .withConferenceID(conferenceId);
        String hostPassword = "";
        try {
            ShowMeetingDetailResponse response = userClient.showMeetingDetail(request);
            logger.info("Meeting subject is: " + response.getConferenceData().getSubject());
            logger.info("Meeting ID is: " + response.getConferenceData().getConferenceID());
            String role = response.getConferenceData()
                    .getPasswordEntry()
                    .get(0)
                    .getConferenceRole();
            if (role.equals("chair")) {
                hostPassword = response.getConferenceData()
                        .getPasswordEntry()
                        .get(0)
                        .getPassword();
                logger.info("Meeting host password is: " + hostPassword);
                logger.info("Meeting guest password is: " + response.getConferenceData()
                        .getPasswordEntry()
                        .get(1)
                        .getPassword());
            } else {
                hostPassword = response.getConferenceData()
                        .getPasswordEntry()
                        .get(0)
                        .getPassword();
                logger.info("Meeting host password is: " + response.getConferenceData()
                        .getPasswordEntry()
                        .get(1)
                        .getPassword());
                logger.info("Meeting guest password is: " + hostPassword);
            }

        } catch (SdkException e) {
            logger.info(e.getMessage());
        }

        return hostPassword;
    }

    public ShowMeetingDetailResponse showMeetingDetail(String conferenceId) {
        logger.info("Start showMeeting...");

        ShowMeetingDetailRequest request = new ShowMeetingDetailRequest()
                .withLimit(500)
                .withOffset(0)
                .withConferenceID(conferenceId);

        String hostPassword = "";
        try {
            ShowMeetingDetailResponse response = userClient.showMeetingDetail(request);
            logger.info("Meeting subject is: " + response.getConferenceData().getSubject());
            logger.info("Meeting ID is: " + response.getConferenceData().getConferenceID());
           return response;
        } catch (SdkException e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }

    }


    public void startMeeting(String conferenceId, String hostPassword) {
        logger.info("Start startMeeting...");
        StartRequest startMeetingBody = new StartRequest()
                .withConferenceID(conferenceId)
                .withPassword(hostPassword);
        StartMeetingRequest request = new StartMeetingRequest()
                .withBody(startMeetingBody);

        try {
            StartMeetingResponse response = userClient.startMeeting(request);
            logger.info("Start meeting success, conference id is: " + conferenceId);

        } catch (SdkException e) {
            logger.info(e.getMessage());
        }
    }

    public SearchOnlineMeetingsResponse searchOnlineMeeting(String meetingID) {
        logger.info("Start SearchOnlineMeeting...");
        SearchOnlineMeetingsRequest request = new SearchOnlineMeetingsRequest();
        request.setSearchKey(meetingID);
        try {
            SearchOnlineMeetingsResponse response = userClient.searchOnlineMeetings(request);
            logger.info("SearchOnlineMeeting success, conference id is: " + meetingID);
            return response;
        } catch (SdkException e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }
}
