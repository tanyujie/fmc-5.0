package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户终端Mapper接口
 * 
 * @author lilinhai
 * @date 2022-06-24
 */
public interface BusiUserTerminalMapper 
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
     * 删除用户终端
     * 
     * @param id 用户终端ID
     * @return 结果
     */
    int deleteBusiUserTerminalById(Long id);

    /**
     * 批量删除用户终端
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteBusiUserTerminalByIds(Long[] ids);

    /**
     * 查询用户终端
     *
     * @param userId
     * @return 用户终端
     */
    BusiUserTerminal selectBusiUserTerminalByUserId(@Param("userId") Long userId);

    /**
     * 根据终端ID搜索绑定的终端信息
     *
     * @param terminalId
     * @return
     */
    BusiUserTerminal selectBusiUserTerminalByTerminalId(@Param("terminalId") Long terminalId);
}
