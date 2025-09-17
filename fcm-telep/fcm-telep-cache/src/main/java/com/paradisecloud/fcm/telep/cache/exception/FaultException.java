package com.paradisecloud.fcm.telep.cache.exception;

/**
 * @author nj
 * @date 2022/10/12 9:25
 */
public class FaultException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public FaultException(String message) {
        super(message);
    }
}
