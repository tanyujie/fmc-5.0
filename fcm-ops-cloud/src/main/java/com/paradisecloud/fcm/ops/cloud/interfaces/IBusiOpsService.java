package com.paradisecloud.fcm.ops.cloud.interfaces;

import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.dao.model.vo.BusiOpsVo;

public interface IBusiOpsService {

    /**
     * 查询OPS
     *
     * @param id OPSID
     * @return OPS
     */
    BusiOpsVo selectBusiOpsById(Long id);

    /**
     * 查询OPS列表
     *
     * @param busiOps OPS
     * @return OPS集合
     */
    PaginationData<Object> selectBusiOpsList(BusiOpsVo busiOps);

    /**
     * 新增OPS
     *
     * @param busiOps OPS
     * @return 结果
     */
    int insertBusiOps(BusiOpsVo busiOps);

    /**
     * 修改OPS
     *
     * @param busiOps OPS
     * @return 结果
     */
    int updateBusiOps(BusiOpsVo busiOps);

    /**
     * 批量删除OPS
     *
     * @param ids 需要删除的OPSID
     * @return 结果
     */
    int deleteBusiOpsByIds(Long[] ids);

    /**
     * 删除OPS信息
     *
     * @param id OPSID
     * @return 结果
     */
    int deleteBusiOpsById(Long id);
}
