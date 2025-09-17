package com.paradisecloud.fcm.common.utils;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.common.vo.EncryptIdVo;

public class EncryptIdUtil {

    // 加密id -------- start
    // 用于会议id，集群id，部门绑定id等与因mcu不同而需要唯一的上下文id
    public static String generateKey(Long id, McuType mcuType) {
        return generateKey(id, mcuType.getCode());
    }

    public static String generateKey(Long id, String mcuType) {
        return id + ":" + mcuType;
    }

    public static String generateEncryptId(String key) {
        if (StringUtils.isNotEmpty(key)) {
            return AesEnsUtils.getAesEncryptor().encryptToHex(key);
        }
        return null;
    }

    public static String generateEncryptId(Long id, String mcuType) {
        if (id != null && StringUtils.isNotEmpty(mcuType)) {
            return generateEncryptId(generateKey(id, mcuType));
        }
        return null;
    }

    public static String generateEncryptId(EncryptIdVo encryptIdVo) {
        if (encryptIdVo != null) {
            return generateEncryptId(encryptIdVo.getId(), encryptIdVo.getMcuType().getCode());
        }
        return null;
    }

    public static String parasToKey(String encryptId) {
        if (StringUtils.isNotEmpty(encryptId)) {
            return AesEnsUtils.getAesEncryptor().decryptHexToString(encryptId);
        }
        return null;
    }

    public static EncryptIdVo parasEncryptId(String encryptId) {
        EncryptIdVo encryptIdVo = new EncryptIdVo();
        String key = parasToKey(encryptId);
        String[] contextKeyArr = key.split(":");
        String mcuTypeStr = contextKeyArr[1];
        Long id = Long.valueOf(contextKeyArr[0]);
        McuType mcuType = McuType.convert(mcuTypeStr);
        encryptIdVo.setId(id);
        encryptIdVo.setMcuType(mcuType);
        return encryptIdVo;
    }

    public static EncryptIdVo parasKey(String key) {
        EncryptIdVo encryptIdVo = new EncryptIdVo();
        String[] contextKeyArr = key.split(":");
        String mcuTypeStr = contextKeyArr[1];
        Long id = Long.valueOf(contextKeyArr[0]);
        McuType mcuType = McuType.convert(mcuTypeStr);
        encryptIdVo.setId(id);
        encryptIdVo.setMcuType(mcuType);
        return encryptIdVo;
    }
    // 加密id -------- end

    // 会议id -------- start
    // 用于会议id因mcu不同而需要唯一的上下文id
    public static String generateContextKey(Long id, McuType mcuType) {
        return generateKey(id, mcuType);
    }

    public static String generateContextKey(Long id, String mcuType) {
        return generateKey(id, mcuType);
    }

    public static String generateConferenceId(String contextKey) {
        return generateEncryptId(contextKey);
    }

    public static String generateConferenceId(Long id, String mcuType) {
        return generateEncryptId(id, mcuType);
    }

    public static String generateConferenceId(ConferenceIdVo conferenceIdVo) {
        return generateEncryptId(conferenceIdVo);
    }

    public static String parasToContextKey(String conferenceId) {
        return parasToKey(conferenceId);
    }

    public static ConferenceIdVo parasConferenceId(String conferenceId) {
        ConferenceIdVo conferenceIdVo = new ConferenceIdVo();
        EncryptIdVo encryptIdVo = parasEncryptId(conferenceId);
        conferenceIdVo.setId(encryptIdVo.getId());
        conferenceIdVo.setMcuType(encryptIdVo.getMcuType());
        return conferenceIdVo;
    }

    public static ConferenceIdVo parasContextKey(String contextKey) {
        ConferenceIdVo conferenceIdVo = new ConferenceIdVo();
        EncryptIdVo encryptIdVo = parasKey(contextKey);
        conferenceIdVo.setId(encryptIdVo.getId());
        conferenceIdVo.setMcuType(encryptIdVo.getMcuType());
        return conferenceIdVo;
    }
    // 会议id -------- end
}
