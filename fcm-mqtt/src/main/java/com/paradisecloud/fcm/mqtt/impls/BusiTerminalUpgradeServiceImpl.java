package com.paradisecloud.fcm.mqtt.impls;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paradisecloud.fcm.common.enumer.AppType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.dao.mapper.BusiTerminalUpgradeMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminalUpgrade;
import com.paradisecloud.fcm.mqtt.constant.MqttConfigConstant;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalUpgradeService;
import com.sinhy.exception.SystemException;

/**
 * 终端升级Service业务层处理
 * 
 * @author zyz
 * @date 2021-10-11
 */
@Service
public class BusiTerminalUpgradeServiceImpl implements IBusiTerminalUpgradeService 
{
    @Autowired
    private BusiTerminalUpgradeMapper busiTerminalUpgradeMapper;

    /**
     * 查询终端升级
     * 
     * @param id 终端升级ID
     * @return 终端升级
     */
    @Override
    public BusiTerminalUpgrade selectBusiTerminalUpgradeById(Long id)
    {
        return busiTerminalUpgradeMapper.selectBusiTerminalUpgradeById(id);
    }

    /**
     * 查询终端升级列表
     * 
     * @param busiTerminalUpgrade 终端升级
     * @return 终端升级
     */
    @Override
    public List<BusiTerminalUpgrade> selectBusiTerminalUpgradeList(BusiTerminalUpgrade busiTerminalUpgrade)
    {
        return busiTerminalUpgradeMapper.selectBusiTerminalUpgradeList(busiTerminalUpgrade);
    }

    /**
     * 新增终端升级
     * 
     * @param busiTerminalUpgrade 终端升级
     * @return 结果
     */
    @Override
    public int insertBusiTerminalUpgrade(BusiTerminalUpgrade busiTerminalUpgrade)
    {
    	int upgrade;
    	if(StringUtils.isEmpty(busiTerminalUpgrade.getTerminalType())) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, "终端类型不能为空!");
    	}
    	
    	if(StringUtils.isEmpty(busiTerminalUpgrade.getServerUrl())) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, "服务器地址不能为空!");
    	}
    	
    	if(StringUtils.isEmpty(busiTerminalUpgrade.getVersionNum())) 
    	{
    		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, "版本号不能为空!");
    	}

        if(StringUtils.isEmpty(busiTerminalUpgrade.getVersionName()))
        {
            throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, "版本名不能为空!");
        }
    	
    	try {
    		busiTerminalUpgrade.setCreateTime(new Date());
            busiTerminalUpgrade.setUpdateTime(new Date());
            upgrade = busiTerminalUpgradeMapper.insertBusiTerminalUpgrade(busiTerminalUpgrade);
		} catch (Exception e) {
			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, "请检查终端类型" + busiTerminalUpgrade.getTerminalType() + "是否重复！");
		}
        
        return upgrade;
    }

    /**
     * 修改终端升级
     * 
     * @param busiTerminalUpgrade 终端升级
     * @return 结果
     */
    @Override
    public int updateBusiTerminalUpgrade(BusiTerminalUpgrade busiTerminalUpgrade)
    {
    	if(null != busiTerminalUpgrade) {
    		if(StringUtils.isEmpty(busiTerminalUpgrade.getTerminalType())) 
        	{
    			throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, "终端类型不能为空!");
        	}
        	
        	if(StringUtils.isEmpty(busiTerminalUpgrade.getServerUrl())) 
        	{
        		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, "服务器地址不能为空!");
        	}
        	
        	if(StringUtils.isEmpty(busiTerminalUpgrade.getVersionNum())) 
        	{
        		throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, "版本号不能为空!");
        	}

            if(StringUtils.isEmpty(busiTerminalUpgrade.getVersionName()))
            {
                throw new SystemException(MqttConfigConstant.EXCEPTION_ONE_ZERO, "版本名不能为空!");
            }
    	}
    	
        busiTerminalUpgrade.setUpdateTime(new Date());
        return busiTerminalUpgradeMapper.updateBusiTerminalUpgrade(busiTerminalUpgrade);
    }

    /**
     * 批量删除终端升级
     * 
     * @param ids 需要删除的终端升级ID
     * @return 结果
     */
    @Override
    public int deleteBusiTerminalUpgradeByIds(Long[] ids)
    {
        return busiTerminalUpgradeMapper.deleteBusiTerminalUpgradeByIds(ids);
    }

    /**
     * 删除终端升级信息
     * 
     * @param id 终端升级ID
     * @return 结果
     */
    @Override
    public int deleteBusiTerminalUpgradeById(Long id)
    {
        return busiTerminalUpgradeMapper.deleteBusiTerminalUpgradeById(id);
    }

    /**
     * 终端 根据类型和客户ID获取版本信息
     *
     * @param terminalType
     * @param versionCode
     *
     * @return
     */
    @Override
    public Map<String, Object> selectBusiAppVersion(String terminalType, String versionCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("checkNow", false);
        AppType appType = AppType.convertByType(terminalType);
        if (appType != null) {
            BusiTerminalUpgrade busiTerminalUpgrade = busiTerminalUpgradeMapper.selectBusiTerminalUpgradeLatestByTerminalType(appType.getCode());
            if (busiTerminalUpgrade != null) {
                if (StringUtils.isEmpty(versionCode) || busiTerminalUpgrade.getVersionNum().length() > versionCode.length() || busiTerminalUpgrade.getVersionNum().compareTo(versionCode) > 0) {

                    if (busiTerminalUpgrade != null) {
                        map.put("serverUrl", busiTerminalUpgrade.getServerUrl());
                        map.put("versionName", busiTerminalUpgrade.getVersionName());
                        map.put("versionNum", busiTerminalUpgrade.getVersionNum());
                        map.put("description", busiTerminalUpgrade.getVersionDescription());
                        map.put("checkNow", true);
                    }
                }
            }

        }
        return map;
    }
}
