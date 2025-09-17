package com.paradisecloud.fcm.cdr.service.core;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrRecordingService;
import com.paradisecloud.fcm.cdr.service.model.RecordElement;
import com.paradisecloud.fcm.cdr.service.model.RecordingElement;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper;
import com.paradisecloud.fcm.dao.mapper.BusiRecordSettingMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.dao.model.BusiRecordSetting;
import com.paradisecloud.fcm.dao.model.CdrRecording;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;

/**
 * @author johnson liu
 * @date 2021/5/13 23:13
 */
@Component
public class RecordingStrategy implements XmlReadStrategy<CdrRecording>
{
    private static final String MESSAGE_TYPE = "recordingStart";
    
    private static final Logger logger = LoggerFactory.getLogger(RecordingStrategy.class);
    
    @Autowired
    private BusiRecordSettingMapper busiRecordSettingMapper;
    
    @Autowired
    private BusiConferenceNumberMapper busiConferenceNumberMapper;
    
    @Autowired
    private ICdrRecordingService cdrRecordingService;
    
    @Override
    public CdrRecording readToBean(String session, RecordElement recordElement)
    {
        CdrRecording cdrRecording = new CdrRecording();
        // 读取节点属性
        String typeAttribute = recordElement.getType();
        String time = recordElement.getTime();
        Integer recordIndex = recordElement.getRecordIndex();
        Integer correlatorIndex = recordElement.getCorrelatorIndex();
        RecordingElement recordingElement = recordElement.getRecording();
        
        Date date = DateUtil.convertDateByString(time);
        
        cdrRecording.setRecordType((MESSAGE_TYPE.equals(typeAttribute)) ? 1 : 0);
        cdrRecording.setRecordIndex(recordIndex);
        cdrRecording.setCorrelatorIndex(correlatorIndex);
        cdrRecording.setTime(date);
        cdrRecording.setSession(session);
        
        BeanUtils.copyProperties(recordingElement, cdrRecording);
        cdrRecording.setCdrId(recordingElement.getId());
        cdrRecording.setCreateTime(new Date());
        cdrRecording.setCallId(recordingElement.getCall());
        
        logger.info("转化CdrRecording-JavaBean后:{}, \n{}", cdrRecording, recordingElement);
        return cdrRecording;
    }
    
    @Override
    public synchronized void executeAdd(CdrRecording cdrRecording)
    {
        cdrRecordingService.insertCdrRecording(cdrRecording);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void executeAdd(String session, RecordElement recordElement, String fmeIp)
    {
        CdrRecording cdrRecording = readToBean(session, recordElement);
        
//        if (cdrRecording.getRecordType().equals(0))
//        {
//            // SSH远程读取文件大小
//            Call call = FmeDataCache.getCallByUuid(cdrRecording.getCallId());
//            if (call != null)
//            {
//                CoSpace coSpace =  FmeDataCache.getCoSpaceById(call.getCoSpace());
//                if (coSpace != null)
//                {
//                    BusiConferenceNumber busiConferenceNumber = busiConferenceNumberMapper.selectBusiConferenceNumberById(Long.parseLong(coSpace.getUri()));
//                    if (busiConferenceNumber != null)
//                    {
//                        BusiRecordSetting recordSetting = new BusiRecordSetting();
//                        recordSetting.setStatus(YesOrNo.YES.getValue());
//                        recordSetting.setDeptId(busiConferenceNumber.getDeptId());
//                        List<BusiRecordSetting> busiRecordSettings = busiRecordSettingMapper.selectBusiRecordSettingList(recordSetting);
//                        if (busiRecordSettings != null && !busiRecordSettings.isEmpty()) {
//                            String path = new StringBuffer(busiRecordSettings.get(0).getPath())
//                                    .append("/")
//                                    .append(busiRecordSettings.get(0).getFolder())
//                                    .append(File.separator)
//                                    .append(coSpace.getId())
//                                    .append(File.separator)
//                                    .append(cdrRecording.getPath().split("_")[2]).append(".mp4")
//                                    .toString();
//                            File file = new File(path);
//                            long s = file.length();
//                            logger.info("远程录制文件["+path+"]大小读取：" + s);
//                            cdrRecording.setFileSize(s);
//                        }
//                    }
//                }
//            }
//        }
        cdrRecordingService.insertCdrRecording(cdrRecording);
    }
}
