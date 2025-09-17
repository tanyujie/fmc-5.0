package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.io.Serializable;

public class CmQuerySysAllResourceStatisticsResponse extends CommonResponse {

    /**
     * get_statics_mr_info : {"schedule_mr_count":0,"convoking_mr_count":2,"total_mr_count":28}
     * get_statics_info : {"all_call_secs":70452426,"max_peak_calls":609,"opened_usr_count":10,"all_mr_secs":2246594,"all_call_cnts":16130,"online_usr_count":0,"total_usr_count":1000,"used_usr_count":10,"all_mr_cnts":474}
     * get_statics_room_info : {"used_room_count":2,"opened_room_count":44,"total_room_count":1000}
     * get_system_resource_statistics : {"used_resource_count":401.1,"system_resource_count":224}
     * cmdid : get_many_show_infos_rsp
     * get_statics_concurrent_info : {"concurrent_usr_count":0,"concurrent_trad_count":0,"total_concurrent_count":300,"trad_concurrent_count":300}
     * get_concurrent_mr_tendency : {"schedule_count":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"concurrent_count":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"time_axis":[1679035200,1679035800,1679036400,1679037000,1679037600,1679038200,1679038800,1679039400,1679040000,1679040600,1679041200,1679041800,1679042400,1679043000,1679043600,1679044200,1679044800,1679045400,1679046000,1679046600,1679047200,1679047800,1679048400,1679049000,1679049600,1679050200,1679050800,1679051400,1679052000,1679052600,1679053200,1679053800,1679054400,1679055000,1679055600,1679056200,1679056800,1679057400,1679058000,1679058600,1679059200,1679059800,1679060400,1679061000,1679061600,1679062200,1679062800,1679063400,1679064000,1679064600,1679065200,1679065800,1679066400,1679067000,1679067600,1679068200,1679068800,1679069400,1679070000,1679070600,1679071200,1679071800,1679072400,1679073000,1679073600,1679074200,1679074800,1679075400,1679076000,1679076600,1679077200,1679077800,1679078400,1679079000,1679079600,1679080200,1679080800,1679081400,1679082000,1679082600,1679083200,1679083800,1679084400,1679085000,1679085600,1679086200,1679086800,1679087400,1679088000,1679088600,1679089200,1679089800,1679090400,1679091000,1679091600,1679092200,1679092800,1679093400,1679094000,1679094600,1679095200,1679095800,1679096400,1679097000,1679097600,1679098200,1679098800,1679099400,1679100000,1679100600,1679101200,1679101800,1679102400,1679103000,1679103600,1679104200,1679104800,1679105400,1679106000,1679106600,1679107200,1679107800,1679108400,1679109000,1679109600,1679110200,1679110800,1679111400,1679112000,1679112600,1679113200,1679113800,1679114400,1679115000,1679115600,1679116200,1679116800,1679117400,1679118000,1679118600,1679119200,1679119800,1679120400,1679121000]}
     */

    private GetStaticsMrInfoResponse get_statics_mr_info;
    private GetStaticsInfoResponse get_statics_info;
    private GetStaticsRoomInfoResponse get_statics_room_info;
    private GetSystemResourceStatisticsResponse get_system_resource_statistics;
    private String cmdid;
    private GetStaticsConcurrentInfoResponse get_statics_concurrent_info;
    private GetConcurrentMrTendencyResponse get_concurrent_mr_tendency;

    public GetStaticsMrInfoResponse getGet_statics_mr_info() {
        return get_statics_mr_info;
    }

    public void setGet_statics_mr_info(GetStaticsMrInfoResponse get_statics_mr_info) {
        this.get_statics_mr_info = get_statics_mr_info;
    }

    public GetStaticsInfoResponse getGet_statics_info() {
        return get_statics_info;
    }

    public void setGet_statics_info(GetStaticsInfoResponse get_statics_info) {
        this.get_statics_info = get_statics_info;
    }

