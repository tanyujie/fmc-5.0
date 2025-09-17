/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : OneSplitScreenCreater.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.layout.creater
 * @author lilinhai 
 * @since 2021-04-09 10:53
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.layout.creater;

import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.SixteenSplitScreen;

/**  
 * <pre>请加上该类的描述</pre>
 * @author lilinhai
 * @since 2021-04-09 10:53
 * @version V1.0  
 */
public class SixteenSplitScreenCreater implements SplitScreenCreater<SixteenSplitScreen>
{

    @Override
    public SixteenSplitScreen create(int maxImportance)
    {
        return new SixteenSplitScreen(maxImportance);
    }
    
    @Override
    public String getLayout()
    {
        return SixteenSplitScreen.LAYOUT;
    }
}
