package com.paradisecloud.fcm.fme.model.cms;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Dial Transform实体类
 *
 * @Author yl
 * @Date 2020/3/6 16:47
 */
@Getter
@Setter
@ToString
public class DialTransform
{
    
    /**
     * 唯一id
     */
    @JSONField(name = "@id")
    private String id;
    
    /**
     * 转换的预处理类型（默认为raw） raw:产生一个组件 strip：删除点、破折号、空格并产生一个组件 phone:一个国际号码产生两个组件
     */
    private String type;
    
    /**
     * 匹配此规则的正则表达式
     */
    private String match;
    
    /**
     * 要应用的替换转换，这允许引用预处理的组件，以及一个或多个正则表达式替换
     */
    private String transform;
    
    /**
     * 转换规则具有的优先级
     */
    private Integer priority;
    
    /**
     * 匹配规则的操作 accept：接受 acceptPhone:号码接受 deny：拒绝
     */
    private String action;
    
}
