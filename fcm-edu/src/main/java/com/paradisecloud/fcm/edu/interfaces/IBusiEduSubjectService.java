package com.paradisecloud.fcm.edu.interfaces;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiEduSubject;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 学科信息Service接口
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
public interface IBusiEduSubjectService 
{
    /**
     * 查询学科信息
     * 
     * @param id 学科信息ID
     * @return 学科信息
     */
    public BusiEduSubject selectBusiEduSubjectById(Long id);

    /**
     * 查询学科信息列表
     * 
     * @param busiEduSubject 学科信息
     * @return 学科信息集合
     */
    public List<BusiEduSubject> selectBusiEduSubjectList(BusiEduSubject busiEduSubject);

    /**
     * 新增学科信息
     * 
     * @param busiEduSubject 学科信息
     * @return 结果
     */
    public int insertBusiEduSubject(BusiEduSubject busiEduSubject);

    /**
     * 修改学科信息
     * 
     * @param busiEduSubject 学科信息
     * @return 结果
     */
    public int updateBusiEduSubject(BusiEduSubject busiEduSubject);

    /**
     * 批量删除学科信息
     * 
     * @param ids 需要删除的学科信息ID
     * @return 结果
     */
    public int deleteBusiEduSubjectByIds(Long[] ids);

    /**
     * 删除学科信息信息
     * 
     * @param id 学科信息ID
     * @return 结果
     */
    public int deleteBusiEduSubjectById(Long id);
    
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
