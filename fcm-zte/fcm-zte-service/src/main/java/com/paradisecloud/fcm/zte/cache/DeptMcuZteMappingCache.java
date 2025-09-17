/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalCache.java
 * Package     : com.paradisecloud.fcm.terminal
 * @author lilinhai 
 * @since 2021-01-22 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.McuZteType;
import com.paradisecloud.fcm.dao.model.BusiMcuZteDept;
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
public class DeptMcuZteMappingCache extends JavaCache<Long, BusiMcuZteDept>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36
     */
    private static final long serialVersionUID = 1L;
    private static final DeptMcuZteMappingCache INSTANCE = new DeptMcuZteMappingCache();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private DeptMcuZteMappingCache()
    {
    }
    
    public static DeptMcuZteMappingCache getInstance()
    {
        return INSTANCE;
    }
    
    public BusiMcuZteDept getBindMcu(Long key)
    {
        BusiMcuZteDept busiMcuZteDept = super.get(key);
        if (busiMcuZteDept == null)
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
            return busiMcuZteDept;
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
        BusiMcuZteDept last = getBindMcu(deptIds[0]);
        for (int i = 1; i < deptIds.length; i++)
        {
            BusiMcuZteDept busiMcuZteDept = getBindMcu(deptIds[i]);
            if (!(McuZteType.convert(busiMcuZteDept.getMcuType()) == McuZteType.convert(last.getMcuType()) && busiMcuZteDept.getMcuId().longValue() == last.getMcuId().longValue()))
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
            BusiMcuZteDept busiMcuZteDept = getBindMcu(deptIds[i]);
            if (McuZteType.convert(busiMcuZteDept.getMcuType()) == McuZteType.CLUSTER)
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
    public int getBindDeptCount(McuZteType mcuXdType, long mcuXdId)
    {
        int c = 0;
        for (BusiMcuZteDept busiMcuZteDept : values())
        {
            if (McuZteType.convert(busiMcuZteDept.getMcuType()) == mcuXdType && busiMcuZteDept.getMcuId().longValue() == mcuXdId)
            {
                c++;
            }
        }
        return c;
    }
}
