package com.paradisecloud.fcm.mcu.zj.model.request.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CcCameraControlRequest extends CommonRequest {

    // 远摇摄像头的动作
    // start：开始
    public static final String action_start = "start";
    // stop：停止
    public static final String action_stop = "stop";
    // stop：停止
    public static final String action_continue = "continue";

    // 移动的方向
    // in:放大
    public static final String movement_in = "in";
    // out:缩小
    public static final String movement_out = "out";
    // left:向左
    public static final String movement_left = "left";
    // right:向右
    public static final String movement_right = "right";
    // up:向上
    public static final String movement_up = "up";
    // down:向下
    public static final String movement_down = "down";
    /**
     * action : start
     * usr_id : 00002
     * movement : up
     * timeout : 1000
     * speed : 4
     */

    private String action;
    private String usr_id;
    //in out
    //left right up down
    private String movement;
    private int timeout;
    private int speed;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(String usr_id) {
        this.usr_id = usr_id;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
