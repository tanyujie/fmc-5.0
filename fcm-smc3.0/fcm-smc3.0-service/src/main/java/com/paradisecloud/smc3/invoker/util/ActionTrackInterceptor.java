package com.paradisecloud.smc3.invoker.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @author nj
 * @date 2022/8/15 15:33
 */
public class ActionTrackInterceptor implements ClientHttpRequestInterceptor {

    public ActionTrackInterceptor(String token) {
        this.token = token;
    }

    private String token;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        HttpHeaders headers = request.getHeaders();

        // 加入自定义字段
        headers.add("token",token );

        // 保证请求继续被执行
        return execution.execute(request, body);
    }
}
