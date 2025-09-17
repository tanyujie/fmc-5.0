package com.paradisecloud.fcm.dao.model.vo;

public class DeptTerminalUseCountVo {

    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 使用数
     */
    private Long useCount;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getUseCount() {
        return useCount;
    }

    public void setUseCount(Long useCount) {
        this.useCount = useCount;
    }

    @Override
    public String toString() {
        return "DeptTerminalUseCountVo{" +
                "deptId=" + deptId +
                ", useCount=" + useCount +
                '}';
    }
}
