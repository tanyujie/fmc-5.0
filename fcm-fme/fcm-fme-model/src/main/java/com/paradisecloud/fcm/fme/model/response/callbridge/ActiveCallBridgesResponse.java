package com.paradisecloud.fcm.fme.model.response.callbridge;

import java.util.ArrayList;

import com.paradisecloud.fcm.fme.model.cms.CallBridge;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个call legs响应
 *
 * @author zt1994 2019/8/26 11:14
 */
@Getter
@Setter
@ToString
public class ActiveCallBridgesResponse
{
    
    /**
     * 总数
     */
    private Integer total;
    
    /**
     * call leg列表
     */
    private ArrayList<CallBridge> callBridge;
}
