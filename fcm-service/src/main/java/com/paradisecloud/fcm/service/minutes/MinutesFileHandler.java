package com.paradisecloud.fcm.service.minutes;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MinutesFileHandler extends FileHandler {

    /**
     * Initialize a {@code FileHandler} to write to the given filename,
     * with optional append.
     * <p>
     * The {@code FileHandler} is configured based on {@code LogManager}
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to no limit, the file count is set to one, and the append
     * mode is set to the given {@code append} argument.
     * <p>
     * There is no limit on the amount of data that may be written,
     * so use this with care.
     *
     * @param pattern the name of the output file
     * @param append  specifies append mode
     * @throws IOException              if there are IO problems opening the files.
     * @throws SecurityException        if a security manager exists and if
     *                                  the caller does not have {@code LoggingPermission("control")}.
     * @throws IllegalArgumentException if pattern is an empty string
     */
    private MinutesFileHandler(String pattern, boolean append) throws IOException, SecurityException {
        super(pattern, append);
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder builder = new StringBuilder();
                builder.append(DateUtil.convertDateToString(new Date(record.getMillis()), "yyyy-MM-dd HH:mm:ss"))
                        .append(" - ")
                        .append(record.getMessage())
                        .append("\n");
                return builder.toString();
            }
        };
        setFormatter(formatter);
    }

    public static String getSpacesPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return PathUtil.getRootPath() + "/spaces";
        } else {
            return "/mnt/nfs/spaces";
        }
    }

    public static String generateFilePath(String coSpaceId, String conferenceNumber, long hisConferenceId) {
        String folderPath = getSpacesPath() + "/" + coSpaceId;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folderPath + "/" + conferenceNumber + "_" + hisConferenceId + ".txt";
    }

    public static String generateFileDocPath(String coSpaceId, String conferenceNumber, long hisConferenceId) {
        String folderPath = getSpacesPath() + "/" + coSpaceId;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folderPath + "/" + conferenceNumber + "_" + hisConferenceId + ".docx";
    }
    public static String generateFilePdfPath(String coSpaceId, String conferenceNumber, long hisConferenceId) {
        String folderPath = getSpacesPath() + "/" + coSpaceId;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folderPath + "/" + conferenceNumber + "_" + hisConferenceId + ".pdf";
    }


    public static MinutesFileHandler createHandler(String coSpaceId, String conferenceNumber, long hisConferenceId) throws IOException, SecurityException {
        String pattern = generateFilePath(coSpaceId, conferenceNumber, hisConferenceId);
        return new MinutesFileHandler(pattern, true);
    }
}
