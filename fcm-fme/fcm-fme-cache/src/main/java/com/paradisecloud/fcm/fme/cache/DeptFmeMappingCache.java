/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalCache.java
 * Package     : com.paradisecloud.fcm.terminal
 * @author lilinhai 
 * @since 2021-01-22 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache;

import org.springframework.util.Assert;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.FmeType;
import com.paradisecloud.fcm.dao.model.BusiFmeDept;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

/**  
 * <pre>FME组-部门映射缓存</pre>
 * key为部门ID
 * @author lilinhai
 * @since 2021-01-22 18:06
 * @version V1.0  
 */
public class DeptFmeMappingCache extends JavaCache<Long, BusiFmeDept>
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36 
     */
    private static final long serialVersionUID = 1L;
    private static final DeptFmeMappingCache INSTANCE = new DeptFmeMappingCache();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-01-22 18:07  
     */
    private DeptFmeMappingCache()
    {
    }
    
    public static DeptFmeMappingCache getInstance()
    {
        return INSTANCE;
    }
    
    public BusiFmeDept getBindFme(Long key)
    {
        BusiFmeDept busiFmeDept = super.get(key);
        if (busiFmeDept == null)
        {
            SysDept sysDept = SysDeptCache.getInstance().get(key);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return getBindFme(sysDept.getParentId());
            }
            else
            {
                return null;
            }
        }
        else
        {
            return busiFmeDept;
        }
    }

    /**
     * 判断多个部门使用绑定同一个FME
     * @author lilinhai
     * @since 2021-03-24 09:37 
     * @param deptIds
     * @return boolean
     */
    public boolean isBindSameFme(long... deptIds)
    {
        Assert.state(deptIds.length > 1, "必须要传入两个及以上的部门ID才能调用本方法！");
        BusiFmeDept last = getBindFme(deptIds[0]);
        for (int i = 1; i < deptIds.length; i++)
        {
            BusiFmeDept busiFmeDept = getBindFme(deptIds[i]);
            if (!(FmeType.convert(busiFmeDept.getFmeType()) == FmeType.convert(last.getFmeType()) && busiFmeDept.getFmeId().longValue() == last.getFmeId().longValue()))
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 是否存在绑定FME集群的部门
     * @author lilinhai
     * @since 2021-03-24 09:47 
     * @param deptIds
     * @return boolean
     */
    public boolean isExsitFmeClusterBind(long... deptIds)
    {
        for (int i = 0; i < deptIds.length; i++)
        {
            BusiFmeDept busiFmeDept = getBindFme(deptIds[i]);
            if (FmeType.convert(busiFmeDept.getFmeType()) == FmeType.CLUSTER)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 返回该FME绑定的租户数
     * @author lilinhai
     * @since 2021-03-23 14:22 
     * @param fmeId
     * @return int
     */
    public int getBindDeptCount(FmeType fmeType, long fmeId)
    {
        int c = 0;
        for (BusiFmeDept busiFmeDept : values())
        {
            if (FmeType.convert(busiFmeDept.getFmeType()) == fmeType && busiFmeDept.getFmeId().longValue() == fmeId)
            {
                c++;
            }
        }
        return c;
    }
}
