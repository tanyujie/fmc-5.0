package com.paradisecloud.fcm.service.minutes;

import java.util.List;;

public class AsrWebsocketResult {
    public String conf_id;
    public String voice_engine;
    //xf
    public String code;
    public String action;
    public String sid;
    public String desc;
    public String data;
    //sherpa
    public Boolean is_final;
    public String text;

    public static class ResultData {
        public Integer seg_id;
        public Cn cn;
    }

    public static class Cn {
        public St st;
    }

    public static class St {
        public String bg;
        public String ed;
        public String rl;
        public List<Rt> rt;
        public String type;
    }

    public static class Rt {
        public List<Ws> ws;
    }

    public static class Ws {
        public Long wb;
        public Long we;
        public List<Cw> cw;
    }

    public static class Cw {
        public String w;
        public String wp;
    }

    public static class TransData {
        public String biz;
        public String dst;
        public boolean isEnd;
        public Integer segId;
        public String src;
        public String type;
        public String bg;
        public String ed;
    }
}
