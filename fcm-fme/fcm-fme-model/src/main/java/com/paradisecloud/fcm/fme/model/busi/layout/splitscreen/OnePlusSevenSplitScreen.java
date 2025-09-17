/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OneSplitScreen.java
 * Package     : com.paradisecloud.fcm.fme.service.model.layout.splitscreen
 * @author lilinhai 
 * @since 2021-02-09 14:38
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.layout.splitscreen;

import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;

/**  
 * <pre>1大7小分屏</pre>
 * @author lilinhai
 * @since 2021-02-09 14:38
 * @version V1.0  
 */
public class OnePlusSevenSplitScreen extends SplitScreen
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 13:06 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 布局
     */
    public static final String LAYOUT = "onePlusSeven";
    public static final String LAYOUT_NAME = "一大七小";

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 14:38
     */
    public OnePlusSevenSplitScreen(int maxImportance)
    {
        super(8, maxImportance);
        this.layout = LAYOUT;
        this.layoutName = LAYOUT_NAME;
    }
    
}
