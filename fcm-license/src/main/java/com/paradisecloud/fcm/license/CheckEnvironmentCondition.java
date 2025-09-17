package com.paradisecloud.fcm.license;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

/**
 * @author nj
 * @date 2023/6/14 14:13
 */
public class CheckEnvironmentCondition  implements Condition {

    public static final String PROD = "prod";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String activeProfile = environment.getActiveProfiles()[0];
        if(Objects.equals(PROD,activeProfile)){
            return true;
        }
        return false;
    }
}
