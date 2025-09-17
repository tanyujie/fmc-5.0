package com.paradisecloud.fcm.web.cache;

public class SystemUpdateCache {

    /**
     * 更新状态：0 正常 1 上传中 2 上传完成 3 解析中 4 更新中 5 还原中 6 备份中 7 重启准备 9 重启中
     * 11 上传错误 12 解析错误 13 更新错误 14 还原错误 15 备份错误
     * 21 更新完成 22 还原完成 23 备份完成
     */
    public static final int UPDATE_STATUS_NORMAL = 0;
    public static final int UPDATE_STATUS_UPLOADING = 1;
    public static final int UPDATE_STATUS_UPLOADED = 2;
    public static final int UPDATE_STATUS_ANALYZING = 3;
    public static final int UPDATE_STATUS_UPDATING = 4;
    public static final int UPDATE_STATUS_RESTORING = 5;
    public static final int UPDATE_STATUS_BACKUPING = 6;
    public static final int UPDATE_STATUS_REBOOT_STANDBY = 8;
    public static final int UPDATE_STATUS_REBOOTING = 9;
    public static final int UPDATE_STATUS_UPLOAD_ERROR = 11;
    public static final int UPDATE_STATUS_ANALYSIS_ERROR = 12;
    public static final int UPDATE_STATUS_UPDATE_ERROR = 13;
    public static final int UPDATE_STATUS_RESTORE_ERROR = 14;
    public static final int UPDATE_STATUS_BACKUP_ERROR = 15;
    public static final int UPDATE_STATUS_UPDATED = 21;
    public static final int UPDATE_STATUS_RESTORED = 22;
    public static final int UPDATE_STATUS_BACKUPED = 23;

    /**
     * FMC更新状态
     */
    public volatile static int fmcUpdateStatus = 0;
    public volatile static long fmcUpdateStatusTime = 0;
    public volatile static String fmcVersion = null;

    public static void updateFmcUpdateStatus(int status) {
        fmcUpdateStatus = status;
        fmcUpdateStatusTime = System.currentTimeMillis();
    }

    public static int getFmcUpdateStatus() {
        return fmcUpdateStatus;
    }

    public static long getFmcUpdateStatusTime() {
        return fmcUpdateStatusTime;
    }

    public static void updateFmcVersion(String version) {
        fmcVersion = version;
    }

    public static String getFmcVersion() {
        if (fmcVersion != null) {
            return fmcVersion;
        }
        return "未知";
    }

}
