package com.paradisecloud.fcm.mcu.zj.model.request.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CcSetBannerRequest extends CommonRequest {

    /**
     * font_size : 36
     * bg_color : 0
     * y_pos : 80
     * font_color : 16777215
     * speed : 20
     * font_name : san
     * to_role : speaker
     * message : 感谢体验云视频
     * has_title : 0
     * bg_alpha : 0
     * h_align : 1
     * v_align : 2
     * h_margin : 10
     * v_margin : 10
     */

    public static final String to_role_guest = "guest";
    public static final String to_role_speaker = "speaker";
    public static final String to_role_chair = "chair";

    private int font_size = 36;
    private int bg_color = 0;
    private int y_pos = 80;
    private int font_color = 16777215;
    private int speed = 20;
    private String font_name = "san";
    private String to_role;
    private String message;
    private int has_title = 1;
    private int bg_alpha = 0;
    private int h_align = 1;
    private int v_align = 2;
    private int h_margin = 10;
    private int v_margin = 10;

    public int getFont_size() {
        return font_size;
    }

    public void setFont_size(int font_size) {
        this.font_size = font_size;
    }

    public int getBg_color() {
        return bg_color;
    }

    public void setBg_color(int bg_color) {
        this.bg_color = bg_color;
    }

    public int getY_pos() {
        return y_pos;
    }

    public void setY_pos(int y_pos) {
        this.y_pos = y_pos;
    }

    public int getFont_color() {
        return font_color;
    }

    public void setFont_color(int font_color) {
        this.font_color = font_color;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getFont_name() {
        return font_name;
    }

    public void setFont_name(String font_name) {
        this.font_name = font_name;
    }

    public String getTo_role() {
        return to_role;
    }

    public void setTo_role(String to_role) {
        this.to_role = to_role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHas_title() {
        return has_title;
    }

    public void setHas_title(int has_title) {
        this.has_title = has_title;
    }

    public int getBg_alpha() {
        return bg_alpha;
    }

    public void setBg_alpha(int bg_alpha) {
        this.bg_alpha = bg_alpha;
    }

    public int getH_align() {
        return h_align;
    }

    public void setH_align(int h_align) {
        this.h_align = h_align;
    }

    public int getV_align() {
        return v_align;
    }

    public void setV_align(int v_align) {
        this.v_align = v_align;
    }

    public int getH_margin() {
        return h_margin;
    }

    public void setH_margin(int h_margin) {
        this.h_margin = h_margin;
    }

    public int getV_margin() {
        return v_margin;
    }

    public void setV_margin(int v_margin) {
        this.v_margin = v_margin;
    }
}
