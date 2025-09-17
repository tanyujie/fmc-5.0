package com.paradisecloud.fcm.common.cache;

import com.paradisecloud.fcm.common.utils.RSAUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * 共通参数缓存
 */
public class CommonConfigCache {

    /**
     * 录制文件存储最大空间
     */
    private Double recordingFilesStorageSpaceMax;

    private RSAUtil.SignCertInfo signCertInfo = null;
    private Date jarCreateDate = null;

    private CommonConfigCache() {

    }

    public static CommonConfigCache getInstance() {
        CommonConfigCache instance = CommonConfigCache.InnerClass.INSTANCE;

        return instance;
    }

    public void initCommonConfig() {
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("server.pkcs12");
            RSAUtil.SignCertInfo signCertInfo = RSAUtil.parse(inputStream, "123456");
            setSignCertInfo(signCertInfo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String jarPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            // 如果无法从Manifest中获取，则使用文件的最后修改日期作为近似创建日期
            jarCreateDate = new Date(new File(jarPath).lastModified());
        } catch (Exception e) {
//            e.printStackTrace();
        }
        if (jarCreateDate == null) {
            jarCreateDate = new Date();
        }
    }

    private static class InnerClass {
        private final static CommonConfigCache INSTANCE = new CommonConfigCache();
    }

    public Double getRecordingFilesStorageSpaceMax() {
        return recordingFilesStorageSpaceMax;
    }

    public void setRecordingFilesStorageSpaceMax(Double recordingFilesStorageSpaceMax) {
        this.recordingFilesStorageSpaceMax = recordingFilesStorageSpaceMax;
    }

    public RSAUtil.SignCertInfo getSignCertInfo() {
        return signCertInfo;
    }

    public void setSignCertInfo(RSAUtil.SignCertInfo signCertInfo) {
        this.signCertInfo = signCertInfo;
    }

    public Date getJarCreateDate() {
        return jarCreateDate;
    }

    public void setJarCreateDate(Date jarCreateDate) {
        this.jarCreateDate = jarCreateDate;
    }
}
