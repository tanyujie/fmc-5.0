package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.paradisecloud.fcm.dao.model.BusiEduSectionScheme;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * 课程节次方案，每个季节都可能有不同的节次方案Mapper接口
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
public interface BusiEduSectionSchemeMapper 
{
    /**
     * 查询课程节次方案，每个季节都可能有不同的节次方案
     * 
     * @param id 课程节次方案，每个季节都可能有不同的节次方案ID
     * @return 课程节次方案，每个季节都可能有不同的节次方案
     */
    public BusiEduSectionScheme selectBusiEduSectionSchemeById(Long id);

    /**
     * 查询课程节次方案，每个季节都可能有不同的节次方案列表
     * 
     * @param busiEduSectionScheme 课程节次方案，每个季节都可能有不同的节次方案
     * @return 课程节次方案，每个季节都可能有不同的节次方案集合
     */
    public List<BusiEduSectionScheme> selectBusiEduSectionSchemeList(BusiEduSectionScheme busiEduSectionScheme);

    /**
     * 新增课程节次方案，每个季节都可能有不同的节次方案
     * 
     * @param busiEduSectionScheme 课程节次方案，每个季节都可能有不同的节次方案
     * @return 结果
     */
    public int insertBusiEduSectionScheme(BusiEduSectionScheme busiEduSectionScheme);

    /**
     * 修改课程节次方案，每个季节都可能有不同的节次方案
     * 
     * @param busiEduSectionScheme 课程节次方案，每个季节都可能有不同的节次方案
     * @return 结果
     */
    public int updateBusiEduSectionScheme(BusiEduSectionScheme busiEduSectionScheme);
    
    int updateBusiEduSectionSchemeEnableStatus(@Param("deptId")Long deptId, @Param("enableStatus")Integer enableStatus);

    /**
     * 删除课程节次方案，每个季节都可能有不同的节次方案
     * 
     * @param id 课程节次方案，每个季节都可能有不同的节次方案ID
     * @return 结果
     */
    public int deleteBusiEduSectionSchemeById(Long id);

    /**
     * 批量删除课程节次方案，每个季节都可能有不同的节次方案
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiEduSectionSchemeByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
