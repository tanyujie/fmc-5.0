package com.paradisecloud.fcm.service.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.service.model.CloudConference;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.paradisecloud.system.service.ISysConfigService;
import com.sinhy.spring.BeanFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author nj
 * @date 2024/6/26 14:47
 */
public class TencentCloudUtil {

    public static final int HTTPCODE = 200;
    private static final Logger log = LoggerFactory.getLogger(TencentCloudUtil.class);

//    public static Map<String,String> getConferenceNumber(String conferenceName) {
//
//        log.info("TencentCloudTask获取SN任务。"+conferenceName);
//
//        String cloudUrl = ExternalConfigCache.getInstance().getCloudUrl();
//        if(Strings.isBlank(cloudUrl)){
//            cloudUrl="https://218.28.249.134:8899/fcm";
//        }
//        log.info("cloudUrl=====================================:"+cloudUrl);
//        try {
//
//            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//            factory.setConnectTimeout(5000);
//            factory.setReadTimeout(5000);
//
//            RestTemplate restTemplate = new RestTemplate(factory);
//
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }};
//
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//            HttpsURLConnection.setDefaultHostnameVerifier(new TencentCloudUtil.NoopHostnameVerifier());
//
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("username", "adminops");
//            jsonObject.put("password", "Admin@2024");
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
//            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//            headers.add("Accept-Charset", "UTF-8");
//            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
//            ResponseEntity<String> entity = restTemplate.postForEntity(cloudUrl + "/user/loginNoCode", formEntity, String.class);
//            if (entity != null) {
//                int statusCodeValue = entity.getStatusCodeValue();
//                log.info("getToken statusCodeValue:{}", statusCodeValue);
//                if (statusCodeValue == HTTPCODE) {
//
//                    String body_1 = entity.getBody();
//                    JSONObject parseObject_1 = JSONObject.parseObject(body_1);
//                    Object data_1 = parseObject_1.get("data");
//
//                    JSONObject dataJason = JSONObject.parseObject(data_1.toString());
//                    String token = dataJason.getString("token");
//
//                    headers.setBearerAuth(token);
//
//
//                    log.info("开始創建腾讯会议:");
//                    jsonObject.put("cascadeTemplateConferences", new ArrayList<>());
//                    HashMap<String, Object> paramMap = new HashMap<>();
//                    jsonObject.put("templateConference", paramMap);
//                    paramMap.put("businessFieldType", 100);
//                    paramMap.put("chairmanPassword", "");
//                    paramMap.put("conferenceTimeType", "INSTANT_CONFERENCE");
//                    paramMap.put("deptId", 1);
//                    paramMap.put("mcuType", "mcu-tencent");
//                    paramMap.put("muteType", 1);
//                    paramMap.put("name", "TX" + conferenceName);
//                    paramMap.put("password", "");
//                    paramMap.put("supportLive", 2);
//                    paramMap.put("supportRecord", 2);
//                    Integer tencentTime = LicenseCache.getInstance().getTencentTime();
//                    if(tencentTime!=null&&tencentTime>0){
//                        paramMap.put("durationTime",LicenseCache.getInstance().getTencentTime());
//                    }
//
//                    formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
//                    ResponseEntity<String> certificateEntity = restTemplate.postForEntity(cloudUrl + "/busi/mcu/all/templateConference", formEntity, String.class);
//                    if (certificateEntity != null) {
//                        int statusCodeValue_cer = certificateEntity.getStatusCodeValue();
//                        log.info("templateConference reslutstatusCodeValue:{}", statusCodeValue_cer);
//                        if (statusCodeValue_cer == HTTPCODE) {
//                            headers.put(HttpHeaders.ACCEPT, Arrays.asList(MediaType.ALL_VALUE));
//                            headers.remove(HttpHeaders.ACCEPT_CHARSET);
//                            headers.put(HttpHeaders.CONNECTION, Arrays.asList("gzip, deflate, br"));
//                            String body = certificateEntity.getBody();
//                            JSONObject parseObject = JSONObject.parseObject(body);
//                            Object data = parseObject.get("data");
//
//                            JSONObject certiJSON = JSONObject.parseObject(data.toString());
//                            String jsonString = certiJSON.getString("templateConference");
//
//
//                            BusiTemplateConference busiTemplateConference = JSONObject.parseObject(jsonString, BusiTemplateConference.class);
//                            String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), McuType.MCU_TENCENT.getCode());
//
//                            JSONObject jsonObject2 = new JSONObject();
//                            String url = cloudUrl + "/busi/mcu/all/conference/startByTemplate/" + conferenceId;
//
//
//                            HttpEntity<String> stringHttpEntity = new HttpEntity<>(JSON.toJSONString(jsonObject2), headers);
//                            ResponseEntity<String> snEntity = restTemplate.postForEntity(url, stringHttpEntity, String.class);
//
//                            if (snEntity != null) {
//                                int statusCodeValue_sn = snEntity.getStatusCodeValue();
//                                log.info("startByTemplate reslutstatusCodeValue:{}", statusCodeValue_sn);
//
//                                if (statusCodeValue_sn == HTTPCODE) {
//                                    String body_sn = snEntity.getBody();
//                                    JSONObject parseObject_sn = JSONObject.parseObject(body_sn);
//                                    Object data_sn = parseObject_sn.get("data");
//                                    JSONObject sn_JSON = JSONObject.parseObject(data_sn.toString());
//
//                                    String conferenceNumber = sn_JSON.getString("conferenceNumber");
//                                    String conferenceTencentId = sn_JSON.getString("conferenceId");
//
//                                    HashMap<String, String> map = new HashMap<>();
//                                    map.put("conferenceId", conferenceTencentId);
//                                    map.put("conferenceNumber", conferenceNumber);
//                                    map.put("conferenceName", "TX" + conferenceName);
//                                    return map;
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            log.info("create cloud tencent conference error:{}", e.getMessage());
//        }
//
//        return null;
//    }

    public static Map<String,String> getConferenceNumber(String conferenceName, String cloudConferenceId) {
        log.info("TencentCloudTask获取SN任务。"+conferenceName);
        if (StringUtils.isEmpty(OpsDataCache.getInstance().getCloudToken())) {
            throw new CustomException("当前未连接云服务！");
        }
        String cloudUrl = ExternalConfigCache.getInstance().getCloudUrl();
        if (Strings.isBlank(cloudUrl)) {
            cloudUrl = "https://218.28.249.134:8899/fcm";
        }
        log.info("cloudUrl=====================================:" + cloudUrl);
        try {

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);

            RestTemplate restTemplate = new RestTemplate(factory);

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
            HttpsURLConnection.setDefaultHostnameVerifier(new TencentCloudUtil.NoopHostnameVerifier());

            JSONObject jsonObject = new JSONObject();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept-Charset", "UTF-8");

            headers.setBearerAuth(OpsDataCache.getInstance().getCloudToken());
            String sn = LicenseCache.getInstance().getSn();
            if (StringUtils.isEmpty(sn)) {
                sn = MqttConfigConstant.OPS_TEST_SN;
            }

            ResponseEntity<String> certificateEntity = null;
            if (StringUtils.isNotEmpty(cloudConferenceId)) {
                HttpEntity formEntity = new HttpEntity<>(headers);
                certificateEntity = restTemplate.exchange(cloudUrl + "/busi/mcu/all/templateConference/" + cloudConferenceId, HttpMethod.GET, formEntity, String.class);
            } else {
                log.info("开始創建腾讯会议:");
                jsonObject.put("cascadeTemplateConferences", new ArrayList<>());
                HashMap<String, Object> paramMap = new HashMap<>();
                jsonObject.put("templateConference", paramMap);
                paramMap.put("businessFieldType", 100);
                paramMap.put("chairmanPassword", "");
                paramMap.put("conferenceTimeType", "INSTANT_CONFERENCE");
                paramMap.put("deptId", 1);
                paramMap.put("mcuType", McuType.MCU_TENCENT.getCode());
                paramMap.put("muteType", 1);
                paramMap.put("name", "TX" + conferenceName);
                paramMap.put("password", "");
                paramMap.put("supportLive", 2);
                paramMap.put("supportRecord", 2);
                HashMap<String, Object> params = new HashMap<>();
                params.put("opsSn", sn);
                paramMap.put("params", params);
                Integer tencentTime = LicenseCache.getInstance().getTencentTime();
                if (tencentTime != null && tencentTime > 0) {
                    paramMap.put("durationTime", LicenseCache.getInstance().getTencentTime());
                }

                HttpEntity formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
                certificateEntity = restTemplate.postForEntity(cloudUrl + "/busi/mcu/all/templateConference", formEntity, String.class);
            }
            if (certificateEntity != null) {
                int statusCodeValue_cer = certificateEntity.getStatusCodeValue();
                log.info("templateConference reslutstatusCodeValue:{}", certificateEntity);
                if (statusCodeValue_cer == HTTPCODE) {
                    headers.put(HttpHeaders.ACCEPT, Arrays.asList(MediaType.ALL_VALUE));
                    headers.remove(HttpHeaders.ACCEPT_CHARSET);
                    headers.put(HttpHeaders.CONNECTION, Arrays.asList("gzip, deflate, br"));
                    String body = certificateEntity.getBody();
                    JSONObject parseObject = JSONObject.parseObject(body);
                    Object data = parseObject.get("data");

                    JSONObject certiJSON = JSONObject.parseObject(data.toString());
                    String jsonString = certiJSON.getString("templateConference");


                    BusiTemplateConference busiTemplateConference = JSONObject.parseObject(jsonString, BusiTemplateConference.class);
                    if (StringUtils.isNotEmpty(cloudConferenceId)) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("success", "true");
                        map.put("conferenceId", cloudConferenceId);
                        map.put("conferenceNumber", busiTemplateConference.getConferenceNumber().toString());
                        map.put("conferenceName", "TX" + conferenceName);
                        return map;
                    }
                    String conferenceId = EncryptIdUtil.generateConferenceId(busiTemplateConference.getId(), McuType.MCU_TENCENT.getCode());

                    JSONObject jsonObject2 = new JSONObject();
                    String url = cloudUrl + "/busi/mcu/all/conference/startByTemplate/" + conferenceId;


                    HttpEntity<String> stringHttpEntity = new HttpEntity<>(JSON.toJSONString(jsonObject2), headers);
                    ResponseEntity<String> snEntity = restTemplate.postForEntity(url, stringHttpEntity, String.class);

                    if (snEntity != null) {
                        int statusCodeValue_sn = snEntity.getStatusCodeValue();
                        log.info("startByTemplate reslutstatusCodeValue:{}", snEntity);

                        if (statusCodeValue_sn == HTTPCODE) {
                            String body_sn = snEntity.getBody();
                            JSONObject parseObject_sn = JSONObject.parseObject(body_sn);
                            Boolean success = parseObject_sn.getBoolean("success");
                            if (!success) {
                                String message = parseObject_sn.getString("message");
                                HashMap<String, String> map = new HashMap<>();
                                map.put("success", "false");
                                map.put("message", message);
                                return map;
                            }
                            Object data_sn = parseObject_sn.get("data");
                            JSONObject sn_JSON = JSONObject.parseObject(data_sn.toString());

                            String conferenceNumber = sn_JSON.getString("conferenceNumber");
                            String conferenceTencentId = sn_JSON.getString("conferenceId");

                            HashMap<String, String> map = new HashMap<>();
                            map.put("success", "true");
                            map.put("conferenceId", conferenceTencentId);
                            map.put("conferenceNumber", conferenceNumber);
                            map.put("conferenceName", "TX" + conferenceName);
                            return map;
                        }

                    }
                }
            }
        } catch (Exception e) {
            log.info("create cloud tencent conference error:{}", e.getMessage());
        }

        return null;
    }