    public GetStaticsRoomInfoResponse getGet_statics_room_info() {
        return get_statics_room_info;
    }

    public void setGet_statics_room_info(GetStaticsRoomInfoResponse get_statics_room_info) {
        this.get_statics_room_info = get_statics_room_info;
    }

    public GetSystemResourceStatisticsResponse getGet_system_resource_statistics() {
        return get_system_resource_statistics;
    }

    public void setGet_system_resource_statistics(GetSystemResourceStatisticsResponse get_system_resource_statistics) {
        this.get_system_resource_statistics = get_system_resource_statistics;
    }

    public String getCmdid() {
        return cmdid;
    }

    public void setCmdid(String cmdid) {
        this.cmdid = cmdid;
    }

    public GetStaticsConcurrentInfoResponse getGet_statics_concurrent_info() {
        return get_statics_concurrent_info;
    }

    public void setGet_statics_concurrent_info(GetStaticsConcurrentInfoResponse get_statics_concurrent_info) {
        this.get_statics_concurrent_info = get_statics_concurrent_info;
    }

    public GetConcurrentMrTendencyResponse getGet_concurrent_mr_tendency() {
        return get_concurrent_mr_tendency;
    }

    public void setGet_concurrent_mr_tendency(GetConcurrentMrTendencyResponse get_concurrent_mr_tendency) {
        this.get_concurrent_mr_tendency = get_concurrent_mr_tendency;
    }

    public static class GetStaticsMrInfoResponse implements Serializable {
        /**
         * schedule_mr_count : 0
         * convoking_mr_count : 2
         * total_mr_count : 28
         */

        private int schedule_mr_count;
        private int convoking_mr_count;
        private int total_mr_count;

        public int getSchedule_mr_count() {
            return schedule_mr_count;
        }

        public void setSchedule_mr_count(int schedule_mr_count) {
            this.schedule_mr_count = schedule_mr_count;
        }

        public int getConvoking_mr_count() {
            return convoking_mr_count;
        }

        public void setConvoking_mr_count(int convoking_mr_count) {
            this.convoking_mr_count = convoking_mr_count;
        }

        public int getTotal_mr_count() {
            return total_mr_count;
        }

        public void setTotal_mr_count(int total_mr_count) {
            this.total_mr_count = total_mr_count;
        }
    }

    public static class GetStaticsInfoResponse implements Serializable {
        /**
         * all_call_secs : 70452426
         * max_peak_calls : 609
         * opened_usr_count : 10
         * all_mr_secs : 2246594
         * all_call_cnts : 16130
         * online_usr_count : 0
         * total_usr_count : 1000
         * used_usr_count : 10
         * all_mr_cnts : 474
         */

        private int all_call_secs;
        private int max_peak_calls;
        private int opened_usr_count;
        private int all_mr_secs;
        private int all_call_cnts;
        private int online_usr_count;
        private int total_usr_count;
        private int used_usr_count;
        private int all_mr_cnts;

        public int getAll_call_secs() {
            return all_call_secs;
        }

        public void setAll_call_secs(int all_call_secs) {
            this.all_call_secs = all_call_secs;
        }

        public int getMax_peak_calls() {
            return max_peak_calls;
        }

        public void setMax_peak_calls(int max_peak_calls) {
            this.max_peak_calls = max_peak_calls;
        }

        public int getOpened_usr_count() {
            return opened_usr_count;
        }

        public void setOpened_usr_count(int opened_usr_count) {
            this.opened_usr_count = opened_usr_count;
        }

        public int getAll_mr_secs() {
            return all_mr_secs;
        }

        public void setAll_mr_secs(int all_mr_secs) {
            this.all_mr_secs = all_mr_secs;
        }

        public int getAll_call_cnts() {
            return all_call_cnts;
        }

        public void setAll_call_cnts(int all_call_cnts) {
            this.all_call_cnts = all_call_cnts;
        }

        public int getOnline_usr_count() {
            return online_usr_count;
        }

        public void setOnline_usr_count(int online_usr_count) {
            this.online_usr_count = online_usr_count;
        }

