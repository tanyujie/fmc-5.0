package com.paradisecloud.fcm.fme.model.response.cospace;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 coSpace 响应类
 *
 * @author zt1994 2019/8/22 14:12
 */
@Getter
@Setter
@ToString
public class CoSpacesResponse
{
    
    /**
     * 多个 coSpace 响应
     */
    private ActiveCoSpacesResponse coSpaces;
}
