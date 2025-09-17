/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalCache.java
 * Package     : com.paradisecloud.fcm.terminal
 * @author lilinhai 
 * @since 2021-01-22 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.terminal.fsbc.cache;

import org.springframework.util.Assert;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiFsbcServerDept;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

/**  
 * <pre>FSBC组-部门映射缓存</pre>
 * key为部门ID
 * @author lilinhai
 * @since 2021-01-22 18:06
 * @version V1.0  
 */
public class DeptFsbcMappingCache extends JavaCache<Long, BusiFsbcServerDept>
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36 
     */
    private static final long serialVersionUID = 1L;
    private static final DeptFsbcMappingCache INSTANCE = new DeptFsbcMappingCache();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-22 18:07  
     */
    private DeptFsbcMappingCache()
    {
    }
    
    public static DeptFsbcMappingCache getInstance()
    {
        return INSTANCE;
    }
    
    @Override
    public BusiFsbcServerDept get(Object key)
    {
        BusiFsbcServerDept fsbcServerDept = super.get(key);
        if (fsbcServerDept == null)
        {
            SysDept sysDept = SysDeptCache.getInstance().get(key);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return get(sysDept.getParentId());
            }
            else
            {
                return null;
            }
        }
        else
        {
            return fsbcServerDept;
        }
    }

    public boolean isBindSameFsbc(long... deptIds)
    {
        Assert.state(deptIds.length > 1, "必须要传入两个及以上的部门ID才能调用本方法！");
        BusiFsbcServerDept last = get(deptIds[0]);
        for (int i = 1; i < deptIds.length; i++)
        {
            BusiFsbcServerDept fsbcServerDept = get(deptIds[i]);
            if (!(fsbcServerDept.getFsbcServerId().equals(last.getFsbcServerId())))
            {
                return false;
            }
        }
        return true;
    }
    
    public int getBindDeptCount(long fsbcServerId)
    {
        int c = 0;
        for (BusiFsbcServerDept fsbcServerDept : values())
        {
            if (fsbcServerId == fsbcServerDept.getFsbcServerId())
            {
                c++;
            }
        }
        return c;
    }
}
