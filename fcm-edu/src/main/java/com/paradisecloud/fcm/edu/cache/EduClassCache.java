/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EduClassCache.java
 * Package     : com.paradisecloud.fcm.fme.cache.edu
 * @author sinhy 
 * @since 2021-10-19 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.edu.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiEduClass;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

/**  
 * <pre>班级缓存</pre>
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version V1.0  
 */
public class EduClassCache extends JavaCache<Long, BusiEduClass>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06 
     */
    private static final long serialVersionUID = 1L;
    private static final EduClassCache INSTANCE = new EduClassCache();
    private Map<Long, Map<Long, BusiEduClass>> deptEduClassesMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-01-22 18:07  
     */
    private EduClassCache()
    {
    }
    
    public synchronized BusiEduClass add(BusiEduClass value)
    {
        Map<Long, BusiEduClass> deptEduClassMap = deptEduClassesMap.get(value.getDeptId());
        if (deptEduClassMap == null)
        {
            deptEduClassMap = new ConcurrentHashMap<>();
            deptEduClassesMap.put(value.getDeptId(), deptEduClassMap);
        }
        
        deptEduClassMap.put(value.getId(), value);
        return super.put(value.getId(), value);
    }
    
    /**
     * 根据ID获取批量信息
     * @author sinhy
     * @since 2021-10-19 18:22 
     * @param deptId
     * @return Map<Long,BusiEduClass>
     */
    public Map<Long, BusiEduClass> getEduClassesByDeptId(Long deptId)
    {
        Map<Long, BusiEduClass> deptEduClassMap = deptEduClassesMap.get(deptId);
        if (ObjectUtils.isEmpty(deptEduClassMap))
        {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return getEduClassesByDeptId(sysDept.getParentId());
            }
            else
            {
                return null;
            }
        }
        else
        {
            return deptEduClassMap;
        }
    }
    
    public BusiEduClass remove(Object id)
    {
        BusiEduClass busiEduClass = super.remove(id);
        if (busiEduClass != null)
        {
            Map<Long, BusiEduClass> deptEduClassMap = deptEduClassesMap.get(busiEduClass.getDeptId());
            if (deptEduClassMap != null)
            {
                deptEduClassMap.remove(id);
            }
            return busiEduClass;
        }
        return null;
    }

    public static EduClassCache getInstance()
    {
        return INSTANCE;
    }
}
