/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SplitScreenCreaterMap.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.layout
 * @author sinhy 
 * @since 2021-09-14 11:43
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.AllEqualSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.AutomaticSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.CustomScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.FourSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.NineSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.OnePlusFiveSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.OnePlusNSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.OnePlusNineSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.OnePlusSevenSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.OneSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.SixteenSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.SplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.StackedSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.TelepresenceSplitScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.TwentyFiveSplitScreenCreater;
import com.sinhy.exception.SystemException;

public class SplitScreenCreaterMap extends ConcurrentHashMap<String, CustomScreenCreater>
{


    public static  Map<String, CustomScreenCreater> LAYOUT_TEMPLATE_ID_MAP = new ConcurrentHashMap<>();
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-09-14 11:43 
     */
    private static final long serialVersionUID = 1L;
    
    private static final Map<String, SplitScreenCreater<? extends SplitScreen>> BUILT_IN_SPLIT_SCREEN_CREATER_MAP = new ConcurrentHashMap<>();
    static
    {
        registerBuiltInSplitScreen(new AutomaticSplitScreenCreater());
        registerBuiltInSplitScreen(new OneSplitScreenCreater());
        registerBuiltInSplitScreen(new SixteenSplitScreenCreater());
        registerBuiltInSplitScreen(new TwentyFiveSplitScreenCreater());
        registerBuiltInSplitScreen(new OnePlusSevenSplitScreenCreater());
        registerBuiltInSplitScreen(new OnePlusNineSplitScreenCreater());
        registerBuiltInSplitScreen(new OnePlusFiveSplitScreenCreater());
        registerBuiltInSplitScreen(new NineSplitScreenCreater());
        registerBuiltInSplitScreen(new FourSplitScreenCreater());
        registerBuiltInSplitScreen(new AllEqualSplitScreenCreater());
        registerBuiltInSplitScreen(new OnePlusNSplitScreenCreater());
        registerBuiltInSplitScreen(new StackedSplitScreenCreater());
        registerBuiltInSplitScreen(new TelepresenceSplitScreenCreater());
    }
    
    public SplitScreen create(String layout, int maxImportance)
    {
        return getSplitScreenCreater(layout).create(maxImportance);
    }
    
    @Override
    public CustomScreenCreater get(Object key)
    {
        CustomScreenCreater customScreenCreater = super.get(key);
        if (customScreenCreater == null)
        {
            throw new SystemException(1004543, "未知的分屏布局：" + key);
        }
        return customScreenCreater;
    }
    
    public SplitScreenCreater<?> getSplitScreenCreater(Object key)
    {
        SplitScreenCreater<?> screenCreater = BUILT_IN_SPLIT_SCREEN_CREATER_MAP.get(key);
        if (screenCreater == null)
        {
            throw new SystemException(1004543, "未知的分屏布局：" + key);
        }
        return screenCreater;
    }

    /**
     * 布局注册
     * @author sinhy
     * @since 2021-08-25 18:29 
     * @param customScreenCreater void
     */
    public void registerSplitScreen(CustomScreenCreater customScreenCreater)
    {
        Assert.notNull(customScreenCreater.getLayout(), "布局名不能为空");
        Assert.notNull(customScreenCreater, "布局创建器不能为空");
        if(customScreenCreater.getLayout()!=null){
            LAYOUT_TEMPLATE_ID_MAP.put(customScreenCreater.getLayout(), customScreenCreater);
        }
        Assert.isTrue(put(customScreenCreater.getLayoutName(), customScreenCreater) == null, "布局名不能重复");
    }
    
    private static void registerBuiltInSplitScreen(SplitScreenCreater<? extends SplitScreen> splitScreenCreater)
    {
        Assert.notNull(splitScreenCreater.getLayout(), "内置布局名不能为空");
        Assert.notNull(splitScreenCreater, "内置布局创建器不能为空");
        Assert.isTrue(BUILT_IN_SPLIT_SCREEN_CREATER_MAP.put(splitScreenCreater.getLayout(), splitScreenCreater) == null, "内置布局名不能重复");
    }

    /**
     * 是否自定义布局
     * @param key
     * @return
     */
    public static boolean isCustomLayoutTemplate(String key) {
        if(key==null){
            return false;
        }
        return BUILT_IN_SPLIT_SCREEN_CREATER_MAP.get(key) == null;
    }
}
