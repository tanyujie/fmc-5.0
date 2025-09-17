package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CcAddMrTerminalRequest extends CommonRequest {
    private String conf_id;
    private List<Mt> mts;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public List<Mt> getMts() {
        return mts;
    }

    public void setMts(List<Mt> mts) {
        this.mts = mts;
    }

    public static class Mt {
        /**
         * account : 2322231
         * account_type : 5
         * bitrate : 2048
         * protocol : 0
         * forced_call : 0
         * call_mode : 0
         */

        private String account;
        private int account_type;
        private int bitrate;
        private int protocol;
        private int forced_call;
        private int call_mode;

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

        public int getBitrate() {
            return bitrate;
        }

        public void setBitrate(int bitrate) {
            this.bitrate = bitrate;
        }

        public int getProtocol() {
            return protocol;
        }

        public void setProtocol(int protocol) {
            this.protocol = protocol;
        }

        public int getForced_call() {
            return forced_call;
        }

        public void setForced_call(int forced_call) {
            this.forced_call = forced_call;
        }

        public int getCall_mode() {
            return call_mode;
        }

        public void setCall_mode(int call_mode) {
            this.call_mode = call_mode;
        }
    }

    @Override
    public List<NameValuePair> buildToList() {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("params", JSON.toJSONString(this)));
        return list;
    }

    /**
     * {
     *   "mts": [
     *     {
     *       "account": "172.16.100.66",
     *       "account_type": 7,
     *       "bitrate": 1024,
     *       "protocol": 1,
     *       "forced_call": 0,
     *       "call_mode": 0
     *     }
     *   ]
     * }
     * @return
     */
    public static CcAddMrTerminalRequest buildDefaultRequest() {
        CcAddMrTerminalRequest ccAddMrTerminalRequest = new CcAddMrTerminalRequest();
        List<Mt> list = new ArrayList<>();
        Mt mt = new Mt();
        mt.setAccount("");
        mt.setAccount_type(7);
        mt.setBitrate(1024);
        mt.setProtocol(1);
        mt.setForced_call(0);
        mt.setCall_mode(0);
        list.add(mt);
        ccAddMrTerminalRequest.setMts(list);
        return ccAddMrTerminalRequest;
    }
}
