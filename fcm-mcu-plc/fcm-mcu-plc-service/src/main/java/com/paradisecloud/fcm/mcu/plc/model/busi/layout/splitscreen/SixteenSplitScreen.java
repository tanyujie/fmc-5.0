/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OneSplitScreen.java
 * Package     : com.paradisecloud.fcm.fme.service.model.layout.splitscreen
 * @author lilinhai 
 * @since 2021-02-09 14:38
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.model.busi.layout.splitscreen;

import com.paradisecloud.fcm.mcu.plc.model.busi.layout.SplitScreen;

/**
 * <pre>16分屏</pre>
 * @author lilinhai
 * @since 2021-02-09 14:38
 * @version V1.0  
 */
public class SixteenSplitScreen extends SplitScreen
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 13:06 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 布局
     */
    public static final String LAYOUT = "allEqualSixteenths";

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 14:38
     */
    public SixteenSplitScreen(int maxImportance)
    {
        super(16, maxImportance);
        this.layout = LAYOUT;
    }
    
}
