package com.paradisecloud.fcm.edu.interfaces;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiEduSectionStrategy;

/**
 * 课程节次策略Service接口
 * 
 * @author lilinhai
 * @date 2021-10-11
 */
public interface IBusiEduSectionStrategyService 
{
    /**
     * 查询课程节次策略
     * 
     * @param id 课程节次策略ID
     * @return 课程节次策略
     */
    public BusiEduSectionStrategy selectBusiEduSectionStrategyById(Long id);

    /**
     * 查询课程节次策略列表
     * 
     * @param busiEduSectionStrategy 课程节次策略
     * @return 课程节次策略集合
     */
    public List<BusiEduSectionStrategy> selectBusiEduSectionStrategyList(BusiEduSectionStrategy busiEduSectionStrategy);

    /**
     * 新增课程节次策略
     * 
     * @param busiEduSectionStrategy 课程节次策略
     * @return 结果
     */
    public int insertBusiEduSectionStrategy(BusiEduSectionStrategy busiEduSectionStrategy);

    /**
     * 修改课程节次策略
     * 
     * @param busiEduSectionStrategy 课程节次策略
     * @return 结果
     */
    public int updateBusiEduSectionStrategy(BusiEduSectionStrategy busiEduSectionStrategy);

    /**
     * 批量删除课程节次策略
     * 
     * @param ids 需要删除的课程节次策略ID
     * @return 结果
     */
    public int deleteBusiEduSectionStrategyByIds(Long[] ids);

    /**
     * 删除课程节次策略信息
     * 
     * @param id 课程节次策略ID
     * @return 结果
     */
    public int deleteBusiEduSectionStrategyById(Long id);
}
