package com.paradisecloud.fcm.fme.model.cms.system;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 系统状态响应类
 *
 * @author zt1994 2019/8/27 17:45
 */
@Getter
@Setter
@ToString
public class SystemStatus
{
    
    /**
     * 当前在呼叫桥接器上运行的软件版本
     */
    private String softwareVersion;
    
    /**
     * 该单元运行的时间长度
     */
    private Integer uptimeSeconds;
    
    /**
     * 呼叫桥接器是否已激活(已获授权);如果没有，它将减少呼叫容量。当前，对于会议服务器总是正确的。
     */
    private Boolean activated;
    
    /**
     * 是否是集群
     */
    private Boolean clusterEnabled;
    
    /**
     * 集群ID
     */
    private String clusterId;
    
    /**
     * 将当前时间戳写入在接收请求时生成的CDR。这将与CDRs中的“time”字段的格式相同
     */
    private Date cdrTime;
    
    /**
     * 请求时活动的call leg的数量
     */
    private Integer callLegsActive;
    
    /**
     * 此会议服务器上同时活动的最高call leg
     */
    private Integer callLegsMaxActive;
    
    /**
     * 已激活但不再连接/存在的call leg的总数
     */
    private Integer callLegsCompleted;
    
    /**
     * 当前总比特率(以每秒比特为单位)对所有输出音频流(从会议服务器发送到远程会议的音频媒体)求和
     */
    private Integer audioBitRateOutgoing;
    
    /**
     * 当前传入音频流的总比特率
     */
    private Integer audioBitRateIncoming;
    
    /**
     * 输出视频流的当前总比特率
     */
    private Integer videoBitRateOutgoing;
    
    /**
     * 当前传入视频流的总比特率
     */
    private Integer videoBitRateIncoming;
    
    /**
     * 将发送的下一个CDR记录的相关器索引。当没有CDR记录已发送，该值为0
     */
    private Integer cdrCorrelatorIndex;
}