        public int getTotal_usr_count() {
            return total_usr_count;
        }

        public void setTotal_usr_count(int total_usr_count) {
            this.total_usr_count = total_usr_count;
        }

        public int getUsed_usr_count() {
            return used_usr_count;
        }

        public void setUsed_usr_count(int used_usr_count) {
            this.used_usr_count = used_usr_count;
        }

        public int getAll_mr_cnts() {
            return all_mr_cnts;
        }

        public void setAll_mr_cnts(int all_mr_cnts) {
            this.all_mr_cnts = all_mr_cnts;
        }
    }

    public static class GetStaticsRoomInfoResponse implements Serializable {
        /**
         * used_room_count : 2
         * opened_room_count : 44
         * total_room_count : 1000
         */

        private int used_room_count;
        private int opened_room_count;
        private int total_room_count;

        public int getUsed_room_count() {
            return used_room_count;
        }

        public void setUsed_room_count(int used_room_count) {
            this.used_room_count = used_room_count;
        }

        public int getOpened_room_count() {
            return opened_room_count;
        }

        public void setOpened_room_count(int opened_room_count) {
            this.opened_room_count = opened_room_count;
        }

        public int getTotal_room_count() {
            return total_room_count;
        }

        public void setTotal_room_count(int total_room_count) {
            this.total_room_count = total_room_count;
        }
    }

    public static class GetSystemResourceStatisticsResponse implements Serializable {
        /**
         * used_resource_count : 401.1
         * system_resource_count : 224
         */

        private double used_resource_count;
        private int system_resource_count;

        public double getUsed_resource_count() {
            return used_resource_count;
        }

        public void setUsed_resource_count(double used_resource_count) {
            this.used_resource_count = used_resource_count;
        }

        public int getSystem_resource_count() {
            return system_resource_count;
        }

        public void setSystem_resource_count(int system_resource_count) {
            this.system_resource_count = system_resource_count;
        }
    }

    public static class GetStaticsConcurrentInfoResponse implements Serializable {
        /**
         * concurrent_usr_count : 0
         * concurrent_trad_count : 0
         * total_concurrent_count : 300
         * trad_concurrent_count : 300
         */

        private int concurrent_usr_count;
        private int concurrent_trad_count;
        private int total_concurrent_count;
        private int trad_concurrent_count;

        public int getConcurrent_usr_count() {
            return concurrent_usr_count;
        }

        public void setConcurrent_usr_count(int concurrent_usr_count) {
            this.concurrent_usr_count = concurrent_usr_count;
        }

        public int getConcurrent_trad_count() {
            return concurrent_trad_count;
        }

        public void setConcurrent_trad_count(int concurrent_trad_count) {
            this.concurrent_trad_count = concurrent_trad_count;
        }

        public int getTotal_concurrent_count() {
            return total_concurrent_count;
        }

        public void setTotal_concurrent_count(int total_concurrent_count) {
            this.total_concurrent_count = total_concurrent_count;
        }

        public int getTrad_concurrent_count() {
            return trad_concurrent_count;
        }

        public void setTrad_concurrent_count(int trad_concurrent_count) {
            this.trad_concurrent_count = trad_concurrent_count;
        }
    }

    public static class GetConcurrentMrTendencyResponse implements Serializable {
        private Integer[] schedule_count;
        private Integer[] concurrent_count;
        private Integer[] time_axis;

        public Integer[] getSchedule_count() {
            return schedule_count;
        }

        public void setSchedule_count(Integer[] schedule_count) {
            this.schedule_count = schedule_count;
        }

        public Integer[] getConcurrent_count() {
            return concurrent_count;
        }

        public void setConcurrent_count(Integer[] concurrent_count) {
            this.concurrent_count = concurrent_count;
        }

        public Integer[] getTime_axis() {
            return time_axis;
        }

        public void setTime_axis(Integer[] time_axis) {
            this.time_axis = time_axis;
        }
    }
}
