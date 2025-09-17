/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ILayoutTemplateService.java
 * Package     : com.paradisecloud.fcm.fme.attendee.interfaces
 * @author sinhy 
 * @since 2021-09-14 14:27
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.interfaces;

import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.LayoutTemplate;

public interface ILayoutTemplateService
{
    
    /**
     * <pre>同步MCU集群端的活跃会议室映射数据</pre>
     * @author lilinhai
     * @since 2020-12-08 17:28 
     * @param fmeBridge void
     */
    void sync(FmeBridge fmeBridge, LayoutTemplateConsumptionProcessor layoutTemplateConsumptionProcessor);
    
    public static interface LayoutTemplateConsumptionProcessor
    {
        void process(FmeBridge fmeBridge, LayoutTemplate layoutTemplate);
    }

}
