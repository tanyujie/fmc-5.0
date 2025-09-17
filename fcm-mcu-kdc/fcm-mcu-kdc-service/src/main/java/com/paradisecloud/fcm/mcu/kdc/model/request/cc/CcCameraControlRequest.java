package com.paradisecloud.fcm.mcu.kdc.model.request.cc;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CcCameraControlRequest extends CommonRequest {

    // 远摇摄像头的动作
    // start：开始
    public static final int state_start = 0;
    // stop：停止
    public static final int state_stop = 1;

    // 移动的方向
    // in:放大
    public static final int type_in = 9;
    // out:缩小
    public static final int type_out = 10;
    // left:向左
    public static final int type_left = 3;
    // right:向右
    public static final int type_right = 4;
    // up:向上
    public static final int type_up = 1;
    // down:向下
    public static final int type_down = 2;

    /**
     * state : 0
     * type : 1
     */

    private String conf_id;
    private String mt_id;
    private int state;
    private int type;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMt_id() {
        return mt_id;
    }

    public void setMt_id(String mt_id) {
        this.mt_id = mt_id;
    }

    @Override
    public List<NameValuePair> buildToList() {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("params", JSON.toJSONString(this)));
        return list;
    }

    public static int convertToType(String movement) {
        if ("left".equals(movement)) {
            return type_left;
        } else if ("right".equals(movement)) {
            return type_right;
        } else if ("up".equals(movement)) {
            return type_up;
        } else if ("down".equals(movement)) {
            return type_down;
        } else if ("in".equals(movement)) {
            return type_in;
        } else if ("out".equals(movement)) {
            return type_out;
        }
        return type_down;
    }
}
