package com.paradisecloud.fcm.web.service.interfaces;

import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.TerminalSearchVo;

import java.util.List;

/**
 * 终端信息Service接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface IBusiTerminalWebService
{
    /**
     * 查询终端信息
     * 
     * @param id 终端信息ID
     * @return 终端信息
     */
    public BusiTerminal selectBusiTerminalById(Long id);

    /**
     * 查询终端信息列表
     * 
     * @param busiTerminal 终端信息
     * @return 终端信息集合
     */
    public PaginationData<ModelBean> selectBusiTerminalList(TerminalSearchVo busiTerminal);

    /**
     * 新增多个终端信息
     *
     * @param busiTerminals 终端信息
     * @return 结果
     */
    public int insertBusiTerminals(List<BusiTerminal> busiTerminals);

    /**
     * 新增终端信息
     * 
     * @param busiTerminal 终端信息
     * @return 结果
     */
    public int insertBusiTerminal(BusiTerminal busiTerminal);

    /**
     * 新增终端信息
     *
     * @param busiTerminal 终端信息
     * @param isAutoAdd 是否是后端自动添加终端
     * @return 结果
     */
    public int insertBusiTerminal(BusiTerminal busiTerminal, boolean isAutoAdd);

    public String delSpace(String sn);

    /**
     * 修改终端信息
     * 
     * @param busiTerminal 终端信息
     * @return 结果
     */
    public int updateBusiTerminal(BusiTerminal busiTerminal);

    /**
     * 批量删除终端信息
     * 
     * @param ids 需要删除的终端信息ID
     * @return 结果
     */
    public int deleteBusiTerminalByIds(Long[] ids);

    /**
     * 删除终端信息信息
     * 
     * @param id 终端信息ID
     * @return 结果
     */
    public int deleteBusiTerminalById(Long id);

    /**
     * 查询所有的终端列表
     * @return
     */
    List<BusiTerminal> selectAll();
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    /**
     * 判断fcm账号是否有效
     *
     * @return boolean
     * @author zyz
     * @since
     */
    public boolean isFcm(long deptId, String credential);

    BusiTerminal selectBusiTerminal(BusiTerminal busiTerminal);

    /**
     * 获取随机账号
     * @param busiTerminal
     * @return
     */
    String getRandomAccount(BusiTerminal busiTerminal);

    PaginationData<ModelBean> getInfoDisplayTerminal(TerminalSearchVo busiTerminal);
}
