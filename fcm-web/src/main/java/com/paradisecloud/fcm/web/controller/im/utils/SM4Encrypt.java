package com.paradisecloud.fcm.web.controller.im.utils;

import java.lang.annotation.*;

/**
 * 标记需要SM4加解密的接口：请求体解密，响应体加密
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SM4Encrypt {
}
