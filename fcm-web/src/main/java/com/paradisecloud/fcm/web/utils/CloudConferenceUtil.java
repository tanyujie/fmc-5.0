package com.paradisecloud.fcm.web.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.service.model.CloudConference;
import com.paradisecloud.system.service.ISysConfigService;
import com.sinhy.spring.BeanFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author nj
 * @date 2024/6/26 14:47
 */
public class CloudConferenceUtil {

    public static final int HTTPCODE = 200;
    private static final Logger log = LoggerFactory.getLogger(CloudConferenceUtil.class);

    public static Map<String, String> getConferenceNumber(String conferenceName, String mcuType) {

        log.info("TencentCloudTask获取SN任务。" + conferenceName);

        String cloudUrl = ExternalConfigCache.getInstance().getCloudUrl();
        if (Strings.isBlank(cloudUrl)) {
            cloudUrl = "https://218.28.249.134:8899/fcm";
        }
        log.info("cloudUrl=====================================:" + cloudUrl);
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


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept-Charset", "UTF-8");

//            String tokenCloud = OpsDataCache.getInstance().getCloudToken();
//            if (StringUtils.isEmpty(tokenCloud)) {
//                tokenCloud = CloudTokenUtil.getToken();
//            }
            String tokenCloud = CloudTokenUtil.getToken();

            headers.setBearerAuth(tokenCloud);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "TX" + conferenceName);
            jsonObject.put("mcuType", mcuType);
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);

            ResponseEntity<String> sEntity = restTemplate.postForEntity(cloudUrl + "/busi/mcu/all/conference/startCloudConference/" + "TX" + conferenceName + "/" + mcuType, formEntity, String.class);
            if (sEntity != null) {
                int statusCodeValue_cer = sEntity.getStatusCodeValue();
                log.info("templateConference reslutstatusCodeValue:{}", statusCodeValue_cer);
                if (statusCodeValue_cer == HTTPCODE) {
                    String body_sn = sEntity.getBody();
                    JSONObject parseObject_sn = JSONObject.parseObject(body_sn);
                    Object data_sn = parseObject_sn.get("data");
                    JSONObject sn_JSON = JSONObject.parseObject(data_sn.toString());

                    String conferenceNumber = sn_JSON.getString("conferenceNumber");
                    String conferenceTencentId = sn_JSON.getString("conferenceId");

                    HashMap<String, String> map = new HashMap<>();
                    map.put("conferenceId", conferenceTencentId);
                    map.put("conferenceNumber", conferenceNumber);
                    map.put("conferenceName", "TX" + conferenceName);
                    return map;
                }

            }

        } catch (Exception e) {
            log.info("create cloud tencent conference error:{}", e.getMessage());
        }

        return null;
    }

    public static void endConference(CloudConference cloudConference) {
        String conferenceNumber = cloudConference.getConferenceNumber();
        if (Strings.isNotBlank(conferenceNumber)) {
            ISysConfigService sysConfigService = BeanFactory.getBean(ISysConfigService.class);
            String tencent_cloud_conference_number = sysConfigService.selectConfigByKey("tencent.cloud.conference.number");
            if (Strings.isBlank(tencent_cloud_conference_number)) {
                tencent_cloud_conference_number = "98855175122";
            }
            if (Objects.equals(tencent_cloud_conference_number, cloudConference.getConferenceNumber())) {
                return;
            }
        }
        if (Objects.equals("98855175122", cloudConference.getConferenceNumber()) || Objects.equals("77161895438", cloudConference.getConferenceNumber())) {
            return;
        }
        log.info("TencentCloudTask endConference。" + cloudConference.getConferenceNumber());

        String cloudUrl = ExternalConfigCache.getInstance().getCloudUrl();
        if (Strings.isBlank(cloudUrl)) {
            cloudUrl = "https://218.28.249.134:8899/fcm";
        }
        log.info("cloudUrl=====================================:" + cloudUrl);
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


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            headers.add("Accept-Charset", "UTF-8");

//            String tokenCloud = OpsDataCache.getInstance().getCloudToken();
//            if (StringUtils.isEmpty(tokenCloud)) {
//                tokenCloud = CloudTokenUtil.getToken();
//            }
           String  tokenCloud = CloudTokenUtil.getToken();
            headers.setBearerAuth(tokenCloud);
            JSONObject jsonObject = new JSONObject();
            log.info("开始结束腾讯会议:");
            HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(jsonObject), headers);

            restTemplate.postForEntity(cloudUrl + "/busi/mcu/all/conference/endCloudConference/" + cloudConference.getConferenceNumber() + "/" + cloudConference.getCascadeMcuType(), formEntity, String.class);

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
