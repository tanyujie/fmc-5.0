package com.paradisecloud.fcm.terminal.fs.db;

import com.alibaba.druid.util.StringUtils;
import com.sinhy.spring.BeanFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Map;
/**
 * free switch事务处理
 */
@Component
@Aspect
@Order(1)
public class FreeSwitchTransactionAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreeSwitchTransactionAspect.class);

    /** 是否启用FS特殊连接 */
    @Value("${freeswitch.userinfo.useFcmDb}")
    private boolean userinfo_db_useFcmDb = false;

    @Around("@annotation(freeSwitchTransaction)")
    public Object around(ProceedingJoinPoint joinPoint, FreeSwitchTransaction freeSwitchTransaction) throws Throwable {
        if (!userinfo_db_useFcmDb) {
            DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
            Map<String, Connection> transactionConnections = null;
            try {
                databasePool.startTransaction();
                transactionConnections = databasePool.getTransactionConnections();
                if (transactionConnections != null) {
                    for (String ip : transactionConnections.keySet()) {
                        Connection connection = transactionConnections.get(ip);
                        if (connection != null && !connection.isClosed()) {
                            connection.setAutoCommit(false);
                        }
                    }
                }
                Object proceed = joinPoint.proceed();
                if (transactionConnections != null) {
                    for (String ip : transactionConnections.keySet()) {
                        Connection connection = transactionConnections.get(ip);
                        if (connection != null && !connection.isClosed()) {
                            connection.commit();
                        }
                    }
                }
                databasePool.endTransaction();
                return proceed;
            } catch (Throwable throwable) {
                boolean dropConnection = false;
                if (transactionConnections != null) {
                    for (String ip : transactionConnections.keySet()) {
                        Connection connection = transactionConnections.get(ip);
                        if (connection != null && !connection.isClosed()) {
                            try {
                                connection.rollback();
                            } catch (Exception e) {
                                dropConnection = true;
                            }
                        } else {
                            dropConnection = true;
                        }
                    }
                }
                databasePool.endTransaction(dropConnection);
                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

                String value = freeSwitchTransaction.value();
                if (StringUtils.isEmpty(value)) {
                    value = methodSignature.getMethod().getDeclaringClass().getName() + "#" +
                            methodSignature.getMethod().getName();
                }
                String[] parameterNames = methodSignature.getParameterNames();
                Object[] args = joinPoint.getArgs();
                StringBuilder sb = new StringBuilder();
                sb.append("【").append(value).append("】").append("调用异常， 异常参数【");
                if (parameterNames != null && parameterNames.length > 0) {
                    for (int i = 0; i < parameterNames.length; i++) {
                        sb.append(parameterNames[i]).append(" : ").append(args[i]);
                        if (i < parameterNames.length - 1) {
                            sb.append(",");
                        }
                    }
                }
                sb.append("】");
                LOGGER.error(sb.toString());
                throw throwable;
            }
        } else {
            return joinPoint.proceed();
        }
    }

}
