package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiMcuZjResourceTemplate;

import java.util.List;

/**
 * 紫荆MCU资源模板Mapper接口
 * 
 * @author lilinhai
 * @date 2023-03-17
 */
public interface BusiMcuZjResourceTemplateMapper 
{
    /**
     * 查询紫荆MCU资源模板
     * 
     * @param id 紫荆MCU资源模板ID
     * @return 紫荆MCU资源模板
     */
    public BusiMcuZjResourceTemplate selectBusiMcuZjResourceTemplateById(Long id);

    /**
     * 查询紫荆MCU资源模板列表
     * 
     * @param busiMcuZjResourceTemplate 紫荆MCU资源模板
     * @return 紫荆MCU资源模板集合
     */
    public List<BusiMcuZjResourceTemplate> selectBusiMcuZjResourceTemplateList(BusiMcuZjResourceTemplate busiMcuZjResourceTemplate);

    /**
     * 新增紫荆MCU资源模板
     * 
     * @param busiMcuZjResourceTemplate 紫荆MCU资源模板
     * @return 结果
     */
    public int insertBusiMcuZjResourceTemplate(BusiMcuZjResourceTemplate busiMcuZjResourceTemplate);

    /**
     * 修改紫荆MCU资源模板
     * 
     * @param busiMcuZjResourceTemplate 紫荆MCU资源模板
     * @return 结果
     */
    public int updateBusiMcuZjResourceTemplate(BusiMcuZjResourceTemplate busiMcuZjResourceTemplate);

    /**
     * 删除紫荆MCU资源模板
     * 
     * @param id 紫荆MCU资源模板ID
     * @return 结果
     */
    public int deleteBusiMcuZjResourceTemplateById(Long id);

    /**
     * 批量删除紫荆MCU资源模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiMcuZjResourceTemplateByIds(Long[] ids);
}
