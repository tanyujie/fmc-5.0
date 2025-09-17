package com.paradisecloud.fcm.dao.core;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.mapper.DummyTableMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * 初始化程序
 */
@Order(1)
@Component
public class InitDatabase implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitDatabase.class);

    @Resource
    private DummyTableMapper dummyTableMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 升级数据库
        updateDb();
    }

    private void updateDb() {
        logger.info("初始化升级数据库");

        String fmcmPath = getFmcPath();
        File updateDbDir = new File(fmcmPath + "/fcm-application/lib/db");
        if (updateDbDir.exists() && updateDbDir.isDirectory()) {
            File[] files = updateDbDir.listFiles();
            List<File> dbFileList = new ArrayList<>();
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.startsWith("fcmdb-2.0-") && fileName.endsWith(".sql")) {
                    dbFileList.add(file);
                }
            }
            boolean hasError = false;
            if (dbFileList.size() > 0) {
                List<String> sqlList = new ArrayList<>();
                Collections.sort(dbFileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                for (File file : dbFileList) {
                    logger.info("=================更新数据库：" + file.getName());
                    String sqls = "";
                    BufferedReader fileReader = null;
                    try {
                        fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        String line = null;
                        while (true) {
                            line = fileReader.readLine();
                            if (line == null) {
                                break;
                            }
                            System.out.println(line);
                            sqls += " " + line;
                        }
                    } catch (FileNotFoundException e) {
                        logger.error("读取数据库更新文件出错：" + file.getName());
                        logger.error("更新数据库停止");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (StringUtils.hasText(sqls)) {
                        String[] sqlArr = sqls.split(";");
                        for (String sql : sqlArr) {
                            sql = sql.trim();
                            if (StringUtils.hasText(sql)) {
                                sqlList.add(sql);
                            }
                        }
                    }
                }
                for (String sql : sqlList) {
                    logger.info("更新数据库：" + sql);
                    try {
                        dummyTableMapper.executeSql(sql);
                    } catch (Exception e) {
                        hasError = true;
                        logger.error("数据库更新出错。");
                    }
                }
            }

            if (!hasError) {
                // 备份旧
                File destDirBak = new File(updateDbDir.getAbsolutePath() + "_updated_" + DateUtil.convertDateToString(new Date(), "yyyyMMddHHmmss"));
                try {
                    if (updateDbDir.exists()) {
                        logger.info("备份db文件 " + updateDbDir.getAbsolutePath() + " => " + destDirBak.getAbsolutePath());
                        FileUtils.moveDirectory(updateDbDir, destDirBak);
                    }
                } catch (IOException e) {
                    logger.error("备份db文件发生错误", e);
                    return;
                }
            }
        }

    }

    private String getFmcPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return PathUtil.getRootPath() + "/fcm";
        } else {
            return "/home/fcm";
        }
    }
}
