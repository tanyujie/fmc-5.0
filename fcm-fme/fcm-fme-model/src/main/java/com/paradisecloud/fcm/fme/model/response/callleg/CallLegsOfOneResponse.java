package com.paradisecloud.fcm.fme.model.response.callleg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个call leg
 *
 * @author zt1994 2019/8/26 11:14
 */
@Getter
@Setter
@ToString
public class CallLegsOfOneResponse
{
    
    /**
     * 单个call leg
     */
    private ActiveCallLegsOfOneResponse callLegs;
    
}
