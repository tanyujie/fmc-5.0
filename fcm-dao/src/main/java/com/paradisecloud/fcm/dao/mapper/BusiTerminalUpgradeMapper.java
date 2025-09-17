package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiTerminalUpgrade;
import org.apache.ibatis.annotations.Param;

/**
 * 终端升级Mapper接口
 * 
 * @author zyz
 * @date 2021-10-11
 */
public interface BusiTerminalUpgradeMapper 
{
    /**
     * 查询终端升级
     * 
     * @param id 终端升级ID
     * @return 终端升级
     */
    BusiTerminalUpgrade selectBusiTerminalUpgradeById(Long id);

    /**
     * 查询终端升级列表
     * 
     * @param busiTerminalUpgrade 终端升级
     * @return 终端升级集合
     */
    List<BusiTerminalUpgrade> selectBusiTerminalUpgradeList(BusiTerminalUpgrade busiTerminalUpgrade);

    /**
     * 新增终端升级
     * 
     * @param busiTerminalUpgrade 终端升级
     * @return 结果
     */
    int insertBusiTerminalUpgrade(BusiTerminalUpgrade busiTerminalUpgrade);

    /**
     * 修改终端升级
     * 
     * @param busiTerminalUpgrade 终端升级
     * @return 结果
     */
    int updateBusiTerminalUpgrade(BusiTerminalUpgrade busiTerminalUpgrade);

    /**
     * 删除终端升级
     * 
     * @param id 终端升级ID
     * @return 结果
     */
    int deleteBusiTerminalUpgradeById(Long id);

    /**
     * 批量删除终端升级
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteBusiTerminalUpgradeByIds(Long[] ids);

    /**
     * 查询终端升级
     *
     * @param terminalType 终端类型
     * @return 终端升级
     */
    BusiTerminalUpgrade selectBusiTerminalUpgradeLatestByTerminalType(@Param("terminalType") String terminalType);
}
