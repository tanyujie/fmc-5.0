package com.paradisecloud.fcm.fme.service.impls;

import com.paradisecloud.fcm.fme.service.interfaces.IAllRecordingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author johnson liu
 * @date 2021/4/30 11:30
 */
@Service
public class AllRecordingServiceImpl implements IAllRecordingService {
    private Logger logger = LoggerFactory.getLogger(AllRecordingServiceImpl.class);

    @Override
    public List<Map<String, Object>> getFolder(String conferenceNumber, String coSpaceId) {
        List<Map<String, Object>> folders = new ArrayList<>();
        return folders;
    }

    @Override
    public void deleteRecordingFile(String ids, String fileName, String coSpaceId) {
    }
}
