package com.paradisecloud.fcm.zte.model.response.cc;

import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import org.json.JSONArray;
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
import java.util.List;

/**
 * @author nj
 * @date 2024/4/8 14:38
 */
public class CcgetParticipantStatusV4Response extends CommonResponse {


    private int TotalCount;

    private List<PartStaV4> partStaV4List;


    public static class PartStaV4
        {
            private String Id;
            private String Name;
            private String No;
            private int State;
            private int Mute;
            private int Silent;
            private int Mic;
            private int Alr;
            private int Camera;
            private int Handup;
            private String Ext;

            public String getId() {
                return Id;
            }

            public void setId(String id) {
                Id = id;
            }

            public String getName() {
                return Name;
            }

            public void setName(String name) {
                Name = name;
            }

            public String getNo() {
                return No;
            }

            public void setNo(String no) {
                No = no;
            }

            public int getState() {
                return State;
            }

            public void setState(int state) {
                State = state;
            }

            public int getMute() {
                return Mute;
            }

            public void setMute(int mute) {
                Mute = mute;
            }

            public int getSilent() {
                return Silent;
            }

            public void setSilent(int silent) {
                Silent = silent;
            }

            public int getMic() {
                return Mic;
            }

            public void setMic(int mic) {
                Mic = mic;
            }

            public int getAlr() {
                return Alr;
            }

            public void setAlr(int alr) {
                Alr = alr;
            }

            public int getCamera() {
                return Camera;
            }

            public void setCamera(int camera) {
                Camera = camera;
            }

            public int getHandup() {
                return Handup;
            }

            public void setHandup(int handup) {
                Handup = handup;
            }

            public String getExt() {
                return Ext;
            }

            public void setExt(String ext) {
                Ext = ext;
            }
        }





    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }

    public List<PartStaV4> getPartStaV4List() {
        return partStaV4List;
    }

    public void setPartStaV4List(List<PartStaV4> partStaV4List) {
        this.partStaV4List = partStaV4List;
    }

    public CcgetParticipantStatusV4Response(String xml) {
        // 解析 SOAP 消息以获取有效负载
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xml)));
            String payload = doc.getDocumentElement().getElementsByTagName("Body").item(0).getTextContent();
            // 将有效负载的 XML 转换为 JSON 对象
            JSONObject jsonObject = XML.toJSONObject(payload);
            if (jsonObject != null) {
                this.TotalCount=jsonObject.getInt("TotalCount");
                this.status= jsonObject.getString("Result");
                JSONArray partStaV4Array = jsonObject.getJSONArray("PartStaV4");
                for(int i=0;i<partStaV4Array.length();i++){
                    PartStaV4 partStaV4Obj = new PartStaV4();
                    JSONObject jsonObjectPart = partStaV4Array.getJSONObject(i);
                    partStaV4Obj.Id=jsonObjectPart.getString("Id");
                    partStaV4Obj.State=jsonObjectPart.getInt("State");
                    partStaV4Obj.Name=jsonObjectPart.getString("Name");
                    partStaV4Obj.No=jsonObjectPart.getString("No");
                    partStaV4Obj.Mute=jsonObjectPart.getInt("Mute");
                    partStaV4Obj.Silent=jsonObjectPart.getInt("Silent");
                    partStaV4Obj.Alr=jsonObjectPart.getInt("Alr");
                    partStaV4Obj.Camera =  jsonObjectPart.getInt("Camera");
                    partStaV4Obj.Handup=jsonObjectPart.getInt("Handup");
                    partStaV4Obj.Mic=jsonObjectPart.getInt("Mic");
                    partStaV4Obj.Ext=jsonObjectPart.getString("Ext");
                    partStaV4List.add(partStaV4Obj);
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
