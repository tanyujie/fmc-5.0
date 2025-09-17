/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DeptTenantCache.java
 * Package     : com.paradisecloud.fcm.fme.cache
 * @author sinhy 
 * @since 2021-08-04 19:24
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiFmeDept;
import com.paradisecloud.fcm.dao.model.BusiTenantSettings;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

/**  
 * <pre>部门租户缓存</pre>
 * @author sinhy
 * @since 2021-08-04 19:24
 * @version V1.0  
 */
public class DeptTenantCache extends JavaCache<Long, BusiTenantSettings>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-08-04 19:26 
     */
    private static final long serialVersionUID = 1L;
 
    private static final DeptTenantCache INSTANCE = new DeptTenantCache();

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-01-22 18:07  
     */
    private DeptTenantCache()
    {
    }

    public BusiTenantSettings get(Object key)
    {
        BusiFmeDept fmeDept = DeptFmeMappingCache.getInstance().get(key);
        BusiTenantSettings v = super.get(key);
        if (v == null)
        {
            SysDept sysDept = SysDeptCache.getInstance().get(key);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return getTenantSettings(sysDept.getParentId(), fmeDept);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return v;
        }
    }
    
    private BusiTenantSettings getTenantSettings(Object key, BusiFmeDept fmeDept)
    {
        BusiTenantSettings v = super.get(key);
        if (fmeDept == DeptFmeMappingCache.getInstance().get(key) && v != null)
        {
            return v;
        }
        else
        {
            SysDept sysDept = SysDeptCache.getInstance().get(key);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return getTenantSettings(sysDept.getParentId(), fmeDept);
            }
            else
            {
                return null;
            }
        }
    }

    public static DeptTenantCache getInstance()
    {
        return INSTANCE;
    }
}
