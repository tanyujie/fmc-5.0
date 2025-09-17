package com.paradisecloud.fcm.telep.cache.util;


import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.telep.cache.TelepBridge;
import com.paradisecloud.fcm.telep.dao.model.BusiTele;
import com.paradisecloud.fcm.telep.model.busi.ConferencesResponse;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.model.busi.participants.VideoToUse;
import com.paradisecloud.fcm.telep.model.request.EnumerateFilter;

import java.util.ArrayList;
import java.util.List;
//import org.apache.xmlrpc.XmlRpcException;
//import org.apache.xmlrpc.client.XmlRpcClient;
//import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Hashtable;
//import java.util.Map;
//import java.util.Vector;

/**
 * @author nj
 * @date 2022/10/12 14:11
 */
public class Test {


    public static void main(String[] args) throws Exception {

//        XmlRpcClient xmlRpcClient = new XmlRpcClient("http://172.16.100.136/RPC2");
//        Map<String, Object> issue = new Hashtable<>();
//        issue.put("authenticationUser", "admin"); // Bug
//        issue.put("authenticationPassword", "cisco"); // Open
//        issue.put("enumerateFilter", "active"); // Open
//        Vector<Object> params = new Vector<>();
//        params.addElement(issue);
//        Object execute = xmlRpcClient.execute("conference.enumerate", params);
//        String jsonString = JSON.toJSONString(execute);
//        System.out.println(jsonString);
//
//        ConferencesResponse conferencesResponse = JSON.parseObject(jsonString, ConferencesResponse.class);
//        System.out.println(conferencesResponse);


        BusiTele busiTele=new BusiTele();
        busiTele.setAdminUsername("admin");
        busiTele.setAdminPassword("cisco");
        busiTele.setIp("172.16.100.136");
        TelepBridge telepBridge = new TelepBridge(busiTele);
        telepBridge.init();
        ConferencesResponse conferencesResponse = telepBridge.getTeleConferenceApiInvoker().enumerateBean(null);
        System.out.println("conferencesResponse:->");
        System.out.println(JSON.toJSON(conferencesResponse));


       // telepBridge.getTeleConferenceApiInvoker().conferenceStatus("bbbb");
        //telepBridge.getTeleParticipantApiInvoker().enumerate(null,null);
        //  telepBridge.getTeleConferenceApiInvoker().end("7811sdfs229");

        //    telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.DISCONNECTED,null);
//        telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.COMPLETED,null);
        //       telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.CONNECTED,null);
//        telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.CONNECTING,null);
//        telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.DORMANT,null);

        telepBridge.getDeviceApiInvoker().query();

        List<TeleParticipant> objects = new ArrayList<>();
        List<TeleParticipant> enumerate = telepBridge.getTeleParticipantApiInvoker().enumerate(EnumerateFilter.CONNECTED, null, objects);
        TeleParticipant teleParticipant = enumerate.get(1);

        //important
//        teleParticipant.setImportant(false);
//        //麦克风上行
//        teleParticipant.setAudioRxMuted(true);
//        teleParticipant.setAudioTxMuted(true);
//        //视频
//        teleParticipant.setVideoRxMuted(true);
//        teleParticipant.setVideoTxMuted(true);
//
//        VideoToUse videoToUse = new VideoToUse();
//        videoToUse.setParticipantProtocol("h323");
//        videoToUse.setParticipantName("35");
//        videoToUse.setParticipantType("ad_hoc");


       // System.out.println(teleParticipant.getLayoutControlEnabled());
        teleParticipant.setFocusType("participant");

        VideoToUse videoToUse = new VideoToUse();
        TeleParticipant t1 = enumerate.get(0);
        videoToUse.setParticipantType(t1.getParticipantType());
        videoToUse.setParticipantName(t1.getParticipantName());
        videoToUse.setParticipantProtocol(t1.getParticipantProtocol());
        teleParticipant.setFocusParticipant(videoToUse);
        telepBridge.getTeleParticipantApiInvoker().participantModify(teleParticipant);
//        ParticipantDisconnectOrConnectRequest participantDisconnectOrConnectRequest = new ParticipantDisconnectOrConnectRequest();
//
//        BeanUtils.copyProperties(teleParticipanttemp,participantDisconnectOrConnectRequest);
//        telepBridge.getTeleParticipantApiInvoker().participantStatus(participantDisconnectOrConnectRequest);


//        telepBridge.getTeleParticipantApiInvoker().participantConnect(participantDisconnectOrConnectRequest);
      //  telepBridge.getTeleParticipantApiInvoker().participantDisconnect(participantDisconnectOrConnectRequest);
//
//        telepBridge.getTeleParticipantApiInvoker().participantStatus("cx12",null);
//
//        telepBridge.getTeleConferenceApiInvoker().conferenceFloorQuery("cx12");
//
//        telepBridge.getTemplateApiInvoker().enumerate();
//


       //telepBridge.getTeleParticipantApiInvoker().participantDiagnostics("213123","dx80");  錯誤
       // telepBridge.getTeleParticipantApiInvoker().participantConnect("participantConnect","dx80");
//
//        try {
//            // 客户端配置对象，并且设置用户名密码
//            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
//            config.setServerURL(new URL(
//                    "http://172.16.100.136/RPC2"));
//            config.setBasicUserName("admin");
//            config.setBasicPassword("cisco");
//            //创建一个XmlRpcClient对象，并给它绑定一个配置对象
//            XmlRpcClient client = new XmlRpcClient();
//            client.setConfig(config);
//
//            HashMap<Object, Object> o = new HashMap<>();
//            o.put("authenticationUser","admin");
//            o.put("authenticationPassword","cisco");
//            o.put("enumerateFilter","active");
//            // 创建参数集合
//            Vector<String> params= new Vector<>();
//            params.addElement("active");
//
//            String result=(String)client.execute("conference.enumerate",params);
//
//            System.out.println(result);
//
//        } catch (MalformedURLException e) {
//            System.out.println(e.toString());
//        } catch (XmlRpcException e) {
//            System.out.println(e.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


}
