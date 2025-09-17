package com.paradisecloud.fcm.web.service;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.web.cache.SystemUpdateCache;
import com.paradisecloud.fcm.web.task.RestoreFmcTask;
import com.paradisecloud.fcm.web.task.UpdateFmcTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Service
public class SystemUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(SystemUpdateService.class);

    public static final String WINDOWS = "windows";

    @Resource
    private TaskService taskService;

    /**
     * 更新FMC
     *
     * @param uploadFile
     * @return
     */
    public boolean updateFmcServer(MultipartFile uploadFile) throws Exception {
        int updateStatus = SystemUpdateCache.getFmcUpdateStatus();
        if (updateStatus >= SystemUpdateCache.UPDATE_STATUS_UPLOADING && updateStatus < SystemUpdateCache.UPDATE_STATUS_UPDATED) {
            throw new CustomException("主机正在更新中，请稍后再试！");
        }
        if (updateStatus >= SystemUpdateCache.UPDATE_STATUS_RESTORING && updateStatus < SystemUpdateCache.UPDATE_STATUS_RESTORED) {
            throw new CustomException("主机正在还原中，请稍后再试！");
        }

        SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_UPLOADING);
        String savePath = getUploadPath();
        String folderPath = null;
        String filePath = null;
        try {
            {
                folderPath = savePath + "/fmc";
                File fold = new File(folderPath);
                if (!fold.exists() || !fold.isDirectory()) {
                    fold.mkdirs();
                }
                fold.setWritable(true, false);
                fold.setReadable(true, false);
            }
            folderPath = savePath + "/fmcm/fmc";
            File fold = new File(folderPath);
            if (!fold.exists() || !fold.isDirectory()) {
                fold.mkdirs();
            }
            fold.setWritable(true, false);
            fold.setReadable(true, false);
            String fileName = "fmc.tty";
            String path = folderPath + fileName;
            File file = new File(path);
            if (file.exists()) {
                deleteFile(file);
            }
            filePath = saveFile(uploadFile, folderPath, fileName);
            SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_UPLOADED);
        } catch (Exception e) {
            e.printStackTrace();
            SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_UPLOAD_ERROR);
            if (e instanceof CustomException) {
                throw e;
            } else {
                throw new CustomException("存储文件失败！");
            }
        }

        UpdateFmcTask updateFmcTask = new UpdateFmcTask("A", 100, filePath);
        taskService.addTask(updateFmcTask);

        return true;
    }

    /**
     * 还原FMC
     *
     * @return
     * @throws Exception
     */
    public boolean restoreFmcServer() throws Exception {
        int updateStatus = SystemUpdateCache.getFmcUpdateStatus();
        if (updateStatus >= SystemUpdateCache.UPDATE_STATUS_UPLOADING && updateStatus < SystemUpdateCache.UPDATE_STATUS_UPDATED) {
            throw new CustomException("主机正在更新中，请稍后再试！");
        }
        if (updateStatus >= SystemUpdateCache.UPDATE_STATUS_RESTORING && updateStatus < SystemUpdateCache.UPDATE_STATUS_RESTORED) {
            throw new CustomException("主机正在还原中，请稍后再试！");
        }

        SystemUpdateCache.updateFmcUpdateStatus(SystemUpdateCache.UPDATE_STATUS_RESTORING);

        RestoreFmcTask restoreFmcTask = new RestoreFmcTask("A", 100);
        taskService.addTask(restoreFmcTask);

        return true;
    }

    public static String saveFile(final MultipartFile file, final String folderPath, final String fileName) throws Exception {
        String filePath = "";
        if (file.isEmpty()) {
            logger.error("文件为空");
            throw new CustomException("上传文件为空！");
        }

        try {
            // 获取保存路径
            File files = new File(folderPath, fileName);
            File parentFile = files.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.transferTo(files);
            File fileNew = new File(folderPath, fileName);
            fileNew.setReadable(true, false);
            fileNew.setExecutable(true, false);
            fileNew.setWritable(true, false);
            filePath = fileNew.getAbsolutePath();
        } catch (IOException e) {
            throw e;
        }
        return filePath;
    }

    public static String getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains(WINDOWS)) {
            return PathUtil.getRootPath() + "/upload";
        } else {
            return "/home/upload";
        }
    }

    private void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            deleteFile(f);
        }
        file.delete();
    }

}
