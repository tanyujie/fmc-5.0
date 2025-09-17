/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SplitScreen.java
 * Package     : com.paradisecloud.fcm.fme.service.model.layout
 * @author lilinhai 
 * @since 2021-02-09 14:12
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.layout;


import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.sinhy.exception.SystemException;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**  
 * <pre>分屏</pre>
 * @author lilinhai
 * @since 2021-02-09 14:12
 * @version V1.0  
 */
public abstract class SplitScreen implements Serializable
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 13:05 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 最大权重
     */
    private static final int MAX_IMPORTANCE = 10000000;
    
    protected String layout;
    
    /**
     * 分屏需要渲染的参会者
     */
    protected List<AttendeeSmc3> attendees = new ArrayList<>();
    
    /**
     * 分屏格子集合
     */
    private List<CellScreen> cellScreens = new ArrayList<>();
    
    protected SplitScreen()
    {
        
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 14:18 
     * @param nos 分屏数
     */
    protected SplitScreen(int nos)
    {
        this(nos, MAX_IMPORTANCE);
    }

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 14:18 
     * @param nos 分屏数
     */
    protected SplitScreen(int nos, int maxImportance)
    {
        initCellScreen(nos, maxImportance);
    }
    
    /**
     * 初始化分屏
     * @author lilinhai
     * @since 2021-02-22 14:08 
     * @param nos
     * @param top void
     */
    public void initCellScreen(int nos, int top)
    {
        if (nos > 0)
        {
            for (int i = 0; i < nos; i++)
            {
                addCellScreen(i+1, top--);
            }
        }
    }
    
    public void addCellScreen(int cellScreenNumber, int importance)
    {
        cellScreens.add(new CellScreen(cellScreenNumber, importance));
    }
    
    public void reInitCellScreenImportance(int importance)
    {
        for (CellScreen cellScreen : cellScreens)
        {
            cellScreen.setImportance(importance--);
        }
    }
    
    /**
     * <p>Get Method   :   cellScreens List<CellScreen></p>
     * @return cellScreens
     */
    public List<CellScreen> getCellScreens()
    {
        return cellScreens;
    }
    
    public int getMinImportance()
    {
        if (!ObjectUtils.isEmpty(cellScreens))
        {
            return cellScreens.get(cellScreens.size() - 1).getImportance();
        }
        return 0;
    }

    /**
     * <p>Get Method   :   layout String</p>
     * @return layout
     */
    public String getLayout()
    {
        return layout;
    }

    /**
     * 根据分屏处理权重
     * @author lilinhai
     * @since 2021-02-09 15:03 
     * @param as
     * @param importanceProcessor void
     */
    public void processImportance(List<AttendeeSmc3> as, ImportanceProcessor importanceProcessor)
    {
        if (ObjectUtils.isEmpty(as))
        {
            throw new SystemException(10074714, "参会人数不能为空");
        }
        
        if (as.size() > getCellScreens().size())
        {
            throw new SystemException(10074716, "轮询的参会人数不能大于分屏数");
        }
        
        for (int i = as.size() - 1; i >=0; i--)
        {
            importanceProcessor.process(as.get(i), getCellScreens().get(i));
        }
    }
    
    @Override
    public String toString()
    {
        return "SplitScreen [layout=" + layout + "]";
    }
}
