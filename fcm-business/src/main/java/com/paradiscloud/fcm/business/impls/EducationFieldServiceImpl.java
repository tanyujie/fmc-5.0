/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CommonBusinessFieldServiceImpl.java
 * Package     : com.paradiscloud.fcm.business.impls
 * @author sinhy 
 * @since 2021-10-23 16:33
 * @version  V1.0
 */ 
package com.paradiscloud.fcm.business.impls;

import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.paradiscloud.fcm.business.interfaces.IBusinessFieldService;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiEduClass;
import com.paradisecloud.fcm.edu.cache.EduClassCache;
import com.paradisecloud.fcm.edu.cache.EduLearningStageCache;

/**  
 * <pre>教育业务领域服务实现</pre>
 * @author sinhy
 * @since 2021-10-23 16:33
 * @version V1.0  
 */
public class EducationFieldServiceImpl implements IBusinessFieldService
{

    @Override
    public Map<String, Object> parseTerminalBusinessProperties(Map<String, Object> businessProperties)
    {
        if (ObjectUtils.isEmpty(businessProperties))
        {
            return null;
        }
        Assert.isTrue(businessProperties.containsKey("classId"), "班级ID不能为空！");
        Long classId = ((Number)businessProperties.get("classId")).longValue();
        BusiEduClass eduClass = EduClassCache.getInstance().get(classId);
        ModelBean mb = new ModelBean(eduClass);
        if (eduClass.getLearningStage() != null)
        {
            mb.put("learningStage", EduLearningStageCache.getInstance().get(eduClass.getLearningStage()));
        }
        return mb;
    }
    
}
