package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiEduSectionStrategy;

/**
 * 课程节次策略Mapper接口
 * 
 * @author lilinhai
 * @date 2021-10-11
 */
public interface BusiEduSectionStrategyMapper 
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
     * 删除课程节次策略
     * 
     * @param id 课程节次策略ID
     * @return 结果
     */
    public int deleteBusiEduSectionStrategyById(Long id);
    
    /**
     * 根据节次方案ID批量删除
     * @author sinhy
     * @since 2021-10-15 13:07 
     * @param id
     * @return int
     */
    public int deleteBusiEduSectionStrategyBySectionSchemeId(Long id);

    /**
     * 批量删除课程节次策略
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiEduSectionStrategyByIds(Long[] ids);
}
