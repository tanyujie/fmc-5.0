package com.paradisecloud.fcm.license;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConfigMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRecordsMapper;
import com.paradisecloud.fcm.dao.model.BusiConfig;
import com.paradisecloud.fcm.dao.model.BusiRecords;
import com.paradisecloud.fcm.fme.service.interfaces.IRecordingService;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/2 11:43
 */
@Aspect
@Component
@Conditional(CheckEnvironmentCondition.class)
public class RecorderAspect {



    @Resource
    private BusiRecordsMapper busiRecordsMapper;



    @Resource
    private BusiConfigMapper busiConfigMapper;


    public RecorderAspect() {
    }

    @Pointcut("execution(* com.paradisecloud.fcm.web.controller.recording.RecordingController.changeRecordingStatus(..))||execution(* com.paradisecloud.fcm.web.controller.mcu.all.RecordingForAllController.changeRecordingStatus(..))||execution(* com.paradisecloud.fcm.web.controller.mobile.web.MobileWebRecordingController.changeRecordingStatus(..))")
    private void myPointCut() {


    }

    @Before("myPointCut()")
    public void before(JoinPoint joinPoint) throws Exception {

        LicenseContent licenseContent = null;
        try {
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
            licenseContent = licenseManager.verify();
        } catch (Exception e) {
            throw new CustomException("当前服务器没在授权范围内,请联系供应商授权后使用");
        }
        Map extra = (Map<String,Object>)licenseContent.getExtra();
        boolean schedule = (boolean) extra.get("schedule");
        boolean useRecorderLimit = (boolean)extra.get("useRecorderLimit");

        if (schedule) {
            String participantLimitTime = (String) extra.get("participantLimitTime");
            long willTime = DateUtil.convertDateByString(participantLimitTime, null).getTime();
            if (System.currentTimeMillis() - willTime >= 0) {
                throw new CustomException("LICENSE到期 ,请联系供应商授权后使用");
            }
        }

        BusiRecords busiRecords_query = new BusiRecords();
        busiRecords_query.setRecordsFileStatus(2);
        busiRecords_query.setDeptId(100L);
        List<BusiRecords> busiRecords = busiRecordsMapper.selectBusiRecordsList(busiRecords_query);
        int fileCount = 0;
        if(CollectionUtils.isNotEmpty(busiRecords)){
            fileCount = busiRecords.size();
        }


        Object useableSpaceObj = extra.get("useableSpace");
        if(useableSpaceObj==null){
            throw new CustomException("录制空间未授权,请联系供应商授权后使用");
        }
        int useableSpace = (int)useableSpaceObj;
        if(useableSpace==0){
            throw new CustomException("录制空间未授权,请联系供应商授权后使用");
        }
        BusiConfig busiConfig1 = new BusiConfig();
        busiConfig1.setConfigKey("Recording_Files_Storage_Space_Max");
        List<BusiConfig> busiConfigs = busiConfigMapper.selectBusiConfigList(busiConfig1);
        if (busiConfigs != null && busiConfigs.size() > 0) {
            BusiConfig busiConfig = busiConfigs.get(0);
            Double recordingFilesStorageSpaceMax = Double.valueOf(busiConfig.getConfigValue());
            if(recordingFilesStorageSpaceMax.intValue()!=useableSpace){
                busiConfig.setConfigValue(String.valueOf(useableSpace));
                busiConfig.setUpdateTime(new Date());
                busiConfigMapper.updateBusiConfig(busiConfig);
            }
        }else {
            busiConfig1.setCreateTime(new Date());
            busiConfig1.setConfigValue(String.valueOf(useableSpace));
            busiConfigMapper.insertBusiConfig(busiConfig1);
        }

        if(useRecorderLimit){
            int recorderLimit = (int)extra.get("recorderLimit");

            if(fileCount>=recorderLimit){
                throw new CustomException("录制文件数量超过限制,请联系供应商授权后使用");
            }
        }

    }
}
