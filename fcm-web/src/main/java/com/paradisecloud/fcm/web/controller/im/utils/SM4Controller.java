package com.paradisecloud.fcm.web.controller.im.utils;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiConferenceOption;
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
    public RestResponse testEncrypt(@RequestBody BusiConferenceOption option) {
        // 处理业务逻辑（此时userDTO已解密）
        System.out.println("解密后的内容：" + option.getContent());
        return RestResponse.success(option);
    }

    // 未标记注解：不加密解密（正常接口）
    @PostMapping("/test/no-encrypt")
    public RestResponse testNoEncrypt(@RequestBody BusiConferenceOption option) {
        return RestResponse.success(option);
    }
}



