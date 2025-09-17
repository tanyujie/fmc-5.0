package com.paradisecloud.fcm.fme.model.param;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 开始会议参数
 *
 * @author zt1994 2020/3/6 15:24
 */
@Getter
@Setter
@ToString
public class StartConferenceParam
{
    
    /**
     * 预约会议id
     */
    @NotNull(message = "预约会议id不能为空")
    private Integer bookId;
    
    /**
     * 指定会议桥节点 ip:port(172.16.100.191:9443)
     */
    private String bridgeAddress;
}
