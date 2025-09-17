package com.paradisecloud.fcm.mqtt.impls;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.dao.mapper.BusiTerminalSysInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminalSysInfo;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalSysInfoService;
import com.paradisecloud.fcm.dao.model.UpgradeTerminalType;

/**
 * 终端系统信息Service业务层处理
 * 
 * @author zyz
 * @date 2021-10-11
 */
@Service
public class BusiTerminalSysInfoServiceImpl implements IBusiTerminalSysInfoService 
{
    @SuppressWarnings("rawtypes")
	@Autowired
    private BusiTerminalSysInfoMapper busiTerminalSysInfoMapper;

    /**
     * 查询终端系统信息
     * 
     * @param id 终端系统信息ID
     * @return 终端系统信息
     */
    @Override
    public BusiTerminalSysInfo selectBusiTerminalSysInfoById(Long id)
    {
        return busiTerminalSysInfoMapper.selectBusiTerminalSysInfoById(id);
    }

    /**
     * 查询终端系统信息列表
     * 
     * @param busiTerminalSysInfo 终端系统信息
     * @return 终端系统信息
     */
    @Override
    public List<BusiTerminalSysInfo> selectBusiTerminalSysInfoList(BusiTerminalSysInfo busiTerminalSysInfo)
    {
        return busiTerminalSysInfoMapper.selectBusiTerminalSysInfoList(busiTerminalSysInfo);
    }

    /**
     * 新增终端系统信息
     * 
     * @param busiTerminalSysInfo 终端系统信息
     * @return 结果
     */
    @Override
    public int insertBusiTerminalSysInfo(BusiTerminalSysInfo busiTerminalSysInfo)
    {
        busiTerminalSysInfo.setCreateTime(new Date());
        return busiTerminalSysInfoMapper.insertBusiTerminalSysInfo(busiTerminalSysInfo);
    }

    /**
     * 修改终端系统信息
     * 
     * @param busiTerminalSysInfo 终端系统信息
     * @return 结果
     */
    @Override
    public int updateBusiTerminalSysInfo(BusiTerminalSysInfo busiTerminalSysInfo)
    {
        busiTerminalSysInfo.setUpdateTime(new Date());
        return busiTerminalSysInfoMapper.updateBusiTerminalSysInfo(busiTerminalSysInfo);
    }

    /**
     * 批量删除终端系统信息
     * 
     * @param ids 需要删除的终端系统信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiTerminalSysInfoByIds(Long[] ids)
    {
        return busiTerminalSysInfoMapper.deleteBusiTerminalSysInfoByIds(ids);
    }

    /**
     * 删除终端系统信息信息
     * 
     * @param id 终端系统信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiTerminalSysInfoById(Long id)
    {
        return busiTerminalSysInfoMapper.deleteBusiTerminalSysInfoById(id);
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<UpgradeTerminalType> getTerminalTypeByGroup() {
		
		return busiTerminalSysInfoMapper.getTerminalType();
	}
}
