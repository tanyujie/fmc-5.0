package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiSmc2DeptTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门smc模板关联Mapper接口
 * 
 * @author lilinhai
 * @date 2023-04-19
 */
public interface BusiSmc2DeptTemplateMapper 
{
    /**
     * 查询部门smc模板关联
     * 
     * @param id 部门smc模板关联ID
     * @return 部门smc模板关联
     */
    public BusiSmc2DeptTemplate selectBusiSmc2DeptTemplateById(Integer id);

    /**
     * 查询部门smc模板关联列表
     * 
     * @param busiSmc2DeptTemplate 部门smc模板关联
     * @return 部门smc模板关联集合
     */
    public List<BusiSmc2DeptTemplate> selectBusiSmc2DeptTemplateList(BusiSmc2DeptTemplate busiSmc2DeptTemplate);

    /**
     * 新增部门smc模板关联
     * 
     * @param busiSmc2DeptTemplate 部门smc模板关联
     * @return 结果
     */
    public int insertBusiSmc2DeptTemplate(BusiSmc2DeptTemplate busiSmc2DeptTemplate);

    /**
     * 修改部门smc模板关联
     * 
     * @param busiSmc2DeptTemplate 部门smc模板关联
     * @return 结果
     */
    public int updateBusiSmc2DeptTemplate(BusiSmc2DeptTemplate busiSmc2DeptTemplate);

    /**
     * 删除部门smc模板关联
     * 
     * @param id 部门smc模板关联ID
     * @return 结果
     */
    public int deleteBusiSmc2DeptTemplateById(Integer id);

    /**
     * 批量删除部门smc模板关联
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiSmc2DeptTemplateByIds(Integer[] ids);


    List<BusiSmc2DeptTemplate> selectBusiSmc2DeptTemplateListNotInAppointMent(@Param("deptId") Integer deptId);
}
