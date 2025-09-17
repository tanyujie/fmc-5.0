package com.paradisecloud.fcm.fme.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个与会者修改参数
 *
 * @author zt1994 2019/8/27 16:52
 */
@Getter
@Setter
@ToString
public class ParticipantModifyRequest
{
    
    /**
     * 设置这个参与者在会议中的重要性。最大值为2,147,483,647。 若要删除重要性，请将重要性参数保留为未设置
     */
    private Integer importance;
    
    /**
     * 设置DTMF序列以播放给该参与者
     */
    private String dtmfSequence;
    
    /**
     * 覆盖此参与者的名称
     */
    private String nameLabelOverride;
    
}
