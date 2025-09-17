package com.paradisecloud.fcm.edu.impls;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.paradisecloud.fcm.dao.mapper.BusiEduLearningStageMapper;
import com.paradisecloud.fcm.dao.model.BusiEduLearningStage;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.edu.cache.EduLearningStageCache;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduLearningStageService;
import com.sinhy.exception.SystemException;

/**
 * 学段信息，小学，初中，高中，大学等Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@Service
public class BusiEduLearningStageServiceImpl implements IBusiEduLearningStageService 
{
    @Autowired
    private BusiEduLearningStageMapper busiEduLearningStageMapper;

    /**
     * 查询学段信息，小学，初中，高中，大学等
     * 
     * @param id 学段信息，小学，初中，高中，大学等ID
     * @return 学段信息，小学，初中，高中，大学等
     */
    @Override
    public BusiEduLearningStage selectBusiEduLearningStageById(Long id)
    {
        return busiEduLearningStageMapper.selectBusiEduLearningStageById(id);
    }

    /**
     * 查询学段信息，小学，初中，高中，大学等列表
     * 
     * @param busiEduLearningStage 学段信息，小学，初中，高中，大学等
     * @return 学段信息，小学，初中，高中，大学等
     */
    @Override
    public List<BusiEduLearningStage> selectBusiEduLearningStageList(BusiEduLearningStage busiEduLearningStage)
    {
        return busiEduLearningStageMapper.selectBusiEduLearningStageList(busiEduLearningStage);
    }

    /**
     * 新增学段信息，小学，初中，高中，大学等
     * 
     * @param busiEduLearningStage 学段信息，小学，初中，高中，大学等
     * @return 结果
     */
    @Override
    public int insertBusiEduLearningStage(BusiEduLearningStage busiEduLearningStage)
    {
        Assert.notNull(busiEduLearningStage.getDeptId(), "部门ID不能为空！");
        busiEduLearningStage.setCreateTime(new Date());
        int c = busiEduLearningStageMapper.insertBusiEduLearningStage(busiEduLearningStage);
        if (c > 0)
        {
            EduLearningStageCache.getInstance().add(busiEduLearningStage);
        }
        return c;
    }

    /**
     * 修改学段信息，小学，初中，高中，大学等
     * 
     * @param busiEduLearningStage 学段信息，小学，初中，高中，大学等
     * @return 结果
     */
    @Override
    public int updateBusiEduLearningStage(BusiEduLearningStage busiEduLearningStage)
    {
        Assert.notNull(busiEduLearningStage.getDeptId(), "部门ID不能为空！");
        busiEduLearningStage.setUpdateTime(new Date());
        int c = busiEduLearningStageMapper.updateBusiEduLearningStage(busiEduLearningStage);
        if (c > 0)
        {
            EduLearningStageCache.getInstance().add(busiEduLearningStage);
        }
        return c;
    }

    /**
     * 批量删除学段信息，小学，初中，高中，大学等
     * 
     * @param ids 需要删除的学段信息，小学，初中，高中，大学等ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduLearningStageByIds(Long[] ids)
    {
        return busiEduLearningStageMapper.deleteBusiEduLearningStageByIds(ids);
    }

    /**
     * 删除学段信息，小学，初中，高中，大学等信息
     * 
     * @param id 学段信息，小学，初中，高中，大学等ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduLearningStageById(Long id)
    {
        try
        {
            int c = busiEduLearningStageMapper.deleteBusiEduLearningStageById(id);
            if (c > 0)
            {
                EduLearningStageCache.getInstance().remove(id);
            }
            return c;
        }
        catch (Exception e)
        {
            throw new SystemException("学段删除失败，存在正在使用它的班级！");
        }
    }

    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-29 12:04 
     * @return
     * @see com.paradisecloud.fcm.edu.interfaces.IBusiEduLearningStageService#getDeptRecordCounts()
     */
    @Override
    public List<DeptRecordCount> getDeptRecordCounts()
    {
        return busiEduLearningStageMapper.getDeptRecordCounts();
    }
    
    
}
