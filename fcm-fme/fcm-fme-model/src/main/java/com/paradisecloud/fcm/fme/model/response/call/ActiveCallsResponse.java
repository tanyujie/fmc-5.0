package com.paradisecloud.fcm.fme.model.response.call;

import java.util.ArrayList;

import com.paradisecloud.fcm.fme.model.cms.Call;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 calls 响应
 *
 * @author zt1994 2019/8/23 15:24
 */
@Getter
@Setter
@ToString
public class ActiveCallsResponse
{
    
    /**
     * 总数
     */
    private Integer total;
    
    /**
     * 多个 calls
     */
    private ArrayList<Call> call;
    
}
