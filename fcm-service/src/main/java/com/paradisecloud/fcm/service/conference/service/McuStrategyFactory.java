package com.paradisecloud.fcm.service.conference.service;

import com.paradisecloud.fcm.service.conference.cascade.AbstractConference;
import com.paradisecloud.fcm.common.enumer.McuType;

/**
 * @author nj
 * @date 2023/6/12 14:54
 */
public class McuStrategyFactory {

    public static AbstractConference getMcuStrategy(McuType mcuType) {
        AbstractConference abstractConference = null;

        String[] values = McuStrategyEnum.convert(mcuType.getCode()).getValue();

        for (String value : values) {
            try {
                Object service = McuServiceFactory.getBean(Class.forName(value));
                if (service != null) {
                    abstractConference = (AbstractConference) service;
                    break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return abstractConference;
    }

}
