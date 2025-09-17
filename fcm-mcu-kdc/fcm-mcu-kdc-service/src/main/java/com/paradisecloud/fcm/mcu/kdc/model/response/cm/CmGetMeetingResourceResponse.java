package com.paradisecloud.fcm.mcu.kdc.model.response.cm;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;

public class CmGetMeetingResourceResponse extends CommonResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private Integer TRA264Usable;
        private Integer TRA265Usable;
        private Integer TRAStarted;

        public Integer getTRA264Usable() {
            return TRA264Usable;
        }

        public void setTRA264Usable(Integer TRA264Usable) {
            this.TRA264Usable = TRA264Usable;
        }

        public Integer getTRA265Usable() {
            return TRA265Usable;
        }

        public void setTRA265Usable(Integer TRA265Usable) {
            this.TRA265Usable = TRA265Usable;
        }

        public Integer getTRAStarted() {
            return TRAStarted;
        }

        public void setTRAStarted(Integer TRAStarted) {
            this.TRAStarted = TRAStarted;
        }
    }

}
