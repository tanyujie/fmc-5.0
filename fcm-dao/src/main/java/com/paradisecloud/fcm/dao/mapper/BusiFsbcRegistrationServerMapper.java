package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiFsbcRegistrationServer;

/**
 * 终端FSBC注册服务器Mapper接口
 * 
 * @author lilinhai
 * @date 2021-04-21
 */
public interface BusiFsbcRegistrationServerMapper 
{
    /**
     * 查询终端FSBC注册服务器
     * 
     * @param id 终端FSBC注册服务器ID
     * @return 终端FSBC注册服务器
     */
    public BusiFsbcRegistrationServer selectBusiFsbcRegistrationServerById(Long id);

    /**
     * 查询终端FSBC注册服务器列表
     * 
     * @param busiFsbcRegistrationServer 终端FSBC注册服务器
     * @return 终端FSBC注册服务器集合
     */
    public List<BusiFsbcRegistrationServer> selectBusiFsbcRegistrationServerList(BusiFsbcRegistrationServer busiFsbcRegistrationServer);

    /**
     * 新增终端FSBC注册服务器
     * 
     * @param busiFsbcRegistrationServer 终端FSBC注册服务器
     * @return 结果
     */
    public int insertBusiFsbcRegistrationServer(BusiFsbcRegistrationServer busiFsbcRegistrationServer);

    /**
     * 修改终端FSBC注册服务器
     * 
     * @param busiFsbcRegistrationServer 终端FSBC注册服务器
     * @return 结果
     */
    public int updateBusiFsbcRegistrationServer(BusiFsbcRegistrationServer busiFsbcRegistrationServer);

    /**
     * 删除终端FSBC注册服务器
     * 
     * @param id 终端FSBC注册服务器ID
     * @return 结果
     */
    public int deleteBusiFsbcRegistrationServerById(Long id);

    /**
     * 批量删除终端FSBC注册服务器
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiFsbcRegistrationServerByIds(Long[] ids);
}
