package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;
import com.paradisecloud.fcm.dao.model.vo.TerminalTypeCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 终端信息Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiTerminalMapper 
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
    public List<BusiTerminal> selectBusiTerminalList(BusiTerminal busiTerminal);

    /**
     * 新增终端信息
     * 
     * @param busiTerminal 终端信息
     * @return 结果
     */
    public int insertBusiTerminal(BusiTerminal busiTerminal);

    /**
     * 修改终端信息
     * 
     * @param busiTerminal 终端信息
     * @return 结果
     */
    public int updateBusiTerminal(BusiTerminal busiTerminal);

    /**
     * 删除终端信息
     * 
     * @param id 终端信息ID
     * @return 结果
     */
    public int deleteBusiTerminalById(Long id);

    /**
     * 批量删除终端信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiTerminalByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts(Integer businessFieldType);

    /**
     * 查询ip和设备号终端信息列表（非注册终端）
     *
     * @param ip ip
     * @param number 设备号
     * @return 终端信息集合
     */
    List<BusiTerminal> selectBusiTerminalListForIpTerminal(@Param("ip") String ip, @Param("number") String number);

    /**
     * 查询终端类型及对应数量
     *
     * @param deptId 部门ID
     * @return
     */
    List<TerminalTypeCountVo> selectTerminalTypeCount(@Param("deptId") Long deptId);

    /**
     * 查询部门终端数
     *
     * @param deptId
     * @return
     */
    List<DeptRecordCount> getDeptTerminalCount(@Param("deptId") Long deptId);

    /**
     * 查询终端信息列表
     *
     * @param busiTerminal 终端信息
     * @return 终端信息集合
     */
    List<BusiTerminal> selectBusiTerminalListOfNotBoundByUser(BusiTerminal busiTerminal);

    /**
     * 获取可用的终端号
     *
     * @return 为空时表示可从1开始
     */
    Integer getAvailableTerminalNum();

    /**
     * 更新终端号
     *
     * @param busiTerminal
     * @return
     */
    int updateTerminalNum(BusiTerminal busiTerminal);

    /**
     * 获取可用的终端号(ZJ终端)
     *
     * @return 为空时表示可从1开始
     */
    Integer getAvailableTerminalNumForZj();

    /**
     * 查询期限日期的终端信息列表（过期日期<期限日期 并且 可用）
     *
     * @param expireDate 期限日期
     * @return 终端信息集合
     */
    List<BusiTerminal> selectBusiTerminalForExpire(@Param("expireDate") Date expireDate);

    /**
     * 根据号段范围获取可用的fcm终端号
     * @return
     */
    Long getAlreadyExistTerminalCredential(@Param("type") int type, @Param("start") Long start, @Param("end") Long end);
}
