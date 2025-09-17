package com.paradisecloud.fcm.fme.model.request;

import lombok.Data;

/**
 * @description:
 * @projectName:paradise
 * @see:com.paradisecloud.request
 * @author:chengyang
 * @createTime:2020/10/15 11:48 上午
 * @version:1.0
 */
@Data
public class CameraControlKLRequest
{
    
    private String action;
    
    private String value;
    
    private String near;
    
    public CameraControlKLRequest(String action, Object index, Object near)
    {
        this.action = action;
        this.value = index.toString();
        this.near = near.toString();
    }
    
}
