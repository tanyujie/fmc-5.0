package com.paradisecloud.fcm.ops.cloud.interfaces;

import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.dao.model.vo.BusiClientVo;

public interface IBusiClientService {

    /**
     * 查询客户端
     *
     * @param id 客户端ID
     * @return 客户端
     */
    BusiClientVo selectBusiClientById(Long id);

    /**
     * 查询客户端列表
     *
     * @param busiClient 客户端
     * @return 客户端集合
     */
    PaginationData<Object> selectBusiClientList(BusiClientVo busiClient);

    /**
     * 新增客户端
     *
     * @param busiClient 客户端
     * @return 结果
     */
    int insertBusiClient(BusiClientVo busiClient);

    /**
     * 修改客户端
     *
     * @param busiClient 客户端
     * @return 结果
     */
    int updateBusiClient(BusiClientVo busiClient);

    /**
     * 批量删除客户端
     *
     * @param ids 需要删除的客户端ID
     * @return 结果
     */
    int deleteBusiClientByIds(Long[] ids);

    /**
     * 删除客户端信息
     *
     * @param id 客户端ID
     * @return 结果
     */
    int deleteBusiClientById(Long id);
}
