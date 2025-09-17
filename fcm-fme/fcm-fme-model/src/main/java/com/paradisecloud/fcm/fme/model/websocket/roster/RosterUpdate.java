package com.paradisecloud.fcm.fme.model.websocket.roster;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * roster 更新
 *
 * @author zt1994 2019/9/2 11:41
 */
@Getter
@Setter
@ToString
public class RosterUpdate
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
    
    /**
     * uri
     */
    private String uri;
    
    /**
     * status 状态
     */
    private String state;
    
    /**
     * direction incoming|outgoing
     */
    private String direction;
    
    /**
     * audioMuted 音频状态
     */
    private Boolean audioMuted;
    
    /**
     * videoMuted 视频状态
     */
    private Boolean videoMuted;
    
    /**
     * importance 权重
     */
    private Integer importance;
    
    /**
     * 布局
     */
    private String layout;
    
    /**
     * activeSpeaker 是否积极发言
     */
    private Boolean activeSpeaker;
    
    /**
     * presenter 是否共享屏幕
     */
    private Boolean presenter;
    
    /**
     * endpointRecording 是否正在录制会议
     */
    private String endpointRecording;
    
    /**
     * Indicates whether this participant can be moved using the movedParticipant API command.(Fromversion2.6)
     */
    private Boolean canMove;
    
    private String participant;
    
    /**
     * <pre>获取所有字段名</pre>
     * 
     * @author lilinhai
     * @since 2020-12-11 18:56
     * @return String[]
     */
    public static String[] getAllFieldNames()
    {
        Field[] fs = RosterUpdate.class.getDeclaredFields();
        List<String> fl = new ArrayList<String>(fs.length);
        for (Field field : fs)
        {
            if (field.getName().equals("updateType")
                    || field.getName().equals("participant")
                    || field.getName().equals("call"))
            {
                continue;
            }
            fl.add(field.getName());
        }
        return fl.toArray(new String[fl.size()]);
    }
}
