/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AppointDatetimeRange.java
 * Package     : com.paradisecloud.fcm.fme.conference.model
 * @author sinhy 
 * @since 2021-10-21 15:52
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model;

/**  
 * <pre>预约时间范围</pre>
 * @author sinhy
 * @since 2021-10-21 15:52
 * @version V1.0  
 */
public class AppointDatetimeRange
{
    
    private String startTime;
    private String endTime;
    
    /**
     * <p>Get Method   :   startTime String</p>
     * @return startTime
     */
    public String getStartTime()
    {
        return startTime;
    }
    /**
     * <p>Set Method   :   startTime String</p>
     * @param startTime
     */
    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }
    /**
     * <p>Get Method   :   endTime String</p>
     * @return endTime
     */
    public String getEndTime()
    {
        return endTime;
    }
    /**
     * <p>Set Method   :   endTime String</p>
     * @param endTime
     */
    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }
    
    public boolean isIntersection(AppointDatetimeRange appointDatetimeRange)
    {
        return (startTime.compareTo(appointDatetimeRange.getStartTime()) >= 0 && startTime.compareTo(appointDatetimeRange.getEndTime()) <= 0)
                || (endTime.compareTo(appointDatetimeRange.getStartTime()) >= 0 && endTime.compareTo(appointDatetimeRange.getEndTime()) <= 0);
    }
    
    /**
     * <pre>TODO 请加上该方法的描述</pre>
     * @author sinhy
     * @since 2021-10-21 15:52 
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "[开始时间=" + startTime + ", 结束时间=" + endTime + "]";
    }
}
