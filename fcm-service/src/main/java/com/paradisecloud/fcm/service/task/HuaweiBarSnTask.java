package com.paradisecloud.fcm.service.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author nj
 * @date 2024/4/2 10:15
 */
public class HuaweiBarSnTask extends Task {
    public static final int HTTPCODE = 200;
    private static final Logger log = LoggerFactory.getLogger(HuaweiBarSnTask.class);
    private  String ip;
    private  String uerName;
    private  String password;
    private String sn;
    private BusiTerminal busiTerminal;
    public HuaweiBarSnTask(String id, long delayInMilliseconds, String ip, String userName, String password, String sn, BusiTerminal busiTerminal) {
        super(id, delayInMilliseconds);
        this.ip = ip;
        this.uerName=userName;
        this.password=password;
        this.sn=sn;
        this.busiTerminal=busiTerminal;
    }

    @Override
    public void run() {
        log.info("获取SN任务。ID:" + getId());
        try {


            RestTemplate restTemplate = new RestTemplate();

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NoopHostnameVerifier());


            JSONObject jsonObject = new JSONObject();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept-Charset", "UTF-8");
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
            ResponseEntity<String> entity = restTemplate.postForEntity("https://" +ip+"/action.cgi?ActionID=WEB_RequestSessionIDAPI", formEntity, String.class);
            if (entity != null) {
                int statusCodeValue = entity.getStatusCodeValue();
                log.info("WEB_RequestSessionIDAPI reslutstatusCodeValue:{}", statusCodeValue);
                if (statusCodeValue == HTTPCODE) {
                    List<String> cookies = entity.getHeaders().get("Set-Cookie");
                    headers.put(HttpHeaders.COOKIE,cookies);

                    log.info("开始认证:" + getId());
                    jsonObject.put("user",uerName);
                    jsonObject.put("password",password);

                    formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
                    ResponseEntity<String> certificateEntity = restTemplate.postForEntity("https://" +ip+"/action.cgi?ActionID=WEB_RequestCertificateAPI", formEntity, String.class);
                    if(certificateEntity!=null){
                        int statusCodeValue_cer = certificateEntity.getStatusCodeValue();
                        log.info("WEB_RequestCertificateAPI reslutstatusCodeValue:{}", statusCodeValue_cer);
                        if (statusCodeValue_cer == HTTPCODE) {
                            cookies = entity.getHeaders().get("Set-Cookie");
                            headers.put(HttpHeaders.COOKIE,cookies);
                            headers.put(HttpHeaders.ACCEPT, Arrays.asList(MediaType.ALL_VALUE));
                            headers.remove(HttpHeaders.ACCEPT_CHARSET);
                            headers.put(HttpHeaders.CONNECTION,Arrays.asList("gzip, deflate, br"));
                            String body = certificateEntity.getBody();
                            JSONObject parseObject = JSONObject.parseObject(body);
                            Object data = parseObject.get("data");

                            JSONObject certiJSON = JSONObject.parseObject(data.toString());
                            String acCSRFToken = certiJSON.getString("acCSRFToken");

                            JSONObject jsonObject2 = new JSONObject();
                            jsonObject2.put("acCSRFToken",acCSRFToken);
                            String url="https://" +ip+"/action.cgi?ActionID=WEB_GetVersionInfoAPI";




                            HttpEntity<String> stringHttpEntity = new HttpEntity<>(JSON.toJSONString(jsonObject2), headers);
                            ResponseEntity<String> snEntity = restTemplate.postForEntity(url, stringHttpEntity, String.class);

                            if(snEntity!=null){
                                int statusCodeValue_sn = snEntity.getStatusCodeValue();
                                log.info("WEB_GetVersionInfoAPI reslutstatusCodeValue:{}", statusCodeValue_sn);

                                if (statusCodeValue_sn == HTTPCODE) {
                                    String body_sn = snEntity.getBody();
                                    JSONObject parseObject_sn = JSONObject.parseObject(body_sn);
                                    Object data_sn = parseObject_sn.get("data");
                                    JSONObject sn_JSON =   JSONObject.parseObject(data_sn.toString());
                                    String license = sn_JSON.getString("lisence");


                                    BusiTerminalMapper busiTerminalMapper = BeanFactory.getBean(BusiTerminalMapper.class);


                                    Map<String, Object> businessProperties = busiTerminal.getBusinessProperties();
                                    if(businessProperties==null){
                                        businessProperties = new HashMap<>();
                                    }
                                    JSONObject licenseJson = new JSONObject();
                                    licenseJson.put("sn",sn);
                                    licenseJson.put("hw_sn",license);
                                    licenseJson.put("username",uerName);
                                    licenseJson.put("password",password);
                                    licenseJson.put("terminalId",busiTerminal.getId());

                                    businessProperties.put("license", licenseJson);

                                    if(Objects.equals(license.toLowerCase(),sn)){
                                        businessProperties.put("license_status", "true");
                                        busiTerminal.setSnCheck(1);
                                    }else {
                                        businessProperties.put("license_status", "false");
                                        businessProperties.put("license_error_reason", "终端序列号错误");
                                        busiTerminal.setSnCheck(0);
                                        busiTerminal.setRemarks("终端序列号错误");
                                    }
                                    busiTerminal.setBusinessProperties(businessProperties);
                                    busiTerminalMapper.updateBusiTerminal(busiTerminal);
                                    TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
                                }

                            }
                        }
                    }
                } else {
                    log.info("SN无法验证:" + getId());
                    BusiTerminalMapper busiTerminalMapper = BeanFactory.getBean(BusiTerminalMapper.class);


                    busiTerminal.setSnCheck(0);

                    Map<String, Object> businessProperties = busiTerminal.getBusinessProperties();
                    if(businessProperties==null){
                        businessProperties = new HashMap<>();
                    }
                    businessProperties.put("license_error_reason", "账户密码错误");
                    busiTerminal.setBusinessProperties(businessProperties);
                    busiTerminal.setRemarks("账户密码错误");
                    busiTerminalMapper.updateBusiTerminal(busiTerminal);
                    TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
                }
            } else {

                log.info("SN无法获取cookie验证:" + getId());
                BusiTerminalMapper busiTerminalMapper = BeanFactory.getBean(BusiTerminalMapper.class);

                Map<String, Object> businessProperties = busiTerminal.getBusinessProperties();
                if(businessProperties==null){
                    businessProperties = new HashMap<>();
                }
                businessProperties.put("license_error_reason", "IP地址错误");
                busiTerminal.setBusinessProperties(businessProperties);
                busiTerminal.setSnCheck(0);
                busiTerminal.setRemarks("IP地址错误");
                busiTerminalMapper.updateBusiTerminal(busiTerminal);
                TerminalCache.getInstance().put(busiTerminal.getId(), busiTerminal);
            }
            log.info("SN处理结束。ID:" + getId());

        } catch (Exception e) {
            log.info("SN处理错误。ID:" + getId()+e.getMessage());
        }

    }

    private HttpComponentsClientHttpRequestFactory createRequestFactory() {
        HttpClient httpClient = HttpClientBuilder.create()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private static class NoopHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    public static String sendSSLPost(String url, String param,String cookie) {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + "?" + param;

        try {
            log.info("sendSSLPost - {}", urlNameString);
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init((KeyManager[])null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
            URL console = new URL(urlNameString);
            HttpsURLConnection conn = (HttpsURLConnection)console.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Cookie", cookie);
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ret = "";

            while((ret = br.readLine()) != null) {
                if (ret != null && !"".equals(ret.trim())) {
                    result.append(new String(ret.getBytes("ISO-8859-1"), "utf-8"));
                }
            }

            log.info("recv - {}", result);
            conn.disconnect();
            br.close();
        } catch (ConnectException var10) {
            log.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, var10);
        } catch (SocketTimeoutException var11) {
            log.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, var11);
        } catch (IOException var12) {
            log.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, var12);
        } catch (Exception var13) {
            log.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, var13);
        }

        return result.toString();
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        private TrustAnyTrustManager() {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        private TrustAnyHostnameVerifier() {
        }

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
