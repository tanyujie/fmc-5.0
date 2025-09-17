/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AutomaticSplitScreenCreater.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.layout.creater
 * @author lilinhai 
 * @since 2021-06-09 16:44
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.layout.creater;

import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AutomaticSplitScreen;

/**  
 * <pre>自动分屏</pre>
 * @author lilinhai
 * @since 2021-06-09 16:44
 * @version V1.0  
 */
public class AutomaticSplitScreenCreater implements SplitScreenCreater<AutomaticSplitScreen>
{

    @Override
    public AutomaticSplitScreen create(int maxImportance)
    {
        return new AutomaticSplitScreen();
    }

    @Override
    public String getLayout()
    {
        return AutomaticSplitScreen.LAYOUT;
    }
}
