package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;

public class CcQueryMrSysMsicsResponse extends CommonResponse {

    private List<Data> mosic_group;

    public List<Data> getMosic_group() {
        return mosic_group;
    }

    public void setMosic_group(List<Data> mosic_group) {
        this.mosic_group = mosic_group;
    }

    public static class Data {
        private Integer layout_mode;
        private String mosic_id;
        private List<Integer> layout_coord;
        private Integer mosic_cnts;
        private String layout_desc;
        private String layout_type;
        private String default_layout_for_type;

        public Integer getLayout_mode() {
            return layout_mode;
        }

        public void setLayout_mode(Integer layout_mode) {
            this.layout_mode = layout_mode;
        }

        public String getMosic_id() {
            return mosic_id;
        }

        public void setMosic_id(String mosic_id) {
            this.mosic_id = mosic_id;
        }

        public List<Integer> getLayout_coord() {
            return layout_coord;
        }

        public void setLayout_coord(List<Integer> layout_coord) {
            this.layout_coord = layout_coord;
        }

        public Integer getMosic_cnts() {
            return mosic_cnts;
        }

        public void setMosic_cnts(Integer mosic_cnts) {
            this.mosic_cnts = mosic_cnts;
        }

        public String getLayout_desc() {
            return layout_desc;
        }

        public void setLayout_desc(String layout_desc) {
            this.layout_desc = layout_desc;
        }

        public String getLayout_type() {
            return layout_type;
        }

        public void setLayout_type(String layout_type) {
            this.layout_type = layout_type;
        }

        public String getDefault_layout_for_type() {
            return default_layout_for_type;
        }

        public void setDefault_layout_for_type(String default_layout_for_type) {
            this.default_layout_for_type = default_layout_for_type;
        }
    }
}
