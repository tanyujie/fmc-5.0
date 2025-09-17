package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiTerminalSysInfo;

/**
 * 终端系统信息Mapper接口
 * 
 * @author zyz
 * @param <UpgradeTerminalType>
 * @date 2021-10-11
 */
public interface BusiTerminalSysInfoMapper<UpgradeTerminalType> 
{
    /**
     * 查询终端系统信息
     * 
     * @param id 终端系统信息ID
     * @return 终端系统信息
     */
    public BusiTerminalSysInfo selectBusiTerminalSysInfoById(Long id);

    /**
     * 查询终端系统信息列表
     * 
     * @param busiTerminalSysInfo 终端系统信息
     * @return 终端系统信息集合
     */
    public List<BusiTerminalSysInfo> selectBusiTerminalSysInfoList(BusiTerminalSysInfo busiTerminalSysInfo);

    /**
     * 新增终端系统信息
     * 
     * @param busiTerminalSysInfo 终端系统信息
     * @return 结果
     */
    public int insertBusiTerminalSysInfo(BusiTerminalSysInfo busiTerminalSysInfo);

    /**
     * 修改终端系统信息
     * 
     * @param busiTerminalSysInfo 终端系统信息
     * @return 结果
     */
    public int updateBusiTerminalSysInfo(BusiTerminalSysInfo busiTerminalSysInfo);

    /**
     * 删除终端系统信息
     * 
     * @param id 终端系统信息ID
     * @return 结果
     */
    public int deleteBusiTerminalSysInfoById(Long id);

    /**
     * 批量删除终端系统信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTerminalSysInfoByIds(Long[] ids);
    
    
    /**
     * 查询终端类型分组
     * 
     * @param id 终端系统信息ID
     * @return 终端系统信息
     */
    public List<UpgradeTerminalType> getTerminalType();
}
