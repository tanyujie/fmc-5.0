package com.paradisecloud.fcm.mqtt.interfaces;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.model.BusiRegisterTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminal;

/**
 * 需绑定fs账号的终端Service接口
 * 
 * @author zyz
 * @date 2021-11-04
 */
public interface IBusiRegisterTerminalService 
{
    /**
     * 查询需绑定fs账号的终端
     * 
     * @param id 需绑定fs账号的终端ID
     * @return 需绑定fs账号的终端
     */
    public BusiRegisterTerminal selectBusiRegisterTerminalById(Long id);

    /**
     * 查询需绑定fs账号的终端列表
     * 
     * @param busiRegisterTerminal 需绑定fs账号的终端
     * @return 需绑定fs账号的终端集合
     */
    public List<BusiRegisterTerminal> selectBusiRegisterTerminalList(BusiRegisterTerminal busiRegisterTerminal);

    /**
     * 新增需绑定fs账号的终端
     * 
     * @param busiRegisterTerminal 需绑定fs账号的终端
     * @return 结果
     */
    public int insertBusiRegisterTerminal(BusiRegisterTerminal busiRegisterTerminal);

    /**
     * 修改需绑定fs账号的终端
     * 
     * @param busiRegisterTerminal 需绑定fs账号的终端
     * @return 结果
     */
    public int updateBusiRegisterTerminal(BusiRegisterTerminal busiRegisterTerminal);

    /**
     * 批量删除需绑定fs账号的终端
     * 
     * @param ids 需要删除的需绑定fs账号的终端ID
     * @return 结果
     */
    public int deleteBusiRegisterTerminalByIds(Long[] ids);

    /**
     * 删除需绑定fs账号的终端信息
     * 
     * @param id 需绑定fs账号的终端ID
     * @return 结果
     */
    public int deleteBusiRegisterTerminalById(Long id);
    
//    public void terminalGetSipAccount(String messageId, BusiTerminal busiTerminal);
    
    public void sipRegister(JSONObject jsonS, String clientId, String messageId);
    
    
    public void updateBusiTerminalStatus(BusiTerminal busiTerminal);

	public void insertBusiTerminalData(BusiTerminal busiTerminal);
}
