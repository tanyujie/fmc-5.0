package com.paradisecloud.fcm.common.exception;

/**
 * 参数异常
 *
 * @author zt1994 2019/7/12 15:44
 */
public class ParamException extends RuntimeException
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2020-12-16 17:13 
     */
    private static final long serialVersionUID = 1L;

    public ParamException()
    {
        super();
    }
    
    public ParamException(String message)
    {
        super(message);
    }
    
    public ParamException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ParamException(Throwable cause)
    {
        super(cause);
    }
    
    protected ParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
