package com.paradisecloud.fcm.terminal.fs.db;

import java.lang.annotation.*;

/**
 * free switch数据库事务
 * free switch数据库相关处理须添加此注解
 *
 * @see FreeSwitchTransactionAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FreeSwitchTransaction {

    /**
     * 如果没有指定该值，则打印的方法名称为 '类名#方法名'
     * @return 自定义名称
     */
    String value() default "";

}
