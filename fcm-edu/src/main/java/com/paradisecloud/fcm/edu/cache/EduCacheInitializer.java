/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EduCacheInitializer.java
 * Package     : com.paradisecloud.fcm.edu.cache
 * @author sinhy 
 * @since 2021-10-23 16:44
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.edu.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.dao.mapper.BusiEduClassMapper;
import com.paradisecloud.fcm.dao.mapper.BusiEduLearningStageMapper;
import com.paradisecloud.fcm.dao.mapper.BusiEduSubjectMapper;
import com.paradisecloud.fcm.dao.model.BusiEduClass;
import com.paradisecloud.fcm.dao.model.BusiEduLearningStage;
import com.paradisecloud.fcm.dao.model.BusiEduSubject;

/**  
 * <pre>教育缓存初始化器</pre>
 * @author sinhy
 * @since 2021-10-23 16:44
 * @version V1.0  
 */
@Order(4)
@Component
public class EduCacheInitializer implements ApplicationRunner
{

    @Autowired
    private BusiEduClassMapper busiEduClassMapper;
    
    @Autowired
    private BusiEduLearningStageMapper busiEduLearningStageMapper;
    
    @Autowired
    private BusiEduSubjectMapper busiEduSubjectMapper;
    
    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        
        List<BusiEduClass> ecs = busiEduClassMapper.selectBusiEduClassList(new BusiEduClass());
        for (BusiEduClass busiEduClass : ecs)
        {
            EduClassCache.getInstance().add(busiEduClass);
        }
        
        List<BusiEduLearningStage> elss = busiEduLearningStageMapper.selectBusiEduLearningStageList(new BusiEduLearningStage());
        for (BusiEduLearningStage busiEduLearningStage : elss)
        {
            EduLearningStageCache.getInstance().add(busiEduLearningStage);
        }
        
        List<BusiEduSubject> ess = busiEduSubjectMapper.selectBusiEduSubjectList(new BusiEduSubject());
        for (BusiEduSubject busiEduSubject : ess)
        {
            EduSubjectCache.getInstance().add(busiEduSubject);
        }
    }
    
}
