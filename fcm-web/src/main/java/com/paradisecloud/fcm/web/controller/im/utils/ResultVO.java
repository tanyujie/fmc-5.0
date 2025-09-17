package com.paradisecloud.fcm.web.controller.im.utils;

// 响应结果类示例
public class ResultVO {
    private int code;
    private String msg;
    private Object data;

    public static ResultVO success(String msg, Object data) {
        ResultVO vo = new ResultVO();
        vo.code = 200;
        vo.msg = msg;
        vo.data = data;
        return vo;
    }
    // get/set方法省略
}