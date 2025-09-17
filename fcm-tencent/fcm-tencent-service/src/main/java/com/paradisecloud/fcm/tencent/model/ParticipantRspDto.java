package com.paradisecloud.fcm.tencent.model;

import com.paradisecloud.fcm.common.enumer.AttendType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/25 15:38
 */
@Data
@NoArgsConstructor
public class ParticipantRspDto implements Comparable<ParticipantRspDto> {
    private Boolean isMute;
    private Boolean isOnline;
    private String id;
    private String uri;
    private String name;
    private Integer ipProtocolType;
    private String dialMode;
    private String encodeType;
    private Boolean forward;
    private Integer rate;
    private Boolean voice;
    private Integer audioProtocol;
    private Integer videoProtocol;
    private Integer videoResolution;
    private Integer dataConfProtocol;
    private String serviceZoneId;
    private String serviceZoneName;
    private Boolean mainParticipant;
    private BackupParticipantDTO backupParticipant;
    private Long deptId;
    private Long terminalId;
    private Boolean isCascade;
    private Object terminalType;
    private Integer weight;
    /**
     * 默认要外呼
     */
    private int attendType = AttendType.OUT_BOUND.getValue();
    @Override
    public int compareTo(ParticipantRspDto o) {
        return o.weight.compareTo(this.weight);
    }

    @NoArgsConstructor
    @Data
    public static class BackupParticipantDTO {
        private String uri;
        private String name;
        private String terminalType;
    }

    private ParticipantState participantState;


    public int getAttendType() {
        return attendType;
    }

    public void setAttendType(int attendType) {
        this.attendType = attendType;
    }
}
