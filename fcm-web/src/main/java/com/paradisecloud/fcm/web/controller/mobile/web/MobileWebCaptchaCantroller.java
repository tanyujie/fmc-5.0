package com.paradisecloud.fcm.web.controller.mobile.web;

import com.google.code.kaptcha.Producer;
import com.paradisecloud.common.constant.Constants;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.sign.Base64;
import com.paradisecloud.common.utils.uuid.IdUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/mobileWeb"})
public class MobileWebCaptchaCantroller {
    @Resource(
            name = "captchaProducer"
    )
    private Producer captchaProducer;
    @Resource(
            name = "captchaProducerMath"
    )
    private Producer captchaProducerMath;
    @Autowired
    private RedisCache redisCache;
    @Value("${application.captchaType}")
    private String captchaType;

    public MobileWebCaptchaCantroller() {
    }

    @GetMapping({"/captchaImage"})
    public RestResponse getCode(HttpServletResponse response) throws IOException {
        String uuid = IdUtils.simpleUUID();
        String verifyKey = "captcha_codes:" + uuid;
        String capStr = null;
        String code = null;
        BufferedImage image = null;
        if ("math".equals(this.captchaType)) {
            String capText = this.captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = this.captchaProducerMath.createImage(capStr);
        } else if ("char".equals(this.captchaType)) {
            capStr = code = this.captchaProducer.createText();
            image = this.captchaProducer.createImage(capStr);
        }

        this.redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();

        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException var9) {
            return RestResponse.fail(var9.getMessage());
        }

        Map<String, Object> data = new HashMap();
        data.put("uuid", uuid);
        data.put("img", Base64.encode(os.toByteArray()));
        return RestResponse.success(data);
    }
}
