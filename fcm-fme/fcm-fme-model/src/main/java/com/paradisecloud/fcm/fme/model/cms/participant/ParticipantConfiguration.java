package com.paradisecloud.fcm.fme.model.cms.participant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 与会者配置
 *
 * @author zt1994 2019/8/27 14:27
 */
@Getter
@Setter
@ToString
public class ParticipantConfiguration
{
    
    /**
     * 权重
     */
    private Integer importance;
    
    /**
     * 与会者复写名称
     */
    private String nameLabelOverride;
}
