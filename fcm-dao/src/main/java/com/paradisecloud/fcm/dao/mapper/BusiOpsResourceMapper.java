package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiOpsResource;

import java.util.List;

/**
 * ops资源Mapper接口
 * 
 * @author lilinhai
 * @date 2024-07-31
 */
public interface BusiOpsResourceMapper 
{
    /**
     * 查询ops资源
     * 
     * @param id ops资源ID
     * @return ops资源
     */
    public BusiOpsResource selectBusiOpsResourceById(Long id);

    /**
     * 查询ops资源列表
     * 
     * @param busiOpsResource ops资源
     * @return ops资源集合
     */
    public List<BusiOpsResource> selectBusiOpsResourceList(BusiOpsResource busiOpsResource);

    /**
     * 新增ops资源
     * 
     * @param busiOpsResource ops资源
     * @return 结果
     */
    public int insertBusiOpsResource(BusiOpsResource busiOpsResource);

    /**
     * 修改ops资源
     * 
     * @param busiOpsResource ops资源
     * @return 结果
     */
    public int updateBusiOpsResource(BusiOpsResource busiOpsResource);

    /**
     * 删除ops资源
     * 
     * @param id ops资源ID
     * @return 结果
     */
    public int deleteBusiOpsResourceById(Long id);

    /**
     * 批量删除ops资源
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiOpsResourceByIds(Long[] ids);
}