//    public static void endConference(CloudConference cloudConference) {
//        String conferenceNumber = cloudConference.getConferenceNumber();
//        if(Strings.isNotBlank(conferenceNumber)){
//            ISysConfigService sysConfigService = BeanFactory.getBean(ISysConfigService.class);
//            String tencent_cloud_conference_number = sysConfigService.selectConfigByKey("tencent.cloud.conference.number");
//            if(Strings.isBlank(tencent_cloud_conference_number)){
//                tencent_cloud_conference_number="98855175122";
//            }
//            if(Objects.equals(tencent_cloud_conference_number,cloudConference.getConferenceNumber())){
//                return;
//            }
//        }
//        if(Objects.equals("98855175122",cloudConference.getConferenceNumber())||Objects.equals("77161895438",cloudConference.getConferenceNumber())){
//            return;
//        }
//        log.info("TencentCloudTask endConference。"+cloudConference.getConferenceNumber());
//
//        String cloudUrl = ExternalConfigCache.getInstance().getCloudUrl();
//        if(Strings.isBlank(cloudUrl)){
//            cloudUrl="https://218.28.249.134:8899/fcm";
//        }
//        log.info("cloudUrl=====================================:"+cloudUrl);
//        try {
//
//            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//            factory.setConnectTimeout(5000);
//            factory.setReadTimeout(5000);
//
//            RestTemplate restTemplate = new RestTemplate(factory);
//
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }};
//
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//            HttpsURLConnection.setDefaultHostnameVerifier(new TencentCloudUtil.NoopHostnameVerifier());
//
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("username", "adminops");
//            jsonObject.put("password", "Admin@2024");
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
//            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//            headers.add("Accept-Charset", "UTF-8");
//            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
//            ResponseEntity<String> entity = restTemplate.postForEntity(cloudUrl + "/user/loginNoCode", formEntity, String.class);
//            if (entity != null) {
//                int statusCodeValue = entity.getStatusCodeValue();
//                log.info("getToken statusCodeValue:{}", statusCodeValue);
//                if (statusCodeValue == HTTPCODE) {
//
//                    String body_1 = entity.getBody();
//                    JSONObject parseObject_1 = JSONObject.parseObject(body_1);
//                    Object data_1 = parseObject_1.get("data");
//
//                    JSONObject dataJason = JSONObject.parseObject(data_1.toString());
//                    String token = dataJason.getString("token");
//                    headers.setBearerAuth(token);
//                    log.info("开始结束腾讯会议:");
//                    formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
//                    ResponseEntity<String> certificateEntity = restTemplate.postForEntity(cloudUrl + "/busi/mcu/all/conference/endCloudConference/"+cloudConference.getCascadeConferenceId()+"/"+cloudConference.getCascadeMcuType(), formEntity, String.class);
//
//                }
//            }
//
//        } catch (Exception e) {
//        }
//
//    }

    public static void endConference(CloudConference cloudConference) {
        log.info("TencentCloudTask结束云会议。");
        if (StringUtils.isEmpty(OpsDataCache.getInstance().getCloudToken())) {
            throw new CustomException("当前未连接云服务！");
        }
        String conferenceNumber = cloudConference.getConferenceNumber();
        if(Strings.isNotBlank(conferenceNumber)){
            ISysConfigService sysConfigService = BeanFactory.getBean(ISysConfigService.class);
            String tencent_cloud_conference_number = sysConfigService.selectConfigByKey("tencent.cloud.conference.number");
            if(Strings.isBlank(tencent_cloud_conference_number)){
                tencent_cloud_conference_number="98855175122";
            }
            if(Objects.equals(tencent_cloud_conference_number,cloudConference.getConferenceNumber())){
                return;
            }
        }
        if(Objects.equals("98855175122",cloudConference.getConferenceNumber())||Objects.equals("77161895438",cloudConference.getConferenceNumber())){
            return;
        }
        log.info("TencentCloudTask endConference。"+cloudConference.getConferenceNumber());

        String cloudUrl = ExternalConfigCache.getInstance().getCloudUrl();
        if(Strings.isBlank(cloudUrl)){
            cloudUrl="https://218.28.249.134:8899/fcm";
        }
        log.info("cloudUrl=====================================:"+cloudUrl);
        try {

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);

            RestTemplate restTemplate = new RestTemplate(factory);

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
            HttpsURLConnection.setDefaultHostnameVerifier(new TencentCloudUtil.NoopHostnameVerifier());

            JSONObject jsonObject = new JSONObject();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept-Charset", "UTF-8");
            headers.setBearerAuth(OpsDataCache.getInstance().getCloudToken());
            log.info("开始结束腾讯会议:");
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);
            ResponseEntity<String> certificateEntity = restTemplate.postForEntity(cloudUrl + "/busi/mcu/all/conference/endCloudConference/"+cloudConference.getCascadeConferenceId()+"/"+cloudConference.getCascadeMcuType(), formEntity, String.class);
        } catch (Exception e) {
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
