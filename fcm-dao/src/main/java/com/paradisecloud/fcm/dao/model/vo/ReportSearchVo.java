package com.paradisecloud.fcm.dao.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @Description 报表查询条件基类
 * @Author johnson liu
 * @Date 2021/6/6 17:20
 **/
@Getter
@Setter
@ToString
public class ReportSearchVo {
    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 会议号码
     */
    private String number;
    /**
     * 当前页面
     */
    private Integer pageNum;
    /**
     * 每页显示条数
     */
    private Integer pageSize;
    /**
     * 会议名称
     */
    private String name;
    /**
     * 终端名称
     */
    private String deviceName;
    /**
     * 是否模式会议
     */
    private boolean modeConference;

    private Integer minutesDoc;
}
