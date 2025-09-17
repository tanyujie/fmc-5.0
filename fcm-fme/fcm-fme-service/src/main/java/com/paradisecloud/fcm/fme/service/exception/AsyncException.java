/* 
 * <p>Copyright   : LinHai Technologies Co., Ltd. Copyright 2018, All right reserved.</p>
 * <p>Description : <pre>TODO(用一句话描述该文件做什么)</pre></p>
 * <p>FileName    : IllegalFieldException.java</p>
 * <p>Package     : com.meme.crm.model.exception</p>
 * @Creator lilinhai 2018年2月9日 下午11:13:29
 * @Version  V1.0  
 */

package com.paradisecloud.fcm.fme.service.exception;

/**
 * <p>Description    : <pre>TODO(这里用一句话描述这个类的作用)</pre></p>
 * <p>ClassName      : IllegalFieldException</p>
 * @author lilinhai 2018年2月9日 下午11:13:29
 * @version 1.0
 */
public class AsyncException extends RuntimeException
{
    
    /**
     * <p>Info          : long serialVersionUID lilinhai 2018年2月9日 下午11:13:33</p>
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * <p>Title        : IllegalFieldException lilinhai 2018年2月9日 下午11:13:39</p>
     */
    public AsyncException()
    {
        super();
    }
    
    /**
     * <p>Title        : IllegalFieldException lilinhai 2018年2月9日 下午11:13:39</p>
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace 
     */
    public AsyncException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    /**
     * <p>Title        : IllegalFieldException lilinhai 2018年2月9日 下午11:13:39</p>
     * @param message
     * @param cause 
     */
    public AsyncException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * <p>Title        : IllegalFieldException lilinhai 2018年2月9日 下午11:13:39</p>
     * @param message 
     */
    public AsyncException(String message)
    {
        super(message);
    }
    
    /**
     * <p>Title        : IllegalFieldException lilinhai 2018年2月9日 下午11:13:39</p>
     * @param cause 
     */
    public AsyncException(Throwable cause)
    {
        super(cause);
    }
    
}
