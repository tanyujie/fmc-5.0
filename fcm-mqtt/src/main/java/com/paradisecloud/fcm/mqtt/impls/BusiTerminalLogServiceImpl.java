package com.paradisecloud.fcm.mqtt.impls;

import java.util.List;
import java.io.File;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.dao.mapper.BusiTerminalLogMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminalLog;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalLogService;
import com.paradisecloud.fcm.terminal.fs.common.FileConvert;

/**
 * 终端日志信息Service业务层处理
 * 
 * @author zyz
 * @date 2021-10-13
 */
@Service
public class BusiTerminalLogServiceImpl implements IBusiTerminalLogService 
{
    @Autowired
    private BusiTerminalLogMapper busiTerminalLogMapper;

    /**
     * 查询终端日志信息
     * 
     * @param id 终端日志信息ID
     * @return 终端日志信息
     */
    @Override
    public BusiTerminalLog selectBusiTerminalLogById(Long id)
    {
        return busiTerminalLogMapper.selectBusiTerminalLogById(id);
    }

    /**
     * 查询终端日志信息列表
     * 
     * @param busiTerminalLog 终端日志信息
     * @return 终端日志信息
     */
    @Override
    public List<BusiTerminalLog> selectBusiTerminalLogList(BusiTerminalLog busiTerminalLog)
    {
        return busiTerminalLogMapper.selectBusiTerminalLogList(busiTerminalLog);
    }

    /**
     * 新增终端日志信息
     * 
     * @param busiTerminalLog 终端日志信息
     * @return 结果
     */
    @Override
    public int insertBusiTerminalLog(BusiTerminalLog busiTerminalLog)
    {
        busiTerminalLog.setCreateTime(new Date());
        return busiTerminalLogMapper.insertBusiTerminalLog(busiTerminalLog);
    }

    /**
     * 修改终端日志信息
     * 
     * @param busiTerminalLog 终端日志信息
     * @return 结果
     */
    @Override
    public int updateBusiTerminalLog(BusiTerminalLog busiTerminalLog)
    {
        busiTerminalLog.setUpdateTime(new Date());
        return busiTerminalLogMapper.updateBusiTerminalLog(busiTerminalLog);
    }

    /**
     * 批量删除终端日志信息
     * 
     * @param ids 需要删除的终端日志信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiTerminalLogByIds(Long[] ids)
    {
    	int del = 0;
    	if(null != ids && ids.length > 0) {
    		for (int i = 0; i < ids.length; i++) {
    			Long id = ids[i];
    			BusiTerminalLog busiTerminalLog = busiTerminalLogMapper.selectBusiTerminalLogById(id);
    			if(null != busiTerminalLog) {
    				File file = new File(busiTerminalLog.getLogFilePath() + busiTerminalLog.getLogFileName());
    				FileConvert.getInstance().deleteFile(file);
    			}
    			
    			del = busiTerminalLogMapper.deleteBusiTerminalLogById(id);
			}
    	}
        return del;
    }

    /**
     * 删除终端日志信息信息
     * 
     * @param id 终端日志信息ID
     * @return 结果
     */
    @Override
    public int deleteBusiTerminalLogById(Long id)
    {
        return busiTerminalLogMapper.deleteBusiTerminalLogById(id);
    }
}
