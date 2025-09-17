package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrMosicConfigRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.util.List;

public class CcUpdateMrMosicConfigResponse extends CommonResponse {

    private List<CcUpdateMrMosicConfigRequest.ConfigInfo> config_info;

    public List<CcUpdateMrMosicConfigRequest.ConfigInfo> getConfig_info() {
        return config_info;
    }

    public void setConfig_info(List<CcUpdateMrMosicConfigRequest.ConfigInfo> config_info) {
        this.config_info = config_info;
    }

    public static class ConfigInfo {
        /**
         * mosic_id : auto
         * cur_mosic_id : pin
         * role : guest
         * roles_lst : [["0"],["0"],["0"],["0"],["0"]]
         * layout_mode : 2
         * poll_secs : 10
         * view_has_self : 0
         * miniMosicId : auto
         */

        private String mosic_id;
        private String cur_mosic_id;
        private String role;
        private int layout_mode;
        private int poll_secs;
        private int view_has_self;
        private String miniMosicId;
        private List<List<String>> roles_lst;

        public String getMosic_id() {
            return mosic_id;
        }

        public void setMosic_id(String mosic_id) {
            this.mosic_id = mosic_id;
        }

        public String getCur_mosic_id() {
            return cur_mosic_id;
        }

        public void setCur_mosic_id(String cur_mosic_id) {
            this.cur_mosic_id = cur_mosic_id;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public int getLayout_mode() {
            return layout_mode;
        }

        public void setLayout_mode(int layout_mode) {
            this.layout_mode = layout_mode;
        }

        public int getPoll_secs() {
            return poll_secs;
        }

        public void setPoll_secs(int poll_secs) {
            this.poll_secs = poll_secs;
        }

        public int getView_has_self() {
            return view_has_self;
        }

        public void setView_has_self(int view_has_self) {
            this.view_has_self = view_has_self;
        }

        public String getMiniMosicId() {
            return miniMosicId;
        }

        public void setMiniMosicId(String miniMosicId) {
            this.miniMosicId = miniMosicId;
        }

        public List<List<String>> getRoles_lst() {
            return roles_lst;
        }

        public void setRoles_lst(List<List<String>> roles_lst) {
            this.roles_lst = roles_lst;
        }
    }
}
