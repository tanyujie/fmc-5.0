package com.paradisecloud.fcm.fme.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 摄像头控制请求类
 *
 * @author zt1994 2020/6/3 14:12
 */
@Getter
@Setter
@ToString
public class CameraControlRequest
{
    
    /**
     * pan 平移 left/right
     */
    private String pan;
    
    /**
     * tilt 垂直 up/down
     */
    private String tilt;
    
    /**
     * zoom 镜头放大缩小 in/out
     */
    private String zoom;
    
    /**
     * focus 聚焦 in/out
     */
    private String focus;
    
}
