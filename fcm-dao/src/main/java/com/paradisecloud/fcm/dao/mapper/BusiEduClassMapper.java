package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiEduClass;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 班级信息Mapper接口
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
public interface BusiEduClassMapper 
{
    /**
     * 查询班级信息
     * 
     * @param id 班级信息ID
     * @return 班级信息
     */
    public BusiEduClass selectBusiEduClassById(Long id);

    /**
     * 查询班级信息列表
     * 
     * @param busiEduClass 班级信息
     * @return 班级信息集合
     */
    public List<BusiEduClass> selectBusiEduClassList(BusiEduClass busiEduClass);

    /**
     * 新增班级信息
     * 
     * @param busiEduClass 班级信息
     * @return 结果
     */
    public int insertBusiEduClass(BusiEduClass busiEduClass);

    /**
     * 修改班级信息
     * 
     * @param busiEduClass 班级信息
     * @return 结果
     */
    public int updateBusiEduClass(BusiEduClass busiEduClass);

    /**
     * 删除班级信息
     * 
     * @param id 班级信息ID
     * @return 结果
     */
    public int deleteBusiEduClassById(Long id);

    /**
     * 批量删除班级信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiEduClassByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
