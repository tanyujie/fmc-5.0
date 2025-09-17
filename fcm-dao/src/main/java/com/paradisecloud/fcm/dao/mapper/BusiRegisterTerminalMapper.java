package com.paradisecloud.fcm.dao.mapper;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiRegisterTerminal;

/**
 * 需绑定fs账号的终端Mapper接口
 * 
 * @author zyz
 * @date 2021-11-04
 */
public interface BusiRegisterTerminalMapper 
{
    /**
     * 查询需绑定fs账号的终端
     * 
     * @param id 需绑定fs账号的终端ID
     * @return 需绑定fs账号的终端
     */
    public BusiRegisterTerminal selectBusiRegisterTerminalById(Long id);

    /**
     * 查询需绑定fs账号的终端列表
     * 
     * @param busiRegisterTerminal 需绑定fs账号的终端
     * @return 需绑定fs账号的终端集合
     */
    public List<BusiRegisterTerminal> selectBusiRegisterTerminalList(BusiRegisterTerminal busiRegisterTerminal);

    /**
     * 新增需绑定fs账号的终端
     * 
     * @param busiRegisterTerminal 需绑定fs账号的终端
     * @return 结果
     */
    public int insertBusiRegisterTerminal(BusiRegisterTerminal busiRegisterTerminal);

    /**
     * 修改需绑定fs账号的终端
     * 
     * @param busiRegisterTerminal 需绑定fs账号的终端
     * @return 结果
     */
    public int updateBusiRegisterTerminal(BusiRegisterTerminal busiRegisterTerminal);

    /**
     * 删除需绑定fs账号的终端
     * 
     * @param id 需绑定fs账号的终端ID
     * @return 结果
     */
    public int deleteBusiRegisterTerminalById(Long id);

    /**
     * 批量删除需绑定fs账号的终端
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiRegisterTerminalByIds(Long[] ids);

    /**
     * 根据mac或者code查询
     *
     * @param busiRegisterTerminal
     * @return busiRegisterTerminal
     */
    public List<BusiRegisterTerminal> selectBusiRegisterTerminalByCodeAndMac(BusiRegisterTerminal busiRegisterTerminal);
}
