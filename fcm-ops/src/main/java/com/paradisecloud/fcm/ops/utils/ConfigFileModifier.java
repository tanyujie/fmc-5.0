package com.paradisecloud.fcm.ops.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Map;
import java.util.Objects;

/**
 * @author nj
 * @date 2024/5/27 9:32
 */
public class ConfigFileModifier {

    private static Logger logger=LoggerFactory.getLogger(ConfigFileModifier.class);

    // 修改 XML 配置文件
    public static void modifyXmlConfigFile(String filePath, Map<String, String> changes) {
        try {
            // 加载 XML 文档
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            // 获取并修改变量
            NodeList variables = doc.getElementsByTagName("variable");
            for (int i = 0; i < variables.getLength(); i++) {
                Element variable = (Element) variables.item(i);
                String name = variable.getAttribute("name");
                if (changes.containsKey(name)) {
                    variable.setAttribute("value", changes.get(name));
                }
            }

            // 写回 XML 文件
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            logger.info("XML 配置已更新成功：" + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void modifyAttribute(String filePath, String elementName, String attributeName, String newValue) throws Exception {
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        NodeList elementNodes = doc.getElementsByTagName(elementName);
        for (int i = 0; i < elementNodes.getLength(); i++) {
            Element listElement = (Element) elementNodes.item(i);

            String name_ = listElement.getAttribute("name");
            if(Objects.equals(name_, "fme")){
                NodeList nodeNodes = listElement.getElementsByTagName(attributeName);
                for (int j = 0; j < nodeNodes.getLength(); j++) {
                    Element nodeElement = (Element) nodeNodes.item(j);
                    nodeElement.setAttribute("name", newValue+":5030");
                }
            }


        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }
    // 修改 INI 配置文件
    public static void modifyIniConfigFile(String filePath, Map<String, String> changes) {
        try {
            // 读取配置文件
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                for (Map.Entry<String, String> entry : changes.entrySet()) {
                    if (trimmedLine.startsWith(entry.getKey() + " ")) {
                        line = entry.getKey() + " " + entry.getValue() + ";";
                        break;
                    }
                }
                content.append(line).append(System.lineSeparator());
            }
            reader.close();

            // 写回配置文件
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content.toString());
            writer.close();

            logger.info("INI 配置已更新成功：" + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void restartService(String serviceName) {
        String command = "sudo systemctl restart " + serviceName;
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info(serviceName + " 服务已成功重启。");
            } else {
                logger.info("重启 " + serviceName + " 服务时出错。退出码：" + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
