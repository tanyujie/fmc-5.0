/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OneSplitScreen.java
 * Package     : com.paradisecloud.fcm.fme.service.model.layout.splitscreen
 * @author lilinhai 
 * @since 2021-02-09 14:38
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.layout;

/**
 * <pre>自动分屏</pre>
 * @author lilinhai
 * @since 2021-02-09 14:38
 * @version V1.0  
 */
public class AutomaticSplitScreen extends SplitScreen
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 13:05 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 布局
     */
    public static final String LAYOUT = "automatic";
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 14:38 
     * @param nos 
     */
    public AutomaticSplitScreen()
    {
        super(-1, -1);
        this.layout = LAYOUT;
    }
}
