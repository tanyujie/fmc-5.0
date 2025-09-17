package com.paradisecloud.fcm.mqtt.interfaces;

import java.util.List;
import java.util.Map;

import com.paradisecloud.fcm.dao.model.BusiTerminalUpgrade;

/**
 * 终端升级Service接口
 * 
 * @author zyz
 * @date 2021-10-11
 */
public interface IBusiTerminalUpgradeService 
{
    /**
     * 查询终端升级
     * 
     * @param id 终端升级ID
     * @return 终端升级
     */
    public BusiTerminalUpgrade selectBusiTerminalUpgradeById(Long id);

    /**
     * 查询终端升级列表
     * 
     * @param busiTerminalUpgrade 终端升级
     * @return 终端升级集合
     */
    public List<BusiTerminalUpgrade> selectBusiTerminalUpgradeList(BusiTerminalUpgrade busiTerminalUpgrade);

    /**
     * 新增终端升级
     * 
     * @param busiTerminalUpgrade 终端升级
     * @return 结果
     */
    public int insertBusiTerminalUpgrade(BusiTerminalUpgrade busiTerminalUpgrade);

    /**
     * 修改终端升级
     * 
     * @param busiTerminalUpgrade 终端升级
     * @return 结果
     */
    public int updateBusiTerminalUpgrade(BusiTerminalUpgrade busiTerminalUpgrade);

    /**
     * 批量删除终端升级
     * 
     * @param ids 需要删除的终端升级ID
     * @return 结果
     */
    public int deleteBusiTerminalUpgradeByIds(Long[] ids);

    /**
     * 删除终端升级信息
     * 
     * @param id 终端升级ID
     * @return 结果
     */
    public int deleteBusiTerminalUpgradeById(Long id);

    /**
     * 终端 根据类型和客户ID获取版本信息
     * @param terminalType
     * @param versionCode
     * @return
     */
    Map<String,Object> selectBusiAppVersion(String terminalType, String versionCode);
}
