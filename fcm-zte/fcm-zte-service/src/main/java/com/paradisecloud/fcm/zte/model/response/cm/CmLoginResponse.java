package com.paradisecloud.fcm.zte.model.response.cm;

import com.paradisecloud.fcm.zte.model.response.CommonResponse;
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

public class CmLoginResponse extends CommonResponse {

    private String randomKey;

    private UserInfo userInfo;

    public String getRandomKey() {
        return randomKey;
    }


    public UserInfo getUserInfo() {
        return userInfo;
    }

    public static class UserInfo{
        private String UserID;
        private String UserName;
        private int UserLevel;
        private String Phone;
        private String Email;

        public String getUserID() {
            return UserID;
        }

        public void setUserID(String userID) {
            UserID = userID;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public int getUserLevel() {
            return UserLevel;
        }

        public void setUserLevel(int userLevel) {
            UserLevel = userLevel;
        }

        public String getPhone() {
            return Phone;
        }

        public void setPhone(String phone) {
            Phone = phone;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }
    }

    /**
     *
     * @param xml
     */
    public CmLoginResponse(String xml) {
        // 解析 SOAP 消息以获取有效负载
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xml)));
            String payload = doc.getDocumentElement().getElementsByTagName("Body").item(0).getTextContent();
            // 将有效负载的 XML 转换为 JSON 对象
            JSONObject jsonObject = XML.toJSONObject(payload);
            if (jsonObject != null) {

                    JSONObject key = jsonObject.getJSONObject("RandomKey");
                    this.randomKey=key.toString();
                    this.status= jsonObject.getString("Result");

                JSONObject UserInfoJSonOb = jsonObject.getJSONObject("UserInfo");
                if(UserInfoJSonOb!=null){
                    UserInfo userInfo = new UserInfo();
                    userInfo.setEmail(UserInfoJSonOb.getString("Email"));
                    userInfo.setPhone(UserInfoJSonOb.getString("Phone"));
                    userInfo.setUserID(UserInfoJSonOb.getString("UserID"));
                    userInfo.setUserName(UserInfoJSonOb.getString("UserName"));
                    userInfo.setUserLevel(UserInfoJSonOb.getInt("UserLevel"));
                    this.userInfo=userInfo;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
