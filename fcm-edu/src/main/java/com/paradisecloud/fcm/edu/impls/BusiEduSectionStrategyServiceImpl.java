package com.paradisecloud.fcm.edu.impls;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.dao.mapper.BusiEduSectionStrategyMapper;
import com.paradisecloud.fcm.dao.model.BusiEduSectionStrategy;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduSectionStrategyService;

/**
 * 课程节次策略Service业务层处理
 * 
 * @author lilinhai
 * @date 2021-10-11
 */
@Service
public class BusiEduSectionStrategyServiceImpl implements IBusiEduSectionStrategyService 
{
    @Autowired
    private BusiEduSectionStrategyMapper busiEduSectionStrategyMapper;

    /**
     * 查询课程节次策略
     * 
     * @param id 课程节次策略ID
     * @return 课程节次策略
     */
    @Override
    public BusiEduSectionStrategy selectBusiEduSectionStrategyById(Long id)
    {
        return busiEduSectionStrategyMapper.selectBusiEduSectionStrategyById(id);
    }

    /**
     * 查询课程节次策略列表
     * 
     * @param busiEduSectionStrategy 课程节次策略
     * @return 课程节次策略
     */
    @Override
    public List<BusiEduSectionStrategy> selectBusiEduSectionStrategyList(BusiEduSectionStrategy busiEduSectionStrategy)
    {
        return busiEduSectionStrategyMapper.selectBusiEduSectionStrategyList(busiEduSectionStrategy);
    }

    /**
     * 新增课程节次策略
     * 
     * @param busiEduSectionStrategy 课程节次策略
     * @return 结果
     */
    @Override
    public int insertBusiEduSectionStrategy(BusiEduSectionStrategy busiEduSectionStrategy)
    {
        busiEduSectionStrategy.setCreateTime(new Date());
        return busiEduSectionStrategyMapper.insertBusiEduSectionStrategy(busiEduSectionStrategy);
    }

    /**
     * 修改课程节次策略
     * 
     * @param busiEduSectionStrategy 课程节次策略
     * @return 结果
     */
    @Override
    public int updateBusiEduSectionStrategy(BusiEduSectionStrategy busiEduSectionStrategy)
    {
        busiEduSectionStrategy.setUpdateTime(new Date());
        return busiEduSectionStrategyMapper.updateBusiEduSectionStrategy(busiEduSectionStrategy);
    }

    /**
     * 批量删除课程节次策略
     * 
     * @param ids 需要删除的课程节次策略ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduSectionStrategyByIds(Long[] ids)
    {
        return busiEduSectionStrategyMapper.deleteBusiEduSectionStrategyByIds(ids);
    }

    /**
     * 删除课程节次策略信息
     * 
     * @param id 课程节次策略ID
     * @return 结果
     */
    @Override
    public int deleteBusiEduSectionStrategyById(Long id)
    {
        return busiEduSectionStrategyMapper.deleteBusiEduSectionStrategyById(id);
    }
}
