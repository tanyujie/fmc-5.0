package com.paradisecloud.fcm.mcu.plc.model.response.cm;


import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class CmQuerySysResourceStatisticsResponse extends CommonResponse {

    private int used_resource_count;
    private int system_resource_count;

    public int getUsed_resource_count() {
        return used_resource_count;
    }

    public void setUsed_resource_count(int used_resource_count) {
        this.used_resource_count = used_resource_count;
    }

    public int getSystem_resource_count() {
        return system_resource_count;
    }

    public void setSystem_resource_count(int system_resource_count) {
        this.system_resource_count = system_resource_count;
    }

    /**
     * <RESPONSE_TRANS_RSRC_REPORT>
     *     <RETURN_STATUS>
     *         <ID>0</ID>
     *         <DESCRIPTION>Status OK</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>0</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <GET_CARMEL_REPORT>
     *             <RSRC_REPORT_RMX_LIST>
     *                 <RSRC_REPORT_RMX>
     *                     <RSRC_REPORT_ITEM>audio</RSRC_REPORT_ITEM>
     *                     <TOTAL>0</TOTAL>
     *                     <OCCUPIED>0</OCCUPIED>
     *                     <RESERVED>0</RESERVED>
     *                     <FREE>0</FREE>
     *                 </RSRC_REPORT_RMX>
     *                 <RSRC_REPORT_RMX>
     *                     <RSRC_REPORT_ITEM>video</RSRC_REPORT_ITEM>
     *                     <TOTAL>120</TOTAL>
     *                     <OCCUPIED>24</OCCUPIED>
     *                     <RESERVED>0</RESERVED>
     *                     <FREE>96</FREE>
     *                 </RSRC_REPORT_RMX>
     *                 <PORT_GAUGE_VALUE>80</PORT_GAUGE_VALUE>
     *             </RSRC_REPORT_RMX_LIST>
     *             <RSRC_REPORT_RMX_LIST_HD>
     *                 <RSRC_REPORT_RMX>
     *                     <RSRC_REPORT_ITEM>audio</RSRC_REPORT_ITEM>
     *                     <TOTAL>0</TOTAL>
     *                     <OCCUPIED>0</OCCUPIED>
     *                     <RESERVED>0</RESERVED>
     *                     <FREE>0</FREE>
     *                 </RSRC_REPORT_RMX>
     *                 <RSRC_REPORT_RMX>
     *                     <RSRC_REPORT_ITEM>HD720p30_video</RSRC_REPORT_ITEM>
     *                     <TOTAL>60</TOTAL>
     *                     <OCCUPIED>12</OCCUPIED>
     *                     <RESERVED>0</RESERVED>
     *                     <FREE>48</FREE>
     *                     <AVAILABLE_PORTION_PPM>0</AVAILABLE_PORTION_PPM>
     *                 </RSRC_REPORT_RMX>
     *                 <PORT_GAUGE_VALUE>80</PORT_GAUGE_VALUE>
     *             </RSRC_REPORT_RMX_LIST_HD>
     *         </GET_CARMEL_REPORT>
     *     </ACTION>
     * </RESPONSE_TRANS_RSRC_REPORT>
     *
     * @param xml
     */
    public CmQuerySysResourceStatisticsResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_RSRC_REPORT = jsonObject.getJSONObject("RESPONSE_TRANS_RSRC_REPORT");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_RSRC_REPORT.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_RSRC_REPORT.getJSONObject("ACTION");
                    JSONObject GET_CARMEL_REPORT = ACTION.getJSONObject("GET_CARMEL_REPORT");
                    {
                        JSONObject RSRC_REPORT_RMX_LIST_HD = GET_CARMEL_REPORT.getJSONObject("RSRC_REPORT_RMX_LIST_HD");
                        JSONArray RSRC_REPORT_RMX = RSRC_REPORT_RMX_LIST_HD.getJSONArray("RSRC_REPORT_RMX");
                        for (Object rsrcReportRmxObj : RSRC_REPORT_RMX) {
                            if (rsrcReportRmxObj instanceof JSONObject) {
                                JSONObject rsrcReportRmx = (JSONObject) rsrcReportRmxObj;
                                String RSRC_REPORT_ITEM = rsrcReportRmx.getString("RSRC_REPORT_ITEM");
                                if (RSRC_REPORT_ITEM.contains("video")) {
                                    used_resource_count = rsrcReportRmx.getInt("OCCUPIED");
                                    system_resource_count = rsrcReportRmx.getInt("TOTAL");
                                }
                            }
                        }
                    }
//                    {
//                        JSONObject RSRC_REPORT_RMX_LIST = GET_CARMEL_REPORT.getJSONObject("RSRC_REPORT_RMX_LIST");
//                        JSONArray RSRC_REPORT_RMX = RSRC_REPORT_RMX_LIST.getJSONArray("RSRC_REPORT_RMX");
//                        for (Object rsrcReportRmxObj : RSRC_REPORT_RMX) {
//                            if (rsrcReportRmxObj instanceof JSONObject) {
//                                JSONObject rsrcReportRmx = (JSONObject) rsrcReportRmxObj;
//                                String RSRC_REPORT_ITEM = rsrcReportRmx.getString("RSRC_REPORT_ITEM");
//                                if (RSRC_REPORT_ITEM.contains("video")) {
//
//                                }
//                            }
//                        }
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
