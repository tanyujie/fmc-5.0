package com.paradisecloud.fcm.fme.model.response.cospace;

import java.util.ArrayList;

import com.paradisecloud.fcm.fme.model.cms.CoSpace;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 coSpace 响应类
 *
 * @author zt1994 2019/8/22 14:13
 */
@Getter
@Setter
@ToString
public class ActiveCoSpacesResponse
{
    
    /**
     * coSpace 总数
     */
    private Integer total;
    
    /**
     * coSpace 信息
     */
    private ArrayList<CoSpace> coSpace;
}
