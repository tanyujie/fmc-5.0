package com.paradisecloud.fcm.web.model.smc;

import com.paradisecloud.com.fcm.smc.modle.TemplateTerminal;
import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConferencePaticipant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/3/17 10:44
 */
@Data
@NoArgsConstructor
public class BusiSmcAppointmentConferenceInfo {

    private Integer id;

    /** 部门id */
    private Long deptId;

    private String chairmanPassword;

    /** 会议类型 */
    private String conferenceTimeType;

    /** 时长 */
    private Integer duration;

    private String guestPassword;

    /** utc时间 */
    private String scheduleStartTime;

    /** 主题 */
    private String subject;

    /** 虚拟号码 */
    private String vmrNumber;

    /** 视频会议，语音会议 */
    private String type;

    /** 最大入会数量 */
    private Integer maxParticipantNum=500;

    /** 静音入会 */
    private Integer voiceActive=2;

    /** 带宽 */
    private Integer rate=1920;


    private String periodConferenceTime;


    private String durationPerPeriodUnit;


    private String periodUnitType;


    private String startDate;


    private String endDate;


    private Integer weekIndexInMonthMode;

    /** 会议ID */
    private String conferenceId;

    /** 视频分辨率（MPI_1080P） */
    private String videoResolution;

    /** svc视频分辨率 */
    private String svcVideoResolution;

    /** 自动静音 */
    private Integer autoMute=2;

    /** 直播 */
    private Integer supportLive=2;

    /** 录播 */
    private Integer supportRecord=2;

    /** 数据会议 */
    private Integer amcRecord=2;

    private List<BusiSmcAppointmentConferencePaticipant> templateTerminalList;


}
