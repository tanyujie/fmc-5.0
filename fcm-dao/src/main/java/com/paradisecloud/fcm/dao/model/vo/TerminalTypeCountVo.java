package com.paradisecloud.fcm.dao.model.vo;

/**
 * 终端类型数类
 */
public class TerminalTypeCountVo {

    /**
     * 类型
     */
    private Integer type;
    /**
     * 数量
     */
    private Long count;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "TerminalTypeCountVo{" +
                "type=" + type +
                ", count=" + count +
                '}';
    }
}
