package com.paradisecloud.smc;

import org.apache.logging.log4j.util.Strings;

/**
 * @author nj
 * @date 2023/3/1 15:08
 */
public class TopicMessage {


    private static StringBuilder heartbeatSb=new StringBuilder();
    private static char lf = 10;
    private static char nl = 0;
    private String name;
    private String id;
    private String token;
    private String destination;


    static {
        heartbeatSb.append("CONNECT").append(lf);
        heartbeatSb.append("accept-version:1.0,1.1,1.2").append(lf);
        heartbeatSb.append("heart-beat:10000,10000").append(lf).append(lf).append(nl);
    }

    public TopicMessage() {
    }




    public TopicMessage(String name, String id, String token, String destination) {
        this.name = name;
        this.id = id;
        this.token = token;
        this.destination = destination;
    }

   public String getSubscribeMessage(){
       StringBuilder sb = new StringBuilder();
       sb.append("SUBSCRIBE").append(lf);
       if(Strings.isNotBlank(token)){
           sb.append("token:"+token).append(lf);
       }
       sb.append("id:"+id).append(lf);
       sb.append("destination:"+destination).append(lf).append(lf).append(nl);
       return sb.toString();
    }


    public static String getUNSubscribeMessage(String id){
        StringBuilder sb = new StringBuilder();
        sb.append("UNSUBSCRIBE").append(lf);
        sb.append("id:"+id).append(lf).append(lf).append(nl);
        return sb.toString();
    }

    /**
     * "CONNECT\n"
     * 			"accept-version:1.0,1.1,1.2\n"
     * 			"heart-beat:10000,10000\n\n";
     */
    public String getConnectMessage(){
        final StringBuilder sb = new StringBuilder();
        sb.append("CONNECT").append(lf);
        sb.append("accept-version:1.0,1.1,1.2").append(lf);
        sb.append("heart-beat:10000,10000").append(lf).append(lf).append(nl);
        return sb.toString();
    }


    public String getConferenceStatus(){
        StringBuilder sb = new StringBuilder();
        sb.append("SUBSCRIBE").append(lf);
        sb.append("id:sub-1").append(lf);
        sb.append("destination:/topic/conferences/status").append(lf).append(lf).append(nl);
        return sb.toString();
    }

    public static String getHeartbeatMessage(){
        return heartbeatSb.toString();
    }



}
