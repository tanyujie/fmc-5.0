package com.paradisecloud.fcm.web.config;

import com.paradisecloud.common.core.model.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author nj
 * @date 2022/11/21 16:26
 */
@RestControllerAdvice
public class Global2ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(com.paradisecloud.fcm.web.config.Global2ExceptionHandler.class);

    @ExceptionHandler(java.sql.SQLException.class)
    public RestResponse handleException(Throwable e)
    {
        log.error(e.getMessage(), e);
        return RestResponse.fail("很抱歉,业务数据错误，请上报系统管理进行处理！");
    }



    @ExceptionHandler(java.lang.NullPointerException.class)
    public RestResponse handleNullPointException(Throwable e)
    {
        log.error(e.getMessage(), e);
        return RestResponse.fail("很抱歉,数据错误，请上报系统管理进行处理！");
    }
}
