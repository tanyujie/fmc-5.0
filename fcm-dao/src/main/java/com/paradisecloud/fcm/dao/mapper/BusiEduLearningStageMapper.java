package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiEduLearningStage;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 学段信息，小学，初中，高中，大学等Mapper接口
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
public interface BusiEduLearningStageMapper 
{
    /**
     * 查询学段信息，小学，初中，高中，大学等
     * 
     * @param id 学段信息，小学，初中，高中，大学等ID
     * @return 学段信息，小学，初中，高中，大学等
     */
    public BusiEduLearningStage selectBusiEduLearningStageById(Long id);

    /**
     * 查询学段信息，小学，初中，高中，大学等列表
     * 
     * @param busiEduLearningStage 学段信息，小学，初中，高中，大学等
     * @return 学段信息，小学，初中，高中，大学等集合
     */
    public List<BusiEduLearningStage> selectBusiEduLearningStageList(BusiEduLearningStage busiEduLearningStage);

    /**
     * 新增学段信息，小学，初中，高中，大学等
     * 
     * @param busiEduLearningStage 学段信息，小学，初中，高中，大学等
     * @return 结果
     */
    public int insertBusiEduLearningStage(BusiEduLearningStage busiEduLearningStage);

    /**
     * 修改学段信息，小学，初中，高中，大学等
     * 
     * @param busiEduLearningStage 学段信息，小学，初中，高中，大学等
     * @return 结果
     */
    public int updateBusiEduLearningStage(BusiEduLearningStage busiEduLearningStage);

    /**
     * 删除学段信息，小学，初中，高中，大学等
     * 
     * @param id 学段信息，小学，初中，高中，大学等ID
     * @return 结果
     */
    public int deleteBusiEduLearningStageById(Long id);

    /**
     * 批量删除学段信息，小学，初中，高中，大学等
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiEduLearningStageByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
