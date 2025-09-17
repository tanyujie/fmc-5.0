package com.paradisecloud.fcm.fme.model.cms.callleg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 远程订阅
 *
 * @author zt1994 2019/8/26 14:10
 */
@Getter
@Setter
@ToString
public class CallLegStatusActiveControlRemote
{
    
    /**
     * 如果存在，则表明远端会议服务器已订阅了本地XCCP功能
     */
    private String capabilities;
    
    /**
     * 如果存在，则表明远端会议服务器已订阅了本地XCCP会议信息(这包括参与者列表和一些会议范围的信息，如记录是否处于活动状态)。
     */
    private String conferenceInfo;
    
    /**
     * 如果存在，则表明远端会议服务器已订阅了本地XCCP布局信息。
     */
    private String layouts;
    
    /**
     * 如果存在，则表明远端会议服务器已订阅了本地XCCP自我信息。
     */
    private String selfInfo;
    
    /**
     * 如果存在，则表明远端会议服务器已订阅了本地XCCP speaker信息。
     */
    private String speakerInfo;
}
