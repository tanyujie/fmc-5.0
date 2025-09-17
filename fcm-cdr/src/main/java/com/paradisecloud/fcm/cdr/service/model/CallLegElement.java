package com.paradisecloud.fcm.cdr.service.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description CallLeg节点
 * @Author johnson liu
 * @Date 2021/6/30 21:27
 **/
@Getter
@Setter
@ToString
public class CallLegElement
{
    // @JSONField(name = "@id")
    private String id;
    
    private String displayName;
    
    private String localAddress;
    
    private String remoteAddress;
    
    private String remoteParty;
    
    private String cdrTag;
    
    private Boolean guestConnection;
    
    private Boolean recording;
    
    private Boolean streaming;
    
    private String type;
    
    private String subType;
    
    private String lyncSubType;
    
    private String direction;
    
    private String call;
    
    private String ownerId;
    
    private String sipCallId;
    
    private String groupId;
    
    private String replacesSipCallId;
    
    private Integer canMove;
    
    private String movedCallLeg;
    
    private String movedCallLegCallBridge;
    
    // callLegEnd字段
    
    private String reason;
    
    private Boolean remoteTeardown;
    
    private Boolean encryptedMedia;
    
    private Boolean unencryptedMedia;
    
    private Integer durationSeconds;
    
    private Integer activatedDuration;
    
    private BigDecimal presentationViewer;
    
    private BigDecimal presentationContributor;
    
    private BigDecimal multistreamVideo;
    
    private Integer maxScreens;
    
    private MediaUsagePercentagesElement mediaUsagePercentages;
    
    private List<AlarmElement> alarm;
    
    private MediaInfoElement txVideo;
    
    private MediaInfoElement rxVideo;
    
    private MediaInfoElement txAudio;
    
    private MediaInfoElement rxAudio;
    
    // callLegUpdate字段
    private String state;
    
    private Integer deactivated;
}
