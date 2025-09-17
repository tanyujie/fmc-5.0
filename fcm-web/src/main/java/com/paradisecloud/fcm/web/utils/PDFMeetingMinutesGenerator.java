package com.paradisecloud.fcm.web.utils;



import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.dao.model.vo.BusiHistoryParticipantVo;
import com.paradisecloud.fcm.service.minutes.MinutesFileHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PDFMeetingMinutesGenerator {

    private static final Logger logger = LoggerFactory.getLogger(PDFMeetingMinutesGenerator.class);

    public static void generateMinutes(String meetingName, String meetingNumber, Date startTime,
                                       Date endTime, int venueCount, List<BusiHistoryParticipantVo> venues, String cospace, String number, Long id) {
        Document document = new Document();

        try {
            String pattern = generateFilePdfPath(cospace, meetingNumber, id);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pattern));
            document.open();

            // 设置中文字体
            BaseFont bf = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
            Paragraph title = new Paragraph(meetingName + "-会议纪要", new com.itextpdf.text.Font(bf, 16));
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("【会议信息】", new com.itextpdf.text.Font(bf))); // 添加空行
            document.add(new Paragraph(" ")); // 添加空行
            PdfPTable cTable = new PdfPTable(2);
            // 会议时长计算
            long durationMillis = endTime.getTime() - startTime.getTime();
            long durationHours = TimeUnit.MILLISECONDS.toHours(durationMillis);
            long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
            long hours = durationSeconds / 3600; // 计算小时
            long minutes = (durationSeconds % 3600) / 60; // 计算分钟
            long seconds = durationSeconds % 60; // 计算秒

            String durationFormatted = String.format("%d小时 %d分钟 %d秒", hours, minutes, seconds);
            // 添加会议信息
            cTable.addCell(new Paragraph("会议名称: " + meetingName, new com.itextpdf.text.Font(bf)));
            cTable.addCell(new Paragraph("会议号码: " + meetingNumber, new com.itextpdf.text.Font(bf)));
            cTable.addCell(new Paragraph("开始时间: " + DateUtil.convertDateToString(startTime, null), new com.itextpdf.text.Font(bf)));
            cTable.addCell(new Paragraph("会议时长: " + durationFormatted, new com.itextpdf.text.Font(bf)));
            cTable.addCell(new Paragraph("结束时间: " + DateUtil.convertDateToString(endTime, null), new com.itextpdf.text.Font(bf)));
            cTable.addCell(new Paragraph("参会会场: " + venueCount + "个", new com.itextpdf.text.Font(bf)));
            cTable.addCell(new Paragraph(" ")); // 添加空行
            document.add(cTable);
            // 参会会场
            document.add(new Paragraph("【参会会场】", new com.itextpdf.text.Font(bf)));
            document.add(new Paragraph(" ")); // 添加空行
            if(CollectionUtils.isEmpty(venues)){
                // 创建表格用于展示会场信息
                PdfPTable venueTable = new PdfPTable(2);
                venueTable.addCell(new Paragraph("会场", new com.itextpdf.text.Font(bf)));
                venueTable.addCell(new Paragraph("无", new com.itextpdf.text.Font(bf)));
            }else {
                // 创建表格用于展示会场信息
                PdfPTable venueTable = new PdfPTable(2);

                for (BusiHistoryParticipantVo venue : venues) {
                    venueTable.addCell(new Paragraph("会场名称", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph(venue.getName(), new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph("入会时间", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph(venue.getJoinTime() != null ? DateUtil.convertDateToString(venue.getJoinTime(), null) : "", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph("离会时间", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph(venue.getOutgoingTime() != null ? DateUtil.convertDateToString(venue.getOutgoingTime(), null) : "", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph("是否入会", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph(venue.getJoined() ? "是" : "否", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph("入会时长(秒)", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph(String.valueOf(venue.getDurationSeconds()), new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph("终端类型", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph(venue.getTerminalTypeName(), new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph("呼叫地址", new com.itextpdf.text.Font(bf)));
                    venueTable.addCell(new Paragraph(venue.getRemoteParty(), new com.itextpdf.text.Font(bf)));

                    //处理图片插入
                    String imagesStr = venue.getImages();
                    if (imagesStr != null && !imagesStr.isEmpty()) {

                        String[] imagesList = imagesStr.split(",");
                        for (int i = 0; i < Math.min(imagesList.length, 3); i++) { // 插入最多3张图片
                            byte[] imageBytes = Base64.getDecoder().decode(imagesList[i]);
                            Image img = Image.getInstance(imageBytes);
                            img.scaleToFit(100, 75); // 根据需要调整图片大小
                            int tmp=i+1;
                            venueTable.addCell(new Paragraph("会场图片"+tmp, new com.itextpdf.text.Font(bf)));
                            venueTable.addCell(img);
                        }
                    }
                    venueTable.addCell(new Paragraph("======================", new com.itextpdf.text.Font(bf))); // 分隔线
                    venueTable.addCell(new Paragraph("======================")); // 空白单元格
                }
                document.add(venueTable);
            }

            // 会议记录部分
            document.add(new Paragraph("【会议记录】", new com.itextpdf.text.Font(bf)));
            String minutesFilePath = MinutesFileHandler.generateFilePath(cospace, number, id);
            File file = new File(minutesFilePath);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(minutesFilePath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 将每一行添加到 PDF
                        String indentedLine = "    " + line;
                        document.add(new Paragraph(indentedLine, new com.itextpdf.text.Font(bf)));
                    }
                } catch (IOException e) {
                    logger.info("读取文件内容错误============================>: "+e.getMessage());
                }
            }else {
                document.add(new Paragraph("       无", new com.itextpdf.text.Font(bf)));
            }


        } catch (DocumentException | IOException e) {
            logger.info("写入PDF错误============================>: "+e.getMessage());
        } finally {
            document.close();
        }
    }


    // 辅助方法：格式化日期
    private static String formatDate(Date date) {
        if (date == null) return "";
        // 使用你自己的日期格式化工具，例如 SimpleDateFormat
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    // 辅助方法：计算会议时长（小时）
    private static long calculateDuration(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) return 0;
        long durationMillis = endTime.getTime() - startTime.getTime();
        return java.util.concurrent.TimeUnit.MILLISECONDS.toHours(durationMillis);
    }




    public static String getSpacesPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return PathUtil.getRootPath() + "/spaces";
        } else {
            return "/mnt/nfs/spaces";
        }
    }

    public static String generateFilePdfPath(String coSpaceId, String conferenceNumber, long hisConferenceId) {
        String folderPath = getSpacesPath() + "/" + coSpaceId;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folderPath + "/" + conferenceNumber + "_" + hisConferenceId + ".pdf";
    }

    // 创建表格行
    private static void createTableRow(XWPFTable table, String fieldName, String value) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(fieldName);
        row.getCell(1).setText(value);
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



}
