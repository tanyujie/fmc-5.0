/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : Stream2.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy
 * @since 2021-09-18 11:24
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.StreamingEnabledType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiLiveClusterMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveSettingMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiLive;
import com.paradisecloud.fcm.dao.model.BusiLiveClusterMap;
import com.paradisecloud.fcm.dao.model.BusiLiveDept;
import com.paradisecloud.fcm.dao.model.BusiLiveSetting;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.CallStreamerProcessor;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.HangUpAttendeeProcessor;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Stream2 extends Stream {

    /**
     * <pre>构造方法</pre>
     *
     * @param method
     * @author sinhy
     * @since 2021-09-18 11:24
     */
    public Stream2(Method method) {
        super(method);
    }

    public void stream(String conferenceId, Boolean streaming, String streamUrl) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (mainConferenceContext.getStreamingEnabled() != null && mainConferenceContext.getStreamingEnabled() == StreamingEnabledType.CLOUDS.getValue()) {
            stream(mainConferenceContext, streaming, mainConferenceContext.getCloudsStreamingUrl());
            String pushUrlFull = null;
            for (String liveUrl : mainConferenceContext.getStreamUrlList()) {
                if (liveUrl.startsWith("rtmp://")) {
                    pushUrlFull = liveUrl;
                }
            }
            if (!streaming || StringUtils.isNotEmpty(pushUrlFull)) {
                BeanFactory.getBean(IMqttService.class).isInviteLiveTerminal(contextKey, streaming, pushUrlFull, mainConferenceContext.getTemplateConferenceId());
            }
            return;
        } else if (mainConferenceContext.getStreamingEnabled() != null && mainConferenceContext.getStreamingEnabled() == StreamingEnabledType.THIRD_PARTY.getValue()) {
            stream(mainConferenceContext, streaming, streamUrl);
            BeanFactory.getBean(IMqttService.class).isInviteLiveTerminal(contextKey, streaming, streamUrl, mainConferenceContext.getTemplateConferenceId());
            return;
        } else {
            if (mainConferenceContext.getIsAutoCreateStreamUrl() == 1) {
                if (streaming) {
                    String streamingUrl = createStreamingUrl(mainConferenceContext.getDeptId(), Long.valueOf(mainConferenceContext.getConferenceNumber()), streamUrl);
                    logger.info("直播地址：" + streamingUrl);
                    if (streamingUrl != null && streamingUrl.length() > 0) {
                        mainConferenceContext.setStreamingUrl(streamingUrl);
                        List<String> streamUrlList = new ArrayList<>();
                        streamUrlList.add(streamingUrl);
                        mainConferenceContext.getStreamUrlList().clear();
                        mainConferenceContext.addStreamUrlList(streamUrlList);
                        if (!streamingUrl.equals(streamUrl)) {
                            streamUrl = streamingUrl;
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("streamingUrl", mainConferenceContext.getStreamingUrl());
                            jsonObject.put("streamUrlList", mainConferenceContext.getStreamUrlList());
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_SHOW_TIP, "直播地址无效，使用新的直播地址！");
                        }
                    }
                }
            }
        }
        if (!streaming) {
            if (mainConferenceContext.getIsAutoCreateStreamUrl() == 2) {
                LiveBridgeCache.getInstance().setLiveUrlTerminalCount(mainConferenceContext.getStreamingUrl(), 0);
                LiveBridgeCache.getInstance().setLiveConferenceTerminalCount(mainConferenceContext.getId(), 0);
            } else {
                Long deptId = mainConferenceContext.getDeptId();
                BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(deptId);
                if (busiLiveDept != null) {
                    if (busiLiveDept.getLiveType() == 1) {
                        LiveBridgeCache.getInstance().setLiveUrlTerminalCount(mainConferenceContext.getStreamingUrl(), 0);
                        LiveBridgeCache.getInstance().setLiveConferenceTerminalCount(mainConferenceContext.getId(), 0);
                    } else {
                        List<String> streamUrlList = mainConferenceContext.getStreamUrlList();
                        if (streamUrlList != null && streamUrlList.size() > 0) {
                            for (String url : streamUrlList) {
                                LiveBridgeCache.getInstance().setLiveUrlTerminalCount(url, 0);
                            }
                        }
                        LiveBridgeCache.getInstance().setLiveConferenceTerminalCount(mainConferenceContext.getId(), 0);
                    }
                }
            }
        }
        if (streaming) {
            Assert.isTrue(StringUtil.isNotEmpty(streamUrl), "直播地址为空，不能开启直播！");
        }
        String streamingRemoteParty = mainConferenceContext.getStreamingRemoteParty();
        if (StringUtils.isEmpty(streamingRemoteParty)) {
            if (StringUtils.isNotEmpty(streamUrl)) {
                BusiLiveSettingMapper busiLiveSettingMapper = BeanFactory.getBean(BusiLiveSettingMapper.class);
                BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
                busiLiveSettingCon.setUrl(streamUrl);
                List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
                if (busiLiveSettingList.size() > 0) {
                    BusiLiveSetting busiLiveSetting = busiLiveSettingList.get(0);
                    if (StringUtils.isNotEmpty(busiLiveSetting.getRemoteParty())) {
                        streamingRemoteParty = busiLiveSetting.getRemoteParty();
                        mainConferenceContext.setStreamingRemoteParty(streamingRemoteParty);
                    }
                    mainConferenceContext.setStreamingName(busiLiveSetting.getName());
                }
            }
        }
        if (StringUtils.isNotEmpty(streamingRemoteParty)) {
            if (streaming) {
                new CallStreamerProcessor(contextKey, streamingRemoteParty).process();
            } else {
                if (mainConferenceContext.getStreamingAttendee() != null) {
                    new HangUpAttendeeProcessor(contextKey, mainConferenceContext.getStreamingAttendee().getId()).process();
                }
                FcmThreadPool.exec(() -> {
                    ThreadUtils.sleep(500);
                    mainConferenceContext.setStreaming(false);

                    // 向所有客户端通知会议的录制状态
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.STREAMING, false);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭直播");
                });
            }
        } else {
            stream(mainConferenceContext, streaming, streamUrl);
            if (streaming) {
                mainConferenceContext.setStreamingUrl(streamUrl);
            }
        }
        BeanFactory.getBean(IMqttService.class).isInviteLiveTerminal(contextKey, streaming, streamUrl, mainConferenceContext.getTemplateConferenceId());
    }

    private String createStreamingUrl(Long deptId, Long conferenceNumber, String streamUrlRequest) {
        String streamUrl = null;
        try {
            BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(deptId);
            if (busiLiveDept != null) {
                if (busiLiveDept.getLiveType() == 1) {
                    BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(busiLiveDept.getLiveId());
//                    if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
//                        streamUrl = busiLive.getProtocolType() + "://" + busiLive.getDomainName() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
//                    } else {
                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
//                    }
                } else {
                    BusiLiveClusterMap busiLiveClusterMap = new BusiLiveClusterMap();
                    busiLiveClusterMap.setClusterId(busiLiveDept.getLiveId());
                    List<BusiLiveClusterMap> busiLiveClusterMaps = BeanFactory.getBean(BusiLiveClusterMapMapper.class).selectBusiLiveClusterMapList(busiLiveClusterMap);
                    if (busiLiveClusterMaps != null && busiLiveClusterMaps.size() > 0) {
                        if (StringUtils.isNotEmpty(streamUrlRequest)) {
                            for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
                                if (liveClusterMap.getLiveType() == 1) {
                                    BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(liveClusterMap.getLiveId());
//                                if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
//                                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getDomainName() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
//                                } else {
                                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
                                    try {
                                        Integer status = LiveBridgeCache.getInstance().get(busiLive.getId()).getBusiLive().getStatus();
                                        if (status != null && status == 1 && streamUrlRequest.equals(streamUrl)) {
                                            return streamUrl;
                                        }
                                    } catch (Exception e) {
                                    }
//                                }
                                }
                            }
                        }
                        for (BusiLiveClusterMap liveClusterMap : busiLiveClusterMaps) {
                            if (liveClusterMap.getLiveType() == 1) {
                                BusiLive busiLive = BeanFactory.getBean(BusiLiveMapper.class).selectBusiLiveById(liveClusterMap.getLiveId());
//                                if (busiLive.getDomainName() != null && busiLive.getDomainName().length() > 0) {
//                                    streamUrl = busiLive.getProtocolType() + "://" + busiLive.getDomainName() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
//                                } else {
                                streamUrl = busiLive.getProtocolType() + "://" + busiLive.getIp() + "/" + busiLive.getUriPath() + "/" + conferenceNumber;
                                try {
                                    Integer status = LiveBridgeCache.getInstance().get(busiLive.getId()).getBusiLive().getStatus();
                                    if (status != null && status == 1) {
                                        break;
                                    }
                                } catch (Exception e) {
                                }
//                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return streamUrl;
    }
}
