import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.paradisecloud.fcm.fme.cache.model.invoker.ParticipantInvoker;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantInfoResponse;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;

/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : Test.java
 * Package :
 * 
 * @author lilinhai
 * 
 * @since 2021-02-03 14:14
 * 
 * @version V1.0
 */

/**
 * <pre>参会者呼入测试</pre>
 * 
 * @author lilinhai
 * @since 2021-02-03 14:14
 * @version V1.0
 */
public class TestCall
{
    
    public static void main(String[] args)
    {
        HttpRequester hr = HttpObjectCreator.getInstance().createHttpRequester("admin", "P@rad1se", true);
        ParticipantInvoker pi = new ParticipantInvoker(hr, "https://172.16.100.93:9443/api/v1/");
        long s = System.currentTimeMillis();
        String id = pi.createParticipant("58b3ab55-8454-4e06-99e2-9fadfd34839a/", buildRequestParams("172.16.100.147", "哈哈哈哈"));
        
        ParticipantInfoResponse participantInfoResponse = pi.getParticipant(id);
        
        // 验证该与会者ID是否真实存在，若存在，代表呼叫成功，否则，代表与会者已经入会
        if (participantInfoResponse != null && participantInfoResponse.getParticipant() != null)
        {
            System.out.println(participantInfoResponse.getParticipant());
        }
        System.out.println(System.currentTimeMillis() - s);
    }
    
    private static List<NameValuePair> buildRequestParams(String ip, String name)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        
        // 初始权重值1
        nameValuePairs.add(new BasicNameValuePair("importance", String.valueOf(1)));
        
        // 与会者IP
        nameValuePairs.add(new BasicNameValuePair("remoteParty", ip));
        
        // 对应与会者的name
        nameValuePairs.add(new BasicNameValuePair("nameLabelOverride", name));
        
        // 是否打开与会者标签
        nameValuePairs.add(new BasicNameValuePair("participantLabels", "false"));
        return nameValuePairs;
    }
}
