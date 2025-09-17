package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiZjNumberSection;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author lilinhai
 * @date 2023-03-27
 */
public interface BusiZjNumberSectionMapper
{
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public BusiZjNumberSection selectBusiZjNumberSectionById(Long id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param busiZjNumberSection 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<BusiZjNumberSection> selectBusiZjNumberSectionList(BusiZjNumberSection busiZjNumberSection);

    /**
     * 新增【请填写功能名称】
     *
     * @param busiZjNumberSection 【请填写功能名称】
     * @return 结果
     */
    public int insertBusiZjNumberSection(BusiZjNumberSection busiZjNumberSection);

    /**
     * 修改【请填写功能名称】
     *
     * @param busiZjNumberSection 【请填写功能名称】
     * @return 结果
     */
    public int updateBusiZjNumberSection(BusiZjNumberSection busiZjNumberSection);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteBusiZjNumberSectionById(Long id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiZjNumberSectionByIds(Long[] ids);

    int countSection(@Param("val") Long val, @Param("id") Long id);

    List<DeptRecordCount> getDeptRecordCounts();
}