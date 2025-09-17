package com.paradisecloud.fcm.web.utils;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.model.vo.BusiHistoryParticipantVo;
import com.paradisecloud.fcm.service.minutes.MinutesFileHandler;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DocMeetingMinutesGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DocMeetingMinutesGenerator.class);

    public static void generateMinutes(String meetingName, String meetingNumber, Date startTime,
                                       Date endTime, int venuCount, List<BusiHistoryParticipantVo> venues, String cospace, String number, Long id
    )   {
        XWPFDocument document = new XWPFDocument();


        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText(meetingName + "-会议纪要");
        titleRun.setBold(true);
        titleRun.setFontSize(16);


        XWPFParagraph infoTitle = document.createParagraph();
        XWPFRun infoTitleRun = infoTitle.createRun();
        infoTitleRun.setText("【会议信息】");
        infoTitleRun.setBold(true);
        document.createParagraph();

        long durationMillis = endTime.getTime() - startTime.getTime();
        long durationHours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
        long hours = durationSeconds / 3600; // 计算小时
        long minutes = (durationSeconds % 3600) / 60; // 计算分钟
        long seconds = durationSeconds % 60; // 计算秒

        String durationFormatted = String.format("%d小时 %d分 %d秒", hours, minutes, seconds);

        XWPFTable infoTable = document.createTable(3, 1);
        // 设置表格宽度
        CTTblWidth width = infoTable.getCTTbl().addNewTblPr().addNewTblW();
        width.setType(STTblWidth.PCT);
        width.setW(5000);

        // 设置表格对齐方式
        infoTable.setTableAlignment(TableRowAlign.LEFT); // 设置为居中对齐

        addTableRow(infoTable.getRow(0), "会议名称", meetingName, "会议号码", meetingNumber);
        addTableRow(infoTable.getRow(1), "开始时间", DateUtil.convertDateToString(startTime, null), "会议时长", durationFormatted);
        addTableRow(infoTable.getRow(2), "结束时间", DateUtil.convertDateToString(endTime, null), "参会会场", venuCount + "个");
        // Venues
        XWPFParagraph venueTitle = document.createParagraph();
        XWPFRun venueTitleRun = venueTitle.createRun();
        venueTitleRun.setText("【参会会场】");
        venueTitleRun.setBold(true);

        // 创建一个新表格，第一行为表头
        XWPFTable table = document.createTable();
        // 添加表头
        XWPFTableRow headerRow = table.getRow(0); // 第一个表头行
        headerRow.getCell(0).setText("字段");
        headerRow.addNewTableCell().setText("值");

        for (BusiHistoryParticipantVo venue : venues) {
            // 每个会场添加到表格中
            addVenueToTable(venue, table, document);

            // 在每个会场之间添加空行
            XWPFTableRow emptyRow = table.createRow();
            emptyRow.getCell(0).setText("");  // 添加空行分隔
            emptyRow.getCell(1).setText("");  // 添加空行分隔
        }


//        for (BusiHistoryParticipantVo venue : venues) {
//            XWPFParagraph venuePara = document.createParagraph();
//            XWPFRun venueRun = venuePara.createRun();
//            venueRun.setText("会场名称：" + venue.getName());
//            venueRun.addBreak();
//            venueRun.setText("入会时间：" + (venue.getJoinTime()!=null? DateUtil.convertDateToString(venue.getJoinTime(), null):""));
//            venueRun.addBreak();
//            venueRun.setText("离会时间：" + (venue.getOutgoingTime()!=null?DateUtil.convertDateToString(venue.getOutgoingTime(), null):""));
//            venueRun.addBreak();
//            String jStr= venue.getJoined() ? "是" : "否";
//            venueRun.setText("是否入会：" +jStr);
//            venueRun.addBreak();
//            venueRun.setText("入会时长(秒)：" +venue.getDurationSeconds());
//            venueRun.addBreak();
//            venueRun.setText("终端类型：" +venue.getTerminalTypeName());
//            venueRun.addBreak();
//            venueRun.setText("呼叫地址：" + venue.getRemoteParty());
//            venueRun.addBreak();
//            venueRun.setText("会场图片：");
//            venueRun.addBreak();
//            // 插入base64编码的图片
//            String imagesStr = venue.getImages();
//            if (Strings.isNotBlank(imagesStr)) {
//                try {
//                    String[] imagesList = imagesStr.split(",");
//                    for (String image : imagesList) {
//                        insertBase64Image(document, venuePara, image);
//                        XWPFParagraph spacePara = document.createParagraph();
//                        spacePara.setSpacingAfter(20);
//                        spacePara.setSpacingBefore(10);
//                    }
//                } catch (Exception e) {
//                    logger.info("插入图片失败: " + e.getMessage());
//                }
//            }
//
//        }


        XWPFParagraph recordTitle = document.createParagraph();
        XWPFRun recordTitleRun = recordTitle.createRun();
        recordTitleRun.setText("【会议记录】");
        recordTitleRun.setBold(true);

        // 读取文件内容并写入到 Word 文档
        String minutesFilePath = MinutesFileHandler.generateFilePath(cospace, number, id);
        File file = new File(minutesFilePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(minutesFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    XWPFParagraph paragraph = document.createParagraph();
                    paragraph.createRun().setText(line);
                }
            } catch (IOException e) {
                logger.info("读取文件内容错误============================>: "+e.getMessage());
                e.printStackTrace();
            }
        }
        String pattern = generateFilePath(cospace, meetingNumber, id);
        // Write the document
       // pattern=meetingNumber+"_"+id+".docx";
        try {
            try (FileOutputStream out = new FileOutputStream(pattern)) {
                document.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void addTableRow(XWPFTableRow row, String label1, String value1, String label2, String value2) {
        row.getCell(0).setText(label1 + ": " + value1);
        row.addNewTableCell().setText(label2 + ": " + value2);


    }

    private static void insertBase64Image(XWPFDocument document, XWPFParagraph paragraph, String base64Image) throws Exception {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(bis);

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        // 按比例缩小图片（例如，80%）
        double scale = 0.5;
        width *= scale;
        height *= scale;

        // 将图片数据写入临时文件
        File tempFile = File.createTempFile("image", ".png");
        ImageIO.write(bufferedImage, "png", tempFile);

        // 将图片插入到文档中
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            XWPFRun run = paragraph.createRun();
            run.addPicture(fis, XWPFDocument.PICTURE_TYPE_PNG, "image.png", Units.toEMU(width), Units.toEMU(height));
        }

        // 删除临时文件
        tempFile.delete();
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
        return folderPath + "/" + conferenceNumber + "_" + hisConferenceId + ".docx";
    }

    // 创建表格行
    private static void createTableRow(XWPFTable table, String fieldName, String value) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(fieldName);
        row.getCell(1).setText(value);
    }

    // 插入 Base64 图片到表格单元格中
    private static void insertBase64ImageIntoTable(XWPFDocument document, XWPFTable table, String base64Image) throws Exception {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        InputStream imageInputStream = new ByteArrayInputStream(imageBytes);

        // 创建新行并插入图片
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText("图片");

        // 插入图片到表格的第二个单元格
        XWPFRun run = row.getCell(1).addParagraph().createRun();
        run.addPicture(imageInputStream, XWPFDocument.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(300), Units.toEMU(200)); // 宽 300px, 高 200px
    }

    // 将多张图片插入到同一个单元格中并横向排列
    private static void insertMultipleImagesInRow(XWPFDocument document, XWPFTable table, String[] base64Images) throws Exception {
        // 在表格中新建一行，用于插入图片
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText("图片");

        XWPFParagraph paragraph = row.getCell(1).addParagraph(); // 添加段落到表格的第二个单元格中
        paragraph.setSpacingAfter(200); // 设置段落后间距
        XWPFRun run = paragraph.createRun(); // 创建Run来添加图片

        for (String base64Image : base64Images) {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            InputStream imageInputStream = new ByteArrayInputStream(imageBytes);

            // 插入图片，设置较小的尺寸
            run.addPicture(imageInputStream, XWPFDocument.PICTURE_TYPE_JPEG, "image.jpg", Units.toEMU(100), Units.toEMU(75)); // 宽 100px, 高 75px
            run.addTab(); // 添加制表符，使图片横向排列
        }
    }

    private static void addVenueToTable(BusiHistoryParticipantVo venue, XWPFTable table, XWPFDocument document) {
        // 创建每个会场的信息行
        createTableRow(table, "会场名称", venue.getName());
        createTableRow(table, "入会时间", venue.getJoinTime() != null ? DateUtil.convertDateToString(venue.getJoinTime(), null) : "");
        createTableRow(table, "离会时间", venue.getOutgoingTime() != null ? DateUtil.convertDateToString(venue.getOutgoingTime(), null) : "");
        createTableRow(table, "是否入会", venue.getJoined() ? "是" : "否");
        createTableRow(table, "入会时长(秒)", String.valueOf(venue.getDurationSeconds()));
        createTableRow(table, "终端类型", venue.getTerminalTypeName());
        createTableRow(table, "呼叫地址", venue.getRemoteParty());

        // 插入图片，如果有图片则插入
//        String imagesStr = venue.getImages();
//        if (Strings.isNotBlank(imagesStr)) {
//            createTableRow(table, "会场图片", ""); // 添加图片标题行
//            try {
//                String[] imagesList = imagesStr.split(",");
//                for (String image : imagesList) {
//                    insertBase64ImageIntoTable(document, table, image); // 将图片插入表格
//                }
//            } catch (Exception e) {
//                logger.info("插入图片失败: " + e.getMessage());
//            }
//        }
        // 插入图片，如果有图片则插入
        String imagesStr = venue.getImages();
        if (Strings.isNotBlank(imagesStr)) {
            createTableRow(table, "会场图片", ""); // 添加图片标题行
            try {
                String[] imagesList = imagesStr.split(",");
                insertMultipleImagesInRow(document, table, imagesList); // 将图片插入同一个单元格并横向排列
            } catch (Exception e) {
                logger.info("插入图片失败: " + e.getMessage());
            }
        }
    }







}
