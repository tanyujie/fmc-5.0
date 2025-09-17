package com.paradisecloud.fcm.terminal.fsbc.service.intefaces;

import java.util.List;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiFsbcRegistrationServer;

/**
 * 终端FSBC注册服务器Service接口
 * 
 * @author lilinhai
 * @date 2021-04-21
 */
public interface IBusiFsbcRegistrationServerService 
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
    public List<ModelBean> selectBusiFsbcRegistrationServerList(BusiFsbcRegistrationServer busiFsbcRegistrationServer);

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
     * 批量删除终端FSBC注册服务器
     * 
     * @param ids 需要删除的终端FSBC注册服务器ID
     * @return 结果
     */
    public int deleteBusiFsbcRegistrationServerByIds(Long[] ids);

    /**
     * 删除终端FSBC注册服务器信息
     * 
     * @param id 终端FSBC注册服务器ID
     * @return 结果
     */
    public int deleteBusiFsbcRegistrationServerById(Long id);
}
