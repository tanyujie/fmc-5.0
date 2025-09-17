package com.paradisecloud.fcm.fme.model.cms;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * CoSpaceMessage 实体类
 *
 * @Author yl
 * @Date 2020/3/11 12:13
 */
@Getter
@Setter
@ToString
public class CoSpaceMessage
{
    
    /**
     * coSpace 唯一id
     */
    @JSONField(name = "@id")
    private String id;
    
    /**
     * 要发布到消息板的消息字符串
     */
    private String message;
    
    /**
     * 一个“from”的名字将显示给留言板的观众作为发起人消息
     */
    private String from;
    
    // /**
    // * 如果在URL中提供，则只删除age至少为指定的值的消息(以秒为单位)
    // */
    // private Integer minAge;
    //
    // /**
    // * 如果在URL中提供，则只删除age最大为指定的值的消息(以秒为单位)
    // */
    // private Integer maxAge;
    
    /**
     * 判断是否在删除范围的值
     */
    private Integer age;
    
}
