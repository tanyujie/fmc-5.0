package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiEduSectionItem;

/**
 * 课程节次条目Mapper接口
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
public interface BusiEduSectionItemMapper 
{
    /**
     * 查询课程节次条目
     * 
     * @param id 课程节次条目ID
     * @return 课程节次条目
     */
    public BusiEduSectionItem selectBusiEduSectionItemById(Long id);

    /**
     * 查询课程节次条目列表
     * 
     * @param busiEduSectionItem 课程节次条目
     * @return 课程节次条目集合
     */
    public List<BusiEduSectionItem> selectBusiEduSectionItemList(BusiEduSectionItem busiEduSectionItem);

    /**
     * 新增课程节次条目
     * 
     * @param busiEduSectionItem 课程节次条目
     * @return 结果
     */
    public int insertBusiEduSectionItem(BusiEduSectionItem busiEduSectionItem);

    /**
     * 修改课程节次条目
     * 
     * @param busiEduSectionItem 课程节次条目
     * @return 结果
     */
    public int updateBusiEduSectionItem(BusiEduSectionItem busiEduSectionItem);

    /**
     * 删除课程节次条目
     * 
     * @param id 课程节次条目ID
     * @return 结果
     */
    public int deleteBusiEduSectionItemById(Long id);
    
    /**
     * 根据节次方案ID批量删除
     * @author sinhy
     * @since 2021-10-15 13:09 
     * @param id
     * @return int
     */
    public int deleteBusiEduSectionItemBuildingBySectionSchemeId(Long id);

    /**
     * 批量删除课程节次条目
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiEduSectionItemByIds(Long[] ids);
}
