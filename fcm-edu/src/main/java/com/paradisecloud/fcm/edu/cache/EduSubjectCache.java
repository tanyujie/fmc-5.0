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
import com.paradisecloud.fcm.dao.model.BusiEduSubject;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

/**  
 * <pre>学科缓存</pre>
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version V1.0  
 */
public class EduSubjectCache extends JavaCache<Long, BusiEduSubject>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06 
     */
    private static final long serialVersionUID = 1L;
    private static final EduSubjectCache INSTANCE = new EduSubjectCache();
    private Map<Long, Map<Long, BusiEduSubject>> deptEduObjsMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-01-22 18:07  
     */
    private EduSubjectCache()
    {
    }
    
    public synchronized BusiEduSubject add(BusiEduSubject value)
    {
        Map<Long, BusiEduSubject> deptEduObjMap = deptEduObjsMap.get(value.getDeptId());
        if (deptEduObjMap == null)
        {
            deptEduObjMap = new ConcurrentHashMap<>();
            deptEduObjsMap.put(value.getDeptId(), deptEduObjMap);
        }
        
        deptEduObjMap.put(value.getId(), value);
        return super.put(value.getId(), value);
    }
    
    /**
     * 根据ID获取批量信息
     * @author sinhy
     * @since 2021-10-19 18:22 
     * @param deptId
     * @return Map<Long,BusiEduClass>
     */
    public Map<Long, BusiEduSubject> getEduObjsByDeptId(Long deptId)
    {
        Map<Long, BusiEduSubject> deptEduObjMap = deptEduObjsMap.get(deptId);
        if (ObjectUtils.isEmpty(deptEduObjMap))
        {
            SysDept sysDept = SysDeptCache.getInstance().get(deptId);
            if (sysDept.getParentId() != null && sysDept.getParentId().longValue() > 0)
            {
                return getEduObjsByDeptId(sysDept.getParentId());
            }
            else
            {
                return null;
            }
        }
        else
        {
            return deptEduObjMap;
        }
    }
    
    public BusiEduSubject remove(Object id)
    {
        BusiEduSubject v = super.remove(id);
        if (v != null)
        {
            Map<Long, BusiEduSubject> deptEduClassMap = deptEduObjsMap.get(v.getDeptId());
            if (deptEduClassMap != null)
            {
                deptEduClassMap.remove(id);
            }
            return v;
        }
        return null;
    }

    public static EduSubjectCache getInstance()
    {
        return INSTANCE;
    }
}
