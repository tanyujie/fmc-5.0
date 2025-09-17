package com.paradisecloud.fcm.web.service.interfaces;

import com.paradisecloud.fcm.dao.model.vo.McuTypeVo;

import java.util.List;

public interface IBusiAllMcuService {

    /**
     * 获取部门绑定MCU类型列表
     *
     * @param deptId
     * @return
     */
    List<McuTypeVo> getMcuTypeList(Long deptId);

    /**
     * 获取部门绑定默认MCU类型
     *
     * @param deptId
     * @return
     */
    McuTypeVo getDefaultMcuType(Long deptId);

    /**
     * 获取部门绑定MCU类型列表
     *
     * @param deptId
     * @return
     */
    List<McuTypeVo> getLiveMcuTypeList(Long deptId);
}
