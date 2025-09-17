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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.CustomLayout;
import com.paradisecloud.fcm.fme.model.cms.LayoutTemplate;

/**  
 * <pre>自定义布局创建器</pre>
 * @author lilinhai
 * @since 2021-04-09 10:53
 * @version V1.0  
 */
public class CustomScreenCreater implements SplitScreenCreater<CustomLayout>
{

    private LayoutTemplate layoutTemplate;
    private JSONArray panes;
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-08-25 18:12 
     * @param layoutTemplate 
     */
    public CustomScreenCreater(LayoutTemplate layoutTemplate, JSONArray panes)
    {
        super();
        this.layoutTemplate = layoutTemplate;
        this.panes = panes;
    }

    @Override
    public CustomLayout create(int maxImportance)
    {
        return new CustomLayout(layoutTemplate.getId(), panes.size(), maxImportance);
    }
    
    public JSONObject getLayoutTemplate()
    {
        JSONObject json = new JSONObject();
        json.put("name", layoutTemplate.getId());
        json.put("panes", panes);
        return json;
    }
    
    @Override
    public String getLayout()
    {
        return layoutTemplate.getId();
    }

    public String getLayoutName()
    {
        return layoutTemplate.getName();
    }
}
