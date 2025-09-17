/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ImportanceProcessor.java
 * Package     : com.paradisecloud.fcm.fme.service.model.layout
 * @author lilinhai 
 * @since 2021-02-09 14:22
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.model.busi.layout;


import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;

/**
 * <pre>权重处理器</pre>
 * @author lilinhai
 * @since 2021-02-09 14:22
 * @version V1.0  
 */
public interface ImportanceProcessor
{
    
    /**
     * 权重处理逻辑抽象接口
     * @author lilinhai
     * @since 2021-02-09 14:22 
     * @param cellScreen void
     */
    void process(AttendeeForMcuZte attendee, CellScreen cellScreen);
}
