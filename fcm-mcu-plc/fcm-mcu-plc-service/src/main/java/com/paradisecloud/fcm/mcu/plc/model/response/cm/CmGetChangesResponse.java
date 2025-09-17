package com.paradisecloud.fcm.mcu.plc.model.response.cm;

import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CmGetChangesResponse extends CommonResponse {

    /**
     * <RESPONSE_TRANS_MCU>
     *     <RETURN_STATUS>
     *         <ID>0</ID>
     *         <DESCRIPTION>Status OK</DESCRIPTION>
     *         <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *         <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         <MESSAGE_ID>1</MESSAGE_ID>
     *         <DESCRIPTION_EX></DESCRIPTION_EX>
     *     </RETURN_STATUS>
     *     <ACTION>
     *         <GET_STATE>
     *             <MCU_STATE>
     *                 <ID>1</ID>
     *                 <DESCRIPTION>Normal</DESCRIPTION>
     *                 <MPL_SERIAL_NUMBER>ED154430E804DD</MPL_SERIAL_NUMBER>
     *                 <LICENSING_VALIDATION_STATE>success</LICENSING_VALIDATION_STATE>
     *                 <NUMBER_OF_ACTIVE_ALARMS>0</NUMBER_OF_ACTIVE_ALARMS>
     *                 <NUMBER_OF_CORE_DUMPS>0</NUMBER_OF_CORE_DUMPS>
     *                 <MEDIA_RECORDING>0</MEDIA_RECORDING>
     *                 <COLLECTING_INFO>0</COLLECTING_INFO>
     *                 <PRODUCT_TYPE>Rmx_1800</PRODUCT_TYPE>
     *                 <SYSTEM_CARDS_MODE>meridian</SYSTEM_CARDS_MODE>
     *                 <SSH>false</SSH>
     *                 <SYSTEM_STARTUP_DURATION>
     *                     <SYSTEM_STARTUP_DURATION_TOTAL_SECONDS>30</SYSTEM_STARTUP_DURATION_TOTAL_SECONDS>
     *                     <SYSTEM_STARTUP_DURATION_REMAINING_SECONDS>0</SYSTEM_STARTUP_DURATION_REMAINING_SECONDS>
     *                 </SYSTEM_STARTUP_DURATION>
     *                 <BACKUP_STATE>idle</BACKUP_STATE>
     *                 <RESTORE_STATE>success</RESTORE_STATE>
     *                 <PLCM_INTERNAL_PARAMS>
     *                     <PARAM1></PARAM1>
     *                     <PARAM2></PARAM2>
     *                     <PARAM3>:0</PARAM3>
     *                 </PLCM_INTERNAL_PARAMS>
     *             </MCU_STATE>
     *         </GET_STATE>
     *     </ACTION>
     * </RESPONSE_TRANS_MCU>
     *
     * @param xml
     */
    public CmGetChangesResponse(String xml) {
        JSONObject jsonObject = XML.toJSONObject(xml);
        if (jsonObject != null) {
            try {
                JSONObject RESPONSE_TRANS_MCU = jsonObject.getJSONObject("RESPONSE_TRANS_MCU");
                JSONObject RETURN_STATUS = RESPONSE_TRANS_MCU.getJSONObject("RETURN_STATUS");
                String DESCRIPTION = RETURN_STATUS.getString("DESCRIPTION");
                status = DESCRIPTION;
                if (STATUS_OK.equals(status)) {
                    JSONObject ACTION = RESPONSE_TRANS_MCU.getJSONObject("ACTION");
                    JSONObject GET_STATE = ACTION.getJSONObject("GET_STATE");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
