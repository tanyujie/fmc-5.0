package com.paradisecloud.fcm.dao.mapper;

import java.util.List;
import com.paradisecloud.fcm.dao.model.BusiProfileDtmf;
import com.paradisecloud.fcm.dao.model.DeptRecordCount;

/**
 * DTMF模板Mapper接口
 * 
 * @author lilinhai
 * @date 2021-07-26
 */
public interface BusiProfileDtmfMapper 
{
    /**
     * 查询DTMF模板
     * 
     * @param id DTMF模板ID
     * @return DTMF模板
     */
    public BusiProfileDtmf selectBusiProfileDtmfById(Long id);

    /**
     * 查询DTMF模板列表
     * 
     * @param busiProfileDtmf DTMF模板
     * @return DTMF模板集合
     */
    public List<BusiProfileDtmf> selectBusiProfileDtmfList(BusiProfileDtmf busiProfileDtmf);

    /**
     * 新增DTMF模板
     * 
     * @param busiProfileDtmf DTMF模板
     * @return 结果
     */
    public int insertBusiProfileDtmf(BusiProfileDtmf busiProfileDtmf);

    /**
     * 修改DTMF模板
     * 
     * @param busiProfileDtmf DTMF模板
     * @return 结果
     */
    public int updateBusiProfileDtmf(BusiProfileDtmf busiProfileDtmf);

    /**
     * 删除DTMF模板
     * 
     * @param id DTMF模板ID
     * @return 结果
     */
    public int deleteBusiProfileDtmfById(Long id);

    /**
     * 批量删除DTMF模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiProfileDtmfByIds(Long[] ids);
    
    /**
     * 部门条目计数
     * @author sinhy
     * @since 2021-10-29 10:54 
     * @param businessFieldType
     * @return List<DeptRecordCount>
     */
    List<DeptRecordCount> getDeptRecordCounts();
}
