package com.paradisecloud.fcm.web.controller.im.utils;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/im/SM4/")
@Tag(name = "会议问卷选项")
public class SM4Controller {

    // 标记@SM4Encrypt：请求体自动解密，响应体自动加密
    @SM4Encrypt
    @PostMapping("/test/encrypt")
    public ResultVO testEncrypt(@RequestBody UserDTO userDTO) {
        // 处理业务逻辑（此时userDTO已解密）
        System.out.println("解密后的用户名：" + userDTO.getUsername());
        return ResultVO.success("处理成功", userDTO);
    }

    // 未标记注解：不加密解密（正常接口）
    @PostMapping("/test/no-encrypt")
    public ResultVO testNoEncrypt(@RequestBody UserDTO userDTO) {
        return ResultVO.success("不加密", userDTO);
    }
}



