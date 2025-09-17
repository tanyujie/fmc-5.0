package com.paradisecloud.fcm.web.controller.im.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 自定义消息转换器：对标记@SM4Encrypt的接口自动处理
 */
public class SM4HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    // SM4密钥（实际项目中从配置文件读取，避免硬编码）
    private final String sm4Key;
    // SM4偏移量（实际项目中从配置文件读取）
    private final String sm4Iv;
    // 用于获取当前请求的HandlerMethod
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    // 构造方法修改为注入RequestMappingHandlerMapping
    public SM4HttpMessageConverter(String sm4Key, String sm4Iv, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.sm4Key = sm4Key;
        this.sm4Iv = sm4Iv;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    /**
     * 读取请求体（解密）
     */
    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        // 判断当前接口是否标记了@SM4Encrypt
        if (isSM4Encrypt()) {
            // 读取加密的请求体
            String encryptedBody = new String(readBytes(inputMessage.getBody()), StandardCharsets.UTF_8);
            try {
                // 解密
                String decryptedBody = SM4Utils.decrypt(encryptedBody, sm4Key, sm4Iv);
                // 解密后的明文转为对象
                return new ObjectMapper().readValue(decryptedBody, getJavaType(type, contextClass));
            } catch (Exception e) {
                throw new HttpMessageNotReadableException("SM4解密失败：" + e.getMessage(), e, inputMessage);
            }
        }
        // 未标记注解，走默认逻辑
        return super.read(type, contextClass, inputMessage);
    }

    /**
     * 写入响应体（加密）
     */
    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        // 判断当前接口是否标记了@SM4Encrypt
        if (isSM4Encrypt()) {
            try {
                // 对象转为JSON明文
                String plainText = new ObjectMapper().writeValueAsString(object);
                // 加密
                String encryptedText = SM4Utils.encrypt(plainText, sm4Key, sm4Iv);
                // 写入加密后的响应
                OutputStream out = outputMessage.getBody();
                out.write(encryptedText.getBytes(StandardCharsets.UTF_8));
                out.flush();
            } catch (Exception e) {
                throw new HttpMessageNotWritableException("SM4加密失败：" + e.getMessage(), e);
            }
        } else {
            // 未标记注解，走默认逻辑
            super.writeInternal(object, type, outputMessage);
        }
    }

    // 判断当前请求的接口是否有@SM4Encrypt注解
    private boolean isSM4Encrypt() {
        try {
            // 获取当前请求
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (!(requestAttributes instanceof ServletRequestAttributes)) {
                return false;
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

            // 通过RequestMappingHandlerMapping获取HandlerMethod
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
            if (handlerExecutionChain == null) {
                return false;
            }

            Object handler = handlerExecutionChain.getHandler();
            if (!(handler instanceof HandlerMethod)) {
                return false;
            }

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return handlerMethod.hasMethodAnnotation(SM4Encrypt.class);
        } catch (Exception e) {
            return false;
        }
    }

    // 读取输入流为字节数组
    private byte[] readBytes(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }
}
