package com.paradisecloud.fcm.web.model.mobile;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.fcm.dao.model.BusiTemplateParticipant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 即时会议请求参数
 * @author nj
 * @date 2022/6/20 11:25
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileConferenceAppointmentRequest implements Serializable {
    /**
     * 会议名称
     */
    private String conferenceName;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 会议时长
     */
    private int durationOfMinutes;
    /**
     * 会议密码
     */
    private String conferencePassword;
    /**
     * 直播地址
     */
    private String streamUrl;

    /**
     * 默认布局 allEqual| speakerOnly| telepresence| stacked| allEqualQuarters| allEqualNinths|
     * allEqualSixteenths| allEqualTwentyFifths| onePlusFive| onePlusSeven| onePlusNine| automatic|
     * onePlusN
     */
    private String defaultViewLayout;
    /**
     * 广播 1默认是 2否
     */
    private int defaultViewIsBroadcast=1;
    /**
     * 补位 1默认是 2否
     */
    private int defaultViewIsFill=1;
    /**
     * 参会者列表
     */
    private List<BusiTemplateParticipant> templateParticipants;

    /**
     * 参会者列表终端ID
     */
    private List<Long> terminalIds;

    /**
     * 主会场
     */
   private Long  masterTerminalId;

    /**
     * 主持人
     */
    private Long  presenter;

    /**
     * 入会是否静音
     */
   private Boolean isMute;

   private int isAutoCreateStreamUrl;

    /** 会议开始时间 */
    private String startTime;

    private Integer conferenceNumber;


    private int defaultViewIsDisplaySelf;
    /**
     * 会议类型:1:预约会议;2:即时会议
     */
    private Integer type;

    /**
     * 更多信息
     */
    private Map<String, Object> businessProperties;
    /**
     * 1:本地直播 2:关闭直播 3:云直播 4:第三方直播
     */
    private Integer streamingEnabled;
    /**
     * 1:开启录制 2:关闭录制
     */
    private Integer recordingEnabled;
    /**
     * 1:自动重呼 2:不自动重呼
     */
    private Integer isAutoCall;

}
