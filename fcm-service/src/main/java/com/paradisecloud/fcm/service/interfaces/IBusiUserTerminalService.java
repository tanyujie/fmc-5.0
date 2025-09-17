package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiUserTerminal;

import java.util.List;

/**
 * 用户终端Service接口
 * 
 * @author lilinhai
 * @date 2022-06-24
 */
public interface IBusiUserTerminalService 
{
    /**
     * 查询用户终端
     * 
     * @param id 用户终端ID
     * @return 用户终端
     */
    BusiUserTerminal selectBusiUserTerminalById(Long id);

    /**
     * 查询用户终端列表
     * 
     * @param busiUserTerminal 用户终端
     * @return 用户终端集合
     */
    List<BusiUserTerminal> selectBusiUserTerminalList(BusiUserTerminal busiUserTerminal);

    /**
     * 新增用户终端
     * 
     * @param busiUserTerminal 用户终端
     * @return 结果
     */
    int insertBusiUserTerminal(BusiUserTerminal busiUserTerminal);

    /**
     * 修改用户终端
     * 
     * @param busiUserTerminal 用户终端
     * @return 结果
     */
    int updateBusiUserTerminal(BusiUserTerminal busiUserTerminal);

    /**
     * 批量删除用户终端
     * 
     * @param ids 需要删除的用户终端ID
     * @return 结果
     */
    int deleteBusiUserTerminalByIds(Long[] ids);

    /**
     * 删除用户终端信息
     * 
     * @param id 用户终端ID
     * @return 结果
     */
    int deleteBusiUserTerminalById(Long id);
}
