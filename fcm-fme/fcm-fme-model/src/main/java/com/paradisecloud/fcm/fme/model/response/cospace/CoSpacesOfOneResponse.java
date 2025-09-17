package com.paradisecloud.fcm.fme.model.response.cospace;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 coSpace 响应类
 *
 * @author zt1994 2019/8/22 13:45
 */
@Getter
@Setter
@ToString
public class CoSpacesOfOneResponse
{
    
    /**
     * 单个 coSpace 响应
     */
    private ActiveCoSpacesOfOneResponse coSpaces;
}
