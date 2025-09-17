package com.paradisecloud.fcm.fme.model.response.callleg;

import java.util.ArrayList;

import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;

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
public class ActiveCallLegsResponse
{
    
    /**
     * 总数
     */
    private Integer total;
    
    /**
     * call leg列表
     */
    private ArrayList<CallLeg> callLeg;
}
