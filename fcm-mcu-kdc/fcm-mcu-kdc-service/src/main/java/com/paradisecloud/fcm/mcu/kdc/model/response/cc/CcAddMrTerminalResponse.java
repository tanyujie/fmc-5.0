package com.paradisecloud.fcm.mcu.kdc.model.response.cc;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;

import java.io.Serializable;
import java.util.List;

public class CcAddMrTerminalResponse extends CommonResponse {

    private List<Mt> mts;

    public List<Mt> getMts() {
        return mts;
    }

    public void setMts(List<Mt> mts) {
        this.mts = mts;
    }

    public static class Mt {
        /**
         * account : 172.16.100.35
         * account_type : 7
         * protocol : 1
         * mt_id : 3
         */

        private String account;
        private int account_type;
        private int protocol;
        private int mt_id;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public int getAccount_type() {
            return account_type;
        }

        public void setAccount_type(int account_type) {
            this.account_type = account_type;
        }

        public int getProtocol() {
            return protocol;
        }

        public void setProtocol(int protocol) {
            this.protocol = protocol;
        }

        public int getMt_id() {
            return mt_id;
        }

        public void setMt_id(int mt_id) {
            this.mt_id = mt_id;
        }
    }
}
