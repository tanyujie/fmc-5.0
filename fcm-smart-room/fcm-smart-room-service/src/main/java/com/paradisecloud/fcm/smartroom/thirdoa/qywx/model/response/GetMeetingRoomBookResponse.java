package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.response;

import java.util.List;

public class GetMeetingRoomBookResponse extends CommonResponse {

    private List<BookingListDTO> booking_list;

    public List<BookingListDTO> getBooking_list() {
        return booking_list;
    }

    public void setBooking_list(List<BookingListDTO> booking_list) {
        this.booking_list = booking_list;
    }

    public static class BookingListDTO {
        private int meetingroom_id;
        private List<ScheduleDTO> schedule;

        public int getMeetingroom_id() {
            return meetingroom_id;
        }

        public void setMeetingroom_id(int meetingroom_id) {
            this.meetingroom_id = meetingroom_id;
        }

        public List<ScheduleDTO> getSchedule() {
            return schedule;
        }

        public void setSchedule(List<ScheduleDTO> schedule) {
            this.schedule = schedule;
        }

        public static class ScheduleDTO {
            private String booking_id;
            private String schedule_id;
            private int start_time;
            private int end_time;
            private String booker;
            private int status;

            public String getBooking_id() {
                return booking_id;
            }

            public void setBooking_id(String booking_id) {
                this.booking_id = booking_id;
            }

            public String getSchedule_id() {
                return schedule_id;
            }

            public void setSchedule_id(String schedule_id) {
                this.schedule_id = schedule_id;
            }

            public int getStart_time() {
                return start_time;
            }

            public void setStart_time(int start_time) {
                this.start_time = start_time;
            }

            public int getEnd_time() {
                return end_time;
            }

            public void setEnd_time(int end_time) {
                this.end_time = end_time;
            }

            public String getBooker() {
                return booker;
            }

            public void setBooker(String booker) {
                this.booker = booker;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }
    }
}
