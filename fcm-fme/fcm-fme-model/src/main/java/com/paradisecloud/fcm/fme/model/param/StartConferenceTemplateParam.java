package com.paradisecloud.fcm.fme.model.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 开始模板会议请求参数
 *
 * @author bkc
 * @date 2020年10月14日
 */
@Setter
@Getter
@ToString
public class StartConferenceTemplateParam
{
    
    /**
     * 模板ID
     */
    private Integer id;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 会议号
     */
    private String confNum;
    
    /**
     * 会议类型
     */
    private Integer type;
    
    /**
     * 是否自动呼叫
     */
    private Boolean autoDial = false;
}
