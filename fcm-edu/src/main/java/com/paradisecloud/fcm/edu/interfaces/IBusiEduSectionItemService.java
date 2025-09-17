package com.paradisecloud.fcm.edu.interfaces;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiEduSectionItem;

/**
 * 课程节次条目Service接口
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
public interface IBusiEduSectionItemService 
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
     * 批量删除课程节次条目
     * 
     * @param ids 需要删除的课程节次条目ID
     * @return 结果
     */
    public int deleteBusiEduSectionItemByIds(Long[] ids);

    /**
     * 删除课程节次条目信息
     * 
     * @param id 课程节次条目ID
     * @return 结果
     */
    public int deleteBusiEduSectionItemById(Long id);
}
