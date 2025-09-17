package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CcSetBannerRequest extends CommonRequest {
    private String id;
    private boolean on;
    private String message_text;
    private String message_font_size;
    private String message_font_size_int;
    private String message_color;
    private String num_of_repetitions;
    private String message_display_speed;
    private String message_display_position;
    private String message_display_position_int;
    private String message_transparence;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getMessage_font_size() {
        return message_font_size;
    }

    public void setMessage_font_size(String message_font_size) {
        this.message_font_size = message_font_size;
    }

    public String getMessage_font_size_int() {
        return message_font_size_int;
    }

    public void setMessage_font_size_int(String message_font_size_int) {
        this.message_font_size_int = message_font_size_int;
    }

    public String getMessage_color() {
        return message_color;
    }

    public void setMessage_color(String message_color) {
        this.message_color = message_color;
    }

    public String getNum_of_repetitions() {
        return num_of_repetitions;
    }

    public void setNum_of_repetitions(String num_of_repetitions) {
        this.num_of_repetitions = num_of_repetitions;
    }

    public String getMessage_display_speed() {
        return message_display_speed;
    }

    public void setMessage_display_speed(String message_display_speed) {
        this.message_display_speed = message_display_speed;
    }

    public String getMessage_display_position() {
        return message_display_position;
    }

    public void setMessage_display_position(String message_display_position) {
        this.message_display_position = message_display_position;
    }

    public String getMessage_display_position_int() {
        return message_display_position_int;
    }

    public void setMessage_display_position_int(String message_display_position_int) {
        this.message_display_position_int = message_display_position_int;
    }

    public String getMessage_transparence() {
        return message_transparence;
    }

    public void setMessage_transparence(String message_transparence) {
        this.message_transparence = message_transparence;
    }

    /**
     *
     * <TRANS_CONF_2>
     *     <TRANS_COMMON_PARAMS>
     *         <MCU_TOKEN>{{mcu_token}}</MCU_TOKEN>
     *         <MCU_USER_TOKEN>{{mcu_user_token}}</MCU_USER_TOKEN>
     *         <ASYNC>
     *             <YOUR_TOKEN1>0</YOUR_TOKEN1>
     *             <YOUR_TOKEN2>0</YOUR_TOKEN2>
     *         </ASYNC>
     *         <MESSAGE_ID>2</MESSAGE_ID>
     *     </TRANS_COMMON_PARAMS>
     *     <ACTION>
     *         <SET_MESSAGE_OVERLAY>
     *             <ID>106</ID>
     *             <MESSAGE_OVERLAY>
     *                 <ON>true</ON>
     *                 <MESSAGE_TEXT>ddddddddddddd</MESSAGE_TEXT>
     *                 <MESSAGE_FONT_SIZE>small</MESSAGE_FONT_SIZE>
     *                 <MESSAGE_FONT_SIZE_INT>12</MESSAGE_FONT_SIZE_INT>
     *                 <MESSAGE_COLOR>white_font_on_light_blue_background</MESSAGE_COLOR>
     *                 <NUM_OF_REPETITIONS>3</NUM_OF_REPETITIONS>
     *                 <MESSAGE_DISPLAY_SPEED>slow</MESSAGE_DISPLAY_SPEED>
     *                 <MESSAGE_DISPLAY_POSITION>bottom</MESSAGE_DISPLAY_POSITION>
     *                 <MESSAGE_DISPLAY_POSITION_INT>90</MESSAGE_DISPLAY_POSITION_INT>
     *                 <MESSAGE_TRANSPARENCE>50</MESSAGE_TRANSPARENCE>
     *             </MESSAGE_OVERLAY>
     *         </SET_MESSAGE_OVERLAY>
     *     </ACTION>
     * </TRANS_CONF_2>
     *
     * @return
     */
    @Override
    public String buildToXml() {
        String xml = "" +
                "<TRANS_CONF_2>" +
                "<TRANS_COMMON_PARAMS>" +
                "<MCU_TOKEN>" + mcuToken + "</MCU_TOKEN>" +
                "<MCU_USER_TOKEN>" + mcuUserToken + "</MCU_USER_TOKEN>" +
                "<ASYNC>" +
                "<YOUR_TOKEN1>" + yourToken1 + "</YOUR_TOKEN1>" +
                "<YOUR_TOKEN2>" + yourToken2 + "</YOUR_TOKEN2>" +
                "</ASYNC>" +
                "<MESSAGE_ID>" + messageId + "</MESSAGE_ID>" +
                "</TRANS_COMMON_PARAMS>" +
                "<ACTION>" +
                "<SET_MESSAGE_OVERLAY>" +
                "<ID>" + id + "</ID>" +
                "<MESSAGE_OVERLAY>" +
                "<ON>" + on + "</ON>" +
                "<MESSAGE_TEXT>" + message_text + "</MESSAGE_TEXT>" +
                "<MESSAGE_FONT_SIZE>" + message_font_size + "</MESSAGE_FONT_SIZE>" +
                "<MESSAGE_FONT_SIZE_INT>" + message_font_size_int + "</MESSAGE_FONT_SIZE_INT>" +
                "<MESSAGE_COLOR>" + message_color + "</MESSAGE_COLOR>" +
                "<NUM_OF_REPETITIONS>" + num_of_repetitions + "</NUM_OF_REPETITIONS>" +
                "<MESSAGE_DISPLAY_SPEED>" + message_display_speed + "</MESSAGE_DISPLAY_SPEED>" +
                "<MESSAGE_DISPLAY_POSITION>" + message_display_position + "</MESSAGE_DISPLAY_POSITION>" +
                "<MESSAGE_DISPLAY_POSITION_INT>" + message_display_position_int + "</MESSAGE_DISPLAY_POSITION_INT>" +
                "<MESSAGE_TRANSPARENCE>" + message_transparence + "</MESSAGE_TRANSPARENCE>" +
                "</MESSAGE_OVERLAY>" +
                "</SET_MESSAGE_OVERLAY>" +
                "</ACTION>" +
                "</TRANS_CONF_2>";
        return xml;
    }
}
