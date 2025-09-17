package com.paradisecloud.fcm.fme.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 与会者批量修改参数
 * <p>
 * PUT to /calls/<call id>/participants/*?filterIds=<id1>,<id2>&mode=(exclude|selected)
 *
 * @author zt1994 2019/8/27 16:37
 */
@Getter
@Setter
@ToString
public class ParticipantsModifyRequest
{
    
    /**
     * true 其他参与者将不会听到呼叫腿使用此call leg profile的音频
     */
    private Boolean rxAudioMute;
    
    /**
     * true 被静音
     */
    private Boolean txAudioMute;
    
    /**
     * true 使用此call leg profile的call leg的(“相机”)视频将不会被其他参与者看到。
     */
    private Boolean rxVideoMute;
    
    /**
     * true 视频流不发送
     */
    private Boolean txVideoMute;
    
    /**
     * 为所有端点设置布局
     */
    private String layout;
    
    /**
     * 设定所有参与者的重要性。最大值为2,147,483,647。 若要删除重要性，请将重要性参数保留为未设置
     */
    private Integer importance;
    
    /**
     * 可选的逗号分隔列表，最多包含或排除20个参与者id(取决于“mode”参数的值)
     */
    private String filterIds;
    
    /**
     * exclude 排除—筛选器中的参与者id被排除在操作之外 selected 只有filterid中的参与者id包含在操作中
     */
    private String mode;
    
}
