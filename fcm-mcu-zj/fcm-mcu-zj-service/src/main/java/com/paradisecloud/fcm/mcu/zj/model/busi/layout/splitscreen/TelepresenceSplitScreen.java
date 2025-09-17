/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OnePlusNSplitScreen.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.layout.splitscreen
 * @author sinhy 
 * @since 2021-09-15 12:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen;

public class TelepresenceSplitScreen extends MainPaneSplitScreen
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 13:06 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 布局
     */
    public static final String LAYOUT = "telepresence";

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 14:38
     */
    public TelepresenceSplitScreen()
    {
        super(-1, -1);
        this.layout = LAYOUT;
    }
}
