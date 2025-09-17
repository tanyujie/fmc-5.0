package com.paradisecloud.fcm.zte.model.response.cc;

import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.fcm.zte.model.response.cm.CmLoginResponse;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class CcSetConnectMrTerminalResponse extends CommonResponse {


    public CcSetConnectMrTerminalResponse(String xml) {
        // 解析 SOAP 消息以获取有效负载
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xml)));
            String payload = doc.getDocumentElement().getElementsByTagName("Body").item(0).getTextContent();
            // 将有效负载的 XML 转换为 JSON 对象
            JSONObject jsonObject = XML.toJSONObject(payload);
            if (jsonObject != null) {
                this.status= jsonObject.getString("Result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
