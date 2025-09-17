/* 
 * <p>Copyright   : LinHai Technologies Co., Ltd. Copyright 2018, All right reserved.</p>
 * <p>Description : <pre>TODO(用一句话描述该文件做什么)</pre></p>
 * <p>FileName    : IllegalFieldException.java</p>
 * <p>Package     : com.meme.crm.model.exception</p>
 * @Creator lilinhai 2018年2月9日 下午11:13:29
 * @Version  V1.0  
 */

package com.paradisecloud.fcm.fme.cache.exception;

/**
 * <p>Description    : <pre>TODO(这里用一句话描述这个类的作用)</pre></p>
 * <p>ClassName      : IllegalFieldException</p>
 * @author lilinhai 2018年2月9日 下午11:13:29
 * @version 1.0
 */
public class FmeCacheException extends RuntimeException
{
    
    /**
     * <p>Info          : long serialVersionUID lilinhai 2018年2月9日 下午11:13:33</p>
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * <p>Title        : IllegalFieldException lilinhai 2018年2月9日 下午11:13:39</p>
     */
    public FmeCacheException()
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
    public FmeCacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    /**
     * <p>Title        : IllegalFieldException lilinhai 2018年2月9日 下午11:13:39</p>
     * @param message
     * @param cause 
     */
    public FmeCacheException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * <p>Title        : IllegalFieldException lilinhai 2018年2月9日 下午11:13:39</p>
     * @param message 
     */
    public FmeCacheException(String message)
    {
        super(message);
    }
    
    /**
     * <p>Title        : IllegalFieldException lilinhai 2018年2月9日 下午11:13:39</p>
     * @param cause 
     */
    public FmeCacheException(Throwable cause)
    {
        super(cause);
    }
    
}
