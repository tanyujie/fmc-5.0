/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : SystemException.java
 * Package : com.paradisecloud.common.exception
 * 
 * @author lilinhai
 * 
 * @since 2020-12-03 10:17
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.zte.attendee.exception;

/**
 * <pre>无可用的FME桥异常</pre>
 * 
 * @author lilinhai
 * @since 2020-12-03 10:17
 * @version V1.0
 */
public class AttendeeCallFailedException extends Exception
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * 
     * @since 2020-12-03 10:17
     */
    private static final long serialVersionUID = 1L;
    
    private long code = 1000;
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-03 10:18
     * @param message
     */
    public AttendeeCallFailedException(String message)
    {
        super(message);
    }
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2020-12-16 15:36 
     * @param message
     * @param cause 
     */
    public AttendeeCallFailedException(String message, Throwable cause)
    {
        super(message, cause);
        
    }



    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2020-12-16 15:33
     * @param code
     * @param message
     */
    public AttendeeCallFailedException(long code, String message)
    {
        super(message);
        this.code = code;
    }

    /**
     * <p>Get Method   :   code long</p>
     * @return code
     */
    public long getCode()
    {
        return code;
    }
}
