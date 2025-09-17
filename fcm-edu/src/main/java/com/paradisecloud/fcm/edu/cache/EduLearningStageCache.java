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
import com.paradisecloud.fcm.dao.model.BusiEduLearningStage;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;

/**  
 * <pre>学段缓存</pre>
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version V1.0  
 */
public class EduLearningStageCache extends JavaCache<Long, BusiEduLearningStage>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06 
     */
    private static final long serialVersionUID = 1L;
    private static final EduLearningStageCache INSTANCE = new EduLearningStageCache();
    private volatile Map<Long, Map<Long, BusiEduLearningStage>> deptEduObjsMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-01-22 18:07  
     */
    private EduLearningStageCache()
    {
    }
    
    public synchronized BusiEduLearningStage add(BusiEduLearningStage value)
    {
        Map<Long, BusiEduLearningStage> deptEduObjMap = deptEduObjsMap.get(value.getDeptId());
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
    public Map<Long, BusiEduLearningStage> getEduObjsByDeptId(Long deptId)
    {
        Map<Long, BusiEduLearningStage> deptEduObjMap = deptEduObjsMap.get(deptId);
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
    
    public BusiEduLearningStage remove(Object id)
    {
        BusiEduLearningStage v = super.remove(id);
        if (v != null)
        {
            Map<Long, BusiEduLearningStage> deptEduClassMap = deptEduObjsMap.get(v.getDeptId());
            if (deptEduClassMap != null)
            {
                deptEduClassMap.remove(id);
            }
            return v;
        }
        return null;
    }

    public static EduLearningStageCache getInstance()
    {
        return INSTANCE;
    }
}
