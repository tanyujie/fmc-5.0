/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CustomLayout.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.layout.splitscreen
 * @author sinhy 
 * @since 2021-08-25 18:00
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.layout.splitscreen;

import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;

/**  
 * <pre>自定义布局</pre>
 * @author sinhy
 * @since 2021-08-25 18:00
 * @version V1.0  
 */
public class CustomLayout extends SplitScreen
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-08-25 18:02 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 14:38 
     * @param nos 
     */
    public CustomLayout(String layout, int splitCount, int maxImportance)
    {
        super(splitCount, maxImportance);
        this.layout = layout;
    }
}
