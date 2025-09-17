/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingCancelException.java
 * Package     : com.paradisecloud.fcm.fme.attendee.exception
 * @author lilinhai 
 * @since 2021-02-26 16:31
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.zte.attendee.exception;

/**  
 * <pre>请加上该类的描述</pre>
 * @author lilinhai
 * @since 2021-02-26 16:31
 * @version V1.0  
 */
public class AttendeeRepeatException extends Exception
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-26 16:31 
     */
    private static final long serialVersionUID = 1L;

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-10 10:25  
     */
    public AttendeeRepeatException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-10 10:25 
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace 
     */
    public AttendeeRepeatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-10 10:25 
     * @param message
     * @param cause 
     */
    public AttendeeRepeatException(String message, Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-10 10:25 
     * @param message 
     */
    public AttendeeRepeatException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-10 10:25 
     * @param cause 
     */
    public AttendeeRepeatException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    
}
