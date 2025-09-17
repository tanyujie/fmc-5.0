package com.paradisecloud.fcm.fme.model.response.cospace;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 coSpace 响应
 *
 * @author zt1994 2019/8/22 13:45
 */
@Getter
@Setter
@ToString
public class ActiveCoSpacesOfOneResponse
{
    
    /**
     * coSpace 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * coSpace 信息
     */
    private CoSpace coSpace;
}
