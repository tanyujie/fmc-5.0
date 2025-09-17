package com.paradisecloud.fcm.fme.model.websocket.calllist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call list 更新
 *
 * @author zt1994 2019/8/30 14:12
 */
@Getter
@Setter
@ToString
public class CallListUpdate
{
    
    /**
     * 更新类型
     */
    private String updateType;
    
    /**
     * call id
     */
    private String call;
    
    /**
     * 名称
     */
    private String name;
    
    /*    *//**
             * 当前与会者总数
             *//*
               private Long participants;*/
    
    /**
     * 分布式实例数
     */
    private Integer distributedInstances;
    
    /**
     * 录制状态
     */
    private String recording;
    
    /**
     * 是否由这次会议目前由一个终端从外部记录
     */
    private String endpointRecording;
    
    /**
     * 直播状态
     */
    private String streaming;
    
    /**
     * 锁定状态
     */
    private String lockState;
    
    /**
     * 呼叫类型
     */
    private String callType;
    
    /**
     * 相关器GUID，它在调用的所有分布式实例中都是相同的
     */
    private String callCorrelator;
    
    /**
     * <pre>获取所有字段名</pre>
     * 
     * @author lilinhai
     * @since 2020-12-11 18:56
     * @return String[]
     */
    public static String[] getAllFieldNames()
    {
        Field[] fs = CallListUpdate.class.getDeclaredFields();
        List<String> fl = new ArrayList<String>(fs.length);
        for (Field field : fs)
        {
            if (field.getName().equals("updateType") || field.getName().equals("call"))
            {
                continue;
            }
            fl.add(field.getName());
        }
        return fl.toArray(new String[fl.size()]);
    }
}
