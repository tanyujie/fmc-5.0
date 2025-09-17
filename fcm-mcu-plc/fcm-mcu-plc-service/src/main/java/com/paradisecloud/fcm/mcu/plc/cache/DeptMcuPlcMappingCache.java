/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalCache.java
 * Package     : com.paradisecloud.fcm.terminal
 * @author lilinhai 
 * @since 2021-01-22 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.mcu.plc.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.McuPlcType;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcDept;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import org.springframework.util.Assert;

/**  
 * <pre>FME组-部门映射缓存</pre>
 * key为部门ID
 * @author lilinhai
 * @since 2021-01-22 18:06
 * @version V1.0  
 */
public class DeptMcuPlcMappingCache extends JavaCache<Long, BusiMcuPlcDept>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36
     */
    private static final long serialVersionUID = 1L;
    private static final DeptMcuPlcMappingCache INSTANCE = new DeptMcuPlcMappingCache();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private DeptMcuPlcMappingCache()
    {
    }
    
    public static DeptMcuPlcMappingCache getInstance()
    {
        return INSTANCE;
    }
    
    public BusiMcuPlcDept getBindMcu(Long key)
    {
        BusiMcuPlcDept busiMcuPlcDept = super.get(key);
        if (busiMcuPlcDept == null)
        {
            SysDept sysDept = SysDeptCache.getInstance().get(key);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return getBindMcu(sysDept.getParentId());
            }
            else
            {
                return null;
            }
        }
        else
        {
            return busiMcuPlcDept;
        }
    }

    /**
     * 判断多个部门使用绑定同一个Mcu
     * @author lilinhai
     * @since 2021-03-24 09:37 
     * @param deptIds
     * @return boolean
     */
    public boolean isBindSameMcu(long... deptIds)
    {
        Assert.state(deptIds.length > 1, "必须要传入两个及以上的部门ID才能调用本方法！");
        BusiMcuPlcDept last = getBindMcu(deptIds[0]);
        for (int i = 1; i < deptIds.length; i++)
        {
            BusiMcuPlcDept busiMcuPlcDept = getBindMcu(deptIds[i]);
            if (!(McuPlcType.convert(busiMcuPlcDept.getMcuType()) == McuPlcType.convert(last.getMcuType()) && busiMcuPlcDept.getMcuId().longValue() == last.getMcuId().longValue()))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否存在绑定MCU集群的部门
     * @author lilinhai
     * @since 2021-03-24 09:47
     * @param deptIds
     * @return boolean
     */
    public boolean isExsitMcuClusterBind(long... deptIds)
    {
        for (int i = 0; i < deptIds.length; i++)
        {
            BusiMcuPlcDept busiMcuPlcDept = getBindMcu(deptIds[i]);
            if (McuPlcType.convert(busiMcuPlcDept.getMcuType()) == McuPlcType.CLUSTER)
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
     * @param mcuXdId
     * @return int
     */
    public int getBindDeptCount(McuPlcType mcuXdType, long mcuXdId)
    {
        int c = 0;
        for (BusiMcuPlcDept busiMcuPlcDept : values())
        {
            if (McuPlcType.convert(busiMcuPlcDept.getMcuType()) == mcuXdType && busiMcuPlcDept.getMcuId().longValue() == mcuXdId)
            {
                c++;
            }
        }
        return c;
    }
}
