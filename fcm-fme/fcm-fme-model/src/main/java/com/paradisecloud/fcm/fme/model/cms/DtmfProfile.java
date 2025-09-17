package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTMF 配置
 *
 * @author zt1994 2019/8/28 11:35
 */
@Getter
@Setter
@ToString
public class DtmfProfile
{
    
    private String id;
    
    /**
     * 开麦
     */
    private String muteSelfAudio;
    
    /**
     * 禁麦
     */
    private String unmuteSelfAudio;
    
    /**
     * 切换音频
     */
    private String toggleMuteSelfAudio;
    
    /**
     * 锁定 call
     */
    private String lockCall;
    
    /**
     * 解锁 call
     */
    private String unlockCall;
    
    /**
     * 切换到下一个布局
     */
    private String nextLayout;
    
    /**
     * 切换到上一个布局
     */
    private String previousLayout;
    
    /**
     * 所有与会者开麦（除自己）
     */
    private String muteAllExceptSelfAudio;
    
    /**
     * 所有与会者禁麦（除自己）
     */
    private String unmuteAllExceptSelfAudio;
    
    /**
     * 开启录制
     */
    private String startRecording;
    
    /**
     * 关闭录制
     */
    private String stopRecording;
    
    /**
     * 开启直播
     */
    private String startStreaming;
    
    /**
     * 关闭直播
     */
    private String stopStreaming;
    
    /**
     * 允许所有参与者自己静音和不静音。将调用对象中的allowAllMuteSelf设置为true
     */
    private String allowAllMuteSelf;
    
    /**
     * 取消允许所有参与者自己静音和不静音的权限。将调用对象中的allowAllMuteSelf设置为false
     */
    private String cancelAllowAllMuteSelf;
    
    /**
     * 允许所有与会者出席
     */
    private String allowAllPresentationContribution;
    
    /**
     * 取消所有参加者出席的许可
     */
    private String cancelAllowAllPresentationContribution;
    
    /**
     * 新加入与会者开麦 将joinAudioMuteOverride调用对象设置为true
     */
    private String muteAllNewAudio;
    
    /**
     * 新加入与会者禁麦 将joinAudioMuteOverride调用对象设置为false
     */
    private String unmuteAllNewAudio;
    
    /**
     * 默认 新加入与会者开麦
     */
    private String defaultMuteAllNewAudio;
    
    /**
     * 新加入与会者开麦（除自己）
     */
    private String muteAllNewAndAllExceptSelfAudio;
    
    /**
     * 新加入与会者禁麦（除自己）
     */
    private String unmuteAllNewAndAllExceptSelfAudio;
    
    /**
     * 结束 call
     */
    private String endCall;
    
    /**
     * 查询所有与会者的数量
     */
    private String getTotalParticipantCount;
    
}
