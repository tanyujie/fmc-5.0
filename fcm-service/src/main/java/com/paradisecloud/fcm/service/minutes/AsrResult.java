package com.paradisecloud.fcm.service.minutes;

public class AsrResult {
    private Integer code;
    private String msg;
    private AsrData data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AsrData getData() {
        return data;
    }

    public void setData(AsrData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AsrResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    class AsrData {
        private String session_id;

        public String getSession_id() {
            return session_id;
        }

        public void setSession_id(String session_id) {
            this.session_id = session_id;
        }

        @Override
        public String toString() {
            return "AsrData{" +
                    "session_id=" + session_id +
                    '}';
        }
    }
}
