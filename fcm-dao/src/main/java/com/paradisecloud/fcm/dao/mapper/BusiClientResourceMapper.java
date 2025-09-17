package com.paradisecloud.fcm.dao.mapper;


import com.paradisecloud.fcm.dao.model.BusiClientResource;

import java.util.List;

/**
 * ops资源Mapper接口
 * 
 * @author lilinhai
 * @date 2024-07-31
 */
public interface BusiClientResourceMapper 
{
    /**
     * 查询ops资源
     * 
     * @param id ops资源ID
     * @return ops资源
     */
    public BusiClientResource selectBusiClientResourceById(Long id);

    /**
     * 查询ops资源列表
     * 
     * @param busiClientResource ops资源
     * @return ops资源集合
     */
    public List<BusiClientResource> selectBusiClientResourceList(BusiClientResource busiClientResource);

    /**
     * 新增ops资源
     * 
     * @param busiClientResource ops资源
     * @return 结果
     */
    public int insertBusiClientResource(BusiClientResource busiClientResource);

    /**
     * 修改ops资源
     * 
     * @param busiClientResource ops资源
     * @return 结果
     */
    public int updateBusiClientResource(BusiClientResource busiClientResource);

    /**
     * 删除ops资源
     * 
     * @param id ops资源ID
     * @return 结果
     */
    public int deleteBusiClientResourceById(Long id);

    /**
     * 批量删除ops资源
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiClientResourceByIds(Long[] ids);
}
