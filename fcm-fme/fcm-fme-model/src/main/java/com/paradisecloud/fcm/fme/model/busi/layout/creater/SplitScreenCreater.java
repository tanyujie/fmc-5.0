/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SplitScreenCreater.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.layout.creater
 * @author lilinhai 
 * @since 2021-04-09 10:52
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.layout.creater;

import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;

/**  
 * <pre>抽象的分频创建器</pre>
 * @author lilinhai
 * @since 2021-04-09 10:52
 * @version V1.0  
 */
public interface SplitScreenCreater<T extends SplitScreen>
{
    
    /**
     * 创建多分频实例的接口
     * @author lilinhai
     * @since 2021-04-09 10:35 
     * @return SplitScreen
     */
    T create(int maxImportance);
    
    /**
     * 获取布局
     * @author sinhy
     * @since 2021-08-25 18:24 
     * @return String
     */
    String getLayout();
}
