package com.paradisecloud.fcm.service.impls;

import java.util.List;
import java.util.Date;

import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.service.interfaces.IBusiUserTerminalService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户终端Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-06-24
 */
@Service
public class BusiUserTerminalServiceImpl implements IBusiUserTerminalService
{
    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;

    /**
     * 查询用户终端
     * 
     * @param id 用户终端ID
     * @return 用户终端
     */
    @Override
    public BusiUserTerminal selectBusiUserTerminalById(Long id)
    {
        return busiUserTerminalMapper.selectBusiUserTerminalById(id);
    }

    /**
     * 查询用户终端列表
     * 
     * @param busiUserTerminal 用户终端
     * @return 用户终端
     */
    @Override
    public List<BusiUserTerminal> selectBusiUserTerminalList(BusiUserTerminal busiUserTerminal)
    {
        return busiUserTerminalMapper.selectBusiUserTerminalList(busiUserTerminal);
    }

    /**
     * 新增用户终端
     * 
     * @param busiUserTerminal 用户终端
     * @return 结果
     */
    @Override
    public int insertBusiUserTerminal(BusiUserTerminal busiUserTerminal)
    {
        busiUserTerminal.setCreateTime(new Date());
        return busiUserTerminalMapper.insertBusiUserTerminal(busiUserTerminal);
    }

    /**
     * 修改用户终端
     * 
     * @param busiUserTerminal 用户终端
     * @return 结果
     */
    @Override
    public int updateBusiUserTerminal(BusiUserTerminal busiUserTerminal)
    {
        return busiUserTerminalMapper.updateBusiUserTerminal(busiUserTerminal);
    }

    /**
     * 批量删除用户终端
     * 
     * @param ids 需要删除的用户终端ID
     * @return 结果
     */
    @Override
    public int deleteBusiUserTerminalByIds(Long[] ids)
    {
        return busiUserTerminalMapper.deleteBusiUserTerminalByIds(ids);
    }

    /**
     * 删除用户终端信息
     * 
     * @param id 用户终端ID
     * @return 结果
     */
    @Override
    public int deleteBusiUserTerminalById(Long id)
    {
        return busiUserTerminalMapper.deleteBusiUserTerminalById(id);
    }
}
