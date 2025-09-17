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

import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AllEqualSplitScreen;

public class AllEqualSplitScreenCreater extends MainPaneSplitScreenCreater<AllEqualSplitScreen>
{
    
    public AllEqualSplitScreen create(int maxImportance)
    {
        return new AllEqualSplitScreen();
    }
 
    @Override
    public String getLayout()
    {
        return AllEqualSplitScreen.LAYOUT;
    }
}
