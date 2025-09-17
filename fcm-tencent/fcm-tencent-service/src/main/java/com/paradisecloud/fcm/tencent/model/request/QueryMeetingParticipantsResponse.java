package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import com.tencentcloudapi.wemeet.models.meeting.QueryParticipantsResponse;

import java.io.Serializable;
import java.util.List;

/**
 * @author nj
 * @date 2023/8/9 10:23
 */
public class QueryMeetingParticipantsResponse extends BaseResponse implements Serializable {
        @Expose
        @SerializedName("meeting_id")
        private String meetingId;
        @Expose
        @SerializedName("meeting_code")
        private String meetingCode;
        @Expose
        @SerializedName("subject")
        private String subject;
        @Expose
        @SerializedName("schedule_start_time")
        private String scheduleStartTime;
        @Expose
        @SerializedName("schedule_end_time")
        private String scheduleEndTime;
        @Expose
        @SerializedName("participants")
        private List<Participant> participants;
        @Expose
        @SerializedName("has_remaining")
        private Boolean hasRemaining;
        @Expose
        @SerializedName("next_pos")
        private Integer nextPos;
        @Expose
        @SerializedName("total_count")
        private Integer totalCount;

    public QueryMeetingParticipantsResponse() {
        }

        public String getMeetingId() {
            return this.meetingId;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public String getMeetingCode() {
            return this.meetingCode;
        }

        public void setMeetingCode(String meetingCode) {
            this.meetingCode = meetingCode;
        }

        public String getSubject() {
            return this.subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getScheduleStartTime() {
            return this.scheduleStartTime;
        }

        public void setScheduleStartTime(String scheduleStartTime) {
            this.scheduleStartTime = scheduleStartTime;
        }

        public String getScheduleEndTime() {
            return this.scheduleEndTime;
        }

        public void setScheduleEndTime(String scheduleEndTime) {
            this.scheduleEndTime = scheduleEndTime;
        }

        public List<Participant> getParticipants() {
            return this.participants;
        }

        public void setParticipants(List<Participant> participants) {
            this.participants = participants;
        }

        public Boolean getHasRemaining() {
            return this.hasRemaining;
        }

        public void setHasRemaining(Boolean hasRemaining) {
            this.hasRemaining = hasRemaining;
        }

        public Integer getNextPos() {
            return this.nextPos;
        }

        public void setNextPos(Integer nextPos) {
            this.nextPos = nextPos;
        }

        public Integer getTotalCount() {
            return this.totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public static class Participant implements Serializable {
            @Expose
            @SerializedName("userid")
            private String userId;
            @Expose
            @SerializedName("user_name")
            private String userName;
            @Expose
            @SerializedName("phone")
            private String phone;
            @Expose
            @SerializedName("join_time")
            private String joinTime;
            @Expose
            @SerializedName("left_time")
            private String leftTime;
            @Expose
            @SerializedName("instanceid")
            private Integer instanceId;

            @Expose
            @SerializedName("IP")
            private String IP;

            public Participant() {
            }

            public String getUserId() {
                return this.userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getUserName() {
                return this.userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getPhone() {
                return this.phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getJoinTime() {
                return this.joinTime;
            }

            public void setJoinTime(String joinTime) {
                this.joinTime = joinTime;
            }

            public String getLeftTime() {
                return this.leftTime;
            }

            public void setLeftTime(String leftTime) {
                this.leftTime = leftTime;
            }

            public Integer getInstanceId() {
                return this.instanceId;
            }

            public void setInstanceId(Integer instanceId) {
                this.instanceId = instanceId;
            }

            public String getIP() {
                return IP;
            }

            public void setIP(String IP) {
                this.IP = IP;
            }
        }
}
