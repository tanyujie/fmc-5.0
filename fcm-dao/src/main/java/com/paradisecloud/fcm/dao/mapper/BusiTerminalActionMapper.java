package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiTerminalAction;

/**
 * 终端动作Mapper接口
 * 
 * @author zyz
 * @date 2021-07-31
 */
public interface BusiTerminalActionMapper 
{
    /**
     * 查询终端动作
     * 
     * @param id 
     * @return BusiTerminalAction
     */
    public BusiTerminalAction selectBusiTerminalActionById(Long id);

    /**
     * 查询终端动作列表
     * 
     * @param busiTerminalAction
     * @return List<BusiTerminalAction>
     */
    public List<BusiTerminalAction> selectBusiTerminalActionList(BusiTerminalAction busiTerminalAction);

    /**
     * 新增终端动作
     * 
     * @param busiTerminalAction 
     * @return int
     */
    public int insertBusiTerminalAction(BusiTerminalAction busiTerminalAction);

    /**
     * 修改终端动作信息
     * 
     * @param busiTerminalAction 
     * @return int
     */
    public int updateBusiTerminalAction(BusiTerminalAction busiTerminalAction);

    /**
     * 删除终端动作
     * 
     * @param id 
     * @return int
     */
    public int deleteBusiTerminalActionById(Long id);

    /**
     * 批量删除终端动作
     * 
     * @param ids 
     * @return int
     */
    public int deleteBusiTerminalActionByIds(Long[] ids);
}
