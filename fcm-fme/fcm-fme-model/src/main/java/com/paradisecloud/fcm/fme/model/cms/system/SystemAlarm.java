package com.paradisecloud.fcm.fme.model.cms.system;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 系统警告信息
 *
 * @author zt1994 2019/8/27 18:07
 */
@Getter
@Setter
@ToString
public class SystemAlarm
{
    
    @JSONField(name = "@id")
    private String id;
    
    /**
     * 此警报状态已激活的时间量
     */
    private Integer activeTimeSeconds;
    
    /**
     * 报警类型 - 详情看文档
     */
    private String type;
    
    /**
     * 对于上面的一些警报类型，提供了关于特定故障原因的附加信息
     */
    private String failureReason;
}
