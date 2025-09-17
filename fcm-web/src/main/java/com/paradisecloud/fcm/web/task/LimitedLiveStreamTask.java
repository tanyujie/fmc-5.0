package com.paradisecloud.fcm.web.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.model.BusiLive;
import com.paradisecloud.fcm.dao.model.BusiLiveSetting;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveSettingCache;

import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Component
public class LimitedLiveStreamTask extends Thread implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private HttpRequester httpRequester = HttpObjectCreator.getInstance().createHttpRequester(false);

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    @Override
    public void run() {
        logger.info("限制观看直播终端数启动成功！");
        ThreadUtils.sleep(60 * 1000);
        String region = ExternalConfigCache.getInstance().getRegion();
        if(!Objects.equals("ops",region)){
            return;
        }
        while (true) {
            try {
                Set<String> ipSet = new TreeSet<>();
                for (BusiLive value : LiveCache.getInstance().values()) {
                    ipSet.add(value.getIp());
                }
                for (BusiLiveSetting value : LiveSettingCache.getInstance().values()) {
                    String url = value.getUrl();
                    String ip = StringUtils.substringBetween(url, "://", "/");
                    Boolean isPing = pingIp(ip);
                    if (isPing) {
                        ipSet.add(ip);
                    }
                }
                LiveBridgeCache.getInstance().clearLiveUrlTerminalCount();
                if (ipSet != null && ipSet.size() > 0) {
                    for (String ip : ipSet) {

                    }
                }
                Integer liveTerminalCount=0;
                Collection<BaseConferenceContext> conferenceContexts = AllConferenceContextCache.getInstance().values();
                if (conferenceContexts != null && conferenceContexts.size() > 0) {
                    for (BaseConferenceContext conferenceContext : conferenceContexts) {
                         liveTerminalCount += conferenceContext.getLiveTerminalCount();

                    }
                }
                Integer liveLimit = LicenseCache.getInstance().getLiveLimit();
                if(liveLimit!=null){
                    if(liveTerminalCount>liveLimit){
                        //踢人
                        kickOutClient(liveLimit+1);
                    }
                }

            } catch (Throwable e) {
                logger.error("限制观看直播终端数出错", e);
            } finally {
                ThreadUtils.sleep(30 * 1000);
            }
        }
    }

    /**
     * ping服务器Ip
     *
     * @param ip
     * @return
     */
    public Boolean pingIp(String ip) {
        if (null == ip || 0 == ip.length()) {
            return false;
        }

        try {
            boolean reachable = InetAddress.getByName(ip).isReachable(500);//超过3秒
            if (reachable) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private void kickOutClient(Integer start) {

        try {
            BusiOpsInfoMapper busiOpsInfoMapper = BeanFactory.getBean(BusiOpsInfoMapper.class);
            List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
            if(CollectionUtils.isEmpty(busiOpsInfos)){
                return;
            }
            String ip = busiOpsInfos.get(0).getIpAddress();
            String httpUrl = "http://" + ip + ":1985/api/v1/clients/?start="+start;
            GenericValue<JSONObject> genericValue = new GenericValue<>();
            httpRequester.get(httpUrl, new HttpResponseProcessorAdapter() {
                @Override
                public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                    try {
                        String s = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = JSON.parseObject(s);
                        genericValue.setValue(jsonObject);
                    } catch (IOException var4) {
                    }
                }
            });
            JSONObject jsonObject = genericValue.getValue();
            if (jsonObject != null) {

                JSONArray jsonArray = JSONArray.parseArray(jsonObject.toJSONString());
                if (jsonArray != null && jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                        JSONObject id = jsonObj.getJSONObject("id");


                        String httpUrl_kick = "http://" + ip + ":1985/api/v1/clients/"+id;
                        GenericValue<JSONObject> genericValue_kick = new GenericValue<>();
                        httpRequester.delete(httpUrl_kick, new HttpResponseProcessorAdapter() {
                            @Override
                            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                              logger.info("剔除成功");
                            }
                        });

                    }
                }


            }
        } catch (Exception e) {
            logger.error("踢人出错"+e.getMessage());
        }
    }

}
