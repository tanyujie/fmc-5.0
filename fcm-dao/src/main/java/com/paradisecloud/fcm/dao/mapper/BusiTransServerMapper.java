package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiTransServer;

import java.util.List;

/**
 * 转流服务器Mapper接口
 *
 * @author lilinhai
 * @date 2024-03-29
 */
public interface BusiTransServerMapper
{
    /**
     * 查询转流服务器
     *
     * @param id 转流服务器ID
     * @return 转流服务器
     */
    public BusiTransServer selectBusiTransServerById(Long id);

    /**
     * 查询转流服务器列表
     *
     * @param busiTransServer 转流服务器
     * @return 转流服务器集合
     */
    public List<BusiTransServer> selectBusiTransServerList(BusiTransServer busiTransServer);

    /**
     * 新增转流服务器
     *
     * @param busiTransServer 转流服务器
     * @return 结果
     */
    public int insertBusiTransServer(BusiTransServer busiTransServer);

    /**
     * 修改转流服务器
     *
     * @param busiTransServer 转流服务器
     * @return 结果
     */
    public int updateBusiTransServer(BusiTransServer busiTransServer);

    /**
     * 删除转流服务器
     *
     * @param id 转流服务器ID
     * @return 结果
     */
    public int deleteBusiTransServerById(Long id);

    /**
     * 批量删除转流服务器
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTransServerByIds(Long[] ids);
}
