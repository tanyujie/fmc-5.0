package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * CDR信息接收器
 *
 * @author zt1994 2019/8/23 11:46
 */
@Getter
@Setter
@ToString
public class CdrReceiver
{
    
    /**
     * 唯一id
     */
    private String id;
    
    /**
     * 接受日志
     */
    private String uri;
}
